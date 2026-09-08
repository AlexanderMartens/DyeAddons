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

object CarmineTracker {

    private val dye = Dye.CARMINE

    private val commonMobs = setOf(
        "Fried Chicken"
    )
    private val uncommonMobs = setOf(
        "Magma Slug", // not counting baby
        "Moogma",
        "Volcanic Snail",
    )
    private val rareMobs = setOf(
        "Lava Leech",
        "Pyroclasitc Worm",
        "Lava Flame",
        "Fire Eel",
        "Fireproof Witch",
        "Flaming Worm",
        "Stridersurfer"
    )
    private val epicMobs = setOf(
        "Taurus",
        "Magma Pillar",
        "Lava Blaze",
        "Lava Pigman"
    )
    private val legendaryMobs = setOf(
        "Thunder",
        "Fiery Scuttler",
        "Plhlegblast"
    )
    private val mythicMobs = setOf(
        "Lord Jawbus",
        "Ragnarok"
    )

    private enum class SeaCreature (val baseChance : Float){
        COMMON(5_000_000f),
        UNCOMMON(5_000_000f),
        RARE(2_500_000f),
        EPIC(2_500_000f),
        LEGENDARY(50_000f),
        MYTHIC(50_000f)
    }

    fun init() {
        EventBus.subscribe(MobKillEvent::class, ::onMobKillEvent)
    }

    private fun onMobKillEvent(event: MobKillEvent) {
        if (!SkyblockUtils.hypixelMain ||
            !SkyblockUtils.isInSkyblock()) return

        val mobName = event.mobName

        val mobType = when (mobName) {
            in commonMobs -> SeaCreature.COMMON
            in uncommonMobs -> SeaCreature.UNCOMMON
            in rareMobs -> SeaCreature.RARE
            in epicMobs -> SeaCreature.EPIC
            in legendaryMobs -> SeaCreature.LEGENDARY
            in mythicMobs -> SeaCreature.MYTHIC
            else -> return
        }

        DyeAddons.debug("Tracked $mobName Kill, Type: $mobType", DebugCategories.DYE_PROGRESS_EVENT)
        updateDyeStats(mobType)
        updateDyeProgress(mobType)
    }

    private fun updateDyeStats(mobType: SeaCreature) {
        val stats = ProfileStorage.lastPlayedProfile() ?: return

        when (mobType) {
            SeaCreature.COMMON -> stats.incrementStat(dye, "Common/Uncommon Sea Creature Kills")
            SeaCreature.UNCOMMON -> stats.incrementStat(dye, "Common/Uncommon Sea Creature Kills")
            SeaCreature.RARE -> stats.incrementStat(dye, "Rare/Epic Sea Creature Kills")
            SeaCreature.EPIC -> stats.incrementStat(dye, "Rare/Epic Sea Creature Kills")
            SeaCreature.LEGENDARY -> stats.incrementStat(dye, "Legendary/Mythic Sea Creature Kills")
            SeaCreature.MYTHIC -> stats.incrementStat(dye, "Legendary/Mythic Sea Creature Kills")
        }
    }

    private fun updateDyeProgress(mobType: SeaCreature) {
        val stats = ProfileStorage.lastPlayedProfile() ?: return

        val (magicFind, looting) = when (mobType) {
            SeaCreature.COMMON -> Pair(DyeMultiplier.MAGIC_FIND_T1, DyeMultiplier.LOOTING_T1)
            SeaCreature.UNCOMMON -> Pair(DyeMultiplier.MAGIC_FIND_T1, DyeMultiplier.LOOTING_T1)
            SeaCreature.RARE -> Pair(DyeMultiplier.MAGIC_FIND_T1, DyeMultiplier.LOOTING_T1)
            SeaCreature.EPIC -> Pair(DyeMultiplier.MAGIC_FIND_T1, DyeMultiplier.LOOTING_T1)
            SeaCreature.LEGENDARY -> Pair(DyeMultiplier.MAGIC_FIND_T2, DyeMultiplier.LOOTING_T2)
            SeaCreature.MYTHIC -> Pair(DyeMultiplier.MAGIC_FIND_T2, DyeMultiplier.LOOTING_T2)
        }
        val dropRate = (1.0 / mobType.baseChance) * stats.getDyeMultiplier(
            dye,
            magicFind,
            looting,
            DyeMultiplier.VINCENT,
            DyeMultiplier.BUCKET_OF_DYE,
            DyeMultiplier.MIRACLE_CHANCE)

        ProfileStorage.lastPlayedProfile()?.dyeData[dye]?.progress += dropRate
        FakeDyeDrop.rollFakeDyeDrop(dye,
            mobType.baseChance.toDouble(),
            dropRate,
            stats.getMagicFind(dye, magicFind))

    }

}