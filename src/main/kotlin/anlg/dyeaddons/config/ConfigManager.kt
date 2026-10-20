package anlg.dyeaddons.config

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.DyeAddons.Companion.MOD_ID
import anlg.dyeaddons.DyeAddons.Companion.logger
import anlg.dyeaddons.data.CalcValue
import anlg.dyeaddons.data.CalcValueAdapter
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.ClientTickEvent
import anlg.dyeaddons.events.models.GameClosedEvent
import anlg.dyeaddons.settings.categories.DebugCategories
import anlg.dyeaddons.utils.SkyblockUtils
import anlg.dyeaddons.utils.StringUtils.compareToVersion
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import net.fabricmc.loader.api.FabricLoader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object ConfigManager {
    val gson : Gson = GsonBuilder()
        .registerTypeAdapter(CalcValue::class.java, CalcValueAdapter())
        .setPrettyPrinting()
        .excludeFieldsWithModifiers()
        .serializeSpecialFloatingPointValues()
        .create()

    private val configDir = File(FabricLoader.getInstance().configDir.toFile(), MOD_ID)
    private const val DATA_FILE_NAME = "data.json"
    private const val CONFIG_FILE_NAME = "config.jsonc"
    private const val BACKUP_DIR_NAME = "backup"
    private const val MAX_BACKUPS = 20

    private val backupFileNames = listOf(
        DATA_FILE_NAME,
        CONFIG_FILE_NAME
    )

    val backupDir = File(configDir, BACKUP_DIR_NAME)

    var data: UserConfig = UserConfig()
    private val configFile = File(configDir, DATA_FILE_NAME)

    private val executor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor { r ->
        Thread(r, "dyeaddons-config-executor").apply {
            isDaemon = true
        }
    }

    private var tickCounter = 0
    private var saveInterval = 20 * 60 * 5 // Save every 5 minutes

    fun init() {
        configDir.mkdirs()
        load()
        EventBus.subscribe(GameClosedEvent::class, ::onGameClosed)
        EventBus.subscribe(ClientTickEvent::class, ::onTick)
    }

    private fun onGameClosed(@Suppress("UNUSED_PARAMETER") event: GameClosedEvent) {
        saveAndBackupData()
    }

    private fun onTick(@Suppress("UNUSED_PARAMETER") event: ClientTickEvent) {
        tickCounter++

        if (tickCounter % saveInterval != 0) return

        save()
    }

    fun load() {
        if (!configFile.exists() || !configFile.canRead()) {
            logger.info("Config file does not exist or cannot be read.")
            save()
            SkyblockUtils.isFirstJoin = true
            return
        }

        try {
            val content = configFile.readText()
            if (content.isBlank()) {
                data = UserConfig()
                save()
                logger.info("Config file is blank")
                return
            }
            val json = JsonParser.parseString(content).asJsonObject

            val defaults = UserConfig()

            try {
                if (json.has("config")) {
                    defaults.config = gson.fromJson(json["config"], UserConfigData::class.java)
                }
            } catch (e: Exception) {
                logger.error("Failed to load config section", e)
                backupFiles()
            }

            try {
                if (json.has("players")) {
                    val type = object : TypeToken<MutableMap<UUID, PlayerData>>() {}.type
                    defaults.players = gson.fromJson(json["players"], type) ?: mutableMapOf()
                }
            } catch (e: Exception) {
                logger.error("Failed to load players section", e)
                backupFiles()
            }

            data = defaults
            if (data.config.lastVersion.isBlank() || data.config.lastVersion.compareToVersion("0.5.0") < 0)
                migrateMFRework()
            data.config.lastVersion = DyeAddons.version.replace(Regex("""\+\d+\.\d+"""), "")
        } catch (e: Exception) {
            logger.error("Error occurred while loading config", e)
            data = UserConfig()
            backupFiles()
        }
    }

    private fun saveAndBackupData() {
        save()
        backupFiles()
    }

    fun save() {
        executor.execute {
            try {
                configDir.mkdirs()

                val json = gson.toJson(data)
                Files.write(
                    configFile.toPath(),
                    json.toByteArray(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE
                )
                DyeAddons.debug("Saved config to $configFile", DebugCategories.FILESYSTEM)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun backupFiles() {
        val filesToBackup = backupFileNames.map { File(configDir, it) }
        val missing = filesToBackup.filter { !it.exists() }
        if (missing.isNotEmpty())
            logger.warn("Not all config files exist to backup. Missing: ${missing.map { it.name }.joinToString(",")}")

        val backupDir = File(configDir, BACKUP_DIR_NAME)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd-HHmmss")
        val zipName = "dyeaddons-backup-${dateFormat.format(Date())}.zip"
        val zipFile = File(backupDir, zipName)

        executor.execute {
            try {
                backupDir.mkdirs()
                FileOutputStream(zipFile).use { fileOut ->
                    ZipOutputStream(fileOut).use { zipOut ->
                        for (file in filesToBackup) {
                            try {
                                ZipEntry(file.name).let { zipOut.putNextEntry(it) }
                                FileInputStream(file).use { it.copyTo(zipOut) }
                                zipOut.closeEntry()
                            } catch (e: Exception) {
                                logger.error("Failed to backup config file ${file.name}", e)
                            }
                        }
                    }
                }
                logger.info("Config backup succeeded: ${zipFile.absolutePath}")
                pruneOldBackups(backupDir)
            } catch (e: Exception) {
                logger.error("Failed to backup files", e)
            }
        }
    }

    private fun pruneOldBackups(backupDir: File) {
        val pattern = Regex("^dyeaddons-backup-\\d{4}-\\d{2}-\\d{2}-\\d{6}\\.zip$")
        val backups = backupDir.listFiles()?.filter { it.isFile && pattern.matches(it.name) } ?: return
        if (backups.size <= MAX_BACKUPS) return
        val sorted = backups.sortedByDescending { it.lastModified() }
        for (oldBackup in sorted.drop(MAX_BACKUPS)) {
            try {
                if (oldBackup.delete()) {
                    logger.info("Removed old backup: ${oldBackup.name}")
                } else {
                    logger.warn("Could not delete old backup: ${oldBackup.absolutePath}")
                }
            } catch (e: Exception) {
                logger.error("Failed to delete old backup: ${oldBackup.absolutePath}", e)
            }
        }
    }

    /**
     * Migrates some statistics to (Legacy) for changed dyes
     */
    private fun migrateMFRework() {
        logger.info("Migrating config to mf rework.")
        SkyblockUtils.hasMigratedMFRework = false
        for (player in data.players.values) {
            for (profile in player.profiles.values) {
                for ((dye, dyeData) in profile.dyeData) {
                    when (dye) {
                        Dye.AQUAMARINE -> {
                            dyeData.migrateStatsToLegacy(listOf(
                                "Common/Uncommon Sea Creature Kills",
                                "Rare/Epic Sea Creature Kills",
                                "Legendary/Mythic Sea Creature Kills",
                                "Magic Find on Common-Epic",
                                "Looting on Common-Epic",
                                "Magic Find on Legendary-Mythic",
                                "Looting on Legendary-Mythic",
                                "Magic Find",
                                "Looting",
                            ))
                        }
                        Dye.BONE -> {
                            dyeData.migrateStatsToLegacy(listOf(
                                "Skeleton Kills",
                                "Magic Find",
                                "Looting",
                            ))
                        }
                        Dye.BRICK_RED -> {
                            dyeData.migrateStatsToLegacy(listOf(
                                "T1 Tarantula Broodfather Kills",
                                "T2 Tarantula Broodfather Kills",
                                "T3 Tarantula Broodfather Kills",
                                "T4 Tarantula Broodfather Kills",
                                "T5 Tarantula Broodfather Kills",
                            ))
                        }
                        Dye.BYZANTIUM -> {
                            dyeData.migrateStatsToLegacy(listOf(
                                "T1 Voidgloom Seraph Kills",
                                "T2 Voidgloom Seraph Kills",
                                "T3 Voidgloom Seraph Kills",
                                "T4 Voidgloom Seraph Kills",
                            ))
                        }
                        Dye.CARMINE -> {
                            dyeData.migrateStatsToLegacy(listOf(
                                "Common/Uncommon Sea Creature Kills",
                                "Rare/Epic Sea Creature Kills",
                                "Legendary/Mythic Sea Creature Kills",
                                "Magic Find on Common-Epic",
                                "Looting on Common-Epic",
                                "Magic Find on Legendary-Mythic",
                                "Looting on Legendary-Mythic",
                                "Magic Find",
                                "Looting",
                            ))
                        }
                        Dye.CELADON -> {
                            dyeData.migrateStatsToLegacy(listOf(
                                "Bacte Kills",
                                "Blobbercyst Kills"
                            ))
                        }
                        Dye.CELESTE -> {
                            dyeData.migrateStatsToLegacy(listOf(
                                "T1 Sven Packmaster Kills",
                                "T2 Sven Packmaster Kills",
                                "T3 Sven Packmaster Kills",
                                "T4 Sven Packmaster Kills",
                            ))
                        }
                        Dye.CYCLAMEN -> {
                            dyeData.migrateStatsToLegacy(listOf(
                                "1/10m Mob Kills",
                                "1/2.5m Mob Kills",
                                "1/250k Mob Kills",
                                "Miniboss Kills",
                                "Magic Find",
                                "Looting",
                            ))
                        }
                        Dye.EMERALD -> {
                            dyeData.migrateStatsToLegacy(listOf(
                                "Emerald Blocks Mined",
                            ))
                        }
                        Dye.FLAME -> {
                            dyeData.migrateStatsToLegacy(listOf(
                                "T1 Inferno Demonlord Kills",
                                "T2 Inferno Demonlord Kills",
                                "T3 Inferno Demonlord Kills",
                                "T4 Inferno Demonlord Kills",
                            ))
                        }
                        Dye.FOSSIL -> {
                            dyeData.migrateStatsToLegacy(listOf(
                                "Suspicious Scrap Excavated",
                            ))
                        }
                        Dye.FROSTBITTEN -> {
                            dyeData.migrateStatsToLegacy(listOf(
                                "Umber/Tungsten Corpses Looted",
                            ))
                        }
                        Dye.ICEBERG -> {
                            dyeData.migrateStatsToLegacy(listOf(
                                "Common/Uncommon Sea Creature Kills",
                                "Rare/Epic Sea Creature Kills",
                                "Legendary/Mythic Sea Creature Kills",
                                "Magic Find on Common-Epic",
                                "Looting on Common-Epic",
                                "Magic Find on Legendary-Mythic",
                                "Looting on Legendary-Mythic",
                                "Magic Find",
                                "Looting",
                            ))
                        }
                        Dye.JADE -> {
                            dyeData.migrateStatsToLegacy(listOf(
                                "Nucleus Runs Completed",
                            ))
                        }
                        Dye.MANGO -> {
                            dyeData.migrateStatsToLegacy(listOf(
                                "Logs Broken",
                            ))
                        }
                        Dye.MATCHA -> {
                            dyeData.migrateStatsToLegacy(listOf(
                                "T1 Revenant Horror Kills",
                                "T2 Revenant Horror Kills",
                                "T3 Revenant Horror Kills",
                                "T4 Revenant Horror Kills",
                                "T5 Revenant Horror Kills",
                            ))
                        }
                        Dye.MIDNIGHT -> {
                            dyeData.migrateStatsToLegacy(listOf(
                                "Common/Uncommon/Rare/Epic Sea Creature Kills",
                                "Legendary/Mythic Sea Creature Kills",
                                "Spooky Mob Kills",
                                "Headless Horseman Kills",
                                "Magic Find on Common-Epic",
                                "Looting on Common-Epic",
                                "Magic Find on Legendary-Mythic",
                                "Looting on Legendary-Mythic",
                                "Magic Find on Horseman",
                                "Looting on Horseman",
                                "Magic Find",
                                "Looting",
                            ))
                        }
                        Dye.MYTHOLOGICAL -> {
                            dyeData.migrateStatsToLegacy(listOf(
                                "Common/Uncommon Mythological Creature Kills",
                                "Rare Mythological Creature Kills",
                                "Epic Mythological Creature Kills",
                                "Legendary Mythological Creature Kills",
                                "Mythic Mythological Creature Kills",
                                "Magic Find on Common-Epic",
                                "Looting on Common-Epic",
                                "Magic Find on Legendary-Mythic",
                                "Looting on Legendary-Mythic",
                                "Magic Find",
                                "Looting",
                            ))
                        }
                        Dye.NADESHIKO -> {
                            dyeData.migrateStatsToLegacy(listOf(
                                "Supreme Superpairs Experiments",
                                "Transcendent Superpairs Experiments",
                                "Metaphysical Superpairs Experiments",
                            ))
                        }
                        Dye.NYANZA -> {
                            dyeData.migrateStatsToLegacy(listOf(
                                "Mining Commissions Completed",
                            ))
                        }
                        Dye.PEARLESCENT -> {
                            dyeData.migrateStatsToLegacy(listOf(
                                "1/10m Mob Kills",
                                "1/5m Mob Kills",
                                "1/100k Mob Kills",
                                "Boss Kills",
                                "Magic Find",
                                "Looting",
                            ))
                        }
                        Dye.PELT -> {
                            dyeData.migrateStatsToLegacy(listOf(
                                "Trackable Animal Kills",
                                "Untrackable Animal Kills",
                                "Undetected Animal Kills",
                                "Endangered Animal Kills",
                                "Elusive Animal Kills",
                            ))
                        }
                        Dye.PERIWINKLE -> {
                            dyeData.migrateStatsToLegacy(listOf(
                                "Runic Kills",
                                "Magic Find",
                                "Looting",
                            ))
                        }
                        Dye.SANGRIA -> {
                            dyeData.migrateStatsToLegacy(listOf(
                                "T1 Riftstalker Bloodfiend Kills",
                                "T2 Riftstalker Bloodfiend Kills",
                                "T3 Riftstalker Bloodfiend Kills",
                                "T4 Riftstalker Bloodfiend Kills",
                                "T5 Riftstalker Bloodfiend Kills",
                            ))
                        }
                        Dye.SECRET -> {
                            dyeData.migrateStatsToLegacy(listOf(
                                "Catacombs Secrets Collected",
                            ))
                        }
                        Dye.TENTACLE -> {
                            dyeData.migrateStatsToLegacy(listOf(
                                "Basic Kuudra Completions + Kismets Used",
                                "Hot Kuudra Completions + Kismets Used",
                                "Burning Kuudra Completions + Kismets Used",
                                "Fiery Kuudra Completions + Kismets Used",
                                "Infernal Kuudra Completions + Kismets Used",
                            ))
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}