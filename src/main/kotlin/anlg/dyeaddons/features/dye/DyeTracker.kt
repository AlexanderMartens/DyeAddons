package anlg.dyeaddons.features.dye

import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.Dye
import java.util.concurrent.TimeUnit

enum class TrackerState {
    NOT_STARTED,
    RUNNING,
    STOPPED
}

class DyeTracker(val dye: Dye) {

    companion object {
        val trackers = Dye.entries.associateWith { DyeTracker(it) }
    }

    private val currentProgress
        get() = ProfileStorage.lastPlayedProfile()?.dyeData[dye]?.progress ?: 0.0
    private var accumulatedProgress = 0.0
    private var resumeProgress = 0.0

    private var startTimeMs = 0L
    private var accumulatedTimeMs = 0L
    private var isRunning = false

    val timeElapsedMS: Long
        get() = if (isRunning) {
            accumulatedTimeMs + (System.currentTimeMillis() - startTimeMs)
        } else {
            accumulatedTimeMs
        }

    var state = TrackerState.NOT_STARTED

    /**
     * Returns ETA in Milliseconds
     */
    fun getETA(): Long {
        val totalProgress = if (isRunning) {
            accumulatedProgress + (currentProgress - resumeProgress)
        } else {
            accumulatedProgress
        }

        if (totalProgress <= 0.0) return Long.MAX_VALUE

        return (timeElapsedMS / (totalProgress)).toLong()
    }

    /**
     * Returns ETA in hh:mm:ss format
     */
    fun getFormattedETA(): String {
        val eta = getETA()
        if (eta == Long.MAX_VALUE) return "Infinity"

        val hours = TimeUnit.MILLISECONDS.toHours(eta)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(eta) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(eta) % 60

        val timerText = buildString {
            if (hours > 0) append("${hours}h ")
            if (minutes > 0) append("${minutes}m ")
            append("${seconds}s")
        }

        return timerText
    }

    /**
     * Returns ETA in hh:mm:ss format
     */
    fun getFormattedTimeElapsed(): String {
        val hours = TimeUnit.MILLISECONDS.toHours(timeElapsedMS)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeElapsedMS) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(timeElapsedMS) % 60

        val timerText = buildString {
            if (hours > 0) append("${hours}h ")
            if (minutes > 0) append("${minutes}m ")
            append("${seconds}s")
        }

        return timerText
    }

    fun start() {
        accumulatedTimeMs = 0
        accumulatedProgress = 0.0

        resumeProgress = currentProgress
        startTimeMs = System.currentTimeMillis()

        isRunning = true
        state = TrackerState.RUNNING
    }

    fun stop() {
        if (isRunning) {
            accumulatedTimeMs += System.currentTimeMillis() - startTimeMs
            accumulatedProgress += currentProgress - resumeProgress

            isRunning = false
        }

        state = TrackerState.STOPPED
    }

    fun resume() {
        if (!isRunning) {
            resumeProgress = currentProgress
            startTimeMs = System.currentTimeMillis()
            isRunning = true

        }

        state = TrackerState.RUNNING
    }

    fun reset() {
        accumulatedTimeMs = 0
        accumulatedProgress = 0.0

        resumeProgress = currentProgress

        isRunning = false
        state = TrackerState.NOT_STARTED
    }
}