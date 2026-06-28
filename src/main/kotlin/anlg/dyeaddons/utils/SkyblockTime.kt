package anlg.dyeaddons.utils

import kotlin.time.Duration

/**
 * SkyBlockTime Utility
 * Originally in NEU; copied and modified with permission.
 * @author hannibal, nea89o
 * Modified further by walker
 */
data class SkyblockTime(
    val year: Int = 1,
    val month: Int = 1,
    val day: Int = 1,
    val hour: Int = 0,
    val minute: Int = 0,
    val second: Int = 0,
) : Comparable<SkyblockTime> {

    val monthName get() = monthName(month)

    fun toMillis(): Long =
        calculateTimeInSkyBlockMillis(year, month, day, hour, minute, second) + SKYBLOCK_EPOCH_START_MILLIS

    override fun compareTo(other: SkyblockTime): Int {
        return when {
            year != other.year -> year.compareTo(other.year)
            month != other.month -> month.compareTo(other.month)
            day != other.day -> day.compareTo(other.day)
            hour != other.hour -> hour.compareTo(other.hour)
            minute != other.minute -> minute.compareTo(other.minute)
            else -> second.compareTo(other.second)
        }
    }

    operator fun plus(duration: Duration): SkyblockTime {
        val millis = toMillis() + duration.inWholeMilliseconds
        return fromTimeMark(millis)
    }

    companion object {
        const val SKYBLOCK_EPOCH_START_MILLIS = 1559829300000L // Day 1, Year 1
        const val SKYBLOCK_YEAR_MILLIS = 124 * 60 * 60 * 1000L
        const val SKYBLOCK_MONTH_MILLIS = SKYBLOCK_YEAR_MILLIS / 12
        const val SKYBLOCK_DAY_MILLIS = SKYBLOCK_MONTH_MILLIS / 31
        const val SKYBLOCK_HOUR_MILLIS = SKYBLOCK_DAY_MILLIS / 24
        private const val SKYBLOCK_MINUTE_MILLIS = SKYBLOCK_HOUR_MILLIS / 60
        private const val SKYBLOCK_SECOND_MILLIS = SKYBLOCK_MINUTE_MILLIS / 60

        fun fromTimeMark(timeMark: Long): SkyblockTime =
            calculateSkyBlockTime(timeMark - SKYBLOCK_EPOCH_START_MILLIS)

        fun fromSBYear(year: Int): SkyblockTime =
            fromTimeMark(SKYBLOCK_EPOCH_START_MILLIS + (SKYBLOCK_YEAR_MILLIS * year))

        fun fromAbsoluteDay(absDay: Int): SkyblockTime {
            val millis = SKYBLOCK_EPOCH_START_MILLIS + absDay * SKYBLOCK_DAY_MILLIS
            return fromTimeMark(millis)
        }

        fun now(): SkyblockTime = fromTimeMark(System.currentTimeMillis())

        private fun calculateSkyBlockTime(realMillis: Long): SkyblockTime {
            var remainingMillis = realMillis
            val year = getUnit(remainingMillis, SKYBLOCK_YEAR_MILLIS)
            remainingMillis %= SKYBLOCK_YEAR_MILLIS
            val month = getUnit(remainingMillis, SKYBLOCK_MONTH_MILLIS) + 1
            remainingMillis %= SKYBLOCK_MONTH_MILLIS
            val day = getUnit(remainingMillis, SKYBLOCK_DAY_MILLIS) + 1
            remainingMillis %= SKYBLOCK_DAY_MILLIS
            val hour = getUnit(remainingMillis, SKYBLOCK_HOUR_MILLIS)
            remainingMillis %= SKYBLOCK_HOUR_MILLIS
            val minute = getUnit(remainingMillis, SKYBLOCK_MINUTE_MILLIS)
            remainingMillis %= SKYBLOCK_MINUTE_MILLIS
            val second = getUnit(remainingMillis, SKYBLOCK_SECOND_MILLIS)
            return SkyblockTime(year, month, day, hour, minute, second)
        }

        private fun getUnit(millis: Long, factor: Long): Int = (millis / factor).toInt()

        private fun calculateTimeInSkyBlockMillis(
            year: Int,
            month: Int,
            day: Int,
            hour: Int,
            minute: Int,
            second: Int,
        ): Long {
            var time = 0L
            time += year * SKYBLOCK_YEAR_MILLIS
            time += (month - 1) * SKYBLOCK_MONTH_MILLIS
            time += (day - 1) * SKYBLOCK_DAY_MILLIS
            time += hour * SKYBLOCK_HOUR_MILLIS
            time += minute * SKYBLOCK_MINUTE_MILLIS
            time += second * SKYBLOCK_SECOND_MILLIS
            return time
        }

        fun monthName(month: Int): String {
            val prefix = when ((month - 1) % 3) {
                0 -> "Early "
                1 -> ""
                2 -> "Late "
                else -> "Undefined!"
            }

            val name = when ((month - 1) / 3) {
                0 -> "Spring"
                1 -> "Summer"
                2 -> "Autumn"
                3 -> "Winter"
                else -> "Undefined!"
            }

            return prefix + name
        }

        fun daySuffix(n: Int): String {
            return if (n in 11..13) {
                "th"
            } else when (n % 10) {
                1 -> "st"
                2 -> "nd"
                3 -> "rd"
                else -> "th"
            }
        }

        fun getSBMonthByName(month: String): Int {
            var monthNr = 0
            for (i in 1..12) {
                val monthName = monthName(i)
                if (month == monthName) {
                    monthNr = i
                }
            }
            return monthNr
        }
    }
}
