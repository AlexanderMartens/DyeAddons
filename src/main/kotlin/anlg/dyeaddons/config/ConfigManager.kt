package anlg.dyeaddons.config

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.DyeAddons.Companion.MOD_ID
import anlg.dyeaddons.data.CalcValue
import anlg.dyeaddons.data.CalcValueAdapter
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.ClientTickEvent
import anlg.dyeaddons.events.models.GameClosedEvent
import anlg.dyeaddons.utils.SkyblockUtils
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import net.fabricmc.loader.api.FabricLoader
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

object ConfigManager {
    val gson : Gson = GsonBuilder()
        .registerTypeAdapter(CalcValue::class.java, CalcValueAdapter())
        .setPrettyPrinting()
        .excludeFieldsWithModifiers()
        .serializeSpecialFloatingPointValues()
        .create()

    private val configDir = File(FabricLoader.getInstance().configDir.toFile(), MOD_ID)
    private const val CONFIG_FILE_NAME = "config.json"

    var data: Config = Config()
    private val configFile = File(configDir, CONFIG_FILE_NAME)

    private val executor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor { r ->
        Thread(r, "dyeaddons-config-executor").apply {
            isDaemon = true
        }
    }

    private var tickCounter = 0
    private var saveInterval = 20 * 60 * 5 // Save every 5 minutes

    fun init() {
        load()
        EventBus.subscribe(GameClosedEvent::class, ::onGameClosed)
        EventBus.subscribe(ClientTickEvent::class, ::onTick)
    }

    private fun onGameClosed(@Suppress("UNUSED_PARAMETER") event: GameClosedEvent) {
        save()
    }

    private fun onTick(@Suppress("UNUSED_PARAMETER") event: ClientTickEvent) {
        tickCounter++

        if (tickCounter % saveInterval != 0) return

        save()
    }

    fun load() {
        if (!configFile.exists() || !configFile.canRead()) {
            save()
            SkyblockUtils.isFirstJoin = true
            DyeAddons.debug("Config file does not exist or can't be read")
            return
        }

        try {
            val type = object : TypeToken<Config>() {}.type
            val content = configFile.readText()
            if (content.isBlank()) {
                data = Config()
                save()
                DyeAddons.debug("Config file is blank")
                return
            }
            data = gson.fromJson(content, type) ?: Config()
        } catch (e: Exception) {
            e.printStackTrace()
            data = Config()
        }
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
                DyeAddons.debug("Saved config to $configFile")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}