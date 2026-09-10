package anlg.dyeaddons

//import anlg.dyeaddons.events.MiningEventHandler
import anlg.dyeaddons.config.ConfigManager
import anlg.dyeaddons.events.*
import anlg.dyeaddons.events.commands.DyesCommand
import anlg.dyeaddons.events.dyes.*
import anlg.dyeaddons.features.dye.CustomDyeMessage
import anlg.dyeaddons.features.dye.DyeRotationStats
import anlg.dyeaddons.features.qol.OfflineVisitorTimer
import anlg.dyeaddons.features.qol.PuddleJumperTimer
import anlg.dyeaddons.gui.overlay.AnnouncementOverlay
import anlg.dyeaddons.gui.overlay.Overlay
import anlg.dyeaddons.settings.Settings
import anlg.dyeaddons.settings.categories.Debug
import anlg.dyeaddons.settings.categories.DebugCategories
import anlg.dyeaddons.utils.ChatUtils
import anlg.dyeaddons.utils.KeyBindUtils
import anlg.dyeaddons.utils.PlayerRankUtils
import anlg.dyeaddons.utils.SkyblockUtils
import anlg.dyeaddons.utils.SoundUtils
import com.teamresourceful.resourcefulconfig.api.loader.Configurator
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.Minecraft
import net.minecraft.resources.Identifier
import org.slf4j.LoggerFactory

class DyeAddons : ClientModInitializer {
	companion object {
		internal const val MOD_ID = "dyeaddons"
		internal const val MOD_NAME = "Dye Addons"

		internal val logger = LoggerFactory.getLogger(MOD_ID)

		lateinit var version: String

		fun debug(message: String, category: DebugCategories = DebugCategories.OTHER) {
			if (Debug.debugMessages.contains(DebugCategories.ALL) || Debug.debugMessages.contains(category)) {
				logger.info(message)
				if (Debug.debugMessagesInChat) {
					ChatUtils.addDebugChatMessage(message, category)
				}
			}
		}

		@JvmField
		val mc: Minecraft = Minecraft.getInstance()

		@JvmStatic
		lateinit var INSTANCE: DyeAddons
			private set
	}

	val configurator = Configurator("dyeaddons")

	val settings = Settings.register(configurator)

	init {
		INSTANCE = this
	}

	override fun onInitializeClient() {
		version = getModVersion()

		ConfigManager.init()
		EventBus.init()
		DyesCommand.init()

		// Utils
		SkyblockUtils.init()
		KeyBindUtils.init()
		PlayerRankUtils.init()
		SoundUtils.init()

		// Event Handlers
		DyeEventHandler.init()
		MiscStatisticsHandler.init()
		// MiningEventHandler.init() Removed for now, will add back and fix crash if any dye needs this
		PacketHandler.init()
		EntityDeathHandler.init()
		KillEventHandler.init()
		RngMeterHandler.init()
		KismetHandler.init()

		// Dye Trackers
		AquamarineTracker.init()
		ArchfiendTracker.init()
		BoneTracker.init()
		BrickRedTracker.init()
		ByzantiumTracker.init()
		CarmineTracker.init()
		CeladonTracker.init()
		CelesteTracker.init()
		ChocolateTracker.init()
		CopperTracker.init()
		CyclamenTracker.init()
		DarkPurpleTracker.init()
		DungTracker.init()
		EmeraldTracker.init()
		FlameTracker.init()
		FrostbittenTracker.init()
		FossilTracker.init()
		HollyTracker.init()
		IcebergTracker.init()
		JadeTracker.init()
		LividTracker.init()
		MangoTracker.init()
		MatchaTracker.init()
		MidnightTracker.init()
		MochaTracker.init()
		MythologicalTracker.init()
		NadeshikoTracker.init()
		NecronTracker.init()
		NyanzaTracker.init()
		PeltTracker.init()
		PearlescentTracker.init()
		PeriwinkleTracker.init()
		PureBlackTracker.init()
		PureWhiteTracker.init()
		SangriaTracker.init()
		SecretTracker.init()
		TentacleTracker.init()
		TreasureTracker.init()
		WildStrawberryTracker.init()

		// Features
		CustomDyeMessage.init()
		DyeRotationStats.init()
		PuddleJumperTimer.init()
		AnnouncementOverlay.init()
		OfflineVisitorTimer.init()

		// Rendering
		HudElementRegistry.attachElementBefore(
			VanillaHudElements.CHAT,
			Identifier.fromNamespaceAndPath(MOD_ID, "before_chat"),
			Overlay
		)
		Overlay.refreshOverlays()

		logger.info("$MOD_NAME initialized!")
	}

	private fun getModVersion(): String {
		return FabricLoader.getInstance().getModContainer(MOD_ID)
			.map { it.metadata.version.friendlyString }
			.orElse("unspecified") ?: ""
	}
}