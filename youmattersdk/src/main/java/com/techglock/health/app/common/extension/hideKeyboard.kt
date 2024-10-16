package com.techglock.health.app.common.extension

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

fun View.hideKeyboard(isHide: Boolean = true) {
    if (isHide) {
        val inputManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }
}


fun View.preventDoubleClick() {
    this.isClickable = false
    this.postDelayed({ this.isClickable = true }, 1000)
}