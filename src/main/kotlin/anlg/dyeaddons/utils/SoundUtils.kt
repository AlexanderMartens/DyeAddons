package anlg.dyeaddons.utils

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.DyeAddons.Companion.logger
import anlg.dyeaddons.DyeAddons.Companion.mc
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.GameStartedEvent
import anlg.dyeaddons.settings.categories.DebugCategories
import anlg.dyeaddons.settings.categories.General
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.sounds.SimpleSoundInstance
import net.minecraft.resources.Identifier
import net.minecraft.server.packs.repository.PackRepository
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.isRegularFile

object SoundUtils {

    const val SOUNDS_IDENTIFIER_PREFIX = "dyeaddons"

    private const val NAMESPACE = "dyeaddons"
    private const val PACK_ID = "dyeaddons_custom_sounds"

    private val VALID_SOUND_NAME = Regex("[a-z0-9._-]+\\.ogg")

    val configSoundDirectory: Path =
        FabricLoader.getInstance()
            .configDir
            .resolve("dyeaddons")
            .resolve("sounds")

    private val resourcePackDirectory: Path =
        FabricLoader.getInstance()
            .gameDir
            .resolve("resourcepacks")
            .resolve(PACK_ID)

    fun init() {
        configSoundDirectory.createDirectories()
        rebuildPack()
        EventBus.subscribe(GameStartedEvent::class, ::onGameStart)
    }

    fun playSound(sound: SoundEvent = SoundEvents.EXPERIENCE_ORB_PICKUP, pitch: Float = 1.0f, volume: Float = 1.0f) {
        if (!General.soundMode) return

        mc.soundManager.play(SimpleSoundInstance.forUI(sound, pitch, volume))
    }

    fun playCustomSound(fileName: String?, pitch: Float = 1.0f, volume: Float = 1.0f) {
        if (!General.soundMode) return

        if (fileName.isNullOrBlank()) return

        try {
            val nameWithoutExtension = fileName.removeSuffix(".ogg")
            val identifier = Identifier.fromNamespaceAndPath(SOUNDS_IDENTIFIER_PREFIX, nameWithoutExtension)
            val soundEvent = SoundEvent.createVariableRangeEvent(identifier)
            playSound(soundEvent, pitch, volume)
        } catch (@Suppress("UNUSED_PARAMETER") e: Exception) {
            DyeAddons.debug("Failed to play sound from file name: $fileName", DebugCategories.ERROR)
        }
    }

    fun playCustomUserSound(fileName: String?, pitch: Float = 1.0f, volume: Float = 1.0f) {
        if (!General.soundMode || fileName.isNullOrBlank()) return

        val id = getSoundId(fileName)

        val soundEvent = SoundEvent.createVariableRangeEvent(id)

        playSound(soundEvent, pitch, volume)
    }

    /**
     * Rebuilds the generated resource pack from the user's sound directory.
     */
    fun rebuildPack() {
        try {
            resourcePackDirectory.createDirectories()

            val assetsDirectory = resourcePackDirectory
                .resolve("assets")
                .resolve(NAMESPACE)

            val soundsDirectory = assetsDirectory.resolve("sounds")
            soundsDirectory.createDirectories()

            writePackMetadata()
            copySounds(soundsDirectory)
            writeSoundsJson(assetsDirectory.resolve("sounds.json"))

            logger.info(
                "Loaded ${getAvailableSounds().size} custom sounds"
            )
        } catch (e: Exception) {
            logger.error(
                "Failed to rebuild custom sound resource pack",
                e
            )
        }
    }

    private fun writePackMetadata() {
        val metadata = """
            {
              "pack": {
                "min_format": 84,
                "max_format": 88,
                "description": "DyeAddons Custom Sounds"
              }
            }
        """.trimIndent()

        Files.writeString(
            resourcePackDirectory.resolve("pack.mcmeta"),
            metadata
        )
    }

    private fun copySounds(destination: Path) {
        // Remove the old generated sounds entirely.
        deleteRecursively(destination)
        destination.createDirectories()

        Files.list(configSoundDirectory).use { files ->
            files.filter { it.isRegularFile() }
                .filter { VALID_SOUND_NAME.matches(it.fileName.toString()) }
                .forEach { source ->
                    val destinationFile =
                        destination
                            .resolve("user")
                            .resolve(source.fileName.toString())

                    destinationFile.parent.createDirectories()

                    Files.copy(
                        source,
                        destinationFile
                    )
                }
        }
    }

    private fun deleteRecursively(path: Path) {
        if (!Files.exists(path)) return

        Files.walk(path)
            .sorted(Comparator.reverseOrder())
            .forEach { Files.deleteIfExists(it) }
    }

    private fun writeSoundsJson(file: Path) {
        val json = buildString {
            appendLine("{")

            val sounds = getAvailableSounds()

            sounds.forEachIndexed { index, sound ->
                val name = sound.removeSuffix(".ogg")

                appendLine(
                    """  "user/$name": {
      "sounds": [
        "$NAMESPACE:user/$name"
      ]
    }${if (index < sounds.lastIndex) "," else ""}"""
                )
            }

            appendLine("}")
        }

        Files.writeString(file, json)
    }

    fun getAvailableSounds(): List<String> {
        if (!Files.exists(configSoundDirectory)) {
            return emptyList()
        }

        return Files.list(configSoundDirectory).use { files ->
            files
                .filter { it.isRegularFile() }
                .map { it.fileName.toString() }
                .filter { VALID_SOUND_NAME.matches(it) }
                .filter { it.endsWith(".ogg", true) }
                .sorted()
                .toList()
        }
    }

    /**
     * Returns the Identifier used by Minecraft for a custom sound.
     *
     * dye-drop.ogg -> dyeaddons:user/dye-drop
     */
    fun getSoundId(fileName: String) = Identifier.fromNamespaceAndPath(
        NAMESPACE,
        "user/${fileName.removeSuffix(".ogg")}")

    private fun reloadRepository() {
        val minecraft = Minecraft.getInstance()
        val repository: PackRepository =
            minecraft.resourcePackRepository

        repository.reload()

        repository.availablePacks.forEach { pack ->
            logger.info("Loaded ${pack.id}, ${pack.description}, ${pack.title.string}")
        }
        if (!repository.isAvailable("file/$PACK_ID")) {
            logger.error("DyeAddons custom sound pack was not found after reload")
            return
        }

        val selected = repository.selectedIds.toMutableList()

        if ("file/$PACK_ID" !in selected) {
            selected.add("file/$PACK_ID")
            repository.setSelected(selected)
        }
    }

    /**
     * Rebuilds and reloads the custom sound pack.
     */
    fun reload() {
        rebuildPack()

        Minecraft.getInstance().execute {
            reloadRepository()
            Minecraft.getInstance().reloadResourcePacks()
        }
    }

    private fun onGameStart(@Suppress("UNUSED_PARAMETER") event: GameStartedEvent) {
        reloadRepository()
    }
}