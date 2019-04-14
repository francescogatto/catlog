package net.francescogatto.catlog.internal

import android.app.Activity
import android.app.Application
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.view.View.MeasureSpec
import net.francescogatto.catlog.CatLog
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


internal object Util {

    fun priorityString(priority: Int) = when (priority) {
        Log.VERBOSE -> "VERBOSE"
        Log.DEBUG -> "DEBUG"
        Log.INFO -> "INFO"
        Log.WARN -> "WARN"
        Log.ERROR -> "ERROR"
        Log.ASSERT -> "ASSERT"
        else -> "UNKNOWN"
    }

    /**
     * Measures and takes a screenshot of the provided [View].
     *
     * @param view The view of which the screenshot is taken
     * @return A [Bitmap] for the taken screenshot.
     */
    fun takeScreenshotForView(view: View): Bitmap {
        //FIXME remove deprecated code
        view.measure(MeasureSpec.makeMeasureSpec(view.getWidth(), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(view.height, MeasureSpec.EXACTLY))
        view.layout(view.x.toInt(), view.y.toInt(), view.x.toInt() + view.measuredWidth, view.y.toInt() + view.measuredHeight)
        view.isDrawingCacheEnabled = true
        view.buildDrawingCache(true)
        val bitmap = Bitmap.createBitmap(view.drawingCache)
        view.isDrawingCacheEnabled = false
        storeBitmap(bitmap, CatLog.instance.directory.canonicalPath + File.separator + CatLog.SCREENSHOT_NAME)
        return bitmap
    }

    /**
     * Stores the given [Bitmap] to a path on the device.
     *
     * @param bitmap   The [Bitmap] that needs to be stored
     * @param filePath The path in which the bitmap is going to be stored.
     */
    private fun storeBitmap(bitmap: Bitmap, filePath: String) {
        val imageFile = File(filePath).apply {
            parentFile.mkdirs()
        }
        try {
            val fout = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fout)
            fout.flush()
            fout.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }


    fun takeScreenshotForScreen(activity: Activity) {
        if (isScreenshotEnabled(activity.application))
            takeScreenshotForView(activity.window.decorView.rootView)
    }

    fun wasAnError(application: Application): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(application).getBoolean("cl_error", false)
    }

    fun resetWasAnError(application: Application) {
        PreferenceManager.getDefaultSharedPreferences(application).edit().putBoolean("cl_error", false).apply()
    }

    //Commit è voluto
    fun setWasAnError(application: Application, value: Boolean) {
        val pref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(application)
        pref.edit().putBoolean("error", value).commit()

    }

    fun setScreenshot(application: Application, screenshotEnabled: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(application).edit().putBoolean("cl_screenshot", screenshotEnabled).apply()
    }

    fun isScreenshotEnabled(application: Application): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(application).getBoolean("cl_screenshot", true)
    }

}// no-op
