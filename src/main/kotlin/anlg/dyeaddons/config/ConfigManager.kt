package anlg.dyeaddons.config

import anlg.dyeaddons.DyeAddons.Companion.MOD_ID
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.GameClosedEvent
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.fabricmc.loader.api.FabricLoader
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

object ConfigManager {
    val gson : Gson = GsonBuilder()
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

    fun init() {
        load()
        EventBus.subscribe(GameClosedEvent::class, ::onGameClosed)
    }

    private fun onGameClosed(event: GameClosedEvent) {
        save()
    }

    fun load() {
        if (!configFile.exists() || !configFile.canRead()) {
            save()
            return
        }

        try {
            val content = configFile.readText()
            if (content.isBlank()) {
                data = Config()
                save()
                return
            }
            data = gson.fromJson(content, Config::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            data = Config()
        }
    }

    fun save() {
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
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}