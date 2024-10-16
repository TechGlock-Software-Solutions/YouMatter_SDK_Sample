package com.techglock.health.app.common.base

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.view.Gravity
import android.view.View
import com.techglock.health.app.R

import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.DialogImageFullViewCommonBinding
import com.github.chrisbanes.photoview.PhotoViewAttacher
import com.squareup.picasso.Picasso

class DialogFullScreenView(context: Context, isImg: Boolean, imgUrl: String, bitmap: Bitmap?) :
    Dialog(context, R.style.TransparentProgressDialog), View.OnClickListener {

    private var binding: DialogImageFullViewCommonBinding =
        DialogImageFullViewCommonBinding.inflate(layoutInflater)

    val url = imgUrl
    val isImage = isImg
    val imgBitmap = bitmap

    init {
        //setContentView(R.layout.dialog_image_full_view_common)
        setContentView(binding.root)
        val wlmp = window!!.attributes
        wlmp.gravity = Gravity.CENTER_HORIZONTAL
        window!!.attributes = wlmp
        setTitle(null)
        binding.expandedBitmapImage
        binding.imgCloseImg.setOnClickListener(this)

        binding.expandedBitmapImage.setOnViewDragListener { dx, dy ->

        }

    }

    override fun show() {
        super.show()
        if (!Utilities.isNullOrEmpty(url)) {
            binding.expandedImage.visibility = View.VISIBLE
            binding.layoutImgBitmap.visibility = View.GONE
            if (isImage) {
                Picasso.get()
                    .load(url)
                    .placeholder(R.drawable.img_placeholder)
                    .resize(15000, 12000)
                    .onlyScaleDown()
                    .error(R.drawable.img_placeholder)
                    .into(binding.expandedImage)
                PhotoViewAttacher(binding.expandedImage)
            } else {
                binding.expandedImage.setImageResource(R.drawable.img_placeholder)
            }
        } else {
            binding.layoutImgBitmap.visibility = View.VISIBLE
            binding.expandedImage.visibility = View.GONE
            if (imgBitmap != null) {
                setCanceledOnTouchOutside(false)
                binding.expandedBitmapImage.setImageBitmap(imgBitmap)
            }
        }

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.img_close_img -> {
                dismiss()
            }
        }
    }

}