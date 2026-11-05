package anlg.dyeaddons.events.dyes

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.DyeAddons.Companion.mc
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.ActionBarEvent
import anlg.dyeaddons.events.models.BlockClickEvent
import anlg.dyeaddons.events.models.InteractMouseButton
import anlg.dyeaddons.events.models.ItemEntityDespawnEvent
import anlg.dyeaddons.events.models.SoundPlayEvent
import anlg.dyeaddons.events.models.WorldChangedEvent
import anlg.dyeaddons.features.dye.FakeDyeDrop
import anlg.dyeaddons.settings.categories.DebugCategories
import anlg.dyeaddons.settings.categories.Dyes
import anlg.dyeaddons.utils.SkyblockUtils
import anlg.dyeaddons.utils.TabListUtils
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.PlayerHeadBlock
import net.minecraft.world.level.block.entity.SkullBlockEntity

object SecretTracker {

    private val dye = Dye.SECRET

    private val itemSecrets = listOf(
        "Decoy",
        "Trap",
        "Training Weights",
        "Spirit Leap",
        "Inflatable Jerry",
        "Healing VIII Splash Potion",
        "Dungeon Chest Key",
        "Treasure Talisman",
        "Defuse Kit",
        "Architect's First Draft",
        "Candycomb",
    )

    private val secretsClicked : MutableList<BlockPos> = mutableListOf()

    private var inSecretRoom = false

    fun init() {
        EventBus.subscribe(BlockClickEvent::class, ::onBlockClick)
        EventBus.subscribe(SoundPlayEvent::class, ::onSound)
        EventBus.subscribe(ItemEntityDespawnEvent::class, ::onItemEntityDespawn)
        EventBus.subscribe(WorldChangedEvent::class, ::onWorldChange)
        EventBus.subscribe(ActionBarEvent::class, ::onActionBar)
    }

    private fun onBlockClick(event: BlockClickEvent) {
        if (!SkyblockUtils.hypixelMain ||
            !SkyblockUtils.isInSkyblock() ||
            TabListUtils.getLineAfter("Dungeon:").trim() != "Catacombs") return

        if (!inSecretRoom) return

        if (event.clickType != InteractMouseButton.RIGHT_CLICK) return

        if (event.state == null || event.state.block !in listOf(Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.PLAYER_HEAD)) return

        // Check if wither essence
        if (event.state.block is PlayerHeadBlock &&
            mc.level?.getBlockEntity(event.pos) is SkullBlockEntity &&
            (mc.level?.getBlockEntity(event.pos) as? SkullBlockEntity)?.ownerProfile?.partialProfile()?.id?.toString() != "2865274b-3097-394e-8149-ec629c72d850") return

        if (event.pos in secretsClicked) return

        secretsClicked.add(event.pos)

        updateDyeStats()
        updateDyeProgress()
        DyeAddons.debug("Tracked secret: chest or wither essence", DebugCategories.DYE_PROGRESS_EVENT)
    }

    private fun onSound(event: SoundPlayEvent) {
        if (!SkyblockUtils.hypixelMain ||
            !SkyblockUtils.isInSkyblock() ||
            TabListUtils.getLineAfter("Dungeon:").trim() != "Catacombs") return

        if (!inSecretRoom) return

        if (event.name != "entity.bat.death") return

        updateDyeStats()
        updateDyeProgress()
        DyeAddons.debug("Tracked secret: bat", DebugCategories.DYE_PROGRESS_EVENT)
    }

    private fun onItemEntityDespawn(event: ItemEntityDespawnEvent) {
        if (!SkyblockUtils.hypixelMain ||
            !SkyblockUtils.isInSkyblock() ||
            TabListUtils.getLineAfter("Dungeon:").trim() != "Catacombs") return

        if (!inSecretRoom) return

        if (event.itemEntity.distanceTo(mc.player ?: return) > 6f) return

        if (event.itemEntity.item.hoverName.string !in itemSecrets) return

        updateDyeStats()
        updateDyeProgress()
        DyeAddons.debug("Tracked secret item: ${event.itemEntity.item.hoverName.string}", DebugCategories.DYE_PROGRESS_EVENT)
    }

    private fun onWorldChange(@Suppress("UNUSED_PARAMETER") event: WorldChangedEvent) {
        secretsClicked.clear()
    }

    private fun onActionBar(event: ActionBarEvent) {
        if (!SkyblockUtils.hypixelMain ||
            !SkyblockUtils.isInSkyblock() ||
            TabListUtils.getLineAfter("Dungeon:").trim() != "Catacombs") return

        inSecretRoom = event.unformattedText.trim().contains("Secrets")
    }

    private fun updateDyeStats() {
        val stats = ProfileStorage.lastPlayedProfile() ?: return

        stats.incrementStat(dye, "Catacombs Secrets Collected")
    }

    private fun updateDyeProgress() {
        val stats = ProfileStorage.lastPlayedProfile() ?: return

        val dropRate = 1.0 / dye.getDropRate("Secret") * stats.getDyeMultiplier(dye)

        ProfileStorage.lastPlayedProfile()?.dyeData[dye]?.progress += dropRate

        val fakeDropRate = if (Dyes.fakeDyeLegacyDropRate) dye.getDropRate("Secret (Legacy)") else dye.getDropRate("Secret")
        val fakeDropChance = 1.0 / fakeDropRate * stats.getDyeMultiplier(dye)

        FakeDyeDrop.rollFakeDyeDrop(dye,
            fakeDropRate,
            fakeDropChance)
    }
}