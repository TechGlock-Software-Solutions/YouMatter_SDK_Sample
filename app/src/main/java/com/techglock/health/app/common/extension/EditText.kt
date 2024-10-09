package com.techglock.health.app.common.extension


import android.app.Service
import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.TextView.BufferType
import androidx.core.content.ContextCompat
import com.techglock.health.app.R
import com.techglock.health.app.common.constants.Constants


fun EditText.isBlank(): Boolean {
    return this.text.toString().trim().isEmpty()
}


fun EditText.getLength(): Int {
    return this.text.toString().trim().length
}

fun EditText.checkString(): String {
    return this.text.toString().trim()
}

fun TextView.isBlank(): Boolean {
    return this.text.toString().trim().isEmpty()
}


fun TextView.getLength(): Int {
    return this.text.toString().trim().length
}

fun TextView.checkString(): String {
    return this.text.toString().trim()
}


fun TextView.setSpanString(
    spanText: String,
    start: Int,
    end: Int = spanText.length,
    showBold: Boolean = false,
    color: Int = R.color.colorPrimary,
    isUnderlineText: Boolean = false,
    onSpanClick: () -> Unit = {}
) {
    val ss = SpannableString(spanText)
    val clickableSpan = object : ClickableSpan() {
        override fun onClick(textView: View) {
            onSpanClick()
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.isUnderlineText = false
            ds.color = ContextCompat.getColor(this@setSpanString.context, color)
        }
    }

    ss.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    if (showBold) {
        ss.setSpan(StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    text = ss
    movementMethod = LinkMovementMethod.getInstance()
    highlightColor = ContextCompat.getColor(this@setSpanString.context, R.color.transparent)
}


fun TextView.setTextWithDifferentSizes(amount: String, monthText: String) {
    // Create the complete text
    val fullText = amount + monthText

    // Create a SpannableString to style different parts of the text
    val spannableString = SpannableString(fullText)

    // Set the size of the amount text
    val amountLength = amount.length

    spannableString.setSpan(RelativeSizeSpan(1f), 0, amountLength, 0)
//    spannableString.setSpan(StyleSpan(Typeface.BOLD), 0, amountLength, 0)

    // Set the size of the "Month" text
    spannableString.setSpan(RelativeSizeSpan(2f), amountLength, fullText.length, 0)

    // Apply the styled text to the TextView
    this.text = spannableString
}

fun TextView.setSpanString(
    spanText: String,
    start: Int,
    end: Int,
    secondStringStartLength: Int,
    secondStringEndLength: Int = spanText.length,
    showBold: Boolean = false,
    color: Int = R.color.colorPrimary,
    color2: Int = R.color.colorPrimary,
    onSpanClick: (value: Int) -> Unit = {}
) {
    val ss = SpannableString(spanText)

    val termsAndCondition: ClickableSpan = object : ClickableSpan() {
        override fun onClick(textView: View) {
            onSpanClick(Constants.SPAN_ONE)
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.isUnderlineText = false
            ds.color = ContextCompat.getColor(this@setSpanString.context, color)
        }
    }

    val privacy: ClickableSpan = object : ClickableSpan() {
        override fun onClick(textView: View) {
            onSpanClick(Constants.SPAN_TWO)
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.isUnderlineText = false
            ds.color = ContextCompat.getColor(this@setSpanString.context, color2)
        }
    }

    ss.setSpan(termsAndCondition, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    ss.setSpan(
        privacy,
        secondStringStartLength,
        secondStringEndLength,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    if (showBold) {
        ss.setSpan(StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        ss.setSpan(
            StyleSpan(Typeface.BOLD),
            secondStringStartLength,
            secondStringEndLength,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    this.setText(ss, BufferType.SPANNABLE)
    movementMethod = LinkMovementMethod.getInstance()
    highlightColor = ContextCompat.getColor(this@setSpanString.context, R.color.transparent)
}


fun TextView.setSpanString(
    spanText: String,
    start: Int,
    end: Int,
    start2: Int,
    end2: Int,
    start3: Int,
    end3: Int,
    showBold: Boolean = false,
    color: Int = R.color.colorPrimary,
    color2: Int = R.color.colorPrimary,
    color3: Int = R.color.colorPrimary,
    onSpanClick: (value: Int) -> Unit = {}
) {
    val ss = SpannableString(spanText)

    val termsAndCondition: ClickableSpan = object : ClickableSpan() {
        override fun onClick(textView: View) {
            onSpanClick(Constants.SPAN_ONE)
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.isUnderlineText = false
            ds.color = ContextCompat.getColor(this@setSpanString.context, color)
        }
    }

    val privacy: ClickableSpan = object : ClickableSpan() {
        override fun onClick(textView: View) {
            onSpanClick(Constants.SPAN_TWO)
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.isUnderlineText = false
            ds.color = ContextCompat.getColor(this@setSpanString.context, color2)
        }
    }

    val thirdString: ClickableSpan = object : ClickableSpan() {
        override fun onClick(textView: View) {
            onSpanClick(Constants.SPAN_THREE)
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.isUnderlineText = false
            ds.color = ContextCompat.getColor(this@setSpanString.context, color3)
        }
    }

    ss.setSpan(termsAndCondition, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    ss.setSpan(privacy, start2, end2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    ss.setSpan(thirdString, start3, end3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

    ss.setSpan(StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    if (showBold) {
        ss.setSpan(StyleSpan(Typeface.BOLD), start2, end2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        ss.setSpan(StyleSpan(Typeface.BOLD), start3, end3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    this.setText(ss, BufferType.SPANNABLE)
    movementMethod = LinkMovementMethod.getInstance()
    highlightColor = ContextCompat.getColor(this@setSpanString.context, R.color.transparent)
}


fun String.isBlank(): Boolean {
    return this.trim().isEmpty()
}


fun String.getLength(): Int {
    return this.trim().length
}

fun String.checkString(): String {
    return this.trim()
}


fun EditText.setFocus() {

    isFocusable = true
    isFocusableInTouchMode = true
    requestFocus()
}

fun EditText.showSoftKeyboard() {
    val imm = context.getSystemService(Service.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, 0)
}


fun EditText.removeSpecialChar(s: String): String {
    return this.text.toString().trim().replace(s, "")
}

fun TextView.changeColor(isSelected: Boolean) {
    if (isSelected) {
        this.setTextColor(ContextCompat.getColor(this.context, R.color.white))
    } else {
        this.setTextColor(ContextCompat.getColor(this.context, R.color.placeholder))
    }
}






