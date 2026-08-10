package anlg.dyeaddons.events.dyes

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.DyeAddons.Companion.mc
import anlg.dyeaddons.config.DyeMultiplier
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.ChatEvent
import anlg.dyeaddons.settings.categories.DebugCategories
import anlg.dyeaddons.utils.SkyblockUtils
import anlg.dyeaddons.utils.extensions.incrementInt

object EmeraldTracker {

    enum class CritterRarity(val dropChance: Int) {
        COMMON(500_000),
        UNCOMMON(250_000),
        RARE(100_000),
        EPIC(50_000),
        LEGENDARY(25_000),
    }


    private val CRITTER_PATTERN = Regex("""(?:(?<lootshare>§e§lLOOT SHARE!)|§a§lCAPTURE!) §7(?:You caught a |You received a )(?:§6§l(?<sparkling>SPARKLING) )?§(?<color>.)""")
    private val HIDEYHO_PATTERN = Regex("""§a§lCAPTURE! §7You found §(?<color>.)Hideyho§7""")
    private val SPARKLING_PATTERN = Regex("""§6§lSPARKLING! §.(?<player>[A-Z-a-z0-9_]+)§e caught a §6§lSPARKLING §(?<color>.)[A-Za-z ]+§e!""")

    fun init() {
        //EventBus.subscribe(OreMinedEvent::class, ::onOreMined)
        EventBus.subscribe(ChatEvent::class, ::onChat)
    }

    /* Emerald Dye no longer drops from emeralds, but keeping this here in case it comes back
    private fun onOreMined(event: OreMinedEvent) {
        if (!SkyblockUtils.hypixelMain || !SkyblockUtils.isInSkyblock()) return

        val emeraldBlocks = event.blocks.filter { it.state.block == Blocks.EMERALD_BLOCK || it.state.block == Blocks.EMERALD_ORE }

        if (emeraldBlocks.isEmpty()) return

        DyeAddons.debug("Broke ${emeraldBlocks.size} emerald blocks", DebugCategories.DYE_PROGRESS_EVENT)
        val totalBlocks = emeraldBlocks.size
        updateDyeStats(totalBlocks)
        updateDyeProgress(totalBlocks)
    }*/

    private fun onChat(event: ChatEvent) {
        if (!SkyblockUtils.hypixelMain ||
            !SkyblockUtils.isInSkyblock() ||
            SkyblockUtils.getWorldName() != "Safari") return

        if (HIDEYHO_PATTERN.matchesAt(event.formattedText.trim(), 0)) {
            DyeAddons.debug("Found Critter! Rarity: RARE, Sparkling: false, Loot share: false")
            updateDyeStats(CritterRarity.RARE)
            updateDyeProgress(CritterRarity.RARE)
            return
        }

        SPARKLING_PATTERN.matchEntire(event.formattedText.trim())?.let {
            val rarity = when(it.groups["color"]?.value) {
                "f" -> CritterRarity.COMMON
                "a" -> CritterRarity.UNCOMMON
                "9" -> CritterRarity.RARE
                "5" -> CritterRarity.EPIC
                "6" -> CritterRarity.LEGENDARY
                else -> return
            }
            val lootshare = it.groups["player"]?.value != mc.player?.name?.string
            DyeAddons.debug("Sparkling mob caught! Rarity: $rarity, Sparkling: true, Loot share: $lootshare", DebugCategories.DYE_PROGRESS_EVENT)
            updateDyeStats(rarity)
            updateDyeProgress(rarity, true)
            return
        }

        val match = CRITTER_PATTERN.matchAt(event.formattedText.trim(), 0) ?: return

        val rarity = when(match.groups["color"]?.value) {
            "f" -> CritterRarity.COMMON
            "a" -> CritterRarity.UNCOMMON
            "9" -> CritterRarity.RARE
            "5" -> CritterRarity.EPIC
            "6" -> CritterRarity.LEGENDARY
            else -> return
        }

        if (match.groups["sparkling"]?.value != null) return // We already count sparklings in the other message

        val lootShare = match.groups["lootshare"]?.value != null

        DyeAddons.debug("Found Critter! Rarity: $rarity, Sparkling: false, Loot share: $lootShare", DebugCategories.DYE_PROGRESS_EVENT)
        updateDyeStats(rarity)
        updateDyeProgress(rarity, false)
    }

    private fun updateDyeStats(rarity: CritterRarity) {
        val stats = ProfileStorage.lastPlayedProfile()?.dyeData[Dye.EMERALD]?.statistics ?: return

        when (rarity) {
            CritterRarity.COMMON -> stats.incrementInt("Common Safari Critters Hunted")
            CritterRarity.UNCOMMON -> stats.incrementInt("Uncommon Safari Critters Hunted")
            CritterRarity.RARE -> stats.incrementInt("Rare Safari Critters Hunted")
            CritterRarity.EPIC -> stats.incrementInt("Epic Safari Critters Hunted")
            CritterRarity.LEGENDARY -> stats.incrementInt("Legendary Safari Critters Hunted")
        }
    }

    private fun updateDyeProgress(rarity: CritterRarity, sparkling: Boolean = false) {
        val stats = ProfileStorage.lastPlayedProfile() ?: return

        val dropRate = 1.0 / rarity.dropChance.toDouble() * (if (sparkling) 100.0 else 1.0) * stats.getDyeMultiplier(
            Dye.EMERALD,
            DyeMultiplier.VINCENT,
            DyeMultiplier.BUCKET_OF_DYE,
            DyeMultiplier.MIRACLE_CHANCE)

        ProfileStorage.lastPlayedProfile()?.dyeData[Dye.EMERALD]?.progress += dropRate
    }
}