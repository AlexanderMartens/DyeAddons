package anlg.dyeaddons.settings.categories

import anlg.dyeaddons.utils.SoundUtils
import com.teamresourceful.resourcefulconfigkt.api.CategoryKt

object Dyes : CategoryKt("Dyes") {

    var progressFormat by string("2%") {
        this.name = Translated("Progress format")
        this.description = Translated("First character is decimal places 0-9. Rest is added after your progress. If % is second character, then the progress will be multiplied by 100. For example \"2%\" = 102.25%, \"3x\" = 1.023x.")
    }

    var customDyeMessageToggle by boolean(false) {
        this.name = Translated("Toggle Custom Dye Message")
        this.description = Translated("Modifies the message when you get a dye. ")
    }

    var customDyeMessage by strings("&d&lWOW! {player} &6found {an} {dye} Dye&6!") {
        this.name = Translated("Custom Dye Message")
        this.description = Translated("Use {player} for the player name (including rank), {dye} for the dye name, {dropped} for how many dropped, {progress} for total dye progress %, {since} for progress % since last dye drop, and {an} for proper a/an for the dye. Keeps player and dye colors.")
    }

    var fakeDyeDropRate by double(0.0) {
        this.name = Translated("Dye Jump Scare")
        this.description = Translated("The relative rate of getting a dye jump scare. Plays the dye drop sound and fake message when you roll a dye. 10 = 10x more likely, 0.1 = 10x less likely than a real dye. Set to 0 to disable.")
    }

    init {
        separator {
            this.title = "Time since last dye drop message"
        }
    }

    var timeSinceLastDyeDrop by boolean(false) {
        this.name = Translated("Show time since last dye")
        this.description = Translated("Shows a chat message of how long since your last dye when you get one.")
    }

    var timeSinceLastDyeWithShop by boolean(false) {
        this.name = Translated("Include shop dyes since last")
        this.description = Translated("Takes into consideration of purchased dyes.")
    }

    var timeSinceLastUnique by boolean(false) {
        this.name = Translated("Show time since last unique")
        this.description = Translated("Shows a chat message of how long since your last unique dye.")
    }

    var timeSinceSameDye by boolean(false) {
        this.name = Translated("Show time since last same dye")
        this.description = Translated("Shows a chat message of how long since your last dye of the same type.")
    }

    init {
        separator {
            this.title = "Custom Dye Sound"
        }
    }

    var customDyeSound by string("") {
        this.name = Translated("Custom Dye Sound")
        this.description = Translated("Plays a custom sound when getting a dye. Put the name of the .ogg file in your config/dyeaddons/sounds folder here.")
    }

    val customDyeSoundOnBoughtDyes by boolean(false) {
        this.name = Translated("Also play for purchased dyes")
        this.description = Translated("Plays the custom dye sound for purchased dyes (pure white/black, chocolate, bingo blue, dark purple)")
    }

    init {
        button {
            title = "Test Custom Dye Sound"
            description = "Plays the custom dye sound to test if it works."
            text = "Play"

            onClick {
                SoundUtils.playCustomUserSound(customDyeSound)
            }
        }
    }

    init {
        separator {
            this.title = "Medal Integration"
            this.description = "Saves Medal clips on dye drops. Requires Medal to be running locally. Make sure you have auto clipping enabled in medal for minecraft."
        }
    }

    var medalClipToggle by boolean(false) {
        this.name = Translated("Enable Medal Clips")
        this.description = Translated("Saves medal clips on some events.")
    }

    var medalClipDyes by boolean(false) {
        this.name = Translated("Clip Dye Drops")
        this.description = Translated("Saves a medal clip when you drop a dye.")
    }

    var medalClipPurchaseDyes by boolean(false) {
        this.name = Translated("Clip Purchased Dyes")
        this.description = Translated("Saves medal clips for purchased dyes (Chocolate, Pure White/Black, Bingo Blue).")
    }

    var medalClipDurationSeconds by int(30) {
        this.range = 1..120
        this.slider = true
        this.name = Translated("Clip Duration (seconds)")
        this.description = Translated("How long Medal clips should be.")
    }

    var medalCaptureDelayMs by int (10000) {
        this.range = 0..60000
        this.slider = true
        this.name = Translated("Capture Delay (ms)")
        this.description = Translated("Delay before Medal captures clips")
    }
}