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
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigInfo
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigOption
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
// @ConfigInfo needs compile-time constants, so DyeAddons.version can't be used directly - this
// swaps into a literal string at generate time (see stonecutter.gradle.kts, mirrors the official
// template's `/*$ marker*/ "value"` convention: nothing else allowed on the same line).
private const val MOD_FULL_VERSION = /*$ mod_full_version*/ "0.1.1+26.2"

@Config(value = "dyeaddons", categories = [General::class, Debug::class])
@ConfigInfo(
    title = "Dye Addons $MOD_FULL_VERSION by Anlg",
    description = "A mod for hypixel skyblock that focuses on dye trackers and qol."
)
class Settings {
    companion object {
        // Separator only stacks on a real @ConfigEntry field (confirmed in the library's own
        // JavaConfigParser/DemoConfig) - there's no standalone banner element. @ConfigOption.Hidden
        // hides this dummy entry's own row so only the separator text shows, matching the DSL's
        // root-level `separator {}` (placed here, not in General, so it shows before category
        // selection like the original).
        @JvmField
        @ConfigEntry(id = "welcomeSeparator", translation = "dyeaddons.config.welcomeSeparator.name")
        @ConfigOption.Hidden
        @ConfigOption.Separator(value = "Welcome to Dye Addons!", description = "Dye tracking and qol mod for Hypixel Skyblock.")
        var welcomeSeparator: Boolean = false

        fun save() = DyeAddons.INSTANCE.configurator.saveConfig(Settings::class.java)
    }
}
*///?}
