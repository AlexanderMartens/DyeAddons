package anlg.dyeaddons.utils

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.settings.categories.DebugCategories
import anlg.dyeaddons.settings.categories.General
import net.minecraft.client.resources.sounds.SimpleSoundInstance
import net.minecraft.resources.Identifier
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents

object SoundUtils {
    const val SOUNDS_IDENTIFIER_PREFIX = "dyeaddons"

    fun playSound(sound: SoundEvent = SoundEvents.EXPERIENCE_ORB_PICKUP, pitch: Float = 1.0f, volume: Float = 1.0f) {
        if (!General.soundMode) return

        val mc = DyeAddons.mc
        mc.soundManager.play(SimpleSoundInstance.forUI(sound, pitch, volume))
    }

    fun playCustomSound(fileName: String?) {
        if (!General.soundMode) return

        if (fileName.isNullOrBlank()) return

        try {
            val nameWithoutExtension = fileName.removeSuffix(".ogg")
            val identifier = Identifier.fromNamespaceAndPath(SOUNDS_IDENTIFIER_PREFIX, nameWithoutExtension)
            val soundEvent = SoundEvent.createVariableRangeEvent(identifier)
            playSound(soundEvent)
        } catch (@Suppress("UNUSED_PARAMETER") e: Exception) {
            DyeAddons.debug("Failed to play sound from file name: $fileName", DebugCategories.ERROR)
        }
    }
}