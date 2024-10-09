package com.techglock.health.app.common.base

import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.utils.Utilities
import org.json.JSONObject

object ClientConfiguration {

    private var appTemplateConfig = ""

    init {
        Utilities.printLog("Inside Client Configuration")
        val template = JSONObject()

        val primaryColor = "#81A684"
        val secondaryColor = "#E8988D"
        /*        val primaryColor = "#776E5F"
                val secondaryColor = "#1CB3C8"*/
//        val primaryColor = "#1CB3C8"
//        val secondaryColor = "#aa595A5C"
        val clientTxtColor = "#000000"

        template.put(Constants.PRIMARY_COLOR, primaryColor)
        template.put(Constants.SECONDARY_COLOR, secondaryColor)
        template.put(Constants.TEXT_COLOR, clientTxtColor)
        template.put(Constants.ICON_TINT_COLOR, primaryColor)
        template.put(Constants.LEFT_BUTTON_COLOR, "#E2E2E2")
        template.put(Constants.RIGHT_BUTTON_COLOR, primaryColor)
        template.put(Constants.LEFT_BUTTON_TEXT_COLOR, primaryColor)
        template.put(Constants.RIGHT_BUTTON_TEXT_COLOR, "#ffffff")
        template.put(Constants.SELECTION_COLOR, primaryColor)
        template.put(Constants.DESELECTION_COLOR, "#f2f4f7")

        appTemplateConfig = template.toString()
    }

    fun getAppTemplateConfig(): String {
        return appTemplateConfig
    }

}