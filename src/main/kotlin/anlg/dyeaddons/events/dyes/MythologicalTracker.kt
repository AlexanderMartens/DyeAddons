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

object MythologicalTracker {

    private val dye = Dye.MYTHOLOGICAL

    private val commonMobs = setOf(
        "Minos Hunter",
        "Bagheera",
        "Azrael",
    )
    private val uncommonMobs = setOf(
        "Stranded Nymph",
        "Cretan Bull"
    )
    private val rareMobs = setOf(
        "Harpy",
        "Gaia Construct"
    )
    private val epicMobs = setOf(
        "Minotaur",
        "Minos Champion"
    )
    private val legendaryMobs = setOf(
        "Sphinx",
        "Minos Inquisitor"
    )
    private val mythicMobs = setOf(
        "Manticore",
        "King Minos"
    )

    private val mythoPrefixes = listOf(
        "Blessed ",
        "Stalwart ",
        "Venerable ",
        "Exalted ",
        "Empyrean "
    )

    private enum class MythologicalCreature (val baseChance : Float){
        COMMON(1_000_000f),
        UNCOMMON(1_000_000f),
        RARE(500_000f),
        EPIC(250_000f),
        LEGENDARY(50_000f),
        MYTHIC(10_000f)
    }

    fun init() {
        EventBus.subscribe(MobKillEvent::class, ::onMobKillEvent)
    }

    private fun onMobKillEvent(event: MobKillEvent) {
        if (!SkyblockUtils.hypixelMain ||
            !SkyblockUtils.isInSkyblock() ||
            SkyblockUtils.getWorldName() != "Hub") return

        val mobName = mythoPrefixes.firstOrNull { event.mobName.startsWith(it) }
            ?.let { event.mobName.removePrefix(it) }
            ?: event.mobName

        val mobType = when (mobName) {
            in commonMobs -> MythologicalCreature.COMMON
            in uncommonMobs -> MythologicalCreature.UNCOMMON
            in rareMobs -> MythologicalCreature.RARE
            in epicMobs -> MythologicalCreature.EPIC
            in legendaryMobs -> MythologicalCreature.LEGENDARY
            in mythicMobs -> MythologicalCreature.MYTHIC
            else -> return
        }

        DyeAddons.debug("Tracked $mobName Kill, Type: $mobType", DebugCategories.DYE_PROGRESS_EVENT)
        updateDyeStats(mobType)
        updateDyeProgress(mobType)
    }

    private fun updateDyeStats(mobType: MythologicalCreature) {
        val stats = ProfileStorage.lastPlayedProfile() ?: return

        when (mobType) {
            MythologicalCreature.COMMON -> stats.incrementStat(dye, "Common/Uncommon Mythological Creature Kills")
            MythologicalCreature.UNCOMMON -> stats.incrementStat(dye, "Common/Uncommon Mythological Creature Kills")
            MythologicalCreature.RARE -> stats.incrementStat(dye, "Rare Mythological Creature Kills")
            MythologicalCreature.EPIC -> stats.incrementStat(dye, "Epic Mythological Creature Kills")
            MythologicalCreature.LEGENDARY -> stats.incrementStat(dye, "Legendary Mythological Creature Kills")
            MythologicalCreature.MYTHIC -> stats.incrementStat(dye, "Mythic Mythological Creature Kills")
        }
    }

    private fun updateDyeProgress(mobType: MythologicalCreature) {
        val stats = ProfileStorage.lastPlayedProfile() ?: return

        val (magicFind, looting) = when (mobType) {
            MythologicalCreature.COMMON -> Pair(DyeMultiplier.MAGIC_FIND_T1, DyeMultiplier.LOOTING_T1)
            MythologicalCreature.UNCOMMON -> Pair(DyeMultiplier.MAGIC_FIND_T1, DyeMultiplier.LOOTING_T1)
            MythologicalCreature.RARE -> Pair(DyeMultiplier.MAGIC_FIND_T1, DyeMultiplier.LOOTING_T1)
            MythologicalCreature.EPIC -> Pair(DyeMultiplier.MAGIC_FIND_T1, DyeMultiplier.LOOTING_T1)
            MythologicalCreature.LEGENDARY -> Pair(DyeMultiplier.MAGIC_FIND_T2, DyeMultiplier.LOOTING_T2)
            MythologicalCreature.MYTHIC -> Pair(DyeMultiplier.MAGIC_FIND_T2, DyeMultiplier.LOOTING_T2)
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