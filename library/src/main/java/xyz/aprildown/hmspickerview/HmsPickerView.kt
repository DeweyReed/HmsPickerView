package xyz.aprildown.hmspickerview

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.text.BidiFormatter
import android.text.TextUtils
import android.text.format.DateUtils
import android.text.style.RelativeSizeSpan
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.core.view.ViewCompat
import java.util.*

class HmsPickerView(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs), View.OnClickListener, View.OnLongClickListener {

    interface Listener {
        fun onHmsPickerViewHasValidInput(hmsPickerView: HmsPickerView)
        fun onHmsPickerViewHasNoInput(hmsPickerView: HmsPickerView)
    }

    private val input = intArrayOf(0, 0, 0, 0, 0, 0)

    private var inputPointer = -1
    private val timeTemplate: CharSequence

    private lateinit var timeView: TextView
    private lateinit var deleteView: View
    private lateinit var dividerView: View
    private lateinit var digitViews: Array<TextView>

    private val hasValidInput: Boolean
        get() = inputPointer != -1

    private var listener: HmsPickerView.Listener? = null

    init {
        val bf = BidiFormatter.getInstance(false /* rtlContext */)
        val hoursLabel = bf.unicodeWrap(context.getString(R.string.hpv_hours_label))
        val minutesLabel = bf.unicodeWrap(context.getString(R.string.hpv_minutes_label))
        val secondsLabel = bf.unicodeWrap(context.getString(R.string.hpv_seconds_label))

        // Create a formatted template for "00h 00m 00s".
        timeTemplate = TextUtils.expandTemplate(
            "^1^4 ^2^5 ^3^6",
            bf.unicodeWrap("^1"),
            bf.unicodeWrap("^2"),
            bf.unicodeWrap("^3"),
            formatText(hoursLabel, RelativeSizeSpan(0.3f)),
            formatText(minutesLabel, RelativeSizeSpan(0.3f)),
            formatText(secondsLabel, RelativeSizeSpan(0.3f))
        )

        LayoutInflater.from(context).inflate(R.layout.hpv_view, this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        timeView = findViewById(R.id.timer_setup_time)
        deleteView = findViewById(R.id.timer_setup_delete)
        dividerView = findViewById(R.id.timer_setup_divider)
        digitViews = arrayOf(
            findViewById(R.id.timer_setup_digit_0),
            findViewById(R.id.timer_setup_digit_1),
            findViewById(R.id.timer_setup_digit_2),
            findViewById(R.id.timer_setup_digit_3),
            findViewById(R.id.timer_setup_digit_4),
            findViewById(R.id.timer_setup_digit_5),
            findViewById(R.id.timer_setup_digit_6),
            findViewById(R.id.timer_setup_digit_7),
            findViewById(R.id.timer_setup_digit_8),
            findViewById(R.id.timer_setup_digit_9)
        )

        // Tint the divider to match the disabled control color by default and used the activated
        // control color when there is valid input.
        val dividerContext = dividerView.context
        val colorControlActivated = resolveColor(
            dividerContext,
            R.attr.colorControlActivated
        )
        val colorControlDisabled = resolveColor(
            dividerContext,
            R.attr.colorControlNormal, intArrayOf(android.R.attr.state_enabled.inv())
        )
        ViewCompat.setBackgroundTintList(
            dividerView, ColorStateList(
                arrayOf(intArrayOf(android.R.attr.state_activated), intArrayOf()),
                intArrayOf(colorControlActivated, colorControlDisabled)
            )
        )
        ViewCompat.setBackgroundTintMode(dividerView, PorterDuff.Mode.SRC)

        // Initialize the digit buttons.
        for (digitView in digitViews) {
            val digit = getDigitForId(digitView.id)
            digitView.text = getFormattedNumber(digit, 1)
            digitView.setOnClickListener(this)
        }

        deleteView.setOnClickListener(this)
        deleteView.setOnLongClickListener(this)

        updateTime()
        updateDeleteAndDivider()
    }

    private fun getDigitForId(@IdRes id: Int): Int = when (id) {
        R.id.timer_setup_digit_0 -> 0
        R.id.timer_setup_digit_1 -> 1
        R.id.timer_setup_digit_2 -> 2
        R.id.timer_setup_digit_3 -> 3
        R.id.timer_setup_digit_4 -> 4
        R.id.timer_setup_digit_5 -> 5
        R.id.timer_setup_digit_6 -> 6
        R.id.timer_setup_digit_7 -> 7
        R.id.timer_setup_digit_8 -> 8
        R.id.timer_setup_digit_9 -> 9
        else -> throw IllegalArgumentException("Invalid id: $id")
    }


    private fun updateTime() {
        val seconds = input[1] * 10 + input[0]
        val minutes = input[3] * 10 + input[2]
        val hours = input[5] * 10 + input[4]

        timeView.text = TextUtils.expandTemplate(
            timeTemplate,
            getFormattedNumber(hours, 2),
            getFormattedNumber(minutes, 2),
            getFormattedNumber(seconds, 2)
        )

        val r = resources
        timeView.contentDescription = r.getString(
            R.string.hpv_time_description,
            r.getQuantityString(R.plurals.hpv_hours, hours, hours),
            r.getQuantityString(R.plurals.hpv_minutes, minutes, minutes),
            r.getQuantityString(R.plurals.hpv_seconds, seconds, seconds)
        )
    }

    private fun updateDeleteAndDivider() {
        val enabled = hasValidInput
        deleteView.isEnabled = enabled
        dividerView.isActivated = enabled
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        var view: View? = null
        if (keyCode == KeyEvent.KEYCODE_DEL) {
            view = deleteView
        } else if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) {
            view = digitViews[keyCode - KeyEvent.KEYCODE_0]
        }

        if (view != null) {
            val result = view.performClick()
            if (result && hasValidInput) {
                listener?.onHmsPickerViewHasValidInput(this)
            }
            return result
        }

        return false
    }

