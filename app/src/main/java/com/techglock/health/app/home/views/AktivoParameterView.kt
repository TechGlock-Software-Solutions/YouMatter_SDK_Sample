package com.techglock.health.app.home.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.techglock.health.app.R


class AktivoParameterView : ConstraintLayout {

    private var mContext: Context
    private var txtParamTitle: TextView? = null
    private var txtParamValue: TextView? = null

    constructor(context: Context) : super(context) {
        mContext = context
        initLayout()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        mContext = context
        initLayout()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        mContext = context
        initLayout()
    }

    private fun initLayout() {
        val rootView = inflate(mContext, R.layout.view_aktivo_parameter, this)
        txtParamTitle = rootView.findViewById<View>(R.id.txt_param_title) as TextView
        txtParamValue = rootView.findViewById<View>(R.id.txt_param_value) as TextView
    }

    fun setParamTitle(title: String) {
        txtParamTitle!!.text = title
    }

    fun setParamValue(value: String) {
        txtParamValue!!.text = value
    }

}