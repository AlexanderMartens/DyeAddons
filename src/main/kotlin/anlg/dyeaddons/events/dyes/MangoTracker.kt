package anlg.dyeaddons.events.dyes

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.config.DyeMultiplier
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.ChatEvent
import anlg.dyeaddons.settings.categories.DebugCategories
import anlg.dyeaddons.utils.SkyblockUtils
import anlg.dyeaddons.utils.extensions.incrementInt

enum class TreeType (val dropRate: Int) {
    FIG(1_000_000),
    MANGROVE(500_000),
    HELIX(100_000),
}

object MangoTracker {

    private val TREE_GIFT_PATTERN = Regex("""You helped cut (?<cut>[\d.]+)% of the (?<tree>\w+) Tree.""")

    fun init() {
        EventBus.subscribe(ChatEvent::class, ::onChat)
    }

    private fun onChat(event: ChatEvent) {
        if (!SkyblockUtils.hypixelMain ||
            !SkyblockUtils.isInSkyblock()) return

        val match = TREE_GIFT_PATTERN.matchEntire(event.unformattedText.trim()) ?: return

        val cut = match.groups["cut"]?.value?.toFloat() ?: return
        val tree = match.groups["tree"]?.value ?: return

        if (cut < 10f) return

        val treeType = when (tree) {
            "Fig" -> TreeType.FIG
            "Mangrove" -> TreeType.MANGROVE
            "Helix" -> TreeType.HELIX
            else -> return
        }

        updateDyeStats(treeType)
        updateDyeProgress(treeType)
        DyeAddons.debug("Tracked tree gift: $treeType", DebugCategories.DYE_PROGRESS_EVENT)
    }

    private fun updateDyeStats(treeType: TreeType) {
        val stats = ProfileStorage.lastPlayedProfile()?.dyeData[Dye.MANGO]?.statistics ?: return

        when (treeType) {
            TreeType.FIG -> stats.incrementInt("Fig Tree Gifts")
            TreeType.MANGROVE -> stats.incrementInt("Mangrove Tree Gifts")
            TreeType.HELIX -> stats.incrementInt("Helix Tree Gifts")
        }
    }

    private fun updateDyeProgress(treeType: TreeType) {
        val stats = ProfileStorage.lastPlayedProfile() ?: return

        val dropRate = 1.0 / treeType.dropRate * stats.getDyeMultiplier(
            Dye.MANGO,
            DyeMultiplier.VINCENT,
            DyeMultiplier.BUCKET_OF_DYE,
            DyeMultiplier.MIRACLE_CHANCE)

        ProfileStorage.lastPlayedProfile()?.dyeData[Dye.MANGO]?.progress += dropRate
    }
}