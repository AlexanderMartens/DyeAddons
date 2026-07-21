package anlg.dyeaddons.config

import java.util.UUID

class Config {
    var config : ConfigData = ConfigData()
    var players: MutableMap<UUID, PlayerData> = mutableMapOf()
}