package anlg.dyeaddons.utils.calc

import anlg.dyeaddons.data.Dye

enum class SeaCreatureFluid {
    WATER,
    LAVA,
}

enum class SeaCreatureArea {
    NORMAL_WATER,
    CRIMSON_ISLE,
    BAYOU,
    LOTUS_ATOLL,
    JERRY,
    MOONGLADE_MARSH,
    TORRHUS_CANYON,
    OASIS,
    GOBLIN_HOLDOUT,
    JUNGLE,
    PRECURSOR_REMNANTS,
    MITHRIL_DEPOSITS,
    ABANDONED_QUARRY,
    MAGMA_FIELDS,
    HOTSPOT,
    SPOOKY,
    SHARK,
    CHUMCAP,
    CARROT,
    ABYSSAL_MINER,
}

enum class SeaCreatureRarity {
    COMMON,
    UNCOMMON,
    RARE,
    EPIC,
    LEGENDARY,
    MYTHIC,
}

enum class FishingRodHook {
    HOTSPOT,
    SPOOKY,
    COMMON,
    PUDDLE_JUMPER,
}

enum class FishingBait {
    WHALE,
    HOTSPOT,
    SPOOKY,
    WORM,
    CARROT,
    SHARK,
}

enum class FishingPet {
    MEGALODON,
    HERMIT_CRAB,
    BAT,
}

