package com.techglock.health.app.common.view

import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter
import com.techglock.health.app.R
import com.techglock.health.app.common.utils.Utilities
import com.squareup.picasso.Picasso

object CommonBinding {

    @BindingAdapter("android:loadImage")
    @JvmStatic
    fun setImageView(imageView: AppCompatImageView, resource: Int?) {
        try {
            imageView.setImageResource(resource!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @BindingAdapter("app:loadImgUrl")
    @JvmStatic
    fun AppCompatImageView.setImgUrl(imgUrl: String) {
        try {
            if (!Utilities.isNullOrEmpty(imgUrl)) {
                Picasso.get()
                    .load(imgUrl)
                    .placeholder(R.drawable.img_placeholder)
                    .resize(6000, 3000)
                    .onlyScaleDown()
                    .error(R.drawable.img_placeholder)
                    .into(this)
            } else {
                setImageResource(R.drawable.img_placeholder)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @BindingAdapter("android:NotNullEmptyText")
    @JvmStatic
    fun setNotNullEmptyText(textView: AppCompatTextView, text: String?) {
        try {
            if (!Utilities.isNullOrEmpty(text)) {
                textView.text = text
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @BindingAdapter("android:NotNullEmptyZeroText")
    @JvmStatic
    fun setNotNullEmptyZeroText(textView: AppCompatTextView, text: String?) {
        try {
            if (!Utilities.isNullOrEmptyOrZero(text)) {
                textView.text = text
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}