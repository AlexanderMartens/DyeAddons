package anlg.dyeaddons.settings.categories

import com.teamresourceful.resourcefulconfigkt.api.CategoryKt

object Dyes : CategoryKt("Dyes") {

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
}