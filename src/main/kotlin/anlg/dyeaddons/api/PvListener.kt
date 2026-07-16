package anlg.dyeaddons.api

import anlg.dyeaddons.DyeAddons.Companion.logger
import anlg.dyeaddons.DyeAddons.Companion.mc
import anlg.dyeaddons.config.ProfileStorage
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

            // Update dye modifiers
            ProfileStorage.lastPlayedProfile()?.dyeModifiers["Pity Level"] =
                ProfileCache.latestProfile?.getMember(mc.player?.uuid)?.attributes?.objPath("stacks")?.get("pity")?.asInt ?: 0

            ChatUtils.addLocalChatMessage("Successfully loaded profile!", true)

        } catch (e: Exception) {
            logger.error("Failed to parse PV profile", e)
        }
    }
}