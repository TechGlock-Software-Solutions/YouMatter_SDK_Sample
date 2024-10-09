package com.techglock.health.app.remote.interceptor

import com.techglock.health.app.common.utils.EncryptionUtility
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.Buffer
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class EncryptInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val urlStr = request.url.toString()
        if (GET == request.method) {
            request = request.newBuilder().url(urlStr).build()
        } else if (POST == request.method) {
            val builder = request.newBuilder()

            val oldBody = request.body
            if (oldBody != null) {
                val buffer = Buffer()
                oldBody.writeTo(buffer)
                val strOldBody = buffer.readUtf8()
                val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
                var strNewBody: String? = strOldBody//toJson(strOldBody)
                try {
                    strNewBody = EncryptionUtility.encrypt(strOldBody)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                val newBody = strNewBody!!.toRequestBody(mediaType)
                //Utilities.printLogError("Encrypted--->$strNewBody")
                request = builder.url(urlStr)
                    .header("Content-Type", oldBody.contentType()!!.toString())
                    .header("Content-Length", strNewBody.length.toString())
                    .header("Accept", "application/json")
                    .post(newBody)
                    .build()

            }
        }
        return chain.proceed(request)
    }

    companion object {

        private val GET = "GET"
        private val POST = "POST"
        private val TAG = EncryptInterceptor::class.java.simpleName

        fun toJson(params: String): String {
            try {
                JSONObject(params)
                return params
            } catch (e: JSONException) {
                val jsonObject = JSONObject()
                val maps = params.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                for (map in maps) {
                    val kv = map.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    try {
                        jsonObject.put(kv[0], if (kv.size == 2) kv[1] else "")
                    } catch (e1: JSONException) {
                        e1.printStackTrace()
                    }
                }
                return jsonObject.toString()
            }
        }
    }
}