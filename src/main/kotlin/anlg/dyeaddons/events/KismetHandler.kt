package anlg.dyeaddons.events

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.config.DyeMultiplier
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.events.models.*
import anlg.dyeaddons.features.dye.FakeDyeDrop
import anlg.dyeaddons.settings.categories.DebugCategories
import anlg.dyeaddons.utils.InventoryUtils.findMatchInLore
import anlg.dyeaddons.utils.ScoreboardUtils
import anlg.dyeaddons.utils.SkyblockUtils

object KismetHandler {

    private val INSTANCE_TYPE_PATTERN = Regex("""((?:Master )?Catacombs|Kuudra) - (?:Floor )?(\w+)""")
    private val BEDROCK_PATTERN = Regex("""(?:BEDROCK|PAID) CHEST REWARDS""")

    private var instanceType : InstanceType? = null

    fun init() {
        EventBus.subscribe(SlotClickEvent::class, ::onSlotClick)
        EventBus.subscribe(ChatEvent::class, ::onChat)
    }

    private fun onSlotClick(event: SlotClickEvent) {
        if (!SkyblockUtils.onHypixel ||
            !SkyblockUtils.isInSkyblock()) return

        val title = event.container.title.string.trim()

        if (INSTANCE_TYPE_PATTERN.matches(title)) {
            val match = INSTANCE_TYPE_PATTERN.matchEntire(title) ?: return
            val instance = match.groupValues[1]
            val tier = match.groupValues[2]

            checkInstanceType(instance, tier)
            DyeAddons.debug("Set instance type: $instanceType", DebugCategories.DYE_PROGRESS_EVENT)
            return
        }

        if (event.item != null && event.item.hoverName.string.trim() == "Reroll Chest") {
            if (event.item.findMatchInLore(Regex("""Click to reroll this chest!""")) == null) return
            val chestType = when (title) {
                "Wood" -> ChestType.WOOD
                "Gold" -> ChestType.GOLD
                "Diamond" -> ChestType.DIAMOND
                "Emerald" -> ChestType.EMERALD
                "Obsidian" -> ChestType.OBSIDIAN
                "Bedrock" -> ChestType.BEDROCK
                "Paid Chest" -> ChestType.PAID
                else -> {DyeAddons.debug("Could not determine chest type", DebugCategories.ERROR); return}
            }
            checkInstanceTypeWithScoreboard()
            if (instanceType == null) {DyeAddons.debug("Could not determine instance type", DebugCategories.ERROR); return}
            instanceType?.let {
                DyeAddons.debug("Kismet used on $chestType chest on $instanceType", DebugCategories.DYE_PROGRESS_EVENT)
                EventBus.publish(KismetUsedEvent(instanceType!!, chestType))
            }
        }
    }

    private fun onChat(event: ChatEvent) {
        if (!SkyblockUtils.hypixelMain ||
            !SkyblockUtils.isInSkyblock()) return

        if (!BEDROCK_PATTERN.matches(event.unformattedText.trim())) return

        checkInstanceTypeWithScoreboard()

        val stats = ProfileStorage.lastPlayedProfile() ?: return

        when (instanceType) {
            InstanceType.MASTER_CATACOMBS_FLOOR_V -> {
                val dropRate = (1.0 / 5_000.0) * stats.getDyeMultiplier(
                    Dye.LIVID,
                    DyeMultiplier.METER,
                    DyeMultiplier.VINCENT,
                    DyeMultiplier.BUCKET_OF_DYE,
                    DyeMultiplier.MIRACLE_CHANCE)

                FakeDyeDrop.rollFakeDyeDrop(
                    Dye.LIVID,
                    5_000.0,
                    dropRate
                )
            }
            InstanceType.MASTER_CATACOMBS_FLOOR_VII -> {
                val dropRate = (1.0 / 2_500.0) * stats.getDyeMultiplier(
                    Dye.NECRON,
                    DyeMultiplier.METER,
                    DyeMultiplier.VINCENT,
                    DyeMultiplier.BUCKET_OF_DYE,
                    DyeMultiplier.MIRACLE_CHANCE)

                FakeDyeDrop.rollFakeDyeDrop(
                    Dye.NECRON,
                    2_500.0,
                    dropRate
                )
            }
            InstanceType.KUUDRA_BASIC -> fakeTentacleDrop(100_000.0)
            InstanceType.KUUDRA_HOT -> fakeTentacleDrop(80_000.0)
            InstanceType.KUUDRA_BURNING -> fakeTentacleDrop(60_000.0)
            InstanceType.KUUDRA_FIERY -> fakeTentacleDrop(40_000.0)
            InstanceType.KUUDRA_INFERNAL -> fakeTentacleDrop(20_000.0)
            else -> return
        }
    }

