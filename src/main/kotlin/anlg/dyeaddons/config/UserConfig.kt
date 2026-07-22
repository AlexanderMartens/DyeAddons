package anlg.dyeaddons.config

import java.util.UUID

class UserConfig {
    var config : UserConfigData = UserConfigData()
    var players: MutableMap<UUID, PlayerData> = mutableMapOf()
}