    override fun onClick(view: View) {
        if (view === deleteView) {
            delete()
        } else {
            append(getDigitForId(view.id))
        }
    }

    override fun onLongClick(view: View): Boolean {
        if (view === deleteView) {
            reset()
            notifyNoInput()
            return true
        }
        return false
    }

    private fun notifyNoInput() {
        listener?.onHmsPickerViewHasNoInput(this)
    }

    private fun append(digit: Int) {
        if (digit < 0 || digit > 9) {
            throw IllegalArgumentException("Invalid digit: $digit")
        }

        // Pressing "0" as the first digit does nothing.
        if (inputPointer == -1 && digit == 0) {
            return
        }

        // No space for more digits, so ignore input.
        if (inputPointer == input.size - 1) {
            return
        }

        // Append the new digit.
        System.arraycopy(input, 0, input, 1, inputPointer + 1)
        input[0] = digit
        inputPointer++
        updateTime()

        // Update TalkBack to read the number being deleted.
        deleteView.contentDescription = context.getString(
            R.string.hpv_delete_number,
            getFormattedNumber(digit)
        )

        // Update the fab, delete, and divider when we have valid input.
        if (inputPointer == 0) {
            notifyNoInput()
            updateDeleteAndDivider()
        }
    }

    private fun delete() {
        // Nothing exists to delete so return.
        if (inputPointer < 0) {
            return
        }

        System.arraycopy(input, 1, input, 0, inputPointer)
        input[inputPointer] = 0
        inputPointer--
        updateTime()

        // Update TalkBack to read the number being deleted or its original description.
        if (inputPointer >= 0) {
            deleteView.contentDescription = context.getString(
                R.string.hpv_delete_number,
                getFormattedNumber(input[0])
            )
        } else {
            deleteView.contentDescription = context.getString(R.string.hpv_delete)
        }

        // Update the fab, delete, and divider when we no longer have valid input.
        if (inputPointer == -1) {
            notifyNoInput()
            updateDeleteAndDivider()
        }
    }

    fun reset() {
        if (inputPointer != -1) {
            Arrays.fill(input, 0)
            inputPointer = -1
            updateTime()
            updateDeleteAndDivider()
        }
    }

    fun setListener(l: HmsPickerView.Listener) {
        listener = l
    }

    fun getSeconds(): Int = input[1] * 10 + input[0]

    fun getMinutes(): Int = input[3] * 10 + input[2]

    fun getHours(): Int = input[5] * 10 + input[4]

    fun getTimeInMillis(): Long =
        getSeconds() * DateUtils.SECOND_IN_MILLIS +
                getMinutes() * DateUtils.MINUTE_IN_MILLIS +
                getHours() * DateUtils.HOUR_IN_MILLIS
}