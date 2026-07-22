package anlg.dyeaddons.events

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.config.ConfigManager
import anlg.dyeaddons.config.DyeRotation
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.events.models.ChatEvent
import anlg.dyeaddons.events.models.InventoryOpenEvent
import anlg.dyeaddons.settings.categories.DebugCategories
import anlg.dyeaddons.utils.InventoryUtils.findMatchInLore
import anlg.dyeaddons.utils.SkyblockUtils
import net.minecraft.client.Minecraft
import net.minecraft.world.item.Items

object DyeEventHandler {

    private val ROTATION_PATTERN = Regex("""This dye is\s*(\d+)x as common during SkyBlock Year\s*(\d+)""")

    private val DROPPED_PATTERN = Regex("""You've (?:dropped|bought):\s*(\d+)""")

    private val DYE_CHAT_PATTERN = Regex("""WOW! (?:\[[^]]+]\s)?(?<player>[A-Za-z0-9_]+) found (?:a|an) (?<dye>[A-Za-z ]+ Dye)(?: #[\d,]+)?!""")

    private val DYE_PURCHASE_PATTERN = Regex("""You bought (?<dye>[A-Za-z ]+ Dye)!""")

    fun init() {
        EventBus.subscribe(InventoryOpenEvent::class, ::onInventoryOpen)
        EventBus.subscribe(ChatEvent::class, ::onChat)
    }

    private fun onInventoryOpen(event : InventoryOpenEvent) {
        if (!SkyblockUtils.hypixelMain) return
        val title = event.inventoryName

        when {
            title.contains("Dyes") -> getDyeRotation(event)
            title.contains("Dye Guide") -> getProfileDyes(event)
        }
    }

    private fun onChat(event : ChatEvent) {
        if (!SkyblockUtils.hypixelMain) return
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
        } catch (_: IllegalArgumentException) {
            return
        }

        ProfileStorage.lastPlayedProfile()?.dyeData[dye]?.dropped++
        ConfigManager.save()
        DyeAddons.debug("Captured Dye drop: $dye", DebugCategories.DYE_EVENT)
    }

    private fun getDyeRotation(event : InventoryOpenEvent) {
        val multipliers = mutableMapOf<Dye, Int>()
        var year = 0

        event.slots.filter {
                it.item.`is`(Items.PLAYER_HEAD)
        }.forEach { slot ->

            val slotItemStack = slot.item
            val dyeName = slotItemStack.hoverName.string
            if (dyeName == "Bucket of Dye") return@forEach

            val match = slot.item.findMatchInLore(ROTATION_PATTERN)

            val multiplier = match?.groupValues?.get(1)?.toInt() ?: 1
            year = match?.groupValues?.get(2)?.toInt() ?: year

            try {
                multipliers[Dye.valueOf(Dye.normalizeDyeName(dyeName))] = multiplier
            } catch (_: IllegalArgumentException) {
                DyeAddons.debug("Failed to add $dyeName", DebugCategories.ERROR)
            }
        }

        if (year == 0 || multipliers.size != 3) {
            DyeAddons.debug("Something went wrong while importing dye rotation", DebugCategories.ERROR)
            return
        }

        val dyeRotation = DyeRotation(multipliers, year)
        ConfigManager.data.config.currentDyeRotation = dyeRotation
        ConfigManager.save()

        DyeAddons.debug("Imported dye rotation", DebugCategories.MENU_EVENT)
    }

    private fun getProfileDyes(event : InventoryOpenEvent) {
        event.slots.filter {
                it.item.`is`(Items.PLAYER_HEAD)
        }.forEach { slot ->

            val slotItemStack = slot.item
            val dyeName = slotItemStack.hoverName.string

            var dye : Dye?
            // Skip fire sale dyes
            try {
                dye = Dye.valueOf(Dye.normalizeDyeName(dyeName))
            } catch (_: IllegalArgumentException) {
                return@forEach
            }

            val dropped = slot.item.findMatchInLore(DROPPED_PATTERN)?.groupValues?.get(1)?.toInt() ?: return@forEach

            ProfileStorage.lastPlayedProfile()?.dyeData[dye]?.dropped = dropped
        }
        ConfigManager.save()
    }

}