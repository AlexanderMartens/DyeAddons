package anlg.dyeaddons.utils

import anlg.dyeaddons.DyeAddons.Companion.mc
import anlg.dyeaddons.config.ConfigManager
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.ClientDisconnectEvent
import anlg.dyeaddons.events.models.ClientTickEvent
import anlg.dyeaddons.events.models.SkyblockYearChangeEvent
import anlg.dyeaddons.events.models.WorldChangedEvent
import net.minecraft.client.Minecraft
import net.minecraft.world.scores.DisplaySlot

object SkyblockUtils {

    private var cachedIsInSkyblock : Boolean = false
    private var cachedWorldName : String? = null

    private const val TICKS_PER_UPDATE = 20
    private var tickCounter = 0

    var isFirstJoin = false

    val onHypixel get() = hypixelMain || hypixelAlpha

    var hypixelMain = false
    var hypixelAlpha = false

    var profileName = ""

    var skyblockTime = SkyblockTime.now()

    fun init() {
        EventBus.subscribe(ClientTickEvent::class, ::onClientTick)
        EventBus.subscribe(WorldChangedEvent::class, ::onWorldChanged)
        EventBus.subscribe(ClientDisconnectEvent::class, ::onDisconnect)
        EventBus.subscribe(SkyblockYearChangeEvent::class, ::onYearChange)
    }

    private fun onClientTick(@Suppress("UNUSED_PARAMETER") event: ClientTickEvent) {
        tickCounter++
        if (tickCounter < TICKS_PER_UPDATE) return
        tickCounter = 0

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
        ChatUtils.addLocalChatMessage("Skyblock year has changed. Talk to Vincent to update dye rotation!", true)
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
        if (skyblockTime.year != SkyblockTime.now().year) EventBus.publish(SkyblockYearChangeEvent(SkyblockTime.now().year))

        skyblockTime = SkyblockTime.now()
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
        if (isFirstJoin && cachedIsInSkyblock) {
            ChatUtils.addLocalChatMessage("Thank you for using DyeAddons! Open your dye menu with /dyes.", true)
            isFirstJoin = false
        }
    }

}