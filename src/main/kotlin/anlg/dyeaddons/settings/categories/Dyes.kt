package anlg.dyeaddons.settings.categories

import com.teamresourceful.resourcefulconfigkt.api.CategoryKt

object Dyes : CategoryKt("Dyes") {

    var customDyeMessageToggle by boolean(false) {
        this.name = Translated("Toggle Custom Dye Message")
        this.description = Translated("Modifies the message when you get a dye. ")
    }

    var customDyeMessage by strings("&d&lWOW! {player} &6found a {dye} Dye&6!") {
        this.name = Translated("Custom Dye Message")
        this.description = Translated("Use {player} for the player name (including rank), {dye} for the dye name, {dropped} for how many dropped, and {progress} for total dye progress %. Keeps player and dye colors.")
    }
}