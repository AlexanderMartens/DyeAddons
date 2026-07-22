package anlg.dyeaddons.settings

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.data.ColorCodes.*
import anlg.dyeaddons.settings.categories.Debug
import anlg.dyeaddons.settings.categories.General
import com.teamresourceful.resourcefulconfig.api.types.options.TranslatableValue
import com.teamresourceful.resourcefulconfigkt.api.ConfigKt

object Settings : ConfigKt("${DyeAddons.MOD_ID}/config"){
    override val name: TranslatableValue
        get() = Literal("${DyeAddons.MOD_NAME} ${DyeAddons.version} by Anlg")

    override val description: TranslatableValue
        get() = Literal("A mod for hypixel skyblock that focuses on dye trackers and qol.")

    init {
        separator {
            title = "Welcome to ${DyeAddons.MOD_NAME}!"
            description = "${GRAY}Dye tracking and qol mod for Hypixel Skyblock."
        }

        // Categories
        category(General)
        category(Debug)
    }

    fun save() = DyeAddons.INSTANCE.settings.save()
}