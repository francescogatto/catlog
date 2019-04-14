package net.francescogatto.catlog

import android.app.Activity
import android.app.Application
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import net.francescogatto.catlog.internal.Util

class AppExceptionHandler(private val systemHandler: Thread.UncaughtExceptionHandler,
                          private val catLog: CatLog,
                          application: Application) : Thread.UncaughtExceptionHandler {


    private var lastStartedActivity: Activity? = null

    private var startCount = 0

    init {
        application.registerActivityLifecycleCallbacks(
                object : Application.ActivityLifecycleCallbacks {
                    override fun onActivityPaused(activity: Activity?) {
                        // empty
                    }

                    override fun onActivityResumed(activity: Activity?) {
                        // empty
                    }

                    override fun onActivityStarted(activity: Activity?) {
                        startCount++
                        lastStartedActivity = activity
                    }

                    override fun onActivityDestroyed(activity: Activity?) {
                        // empty
                    }

                    override fun onActivitySaveInstanceState(activity: Activity?,
                                                             outState: Bundle?) {
                        // empty
                    }

                    override fun onActivityStopped(activity: Activity?) {
                        startCount--
                        if (startCount <= 0) {
                            lastStartedActivity = null
                        }
                    }

                    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
                        // empty
                    }
                })
    }

    override fun uncaughtException(t: Thread?, e: Throwable) {
        //save the file in another file
        catLog.saveErrorLogFile()
        lastStartedActivity?.let {
            Util.takeScreenshotForScreen(it)
        }
        Util.setWasAnError(catLog.application, true)
        systemHandler.uncaughtException(t, e)
    }

}