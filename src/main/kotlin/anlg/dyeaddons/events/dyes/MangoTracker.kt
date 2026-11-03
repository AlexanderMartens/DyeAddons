package anlg.dyeaddons.events.dyes

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.ChatEvent
import anlg.dyeaddons.events.models.SoundPlayEvent
import anlg.dyeaddons.features.dye.FakeDyeDrop
import anlg.dyeaddons.settings.categories.DebugCategories
import anlg.dyeaddons.settings.categories.Dyes
import anlg.dyeaddons.utils.SkyblockUtils

object MangoTracker {

    private val dye = Dye.MANGO

    private val TREE_GIFT_PATTERN = Regex("""You helped cut (?<cut>[\d.]+)% of the (?<tree>\w+) Tree.""")

    private enum class TreeType (val dropRate: Double) {
        FIG(dye.getDropRate("Fig")),
        MANGROVE(dye.getDropRate("Mangrove")),
        HELIX(dye.getDropRate("Helix")),
    }

    fun init() {
        EventBus.subscribe(ChatEvent::class, ::onChat)
        EventBus.subscribe(SoundPlayEvent::class, ::onSound)
    }

    private fun onSound(event: SoundPlayEvent) {
        if (!SkyblockUtils.isInSkyblock() ||
            SkyblockUtils.getWorldName() == "Private Island" ||
            !Dyes.addLegacyFakeDyes) return

        val soundName = event.name
        if (soundName != "block.ladder.break") return

        //DyeAddons.debug("Tracked log broken", DebugCategories.DYE_PROGRESS_EVENT)

        val stats = ProfileStorage.lastPlayedProfile() ?: return

        val dropRate = 1.0 / dye.getDropRate("Log (Legacy)") * stats.getDyeMultiplier(dye)
        FakeDyeDrop.rollFakeDyeDrop(dye, dye.getDropRate("Log (Legacy)"), dropRate)
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
        val stats = ProfileStorage.lastPlayedProfile() ?: return

        when (treeType) {
            TreeType.FIG -> stats.incrementStat(dye, "Fig Tree Gifts")
            TreeType.MANGROVE -> stats.incrementStat(dye, "Mangrove Tree Gifts")
            TreeType.HELIX -> stats.incrementStat(dye, "Helix Tree Gifts")
        }
    }

    private fun updateDyeProgress(treeType: TreeType) {
        val stats = ProfileStorage.lastPlayedProfile() ?: return

        val dropRate = 1.0 / treeType.dropRate * stats.getDyeMultiplier(dye)

        ProfileStorage.lastPlayedProfile()?.dyeData[dye]?.progress += dropRate
        FakeDyeDrop.rollFakeDyeDrop(dye,
            treeType.dropRate,
            dropRate)
    }
}