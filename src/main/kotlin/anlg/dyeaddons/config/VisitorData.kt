package anlg.dyeaddons.config

import anlg.dyeaddons.utils.calc.Visitor

data class VisitorData (
    val name : String,
    val rarity : Visitor,
    var visits : Int,
)