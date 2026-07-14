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

object CyclamenTracker {

    val t1Mobs = setOf(
        "Blaze",
        "Bezal",
        "Mutated Blaze",
        "Magma Cube",
        "Pack Magma Cube",
        "Flaming Spider",
        "Kada Knight",
        "Magma Cube Rider",
        "Mushroom Bull",
        "WIther Skeleton",
        "Wither Spectre",
        "Flare",
    )
    val t2Mobs = setOf(
        "Ghast",
        "Dive Ghast",
        "Barbarian",
        "Barbarian Guard",
        "Fire Mage",
        "Goliath Barbarian",
        "Krondor Necromancer",
        "Mage Guard",
        "Unstable Magma",
        "Magma Glare",
        "Millennia-Aged Blaze",
        "Smoldering Blaze",
    )
    val t3Mobs = setOf(
        "Matcho",
        "Hellwisp",
        "Vanquisher",
        "Cinderbat",
    )

    val MINIBOSS_PATTERN = Regex("""[A-Z ]+ DOWN!""")

    private enum class CyclamenType (val dropRate: Int){
        T1(10_000_000),
        T2(2_500_000),
        T3(250_000),
        MINIBOSS(250_000)
    }

    fun init() {
        EventBus.subscribe(MobKillEvent::class, ::onMobKillEvent)
        EventBus.subscribe(ChatEvent::class, ::onChat)
    }

    private fun onMobKillEvent(event: MobKillEvent) {
        if (!SkyblockUtils.hypixelMain ||
            !SkyblockUtils.isInSkyblock() ||
            SkyblockUtils.getWorldName() != "Crimson Isle") return

        val mobName = event.mobName

        val mobType = when (mobName) {
            in t1Mobs -> CyclamenType.T1
            in t2Mobs -> CyclamenType.T2
            in t3Mobs -> CyclamenType.T3
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

        if (MINIBOSS_PATTERN.matches(event.unformattedText.trim())) {
            DyeAddons.debug("Tracked Miniboss Kill")
            updateDyeStats(CyclamenType.MINIBOSS)
            updateDyeProgress(CyclamenType.MINIBOSS)
        }
    }

    private fun updateDyeStats(mobType: CyclamenType) {
        val stats = ProfileStorage.lastPlayedProfile()?.dyeData[Dye.CYCLAMEN]?.statistics ?: return

        when (mobType) {
            CyclamenType.T1 -> stats.incrementInt("1/10m Mob Kills")
            CyclamenType.T2 -> stats.incrementInt("1/2.5m Mob Kills")
            CyclamenType.T3 -> stats.incrementInt("1/250k Mob Kills")
            CyclamenType.MINIBOSS -> stats.incrementInt("Miniboss Kills")
        }
    }

    private fun updateDyeProgress(mobType: CyclamenType) {
        val dyeRotation = ConfigManager.data.config.currentDyeRotation
        val multiplier = dyeRotation?.getMultiplier(Dye.CYCLAMEN) ?: 1
        val stats = ProfileStorage.lastPlayedProfile()?.dyeData[Dye.CYCLAMEN]?.statistics ?: return

        val magicFind = stats["Magic Find"]?.asFloat() ?: 0f
        val looting = stats["Looting"]?.asInt() ?: 0

        val dropRate = 1.0 / mobType.dropRate * if (mobType != CyclamenType.MINIBOSS) ((1.0 + magicFind / 100.0) * (1.0 + looting * 0.15)) else 1.0

        ProfileStorage.lastPlayedProfile()?.dyeData[Dye.CYCLAMEN]?.progress += dropRate * multiplier
    }

}