package anlg.dyeaddons.settings.categories

import anlg.dyeaddons.config.ConfigManager
import anlg.dyeaddons.data.ColorCodes.*
import net.minecraft.util.Util
//? if <26.2 {
import com.teamresourceful.resourcefulconfigkt.api.CategoryKt
//?} else {
/*import com.teamresourceful.resourcefulconfig.api.annotations.Category
import com.teamresourceful.resourcefulconfig.api.annotations.Comment
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigButton
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry
*///?}

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

//? if <26.2 {
object Debug : CategoryKt("Debug") {

    var debugMessages by select(DebugCategories.ERROR) {
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
//?} else {
/*
// 26.2 has no resourcefulconfigkt build yet - this is the plain annotation-based Java API
// (config.wiki.teamresourceful.com) instead of the Kotlin DSL used on other versions.
// Fields must be `public static`, hence the @JvmField companion members.
@Category("Debug")
class Debug {
    companion object {
        @JvmField
        @ConfigEntry(id = "debugMessages", translation = "dyeaddons.config.debugMessages.name")
        @Comment(value = "", translation = "dyeaddons.config.debugMessages.comment")
        var debugMessages: Array<DebugCategories> = arrayOf(DebugCategories.ERROR)

        @JvmField
        @ConfigEntry(id = "alwaysHypixelMain", translation = "dyeaddons.config.alwaysHypixelMain.name")
        @Comment(value = "", translation = "dyeaddons.config.alwaysHypixelMain.comment")
        var alwaysHypixelMain: Boolean = false

        @JvmField
        @ConfigEntry(id = "alwaysOnSkyblock", translation = "dyeaddons.config.alwaysOnSkyblock.name")
        @Comment(value = "", translation = "dyeaddons.config.alwaysOnSkyblock.comment")
        var alwaysOnSkyblock: Boolean = false

        @JvmField
        @ConfigButton(title = "Open backups folder", text = "Open")
        val openBackupsFolder: Runnable = Runnable {
            val dir = ConfigManager.backupDir
            if (!dir.exists()) dir.mkdirs()
            Util.getPlatform().openUri(dir.toURI().toString())
        }
    }
}
*///?}
