package com.techglock.health.app.common.utils

import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.content.ContextCompat
import com.techglock.health.app.R

import com.techglock.health.app.databinding.DefaultDialogBinding

class DefaultNotificationDialog(
    context: Context?,
    private val onDialogValueListener: OnDialogValueListener,
    data: DialogData
) : Dialog(context!!), View.OnClickListener {

    private lateinit var binding: DefaultDialogBinding

    private val appColorHelper = AppColorHelper.instance!!
    private var dialogData: DialogData? = null
    private var mContext: Context? = null

    init {
        //Utilities.printData("dialogData", data)
        this.dialogData = data
        this.mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DefaultDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setContentView(R.layout.default_dialog)
        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        this.setCancelable(false)
        this.setCanceledOnTouchOutside(false)

        binding.txtDialogTitle
        binding.imgCloseInput.setOnClickListener(this)
        binding.btnLeftSide.setOnClickListener(this)
        binding.btnRightSide.setOnClickListener(this)

        binding.btnLeftSide.text = dialogData!!.btnLeftName
        binding.btnRightSide.text = dialogData!!.btnRightName
        binding.txtDialogTitle.text = dialogData!!.title
        //binding.txtDialogDescription.text = dialogData!!.message
        binding.txtDialogDescription.text = Html.fromHtml(dialogData!!.message)

        if (dialogData!!.imgResource != 0) {
            if (dialogData!!.isLottieImg) {
                binding.imgDialogImageLottie.visibility = View.VISIBLE
                binding.imgDialogImage.visibility = View.GONE
                binding.imgDialogImageLottie.setAnimation(dialogData!!.imgResource)
            } else {
                binding.imgDialogImage.visibility = View.VISIBLE
                binding.imgDialogImageLottie.visibility = View.GONE
                binding.imgDialogImage.setImageResource(dialogData!!.imgResource)
            }
        } else {
            binding.layoutImage.visibility = View.GONE
        }

        if (Utilities.isNullOrEmpty(dialogData!!.title)) {
            binding.txtDialogTitle.visibility = View.GONE
        } else {
            binding.txtDialogTitle.visibility = View.VISIBLE
        }

        if (!dialogData!!.showLeftButton) {
            binding.btnLeftSide.visibility = View.GONE
        } else {
            binding.btnLeftSide.visibility = View.VISIBLE
        }
        if (!dialogData!!.showRightButton) {
            binding.btnRightSide.visibility = View.GONE
        } else {
            binding.btnRightSide.visibility = View.VISIBLE
        }

        if (dialogData!!.showDismiss) {
            binding.imgCloseInput.visibility = View.VISIBLE
        } else {
            binding.imgCloseInput.visibility = View.INVISIBLE
        }

        if (dialogData!!.hasErrorBtn) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                binding.btnRightSide.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(mContext!!, R.color.state_error))
            }
        }

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_left_side -> {
                onDialogValueListener.onDialogClickListener(
                    isButtonLeft = true,
                    isButtonRight = false
                )
                dismiss()
            }

            R.id.btn_right_side -> {
                onDialogValueListener.onDialogClickListener(
                    isButtonLeft = false,
                    isButtonRight = true
                )
                dismiss()
            }

            R.id.img_close_input -> {
                onDialogValueListener.onDialogClickListener(
                    isButtonLeft = false,
                    isButtonRight = false
                )
                dismiss()
            }
        }
    }

    class DialogData {
        var imgResource = 0
        var isLottieImg = false
        var title = ""
        var message = ""
        var btnLeftName = "Cancel"
        var btnRightName = "Ok"
        var showLeftButton = true
        var showRightButton = true
        var showDismiss = true
        var hasErrorBtn = false
    }

    interface OnDialogValueListener {
        fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean)
    }

}