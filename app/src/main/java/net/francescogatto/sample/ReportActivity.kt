package net.francescogatto.sample

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.activity_report.*
import net.francescogatto.catlog.CatLog


class ReportActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        title = "Report Issue"

        val items = arrayOf("HIGH", "MEDIUM", "LOW")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        val logUri = FileProvider.getUriForFile(this, "${this.packageName}.provider", CatLog.instance.lastErrorLogFile)
        val screenshotUri =
            FileProvider.getUriForFile(this, "${this.packageName}.provider", CatLog.instance.screehshotFile)
        shareFile(logUri, screenshotUri)

    }

    fun shareFile(logUri: Uri, screenshotUri: Uri) {
        val intent = Intent(Intent.ACTION_SEND_MULTIPLE);
        intent.type = "plain/text";
        intent.putExtra(Intent.EXTRA_SUBJECT, "That one works");

        val uris = ArrayList<Uri>()
        uris.add(logUri)
        uris.add(screenshotUri)

        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
        startActivityForResult(Intent.createChooser(intent, "Sending multiple attachment"), 12345);
    }
}
