package net.francescogatto.catlog.internal

import android.app.Application
import androidx.annotation.CheckResult
import net.francescogatto.catlog.CatLog
import net.francescogatto.catlog.CatLog.Companion.LOG_LEVEL
import net.francescogatto.catlog.CatLog.Companion.SIZE
import net.francescogatto.catlog.CatLog.Companion.instance
import java.io.File

/**
 * Builder class for [net.francescogatto.catlog.CatLog]
 */
class Builder internal constructor(private val application: Application) {

    private var directory = application.filesDir
    private var size = SIZE
    private var logLevel = LOG_LEVEL
    private var debug = false
    private var callbacks: CatLog.Callbacks? = null
    private var deleteLogOnStart = true
    private var screenshotEnabled = true


    /**
     * Specify a directory where CatLog's Database file is stored.
     *
     * @param directory directory to save SQLite database file.
     * @return Builder
     */
    @CheckResult
    fun directory(directory: File): Builder {
        this.directory = directory
        return this
    }

    /**
     * Specify the max lines of log
     *
     *
     * Default is 5000kb.
     *
     * @param size max row number
     * @return Builder
     */
    @CheckResult
    fun size(size: Int): Builder {
        if (size < 0) throw IllegalArgumentException("size should be 0 or greater")
        this.size = size
        return this
    }

    /**
     * Specify minimum log level to save. The value should be any one of
     * [android.util.Log.VERBOSE],
     * [android.util.Log.DEBUG],
     * [android.util.Log.INFO],
     * [android.util.Log.WARN],
     * [android.util.Log.ERROR] or
     * [android.util.Log.ASSERT].
     *
     *
     * Default is [android.util.Log.INFO]
     *
     * @param logLevel log level
     * @return Builder
     */
    @CheckResult
    fun logLevel(logLevel: Int): Builder {
        this.logLevel = logLevel
        return this
    }

    /**
     * Enable/disable CatLog's debug logs(not saved to file).
     *
     *
     * Default is false.
     *
     * @param debug true: output logs. false: no debug logs
     * @return Builder
     */
    @CheckResult
    fun debug(debug: Boolean): Builder {
        this.debug = debug
        return this
    }

    /**
     * Enable/disable CatLog's screenshot on error
     *
     *
     * Default is true.
     *
     * @param value true: take screenshot false: no screenshot is taken
     * @return Builder
     */
    @CheckResult
    fun takeScreenshot(value: Boolean): Builder {
        this.screenshotEnabled = value
        return this
    }

    /**
     * Specify callbacks. This callbacks are called each time CatLog save a log.
     * This callbacks are called on background thread.
     *
     *
     * Default is [DefaultCallbacks]
     *
     * @param callbacks callbacks to execute.
     * @return Builder
     */
    @CheckResult
    fun callbacks(callbacks: CatLog.Callbacks): Builder {
        this.callbacks = callbacks
        return this
    }

    /**
     * Build CatLog. You need to call this method to use [CatLog]
     *
     * @return [CatLog]
     */
    @CheckResult
    fun build(): CatLog {
        instance = CatLog(application, directory, size, logLevel, debug, deleteLogOnStart, screenshotEnabled, callbacks)
        instance.initialize()
        return instance
    }

    /**
     * When application restart, the log file is deleted/not deleted and new logs are appended
     * (ATTENTION: the max file size is [size])
     *
     *
     * Default is true
     *
     * @return Builder
     */
    @CheckResult
    fun deleteLogOnStart(value: Boolean): Builder {
        deleteLogOnStart = value
        return this
    }
}