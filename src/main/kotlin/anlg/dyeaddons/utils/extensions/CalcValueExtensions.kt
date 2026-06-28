package anlg.dyeaddons.utils.extensions

import anlg.dyeaddons.data.CalcValue

fun MutableMap<String, CalcValue>.incrementInt(
    key: String,
    amount: Int = 1
) {
    this[key] = CalcValue.IntVal((this[key]?.asInt() ?: 0) + amount)
}

fun MutableMap<String, CalcValue>.incrementFloat(
    key: String,
    amount: Float = 1f
) {
    this[key] = CalcValue.FloatVal((this[key]?.asFloat() ?: 0f) + amount)
}

fun MutableMap<String, CalcValue>.toggleBoolean(key: String) {
    this[key] = CalcValue.BoolVal(!(this[key]?.asBool() ?: false))
}

fun MutableMap<String, CalcValue>.setInt(key: String, value: Int) {
    this[key] = CalcValue.IntVal(value)
}

fun MutableMap<String, CalcValue>.setFloat(key: String, value: Float) {
    this[key] = CalcValue.FloatVal(value)
}