enum class SeaCreature(val mobName: String,
                       val catchMessage: String,
                       val fluid: SeaCreatureFluid,
                       val area: SeaCreatureArea,
                       val rarity: SeaCreatureRarity,
                       var weight: Float,
                       val numCreatures: Int = 1) {
    SQUID("Squid", "A Squid appeared.", SeaCreatureFluid.WATER, SeaCreatureArea.NORMAL_WATER, SeaCreatureRarity.COMMON, 1200F),
    SEA_WALKER("Sea Walker", "You caught a Sea Walker.", SeaCreatureFluid.WATER, SeaCreatureArea.NORMAL_WATER, SeaCreatureRarity.COMMON, 800F),
    SEA_WITCH("Sea Witch", "It looks like you've disrupted the Sea Witch's brewing session. Watch out, she's furious!", SeaCreatureFluid.WATER, SeaCreatureArea.NORMAL_WATER, SeaCreatureRarity.UNCOMMON, 700F),
    SEA_ARCHER("Sea Archer", "You reeled in a Sea Archer.", SeaCreatureFluid.WATER, SeaCreatureArea.NORMAL_WATER, SeaCreatureRarity.UNCOMMON, 550F),
    RIDER_OF_THE_DEEP("Rider of the Deep", "The Rider of the Deep has emerged.", SeaCreatureFluid.WATER, SeaCreatureArea.NORMAL_WATER, SeaCreatureRarity.UNCOMMON, 400F),
    CATFISH("Catfish", "Huh? A Catfish!", SeaCreatureFluid.WATER, SeaCreatureArea.NORMAL_WATER, SeaCreatureRarity.RARE, 250f),
    SEA_LEECH("Sea Leech", "Gross! A Sea Leech!", SeaCreatureFluid.WATER, SeaCreatureArea.NORMAL_WATER, SeaCreatureRarity.RARE, 160f),
    GUARDIAN_DEFENDER("Guardian Defender", "You've discovered a Guardian Defender of the sea.", SeaCreatureFluid.WATER, SeaCreatureArea.NORMAL_WATER, SeaCreatureRarity.EPIC, 130f),
    DEEP_SEA_PROTECTOR("Deep Sea Protector", "You have awoken the Deep Sea Protector, prepare for a battle!", SeaCreatureFluid.WATER, SeaCreatureArea.NORMAL_WATER, SeaCreatureRarity.EPIC, 88f),
    WATER_HYDRA("Water Hydra", "The Water Hydra has come to test your strength.", SeaCreatureFluid.WATER, SeaCreatureArea.NORMAL_WATER, SeaCreatureRarity.LEGENDARY, 18f, 2),
    FROG_MAN("Frog Man", "Is it a frog? Is it a man? Well, yes, sorta, IT'S FROG MAN!!!!!!", SeaCreatureFluid.WATER, SeaCreatureArea.HOTSPOT, SeaCreatureRarity.COMMON, 5000f),
    INKLING("Inkling", "You get an inkling that you've caught... an Inkling!", SeaCreatureFluid.WATER, SeaCreatureArea.HOTSPOT, SeaCreatureRarity.UNCOMMON, 2875f),
    SNAPPING_TURTLE("Snapping Turtle", "A Snapping Turtle is coming your way, and it's ANGRY!", SeaCreatureFluid.WATER, SeaCreatureArea.HOTSPOT, SeaCreatureRarity.RARE, 1500f),
    MANTA_RAY("Manta Ray", "A majestic creature rises from the water. It's a Manta Ray.", SeaCreatureFluid.WATER, SeaCreatureArea.HOTSPOT, SeaCreatureRarity.EPIC, 500f),
    BLUE_RINGED_OCTOPUS("Blue Ringed Octopus", "A garish set of tentacles arise. It's a Blue Ringed Octopus!", SeaCreatureFluid.WATER, SeaCreatureArea.HOTSPOT, SeaCreatureRarity.LEGENDARY, 100f),
    WIKI_TIKI("Wiki Tiki", "The water bubbles and froths. A massive form emerges- you have disturbed the Wiki Tiki! You shall pay the price.", SeaCreatureFluid.WATER, SeaCreatureArea.HOTSPOT, SeaCreatureRarity.MYTHIC, 25f),
    OASIS_SHEEP("Oasis Sheep", "An Oasis Sheep appears from the water.", SeaCreatureFluid.WATER, SeaCreatureArea.OASIS, SeaCreatureRarity.UNCOMMON, 700f),
    OASIS_RABBIT("Oasis Rabbit", "An Oasis Rabbit appears from the water.", SeaCreatureFluid.WATER, SeaCreatureArea.OASIS, SeaCreatureRarity.UNCOMMON, 300f),
    WATER_WORM("Water Worm", "A Water Worm surfaces!", SeaCreatureFluid.WATER, SeaCreatureArea.GOBLIN_HOLDOUT, SeaCreatureRarity.RARE, 300f),
    POISONED_WATER_WORM("Poisoned Water Worm", "A Poisoned Water Worm surfaces!", SeaCreatureFluid.WATER, SeaCreatureArea.GOBLIN_HOLDOUT, SeaCreatureRarity.RARE, 300f),
    ABYSSAL_MINER("Abyssal Miner", "An Abyssal Miner breaks out of the water!", SeaCreatureFluid.WATER, SeaCreatureArea.ABYSSAL_MINER, SeaCreatureRarity.LEGENDARY, 90f),
    MITHRIL_GRUBBER("Mithril Grubber", "A leech of the mines surfaces... you've caught a Mithril Grubber.", SeaCreatureFluid.WATER, SeaCreatureArea.ABANDONED_QUARRY, SeaCreatureRarity.UNCOMMON, 385f),
    TRASH_GOBBLER("Trash Gobbler", "The Trash Gobbler is hungry for you!", SeaCreatureFluid.WATER, SeaCreatureArea.BAYOU, SeaCreatureRarity.COMMON, 5000f),
    DUMPSTER_DIVER("Dumpster Diver", "A Dumpster Diver has emerged from the swamp!", SeaCreatureFluid.WATER, SeaCreatureArea.BAYOU, SeaCreatureRarity.UNCOMMON, 2875f),
    BANSHEE("Banshee", "The desolate wail of a Banshee breaks the silence.", SeaCreatureFluid.WATER, SeaCreatureArea.BAYOU, SeaCreatureRarity.RARE, 1500f),
    BAYOU_SLUDGE("Bayou Sludge", "A swampy mass of slime emerges, the Bayou Sludge!", SeaCreatureFluid.WATER, SeaCreatureArea.BAYOU, SeaCreatureRarity.EPIC, 500f),
    ALLIGATOR("Alligator", "A long snout breaks the surface of the water. It's an Alligator!", SeaCreatureFluid.WATER, SeaCreatureArea.BAYOU, SeaCreatureRarity.LEGENDARY, 100f),
    TITANOBOA("Titanoboa", "A massive Titanoboa surfaces. Its body stretches as far as the eye can see.", SeaCreatureFluid.WATER, SeaCreatureArea.BAYOU, SeaCreatureRarity.MYTHIC, 25f),
    ATOLL_CROAKER("Atoll Croaker", "An inquisitive Atoll Croaker takes the bait!", SeaCreatureFluid.WATER, SeaCreatureArea.LOTUS_ATOLL, SeaCreatureRarity.COMMON, 5000f),
    LOTUS_GUARDIAN("Lotus Guardian", "A Lotus Guardian emerges, ready to protect the Atoll.", SeaCreatureFluid.WATER, SeaCreatureArea.LOTUS_ATOLL, SeaCreatureRarity.UNCOMMON, 2875f),
    GORF("gorF", "What even is that?! A... gorF?", SeaCreatureFluid.WATER, SeaCreatureArea.LOTUS_ATOLL, SeaCreatureRarity.RARE, 1500f),
    DROWNED_CAPTAIN("Drowned Captain", "A Drowned Captain takes hold of your bobber!", SeaCreatureFluid.WATER, SeaCreatureArea.LOTUS_ATOLL, SeaCreatureRarity.EPIC, 500f),
    PUDDLE_JUMPER("Puddle Jumper", "A Puddle Jumper is preparing for liftoff—cast your rod into it and hold on tight!", SeaCreatureFluid.WATER, SeaCreatureArea.LOTUS_ATOLL, SeaCreatureRarity.LEGENDARY, 100f),
    FROG_PRINCE("Frog Prince", "Bow down before the Frog Prince... or pay the hefty price!", SeaCreatureFluid.WATER, SeaCreatureArea.LOTUS_ATOLL, SeaCreatureRarity.MYTHIC, 25f),
    JUMPIN_JACK("Jumpin' Jack", "Watch out! It's Jumpin' Jack.", SeaCreatureFluid.WATER, SeaCreatureArea.SPOOKY, SeaCreatureRarity.COMMON, 5000f),
    SCARECROW("Scarecrow", "Phew! It's only a Scarecrow.", SeaCreatureFluid.WATER, SeaCreatureArea.SPOOKY, SeaCreatureRarity.UNCOMMON, 2875f),
    NIGHTMARE("Nightmare", "You hear trotting from beneath the waves, you caught a Nightmare.", SeaCreatureFluid.WATER, SeaCreatureArea.SPOOKY, SeaCreatureRarity.RARE, 1500f),
    WEREWOLF("Werewolf", "It must be a full moon, a Werewolf appears.", SeaCreatureFluid.WATER, SeaCreatureArea.SPOOKY, SeaCreatureRarity.EPIC, 500f),
    PHANTOM_FISHER("Phantom Fisher", "The spirit of a long lost Phantom Fisher has come to haunt you.", SeaCreatureFluid.WATER, SeaCreatureArea.SPOOKY, SeaCreatureRarity.LEGENDARY, 100f),
    GRIM_REAPER("Grim Reaper", "This can't be! The manifestation of death himself!", SeaCreatureFluid.WATER, SeaCreatureArea.SPOOKY, SeaCreatureRarity.MYTHIC, 25f),
    FROZEN_STEVE("Frozen Steve", "Frozen Steve fell into the pond long ago, never to resurface...until now!", SeaCreatureFluid.WATER, SeaCreatureArea.JERRY, SeaCreatureRarity.COMMON, 5000f),
    FROSTY("Frosty", "It's a snowman! He looks harmless.", SeaCreatureFluid.WATER, SeaCreatureArea.JERRY, SeaCreatureRarity.UNCOMMON, 2875f),
    GRINCH("Grinch", "The Grinch stole Jerry's gifts... Get them back!", SeaCreatureFluid.WATER, SeaCreatureArea.JERRY, SeaCreatureRarity.RARE, 1500f),
    NUTCRACKER("Nutcracker", "You found a forgotten Nutcracker laying beneath the ice.", SeaCreatureFluid.WATER, SeaCreatureArea.JERRY, SeaCreatureRarity.EPIC, 500f),
    YETI("Yeti", "What is this creature!?", SeaCreatureFluid.WATER, SeaCreatureArea.JERRY, SeaCreatureRarity.LEGENDARY, 100f),
    REINDRAKE("Reindrake", "A Reindrake forms from the depths.", SeaCreatureFluid.WATER, SeaCreatureArea.JERRY, SeaCreatureRarity.MYTHIC, 25f),
    NURSE_SHARK("Nurse Shark", "A tiny fin emerges from the water, you've caught a Nurse Shark.", SeaCreatureFluid.WATER, SeaCreatureArea.SHARK, SeaCreatureRarity.UNCOMMON, 2875f),
    BLUE_SHARK("Blue Shark", "You spot a fin as blue as the water it came from, it's a Blue Shark.", SeaCreatureFluid.WATER, SeaCreatureArea.SHARK, SeaCreatureRarity.RARE, 1500f),
    TIGER_SHARK("Tiger Shark", "A striped beast bounds from the depths, the wild Tiger Shark!", SeaCreatureFluid.WATER, SeaCreatureArea.SHARK, SeaCreatureRarity.EPIC, 500f),
    GREAT_WHITE_SHARK("Great White Shark", "Hide no longer, a Great White Shark has tracked your scent and thirsts for your blood!", SeaCreatureFluid.WATER, SeaCreatureArea.SHARK, SeaCreatureRarity.LEGENDARY, 100f),
    BOGGED("Bogged", "You've hooked a Bogged!", SeaCreatureFluid.WATER, SeaCreatureArea.MOONGLADE_MARSH, SeaCreatureRarity.COMMON, 5000f),
    WETWING("Wetwing", "Look! A Wetwing emerges!", SeaCreatureFluid.WATER, SeaCreatureArea.MOONGLADE_MARSH, SeaCreatureRarity.UNCOMMON, 2875f),
    TADGANG("Tadgang", "A gang of Liltads!", SeaCreatureFluid.WATER, SeaCreatureArea.MOONGLADE_MARSH, SeaCreatureRarity.RARE, 1500f),
    ENT("Ent", "You've hooked an Ent, as ancient as the forest itself.", SeaCreatureFluid.WATER, SeaCreatureArea.MOONGLADE_MARSH, SeaCreatureRarity.EPIC, 500f),
    LOCH_EMPEROR("Loch Emperor", "The Loch Emperor arises from the depths.", SeaCreatureFluid.WATER, SeaCreatureArea.MOONGLADE_MARSH, SeaCreatureRarity.LEGENDARY, 100f),
    NESSIE("Nessie", "You've caused a disturbance in the loch. Could it be... Nessie?", SeaCreatureFluid.WATER, SeaCreatureArea.MOONGLADE_MARSH, SeaCreatureRarity.MYTHIC, 25f),
    HAGGARD("Haggard", "A Haggard stumbles to the shore, ready for a fight!", SeaCreatureFluid.WATER, SeaCreatureArea.TORRHUS_CANYON, SeaCreatureRarity.COMMON, 5000f),
    BRINELING("Brineling", "A Brineling interrupts you with a stream of bubbles!", SeaCreatureFluid.WATER, SeaCreatureArea.TORRHUS_CANYON, SeaCreatureRarity.UNCOMMON, 2875f),
    SPRAWL("Sprawl", "A Sprawl emerges from the blue, and it’s looking for you!", SeaCreatureFluid.WATER, SeaCreatureArea.TORRHUS_CANYON, SeaCreatureRarity.RARE, 1500f),
    TORRID("Torrid", "The laughter of a Torrid echoes through the air.", SeaCreatureFluid.WATER, SeaCreatureArea.TORRHUS_CANYON, SeaCreatureRarity.EPIC, 500f),
    SILKBREEZE("Silkbreeze", "Something zips through the air - it’s a Silkbreeze!", SeaCreatureFluid.WATER, SeaCreatureArea.TORRHUS_CANYON, SeaCreatureRarity.LEGENDARY, 100f),
    GIANT_ISOPOD("Giant Isopod", "A Giant Isopod was dredged up from the depths!", SeaCreatureFluid.WATER, SeaCreatureArea.TORRHUS_CANYON, SeaCreatureRarity.MYTHIC, 25f),
    CARROT_KING("Carrot King", "Is this even a fish? It's the Carrot King!", SeaCreatureFluid.WATER, SeaCreatureArea.CARROT, SeaCreatureRarity.RARE, 1F),
    AGARIMOO("Agarimoo", "Your Chumcap Bucket trembles, it's an Agarimoo.", SeaCreatureFluid.WATER, SeaCreatureArea.CHUMCAP, SeaCreatureRarity.RARE, 1F),
    MAGMA_SLUG("Magma Slug", "From beneath the lava appears a Magma Slug.", SeaCreatureFluid.LAVA, SeaCreatureArea.CRIMSON_ISLE, SeaCreatureRarity.UNCOMMON, 1600f),
    MOOGMA("Moogma", "You hear a faint Moo from the lava... A Moogma appears.", SeaCreatureFluid.LAVA, SeaCreatureArea.CRIMSON_ISLE, SeaCreatureRarity.UNCOMMON, 1200f),
    LAVA_LEECH("Lava Leech", "A small but fearsome Lava Leech emerges.", SeaCreatureFluid.LAVA, SeaCreatureArea.CRIMSON_ISLE, SeaCreatureRarity.RARE, 600f),
    PYROCLASTIC_WORM("Pyroclastic Worm", "You feel the heat radiating as a Pyroclastic Worm surfaces.", SeaCreatureFluid.LAVA, SeaCreatureArea.CRIMSON_ISLE, SeaCreatureRarity.RARE, 400f),
    LAVA_FLAME("Lava Flame", "A Lava Flame flies out from beneath the lava.", SeaCreatureFluid.LAVA, SeaCreatureArea.CRIMSON_ISLE, SeaCreatureRarity.RARE, 360f),
    FIRE_EEL("Fire Eel", "A Fire Eel slithers out from the depths.", SeaCreatureFluid.LAVA, SeaCreatureArea.CRIMSON_ISLE, SeaCreatureRarity.RARE, 280f),
    TAURUS("Taurus", "Taurus and his steed emerge.", SeaCreatureFluid.LAVA, SeaCreatureArea.CRIMSON_ISLE, SeaCreatureRarity.EPIC, 160f),
    THUNDER("Thunder", "You hear a massive rumble as Thunder emerges.", SeaCreatureFluid.LAVA, SeaCreatureArea.CRIMSON_ISLE, SeaCreatureRarity.LEGENDARY, 40f),
    LORD_JAWBUS("Lord Jawbus", "You have angered a legendary creature... Lord Jawbus has arrived.", SeaCreatureFluid.LAVA, SeaCreatureArea.CRIMSON_ISLE, SeaCreatureRarity.MYTHIC, 8f),
    FRIED_CHICKEN("Fried Chicken", "Smells of burning. Must be a Fried Chicken.", SeaCreatureFluid.LAVA, SeaCreatureArea.HOTSPOT, SeaCreatureRarity.COMMON, 5000f),
    VOLCANIC_SNAIL("Volcanic Snail", "You feel a burning sensation as you reel in a Volcanic Snail!", SeaCreatureFluid.LAVA, SeaCreatureArea.HOTSPOT, SeaCreatureRarity.UNCOMMON, 2875f),
    FIREPROOF_WITCH("Fireproof Witch", "Trouble's brewing, it's a Fireproof Witch!", SeaCreatureFluid.LAVA, SeaCreatureArea.HOTSPOT, SeaCreatureRarity.RARE, 1500f),
    MAGMA_PILLAR("Magma Pillar", "A Magma Pillar rises from the lava.", SeaCreatureFluid.LAVA, SeaCreatureArea.HOTSPOT, SeaCreatureRarity.EPIC, 500f),
    FIERY_SCUTTLER("Fiery Scuttler", "A Fiery Scuttler inconspicuously waddles up to you, friends in tow.", SeaCreatureFluid.LAVA, SeaCreatureArea.HOTSPOT, SeaCreatureRarity.LEGENDARY, 100f, 6),
    RAGNAROK("Ragnarok", "The sky darkens and the air thickens. The end times are upon us: Ragnarok is here.", SeaCreatureFluid.LAVA, SeaCreatureArea.HOTSPOT, SeaCreatureRarity.MYTHIC, 25f),
    FLAMING_WORM("Flaming Worm", "A Flaming Worm surfaces from the depths!", SeaCreatureFluid.LAVA, SeaCreatureArea.PRECURSOR_REMNANTS, SeaCreatureRarity.RARE, 180f),
    LAVA_BLAZE("Lava Blaze", "A Lava Blaze has surfaced from the depths!", SeaCreatureFluid.LAVA, SeaCreatureArea.MAGMA_FIELDS, SeaCreatureRarity.EPIC, 36f),
    LAVA_PIGMAN("Lava Pigman", "A Lava Pigman arose from the depths!", SeaCreatureFluid.LAVA, SeaCreatureArea.MAGMA_FIELDS, SeaCreatureRarity.EPIC, 36f),
    STRIDERSURFER("Stridersurfer", "You caught a Stridersurfer.", SeaCreatureFluid.LAVA, SeaCreatureArea.MOONGLADE_MARSH, SeaCreatureRarity.RARE, 200f)
}

