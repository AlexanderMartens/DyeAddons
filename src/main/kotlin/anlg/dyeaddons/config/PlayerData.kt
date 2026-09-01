package anlg.dyeaddons.config

data class PlayerData (
    val profiles: MutableMap<String, ProfileData> = mutableMapOf(),
    var lastPlayedProfile : String = "",
    var playerRankText : String = ""
)