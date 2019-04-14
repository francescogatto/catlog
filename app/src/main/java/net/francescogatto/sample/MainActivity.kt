package net.francescogatto.sample

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity;

import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import java.util.concurrent.atomic.AtomicLong

class MainActivity : AppCompatActivity() {

    internal var counter = AtomicLong()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
            var i = 0
            while (i < 10000) {
                Timber.i("test: %d", counter.getAndIncrement());
                Timber.tag("TEST").i("test: %d", counter.getAndIncrement())
                i++
//                Timber.e(RuntimeException("sjndjf"), "Error");
//                4 / 0
            }
        }
    }

}