class SeaCreatureTable (
    val fluid: SeaCreatureFluid,
    val area: SeaCreatureArea,
    val event: List<SeaCreatureArea>, // Hotspot, Spooky, Shark
    val hook: FishingRodHook?,
    val bait: FishingBait?,
    val pet: FishingPet?,
    val tracking: Float,
    carnivalPerk: Boolean,
    val piperPerk: Boolean,
    val squidHat: Boolean,
    hotspotTonic: Boolean,
    sharkScaleArmor: Int, // Number of pieces of shark scale armor 0-4
    val chumcapBucket: Boolean,
    val catchesPerHour: Float,
    val doubleHookChance: Float,
    val partySize: Int, // Size of party to loot share, 1 = solo
    val sharingRarity: SeaCreatureRarity? // Minimum sea creature rarity to share, null = no sharing
) {

    private val creatures = SeaCreature.entries
        .filter { it.fluid == fluid }
        .filter { when (it.area) {
            SeaCreatureArea.NORMAL_WATER -> area in listOf(
                SeaCreatureArea.NORMAL_WATER,
                SeaCreatureArea.OASIS,
                SeaCreatureArea.ABANDONED_QUARRY,
                SeaCreatureArea.GOBLIN_HOLDOUT,
                SeaCreatureArea.PRECURSOR_REMNANTS,
                SeaCreatureArea.MITHRIL_DEPOSITS,
                SeaCreatureArea.JUNGLE)
            in listOf(SeaCreatureArea.HOTSPOT, SeaCreatureArea.SPOOKY, SeaCreatureArea.SHARK) -> it.area in event
            SeaCreatureArea.CHUMCAP -> chumcapBucket
            SeaCreatureArea.CARROT -> bait == FishingBait.CARROT
            SeaCreatureArea.ABYSSAL_MINER -> area in listOf(SeaCreatureArea.GOBLIN_HOLDOUT, SeaCreatureArea.JUNGLE, SeaCreatureArea.MITHRIL_DEPOSITS)
            else -> it.area == area
        } }
        .filter { (bait != FishingBait.WORM ) || (it in listOf(SeaCreature.POISONED_WATER_WORM, SeaCreature.WATER_WORM, SeaCreature.FLAMING_WORM)) }

    private val hotspotChance = if (SeaCreatureArea.HOTSPOT in event) {
        0.15f + (if (hotspotTonic) 0.1f else 0f) +
                (if (pet == FishingPet.HERMIT_CRAB) 0.2f else 0f) +
                (if (bait == FishingBait.HOTSPOT) 0.2f else 0f) +
                (if (hook == FishingRodHook.HOTSPOT) 0.2f else 0f)
    } else 0f

    private val spookyChance = if (SeaCreatureArea.SPOOKY in event) {
        0.15f + (if (carnivalPerk) 0.05f else 0f) +
                (if (pet == FishingPet.BAT) 0.2f else 0f) +
                (if (bait == FishingBait.SPOOKY) 0.2f else 0f) +
                (if (hook == FishingRodHook.SPOOKY) 0.2f else 0f)
    } else 0f

    private val sharkChance = if (SeaCreatureArea.SHARK in event) {
        0.15f + (if (carnivalPerk) 0.05f else 0f) +
                (if (pet == FishingPet.MEGALODON) 0.2f else 0f) +
                (if (bait == FishingBait.SHARK) 0.2f else 0f) +
                (sharkScaleArmor * 0.05f)
    } else 0f

    private val agarimooChance = if (chumcapBucket) 0.15f else 0f

    private val carrotKingChance = if (bait == FishingBait.CARROT) 0.05f else 0f

    private val weights = creatures.associateWith { it.weight }.toMutableMap()

    private val baseTotalWeight: Float get() = weights.filterKeys { it.area !in listOf(
        SeaCreatureArea.HOTSPOT,
        SeaCreatureArea.SPOOKY,
        SeaCreatureArea.SHARK,
        SeaCreatureArea.CHUMCAP,
        SeaCreatureArea.CARROT,)
    }.values.sum()
    private val totalWeight: Float get() = weights.values.sum()

    init {
        // Apply normal modifiers to this table's local weights
        creatures.forEach { creature ->
            weights[creature] = creature.weight *
                    (if (creature.weight <= 400f && bait == FishingBait.WHALE) 1.25f else 1f) *
                    (if (creature.rarity >= SeaCreatureRarity.LEGENDARY) (1f + tracking / 100f) else 1f) *
                    (if (hook == FishingRodHook.COMMON && creature.rarity == SeaCreatureRarity.COMMON) 1.25f else 1f) *
                    (if (hook == FishingRodHook.PUDDLE_JUMPER && creature == SeaCreature.PUDDLE_JUMPER) 5f else 1f) *
                    (if (piperPerk && creature == SeaCreature.REINDRAKE) 1.1f else 1f) *
                    (if (squidHat && creature in listOf(SeaCreature.SQUID, SeaCreature.INKLING)) 2f else 1f)
        }

        // Scale event pools
        val baseWeight = baseTotalWeight

        val hotspotMultiplier = eventWeightMultiplier(SeaCreatureArea.HOTSPOT, hotspotChance, baseWeight)
        val spookyMultiplier = eventWeightMultiplier(SeaCreatureArea.SPOOKY, spookyChance, baseWeight)
        val sharkMultiplier = eventWeightMultiplier(SeaCreatureArea.SHARK, sharkChance, baseWeight)
        val chumCapMultiplier = eventWeightMultiplier(SeaCreatureArea.CHUMCAP, agarimooChance, baseWeight)
        val carrotMultiplier = eventWeightMultiplier(SeaCreatureArea.CARROT, carrotKingChance, baseWeight)

        creatures.forEach { creature ->
            weights[creature] = weights[creature]!! * when (creature.area) {
                SeaCreatureArea.HOTSPOT -> hotspotMultiplier
                SeaCreatureArea.SPOOKY -> spookyMultiplier
                SeaCreatureArea.SHARK -> sharkMultiplier
                SeaCreatureArea.CHUMCAP -> chumCapMultiplier
                SeaCreatureArea.CARROT -> carrotMultiplier
                else -> 1f
            }
        }
    }

    private fun eventWeightMultiplier(
        eventArea: SeaCreatureArea,
        chance: Float,
        baseWeight: Float
    ): Float {
        if (chance <= 0f) return 0f

        val currentEventWeight = weights.filterKeys { it.area == eventArea }.values.sum()

        if (currentEventWeight <= 0f) return 0f

        val desiredEventWeight = baseWeight * chance / (1f - chance)

        return desiredEventWeight / currentEventWeight
    }

    fun hoursForDye(dye: Dye, magicFind: Float, bloodshot: Boolean): Float {
        return when(dye) {
            Dye.AQUAMARINE -> {
                1f / creatures.filter { it.fluid == SeaCreatureFluid.WATER && it != SeaCreature.REINDRAKE }
                    .sumOf {
                        (1f / when (it.rarity) {
                            SeaCreatureRarity.COMMON -> Dye.AQUAMARINE.getDropRate("Common")
                            SeaCreatureRarity.UNCOMMON -> Dye.AQUAMARINE.getDropRate("Uncommon")
                            SeaCreatureRarity.RARE -> Dye.AQUAMARINE.getDropRate("Rare")
                            SeaCreatureRarity.EPIC -> Dye.AQUAMARINE.getDropRate("Epic")
                            SeaCreatureRarity.LEGENDARY -> Dye.AQUAMARINE.getDropRate("Legendary")
                            SeaCreatureRarity.MYTHIC -> Dye.AQUAMARINE.getDropRate("Mythic")
                        } * (weights[it]!! / totalWeight) *
                                catchesPerHour *
                                (if (it != SeaCreature.PUDDLE_JUMPER) (1f + doubleHookChance / 100f) else 1f) *
                                it.numCreatures *
                                (if (bloodshot && it != SeaCreature.PUDDLE_JUMPER) 1f / (1f - it.numCreatures * 0.025f) else 1f) *
                                if (sharingRarity != null && it.rarity >= sharingRarity) {
                                    (1f + (partySize - 1) / 5f)
                                } else {
                                    1f
                                }
                        )
                    }.toFloat()
            }
            Dye.CARMINE -> {
                1f / creatures.filter { it.fluid == SeaCreatureFluid.LAVA }
                    .sumOf {
                        (1f / when (it.rarity) {
                            SeaCreatureRarity.COMMON -> Dye.CARMINE.getDropRate("Common")
                            SeaCreatureRarity.UNCOMMON -> Dye.CARMINE.getDropRate("Uncommon")
                            SeaCreatureRarity.RARE -> Dye.CARMINE.getDropRate("Rare")
                            SeaCreatureRarity.EPIC -> Dye.CARMINE.getDropRate("Epic")
                            SeaCreatureRarity.LEGENDARY -> Dye.CARMINE.getDropRate("Legendary")
                            SeaCreatureRarity.MYTHIC -> Dye.CARMINE.getDropRate("Mythic")
                        } * (weights[it]!! / totalWeight) *
                                catchesPerHour *
                                (1f + doubleHookChance / 100f) *
                                it.numCreatures *
                                (if (bloodshot) 1f / (1f - it.numCreatures * 0.025f) else 1f) *
                                if (sharingRarity != null && it.rarity >= sharingRarity) {
                                    (1f + (partySize - 1) / 5f)
                                } else {
                                    1f
                                }
                        )
                    }.toFloat()
            }
            Dye.MIDNIGHT -> {
                1f / creatures.filter { it.area == SeaCreatureArea.SPOOKY }
                    .sumOf {
                        (1f / when (it.rarity) {
                            SeaCreatureRarity.COMMON -> Dye.MIDNIGHT.getDropRate("Common")
                            SeaCreatureRarity.UNCOMMON -> Dye.MIDNIGHT.getDropRate("Uncommon")
                            SeaCreatureRarity.RARE -> Dye.MIDNIGHT.getDropRate("Rare")
                            SeaCreatureRarity.EPIC -> Dye.MIDNIGHT.getDropRate("Epic")
                            SeaCreatureRarity.LEGENDARY -> Dye.MIDNIGHT.getDropRate("Legendary")
                            SeaCreatureRarity.MYTHIC -> Dye.MIDNIGHT.getDropRate("Mythic")
                        } * (weights[it]!! / totalWeight) *
                                catchesPerHour *
                                (1f + doubleHookChance / 100f) *
                                it.numCreatures *
                                (if (bloodshot) 1f / (1f - it.numCreatures * 0.025f) else 1f) *
                                if (sharingRarity != null && it.rarity >= sharingRarity) {
                                    (1f + (partySize - 1) / 5f)
                                } else {
                                    1f
                                }
                        )
                    }.toFloat()
            }
            Dye.ICEBERG -> {
                1f / creatures.filter { it.area == SeaCreatureArea.JERRY }
                    .sumOf {
                        (1f / when (it.rarity) {
                            SeaCreatureRarity.COMMON -> Dye.ICEBERG.getDropRate("Common")
                            SeaCreatureRarity.UNCOMMON -> Dye.ICEBERG.getDropRate("Uncommon")
                            SeaCreatureRarity.RARE -> Dye.ICEBERG.getDropRate("Rare")
                            SeaCreatureRarity.EPIC -> Dye.ICEBERG.getDropRate("Epic")
                            SeaCreatureRarity.LEGENDARY -> Dye.ICEBERG.getDropRate("Legendary")
                            SeaCreatureRarity.MYTHIC -> Dye.ICEBERG.getDropRate("Mythic")
                        } * (weights[it]!! / totalWeight) *
                                catchesPerHour *
                                (1f + doubleHookChance / 100f) *
                                it.numCreatures *
                                (if (bloodshot) 1f / (1f - it.numCreatures * 0.025f) else 1f) *
                                if (sharingRarity != null && it.rarity >= sharingRarity) {
                                    (1f + (partySize - 1) / 5f)
                                } else {
                                    1f
                                }
                        )
                    }.toFloat()
            }
            else -> 0f
        } / (1f + magicFind / 100f)
    }

}