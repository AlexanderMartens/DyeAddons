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

object AquamarineTracker {

    private val commonMobs = setOf(
        "Squid",
        "Sea Walker",
        "Frog Man",
        "Trash Gobbler",
        "Atoll Croaker",
        "Bogged",
        "Haggard",
        "Frozen Steve",
    )
    private val uncommonMobs = setOf(
        "Sea Witch",
        "Sea Archer",
        "Rider of the Deep",
        "Inkling",
        "Oasis Sheep",
        "Oasis Rabbit",
        "Dumpster Diver",
        "Lotus Guardian",
        "Nurse Shark",
        "Wetwing",
        "Brineling",
        "Frosty",
    )
    private val rareMobs = setOf(
        "Catfish",
        "Sea Leech",
        "Snapping Turtle",
        "Water Worm",
        "Poisoned Water Worm",
        "Banshee",
        "gorF",
        "Blue Shark",
        "Tadgang",
        "Carrot King",
        "Agarimoo",
        "Sprawl",
        "Grinch",
    )
    private val epicMobs = setOf(
        "Guardian Defender",
        "Deep Sea Protector",
        "Manta Ray",
        "Bayou Sludge",
        "Drowned Captain",
        "Tiger Shark",
        "Ent",
        "Torrid",
        "Nutcracker",
    )
    private val legendaryMobs = setOf(
        "Water Hydra",
        "Blue Ringed Octopus",
        "Abyssal Miner",
        "Alligator",
        "Puddle Jumper",
        "Great White Shark",
        "The Loch Emperor",
        "Silkbreeze",
        "Yeti",
    )
    private val mythicMobs = setOf(
        "Wiki Tiki",
        "Titanoboa",
        "Frog Prince",
        "Nesse",
        "Giant Isopod"
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
        val stats = ProfileStorage.lastPlayedProfile()?.dyeData[Dye.AQUAMARINE]?.statistics ?: return

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
        val stats = ProfileStorage.lastPlayedProfile() ?: return

        val (magicFind, looting) = when (mobType) {
            SeaCreature.COMMON -> Pair(DyeMultiplier.MAGIC_FIND_T1, DyeMultiplier.LOOTING_T1)
            SeaCreature.UNCOMMON -> Pair(DyeMultiplier.MAGIC_FIND_T1, DyeMultiplier.LOOTING_T1)
            SeaCreature.RARE -> Pair(DyeMultiplier.MAGIC_FIND_T1, DyeMultiplier.LOOTING_T1)
            SeaCreature.EPIC -> Pair(DyeMultiplier.MAGIC_FIND_T1, DyeMultiplier.LOOTING_T1)
            SeaCreature.LEGENDARY -> Pair(DyeMultiplier.MAGIC_FIND_T2, DyeMultiplier.LOOTING_T2)
            SeaCreature.MYTHIC -> Pair(DyeMultiplier.MAGIC_FIND_T2, DyeMultiplier.LOOTING_T2)
        }
        val dropRate = 1.0 / mobType.baseChance * stats.getDyeMultiplier(
            Dye.AQUAMARINE,
            magicFind,
            looting,
            DyeMultiplier.VINCENT,
            DyeMultiplier.BUCKET_OF_DYE,
            DyeMultiplier.MIRACLE_CHANCE)

        ProfileStorage.lastPlayedProfile()?.dyeData[Dye.AQUAMARINE]?.progress += dropRate
    }

}