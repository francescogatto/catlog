package net.francescogatto.catlog.internal

import android.content.Context
import android.util.Log
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

/**
 * Class for log writing operation
 */

class LogWriterFile(val context: Context, private val size: Int, private val file: File) : LogWriter {

    override fun log(log: LogEntity) {
        file.appendLog(formatMsg(log.tag, log.message + log.stackException))
    }


    fun File.appendLog(text: String) {
        if (length() > size * 1024) {
            delete()
        }
        if (!exists()) {
            try {
                createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        try {
            val buf = BufferedWriter(FileWriter(this, true))
            buf.append(text)
            buf.newLine()
            buf.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * Clear current log
     */
    override fun delete() {
        file.delete()
    }

    companion object {
        private val TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS"
        private val MSG_FORMAT = "%s: %s - %s"  // timestamp, tag, message

        private fun formatMsg(tag: String, msg: String): String {
            return String.format(MSG_FORMAT, currentTimeStamp, tag, msg)
        }

        private val currentTimeStamp: String?
            get() {
                var currentTimeStamp: String? = null

                try {
                    val dateFormat = SimpleDateFormat(TIMESTAMP_FORMAT, java.util.Locale.getDefault())
                    currentTimeStamp = dateFormat.format(Date())
                } catch (e: Exception) {
                    Log.e("FileLog", Log.getStackTraceString(e))
                }

                return currentTimeStamp
            }
    }

}
