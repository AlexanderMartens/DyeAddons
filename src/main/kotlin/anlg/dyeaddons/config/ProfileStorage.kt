package anlg.dyeaddons.config

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.utils.SkyblockUtils

object ProfileStorage {

    fun currentProfile(): ProfileData? {
        val uuid = DyeAddons.mc.player?.uuid ?: return null
        val profileName = SkyblockUtils.profileName
        if (profileName.isEmpty()) return null

        val player = ConfigManager.data.players.getOrPut(uuid) {
                PlayerData()
            }

        return player.profiles.getOrPut(profileName) {
            ProfileData()
        }
    }

    fun getProfileData(profile : String) : ProfileData? {
        if (profile.isEmpty()) return null
        val uuid = DyeAddons.mc.player?.uuid ?: return null
        val player = ConfigManager.data.players.getOrPut(uuid) {
            PlayerData()
        }
        return player.profiles.getOrDefault(profile, null)
    }
}