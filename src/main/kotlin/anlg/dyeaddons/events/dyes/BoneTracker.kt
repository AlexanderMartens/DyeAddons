package anlg.dyeaddons.events.dyes

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.config.ConfigManager
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.MobKillEvent
import anlg.dyeaddons.utils.SkyblockUtils
import anlg.dyeaddons.utils.extensions.incrementInt
import net.minecraft.world.entity.EntityType

object BoneTracker {

    private val skeletonTypes = setOf(
        EntityType.SKELETON,
        EntityType.WITHER_SKELETON,
        EntityType.BOGGED
    )

    fun init() {
        EventBus.subscribe(MobKillEvent::class, ::onMobKillEvent)
    }

    private fun onMobKillEvent(event: MobKillEvent) {
        if (!SkyblockUtils.hypixelMain || !SkyblockUtils.isInSkyblock()) return
        if (event.entity.type !in skeletonTypes) return

        DyeAddons.debug("Tracked Skeleton Kill")
        updateDyeStats()
        updateDyeProgress()
    }

    private fun updateDyeStats() {
        val stats = ProfileStorage.lastPlayedProfile()?.dyeData[Dye.BONE]?.statistics ?: return

        stats.incrementInt("Skeleton Kills")
    }

    private fun updateDyeProgress() {
        val dyeRotation = ConfigManager.data.config.currentDyeRotation
        val multiplier = dyeRotation?.getMultiplier(Dye.BONE) ?: 1
        val stats = ProfileStorage.lastPlayedProfile()?.dyeData[Dye.BONE]?.statistics ?: return

        val magicFind = stats["Magic Find"]?.asFloat() ?: 0f
        val looting = stats["Looting"]?.asInt() ?: 0

        ProfileStorage.lastPlayedProfile()?.dyeData[Dye.BONE]?.progress += (1.0 / 3_000_000.0) * (1.0 + magicFind / 100.0) * (1.0 + looting * 0.15) * multiplier
    }

}