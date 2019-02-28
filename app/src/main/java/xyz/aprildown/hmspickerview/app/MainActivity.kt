package xyz.aprildown.hmspickerview.app

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnDialog.setOnClickListener {
            AlertDialog.Builder(this)
                .setView(R.layout.layout_picker)
                .show()
        }
    }
}
