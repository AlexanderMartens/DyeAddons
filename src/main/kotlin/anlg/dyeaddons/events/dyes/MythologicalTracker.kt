package anlg.dyeaddons.events.dyes

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.config.DyeMultiplier
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.MobKillEvent
import anlg.dyeaddons.settings.categories.DebugCategories
import anlg.dyeaddons.utils.SkyblockUtils
import anlg.dyeaddons.utils.extensions.incrementInt

object MythologicalTracker {

    private val commonMobs = setOf(
        "Minos Hunter",
        "Siamese Lynx"
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

        val mobName = event.mobName

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
        val stats = ProfileStorage.lastPlayedProfile()?.dyeData[Dye.MYTHOLOGICAL]?.statistics ?: return

        when (mobType) {
            MythologicalCreature.COMMON -> stats.incrementInt("Common/Uncommon Mythological Creature Kills")
            MythologicalCreature.UNCOMMON -> stats.incrementInt("Common/Uncommon Mythological Creature Kills")
            MythologicalCreature.RARE -> stats.incrementInt("Rare Mythological Creature Kills")
            MythologicalCreature.EPIC -> stats.incrementInt("Epic Mythological Creature Kills")
            MythologicalCreature.LEGENDARY -> stats.incrementInt("Legendary Mythological Creature Kills")
            MythologicalCreature.MYTHIC -> stats.incrementInt("Mythic Mythological Creature Kills")
        }
    }

    private fun updateDyeProgress(mobType: MythologicalCreature) {
        val stats = ProfileStorage.lastPlayedProfile() ?: return

        val dropRate = (1.0 / mobType.baseChance) * stats.getDyeMultiplier(
            Dye.MYTHOLOGICAL,
            DyeMultiplier.MAGIC_FIND,
            DyeMultiplier.LOOTING,
            DyeMultiplier.VINCENT,
            DyeMultiplier.BUCKET_OF_DYE,
            DyeMultiplier.MIRACLE_CHANCE)

        ProfileStorage.lastPlayedProfile()?.dyeData[Dye.MYTHOLOGICAL]?.progress += dropRate
    }
}