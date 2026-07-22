package anlg.dyeaddons.settings.categories

import anlg.dyeaddons.config.ConfigManager
import anlg.dyeaddons.data.ColorCodes.*
import com.teamresourceful.resourcefulconfigkt.api.CategoryKt
import net.minecraft.util.Util

enum class DebugCategories(val displayName : String) {
    ALL("${WHITE}All"),
    OTHER("${GRAY}Other"),
    ERROR("${RED}Error"),
    FILESYSTEM("${DARK_AQUA}File System"),
    DYE_EVENT("${GOLD}Dye Event"),
    DYE_PROGRESS_EVENT("${AQUA}Dye Progress Event"),
    MENU_EVENT("${GREEN}Menu Event"),
    KILL_EVENT("${DARK_RED}Kill Event"),
}


object Debug : CategoryKt("Debug") {

    var debugMessages by select(DebugCategories.ALL, *DebugCategories.entries.toTypedArray()) {
        this.name = Translated("Select debug messages to see in chat")
        this.description = Translated("All will select every debug message, Other will select messages without a category")
    }

    var alwaysHypixelMain by boolean(false) {
        this.name = Translated("Always on Hypixel Main")
        this.description = Translated("Makes mod think you are always on the main hypixel server")
    }

    var alwaysOnSkyblock by boolean(false) {
        this.name = Translated("Always on Skyblock")
        this.description = Translated("Makes mod thnk you are alawys on skyblock")
    }

    init {
        button {
            title = "Open backups folder"
            description = "Opens the folder where data backups are stored on game close."
            text = "Open"
            onClick {
                val dir = ConfigManager.backupDir
                if (!dir.exists()) dir.mkdirs()
                Util.getPlatform().openUri(dir.toURI().toString())
            }
        }
    }
}