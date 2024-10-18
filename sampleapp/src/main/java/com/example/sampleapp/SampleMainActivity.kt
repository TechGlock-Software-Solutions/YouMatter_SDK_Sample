package com.example.sampleapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.sampleapp.databinding.ActivitySampleMainBinding
import com.techglock.health.app.YoumatterSDK
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class SampleMainActivity : AppCompatActivity() {
    var binding: ActivitySampleMainBinding?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this, R.layout.activity_sample_main)

        binding!!.layoutMain.setOnClickListener {
            val sdk = YoumatterSDK.getInstance(this)
            sdk.launchSdk(getSsoDetails())
            //sdk.launchSdkDirectly()
        }

    }

    private fun getSsoDetails() : JSONObject {
        val ssoData = JSONObject()
        ssoData.put("PartnerCode", "BOI")
        ssoData.put("Name", "Ayush Patil")
        ssoData.put("EmailAddress", "ayushpatil1@gmail.com")
        ssoData.put("PhoneNumber", "8888888881")
        ssoData.put("DOB", "1993-06-15")
        ssoData.put("Gender", "Male")
        ssoData.put("ClientKey", "BOIKEY142063")
        ssoData.put("ClientUserId", "2011")
        ssoData.put("EmployeeID", "15200")
        //ssoData.put("ClientAppBundleId", "com.boi")
        return ssoData
    }

/*    private fun getSsoDetails() : JSONObject {
        val ssoData = JSONObject()
        ssoData.put("PartnerCode", "BOI")
        ssoData.put("Name", "Savita Gawande")
        ssoData.put("EmailAddress", "savita17961@gmail.com")
        ssoData.put("PhoneNumber", "9881251765")
        ssoData.put("DOB", "1961-06-23")
        ssoData.put("Gender", "Female")
        ssoData.put("ClientKey", "BOIKEY142063")
        ssoData.put("ClientUserId", "2012")
        ssoData.put("EmployeeID", "15201")
        //ssoData.put("ClientAppBundleId", "com.boi")
        return ssoData
    }*/

/*    private fun getSsoDetails() : JSONObject {
        val ssoData = JSONObject()
        ssoData.put("PartnerCode", "BOI")
        ssoData.put("Name", "Gaurav Patidar")
        ssoData.put("EmailAddress", "GauravSSO17@gmail.com")
        ssoData.put("PhoneNumber", "7777777717")
        ssoData.put("DOB", "1995-10-25")
        ssoData.put("Gender", "Male")
        ssoData.put("ClientKey", "BOIKEY142063")
        ssoData.put("ClientUserId", "1017")
        ssoData.put("EmployeeID", "1017")
        //ssoData.put("ClientAppBundleId", "com.boi")
        return ssoData
    }*/

}