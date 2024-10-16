package com.techglock.health.app.common.utils

import android.app.Dialog
import android.content.Context
import android.view.ViewGroup
import android.view.Window
import com.techglock.health.app.R

class DialogSuccess(private val mcontext: Context?) : Dialog(mcontext!!) {



    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_change_password_success)
        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        setCanceledOnTouchOutside(false)
        setCancelable(false)
    }

    override fun show() {
        super.show()
        try {

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /*    override fun show() {
            super.show()
            try {
                img_password_changed_anim.visibility = View.VISIBLE
                img_password_changed.visibility = View.GONE
                //btn_go_to_app!!.text = btnText
                Handler(Looper.getMainLooper()).postDelayed({
                    img_password_changed_anim.visibility = View.GONE
                    img_password_changed.visibility = View.VISIBLE
                }, 3000)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }*/

}
