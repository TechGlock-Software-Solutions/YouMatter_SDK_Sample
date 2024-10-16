package com.techglock.health.app.common.utils

import android.animation.ObjectAnimator
import android.app.Activity
import android.app.KeyguardManager
import android.app.NotificationManager
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Rect
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.CancellationSignal
import android.provider.DocumentsContract
import android.util.Base64
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.CookieManager
import android.webkit.WebStorage
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.techglock.health.app.BuildConfig
import com.techglock.health.app.R
import com.techglock.health.app.common.base.DialogFullScreenView
import com.techglock.health.app.common.constants.CleverTapConstants
import com.techglock.health.app.common.constants.Configuration
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.constants.NavigationConstants
import com.techglock.health.app.common.constants.PreferenceConstants
import com.techglock.health.app.common.fitness.FitnessDataManager
import com.techglock.health.app.home.common.PolicyDataSingleton
import com.techglock.health.app.home.common.WellfieSingleton
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.squareup.picasso.Picasso
import org.json.JSONObject
import timber.log.Timber
import java.io.File
import java.math.BigDecimal
import java.math.RoundingMode
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.math.roundToInt


object Utilities {

    private val gson: Gson = GsonBuilder().create()
    private val prettyGson: Gson = GsonBuilder().setPrettyPrinting().create()

    private val appColorHelper = AppColorHelper.instance!!

    lateinit var preferenceUtils: PreferenceUtils

    fun initPreferenceUtils(preferenceUtils: PreferenceUtils) {
        this.preferenceUtils = preferenceUtils
    }

    fun dip2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    fun px2dip(context: Context, pxValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    fun getHashKey(context: Context): String {
        try {
            val info = context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey = String(Base64.encode(md.digest(), Base64.DEFAULT))
                printLogError("FACEBOOK KEYHASH--->$hashKey")
                return hashKey
            }
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            printLogError("FACEBOOK KEYHASH--->UNABLE TO GENERATE")
        } catch (e: Exception) {
            e.printStackTrace()
            printLogError("FACEBOOK KEYHASH--->UNABLE TO GENERATE")
        }
        return ""
    }

