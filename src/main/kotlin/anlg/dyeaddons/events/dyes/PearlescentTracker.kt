package anlg.dyeaddons.events.dyes

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.config.DyeMultiplier
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.ChatEvent
import anlg.dyeaddons.events.models.MobKillEvent
import anlg.dyeaddons.features.dye.FakeDyeDrop
import anlg.dyeaddons.settings.categories.DebugCategories
import anlg.dyeaddons.utils.SkyblockUtils

object PearlescentTracker {

    private val dye = Dye.PEARLESCENT

    val t1Mobs = setOf(
        "Enderman",
        "Endermite",
        "Obsidian Defender",
        "Seer",
        "Zealot",
        "Zealot Bruiser"
    )
    val t2Mobs = setOf(
        "Nest Endermite",
        "Voidling Extremist",
        "Voidling Fanatic",
    )
    val t3Mobs = setOf(
        "Special Zealot"
    )

    val BOSS_PATTERN = Regex("""[A-Z ]+ DOWN!""")

    private enum class PearlescentType (val dropRate: Int){
        T1(10_000_000),
        T2(5_000_000),
        T3(100_000),
        BOSS(100_000)
    }

    fun init() {
        EventBus.subscribe(MobKillEvent::class, ::onMobKillEvent)
        EventBus.subscribe(ChatEvent::class, ::onChat)
    }

    private fun onMobKillEvent(event: MobKillEvent) {
        if (!SkyblockUtils.hypixelMain ||
            !SkyblockUtils.isInSkyblock() ||
            SkyblockUtils.getWorldName() != "The End") return

        val mobName = event.mobName

        val mobType = when (mobName) {
            in t1Mobs -> PearlescentType.T1
            in t2Mobs -> PearlescentType.T2
            in t3Mobs -> PearlescentType.T3
            else -> return
        }

        DyeAddons.debug("Tracked $mobName Kill, Type: $mobType", DebugCategories.DYE_PROGRESS_EVENT)
        updateDyeStats(mobType)
        updateDyeProgress(mobType)
    }

    private fun onChat(event: ChatEvent) {
        if (!SkyblockUtils.hypixelMain ||
            !SkyblockUtils.isInSkyblock() ||
            SkyblockUtils.getWorldName() != "Crimson Isle") return

        if (BOSS_PATTERN.matches(event.unformattedText.trim())) {
            DyeAddons.debug("Tracked Miniboss Kill", DebugCategories.DYE_PROGRESS_EVENT)
            updateDyeStats(PearlescentType.BOSS)
            updateDyeProgress(PearlescentType.BOSS)
            if (event.unformattedText.trim().contains("SUPERIOR")) updateDyeProgress(PearlescentType.BOSS)
        }
    }

    private fun updateDyeStats(mobType: PearlescentType) {
        val stats = ProfileStorage.lastPlayedProfile() ?: return

        when (mobType) {
            PearlescentType.T1 -> stats.incrementStat(dye, "1/10m Mob Kills")
            PearlescentType.T2 -> stats.incrementStat(dye, "1/5m Mob Kills")
            PearlescentType.T3 -> stats.incrementStat(dye, "1/100k Mob Kills")
            PearlescentType.BOSS -> stats.incrementStat(dye, "Miniboss Kills")
        }
    }

    private fun updateDyeProgress(mobType: PearlescentType) {
        val stats = ProfileStorage.lastPlayedProfile() ?: return

        val dropRate = 1.0 / mobType.dropRate *
                if (mobType != PearlescentType.BOSS) {
                    stats.getDyeMultiplier(
                        dye,
                        DyeMultiplier.MAGIC_FIND,
                        DyeMultiplier.LOOTING,
                        DyeMultiplier.VINCENT,
                        DyeMultiplier.BUCKET_OF_DYE,
                        DyeMultiplier.MIRACLE_CHANCE)
                }
                else {
                    stats.getDyeMultiplier(
                        dye,
                        DyeMultiplier.VINCENT,
                        DyeMultiplier.BUCKET_OF_DYE,
                        DyeMultiplier.MIRACLE_CHANCE)
                }

        ProfileStorage.lastPlayedProfile()?.dyeData[dye]?.progress += dropRate
        if (mobType != PearlescentType.BOSS) {
            FakeDyeDrop.rollFakeDyeDrop(dye,
                mobType.dropRate.toDouble(),
                dropRate,
                stats.getMagicFind(dye, DyeMultiplier.MAGIC_FIND))
        } else {
            FakeDyeDrop.rollFakeDyeDrop(dye,
                mobType.dropRate.toDouble(),
                dropRate)
        }
    }
}