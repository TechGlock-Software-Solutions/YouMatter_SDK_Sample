package com.techglock.health.app.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
class DrawerData(
    var icon: Int = 0,
    var rootBG: Int = 0,
    var type: Int = 0,
    var id: Int = 0,
    var status: Int = 0,
    var textColor: Int = 0,
    var progress: Double = 0.0,
    var name: String = "",
    var isCheck: Boolean = false,
) : Parcelable