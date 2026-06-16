package anlg.dyeaddons.api

import com.google.gson.annotations.SerializedName
import java.util.UUID

data class Profile(
    @SerializedName("members") val members : Map<String, Member> = emptyMap(),
    @SerializedName("game_mode") val gameMode : String? = null,
    @SerializedName("cute_name") val profileName : String? = null,
    @SerializedName("selected") val selected : Boolean = false,
)

fun Profile.getMember(uuid : UUID?) : Member? {
    val cleanUuid = uuid.toString().replace("-", "")
    return this.members[cleanUuid]
}

data class Member (
    @SerializedName("player_stats") val playerStats : ApiPlayerStats? = null
)

data class ApiPlayerStats (
    @SerializedName("kills") val kills : Map<String, Int>
)