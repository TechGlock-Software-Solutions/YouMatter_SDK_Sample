package com.techglock.health.app.remote.interceptor

import com.techglock.health.app.common.utils.EncryptionUtility
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.model.BaseResponse
import com.techglock.health.app.model.security.TermsConditionsModel
import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.json.JSONObject
import java.io.IOException
import java.nio.charset.Charset


class DecryptInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        if (response.isSuccessful) {
            val newResponse = response.newBuilder()
            val contentType = response.header("Content-Type")
            val source = response.body?.source()
            source?.request(java.lang.Long.MAX_VALUE) // Buffer the entire body.
            val buffer = source?.buffer
            val responseBodyString = buffer?.clone()?.readString(Charset.forName("UTF-8"))
            Utilities.printLog("DecryptInterceptor---encryptedStream--->responseBodyString")
            var decrypted: String? = responseBodyString
            try {
                decrypted = EncryptionUtility.decrypt(responseBodyString!!)
                val baseResponse = Gson().fromJson(decrypted.toString(), BaseResponse::class.java)
                val resultResponse = Gson().fromJson(baseResponse.jSONData.toString(),
                    TermsConditionsModel.TermsConditionsResponse::class.java)
                if (resultResponse.termsConditions == null) {
                    decrypted = getJsonString(decrypted)
                } else {
                    val jsonObject: JSONObject = JSONObject()
                    val headerJsonObject: JSONObject = JSONObject(Gson().toJson(baseResponse.header,BaseResponse.Header::class.java))
                    val jsonObjJsonObject: JSONObject = JSONObject(Gson().toJson(resultResponse, TermsConditionsModel.TermsConditionsResponse::class.java))
                    jsonObject.put("Header", headerJsonObject)
                    jsonObject.put("JSONData", jsonObjJsonObject)
                    jsonObject.put("Data", "")
                    Utilities.printLog("Response===>$jsonObject")
                    decrypted = jsonObject.toString()
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }

            newResponse.body(decrypted!!.toResponseBody(contentType!!.toMediaTypeOrNull()))
            return newResponse.build()
        }
        return response
    }

    private fun getJsonString(decrypted: String?): String {
        /* val decryptedResponse: String = decrypted!!
             .replace("\\r\\n", "")
             .replace("\\\"", "\"")
             .replace("\"{", "{")
             .replace("}\"", "}")
         return decryptedResponse*/
        val decryptedResponse: String = decrypted!!
            .replace("\\r\\n", "")
            .replace("\\\\r\\\\n", "")
            .replace("\\\"", "\"")
            .replace("\\\\\"", "\"")
            .replace("\"{", "{")
            .replace("\"[", "[")
            .replace("}\"", "}")
            .replace("]\"", "]")
        return decryptedResponse

    }
}