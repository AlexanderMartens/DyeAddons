package anlg.dyeaddons.data

import anlg.dyeaddons.gui.widgets.AbstractCalcWidget
import com.google.gson.JsonParseException
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

sealed class CalcValue {
    abstract val type : String

    data class IntVal(val value: Int) : CalcValue() { override val type = "int"}
    data class FloatVal(val value: Float) : CalcValue() { override val type = "float"}
    data class StringVal(val value: String) : CalcValue() { override val type = "string"}
    data class BoolVal(val value: Boolean) : CalcValue() { override val type = "bool"}
    data class LongVal(val value: Long) : CalcValue() { override val type = "long"}
    fun asInt() : Int? = (this as? CalcValue.IntVal)?.value
    fun asFloat(): Float? = (this as? CalcValue.FloatVal)?.value
    fun asString(): String? = (this as? CalcValue.StringVal)?.value
    fun asBool(): Boolean? = (this as? CalcValue.BoolVal)?.value
    fun asLong(): Long? = (this as? CalcValue.LongVal)?.value
}

class CalcValueAdapter : TypeAdapter<CalcValue>() {

    override fun write(
        out: JsonWriter,
        value: CalcValue?
    ) {
        if (value == null) {
            out.nullValue()
            return
        }

        out.beginObject()

        when (value) {
            is CalcValue.IntVal -> {
                out.name("type").value(value.type)
                out.name("value").value(value.value)
            }

            is CalcValue.FloatVal -> {
                out.name("type").value(value.type)
                out.name("value").value(value.value)
            }

            is CalcValue.StringVal -> {
                out.name("type").value(value.type)
                out.name("value").value(value.value)
            }

            is CalcValue.BoolVal -> {
                out.name("type").value(value.type)
                out.name("value").value(value.value)
            }

            is CalcValue.LongVal -> {
                out.name("type").value(value.type)
                out.name("value").value(value.value)
            }
        }

        out.endObject()
    }

    override fun read(`in`: JsonReader): CalcValue {
        `in`.beginObject()

        var type: String? = null
        var value: Any? = null

        while (`in`.hasNext()) {
            when (`in`.nextName()) {
                "type" -> type = `in`.nextString()

                "value" -> {
                    value = when (type) {
                        "int" -> `in`.nextInt()
                        "float" -> `in`.nextDouble().toFloat()
                        "string" -> `in`.nextString()
                        "bool" -> `in`.nextBoolean()
                        "long" -> `in`.nextLong()
                        else -> {
                            `in`.skipValue()
                            null
                        }
                    }
                }

                else -> `in`.skipValue()
            }
        }

        `in`.endObject()

        return when (type) {
            "int" -> CalcValue.IntVal(value as Int)
            "float" -> CalcValue.FloatVal(value as Float)
            "string" -> CalcValue.StringVal(value as String)
            "bool" -> CalcValue.BoolVal(value as Boolean)
            "long" -> CalcValue.LongVal(value as Long)

            else -> throw JsonParseException("Unknown CalcValue type: $type")
        }
    }
}

class CalcContext(
    widgets: Map<String, AbstractCalcWidget>
) {
    private val values: Map<String, CalcValue> =
        widgets.mapValues { it.value.getValue() }

    fun getInt(key: String, default: Int = 0): Int {
        return (values[key] as? CalcValue.IntVal)?.value ?: default
    }

    fun getFloat(key: String, default: Float = 0f): Float {
        return (values[key] as? CalcValue.FloatVal)?.value ?: default
    }

    fun getString(key: String, default: String = ""): String {
        return (values[key] as? CalcValue.StringVal)?.value ?: default
    }

    fun getBoolean(key: String, default: Boolean = false): Boolean {
        return (values[key] as? CalcValue.BoolVal)?.value ?: default
    }

    fun getLong(key: String, default: Long = 0L): Long {
        return (values[key] as? CalcValue.LongVal)?.value ?: default
    }
}

object Parsers {
    val INT = { s: String -> CalcValue.IntVal(s.toIntOrNull() ?: 0) }
    val FLOAT = { s: String -> CalcValue.FloatVal(s.toFloatOrNull() ?: 0f) }
    val STRING = { s: String -> CalcValue.StringVal(s) }
    val BOOL = { s: String -> CalcValue.BoolVal( s.lowercase() in setOf("true", "1", "yes"))}
    val LONG = { s: String -> CalcValue.LongVal(s.toLongOrNull() ?: 0) }
}