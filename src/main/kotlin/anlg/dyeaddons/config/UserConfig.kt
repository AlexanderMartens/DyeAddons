package anlg.dyeaddons.config

import java.util.*

class UserConfig {
    var config : UserConfigData = UserConfigData()
    var players: MutableMap<UUID, PlayerData> = mutableMapOf()
}