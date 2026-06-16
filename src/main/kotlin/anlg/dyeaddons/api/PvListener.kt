package anlg.dyeaddons.api

import anlg.dyeaddons.DyeAddons.Companion.logger
import anlg.dyeaddons.utils.ChatUtils
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject

object PvListener {

    private val hypixelApiGson by lazy {
        GsonBuilder()
            .setPrettyPrinting()
            .excludeFieldsWithModifiers()
            .create()
    }

    @JvmStatic
    fun onPvLoad(data: JsonObject) {
        try {
            val profile = hypixelApiGson.fromJson(data, Profile::class.java)

            ProfileCache.latestProfile = profile

            ChatUtils.addLocalChatMessage("Successfully loaded profile!", true)

        } catch (e: Exception) {
            logger.error("Failed to parse PV profile", e)
        }
    }
}