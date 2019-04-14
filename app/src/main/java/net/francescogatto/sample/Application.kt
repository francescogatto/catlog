package net.francescogatto.sample

import android.util.Log
import net.francescogatto.catlog.CatLog
import net.francescogatto.catlog.CatLogTree
import timber.log.Timber

/**
 * @author Francesco Gatto
 */
class Application : android.app.Application() {

    override fun onCreate() {
        super.onCreate()

        val catLog = CatLogTree.with(CatLog.builder(this)
            .logLevel(Log.DEBUG)
            //.debug(BuildConfig.DEBUG)
            .deleteLogOnStart(false)
            .size(5000)
            .takeScreenshot(true)
            .build())

        Timber.plant(Timber.DebugTree())
        Timber.plant(catLog)

        CatLog.instance.lastErrorLogFile

        /* // 1. Get the system handler.
         val systemHandler = Thread.getDefaultUncaughtExceptionHandler()

         // 2. Set the default handler as a dummy (so that crashlytics fallbacks to this one, once set)
         Thread.setDefaultUncaughtExceptionHandler { t, e -> /* do nothing */ }

         // 3. Setup crashlytics so that it becomes the default handler (and fallbacking to our dummy handler)
         //Fabric.with(this, Crashlytics())

         val fabricExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()

         // 4. Setup our handler, which tries to restart the app.
         Thread.setDefaultUncaughtExceptionHandler(AppExceptionHandler(systemHandler, fabricExceptionHandler, this)) */
    }

}
