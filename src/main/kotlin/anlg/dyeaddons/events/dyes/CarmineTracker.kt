package anlg.dyeaddons.events.dyes

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.config.ConfigManager
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.MobKillEvent
import anlg.dyeaddons.settings.categories.DebugCategories
import anlg.dyeaddons.utils.SkyblockUtils
import anlg.dyeaddons.utils.extensions.incrementInt

object CarmineTracker {

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
        val stats = ProfileStorage.lastPlayedProfile()?.dyeData[Dye.CARMINE]?.statistics ?: return

        when (mobType) {
            SeaCreature.COMMON -> stats.incrementInt("Common/Uncommon Sea Creature Kills")
            SeaCreature.UNCOMMON -> stats.incrementInt("Common/Uncommon Sea Creature Kills")
            SeaCreature.RARE -> stats.incrementInt("Rare/Epic Sea Creature Kills")
            SeaCreature.EPIC -> stats.incrementInt("Rare/Epic Sea Creature Kills")
            SeaCreature.LEGENDARY -> stats.incrementInt("Legendary/Mythic Sea Creature Kills")
            SeaCreature.MYTHIC -> stats.incrementInt("Legendary/Mythic Sea Creature Kills")
        }
    }

    private fun updateDyeProgress(mobType: SeaCreature) {
        val dyeRotation = ConfigManager.data.config.currentDyeRotation
        val multiplier = dyeRotation?.getMultiplier(Dye.CARMINE) ?: 1
        val stats = ProfileStorage.lastPlayedProfile()?.dyeData[Dye.CARMINE]?.statistics ?: return

        val magicFind = stats["Magic Find"]?.asFloat() ?: 0f
        val looting = stats["Looting"]?.asInt() ?: 0

        val dropRate = 1.0 / mobType.baseChance * (1.0 + magicFind / 100.0) * (1.0 + looting * 0.15)

        ProfileStorage.lastPlayedProfile()?.dyeData[Dye.CARMINE]?.progress += dropRate * multiplier
    }

}