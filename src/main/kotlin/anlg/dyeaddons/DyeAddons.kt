package anlg.dyeaddons

import anlg.dyeaddons.config.ConfigManager
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.commands.DyesCommand
import anlg.dyeaddons.events.DyeEventHandler
import anlg.dyeaddons.events.MiscStatisticsHandler
import anlg.dyeaddons.utils.SkyblockUtils
import net.fabricmc.api.ClientModInitializer
import net.minecraft.client.Minecraft
import org.slf4j.LoggerFactory

class DyeAddons : ClientModInitializer {
	companion object {
		internal const val MOD_ID = "dyeaddons"

		internal val logger = LoggerFactory.getLogger(MOD_ID)

		@JvmField
		val mc: Minecraft = Minecraft.getInstance()

		@JvmStatic
		lateinit var INSTANCE: DyeAddons
			private set
	}

	init {
		INSTANCE = this
	}

	override fun onInitializeClient() {

		ConfigManager.init()
		EventBus.init()
		DyesCommand.init()

		SkyblockUtils.init()
		DyeEventHandler.init()
		MiscStatisticsHandler.init()

		logger.info("Dye Addons initialized!")
	}
}