package anlg.dyeaddons.utils

import java.util.concurrent.TimeUnit

object StringUtils {


    /**
     * Formats the time of milliseconds to time elapsed string. (e.g. 1 day, 20 hours, 24 minutes, 13 seconds)
     * @param time The time in milliseconds
     */
    fun formatTime(time: Long): String {
        if (time == Long.MAX_VALUE) return "Infinity"
        if (time == 0L) return "0 seconds"

        val days = TimeUnit.MILLISECONDS.toDays(time)
        val hours = TimeUnit.MILLISECONDS.toHours(time) % 24
        val minutes = TimeUnit.MILLISECONDS.toMinutes(time) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(time) % 60
        return buildList {
            if (days > 0) add("$days ${if (days == 1L) "day" else "days"}")
            if (hours > 0) add("$hours ${if (hours == 1L) "hour" else "hours"}")
            if (minutes > 0) add("$minutes ${if (minutes == 1L) "minute" else "minutes"}")
            if (seconds > 0) add("$seconds ${if (seconds == 1L) "second" else "seconds"}")
        }.joinToString(", ")
    }

    /**
     * Formats the time of milliseconds to time elapsed string. (e.g. 1d 20h 24m 13s)
     * @param time The time in milliseconds
     */
    fun formatTimeShort(time: Long): String {
        if (time == Long.MAX_VALUE) return "Infinity"
        if (time == 0L) return "0s"

        val days = TimeUnit.MILLISECONDS.toDays(time)
        val hours = TimeUnit.MILLISECONDS.toHours(time) % 24
        val minutes = TimeUnit.MILLISECONDS.toMinutes(time) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(time) % 60

        val timerText = buildString {
            if (days > 0) append("${days}d")
            if (hours > 0) append("${hours}h ")
            if (minutes > 0) append("${minutes}m ")
            if (seconds > 0) append("${seconds}s")
        }

        return timerText
    }

    fun toOrdinal(number: Int): String {
        val suffix = when {
            (number % 100 in 11..13) -> "th"
            (number % 10) == 1 -> "st"
            (number % 10) == 2 -> "nd"
            (number % 10) == 3 -> "rd"
            else -> "th"
        }
        return "$number$suffix"
    }
}