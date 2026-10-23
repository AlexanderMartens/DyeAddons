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

object AquamarineTracker {

    private val dye = Dye.AQUAMARINE

    private val commonMobs = setOf(
        "Squid",
        "Sea Walker",
        "Frog Man",
        "Trash Gobbler",
        "Atoll Croaker",
        "Bogged",
        "Haggard",
        "Frozen Steve",
        "Jumpin' Jack",
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
        "Scarecrow",
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
        "Nightmare",
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
        "Werewolf",
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
        "Phantom Fisher"
    )
    private val mythicMobs = setOf(
        "Wiki Tiki",
        "Titanoboa",
        "Frog Prince",
        "Nesse",
        "Giant Isopod",
        "Grim Reaper",
    )

    private enum class SeaCreature (val baseChance : Double){
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
            SeaCreature.COMMON -> stats.incrementStat(dye, "Common Sea Creatures")
            SeaCreature.UNCOMMON -> stats.incrementStat(dye, "Uncommon Sea Creatures")
            SeaCreature.RARE -> stats.incrementStat(dye, "Rare Sea Creatures")
            SeaCreature.EPIC -> stats.incrementStat(dye, "Epic Sea Creatures")
            SeaCreature.LEGENDARY -> stats.incrementStat(dye, "Legendary Sea Creatures")
            SeaCreature.MYTHIC -> stats.incrementStat(dye, "Mythic Sea Creatures")
        }
    }

    private fun updateDyeProgress(mobType: SeaCreature) {
        val stats = ProfileStorage.lastPlayedProfile() ?: return

        val dropRate = 1.0 / mobType.baseChance * stats.getDyeMultiplier(dye)

        ProfileStorage.lastPlayedProfile()?.dyeData[dye]?.progress += dropRate
        FakeDyeDrop.rollFakeDyeDrop(dye,
            mobType.baseChance,
            dropRate,
            stats.getMagicFind(dye, DyeMultiplier.MAGIC_FIND))
    }

}