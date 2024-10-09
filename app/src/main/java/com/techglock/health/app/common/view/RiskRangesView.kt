package com.techglock.health.app.common.view

import android.content.Context
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import com.techglock.health.app.R

class RiskRangesView : ConstraintLayout {

    private var mContext: Context
    private var view: View? = null
    private var txtRangeValue: TextView? = null
    private var txtRangeTitle: TextView? = null
    private var layoutView: ConstraintLayout? = null

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
        val rootView = inflate(mContext, R.layout.item_risk_ranges, this)
        view = rootView.findViewById<View>(R.id.view) as View
        layoutView = rootView.findViewById<View>(R.id.layout_view) as ConstraintLayout
        txtRangeValue = rootView.findViewById<View>(R.id.txt_range_value) as TextView
        txtRangeTitle = rootView.findViewById<View>(R.id.txt_range_title) as TextView
    }

    fun setViewColor(color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            view!!.background.colorFilter =
                BlendModeColorFilter(ContextCompat.getColor(mContext, color), BlendMode.SRC_ATOP)
        } else {
            view!!.background.setColorFilter(
                ContextCompat.getColor(mContext, color),
                PorterDuff.Mode.SRC_ATOP
            )
        }
    }

    fun setRangeValue(value: String) {
        txtRangeValue!!.text = value
    }

    fun setRangeTitle(value: String) {
        txtRangeTitle!!.text = value
    }

    fun setViewBackgroundColor(value: Int) {
        layoutView!!.background.colorFilter =
            BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                ContextCompat.getColor(context, value), BlendModeCompat.SRC_ATOP
            )
    }

}