package anlg.dyeaddons.events

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.config.*
import anlg.dyeaddons.data.ColorCodes.*
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.events.models.ChatEvent
import anlg.dyeaddons.events.models.InventoryOpenEvent
import anlg.dyeaddons.features.dye.MedalIntegration
import anlg.dyeaddons.settings.categories.DebugCategories
import anlg.dyeaddons.settings.categories.Dyes
import anlg.dyeaddons.utils.*
import anlg.dyeaddons.utils.InventoryUtils.findMatchInLore
import net.minecraft.client.Minecraft
import net.minecraft.world.item.Items

object DyeEventHandler {

    private val ROTATION_PATTERN = Regex("""This dye is\s*(\d+)x as common during SkyBlock Year\s*(\d+)""")

    private val DROPPED_PATTERN = Regex("""You've (?:dropped|bought):\s*(\d+)""")

    private val DYE_CHAT_PATTERN = Regex("""WOW! (?:\[[^]]+]\s)?(?<player>[A-Za-z0-9_]+) found (?:a|an) (?<dye>[A-Za-z ]+ Dye)(?: #[\d,]+)?!""")

    private val DYE_PURCHASE_PATTERN = Regex("""You (?:bought|claimed) (?<dye>[A-Za-z ]+ Dye)!""")

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
        val text = event.unformattedText.trim()

        val dropMatch = DYE_CHAT_PATTERN.matchEntire(text)
        val buyMatch = DYE_PURCHASE_PATTERN.matchEntire(text)

        if (dropMatch == null && buyMatch == null) return

        var dyeName : String
        if (dropMatch != null) {
            val player = dropMatch.groups["player"]?.value
            if (player != Minecraft.getInstance().player?.name?.string) return
            dyeName = dropMatch.groups["dye"]?.value ?: return
        } else {
            dyeName = buyMatch?.groups["dye"]?.value ?: return
        }

        val dye = Dye.fromValue(dyeName) ?: return

        RngMeter.guaranteedDye(dye)
        ProfileStorage.lastPlayedProfile()?.dyeData[dye]?.let {
            // Increment Dropped
            it.dropped++

            // Time since last any dye drop message
            if (Dyes.timeSinceLastDyeDrop) {
                val lastDye = if (Dyes.timeSinceLastDyeWithShop) {
                    ProfileStorage.lastPlayedProfile()?.dyeData?.maxByOrNull { (_, dyeData) ->
                        dyeData.dyesDropped.maxOfOrNull { dyesDropped -> dyesDropped.timestamp } ?: 0L }
                } else {
                    ProfileStorage.lastPlayedProfile()?.dyeData?.filter { (dye, _) ->
                        !dye.isShopDye() }?.maxByOrNull { (_, dyeData) ->
                        dyeData.dyesDropped.maxOfOrNull { dyesDropped -> dyesDropped.timestamp } ?: 0L }
                }
                val lastDyeTimestamp = lastDye?.value?.dyesDropped?.maxOfOrNull { dyesDropped ->
                    dyesDropped.timestamp } ?: 0L

                if (lastDyeTimestamp != 0L && (Dyes.timeSinceLastDyeWithShop || !dye.isShopDye())) {
                    val timeSinceLast = System.currentTimeMillis() - lastDyeTimestamp
                    ChatUtils.addLocalChatMessage("Your last dye (${lastDye?.key?.colorCode}${lastDye?.key}${WHITE}) " +
                            "was ${StringUtils.formatTime(timeSinceLast)} ago.", true)
                }
            }

            // Time since last unique message
            if (it.dropped == 1 && Dyes.timeSinceLastUnique) {
                val lastUniqueDyeTimestamp = ProfileStorage.lastPlayedProfile()?.dyeData?.maxOfOrNull { dyeData ->
                    dyeData.value.dyesDropped.minOfOrNull { dyesDropped -> dyesDropped.timestamp } ?: 0L
                } ?: 0L
                val uniqueDyes = ProfileStorage.lastPlayedProfile()?.uniqueDyes

                if (lastUniqueDyeTimestamp != 0L) {
                    val timeSinceLast = System.currentTimeMillis() - lastUniqueDyeTimestamp
                    ChatUtils.addLocalChatMessage("It took " +
                            "${StringUtils.formatTime(timeSinceLast)} for your " +
                            "${StringUtils.toOrdinal(uniqueDyes ?: 1)} unique dye.", true)
                }
            }

            // Time since last same dye message
            if (it.dropped > 1 && Dyes.timeSinceSameDye) {
                val lastDyeTimestamp = it.dyesDropped.maxOfOrNull { dyeDropped -> dyeDropped.timestamp } ?: 0L
                val timeSinceLast = System.currentTimeMillis() - lastDyeTimestamp

                if (lastDyeTimestamp != 0L) {
                    ChatUtils.addLocalChatMessage("It took " +
                            "${StringUtils.formatTime(timeSinceLast)} for your " +
                            "${StringUtils.toOrdinal(it.dropped)} ${dye.colorCode}$dye ${WHITE}Dye.", true)
                }
            }

            // Add to dyesDropped and play custom sound
            if (!dye.isShopDye()) {
                it.dyesDropped.add(DyeDropped(System.currentTimeMillis(), it.progress))

                if (Dyes.customDyeSound != "") SoundUtils.playCustomUserSound(Dyes.customDyeSound)
            } else {
                it.dyesDropped.add(DyeDropped(System.currentTimeMillis(), it.dropped.toDouble()))

                if (Dyes.customDyeSoundOnBoughtDyes && Dyes.customDyeSound != "")
                    SoundUtils.playCustomUserSound(Dyes.customDyeSound)
            }
        }
        MedalIntegration.saveDyeClip(dye)
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
            if (ConfigManager.data.config.currentDyeRotation?.year != SkyblockTime.now().year)
                DyeAddons.debug("Something went wrong while importing dye rotation. Reopen the dye menu to try again.", DebugCategories.ERROR)
            return
        }

        val dyeRotation = DyeRotation(multipliers, year)
        ConfigManager.data.config.currentDyeRotation = dyeRotation
        ProfileStorage.lastPlayedProfile()?.let {
            val stats = it.dyeData
            val rotationData = it.rotationData
            rotationData.putIfAbsent(year, RotationData(
                multipliers,
                multipliers.mapValues { (dye, _) ->
                    stats[dye]?.copy(statistics = stats[dye]?.statistics?.filterKeys { stat ->
                        stats[dye]?.statistics?.none { otherStat ->
                            stat.replace(Regex(""" \(\dx\)"""), "▬") == "${otherStat.key}▬"
                        } ?: true
                    }?.toMutableMap() ?: mutableMapOf()) ?: DyeData()
                }
            ))
        }

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