package anlg.dyeaddons.utils

/**
 * Returns the color with a different brightness
 */
fun adjustBrightness(color: Int, amount: Float = 0.15f): Int {
    val a = 0xFF
    val r = (color shr 16) and 0xFF
    val g = (color shr 8) and 0xFF
    val b = color and 0xFF

    val isBright = (r + g + b) > 382

    fun shift(value: Int): Int = if (isBright) {
        (value * (1f - amount)).toInt().coerceIn(0, 255)
    } else {
        (255 - (255 - value) * (1f - amount)).toInt().coerceIn(0, 255)
    }

    return (a shl 24) or
            (shift(r) shl 16) or
            (shift(g) shl 8) or
            shift(b)
}