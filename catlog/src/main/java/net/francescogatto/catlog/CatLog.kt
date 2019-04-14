package net.francescogatto.catlog

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.annotation.CheckResult
import net.francescogatto.catlog.internal.*
import java.io.File
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * CatLog
 */

class CatLog internal constructor(val application: Application,
                                  val directory: File,
                                  val size: Int,
                                  val logLevel: Int,
                                  val debug: Boolean,
                                  private val deleteLogOnStart: Boolean,
                                  screenshotEnabled: Boolean,
                                  callbacks: Callbacks?) {
    companion object {

        internal const val LOG_NAME = "log.txt"
        internal const val ERROR_LOG_NAME = "logError.txt"
        internal const val SCREENSHOT_NAME = "screenshotError.jpg"
        internal const val SIZE = 5000 //KB
        internal const val LOG_LEVEL = Log.INFO
        private const val TAG = "CatLog"

        /**
         * Get Builder
         *
         * @param context Context
         * @return [Builder]
         */
        @CheckResult
        fun builder(application: Application): Builder {
            return Builder(application)
        }

        lateinit var instance: CatLog
            internal set
    }

    interface Callbacks {
        fun onSuccess()
        fun onFailure(throwable: Throwable)
    }


    private var logFile: File
    var logWriter: LogWriter
    val callbacks: Callbacks


    private val executorService: ExecutorService
    private var initialized = false

    init {
        this.callbacks = callbacks ?: DefaultCallbacks(debug)
        createDirIfNeeded(directory)
        executorService = Executors.newSingleThreadExecutor()
        logFile = File(directory.canonicalPath + File.separator + LOG_NAME)
        logWriter = LogWriterFile(application.baseContext, size, logFile)
        Util.setScreenshot(application, screenshotEnabled)
    }

    /**
     * initialize
     */
    internal fun initialize() {
        if (initialized) return
        initialized = true
        if (Util.wasAnError(application)) {
            saveErrorLogFile()
            Util.resetWasAnError(application)
        }
        Thread.setDefaultUncaughtExceptionHandler(AppExceptionHandler(Thread.getDefaultUncaughtExceptionHandler(), this, application))
        if (deleteLogOnStart) deleteCurrentLog()
    }


    fun log(priority: Int, tag: String?, message: String?) {
        checkInitialized()
        if (priority < logLevel) return
        if (message.isNullOrEmpty()) return
        if(debug) return
        executorService.execute(LogWritingTask(callbacks, logWriter, LogEntity.create(priority, tag
                ?: "", message, System.currentTimeMillis())))
    }


    /**
     * Terminate CatLog
     * This method will perform;
     * - close underlying [net.francescogatto.catlog.internal.DbOpenHelper]
     *
     *
     * After calling this method, all calls to this instance of [net.francescogatto.catlog.CatLog]
     * can produce exception or undefined behavior.
     */
    private fun terminate() {
        checkInitialized()
    }

    /**
     * deleteCurrentLog cache
     */
    fun deleteCurrentLog() {
        checkInitialized()
        logFile.delete()
    }

    /**
     * Get absolute path of log file
     *
     * @return absolute path of log file
     */
    private val pathLogFile: String
        get() {
            checkInitialized()
            try {
                return directory.canonicalPath + File.separator + LOG_NAME
            } catch (e: IOException) {
                throw CatLogFileException("Could not resolve the canonical path to the CatLog DB file: " + directory.absolutePath, e)
            }

        }

    /**
     * Get the error log file
     *
     * @return the file
     */
    private val errorLogFile: File
        get() {
            checkInitialized()
            try {
                return File(directory.canonicalPath + File.separator + ERROR_LOG_NAME)
            } catch (e: IOException) {
                throw CatLogFileException("Could not resolve the canonical path to the CatLog DB file: " + directory.absolutePath, e)
            }

        }

    /**
     * Get the screenshot file
     *
     * @return the file
     */
    private val screehshotFile: File
        get() {
            checkInitialized()
            try {
                return File(directory.canonicalPath + File.separator + SCREENSHOT_NAME)
            } catch (e: IOException) {
                throw CatLogFileException("Could not resolve the canonical path to the CatLog DB file: " + directory.absolutePath, e)
            }

        }

    private fun createDirIfNeeded(file: File) {
        if (!file.exists()) file.mkdir()
    }

    /**
     * throw if [CatLog.initialize] is not called.
     */
    fun checkInitialized(): Boolean {
        if (!initialized) throw IllegalStateException("CatLog#initialize is not called")
        return initialized
    }

    internal fun saveErrorLogFile() {
        logFile.renameTo(errorLogFile)
    }

    fun moveLog(destFile: File) {
        logFile.renameTo(destFile)
    }

    val lastErrorLogFile: File
        get() {
            return errorLogFile
        }

    fun clean() {
        directory.deleteRecursively()
    }


    internal class DefaultCallbacks(private val debug: Boolean) : Callbacks {

        override fun onSuccess() {
            // no-op
        }

        @SuppressLint("LogNotTimber")
        override fun onFailure(throwable: Throwable) {
            if (debug) Log.e(TAG, "Something happened while trying to save a log", throwable)
        }
    }

}
