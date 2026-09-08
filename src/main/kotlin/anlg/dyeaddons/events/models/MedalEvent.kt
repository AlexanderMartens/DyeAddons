package anlg.dyeaddons.events.models

data class MedalEvent(
    val eventId: String,
    val eventName: String,
    val durationSeconds: Int,
    val captureDelayMs: Int,
)
