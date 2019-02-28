package xyz.aprildown.hmspickerview.app

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import kotlinx.android.synthetic.main.activity_main.*
import xyz.aprildown.hmspickerview.HmsPickerView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnNight.setOnClickListener {
            val isNight =
                AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
            if (isNight) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            recreate()
        }

        btnDialog.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
                .setTitle("HmsPickerView")
                .setView(R.layout.layout_picker)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null)
                .show()

            val hmsPickerView = dialog.findViewById<HmsPickerView>(R.id.hmsPickerView)!!
            dialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(android.R.string.ok)) { _, _ ->
                Toast.makeText(
                    this, "%2dh %2dm %2ds".format(
                        hmsPickerView.getHours(),
                        hmsPickerView.getMinutes(),
                        hmsPickerView.getSeconds()
                    ),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
