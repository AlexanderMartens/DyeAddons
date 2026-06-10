package anlg.dyeaddons.config

import java.util.UUID

class Config {
    val config : ConfigData = ConfigData()
    val players: MutableMap<UUID, PlayerData> = mutableMapOf()
}