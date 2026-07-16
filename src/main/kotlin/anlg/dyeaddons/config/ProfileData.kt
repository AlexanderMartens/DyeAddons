package anlg.dyeaddons.config

import anlg.dyeaddons.data.Dye

class ProfileData {
    val dyeData : MutableMap<Dye, DyeData> = Dye.entries.associateWith { DyeData() }.toMutableMap()
    val dyeModifiers : MutableMap<String, Int> = mutableMapOf()
    val rngMeters : MutableMap<String, MeterData> = mutableMapOf()
    var visitorData :  List<VisitorData> = listOf()
}