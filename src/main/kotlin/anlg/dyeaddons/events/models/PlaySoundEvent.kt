package anlg.dyeaddons.events.models

import net.minecraft.client.resources.sounds.SoundInstance

data class SoundPlayEvent(
    val sound: SoundInstance,
    val volume: Float,
    val pitch: Float,
    val x: Double,
    val y: Double,
    val z: Double,
) {
    val name = sound.identifier.toString().removePrefix("minecraft:")
}