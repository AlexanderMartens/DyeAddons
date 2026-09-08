package anlg.dyeaddons.features.dye

import anlg.dyeaddons.config.ConfigManager
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.utils.RngMeter

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
        get() = if (ConfigManager.data.config.meterProgressBar && RngMeter.getMeterProgress(dye) != null) {
            RngMeter.getMeterProgress(dye) ?: 0.0
        } else {
            ProfileStorage.lastPlayedProfile()?.dyeData[dye]?.progress ?: 0.0
        }
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