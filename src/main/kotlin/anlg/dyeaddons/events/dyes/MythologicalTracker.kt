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

    private enum class MythologicalCreature (val baseChance : Double){
        COMMON(dye.getDropRate("Common")),
        UNCOMMON(dye.getDropRate("Uncommon")),
        RARE(dye.getDropRate("Rare")),
        EPIC(dye.getDropRate("Epic")),
        LEGENDARY(dye.getDropRate("Legendary")),
        MYTHIC(dye.getDropRate("Mythic"))
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
            MythologicalCreature.COMMON -> stats.incrementStat(dye, "Common Mythological Creatures")
            MythologicalCreature.UNCOMMON -> stats.incrementStat(dye, "Uncommon Mythological Creatures")
            MythologicalCreature.RARE -> stats.incrementStat(dye, "Rare Mythological Creatures")
            MythologicalCreature.EPIC -> stats.incrementStat(dye, "Epic Mythological Creatures")
            MythologicalCreature.LEGENDARY -> stats.incrementStat(dye, "Legendary Mythological Creatures")
            MythologicalCreature.MYTHIC -> stats.incrementStat(dye, "Mythic Mythological Creatures")
        }
    }

    private fun updateDyeProgress(mobType: MythologicalCreature) {
        val stats = ProfileStorage.lastPlayedProfile() ?: return

        val dropRate = (1.0 / mobType.baseChance) * stats.getDyeMultiplier(dye)

        ProfileStorage.lastPlayedProfile()?.dyeData[dye]?.progress += dropRate
        FakeDyeDrop.rollFakeDyeDrop(dye,
            mobType.baseChance,
            dropRate,
            stats.getMagicFind(dye, DyeMultiplier.MAGIC_FIND))
    }
}