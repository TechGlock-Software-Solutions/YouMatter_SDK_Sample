package com.techglock.health.app.common.utils

import android.graphics.Bitmap

class UserSingleton {

    var profPicBitmap: Bitmap? = null

    fun clearData() {
        instance = null
        Utilities.printLogError("Cleared Profile Picture Data")
    }

    companion object {
        private var instance: UserSingleton? = null
        fun getInstance(): UserSingleton? {
            if (instance == null) {
                instance = UserSingleton()
            }
            return instance
        }
    }

}