package anlg.dyeaddons.events.dyes

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.config.ConfigManager
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.ChatEvent
import anlg.dyeaddons.events.models.MobKillEvent
import anlg.dyeaddons.utils.SkyblockUtils
import anlg.dyeaddons.utils.extensions.incrementInt

object PearlescentTracker {

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

        DyeAddons.debug("Tracked $mobName Kill, Type: $mobType")
        updateDyeStats(mobType)
        updateDyeProgress(mobType)
    }

    private fun onChat(event: ChatEvent) {
        if (!SkyblockUtils.hypixelMain ||
            !SkyblockUtils.isInSkyblock() ||
            SkyblockUtils.getWorldName() != "Crimson Isle") return

        if (BOSS_PATTERN.matches(event.unformattedText.trim())) {
            DyeAddons.debug("Tracked Miniboss Kill")
            updateDyeStats(PearlescentType.BOSS)
            updateDyeProgress(PearlescentType.BOSS)
            if (event.unformattedText.trim().contains("SUPERIOR")) updateDyeProgress(PearlescentType.BOSS)
        }
    }

    private fun updateDyeStats(mobType: PearlescentType) {
        val stats = ProfileStorage.lastPlayedProfile()?.dyeData[Dye.PEARLESCENT]?.statistics ?: return

        when (mobType) {
            PearlescentType.T1 -> stats.incrementInt("1/10m Mob Kills")
            PearlescentType.T2 -> stats.incrementInt("1/5m Mob Kills")
            PearlescentType.T3 -> stats.incrementInt("1/100k Mob Kills")
            PearlescentType.BOSS -> stats.incrementInt("Miniboss Kills")
        }
    }

    private fun updateDyeProgress(mobType: PearlescentType) {
        val dyeRotation = ConfigManager.data.config.currentDyeRotation
        val multiplier = dyeRotation?.getMultiplier(Dye.PEARLESCENT) ?: 1
        val stats = ProfileStorage.lastPlayedProfile()?.dyeData[Dye.PEARLESCENT]?.statistics ?: return

        val magicFind = stats["Magic Find"]?.asFloat() ?: 0f
        val looting = stats["Looting"]?.asInt() ?: 0

        val dropRate = 1.0 / mobType.dropRate * if (mobType != PearlescentType.BOSS) ((1.0 + magicFind / 100.0) * (1.0 + looting * 0.15)) else 1.0

        ProfileStorage.lastPlayedProfile()?.dyeData[Dye.PEARLESCENT]?.progress += dropRate * multiplier
    }

}