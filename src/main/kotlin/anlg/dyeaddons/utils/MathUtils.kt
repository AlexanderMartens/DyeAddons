package anlg.dyeaddons.utils

import kotlin.math.exp

/**
 * Calculates the chance of at least x successes for a np lambda
 */
fun poissonAtLeast(x: Int, lambda: Double): Double {
    if (x <= 0) return 1.0
    if (lambda <= 0.0) return 0.0

    var term = 1.0
    var cumulative = term

    for (k in 1 until x) {
        term *= lambda / k
        cumulative += term
    }

    return (1.0 - exp(-lambda) * cumulative).coerceIn(0.0, 1.0)
}