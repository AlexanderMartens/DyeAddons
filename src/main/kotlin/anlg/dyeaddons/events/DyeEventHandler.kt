package anlg.dyeaddons.events

import anlg.dyeaddons.DyeAddons.Companion.logger
import anlg.dyeaddons.config.ConfigManager
import anlg.dyeaddons.config.DyeInRotation
import anlg.dyeaddons.config.DyeRotation
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.events.models.ChatEvent
import anlg.dyeaddons.events.models.InventoryOpenEvent
import anlg.dyeaddons.utils.ChatUtils
import anlg.dyeaddons.utils.SkyblockUtils
import net.minecraft.client.Minecraft
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.Items

object DyeEventHandler {

    val ROTATION_PATTERN = Regex("""This dye is\s*(\d+)x as common during SkyBlock Year\s*(\d+)""")

    val DROPPED_PATTERN = Regex("""You've (?:dropped|bought):\s*(\d+)""")

    val DYE_CHAT_PATTERN = Regex("""WOW! (?:\[[^]]+]\s)?(?<player>[A-Za-z0-9_]+) found (?:a|an) (?<dye>[A-Za-z ]+ Dye)(?: #[\d,]+)?!""")

    val DYE_PURCHASE_PATTERN = Regex("""You bought (?<dye>[A-Za-z ]+ Dye)!""")

    fun init() {
        EventBus.subscribe(InventoryOpenEvent::class, ::onInventoryOpen)
        EventBus.subscribe(ChatEvent::class, ::onChat)
    }

    private fun onInventoryOpen(event : InventoryOpenEvent) {
        val menu = event.screen.menu
        val title = event.screen.getTitle()

        if (title.contains(Component.literal("Dyes")) && !SkyblockUtils.hypixelAlpha) getDyeRotation(menu)
        if (title.contains(Component.literal("Dye Compendium")) && !SkyblockUtils.hypixelAlpha) getProfileDyes(menu)
    }

    private fun onChat(event : ChatEvent) {
        if (SkyblockUtils.hypixelAlpha) return
        val text = event.unformattedText

        val dropMatch = DYE_CHAT_PATTERN.find(text)
        val buyMatch = DYE_PURCHASE_PATTERN.find(text)

        if (dropMatch == null && buyMatch == null) return

        var dyeName : String
        if (dropMatch != null) {
            val player = dropMatch.groups["player"]?.value
            if (player != Minecraft.getInstance().player?.name?.string) return
            dyeName = dropMatch.groups["dye"]?.value ?: return
        } else {
            dyeName = buyMatch?.groupValues?.get(1) ?: return
        }

        val dye : Dye?
        try {
            dye = Dye.valueOf(Dye.normalizeDyeName(dyeName))
        } catch (e: IllegalArgumentException) {
            return
        }

        ProfileStorage.lastPlayedProfile()?.dyeData[dye]?.dropped++
        ConfigManager.save()
    }

    private fun getDyeRotation(menu : AbstractContainerMenu) {
        val dyes = mutableListOf<DyeInRotation>()
        var year = 0

        menu.slots.filter { !it.item.isEmpty &&
                it.item.`is`(Items.PLAYER_HEAD) &&
                it.container !is Inventory
        }.forEach { slot ->

            val slotItemStack = slot.item
            val dyeName = slotItemStack.hoverName.string
            if (dyeName == "Bucket of Dye") return@forEach

            val lore = slotItemStack.get(DataComponents.LORE)
            val loreText = lore?.lines()?.joinToString(" ") { it.string } ?: ""

            val multiplier = ROTATION_PATTERN.find(loreText)?.groupValues?.get(1)?.toInt() ?: 1
            year = ROTATION_PATTERN.find(loreText)?.groupValues?.get(2)?.toInt() ?: year

            try {
                dyes.add(DyeInRotation(Dye.valueOf(Dye.normalizeDyeName(dyeName)), multiplier))
            } catch (e: IllegalArgumentException) {
                logger.info("Failed to add $dyeName")
            }
        }

        if (year == 0 || dyes.size != 3) {
            ChatUtils.addLocalChatMessage("Something went wrong while importing dye rotation", true)
            return
        }

        val dyeRotation = DyeRotation(dyes, year)
        ConfigManager.data.config.currentDyeRotation = dyeRotation
        ConfigManager.save()

        ChatUtils.addLocalChatMessage("Imported dye rotation", true)
    }

    private fun getProfileDyes(menu : AbstractContainerMenu) {
        menu.slots.filter { !it.item.isEmpty &&
                it.item.`is`(Items.PLAYER_HEAD) &&
                it.container !is Inventory
        }.forEach { slot ->

            val slotItemStack = slot.item
            val dyeName = slotItemStack.hoverName.string

            var dye : Dye?
            // Skip fire sale dyes
            try {
                dye = Dye.valueOf(Dye.normalizeDyeName(dyeName))
            } catch (e: IllegalArgumentException) {
                return@forEach
            }

            val lore = slotItemStack.get(DataComponents.LORE)
            val loreText = lore?.lines()?.joinToString(" ") { it.string } ?: ""

            val dropped = DROPPED_PATTERN.find(loreText)?.groupValues?.get(1)?.toInt()

            if (dropped != null) {
                ProfileStorage.lastPlayedProfile()?.dyeData[dye]?.dropped = dropped
            }
        }
        ConfigManager.save()
    }

}