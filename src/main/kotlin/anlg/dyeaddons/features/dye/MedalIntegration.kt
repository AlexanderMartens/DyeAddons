package anlg.dyeaddons.features.dye

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.events.models.MedalEvent
import anlg.dyeaddons.settings.categories.DebugCategories
import anlg.dyeaddons.settings.categories.Dyes
import anlg.dyeaddons.utils.ChatUtils
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import java.util.concurrent.CompletableFuture

/**
 * Credits to SBO.
 */
object MedalIntegration {

    private const val PUBLIC_KEY = "pub_0V8SwC8KH2ldj4FMNH19WhuuleN2EqUO"
    private const val MEDAL_API_URL = "http://localhost:12665/api/v1/event/invoke"

    private val client: HttpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(2))
        .build()

    fun saveDyeClip(dye: Dye) {
        if (!Dyes.medalClipDyes) return
        if (dye.isShopDye() && !Dyes.medalClipPurchaseDyes) return

        saveClip(MedalEvent(
            "${dye.ordinal + 1}",
            "$dye Dye Drop",
            Dyes.medalClipDurationSeconds,
            Dyes.medalCaptureDelayMs
        ))
    }

    fun testClip(dye: Dye) {
        if (!Dyes.medalClipToggle) {
            ChatUtils.addLocalChatMessage("Medal Clips are disabled. Enable them before testing.", true)
        }
        DyeAddons.logger.info("Sending Medal test clip request: $dye Dye")
        saveDyeClip(dye)
    }

    private fun saveClip(event: MedalEvent) {
        if (!Dyes.medalClipToggle) return

        val body = """
            {"eventId":"${event.eventId}","eventName":"${event.eventName}","triggerActions":["SaveClip"],"clipOptions":{"duration":${event.durationSeconds},"captureDelayMs":${event.captureDelayMs}}}
        """.trimIndent()

        CompletableFuture.runAsync {
            try {
                val request = HttpRequest.newBuilder()
                    .uri(URI.create(MEDAL_API_URL))
                    .header("Content-Type", "application/json; charset=utf-8")
                    .header("publicKey", PUBLIC_KEY)
                    .timeout(Duration.ofSeconds(1))
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build()

                val response = client.send(request, HttpResponse.BodyHandlers.discarding())
                if (response.statusCode() == 200) {
                    DyeAddons.debug("Saved clip for ${event.eventName}", DebugCategories.OTHER)
                } else {
                    DyeAddons.logger.warn("Medal clip failed for ${event.eventName}: HTTP ${response.statusCode()}")
                    ChatUtils.addLocalChatMessage("Failed to clip. Medal may not be running.", true)
                }
            } catch (e: Exception) {
                DyeAddons.logger.warn("Medal clip failed for ${event.eventName}", e)
                ChatUtils.addLocalChatMessage("Failed to clip. Medal may not be running.", true)
            }
        }
    }
}