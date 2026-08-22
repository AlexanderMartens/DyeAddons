package anlg.dyeaddons.utils.calc

import anlg.dyeaddons.data.Dye
import kotlin.collections.forEach

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

enum class SeaCreature(val fluid: SeaCreatureFluid,
                       val area: SeaCreatureArea,
                       val rarity: SeaCreatureRarity,
                       var weight: Float,
                       val numCreatures: Int = 1) {
    SQUID(SeaCreatureFluid.WATER, SeaCreatureArea.NORMAL_WATER, SeaCreatureRarity.COMMON, 1200F),
    SEA_WALKER(SeaCreatureFluid.WATER, SeaCreatureArea.NORMAL_WATER, SeaCreatureRarity.COMMON, 800F),
    SEA_WITCH(SeaCreatureFluid.WATER, SeaCreatureArea.NORMAL_WATER, SeaCreatureRarity.UNCOMMON, 700F),
    SEA_ARCHER(SeaCreatureFluid.WATER, SeaCreatureArea.NORMAL_WATER, SeaCreatureRarity.UNCOMMON, 550F),
    RIDER_OF_THE_DEEP(SeaCreatureFluid.WATER, SeaCreatureArea.NORMAL_WATER, SeaCreatureRarity.UNCOMMON, 400F),
    CATFISH(SeaCreatureFluid.WATER, SeaCreatureArea.NORMAL_WATER, SeaCreatureRarity.RARE, 250f),
    SEA_LEECH(SeaCreatureFluid.WATER, SeaCreatureArea.NORMAL_WATER, SeaCreatureRarity.RARE, 160f),
    GUARDIAN_DEFENDER(SeaCreatureFluid.WATER, SeaCreatureArea.NORMAL_WATER, SeaCreatureRarity.EPIC, 130f),
    DEEP_SEA_PROTECTOR(SeaCreatureFluid.WATER, SeaCreatureArea.NORMAL_WATER, SeaCreatureRarity.EPIC, 88f),
    WATER_HYDRA(SeaCreatureFluid.WATER, SeaCreatureArea.NORMAL_WATER, SeaCreatureRarity.LEGENDARY, 18f, 2),
    FROG_MAN(SeaCreatureFluid.WATER, SeaCreatureArea.HOTSPOT, SeaCreatureRarity.COMMON, 5000f),
    INKLING(SeaCreatureFluid.WATER, SeaCreatureArea.HOTSPOT, SeaCreatureRarity.UNCOMMON, 2875f),
    SNAPPING_TURTLE(SeaCreatureFluid.WATER, SeaCreatureArea.HOTSPOT, SeaCreatureRarity.RARE, 1500f),
    MANTA_RAY(SeaCreatureFluid.WATER, SeaCreatureArea.HOTSPOT, SeaCreatureRarity.EPIC, 500f),
    BLUE_RINGED_OCTOPUS(SeaCreatureFluid.WATER, SeaCreatureArea.HOTSPOT, SeaCreatureRarity.LEGENDARY, 100f),
    WIKI_TIKI(SeaCreatureFluid.WATER, SeaCreatureArea.HOTSPOT, SeaCreatureRarity.MYTHIC, 25f),
    OASIS_SHEEP(SeaCreatureFluid.WATER, SeaCreatureArea.OASIS, SeaCreatureRarity.UNCOMMON, 700f),
    OASIS_RABBIT(SeaCreatureFluid.WATER, SeaCreatureArea.OASIS, SeaCreatureRarity.UNCOMMON, 300f),
    WATER_WORM(SeaCreatureFluid.WATER, SeaCreatureArea.GOBLIN_HOLDOUT, SeaCreatureRarity.RARE, 300f),
    POISONED_WATER_WORM(SeaCreatureFluid.WATER, SeaCreatureArea.GOBLIN_HOLDOUT, SeaCreatureRarity.RARE, 300f),
    ABYSSAL_MINER(SeaCreatureFluid.WATER, SeaCreatureArea.ABYSSAL_MINER, SeaCreatureRarity.LEGENDARY, 90f),
    MITHRIL_GRUBBER(SeaCreatureFluid.WATER, SeaCreatureArea.ABANDONED_QUARRY, SeaCreatureRarity.UNCOMMON, 385f),
    TRASH_GOBBLER(SeaCreatureFluid.WATER, SeaCreatureArea.BAYOU, SeaCreatureRarity.COMMON, 5000f),
    DUMPSTER_DIVER(SeaCreatureFluid.WATER, SeaCreatureArea.BAYOU, SeaCreatureRarity.UNCOMMON, 2875f),
    BANSHEE(SeaCreatureFluid.WATER, SeaCreatureArea.BAYOU, SeaCreatureRarity.RARE, 1500f),
    BAYOU_SLUDGE(SeaCreatureFluid.WATER, SeaCreatureArea.BAYOU, SeaCreatureRarity.EPIC, 500f),
    ALLIGATOR(SeaCreatureFluid.WATER, SeaCreatureArea.BAYOU, SeaCreatureRarity.LEGENDARY, 100f),
    TITANOBOA(SeaCreatureFluid.WATER, SeaCreatureArea.BAYOU, SeaCreatureRarity.MYTHIC, 25f),
    ATOLL_CROAKER(SeaCreatureFluid.WATER, SeaCreatureArea.LOTUS_ATOLL, SeaCreatureRarity.COMMON, 5000f),
    LOTUS_GUARDIAN(SeaCreatureFluid.WATER, SeaCreatureArea.LOTUS_ATOLL, SeaCreatureRarity.UNCOMMON, 2875f),
    GORF(SeaCreatureFluid.WATER, SeaCreatureArea.LOTUS_ATOLL, SeaCreatureRarity.RARE, 1500f),
    DROWNED_CAPTAIN(SeaCreatureFluid.WATER, SeaCreatureArea.LOTUS_ATOLL, SeaCreatureRarity.EPIC, 500f),
    PUDDLE_JUMPER(SeaCreatureFluid.WATER, SeaCreatureArea.LOTUS_ATOLL, SeaCreatureRarity.LEGENDARY, 100f),
    FROG_PRINCE(SeaCreatureFluid.WATER, SeaCreatureArea.LOTUS_ATOLL, SeaCreatureRarity.MYTHIC, 25f),
    JUMPIN_JACK(SeaCreatureFluid.WATER, SeaCreatureArea.SPOOKY, SeaCreatureRarity.COMMON, 5000f),
    SCARECROW(SeaCreatureFluid.WATER, SeaCreatureArea.SPOOKY, SeaCreatureRarity.UNCOMMON, 2875f),
    NIGHTMARE(SeaCreatureFluid.WATER, SeaCreatureArea.SPOOKY, SeaCreatureRarity.RARE, 1500f),
    WEREWOLF(SeaCreatureFluid.WATER, SeaCreatureArea.SPOOKY, SeaCreatureRarity.EPIC, 500f),
    PHANTOM_FISHER(SeaCreatureFluid.WATER, SeaCreatureArea.SPOOKY, SeaCreatureRarity.LEGENDARY, 100f),
    GRIM_REAPER(SeaCreatureFluid.WATER, SeaCreatureArea.SPOOKY, SeaCreatureRarity.MYTHIC, 25f),
    FROZEN_STEVE(SeaCreatureFluid.WATER, SeaCreatureArea.JERRY, SeaCreatureRarity.COMMON, 5000f),
    FROSTY(SeaCreatureFluid.WATER, SeaCreatureArea.JERRY, SeaCreatureRarity.UNCOMMON, 2875f),
    GRINCH(SeaCreatureFluid.WATER, SeaCreatureArea.JERRY, SeaCreatureRarity.RARE, 1500f),
    NUTCRACKER(SeaCreatureFluid.WATER, SeaCreatureArea.JERRY, SeaCreatureRarity.EPIC, 500f),
    YETI(SeaCreatureFluid.WATER, SeaCreatureArea.JERRY, SeaCreatureRarity.LEGENDARY, 100f),
    REINDRAKE(SeaCreatureFluid.WATER, SeaCreatureArea.JERRY, SeaCreatureRarity.MYTHIC, 25f),
    NURSE_SHARK(SeaCreatureFluid.WATER, SeaCreatureArea.SHARK, SeaCreatureRarity.UNCOMMON, 2875f),
    BLUE_SHARK(SeaCreatureFluid.WATER, SeaCreatureArea.SHARK, SeaCreatureRarity.RARE, 1500f),
    TIGER_SHARK(SeaCreatureFluid.WATER, SeaCreatureArea.SHARK, SeaCreatureRarity.EPIC, 500f),
    GREAT_WHITE_SHARK(SeaCreatureFluid.WATER, SeaCreatureArea.SHARK, SeaCreatureRarity.LEGENDARY, 100f),
    BOGGED(SeaCreatureFluid.WATER, SeaCreatureArea.MOONGLADE_MARSH, SeaCreatureRarity.COMMON, 5000f),
    WETWING(SeaCreatureFluid.WATER, SeaCreatureArea.MOONGLADE_MARSH, SeaCreatureRarity.UNCOMMON, 2875f),
    TADGANG(SeaCreatureFluid.WATER, SeaCreatureArea.MOONGLADE_MARSH, SeaCreatureRarity.RARE, 1500f, 5),
    ENT(SeaCreatureFluid.WATER, SeaCreatureArea.MOONGLADE_MARSH, SeaCreatureRarity.EPIC, 500f),
    LOCH_EMPEROR(SeaCreatureFluid.WATER, SeaCreatureArea.MOONGLADE_MARSH, SeaCreatureRarity.LEGENDARY, 100f),
    NESSE(SeaCreatureFluid.WATER, SeaCreatureArea.MOONGLADE_MARSH, SeaCreatureRarity.MYTHIC, 25f),
    HAGGARD(SeaCreatureFluid.WATER, SeaCreatureArea.TORRHUS_CANYON, SeaCreatureRarity.COMMON, 5000f),
    BRINELING(SeaCreatureFluid.WATER, SeaCreatureArea.TORRHUS_CANYON, SeaCreatureRarity.UNCOMMON, 2875f),
    SPRAWL(SeaCreatureFluid.WATER, SeaCreatureArea.TORRHUS_CANYON, SeaCreatureRarity.RARE, 1500f),
    TORRID(SeaCreatureFluid.WATER, SeaCreatureArea.TORRHUS_CANYON, SeaCreatureRarity.EPIC, 500f),
    SILKBREEZE(SeaCreatureFluid.WATER, SeaCreatureArea.TORRHUS_CANYON, SeaCreatureRarity.LEGENDARY, 100f),
    GIANT_ISOPOD(SeaCreatureFluid.WATER, SeaCreatureArea.TORRHUS_CANYON, SeaCreatureRarity.MYTHIC, 25f),
    CARROT_KING(SeaCreatureFluid.WATER, SeaCreatureArea.CARROT, SeaCreatureRarity.RARE, 1F),
    AGARIMOO(SeaCreatureFluid.WATER, SeaCreatureArea.CHUMCAP, SeaCreatureRarity.RARE, 1F),
    MAGMA_SLUG(SeaCreatureFluid.LAVA, SeaCreatureArea.CRIMSON_ISLE, SeaCreatureRarity.UNCOMMON, 1600f),
    MOOGMA(SeaCreatureFluid.LAVA, SeaCreatureArea.CRIMSON_ISLE, SeaCreatureRarity.UNCOMMON, 1200f),
    LAVA_LEECH(SeaCreatureFluid.LAVA, SeaCreatureArea.CRIMSON_ISLE, SeaCreatureRarity.RARE, 600f),
    PYROCLASTIC_WORM(SeaCreatureFluid.LAVA, SeaCreatureArea.CRIMSON_ISLE, SeaCreatureRarity.RARE, 400f),
    LAVA_FLAME(SeaCreatureFluid.LAVA, SeaCreatureArea.CRIMSON_ISLE, SeaCreatureRarity.RARE, 360f),
    FIRE_EEL(SeaCreatureFluid.LAVA, SeaCreatureArea.CRIMSON_ISLE, SeaCreatureRarity.RARE, 280f),
    TAURUS(SeaCreatureFluid.LAVA, SeaCreatureArea.CRIMSON_ISLE, SeaCreatureRarity.EPIC, 160f),
    THUNDER(SeaCreatureFluid.LAVA, SeaCreatureArea.CRIMSON_ISLE, SeaCreatureRarity.LEGENDARY, 40f),
    LORD_JAWBUS(SeaCreatureFluid.LAVA, SeaCreatureArea.CRIMSON_ISLE, SeaCreatureRarity.MYTHIC, 8f),
    FRIED_CHICKEN(SeaCreatureFluid.LAVA, SeaCreatureArea.HOTSPOT, SeaCreatureRarity.COMMON, 5000f),
    VOLCANIC_SNAIL(SeaCreatureFluid.LAVA, SeaCreatureArea.HOTSPOT, SeaCreatureRarity.UNCOMMON, 2875f),
    FIREPROOF_WITCH(SeaCreatureFluid.LAVA, SeaCreatureArea.HOTSPOT, SeaCreatureRarity.RARE, 1500f),
    MAGMA_PILLAR(SeaCreatureFluid.LAVA, SeaCreatureArea.HOTSPOT, SeaCreatureRarity.EPIC, 500f),
    FIERY_SCUTTLER(SeaCreatureFluid.LAVA, SeaCreatureArea.HOTSPOT, SeaCreatureRarity.LEGENDARY, 100f, 6),
    RAGNAROK(SeaCreatureFluid.LAVA, SeaCreatureArea.HOTSPOT, SeaCreatureRarity.MYTHIC, 25f),
    FLAMING_WORM(SeaCreatureFluid.LAVA, SeaCreatureArea.PRECURSOR_REMNANTS, SeaCreatureRarity.RARE, 180f),
    LAVA_BLAZE(SeaCreatureFluid.LAVA, SeaCreatureArea.MAGMA_FIELDS, SeaCreatureRarity.EPIC, 36f),
    LAVA_PIGMAN(SeaCreatureFluid.LAVA, SeaCreatureArea.MAGMA_FIELDS, SeaCreatureRarity.EPIC, 36f),
    STRIDERSURFER(SeaCreatureFluid.LAVA, SeaCreatureArea.MOONGLADE_MARSH, SeaCreatureRarity.RARE, 200f)
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
    val doubleHookChance: Float) {

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

    fun hoursForDye(dye: Dye, magicFind: Float, looting: Int, bloodshot: Boolean): Float {
        return when(dye) {
            Dye.AQUAMARINE -> {
                1f / creatures.filter { it.fluid == SeaCreatureFluid.WATER && it != SeaCreature.REINDRAKE }
                    .sumOf {
                        (1f / when (it.rarity) {
                            SeaCreatureRarity.COMMON -> 5_000_000f
                            SeaCreatureRarity.UNCOMMON -> 5_000_000f
                            SeaCreatureRarity.RARE -> 2_500_000f
                            SeaCreatureRarity.EPIC -> 2_500_000f
                            SeaCreatureRarity.LEGENDARY -> 50_000f
                            SeaCreatureRarity.MYTHIC -> 50_000f
                        } * (weights[it]!! / totalWeight) *
                                catchesPerHour *
                                (if (it != SeaCreature.PUDDLE_JUMPER) (1f + doubleHookChance / 100f) else 1f) *
                                it.numCreatures *
                                (if (bloodshot && it != SeaCreature.PUDDLE_JUMPER) 1f / (1f - it.numCreatures * 0.025f) else 1f)).toDouble()
                    }.toFloat()
            }
            Dye.CARMINE -> {
                1f / creatures.filter { it.fluid == SeaCreatureFluid.LAVA }
                    .sumOf {
                        (1f / when (it.rarity) {
                            SeaCreatureRarity.COMMON -> 5_000_000f
                            SeaCreatureRarity.UNCOMMON -> 5_000_000f
                            SeaCreatureRarity.RARE -> 2_500_000f
                            SeaCreatureRarity.EPIC -> 2_500_000f
                            SeaCreatureRarity.LEGENDARY -> 50_000f
                            SeaCreatureRarity.MYTHIC -> 50_000f
                        } * (weights[it]!! / totalWeight) *
                                catchesPerHour *
                                (1f + doubleHookChance / 100f) *
                                it.numCreatures *
                                (if (bloodshot) 1f / (1f - it.numCreatures * 0.025f) else 1f)).toDouble()
                    }.toFloat()
            }
            Dye.MIDNIGHT -> {
                1f / creatures.filter { it.area == SeaCreatureArea.SPOOKY }
                    .sumOf {
                        (1f / when (it.rarity) {
                            SeaCreatureRarity.COMMON -> 1_000_000f
                            SeaCreatureRarity.UNCOMMON -> 1_000_000f
                            SeaCreatureRarity.RARE -> 1_000_000f
                            SeaCreatureRarity.EPIC -> 1_000_000f
                            SeaCreatureRarity.LEGENDARY -> 50_000f
                            SeaCreatureRarity.MYTHIC -> 50_000f
                        } * (weights[it]!! / totalWeight) *
                                catchesPerHour *
                                (1f + doubleHookChance / 100f) *
                                it.numCreatures *
                                (if (bloodshot) 1f / (1f - it.numCreatures * 0.025f) else 1f)).toDouble()
                    }.toFloat()
            }
            Dye.ICEBERG -> {
                1f / creatures.filter { it.area == SeaCreatureArea.JERRY && it != SeaCreature.REINDRAKE }
                    .sumOf {
                        (1f / when (it.rarity) {
                            SeaCreatureRarity.COMMON -> 2_500_000f
                            SeaCreatureRarity.UNCOMMON -> 2_500_000f
                            SeaCreatureRarity.RARE -> 500_000f
                            SeaCreatureRarity.EPIC -> 500_000f
                            SeaCreatureRarity.LEGENDARY -> 50_000f
                            SeaCreatureRarity.MYTHIC -> 50_000f
                        } * (weights[it]!! / totalWeight) *
                                catchesPerHour *
                                (1f + doubleHookChance / 100f) *
                                it.numCreatures *
                                (if (bloodshot) 1f / (1f - it.numCreatures * 0.025f) else 1f)).toDouble()
                    }.toFloat()
            }
            else -> 0f
        } / (1f + magicFind / 100f) / (1f + looting * 0.15f)
    }

}