    fun isNullOrEmpty(data: String?): Boolean {
        var result = false
        try {
            result = data == null || data == "" || data.equals(
                "null",
                ignoreCase = true
            ) || data == "." || data.trim { it <= ' ' }.isEmpty()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return result
    }

    fun isNullOrEmptyOrZero(data: String?): Boolean {
        var result = false
        try {
            result = data == null || data == "" || data.equals(
                "null",
                ignoreCase = true
            ) || data == "." || data == "0" || data == "0.0"
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return result
    }

    fun hideKeyboard(view: View, context: Context) {
        try {
            val views = view.findViewById<View>(android.R.id.content)
            if (views != null) {
                val imm = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(views.windowToken, 0)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun hideKeyboard(activity: Activity) {
        try {
            val view = activity.findViewById<View>(android.R.id.content)
            if (view != null) {
                val imm = activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun showKeyboard(view: View, context: Context) {
        val imm = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(
            view, InputMethodManager.SHOW_IMPLICIT
            /* WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE*/
        )
    }

    /*fun showToolTip( view : View,msg : String,context: Context ) {
        Tooltip.on(view)
            .text(msg)
            .iconStart(android.R.drawable.ic_dialog_info)
            .iconStartSize(30, 30)
            .color(ContextCompat.getColor(context,R.color.colorPrimary))
            .border(ContextCompat.getColor(context,R.color.colorPrimary), 1f)
            .clickToHide(true)
            .corner(5)
            .position(Position.TOP)
            .show(2000)
    }*/

    fun toastMessageLong(context: Context?, message: String) {
        try {
            if (context != null && !isNullOrEmpty(message)) {
                val toast = Toast.makeText(context, message, Toast.LENGTH_LONG)
                //toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun toastMessageShort(context: Context?, message: String) {
        try {
            if (context != null && !isNullOrEmpty(message)) {
                val toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
                //toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /*    fun toastMessageLong(context: Context?, message: String) {
            try {
                if (context != null && !isNullOrEmpty(message)) {
                    val customToast = Toast(context).also {
                        // View and duration has to be set
                        val viewToast = LayoutInflater.from(context).inflate(R.layout.custom_toast, null)
                        it.view = viewToast
                        it.duration = Toast.LENGTH_LONG
                        it.setText(message)
                        it.setGravity(Gravity.CENTER, 0, 0)
                        //it.setMargin(0.1f, 0.2f)
                    }
                    customToast.show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun toastMessageShort(context: Context?, message: String) {
            try {
                if (context != null && !isNullOrEmpty(message)) {
                    val customToast = Toast(context).also {
                        // View and duration has to be set
                        val viewToast = LayoutInflater.from(context).inflate(R.layout.custom_toast, null)
                        it.view = viewToast
                        it.duration = Toast.LENGTH_SHORT
                        it.setText(message)
                        it.setGravity(Gravity.CENTER, 0, 0)
                        //it.setMargin(0.1f, 0.2f)
                    }
                    customToast.show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }*/

    /*    fun toastMessageLong(context: Context?, message: String) {
            try {
                if (context != null && !isNullOrEmpty(message)) {
                    var localResource = LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
                    val toast = Toast.makeText(context, message, Toast.LENGTH_LONG)
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                        toast.setGravity(Gravity.CENTER, 0, 0)
                        val view = toast.view
                        view?.background?.colorFilter = PorterDuffColorFilter(appColorHelper.secondaryColor(),PorterDuff.Mode.SRC_IN)
                        val text = view?.findViewById<TextView>(android.R.id.message)
                        text?.setTextColor(ContextCompat.getColor(context, R.color.white))
                    }
                    toast.show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun toastMessageShort(context: Context?, message: String) {
            try {
                if (context != null && !isNullOrEmpty(message)) {
                    val toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                        toast.setGravity(Gravity.CENTER, 0, 0)
                        val view = toast.view
                        view!!.background.colorFilter = PorterDuffColorFilter(appColorHelper.secondaryColor(),PorterDuff.Mode.SRC_IN)
                        val text = view.findViewById<TextView>(android.R.id.message)
                        text.setTextColor(ContextCompat.getColor(context, R.color.white))
                    }
                    toast.show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }*/

    fun findTypeOfDocument(fileName: String): String {
        val extension = FileUtils.getFileExt(fileName)
        printLog("Extension : $extension")
        var documentType = "Unknown"
        // before uploading verifing it's extension
        if (extension.equals("JPEG", ignoreCase = true)
            || extension.equals("jpg", ignoreCase = true)
            || extension.equals("PNG", ignoreCase = true)
            || extension.equals("png", ignoreCase = true)
        ) {
            documentType = "IMAGE"
        } else if (extension.equals("PDF", ignoreCase = true)
            || extension.equals("pdf", ignoreCase = true)
        ) {
            documentType = "PDF"
        } else if (extension.equals("DOC", ignoreCase = true)
            || extension.equals("doc", ignoreCase = true)
            || extension.equals("docx", ignoreCase = true)
            || extension.equals("DOCX", ignoreCase = true)
        ) {
            documentType = "DOC"
        }
        return documentType
    }

    /**
     * detaches Spinner Window
     *
     * @param spinner
     */
    fun detachSpinnerWindow(spinner: Spinner) {
        try {
            val method = Spinner::class.java.getDeclaredMethod("onDetachedFromWindow")
            method.isAccessible = true
            method.invoke(spinner)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getRelationshipByRelationshipCode(relationshipCode: String, context: Context): String {
        var relationship = ""

        when (relationshipCode) {
            Constants.FATHER_RELATIONSHIP_CODE -> {
                relationship = context.resources.getString(R.string.FATHER)
            }

            Constants.MOTHER_RELATIONSHIP_CODE -> {
                relationship = context.resources.getString(R.string.MOTHER)
            }

            Constants.SON_RELATIONSHIP_CODE -> {
                relationship = context.resources.getString(R.string.SON)
            }

            Constants.DAUGHTER_RELATIONSHIP_CODE -> {
                relationship = context.resources.getString(R.string.DAUGHTER)
            }

            Constants.GRANDFATHER_RELATIONSHIP_CODE -> {
                relationship = context.resources.getString(R.string.GRAND_FATHER)
            }

            Constants.GRANDMOTHER_RELATIONSHIP_CODE -> {
                relationship = context.resources.getString(R.string.GRAND_MOTHER)
            }

            Constants.HUSBAND_RELATIONSHIP_CODE -> {
                relationship = context.resources.getString(R.string.HUSBAND)
            }

            Constants.WIFE_RELATIONSHIP_CODE -> {
                relationship = context.resources.getString(R.string.WIFE)
            }

            Constants.BROTHER_RELATIONSHIP_CODE -> {
                relationship = context.resources.getString(R.string.BROTHER)
            }

            Constants.SISTER_RELATIONSHIP_CODE -> {
                relationship = context.resources.getString(R.string.SISTER)
            }

            Constants.SELF_RELATIONSHIP_CODE -> {
                relationship = context.resources.getString(R.string.SELF)
            }
        }

        return relationship
    }

    fun getRelativeImgIdWithGender(relationshipCode: String, gender: String): Int {
        var relationImgTobeAdded: Int = R.drawable.img_husband

        if (relationshipCode == "ADD") {
            relationImgTobeAdded = R.drawable.img_person_add
        } else if (relationshipCode == "SELF" && gender == "1") {
            relationImgTobeAdded = R.drawable.img_husband
            //relationImgTobeAdded = R.drawable.img_brother
        } else if (relationshipCode == "SELF" && gender == "2") {
            relationImgTobeAdded = R.drawable.img_wife
        } else if (relationshipCode == "FAT") {
            relationImgTobeAdded = R.drawable.img_father
        } else if (relationshipCode == "MOT") {
            relationImgTobeAdded = R.drawable.img_mother
        } else if (relationshipCode == "SON") {
            relationImgTobeAdded = R.drawable.img_son
        } else if (relationshipCode == "DAU") {
            relationImgTobeAdded = R.drawable.img_daughter
        } else if (relationshipCode == "GRF") {
            relationImgTobeAdded = R.drawable.img_gf
        } else if (relationshipCode == "GRM") {
            relationImgTobeAdded = R.drawable.img_gm
        } else if (relationshipCode == "HUS") {
            relationImgTobeAdded = R.drawable.img_husband
        } else if (relationshipCode == "WIF") {
            relationImgTobeAdded = R.drawable.img_wife
        } else if (relationshipCode == "BRO") {
            relationImgTobeAdded = R.drawable.img_brother
        } else if (relationshipCode == "SIS") {
            relationImgTobeAdded = R.drawable.img_sister
        } else if (relationshipCode.isEmpty() && gender == "1") {
            relationImgTobeAdded = R.drawable.img_husband
        } else if (relationshipCode.isEmpty() && gender == "2") {
            relationImgTobeAdded = R.drawable.img_wife
        }
        return relationImgTobeAdded
    }

    fun getGenderByCode(code: Int, context: Context): String {
        var gender = ""
        when (code) {
            1 -> {
                gender = context.resources.getString(R.string.MALE)
            }

            2 -> {
                gender = context.resources.getString(R.string.FEMALE)
            }
        }
        return gender
    }

    fun getDisplayGender(code: String): String {
        var gender = "Male"
        when (code) {
            "1" -> {
                gender = "Male"
            }

            "2" -> {
                gender = "Female"
            }
        }
        return gender
    }

    fun getGenderCodeForCleverTap(code: String): String {
        var gender = "M"
        when (code) {
            "1" -> {
                gender = "M"
            }

            "2" -> {
                gender = "F"
            }
        }
        return gender
    }

    fun getGenderImageCode(code: Int): Int {
        var imgGender: Int = R.drawable.img_husband
        when (code) {
            1 -> {
                imgGender = R.drawable.img_husband
            }

            2 -> {
                imgGender = R.drawable.img_husband
            }
        }
        return imgGender
    }

    fun isEmailValid(email: String): Boolean {
        //return email.contains("@")
        val pattern: Pattern
        val matcher: Matcher
        val EMAIL_PATTERN =
            "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
        pattern = Pattern.compile(EMAIL_PATTERN)
        matcher = pattern.matcher(email)
        return matcher.matches()
    }

    fun isPasswordValid(password: String): Boolean {
        return password.length >= 6
    }

    fun isMobileValid(mobileNumber: String): Boolean {
        return mobileNumber.length == 10
    }

    fun isValidPhoneNumber(phoneNumber: String): Boolean {
        val Regex = "[^\\d]"
        val PhoneDigits = phoneNumber.replace(Regex.toRegex(), "")
        //val isValid = PhoneDigits.length >= 8 && !phoneNumber.startsWith("0")
        val isValid = PhoneDigits.length == 10 && !phoneNumber.startsWith("0")
        Utilities.printLog("isValidPhoneNumber--->$isValid")
        return isValid
    }

    fun isPhoneValid(phoneNumber: String): Boolean {
        val pattern =
            Pattern.compile("\\ic_pdf{10}|(?:\\ic_pdf{3}-){2}\\ic_pdf{4}|\\(\\ic_pdf{3}\\)\\ic_pdf{3}-?\\ic_pdf{4}")
        return phoneNumber.matches(pattern.toRegex())
    }

    fun roundOffPrecision(value: Double, precision: Int): Double {
        val scale = Math.pow(10.0, precision.toDouble()).toInt()
        return Math.round(value * scale).toDouble() / scale
    }

    fun roundOffPrecisionToTwoDecimalPlaces(value: Double): Double {
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.CEILING
        //printLogError("Converted--->${df.format(value).toDouble()}")
        return df.format(value).toDouble()
    }

    fun roundOffPrecisionToTwoDecimalPlaces(inputValue: String): Double {
        val value = inputValue.replace(",","").toDouble()
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.CEILING
        //printLogError("Converted--->${df.format(value).toDouble()}")
        return df.format(value).toDouble()
    }

    /*    fun roundOffPrecision(value: Double, precision: Int): Double {
            val scale = 10.0.pow(precision.toDouble()).toInt()
            return (value * scale) / scale
            //return (value * scale).roundToInt().toDouble() / scale
        }*/

    fun round(d: Double, decimalPlace: Int): Float {
        try {
            var bd = BigDecimal(d.toString())
            bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP)
            return bd.toFloat()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return d.toFloat()
    }

    /**
     * convert FeetInch To Kg
     *
     * @param strFeetValue
     * @param strInchValue
     * @return
     */
    fun convertFeetInchToCm(strFeetValue: String, strInchValue: String): String {
        var strConvertedValue = ""
        if (!isNullOrEmpty(strFeetValue) && !isNullOrEmpty(strInchValue)) {
            val cm = java.lang.Double.parseDouble(strFeetValue) * 30.48 +
                    java.lang.Double.parseDouble(strInchValue) * 2.54
            strConvertedValue = cm.toString() + ""
        }
        return strConvertedValue
    }

    /*    fun getAppFolderLocation(context: Context): String {
            val subirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
            return subirectory + "/" + context.resources.getString(R.string.app_name)
        }*/

    fun getAppFolderLocation(context: Context): String {
        var appFolderLocation =
            Constants.primaryStorage + "/" + context.resources.getString(R.string.app_name)
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val lmn = context.externalMediaDirs
                for (i in lmn) {
                    if (i.absolutePath.contains(context.packageName)) {
                        Utilities.printLogError(i.absolutePath)
                        appFolderLocation =
                            i.absolutePath + "/" + context.resources.getString(R.string.app_name)
                        break
                    }
                }
            }
            val dir = File(appFolderLocation)
            if (!dir.exists()) {
                val directoryCreated = dir.mkdirs()
                Utilities.printLogError("DirectoryCreated--->$directoryCreated")
                Utilities.printLogError("DirectoryName--->$dir")
            } else {
                Utilities.printLogError("DirectoryAlreadyExist")
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return appFolderLocation
    }

    fun getAppName(context: Context): String {
        return context.resources.getString(R.string.app_name)
    }

/*    fun getAppVersion(context: Context): Int {
        return try {
            val packageInfo: PackageInfo =
                context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            throw RuntimeException("Could not get package name: $e")
        }
    }*/

    fun getAppVersion(context: Context): Int {
        return Constants.versionCode
    }

    fun getVersionName(context: Context): String {
        return Constants.versionName
    }

/*    fun getVersionName(context: Context): String {
        return try {
            val packageInfo: PackageInfo =
                context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            throw RuntimeException("Could not get package name: $e")
        }
    }*/

    fun getDocumentTypeFromExt(extension: String): String {
        var documentType = ""
        if (extension.equals("JPEG", ignoreCase = true) ||
            extension.equals("jpg", ignoreCase = true) ||
            extension.equals("PNG", ignoreCase = true) ||
            extension.equals("png", ignoreCase = true)
        ) {
            documentType = "IMAGE"
        } else if (extension.equals("PDF", ignoreCase = true) || extension.equals(
                "pdf",
                ignoreCase = true
            )
        ) {
            documentType = "PDF"
        } else if (extension.equals("txt", ignoreCase = true) ||
            extension.equals("doc", ignoreCase = true) ||
            extension.equals("docx", ignoreCase = true)
        ) {
            documentType = "DOC"
        }
        return documentType
    }

    fun isAcceptableDocumentType(extension: String): Boolean {
        var isAcceptable = false
        var documentType = ""
        //val extension1 = RealPathUtil.getFileExt(filePath)
        printLogError("Extension : $extension")

        if (extension.equals("JPEG", ignoreCase = true) ||
            extension.equals("jpg", ignoreCase = true) ||
            extension.equals("PNG", ignoreCase = true) ||
            extension.equals("png", ignoreCase = true)
        ) {
            documentType = "IMAGE"
        } else if (extension.equals("PDF", ignoreCase = true) || extension.equals(
                "pdf",
                ignoreCase = true
            )
        ) {
            documentType = "PDF"
        } else if (extension.equals("txt", ignoreCase = true) ||
            extension.equals("doc", ignoreCase = true) ||
            extension.equals("docx", ignoreCase = true)
        ) {
            documentType = "DOC"
        }
        printLogError("documentType : $documentType")
        if (!isNullOrEmpty(documentType)) {
            isAcceptable = true
        }
        return isAcceptable
    }

    fun deleteFileFromLocalSystem(path: String) {
        val file = File(path)
        if (file.exists()) {
            val isDeleted = file.delete()
            printLogError("isDeleted--->$isDeleted")
        } else {
            printLogError("File not exist")
        }
    }

    fun deleteFile(file: File) {
        if (file.exists()) {
            val isDeleted = file.delete()
            printLogError("isDeleted--->$isDeleted")
        } else {
            printLogError("File not exist")
        }
    }

    fun deleteDocumentFileFromLocalSystem(context: Context, uri: Uri, filename: String) {
        try {
            val isDeleted = DocumentsContract.deleteDocument(context.contentResolver, uri)
            printLogError("Deleted : ${filename}--->$isDeleted")
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    fun getVitalParameterData(parameter: String, context: Context): VitalParameter {
        val vitalParameter = VitalParameter()

        when (parameter) {

            context.resources.getString(R.string.FT) -> {
                vitalParameter.unit = context.resources.getString(R.string.FT)
                vitalParameter.minRange = 4
                vitalParameter.maxRange = 7
            }

            context.resources.getString(R.string.CM) -> {
                vitalParameter.unit = context.resources.getString(R.string.CM)
                vitalParameter.minRange = 120
                vitalParameter.maxRange = 240
            }

            context.resources.getString(R.string.LBS) -> {
                vitalParameter.unit = context.resources.getString(R.string.LBS)
                vitalParameter.minRange = 64
                vitalParameter.maxRange = 550
            }

            context.resources.getString(R.string.KG) -> {
                vitalParameter.unit = context.resources.getString(R.string.KG)
                vitalParameter.minRange = 30
                vitalParameter.maxRange = 250
            }

        }
        return vitalParameter
    }

    fun getHraObservationColorFromScore(score: Int): Int {
        var wellnessScore = score
        if (wellnessScore <= 0) {
            wellnessScore = 0
        }
        var color: Int = appColorHelper.textColor
        when {
            wellnessScore in 0..15 -> {
                color = R.color.high_risk
            }

            wellnessScore in 16..45 -> {
                color = R.color.moderate_risk
            }

            wellnessScore in 46..85 -> {
                color = R.color.healthy_risk
            }

            wellnessScore > 85 -> {
                color = R.color.optimum_risk
            }
        }
        return color
    }

    fun clearStepsData(context: Context) {
        val intent = Intent()
        intent.action = Constants.CLEAR_FITNESS_DATA
        intent.component =
            ComponentName(NavigationConstants.APPID, NavigationConstants.FITNESS_BROADCAST_RECEIVER)
        //intent.putExtra(GlobalConstants.EVENT, event)
        context.sendBroadcast(intent)
    }

    fun printLog(message: String) {
        Timber.i(message)
    }

    fun printLogError(message: String) {
        Timber.e(message)
    }

    fun printException(e: Exception) {
        if (BuildConfig.DEBUG) {
            e.printStackTrace()
        }
    }

    fun printData(tag: String, list: Any, toPretty: Boolean = true) {
        printListData(tag, list, toPretty)
    }

    private fun printListData(tag: String, list: Any, toPretty: Boolean = true) {
        try {
            var jsonArrayString: String = if (toPretty) {
                prettyGson.toJson(list)
            } else {
                gson.toJson(list)
            }
            jsonArrayString = jsonArrayString.replace("\\\"", "\"")
                .replace("\\r\\n", "")
                .replace("\\\\r\\\\n", "")
                .replace("\\\\\"", "\"")
                .replace("\"{", "{")
                .replace("\"[", "[")
                .replace("}\"", "}")
                .replace("]\"", "]")
            printLog("$tag---> $jsonArrayString")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun storeUserPreference(key: String, value: String) {
        printLogError("Storing $key--->$value")
        preferenceUtils.storePreference(key, value)
    }

    fun storeBooleanPreference(key: String, value: Boolean) {
        printLogError("Storing $key--->$value")
        preferenceUtils.storeBooleanPreference(key, value)
    }

    fun getUserPreference(key: String): String {
        val userPreference = preferenceUtils.getPreference(key)
        printLogError("$key--->$userPreference")
        return userPreference
    }

    fun getBooleanPreference(key: String): Boolean {
        val userPreference = preferenceUtils.getBooleanPreference(key)
        printLogError("$key--->$userPreference")
        return userPreference
    }

    fun formatNumberWithComma(number: Any): String {
        val abc = NumberFormat.getNumberInstance(Locale.US).format(number)
        return abc.toString()
    }

    fun formatNumberDecimalWithComma(number: Double): String {
        var finalOutput = number.toString()
        try {
            val dec = DecimalFormat("##,##,###.##")
            finalOutput = dec.format(number)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return finalOutput.toString()
    }

    fun formatNumberDecimalWithComma(number: Int): String {
        var finalOutput = number.toString()
        try {
            val dec = DecimalFormat("##,##,###")
            finalOutput = dec.format(number)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return finalOutput.toString()
    }

    fun convertStringToPascalCase(input: String): String {
        return input.lowercase().split(" ").joinToString(" ") { inp ->
            inp.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ENGLISH) else it.toString() }
        }
    }

    fun Activity.getRootView(): View {
        return findViewById<View>(android.R.id.content)
    }

    private fun Context.convertDpToPx(dp: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            this.resources.displayMetrics
        )
    }

    fun Activity.isKeyboardOpen(): Boolean {
        val visibleBounds = Rect()
        this.getRootView().getWindowVisibleDisplayFrame(visibleBounds)
        val heightDiff = getRootView().height - visibleBounds.height()
        val marginOfError = this.convertDpToPx(50F).roundToInt()
        return heightDiff > marginOfError
    }

    fun Activity.isKeyboardClosed(): Boolean {
        return !this.isKeyboardOpen()
    }

    fun viewImage(context: Context, completeFilePath: String) {
        try {
            if (!isNullOrEmpty(completeFilePath)) {
                val file = File(completeFilePath)
                if (file.exists()) {
                    val type = "image/*"
                    val intent = Intent(Intent.ACTION_VIEW)
                    val uri = Uri.fromFile(file)
                    intent.setDataAndType(uri, type)
                    //intent.setDataAndType(FileProvider.getUriForFile(this, getPackageName().toString() + ".provider", file), type)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
                    try {
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        toastMessageShort(
                            context,
                            context.resources.getString(R.string.ERROR_UNABLE_TO_OPEN_FILE)
                        )
                    }
                } else {
                    toastMessageShort(
                        context,
                        context.resources.getString(R.string.ERROR_FILE_DOES_NOT_EXIST)
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showFullImageWithBitmap(bitmap: Bitmap, context: Context, isImage: Boolean) {
        try {
            val dialog = DialogFullScreenView(context, isImage, "", bitmap)
            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showFullImageWithImgUrl(imgUrl: String, context: Context, isImage: Boolean) {
        try {
            val dialog = DialogFullScreenView(context, isImage, imgUrl, null)
            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getCategoryImageByCode(code: String): Int {
        return when (code) {
            "LAB" -> {
                R.drawable.img_pathology_report
            }

            "HOS" -> {
                R.drawable.img_hos_report
            }

            "PRE" -> {
                R.drawable.img_doctor_prescription
            }

            "DIET_PLAN" -> {
                R.drawable.img_diet_plan
            }

            "FIT_PLAN" -> {
                R.drawable.img_fitness_plan
            }

            "HRAREPORT" -> {
                R.drawable.img_hra_report
            }

            "OTR" -> {
                R.drawable.img_other
            }

            else -> {
                R.drawable.img_other
            }
        }
    }

    fun getCategoryByCode(code: String, context: Context): String {
        return when {
            code.equals("LAB", ignoreCase = true) -> {
                context.resources.getString(R.string.PATHOLOGY_REPORT)
            }

            code.equals("HOS", ignoreCase = true) -> {
                context.resources.getString(R.string.HOSPITAL_REPORT)
            }

            code.equals("REC", ignoreCase = true) -> {
                context.resources.getString(R.string.RECEIPT)
            }

            code.equals("PRE", ignoreCase = true) -> {
                context.resources.getString(R.string.DOCTOR_PRESCRIPTION)
            }

            code.equals("OTR", ignoreCase = true) -> {
                context.resources.getString(R.string.OTHER_DOCUMENT)
            }

            code.equals("HRAREPORT", ignoreCase = true) -> {
                context.resources.getString(R.string.HRA_REPORT)
            }

            code.equals("DIET_PLAN", ignoreCase = true) -> {
                context.resources.getString(R.string.DIET_PLAN)
            }

            code.equals("FIT_PLAN", ignoreCase = true) -> {
                context.resources.getString(R.string.FITNESS_PLAN)
            }

            else -> {
                context.resources.getString(R.string.OTHER_DOCUMENT)
            }
        }
    }

    fun getLanguageDescription(item: String, languageCode: String): String {
        var result = ""
        if (languageCode.equals("en", true)) {
            when (item.uppercase()) {
                "HEIGHT", "WEIGHT", "SYSTOLIC", "DIASTOLIC", "HIP", "WAIST", "PULSE" -> {
                    result = item
                }
            }
        } else {
            when (item.uppercase()) {
                "HEIGHT" -> {
                    result = "CHIỀU CAO"
                }

                "WEIGHT" -> {
                    result = "CÂN NẶNG"
                }

                "SYSTOLIC" -> {
                    result = "Tâm thu"
                }

                "DIASTOLIC" -> {
                    result = "Tâm trương"
                }

                "HIP" -> {
                    result = "HÔNG"
                }

                "WAIST" -> {
                    result = "Vòng eo"
                }

                "PULSE" -> {
                    result = "Xung"
                }
            }
        }
        return result
    }

    fun goToPlayStore(context: Context) {
        try {
            //Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
            val uri =
                Uri.parse("http://play.google.com/store/apps/details?id=" + context.packageName)
            val goToPlayStore = Intent(Intent.ACTION_VIEW, uri)
            // To count with Play market backstack, After pressing back button,
            // to take back to our application, we need to add following flags to intent.
            goToPlayStore.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_MULTIPLE_TASK or Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
            context.startActivity(goToPlayStore)
        } catch (e: Exception) {
            val uri =
                Uri.parse("http://play.google.com/store/apps/details?id=" + context.packageName)
            val intent = Intent(Intent(Intent.ACTION_VIEW, uri))
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }



    fun logout(context: Context, activity: Activity): Boolean {
        Configuration.EntityID = "0"
//        invalidateAktivoData(context,activity)
        FitnessDataManager(context).signOutGoogleAccount()
        UserSingleton.getInstance()!!.clearData()
        WellfieSingleton.getInstance()!!.clearData()
        PolicyDataSingleton.getInstance()!!.clearData()
        clearStepsData(context)
        resetPreferencesOnLogout()
        //Dismiss all notifications from Notification tray,
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
        return true
    }

    /*private fun invalidateAktivoData(context: Context,activity: Activity) {
        try {
            val aktivoManager = AktivoManager.getInstance(context)
            aktivoManager!!.invalidateUser(activity).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(object : CompletableObserver {
                    override fun onSubscribe(d: Disposable) {}
                    override fun onComplete() {
                        //toastMessageShort(this@HomeMainActivity,"User invalidated")
                        printLogError("User invalidated")
                    }

                    override fun onError(e: Throwable) {}
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }*/

    private fun resetPreferencesOnLogout() {
        try {
            // Clear all the Application Cache, Web SQL Database and the HTML5 Web Storage
            WebStorage.getInstance().deleteAllData()
            // Clear all the cookies
            CookieManager.getInstance().removeAllCookies(null)
            CookieManager.getInstance().flush()

            preferenceUtils.storeBooleanPreference(PreferenceConstants.IS_LOGIN,false)
            preferenceUtils.storeBooleanPreference(PreferenceConstants.IS_OTP_AUTHENTICATED,false)
            preferenceUtils.storeBooleanPreference(PreferenceConstants.ENABLE_PUSH_NOTIFICATION,true)
            preferenceUtils.storeBooleanPreference(PreferenceConstants.IS_FITNESS_DATA_NOT_SYNC,true)
            preferenceUtils.clearPreference(PreferenceConstants.TOKEN)
            preferenceUtils.clearPreference(PreferenceConstants.FCM_REGISTRATION_ID)
            //preferenceUtils.clearPreference(PreferenceConstants.ADMIN_PERSON_ID)
            //preferenceUtils.clearPreference(PreferenceConstants.PERSONID)
            //preferenceUtils.clearPreference(PreferenceConstants.ACCOUNTID)
            preferenceUtils.resetPreferenceWithZero(PreferenceConstants.ADMIN_PERSON_ID)
            preferenceUtils.resetPreferenceWithZero(PreferenceConstants.PERSONID)
            preferenceUtils.resetPreferenceWithZero(PreferenceConstants.ACCOUNTID)
            preferenceUtils.clearPreference(PreferenceConstants.DOB)
            preferenceUtils.clearPreference(PreferenceConstants.JOINING_DATE)
            preferenceUtils.clearPreference(PreferenceConstants.EMAIL)
            preferenceUtils.clearPreference(PreferenceConstants.PHONE)
            preferenceUtils.clearPreference(PreferenceConstants.POLICY_MOBILE_NUMBER)
            preferenceUtils.clearPreference(PreferenceConstants.RELATIONSHIPCODE)
            preferenceUtils.clearPreference(PreferenceConstants.FIRSTNAME)
            preferenceUtils.clearPreference(PreferenceConstants.GENDER)
            preferenceUtils.clearPreference(PreferenceConstants.SELECTION_PARAM)
            preferenceUtils.clearPreference(PreferenceConstants.AKTIVO_MEMBER_ID)
            preferenceUtils.clearPreference(PreferenceConstants.AKTIVO_ACCESS_TOKEN)
            preferenceUtils.clearPreference(PreferenceConstants.AKTIVO_REFRESH_TOKEN)
            preferenceUtils.clearPreference(PreferenceConstants.MPT_RESULT_ID)
            preferenceUtils.clearPreference(PreferenceConstants.MPT_RESULT_PAGE_URL)
            preferenceUtils.clearPreference(PreferenceConstants.MPT_COHORT_TITLE)
            preferenceUtils.clearPreference(PreferenceConstants.MPT_COHORT_ICON_URL)
            preferenceUtils.storeBooleanPreference(PreferenceConstants.IS_BOI_EMPLOYEE,false)
            preferenceUtils.storeBooleanPreference(PreferenceConstants.IS_UBI_EMPLOYEE,false)
            preferenceUtils.storeBooleanPreference(PreferenceConstants.IS_SUD_LIFE_EMPLOYEE,false)
            preferenceUtils.storeBooleanPreference(PreferenceConstants.IS_DARWINBOX_DETAILS_AVAILABLE,false)
            preferenceUtils.clearPreference(PreferenceConstants.ORG_NAME)
            preferenceUtils.clearPreference(PreferenceConstants.ORG_EMPLOYEE_ID)
        } catch ( e:Exception ) {
            e.printStackTrace()
        }
    }

    fun getWaterDropImageByValue(percent: Double): Int {
        return when (percent.roundToInt()) {
            in 1..5 -> {
                R.drawable.img_water_drop_5
            }

            in 6..10 -> {
                R.drawable.img_water_drop_10
            }

            in 11..20 -> {
                R.drawable.img_water_drop_20
            }

            in 21..30 -> {
                R.drawable.img_water_drop_30
            }

            in 31..40 -> {
                R.drawable.img_water_drop_40
            }

            in 41..50 -> {
                R.drawable.img_water_drop_50
            }

            in 51..60 -> {
                R.drawable.img_water_drop_60
            }

            in 61..70 -> {
                R.drawable.img_water_drop_70
            }

            in 71..80 -> {
                R.drawable.img_water_drop_80
            }

            in 81..90 -> {
                R.drawable.img_water_drop_90
            }

            in 91..99 -> {
                R.drawable.img_water_drop_95
            }

            in 100..10000 -> {
                R.drawable.img_water_drop_100
            }

            else -> {
                R.drawable.img_water_drop_0
            }
        }
    }

    fun getWellfieParameterIdByCode(code: String): Int {
        return when (code) {
            "BP" -> 1
            "STRESS", "STRESS_INDEX" -> 2
            "HEART_RATE" -> 3
            "BREATHING_RATE" -> 4
            "BLOOD_OXYGEN" -> 5
            "BMI" -> 6
            else -> 0
        }
    }

    fun getWellfieParameterImageByCode(code: String): Int {
        return when (code) {
            "BP", "BP_SYS", "BP_DIA", "BP_STATUS" -> {
                R.drawable.img_wellfie_bp
            }

            "STRESS", "STRESS_INDEX", "STRESS_STATUS" -> {
                R.drawable.img_wellfie_stress
            }

            "HEART_RATE" -> {
                R.drawable.img_wellfie_heartrate
            }

            "BREATHING_RATE" -> {
                R.drawable.img_wellfie_breathing
            }

            "BLOOD_OXYGEN" -> {
                R.drawable.img_wellfie_oxygen
            }

            "BMI" -> {
                R.drawable.img_bmi_wellfie
            }

            else -> {
                R.drawable.img_wellfie_param
            }
        }
    }

    fun getWellfieBpColorByObservation(code: String): String {
        return when (code) {
            "NORMAL" -> "#a9d08d"
            "ELEVATED" -> "#f4b084"
            "HYP_S1", "HYP_S2", "HYP_CRISIS" -> "#ed2024"
            else -> "#000000"
        }
    }

    fun getDescriptionByModuleCode(code: String): String {
        var desc = ""
        when (code) {
            "DASHBOARD_URL" -> desc = "Dashboard"
            "ASMT_HOME_URL" -> desc = "Assessments landing page"
            "AUDIO_URL" -> desc = "Audios Landing page"
            "VIDEO_URL" -> desc = "Video Link"
            "BLOGS_URL" -> desc = "Articles Landing page"
            "TELE_URL" -> desc = "Teleconsultation"
            "THERAPIST_DASHBOARD" -> desc = "Therapist Dashboard"
            "PSYCHIATRIST_DASHBOARD" -> desc = "Psychiatrist Dashboard"
            "BOT_URL" -> desc = "Bot Link"
        }
        return desc
    }

    fun getPayPremiumButtonStateByStatus(status: String): Boolean {
        return when (status.uppercase().trim()) {
            "IN FORCE", "CONTRACT LAPSED", "PREMIUM DISCONTINUANCE" -> true
            else -> false
        }
    }

    fun redirectToChrome(url: String, context: Context) {
        //val urlString = "http://mysuperwebsite"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.setPackage("com.android.chrome")
        try {
            context.startActivity(intent)
        } catch (ex: ActivityNotFoundException) {
            // Chrome is probably not installed
            // Try with the default browser
            intent.setPackage(null);
            context.startActivity(intent);

            // Chrome browser presumably not installed and open Kindle Browser
            //intent.setPackage("com.amazon.cloud9")
            //context.startActivity(intent)
        }
    }

    fun appInstalledOrNot(packageName: String, context: Context): Boolean {
        val isInstalled: Boolean
        val pm: PackageManager = context.packageManager
        isInstalled = try {
            pm.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
        return isInstalled
    }

    fun openWhatsApp(context: Context, url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun openEmailClientIntent(context: Context, email: String) {
        try {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:") // only email apps should handle this
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            //intent.putExtra(Intent.EXTRA_SUBJECT, "")
            context.startActivity(intent)
            /*            if (intent.resolveActivity(context.packageManager) != null) {
                            context.startActivity(Intent.createChooser(intent, "Select App..."))
                        }*/
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun openPhoneDialerWithNumber(context: Context, number: String) {
        try {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$number")
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun checkBiometricSupport(context: Context): Boolean {
        val keyguardManager: KeyguardManager =
            context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        if (!keyguardManager.isKeyguardSecure) {
            printLogError("Fingerprint has not been enabled in settings.")
            return false
        }
        if (ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.USE_BIOMETRIC
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            printLogError("Fingerprint has not been enabled in settings.")
            return false
        }
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)
    }

    fun getCancellationSignal(): CancellationSignal {
        val cancellationSignal = CancellationSignal()
        cancellationSignal.setOnCancelListener {
            //toastMessageShort(requireContext(),"Authentication was cancelled by the user")
        }
        return cancellationSignal
    }

    fun openDownloadedDocumentFile(file: File, context: Context) {
        try {
            val uri = FileProvider.getUriForFile(context, context.packageName + ".provider", file)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri, "application/pdf")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
            //val openIntent = Intent.createChooser(intent,"Open using")
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            toastMessageShort(
                context,
                context.resources.getString(R.string.ERROR_NO_APPLICATION_TO_VIEW_PDF)
            )
        } catch (e: Exception) {
            e.printStackTrace()
            toastMessageShort(
                context,
                context.resources.getString(R.string.ERROR_UNABLE_TO_OPEN_FILE)
            )
        }
    }

    fun getEncryptedData(
        name: String = "",
        email: String,
        phoneNumber: String = "",
        gender: String = "1",
        dob: String = "",
        password: String,
        otp: String = "",
        employeeId: String = "",
        orgName: String = "",
        isSocial: Boolean = false,
        source: String = Constants.LOGIN_SOURCE
    ): String {
        val registerObject = JSONObject()
        registerObject.put(Constants.UserConstants.EMAIL_ADDRESS, email)
        registerObject.put(Constants.UserConstants.PASSWORD, password)
        registerObject.put(Constants.UserConstants.AUTH_TYPE,if (isSocial) Constants.SSO else Constants.NSSO)
        registerObject.put(Constants.UserConstants.PHONE_NUMBER, phoneNumber)
        registerObject.put(Constants.UserConstants.PARTNER_CODE, Constants.PartnerCode)
        //registerObject.put(Constants.UserConstants.PARTNER_CODE,"SUDLIFE")
        registerObject.put(Constants.UserConstants.OTP, otp)
        registerObject.put(Constants.UserConstants.CLUSTER_CODE, "")
        registerObject.put(Constants.UserConstants.NAME, name)
        registerObject.put(Constants.UserConstants.DOB, dob)
        registerObject.put(Constants.UserConstants.GENDER, gender)
        registerObject.put(Constants.UserConstants.EMPLOYEE_ID, employeeId)
        registerObject.put(Constants.UserConstants.ORG_NAME, orgName)
        registerObject.put(Constants.UserConstants.SOURCE, source)
        registerObject.put(Constants.UserConstants.MEDIUM, "ANDROID")
        registerObject.put(Constants.UserConstants.HANDSHAKE, "PER")
        printLogError("BEFORE -> $registerObject")
        val loginEncrypted = EncryptionUtility.encrypt(registerObject.toString())
        //printLogError("Encrypted -> $loginEncrypted")
        return loginEncrypted
    }

    fun setCampaignFeatureDetails(feature: String) {
        printLogError("Setting Campaign Feature Details")
        preferenceUtils.storePreference(Constants.CAMPAIGN_FEATURE_NAME,feature)
        preferenceUtils.storeBooleanPreference(PreferenceConstants.IS_CAMPAIGN_DETAILS_AVAILABLE,true)
    }

    fun clearCampaignFeatureDetails() {
        printLogError("Cleared Campaign Feature Details")
        preferenceUtils.storePreference(Constants.CAMPAIGN_FEATURE_NAME,"")
        preferenceUtils.storeBooleanPreference(PreferenceConstants.IS_CAMPAIGN_DETAILS_AVAILABLE,false)
    }

    fun setReferralDetails(referralName: String, referralPID: String) {
        preferenceUtils.storePreference(CleverTapConstants.REFERRAL_NAME, referralName)
        preferenceUtils.storePreference(CleverTapConstants.REFERRAL_PID, referralPID)
        preferenceUtils.storeBooleanPreference(PreferenceConstants.IS_REFERRAL_DETAILS_AVAILABLE, true)
    }

    fun setDarwinBoxDetails(darwinboxUrl: String) {
        preferenceUtils.storePreference(Constants.DARWINBOX_URL, darwinboxUrl)
        preferenceUtils.storeBooleanPreference(PreferenceConstants.IS_DARWINBOX_DETAILS_AVAILABLE, true)
    }

    fun clearDarwinBoxDetails() {
        preferenceUtils.storePreference(Constants.DARWINBOX_URL, "")
        preferenceUtils.storeBooleanPreference(
            PreferenceConstants.IS_DARWINBOX_DETAILS_AVAILABLE,
            false
        )
    }

    fun getLoginStatus(): Boolean {
        val isLogin = preferenceUtils.getBooleanPreference(PreferenceConstants.IS_LOGIN, false)
        printLogError("IsUserLoggedIn--->$isLogin")
        return isLogin
    }

    fun setEmployeeType(empType: String) {
        printLogError("UserType--->$empType")
        when (empType) {
            Constants.BOI -> {
                preferenceUtils.storeBooleanPreference(PreferenceConstants.IS_BOI_EMPLOYEE, true)
                preferenceUtils.storeBooleanPreference(PreferenceConstants.IS_UBI_EMPLOYEE, false)
                preferenceUtils.storeBooleanPreference(
                    PreferenceConstants.IS_SUD_LIFE_EMPLOYEE,
                    false
                )
            }

            Constants.UBI -> {
                preferenceUtils.storeBooleanPreference(PreferenceConstants.IS_BOI_EMPLOYEE, false)
                preferenceUtils.storeBooleanPreference(PreferenceConstants.IS_UBI_EMPLOYEE, true)
                preferenceUtils.storeBooleanPreference(
                    PreferenceConstants.IS_SUD_LIFE_EMPLOYEE,
                    false
                )
            }

            Constants.SUD_LIFE -> {
                preferenceUtils.storeBooleanPreference(PreferenceConstants.IS_BOI_EMPLOYEE, false)
                preferenceUtils.storeBooleanPreference(PreferenceConstants.IS_UBI_EMPLOYEE, false)
                preferenceUtils.storeBooleanPreference(
                    PreferenceConstants.IS_SUD_LIFE_EMPLOYEE,
                    true
                )
            }

            Constants.CUSTOMER -> {
                preferenceUtils.storeBooleanPreference(PreferenceConstants.IS_BOI_EMPLOYEE, false)
                preferenceUtils.storeBooleanPreference(PreferenceConstants.IS_UBI_EMPLOYEE, false)
                preferenceUtils.storeBooleanPreference(
                    PreferenceConstants.IS_SUD_LIFE_EMPLOYEE,
                    false
                )
            }
        }
    }

    fun getEmployeeType(): String {
        var empType = Constants.CUSTOMER
        if (preferenceUtils.getBooleanPreference(PreferenceConstants.IS_BOI_EMPLOYEE, false)) {
            empType = Constants.BOI
        } else if (preferenceUtils.getBooleanPreference(
                PreferenceConstants.IS_UBI_EMPLOYEE,
                false
            )
        ) {
            empType = Constants.UBI
        } else if (preferenceUtils.getBooleanPreference(
                PreferenceConstants.IS_SUD_LIFE_EMPLOYEE,
                false
            )
        ) {
            empType = Constants.SUD_LIFE
        }
        printLogError("EmployeeType--->$empType")
        return empType
    }

    fun getUserPhoneNumber(): String {
        return preferenceUtils.getPreference(PreferenceConstants.PHONE, "")
    }

    fun getRefID(): String {
        val refID = if( getLoginStatus() ) {
            preferenceUtils.getPreference(PreferenceConstants.ACCOUNTID, "0").ifEmpty { "0" }
        } else {
            "0"
        }
        return refID
    }

    fun getEmployeeID(): String {
        return preferenceUtils.getPreference(PreferenceConstants.ORG_EMPLOYEE_ID, "")
    }

    fun logCleverTapEmployeeEventLogin(context: Context, empType: String) {
        when (empType) {
            Constants.BOI -> CleverTapHelper.pushEvent(context, CleverTapConstants.BOI_LOGIN)
            Constants.UBI -> CleverTapHelper.pushEvent(context, CleverTapConstants.UBI_LOGIN)
            Constants.SUD_LIFE -> CleverTapHelper.pushEvent(
                context,
                CleverTapConstants.SUD_LIFE_LOGIN
            )
        }
    }

    fun logCleverTapEmployeeEventSignUp(context: Context, empType: String) {
        when (empType) {
            Constants.BOI -> CleverTapHelper.pushEvent(context, CleverTapConstants.BOI_SIGN_UP)
            Constants.UBI -> CleverTapHelper.pushEvent(context, CleverTapConstants.UBI_SIGN_UP)
            Constants.SUD_LIFE -> CleverTapHelper.pushEvent(
                context,
                CleverTapConstants.SUD_LIFE_SIGN_UP
            )
        }
    }

/*    fun getSmartHealthRedirectionUrlBasedOnEmployeeType(empType: String): String {
        var url = Constants.CUSTOMER_SMART_HEALTH_BANNER_API
        when (empType) {
            Constants.BOI -> url = Constants.BOI_SMART_HEALTH_BANNER_API
            Constants.UBI -> url = Constants.UBI_SMART_HEALTH_BANNER_API
            Constants.SUD_LIFE -> url = Constants.SUD_SMART_HEALTH_BANNER_API
        }
        printLogError("RedirectionUrl--->$url")
        return url
    }*/

    fun getCenturionRedirectionUrlBasedOnEmployeeTypeFinal(
        startUrl: String,
        empType: String,
        phone: String,
        employeeID: String
    ): String {
        var url = "$startUrl&RefCode=$phone"
        when (empType) {
            Constants.SUD_LIFE -> url = "$startUrl&RefCode=$phone|$employeeID"
        }
        printLogError("RedirectionUrlFinal--->$url")
        return url
    }

    fun getCenturionShareUrl(startUrl: String, phone: String): String {
        val finalShareUrl = "$startUrl&RefCode=$phone"
        printLogError("ShareUrl--->$finalShareUrl")
        return finalShareUrl
    }

    /*    fun getCenturionShareUrl(phone:String) : String {
            printLogError("ShareUrl--->${Constants.CENTURION_SHARE_URL+"&RefCode=$phone"}")
            return Constants.CENTURION_SHARE_URL+"&RefCode=$phone"
        }*/

    /*fun getCenturionShareUrlBasedOnEmployeeType(empType:String,phone:String,employeeID:String) : String {
        var url = Constants.CUSTOMER_CENTURION_SHARE_URL+"&addValues=$phone&employeeId=$employeeID"
        when(empType) {
            Constants.BOI -> url = Constants.BOI_CENTURION_SHARE_URL+"&addValues=$phone|$employeeID&employeeId=$employeeID"
            Constants.UBI -> url = Constants.UBI_CENTURION_SHARE_URL+"&addValues=$phone|$employeeID&employeeId=$employeeID"
            Constants.SUD_LIFE -> url = Constants.SUD_CENTURION_SHARE_URL+"&addValues=$phone|$employeeID&employeeId=$employeeID"
        }
        printLogError("ShareUrl--->$url")
        return url
    }*/
    fun getMonthConverted(mon: String, context: Context): String {
        val localResource =
            LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        var month = ""
        when (mon.uppercase()) {
            "JANUARY", "JAN" -> month = localResource.getString(R.string.JANUARY)
            "FEBRUARY", "FEB" -> month = localResource.getString(R.string.FEBRUARY)
            "MARCH", "MAR" -> month = localResource.getString(R.string.MARCH)
            "APRIL", "APR" -> month = localResource.getString(R.string.APRIL)
            "MAY" -> month = localResource.getString(R.string.MAY)
            "JUNE", "JUN" -> month = localResource.getString(R.string.JUNE)
            "JULY", "JUL" -> month = localResource.getString(R.string.JULY)
            "AUGUST", "AUG" -> month = localResource.getString(R.string.AUGUST)
            "SEPTEMBER", "SEP" -> month = localResource.getString(R.string.SEPTEMBER)
            "OCTOBER", "OCT" -> month = localResource.getString(R.string.OCTOBER)
            "NOVEMBER", "NOV" -> month = localResource.getString(R.string.NOVEMBER)
            "DECEMBER", "DEC" -> month = localResource.getString(R.string.DECEMBER)
        }
        return month
    }

    fun getDayOfWeekConverted(day: String, context: Context): String {
        val localResource =
            LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        var dayOfWeek = ""
        when (day.uppercase()) {
            "MON" -> dayOfWeek = localResource.getString(R.string.MON)
            "TUE" -> dayOfWeek = localResource.getString(R.string.TUE)
            "WED" -> dayOfWeek = localResource.getString(R.string.WED)
            "THU" -> dayOfWeek = localResource.getString(R.string.THU)
            "FRI" -> dayOfWeek = localResource.getString(R.string.FRI)
            "SAT" -> dayOfWeek = localResource.getString(R.string.SAT)
            "SUN" -> dayOfWeek = localResource.getString(R.string.SUN)
        }
        return dayOfWeek
    }

    fun getBadgeNameByCode(code: String, context: Context): String {
        val localResource =
            LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        var badge = ""
        printLogError("BadgeCode--->$code")
        when (code.uppercase()) {
            "CONTENDER" -> badge = localResource.getString(R.string.BADGE_CONTENDER)
            "CHALLENGER" -> badge = localResource.getString(R.string.BADGE_CHALLENGER)
            "ACHIEVER" -> badge = localResource.getString(R.string.BADGE_ACHIEVER)
        }
        return badge
    }

    fun getBadgeImageByCode(code: String): Int {
        var badge = R.drawable.img_badge_contender
        printLogError("BadgeCode--->$code")
        when (code.uppercase()) {
            "CONTENDER" -> badge = R.drawable.img_badge_contender
            "CHALLENGER" -> badge = R.drawable.img_badge_challenger
            "ACHIEVER" -> badge = R.drawable.img_badge_achiever
        }
        return badge
    }

    fun setProgressWithAnimation(view: ProgressBar, progress: Int, animDuration: Int) {
        ObjectAnimator.ofInt(view, "progress", progress)
            .setDuration(animDuration.toLong())
            .start()
    }

    fun changeLanguage(code: String, context: Context) {
        when (code) {
            Constants.LANGUAGE_CODE_ENGLISH -> {
                LocaleHelper.setLocale(context, Constants.LANGUAGE_CODE_ENGLISH)
            }

            Constants.LANGUAGE_CODE_HINDI -> {
                LocaleHelper.setLocale(context, Constants.LANGUAGE_CODE_HINDI)
            }
        }
    }

    fun logCleverTapChangeLanguage(code: String, context: Context) {
        val data = HashMap<String, Any>()
        when (code) {
            Constants.LANGUAGE_CODE_ENGLISH -> {
                data[CleverTapConstants.LANGUAGE] = Constants.LANGUAGE_ENGLISH
                CleverTapHelper.pushEventWithProperties(
                    context,
                    CleverTapConstants.APP_LANGUAGE,
                    data,
                    false
                )
            }

            Constants.LANGUAGE_CODE_HINDI -> {
                data[CleverTapConstants.LANGUAGE] = Constants.LANGUAGE_HINDI
                CleverTapHelper.pushEventWithProperties(
                    context,
                    CleverTapConstants.APP_LANGUAGE,
                    data,
                    false
                )
            }
        }
    }

    fun getLanguageNameConverted(code: String, context: Context): String {
        val localResource =
            LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        var language = localResource.getString(R.string.ENGLISH)
        when (code) {
            Constants.LANGUAGE_CODE_ENGLISH -> language = localResource.getString(R.string.ENGLISH)
            Constants.LANGUAGE_CODE_HINDI -> language = localResource.getString(R.string.HINDI)
        }
        return language
    }

    fun loadImageUrl(imgUrl: String, view: ImageView) {
        try {
            if (!isNullOrEmpty(imgUrl)) {
                Picasso.get()
                    .load(imgUrl)
                    .placeholder(R.drawable.img_placeholder)
                    .error(R.drawable.img_placeholder)
                    .into(view)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}