package com.techglock.health.app.common.utils

import android.util.Base64
import com.techglock.health.app.common.constants.Constants
import java.io.UnsupportedEncodingException
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.Mac
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.experimental.and


/**
 * Encrypt and decrypt messages using AES 256 bit encryption that are compatible with AESCrypt-ObjC and AESCrypt Ruby.
 *
 *
 * Created by scottab on 04/10/2014.
 */
class EncryptionUtility {
    companion object {

        private val TAG = "EncryptionUtility"

        //AESCrypt-ObjC uses CBC and PKCS7Padding
        private val AES_MODE = "AES/CBC/ISO10126Padding"
        val CHARSET: String = "UTF-8"

        //AESCrypt-ObjC uses SHA-256 (and so a 256-bit key)
        private val HASH_ALGORITHM = "SHA-256"

        //AESCrypt-ObjC uses blank IV (not the best security, but the aim here is compatibility)

        //https://onlinestringtools.com/convert-bytes-to-string
        val IVkey = byteArrayOf(
            0x41,
            0x31,
            0x48,
            0x53,
            0x38,
            0x43,
            0x55,
            0x52,
            0x31,
            0x54,
            0x59,
            0x40,
            0x39,
            0x38,
            0x31,
            0x32
        )

        //togglable log option (please turn off in live!)
        var DEBUG_LOG_ENABLED = false

        /**
         * Generates SHA256 hash of the password which is used as key
         *
         * @return SHA256 of the password
         */
        private fun generateKey(keyValue: String): SecretKeySpec {
            val digest = MessageDigest.getInstance(HASH_ALGORITHM)
            val bytes = keyValue.toByteArray(charset("UTF-8"))
            digest.update(bytes, 0, bytes.size)
            val key = digest.digest()
            return SecretKeySpec(key, "AES")
        }

        /**
         * Encrypt and encode message using 256-bit AES with key generated from password.
         *
         * @param key     : secrete key
         * @param message the thing you want to encrypt assumed String UTF-8
         * @return Base64 encoded CipherText
         */
        //fun encrypt(key: String, message: String, IV: String): String {
        fun encrypt(message: String): String {
            var encryptedString = ""
            try {

                val ivBytes = Constants.IVkey//IV.toByteArray()
                val keyBytes = Constants.IVkey//key.toByteArray(charset("UTF-8"))

                //val ivBytes = IV.toByteArray()
                //val keyBytes = key.toByteArray(charset("UTF-8"))

                val secreteKey = SecretKeySpec(keyBytes, "AES")
                val cipherText = encrypt(
                    secreteKey,
                    ivBytes,
                    message.toByteArray(charset(CHARSET))
                )

                encryptedString = Base64.encodeToString(cipherText, Base64.NO_WRAP)
            } catch (e: UnsupportedEncodingException) {
                if (DEBUG_LOG_ENABLED) {
                    Utilities.printLogError("$TAG---UnsupportedEncodingException--->$e")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return encryptedString
        }

        /**
         * More flexible AES encrypt that doesn't encode
         *
         * @param key     AES key typically 128, 192 or 256 bit
         * @param iv      Initiation Vector
         * @param message in bytes (assumed it's already been decoded)
         * @return Encrypted cipher text (not encoded)
         */
        fun encrypt(key: SecretKeySpec, iv: ByteArray, message: ByteArray): ByteArray {
            var cipherText = ByteArray(0)
            try {
                val cipher = Cipher.getInstance(AES_MODE)
                val ivSpec = IvParameterSpec(iv)
                cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec)
                cipherText = cipher.doFinal(message)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return cipherText
        }

        /**
         * Decrypt and decode ciphertext using 256-bit AES with key generated from password
         *
         * @param key
         * @param base64EncodedCipherText the encrpyted message encoded with base64
         * @return message in Plain text (String UTF-8)
         */

        //fun decrypt(key: String, base64EncodedCipherText: String, IV: String): String {
        fun decrypt(base64EncodedCipherText: String): String {
            var deceyptedString = ""
            try {
                val ivBytes = Constants.IVkey//IV.toByteArray()
                val keyBytes = Constants.IVkey//key.toByteArray(charset("UTF-8"))

                //val ivBytes = IV.toByteArray()
                //val keyBytes = key.toByteArray(charset("UTF-8"))

                val secretKey = SecretKeySpec(keyBytes, "AES")
                val decodedCipherText = Base64.decode(base64EncodedCipherText, Base64.NO_WRAP)
                val decryptedBytes =
                    decrypt(secretKey, ivBytes, decodedCipherText)
                deceyptedString = String(decryptedBytes, charset(CHARSET))
            } catch (e: UnsupportedEncodingException) {
                if (DEBUG_LOG_ENABLED) {
                    Utilities.printLogError("$TAG---UnsupportedEncodingException--->$e")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            //Utilities.printLogError("deceyptedString--->$deceyptedString")
            return deceyptedString
        }

        /**
         * More flexible AES decrypt that doesn't encode
         *
         * @param key               AES key typically 128, 192 or 256 bit
         * @param iv                Initiation Vector
         * @param decodedCipherText in bytes (assumed it's already been decoded)
         * @return Decrypted message cipher text (not encoded)
         */
        fun decrypt(key: SecretKeySpec, iv: ByteArray, decodedCipherText: ByteArray): ByteArray {
            var decryptedBytes = ByteArray(0)
            try {
                val cipher = Cipher.getInstance(AES_MODE)
                val ivSpec = IvParameterSpec(iv)
                cipher.init(Cipher.DECRYPT_MODE, key, ivSpec)
                decryptedBytes = cipher.doFinal(decodedCipherText)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return decryptedBytes
        }

        /*private static void log(String what, String value)
		    {
		        if (DEBUG_LOG_ENABLED)
		            Log.ic_pdf(TAG, what + "[" + value.length() + "] [" + value + "]");
		    }*/

        private fun log(what: String, bytes: ByteArray) {
            if (DEBUG_LOG_ENABLED) {
                //Utilities.printLog(what + "[" + bytes.size + "] [" + bytesToHex(bytes) + "]")
            }
        }

        fun hmac(algorithm: String?, key: ByteArray?, message: ByteArray?): ByteArray {
            val mac = Mac.getInstance(algorithm)
            mac.init(SecretKeySpec(key, algorithm))
            return mac.doFinal(message)
        }

        /**
         * Converts byte array to hexidecimal useful for logging and fault finding
         *
         * @param bytes
         * @return
         */
        fun bytesToHex(bytes: ByteArray): String {
            val hexArray = "0123456789abcdef".toCharArray()
            val hexChars = CharArray(bytes.size * 2)
            var j = 0
            var v: Int
            while (j < bytes.size) {
                v = (bytes[j] and 0xFF.toByte()).toInt()
                hexChars[j * 2] = hexArray[v ushr 4]
                hexChars[j * 2 + 1] = hexArray[v and 0x0F]
                j++
            }
            return String(hexChars)
        }

        fun generateHashWithHmac256(key: String, message: String): String? {
            //Utilities.printLog("encryptSha256--> message--> " + message)
            //val key = SecretKeySpec(key.toByteArray(charset("UTF-8")), "HmacSHA256")
            val key = SecretKeySpec(Constants.IVkey, "HmacSHA256")
            val mac = Mac.getInstance("HmacSHA256")
            mac.init(key)
            val bytes = mac.doFinal(message.toByteArray(charset("UTF-8")))
            //return String(Base64.encodeBase64(bytes))
            return Base64.encodeToString(bytes, Base64.NO_WRAP)
        }
    }

}