    private fun fakeTentacleDrop(baseChance: Double) {
        val stats = ProfileStorage.lastPlayedProfile() ?: return

        val dropRate = (1.0 / baseChance) * stats.getDyeMultiplier(
            Dye.TENTACLE,
            DyeMultiplier.VINCENT,
            DyeMultiplier.BUCKET_OF_DYE,
            DyeMultiplier.MIRACLE_CHANCE)

        FakeDyeDrop.rollFakeDyeDrop(
            Dye.TENTACLE,
            baseChance,
            dropRate
        )
    }

    private fun checkInstanceType(instance: String, tier: String) {
        instanceType = when (instance to tier) {
            "Catacombs" to "I" -> InstanceType.CATACOMBS_FLOOR_I
            "Catacombs" to "II" -> InstanceType.CATACOMBS_FLOOR_II
            "Catacombs" to "III" -> InstanceType.CATACOMBS_FLOOR_III
            "Catacombs" to "IV" -> InstanceType.CATACOMBS_FLOOR_IV
            "Catacombs" to "V" -> InstanceType.CATACOMBS_FLOOR_V
            "Catacombs" to "VI" -> InstanceType.CATACOMBS_FLOOR_VI
            "Catacombs" to "VII" -> InstanceType.CATACOMBS_FLOOR_VII
            "Master Catacombs" to "I" -> InstanceType.MASTER_CATACOMBS_FLOOR_I
            "Master Catacombs" to "II" -> InstanceType.MASTER_CATACOMBS_FLOOR_II
            "Master Catacombs" to "III" -> InstanceType.MASTER_CATACOMBS_FLOOR_III
            "Master Catacombs" to "IV" -> InstanceType.MASTER_CATACOMBS_FLOOR_IV
            "Master Catacombs" to "V" -> InstanceType.MASTER_CATACOMBS_FLOOR_V
            "Master Catacombs" to "VI" -> InstanceType.MASTER_CATACOMBS_FLOOR_VI
            "Master Catacombs" to "VII" -> InstanceType.MASTER_CATACOMBS_FLOOR_VII
            "Kuudra" to "Basic" -> InstanceType.KUUDRA_BASIC
            "Kuudra" to "Hot" -> InstanceType.KUUDRA_HOT
            "Kuudra" to "Burning" -> InstanceType.KUUDRA_BURNING
            "Kuudra" to "Fiery" -> InstanceType.KUUDRA_FIERY
            "Kuudra" to "Infernal" -> InstanceType.KUUDRA_INFERNAL
            else -> null
        }
    }

    private fun checkInstanceTypeWithScoreboard() {
        instanceType = when (ScoreboardUtils.getLineAfter("The Catacombs")) {
            "(F1)" -> InstanceType.CATACOMBS_FLOOR_I
            "(F2)" -> InstanceType.CATACOMBS_FLOOR_II
            "(F3)" -> InstanceType.CATACOMBS_FLOOR_III
            "(F4)" -> InstanceType.CATACOMBS_FLOOR_IV
            "(F5)" -> InstanceType.CATACOMBS_FLOOR_V
            "(F6)" -> InstanceType.CATACOMBS_FLOOR_VI
            "(F7)" -> InstanceType.CATACOMBS_FLOOR_VII
            "(M1)" -> InstanceType.MASTER_CATACOMBS_FLOOR_I
            "(M2)" -> InstanceType.MASTER_CATACOMBS_FLOOR_II
            "(M3)" -> InstanceType.MASTER_CATACOMBS_FLOOR_III
            "(M4)" -> InstanceType.MASTER_CATACOMBS_FLOOR_IV
            "(M5)" -> InstanceType.MASTER_CATACOMBS_FLOOR_V
            "(M6)" -> InstanceType.MASTER_CATACOMBS_FLOOR_VI
            "(M7)" -> InstanceType.MASTER_CATACOMBS_FLOOR_VII
            else -> when (ScoreboardUtils.getLineAfter("Kuudra's Hollow")) {
                "(T1)" -> InstanceType.KUUDRA_BASIC
                "(T2)" -> InstanceType.KUUDRA_HOT
                "(T3)" -> InstanceType.KUUDRA_BURNING
                "(T4)" -> InstanceType.KUUDRA_FIERY
                "(T5)" -> InstanceType.KUUDRA_INFERNAL
                else -> instanceType
            }
        }
    }
}