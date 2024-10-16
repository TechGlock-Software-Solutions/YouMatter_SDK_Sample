package com.techglock.health.app.fitness_tracker.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.techglock.health.app.R

class FitnessDetailView : ConstraintLayout {

    private var mContext: Context
    private var txtDataType: TextView? = null
    private var txtDataValue: TextView? = null
    private var txtDataUnit: TextView? = null

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
        val rootView = inflate(mContext, R.layout.view_fitness_detail, this)
        txtDataType = rootView.findViewById<View>(R.id.txt_data_type) as TextView
        txtDataValue = rootView.findViewById<View>(R.id.txt_data_value) as TextView
        txtDataUnit = rootView.findViewById<View>(R.id.txt_data_unit) as TextView
    }

    fun setDataType(title: String) {
        txtDataType!!.text = title
    }

    fun setDataValue(value: String) {
        txtDataValue!!.text = value
    }

    fun setDataUnit(value: String) {
        txtDataUnit!!.text = value
    }

}