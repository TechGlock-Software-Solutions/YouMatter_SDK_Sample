package com.techglock.health.app.remote

import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.model.BaseHandlerResponse
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.nio.charset.Charset

class DecryptionInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        if (response.isSuccessful && response.code == 200) {
            val newResponse = response.newBuilder()
            val contentType = response.header("Content-Type")
            val source = response.body?.source()
            source?.request(java.lang.Long.MAX_VALUE) // Buffer the entire body.
            val buffer = source?.buffer
            val responseBodyString = buffer?.clone()?.readString(Charset.forName("UTF-8"))
            var decrypted: String? = responseBodyString
            var decryptedResponse: String? = responseBodyString
            try {
                decrypted = getJsonString(decrypted)
                /*val jObj = JSONObject(decrypted)
                jObj.put("JSONData",jObj.get("JObject"))
                jObj.remove("JObject")*/

                val baseHandlerResponse =
                    Gson().fromJson(decrypted, BaseHandlerResponse::class.java)
                Utilities.printLog("baseHandlerResponse => " + decrypted)
                if (baseHandlerResponse.jObject == null || baseHandlerResponse.jObject?.equals("")!!) {
                    baseHandlerResponse.jObject = JsonObject()
                    Utilities.printLog("baseHandlerResponse => JSONData--> " + baseHandlerResponse.jsonData)
                    if (baseHandlerResponse.jsonData != null) {
                        baseHandlerResponse.jObject = baseHandlerResponse.jsonData
                        baseHandlerResponse.errorNumber = "0"
                        baseHandlerResponse.statusCode = "200"
                    }

                    if (!Utilities.isNullOrEmptyOrZero(baseHandlerResponse.profileImageID)) {
                        val jsonParser = JsonParser()
                        val gsonObject = jsonParser.parse(decrypted) as JsonObject
                        baseHandlerResponse.jsonData = JsonObject()
                        baseHandlerResponse.jsonData = gsonObject
                        baseHandlerResponse.jObject = gsonObject
                        baseHandlerResponse.errorNumber = "0"
                        baseHandlerResponse.statusCode = "200"
                    }
                    if (!baseHandlerResponse.categoryList.isNullOrEmpty()) {
                        val jsonParser = JsonParser()
                        val gsonObject = jsonParser.parse(decrypted) as JsonObject
                        baseHandlerResponse.jsonData = JsonObject()
                        baseHandlerResponse.jsonData = gsonObject
                        baseHandlerResponse.jObject = gsonObject
                        baseHandlerResponse.errorNumber = "0"
                        baseHandlerResponse.statusCode = "200"
                    }
                }
                decryptedResponse = Gson().toJson(baseHandlerResponse)

                newResponse.body(
                    decryptedResponse!!
                        .toResponseBody(contentType!!.toMediaTypeOrNull())
                )
                return newResponse.build()

            } catch (e: Exception) {
                e.printStackTrace()
            }
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
            .replace("\\\"", "\"")
            .replace("\\\\\"", "\"")
            .replace("\"{", "{")
            .replace("\"[", "[")
            .replace("}\"", "}")
            .replace("]\"", "]")
        return decryptedResponse

    }

}