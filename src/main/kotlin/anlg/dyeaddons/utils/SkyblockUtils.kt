package anlg.dyeaddons.utils

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.DyeAddons.Companion.mc
import anlg.dyeaddons.data.ColorCodes.*
import anlg.dyeaddons.config.ConfigManager
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.ClientDisconnectEvent
import anlg.dyeaddons.events.models.ClientTickEvent
import anlg.dyeaddons.events.models.SkyblockYearChangeEvent
import anlg.dyeaddons.events.models.WorldChangedEvent
import anlg.dyeaddons.settings.categories.Debug
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.HoverEvent
import net.minecraft.world.scores.DisplaySlot

object SkyblockUtils {

    private var cachedIsInSkyblock : Boolean = false
        get() = field || Debug.alwaysOnSkyblock
    
    private var cachedWorldName : String? = null

    private const val TICKS_PER_ANNOYANCE = 20 * 60 * 5 // Tell user about dye rotation every 5 minutes
    private const val TICKS_PER_UPDATE = 20
    private var tickCounter = 0

    var isFirstJoin = false
    var hasMigratedMFRework = true
    var firstJoinVersion = ""

    val onHypixel get() = hypixelMain || hypixelAlpha || Debug.alwaysHypixelMain

    var hypixelMain = false
        get() = field || Debug.alwaysHypixelMain

    var hypixelAlpha = false

    var profileName = ""

    fun init() {
        EventBus.subscribe(ClientTickEvent::class, ::onClientTick)
        EventBus.subscribe(WorldChangedEvent::class, ::onWorldChanged)
        EventBus.subscribe(ClientDisconnectEvent::class, ::onDisconnect)
        EventBus.subscribe(SkyblockYearChangeEvent::class, ::onYearChange)
    }

    private fun onClientTick(@Suppress("UNUSED_PARAMETER") event: ClientTickEvent) {
        tickCounter++
        if (tickCounter % TICKS_PER_ANNOYANCE == 0) askDyeRotation()
        if (tickCounter % TICKS_PER_UPDATE != 0) return

        updateCache()
    }

    private fun onWorldChanged(@Suppress("UNUSED_PARAMETER") event: WorldChangedEvent) {
        tickCounter = TICKS_PER_UPDATE
        cachedIsInSkyblock = false
        cachedWorldName = null

        updateCache()
    }

    private fun onDisconnect(@Suppress("UNUSED_PARAMETER") event: ClientDisconnectEvent) {
        hypixelMain = false
        hypixelAlpha = false
    }

    private fun onYearChange(@Suppress("UNUSED_PARAMETER") event: SkyblockYearChangeEvent) {
        if (isFirstJoin) return
        ChatUtils.addLocalChatMessage("Skyblock year has changed. Talk to Vincent or do /dyes to update dye rotation!", true)
    }

    private fun updateCache() {
        cachedIsInSkyblock = readIsInSkyblock()

        cachedWorldName = if (cachedIsInSkyblock) {
            readWorldName()
        } else null
        checkHypixel()
        checkProfile()
        checkYear()
        sendWelcomeMessage()
    }

    private fun readWorldName() : String? {
        val worldName = TabListUtils.getLineAfter("Area:")
        return worldName.ifEmpty { null }
    }

    private fun readIsInSkyblock(): Boolean {
        val scoreboard = mc.level?.scoreboard ?: return false
        val objective = scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR) ?: return false
        val title = objective.displayName.string
        return title.contains("skyblock", ignoreCase = true)
    }

    private fun checkProfile() {
        var newProfile = TabListUtils.getLineAfter("Profile:").trim().split(" ").getOrElse(0) { "" }
        if (cachedWorldName == "The Rift") newProfile = newProfile.reversed()
        profileName = newProfile
        if (!profileName.isEmpty()) {
            ConfigManager.data.players[mc.player?.uuid]?.lastPlayedProfile = profileName
        }
    }

    private fun checkHypixel() {
        val mc = Minecraft.getInstance()
        if (mc.player == null) return

        var hypixel = false

        val clientBrand = mc.connection?.serverBrand()

        val ip = mc.currentServer?.ip

        clientBrand?.let {
            if (it.contains("hypixel", ignoreCase = true)) {
                hypixel = true
            }
        }

        ip?.let {
            if (it.startsWith("alpha.")) hypixelAlpha = true
        }

        hypixelMain = hypixel && !hypixelAlpha
    }

    private fun checkYear() {
        if (!cachedIsInSkyblock) return

        if (ConfigManager.data.config.cachedSbYear != SkyblockTime.now().year) {
            EventBus.publish(SkyblockYearChangeEvent(
                ConfigManager.data.config.cachedSbYear,
                SkyblockTime.now().year))
            ConfigManager.data.config.cachedSbYear = SkyblockTime.now().year
        }
    }

    fun isInSkyblock(): Boolean {
        return cachedIsInSkyblock
    }

    /**
     * Gets current skyblock world name or null if not found or not in skyblock
     */
    fun getWorldName(): String? {
        return cachedWorldName
    }

    private fun sendWelcomeMessage() {
        if (!cachedIsInSkyblock) return
        if (!isFirstJoin && !hasMigratedMFRework) {
            ChatUtils.addLocalChatMessage(
                "Most of your statistics have been migrated to the legacy tab because of the magic find rework. " +
                        "They will still count toward your dye progress in the statistics menu. " +
                        "You can look at them by switching to 'Legacy' in the statistics menu if there are legacy statistics.",
                true)
            hasMigratedMFRework = true
        }
        if (!isFirstJoin && !firstJoinVersion.isBlank()) {
            ChatUtils.addLocalChatMessage(
                Component.literal("Thank you for updating to version ${DyeAddons.rawVersion}! ")
                    .append(Component.literal("${YELLOW}${UNDERLINE}Click here to configure the new version.")
                        .withStyle { it.withHoverEvent(
                            HoverEvent.ShowText(
                                Component.literal("${YELLOW}Click here to run /dyeaddons config ${DyeAddons.rawVersion}")
                            ))}
                        .withStyle { it.withClickEvent(
                            ClickEvent.RunCommand("/dyeaddons config ${DyeAddons.rawVersion}")
                        )}),
                true)
            firstJoinVersion = ""
        }
        if (isFirstJoin) {
            val chatBreak = "${GRAY}${ChatUtils.getChatBreak("▬")}"
            ChatUtils.addLocalChatMessage(chatBreak)
            ChatUtils.addLocalChatMessage("Thank you for using ${RED}${BOLD}DyeAddons${WHITE}!")
            ChatUtils.addLocalChatMessage("${GRAY}- ${RED}Open your dye compendium with /dyeaddons")
            ChatUtils.addLocalChatMessage("${GRAY}- ${GOLD}Open config with /dyeaddons config")
            ChatUtils.addLocalChatMessage("${GRAY}- ${YELLOW}Run /dyeaddons quickstart {mf} {looting} {overbloom} to get started.")
            ChatUtils.addLocalChatMessage(chatBreak)
            isFirstJoin = false
            firstJoinVersion = ""
        }
    }

    private fun askDyeRotation() {
        if (ConfigManager.data.config.currentDyeRotation?.year == SkyblockTime.now().year) return
        ChatUtils.addLocalChatMessage("Dye rotation not loaded! Do /dyes to load dye rotation. Vincent multipliers will not work until you do.", true)
    }

}