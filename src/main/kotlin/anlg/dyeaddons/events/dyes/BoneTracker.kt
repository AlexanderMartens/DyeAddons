package anlg.dyeaddons.events.dyes

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.config.DyeMultiplier
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.MobKillEvent
import anlg.dyeaddons.features.dye.FakeDyeDrop
import anlg.dyeaddons.settings.categories.DebugCategories
import anlg.dyeaddons.utils.SkyblockUtils

object BoneTracker {

    private val dye = Dye.BONE

    private val skeletonNames = setOf(
        "Skeleton",
        "Apostle",
        "Flint Skeleton",
        "Jockey Skeleton",
        "Miner Skeleton",
        "Obsidian Defender",
        "Seer",
        "Scared Skeleton",
        "Sea Archer",
        "Seer",
        "Skeleton Grunt",
        "Skeleton Lord",
        "Skeleton Master",
        "Skeletor",
        "Sniper",
        "Super Archer",
        "Undead Skeleton",
        "Wither Gourd",
        "Wither Guard",
        "Wither Husk",
        "Wither Miner",
        "Wither Skeleton",
        "Wither Spectre",
        "Withermancer",
        "Bogged",
        "Haggard",
        "Loch Emperor",
        "Bladesoul",
        "Chillblade",
        "Chillshot",
        "Headless Horseman"
    )

    fun init() {
        EventBus.subscribe(MobKillEvent::class, ::onMobKillEvent)
    }

    private fun onMobKillEvent(event: MobKillEvent) {
        if (!SkyblockUtils.hypixelMain || !SkyblockUtils.isInSkyblock()) return
        if (event.mobName !in skeletonNames) return

        DyeAddons.debug("Tracked Skeleton Kill, MobName: ${event.mobName}", DebugCategories.DYE_PROGRESS_EVENT)
        updateDyeStats()
        updateDyeProgress()
    }

    private fun updateDyeStats() {
        val stats = ProfileStorage.lastPlayedProfile() ?: return

        stats.incrementStat(dye, "Skeleton Kills")
    }

    private fun updateDyeProgress() {
        val stats = ProfileStorage.lastPlayedProfile() ?: return

        val dropRate = (1.0 / 3_000_000.0) * stats.getDyeMultiplier(
            dye,
            DyeMultiplier.MAGIC_FIND,
            DyeMultiplier.LOOTING,
            DyeMultiplier.VINCENT,
            DyeMultiplier.BUCKET_OF_DYE,
            DyeMultiplier.MIRACLE_CHANCE)

        ProfileStorage.lastPlayedProfile()?.dyeData[dye]?.progress += dropRate
        FakeDyeDrop.rollFakeDyeDrop(dye,
            3_000_000.0,
            dropRate,
            stats.getMagicFind(dye, DyeMultiplier.MAGIC_FIND))
    }

}