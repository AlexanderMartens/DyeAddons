package anlg.dyeaddons.config

import anlg.dyeaddons.data.CalcValue

data class DyeData(
    var dropped : Int = 0,
    var progress : Double = 0.0,
    val statistics : MutableMap<String, CalcValue> = mutableMapOf()
)