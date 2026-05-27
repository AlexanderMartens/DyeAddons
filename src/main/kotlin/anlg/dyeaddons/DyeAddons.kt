package anlg.dyeaddons

import net.fabricmc.api.ClientModInitializer
import org.slf4j.LoggerFactory

object DyeAddons : ClientModInitializer {
	internal const val MOD_ID = "dyeaddons"
    internal val logger = LoggerFactory.getLogger(MOD_ID)

	override fun onInitializeClient() {


		logger.info("Dye Addons initialized!")
	}
}