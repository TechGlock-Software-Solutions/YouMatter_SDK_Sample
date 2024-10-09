package com.techglock.health.app.common.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import com.techglock.health.app.R
import com.techglock.health.app.common.base.ClientConfiguration
import com.techglock.health.app.common.constants.Constants
import org.json.JSONObject

class ArcView : View {

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        styleImage(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        styleImage(context, attrs)
    }

    private fun styleImage(context: Context, attrs: AttributeSet?) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CustomTemplate)
        val isTemplate = a.getBoolean(R.styleable.CustomTemplate_isTemplate, false)
        if (isTemplate) {
            try {
                val templateJSON =
                    JSONObject(ClientConfiguration.getAppTemplateConfig())
                if (templateJSON.has(Constants.PRIMARY_COLOR)) {
                    setBackgroundColor(Color.parseColor(templateJSON.getString(Constants.PRIMARY_COLOR)))
                    minimumWidth = com.intuit.sdp.R.dimen._1sdp
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}