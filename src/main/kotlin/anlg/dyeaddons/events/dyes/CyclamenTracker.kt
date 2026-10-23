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

object CyclamenTracker {

    private val dye = Dye.CYCLAMEN

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

    private enum class CyclamenType (val dropRate: Double){
        T1(dye.getDropRate("T1")),
        T2(dye.getDropRate("T2")),
        T3(dye.getDropRate("T3")),
        MINIBOSS(dye.getDropRate("T3"))
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

        DyeAddons.debug("Tracked $mobName Kill, Type: $mobType", DebugCategories.DYE_PROGRESS_EVENT)
        updateDyeStats(mobType)
        updateDyeProgress(mobType)
    }

    private fun onChat(event: ChatEvent) {
        if (!SkyblockUtils.hypixelMain ||
            !SkyblockUtils.isInSkyblock() ||
            SkyblockUtils.getWorldName() != "Crimson Isle") return

        if (MINIBOSS_PATTERN.matches(event.unformattedText.trim())) {
            DyeAddons.debug("Tracked Miniboss Kill", DebugCategories.DYE_PROGRESS_EVENT)
            updateDyeStats(CyclamenType.MINIBOSS)
            updateDyeProgress(CyclamenType.MINIBOSS)
        }
    }

    private fun updateDyeStats(mobType: CyclamenType) {
        val stats = ProfileStorage.lastPlayedProfile() ?: return

        when (mobType) {
            CyclamenType.T1 -> stats.incrementStat(dye, "T1 Mob Kills")
            CyclamenType.T2 -> stats.incrementStat(dye, "T2 Mob Kills")
            CyclamenType.T3 -> stats.incrementStat(dye, "T3 Mob Kills")
            CyclamenType.MINIBOSS -> stats.incrementStat(dye, "Miniboss Kills")
        }
    }

    private fun updateDyeProgress(mobType: CyclamenType) {
        val stats = ProfileStorage.lastPlayedProfile() ?: return

        val dropRate = 1.0 / mobType.dropRate *
                if (mobType != CyclamenType.MINIBOSS) {
                    stats.getDyeMultiplier(dye)
                }
                else {
                    stats.getDyeMultiplier(
                        dye,
                        DyeMultiplier.VINCENT,
                        DyeMultiplier.BUCKET_OF_DYE,
                        DyeMultiplier.MIRACLE_CHANCE,
                        DyeMultiplier.PAINT_PALETTE,
                        DyeMultiplier.FOUR_LEAF_CLOVER)
                }

        ProfileStorage.lastPlayedProfile()?.dyeData[dye]?.progress += dropRate
        if (mobType != CyclamenType.MINIBOSS) {
            FakeDyeDrop.rollFakeDyeDrop(dye,
                mobType.dropRate,
                dropRate,
                stats.getMagicFind(dye, DyeMultiplier.MAGIC_FIND))
        } else {
            FakeDyeDrop.rollFakeDyeDrop(dye,
                mobType.dropRate,
                dropRate)
        }
    }
}