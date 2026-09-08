package anlg.dyeaddons.settings

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.data.ColorCodes.GRAY
import anlg.dyeaddons.settings.categories.Debug
import anlg.dyeaddons.settings.categories.Dyes
import anlg.dyeaddons.settings.categories.General
import anlg.dyeaddons.settings.categories.QOL
import com.teamresourceful.resourcefulconfig.api.types.options.TranslatableValue
import com.teamresourceful.resourcefulconfigkt.api.ConfigKt
import net.minecraft.util.Util

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

        button {
            title = "Modrinth"
            description = "Find official releases and changelogs here."
            text = "Open"
            onClick {
                openLink("https://modrinth.com/mod/dyeaddons/versions")
            }
        }

        button {
            title = "GitHub"
            description = "Find source code here."
            text = "Open"
            onClick {
                openLink("https://github.com/AlexanderMartens/DyeAddons")
            }
        }

        button {
            title = "Dye Discord"
            description = "Dye community discord with guides, extra dye info, and more! Check out #mod-support for support and #mod-suggestions to make a suggestion or suggest a guide."
            text = "Join Discord"
            onClick {
                openLink("https://discord.gg/9kNJZf8Edv")
            }
        }

        // Categories
        category(General)
        category(Dyes)
        category(QOL)
        category(Debug)
    }

    fun save() = DyeAddons.INSTANCE.settings.save()

    private fun openLink(url: String) {
        Util.getPlatform().openUri(url)
    }
}
