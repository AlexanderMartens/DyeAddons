package anlg.dyeaddons.settings.categories

import anlg.dyeaddons.config.ConfigManager
import com.teamresourceful.resourcefulconfigkt.api.CategoryKt
import com.teamresourceful.resourcefulconfigkt.api.ObservableEntry

object QOL : CategoryKt("QOL") {

    var puddleJumperToggle by ObservableEntry(
        boolean(ConfigManager.data.config.overlays["Text:Puddle Jumper"]?.toggled ?: false) {
            this.name = Translated("Puddle Jumper Timer")
            this.description = Translated("Tells you when your puddle jumpers is going to die. May be off by 1-2 seconds.")
        }
    ) { _, new ->
        if ((ConfigManager.data.config.overlays["Text:Puddle Jumper"]?.toggled ?: false) != new) {
            ConfigManager.data.config.toggleOverlay("Text:Puddle Jumper")
        }
    }

    var puddleJumperAnnouncementToggle by boolean(true) {
        this.name = Translated("Puddle Jumper Announcement")
        this.description = Translated("Plays an announcement when a puddle jumper is about to die")
    }

    var puddleJumperAnnouncementTimerTicks by int(80) {
        this.name = Translated("Ticks before death for announcement")
        this.description = Translated("How many ticks before the puddle jumper dies to play the announcement.")
        this.range = 0..120
        this.slider = true
    }
}