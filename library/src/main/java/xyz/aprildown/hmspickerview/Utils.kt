package xyz.aprildown.hmspickerview

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import java.util.*

/**
 * Applies a span over the length of the given text.
 *
 * @param text the [CharSequence] to be formatted
 * @param span the span to apply
 * @return the text with the span applied
 */
internal fun formatText(text: CharSequence?, span: Any): CharSequence? {
    if (text == null) {
        return null
    }

    val formattedText = SpannableString.valueOf(text)
    formattedText.setSpan(span, 0, formattedText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    return formattedText
}

/**
 * Convenience method for retrieving a themed color value.
 *
 * @param context the [Context] to resolve the theme attribute against
 * @param attr    the attribute corresponding to the color to resolve
 * @return the color value of the resolved attribute
 */
@ColorInt
internal fun resolveColor(context: Context, @AttrRes attr: Int): Int {
    return resolveColor(context, attr, null /* stateSet */)
}

/**
 * Convenience method for retrieving a themed color value.
 *
 * @param context  the [Context] to resolve the theme attribute against
 * @param attr     the attribute corresponding to the color to resolve
 * @param stateSet an array of [android.view.View] states
 * @return the color value of the resolved attribute
 */
@ColorInt
internal fun resolveColor(context: Context, @AttrRes attr: Int, @AttrRes stateSet: IntArray?): Int {

    /** Temporary array used internally to resolve attributes.  */
    val tempAttr = IntArray(1)

    val a: TypedArray
    tempAttr[0] = attr
    a = context.obtainStyledAttributes(tempAttr)

    try {
        if (stateSet == null) {
            return a.getColor(0, Color.RED)
        }

        val colorStateList = a.getColorStateList(0)
        return colorStateList?.getColorForState(stateSet, Color.RED) ?: Color.RED
    } finally {
        a.recycle()
    }
}

internal fun getFormattedNumber(value: Int): String {
    val length = if (value == 0) 1 else Math.log10(value.toDouble()).toInt() + 1
    return getFormattedNumber(value, length)
}

internal fun getFormattedNumber(value: Int, length: Int): String {
    return String.format(Locale.getDefault(), "%0${length}d", value)
}