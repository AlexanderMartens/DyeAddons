package anlg.dyeaddons

import anlg.dyeaddons.config.ConfigManager
import anlg.dyeaddons.events.BlockPacketHandler
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.commands.DyesCommand
import anlg.dyeaddons.events.DyeEventHandler
import anlg.dyeaddons.events.EntityDeathHandler
import anlg.dyeaddons.events.KillEventHandler
import anlg.dyeaddons.events.KismetHandler
import anlg.dyeaddons.events.MiningEventHandler
import anlg.dyeaddons.events.MiscStatisticsHandler
import anlg.dyeaddons.events.RngMeterHandler
import anlg.dyeaddons.events.dyes.AquamarineTracker
import anlg.dyeaddons.events.dyes.ArchfiendTracker
import anlg.dyeaddons.events.dyes.BoneTracker
import anlg.dyeaddons.events.dyes.BrickRedTracker
import anlg.dyeaddons.events.dyes.ByzantiumTracker
import anlg.dyeaddons.events.dyes.CarmineTracker
import anlg.dyeaddons.events.dyes.CeladonTracker
import anlg.dyeaddons.events.dyes.CelesteTracker
import anlg.dyeaddons.events.dyes.ChocolateTracker
import anlg.dyeaddons.events.dyes.CopperTracker
import anlg.dyeaddons.events.dyes.CyclamenTracker
import anlg.dyeaddons.events.dyes.DarkPurpleTracker
import anlg.dyeaddons.events.dyes.DungTracker
import anlg.dyeaddons.events.dyes.EmeraldTracker
import anlg.dyeaddons.events.dyes.FlameTracker
import anlg.dyeaddons.events.dyes.FossilTracker
import anlg.dyeaddons.events.dyes.FrostbittenTracker
import anlg.dyeaddons.events.dyes.HollyTracker
import anlg.dyeaddons.events.dyes.IcebergTracker
import anlg.dyeaddons.events.dyes.JadeTracker
import anlg.dyeaddons.events.dyes.LividTracker
import anlg.dyeaddons.events.dyes.MangoTracker
import anlg.dyeaddons.events.dyes.MatchaTracker
import anlg.dyeaddons.events.dyes.MidnightTracker
import anlg.dyeaddons.events.dyes.MochaTracker
import anlg.dyeaddons.events.dyes.MythologicalTracker
import anlg.dyeaddons.events.dyes.NadeshikoTracker
import anlg.dyeaddons.events.dyes.NecronTracker
import anlg.dyeaddons.events.dyes.NyanzaTracker
import anlg.dyeaddons.events.dyes.PearlescentTracker
import anlg.dyeaddons.events.dyes.PeltTracker
import anlg.dyeaddons.events.dyes.PeriwinkleTracker
import anlg.dyeaddons.events.dyes.PureBlackTracker
import anlg.dyeaddons.events.dyes.PureWhiteTracker
import anlg.dyeaddons.events.dyes.SangriaTracker
import anlg.dyeaddons.events.dyes.SecretTracker
import anlg.dyeaddons.events.dyes.TentacleTracker
import anlg.dyeaddons.events.dyes.TreasureTracker
import anlg.dyeaddons.events.dyes.WildStrawberryTracker
import anlg.dyeaddons.gui.overlay.Overlay
import anlg.dyeaddons.settings.Settings
import anlg.dyeaddons.settings.categories.Debug
import anlg.dyeaddons.settings.categories.DebugCategories
import anlg.dyeaddons.utils.ChatUtils
import anlg.dyeaddons.utils.SkyblockUtils
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
			logger.info(message)
			if (Debug.debugMessages.contains(DebugCategories.ALL) || Debug.debugMessages.contains(category))
				ChatUtils.addDebugChatMessage(message, category)
		}

		@JvmField
		val mc: Minecraft = Minecraft.getInstance()

		@JvmStatic
		lateinit var INSTANCE: DyeAddons
			private set
	}

	val configurator = Configurator("dyeaddons")

	//? if <26.2 {
	val settings = Settings.register(configurator)
	//?} else
	/*init { configurator.register(Settings::class.java) }*/

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

		// Event Handlers
		DyeEventHandler.init()
		MiscStatisticsHandler.init()
		MiningEventHandler.init()
		BlockPacketHandler.init()
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

		// Rendering
		HudElementRegistry.attachElementBefore(
			VanillaHudElements.CHAT,
			Identifier.fromNamespaceAndPath(MOD_ID, "before_chat"),
			Overlay
		)

		logger.info("$MOD_NAME initialized!")
	}

	private fun getModVersion(): String {
		return FabricLoader.getInstance().getModContainer(MOD_ID)
			.map { it.metadata.version.friendlyString }
			.orElse("unspecified") ?: ""
	}
}