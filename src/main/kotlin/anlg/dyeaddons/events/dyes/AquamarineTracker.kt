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

object AquamarineTracker {

    private val commonMobs = setOf(
        "Squid",
        "Sea Walker",
        "Frog Man",
        "Trash Gobbler",
        "Atoll Croaker",
        "Bogged"
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
        "Wetwing"
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
    )
    private val epicMobs = setOf(
        "Guardian Defender",
        "Deep Sea Protector",
        "Manta Ray",
        "Bayou Sludge",
        "Drowned Captain",
        "Tiger Shark",
        "Ent",
    )
    private val legendaryMobs = setOf(
        "Water Hydra",
        "Blue Ringed Octopus",
        "Abyssal Miner",
        "Alligator",
        "Puddle Jumper",
        "Great White Shark",
        "The Loch Emperor"
    )
    private val mythicMobs = setOf(
        "Wiki Tiki",
        "Titanoboa",
        "Frog Prince",
        "Nesse"
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
        EventBus.subscribe(ChatEvent::class, ::onChat)
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

        DyeAddons.debug("Tracked $mobName Kill, Type: $mobType")
        updateDyeStats(mobType)
        updateDyeProgress(mobType)
    }

    private fun onChat(event: ChatEvent) {
        if (!SkyblockUtils.hypixelMain ||
            !SkyblockUtils.isInSkyblock() ||
            SkyblockUtils.getWorldName() != "Lotus Atoll") return

        if (event.unformattedText.trim() == "A Puddle Jumper is preparing for liftoff—cast your rod into it and hold on tight!") {
            DyeAddons.debug("Tracked Puddle Jumper Kill, Type: LEGENDARY")
            updateDyeStats(SeaCreature.LEGENDARY)
            updateDyeProgress(SeaCreature.LEGENDARY)
        }
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
        val dyeRotation = ConfigManager.data.config.currentDyeRotation
        val multiplier = dyeRotation?.getMultiplier(Dye.AQUAMARINE) ?: 1
        val stats = ProfileStorage.lastPlayedProfile()?.dyeData[Dye.AQUAMARINE]?.statistics ?: return

        val magicFind = stats["Magic Find"]?.asFloat() ?: 0f
        val looting = stats["Looting"]?.asInt() ?: 0

        val dropRate = 1.0 / mobType.baseChance * (1.0 + magicFind / 100.0) * (1.0 + looting * 0.15)

        ProfileStorage.lastPlayedProfile()?.dyeData[Dye.AQUAMARINE]?.progress += dropRate * multiplier
    }

}