package anlg.dyeaddons.settings

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.data.ColorCodes.*
import anlg.dyeaddons.settings.categories.Debug
import anlg.dyeaddons.settings.categories.General
//? if <26.2 {
import com.teamresourceful.resourcefulconfig.api.types.options.TranslatableValue
import com.teamresourceful.resourcefulconfigkt.api.ConfigKt
//?} else {
/*import com.teamresourceful.resourcefulconfig.api.annotations.Config
*///?}

//? if <26.2 {
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
//?} else {
/*
// 26.2 has no resourcefulconfigkt build yet - this is the plain annotation-based Java API
// (config.wiki.teamresourceful.com) instead of the Kotlin DSL used on other versions.
// @ConfigInfo can't hold DyeAddons.version (annotation values must be compile-time constants),
// so the dynamic "name"/"description" from the Kotlin DSL is lost here.
@Config(value = "dyeaddons", categories = [General::class, Debug::class])
class Settings {
    companion object {
        fun save() = DyeAddons.INSTANCE.configurator.saveConfig(Settings::class.java)
    }
}
*///?}
