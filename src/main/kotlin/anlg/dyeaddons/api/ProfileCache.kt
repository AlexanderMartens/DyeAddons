package anlg.dyeaddons.api

object ProfileCache {

    @Volatile
    var latestProfile: Profile? = null

    fun clear() {
        latestProfile = null
    }

    fun isAvailable(): Boolean {
        return latestProfile != null
    }
}