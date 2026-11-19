package anlg.dyeaddons.features.dye

import anlg.dyeaddons.DyeAddons.Companion.mc
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.AfterSlotRenderedEvent
import anlg.dyeaddons.events.models.BeforeSlotRenderedEvent
import anlg.dyeaddons.settings.categories.Dyes
import anlg.dyeaddons.utils.InventoryUtils.findMatchInLore
import anlg.dyeaddons.utils.SkyblockUtils
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.world.inventory.Slot
import java.awt.Color

object DyeMenuHighlight {

    private val DROPPED_PATTERN = Regex("""You've (?:dropped|bought):\s*(\d+)""")
    private val GEM_DYE_PATTERN = Regex("""cannot be donated to Vincent!""")

    private val DEFAULT_SLOT_SIZE = 16

    fun init() {
        EventBus.subscribe(BeforeSlotRenderedEvent::class, ::onBeforeSlotRender)
        EventBus.subscribe(AfterSlotRenderedEvent::class, ::onAfterSlotRender)
    }

    private fun onBeforeSlotRender(event: BeforeSlotRenderedEvent) {
        if (!SkyblockUtils.isInSkyblock()) return
        if (!event.screen.title.string.contains("Dye Guide")) return

        val slot = event.slot
        val item = slot.item
        if (item.isEmpty) return

        highlightDroppedDye(slot, event.drawContext)
        highlightGemDye(slot, event.drawContext)
    }

    private fun onAfterSlotRender(event: AfterSlotRenderedEvent) {
        if (!Dyes.dyeMenuItemStack) return
        if (!SkyblockUtils.isInSkyblock()) return
        if (!event.screen.title.string.contains("Dye Guide")) return

        val slot = event.slot
        val item = slot.item
        if (item.isEmpty) return

        val dropped = slot.item.findMatchInLore(DROPPED_PATTERN)?.groupValues?.get(1)?.toInt() ?: return

        val textRenderer = mc.font

        event.drawContext.text(textRenderer,
            dropped.toString(),
            slot.x + DEFAULT_SLOT_SIZE + 1 - textRenderer.width(dropped.toString()),
            slot.y + DEFAULT_SLOT_SIZE + 2 - textRenderer.lineHeight,
            Color(255, 255, 255, 255).rgb,
            true)
    }

    private fun highlightDroppedDye(slot: Slot, drawContext: GuiGraphicsExtractor) {
        if (!Dyes.highlightObtainedDyes) return

        val dropped = slot.item.findMatchInLore(DROPPED_PATTERN)?.groupValues?.get(1)?.toInt() ?: return
        if (dropped == 0) return

        val x = slot.x
        val y = slot.y
        drawContext.fill(x, y, x + DEFAULT_SLOT_SIZE, y + DEFAULT_SLOT_SIZE, Color(0, 255, 0, 200).rgb)
    }

    private fun highlightGemDye(slot: Slot, drawContext: GuiGraphicsExtractor) {
        if (!Dyes.highlightGemDyes) return
        slot.item.findMatchInLore(GEM_DYE_PATTERN) ?: return

        val x = slot.x
        val y = slot.y
        drawContext.fill(x, y, x + DEFAULT_SLOT_SIZE, y + DEFAULT_SLOT_SIZE, Color(255, 0, 0, 200).rgb)
    }
}