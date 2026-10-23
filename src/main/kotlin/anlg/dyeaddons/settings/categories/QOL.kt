package anlg.dyeaddons.settings.categories

import anlg.dyeaddons.config.ConfigManager
import anlg.dyeaddons.data.ModVersion
import com.teamresourceful.resourcefulconfigkt.api.CategoryKt
import com.teamresourceful.resourcefulconfigkt.api.ObservableEntry

object QOL : CategoryKt("QOL") {

    init {
        separator {
            this.title = "Puddle Jumper Timer"
        }
    }

    var puddleJumperToggle by ObservableEntry(
        boolean(ConfigManager.data.config.overlays["Text:Puddle Jumper"]?.toggled ?: false) {
            this.name = Translated("Puddle Jumper Timer")
            this.description = Translated("Tells you when your puddle jumpers is going to die. May be off by 1-2 seconds.")
            this.searchTerms = listOf(ModVersion.VERSION_0_2_1)
        }
    ) { _, new ->
        if ((ConfigManager.data.config.overlays["Text:Puddle Jumper"]?.toggled ?: false) != new) {
            ConfigManager.data.config.toggleOverlay("Text:Puddle Jumper")
        }
    }

    var puddleJumperAnnouncementToggle by boolean(false) {
        this.name = Translated("Puddle Jumper Announcement")
        this.description = Translated("Plays an announcement when a puddle jumper is about to die")
        this.searchTerms = listOf(ModVersion.VERSION_0_2_1)
    }

    var puddleJumperAnnouncementTimerTicks by int(80) {
        this.name = Translated("Ticks before death for announcement")
        this.description = Translated("How many ticks before the puddle jumper dies to play the announcement.")
        this.searchTerms = listOf(ModVersion.VERSION_0_2_1)
        this.range = 0..120
        this.slider = true
    }

    init {
        separator {
            this.title = "Offline Visitor Timer"
        }
    }

    var offlineVisitorMinutes by int(0) {
        this.name = Translated("Offline Visitor Announcement")
        this.description = Translated("Time in minutes to alert for offline visitor per visitor. Set to 0 to disable.")
        this.searchTerms = listOf(ModVersion.VERSION_0_3_0)
        this.range = 0..15
        this.slider = true
    }

    var offlineVisitorQueueLength by int(5) {
        this.name = Translated("Visitor Queue Length")
        this.description = Translated("Number of visitors that can be in your visitor queue at once.")
        this.searchTerms = listOf(ModVersion.VERSION_0_5_0)
        this.range = 5..10
        this.slider = true
    }

    var offlineVisitorsOutsideSkyblock by boolean(false) {
        this.name = Translated("Show announcement outside skyblock")
        this.description = Translated("Whether or not to play the announcement outside of skyblock.")
        this.searchTerms = listOf(ModVersion.VERSION_0_3_0)
    }
}