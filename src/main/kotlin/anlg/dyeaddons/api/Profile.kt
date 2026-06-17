package anlg.dyeaddons.api

import com.google.gson.JsonObject
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
    @SerializedName("player_stats") val playerStats : ApiPlayerStats? = null,
    @SerializedName("player_data") val playerData : ApiPlayerData? = null,
    @SerializedName("dungeons") val dungeons : JsonObject? = null,
    @SerializedName("slayer") val slayer : JsonObject? = null,
    @SerializedName("collection") val collection : Map<String, Long> = emptyMap(),
    @SerializedName("glacite_player_data") val glacitePlayerData : JsonObject? = null,
    @SerializedName("mining_core") val miningCore : JsonObject? = null,
    @SerializedName("bestiary") val bestiary : JsonObject? = null,
    @SerializedName("attributes") val attributes : JsonObject? = null,
    @SerializedName("experimentation") val experimentation : JsonObject? = null,
    @SerializedName("nether_island_player_data") val netherData : JsonObject? = null,
    @SerializedName("events") val events : JsonObject? = null,
)

fun Member.sumOfBestiaryKills(killList : List<String>) : Int {
    var sum = 0
    killList.forEach { kill ->
        sum += this.bestiary?.objPath("kills")?.get(kill)?.asInt ?: 0
    }
    return sum
}

data class ApiPlayerStats (
    @SerializedName("kills") val kills : Map<String, Int> = emptyMap(),
    @SerializedName("rift") val rift : JsonObject? = null,
    @SerializedName("items_fished") val itemsFished : Map<String, Int> = emptyMap(),
)

fun ApiPlayerStats.sumOfKills(killList : List<String>) : Int {
    return this.kills.filterKeys { it in killList }.values.sumOf { it }
}

data class ApiPlayerData (
    @SerializedName("perks") val perks : Map<String, Int> // Essence shop perks
)

fun JsonObject.objPath(vararg keys : String) : JsonObject? {
    var current : JsonObject = this

    for (key in keys) {
        val next = current.getAsJsonObject(key) ?: return null
        current = next
    }

    return current
}