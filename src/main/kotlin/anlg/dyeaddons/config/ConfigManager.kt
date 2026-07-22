package anlg.dyeaddons.config

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.DyeAddons.Companion.MOD_ID
import anlg.dyeaddons.DyeAddons.Companion.logger
import anlg.dyeaddons.data.CalcValue
import anlg.dyeaddons.data.CalcValueAdapter
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.ClientTickEvent
import anlg.dyeaddons.events.models.GameClosedEvent
import anlg.dyeaddons.settings.categories.DebugCategories
import anlg.dyeaddons.utils.SkyblockUtils
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
import java.util.Date
import java.util.UUID
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
            DyeAddons.debug("Config file does not exist or cannot be read.", DebugCategories.FILESYSTEM)
            save()
            SkyblockUtils.isFirstJoin = true
            return
        }

        try {
            val content = configFile.readText()
            if (content.isBlank()) {
                data = UserConfig()
                save()
                DyeAddons.debug("Config file is blank", DebugCategories.FILESYSTEM)
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
                DyeAddons.debug("Failed to load config section", DebugCategories.ERROR)
                backupFiles()
            }

            try {
                if (json.has("players")) {
                    val type = object : TypeToken<MutableMap<UUID, PlayerData>>() {}.type
                    defaults.players = gson.fromJson(json["players"], type) ?: mutableMapOf()
                }
            } catch (e: Exception) {
                logger.error("Failed to load players section", e)
                DyeAddons.debug("Failed to load players section", DebugCategories.ERROR)
                backupFiles()
            }

            data = defaults
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
                logger.info("Saved config to $configFile")
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
        val pattern = Regex("^dyaddons-backup-\\d{4}-\\d{2}-\\d{2}-\\d{6}\\.zip$")
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
}