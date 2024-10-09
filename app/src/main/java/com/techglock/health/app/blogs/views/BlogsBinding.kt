package com.techglock.health.app.blogs.views

import android.os.Build
import android.text.Html
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter

object BlogsBinding {
    @BindingAdapter("app:htmlTxt")
    @JvmStatic
    fun AppCompatTextView.setHtmlTxt(html: String) {
        try {
            text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
            } else {
                Html.fromHtml(html)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}