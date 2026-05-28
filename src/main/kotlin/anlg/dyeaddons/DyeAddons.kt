package anlg.dyeaddons

import anlg.dyeaddons.events.commands.DyesCommand
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

		DyesCommand.initialize()
		logger.info("Dye Addons initialized!")
	}
}