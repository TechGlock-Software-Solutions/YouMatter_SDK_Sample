package com.techglock.health.app.water_tracker.common

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.techglock.health.app.R
import com.techglock.health.app.common.utils.DateHelper
import com.techglock.health.app.common.utils.HeightWeightDialog
import com.techglock.health.app.common.utils.LocaleHelper
import com.techglock.health.app.common.utils.ParameterDataModel
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.model.fitness.MonthModel
import com.techglock.health.app.water_tracker.model.CalenderTrackModel
import com.techglock.health.app.water_tracker.model.DrinkQuantityModel
import com.techglock.health.app.water_tracker.model.DrinkTypeModel
import com.techglock.health.app.water_tracker.ui.AddWaterIntakeBottomSheet
import com.techglock.health.app.water_tracker.ui.CalculateWaterIntakeFragment
import com.techglock.health.app.water_tracker.ui.ParameterValuePickerDialog
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

//object WaterTrackerHelper {
class WaterTrackerHelper(val context: Context) {

    fun getDrinkTypeList(): ArrayList<DrinkTypeModel> {
        val localResource =
            LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        val medTypeList: ArrayList<DrinkTypeModel> = ArrayList()
        localResource.getString(R.string.WATER)
        medTypeList.add(
            DrinkTypeModel(
                localResource.getString(R.string.WATER),
                "WATER",
                R.drawable.img_water
            )
        )
        medTypeList.add(
            DrinkTypeModel(
                localResource.getString(R.string.COFFEE),
                "COFFEE",
                R.drawable.img_coffee
            )
        )
        medTypeList.add(
            DrinkTypeModel(
                localResource.getString(R.string.TEA),
                "TEA",
                R.drawable.img_tea
            )
        )
        medTypeList.add(
            DrinkTypeModel(
                localResource.getString(R.string.MILK),
                "MILK",
                R.drawable.img_milk
            )
        )
        medTypeList.add(
            DrinkTypeModel(
                localResource.getString(R.string.JUICE),
                "JUICE",
                R.drawable.img_fruit_juice
            )
        )
        medTypeList.add(
            DrinkTypeModel(
                localResource.getString(R.string.ENERGY_DRINK),
                "ENERGY_DRINK",
                R.drawable.img_energy_drink
            )
        )
        return medTypeList
    }

    fun getDrinkQuantityList(): ArrayList<DrinkQuantityModel> {
        //val localResource = LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        val medTypeList: ArrayList<DrinkQuantityModel> = ArrayList()
        medTypeList.add(DrinkQuantityModel("100", "ml"))
        medTypeList.add(DrinkQuantityModel("150", "ml"))
        medTypeList.add(DrinkQuantityModel("250", "ml"))
        medTypeList.add(DrinkQuantityModel("500", "ml"))
        medTypeList.add(DrinkQuantityModel("750", "ml"))
        medTypeList.add(DrinkQuantityModel("1000", "ml"))
        return medTypeList
    }

    /*    fun getWaterDropImageByValue(percent: Int): Int {
            return when (percent) {
                in 1..5 -> {
                    R.drawable.img_water_drop_5
                }
                in 5..10 -> {
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
                in 100..1000 -> {
                    R.drawable.img_water_drop_100
                }
                else -> {
                    R.drawable.img_water_drop_0
                }
            }
        }*/

    fun getAllMonthsListFromJoiningToCurrentDate(
        startDate: String,
        endDate: String,
        fm: String
    ): MutableList<MonthModel> {
        val months: MutableList<MonthModel> = mutableListOf()
        try {
            val df1: DateFormat = SimpleDateFormat(fm, Locale.ENGLISH)
            val dateFrom = df1.parse(startDate)!!
            val dateTo = df1.parse(endDate)!!
            //Utilities.printLog("dateFrom---> $dateFrom")
            //Utilities.printLog("dateTo---> $dateTo")
            val calendar = Calendar.getInstance()
            calendar.time = dateFrom
            var isStart = true
            var date: MonthModel
            //months.add(date)
            while (calendar.time.time <= dateTo.time) {
                date = MonthModel()
                date.year = getFormattedValue("yyyy", calendar.time)
                date.month = getFormattedValue("MMM", calendar.time)
                date.monthOfYear = getFormattedValue("MM", calendar.time)
                date.displayValue = getFormattedValue("MMM yyyy", calendar.time)
                if (isStart) {
                    date.startDate = date.year + "-" + date.monthOfYear + "-" + getFormattedValue(
                        "dd",
                        calendar.time
                    )
                    isStart = false
                } else {
                    date.startDate =
                        date.year + "-" + date.monthOfYear + "-" + calendar.getActualMinimum(
                            Calendar.DATE
                        )
                }
                date.endDate =
                    date.year + "-" + date.monthOfYear + "-" + calendar.getActualMaximum(Calendar.DATE)
                months.add(date)
                calendar.add(Calendar.MONTH, 1)
            }
            //months[months.size - 1].endDate = endDate!!

            val currentMonth = DateHelper.currentMonthAsStringMMM
            val currentYear = DateHelper.currentYearAsStringyyyy
            var hasCurrentMonth = false
            for (month in months) {
                if (month.year == currentYear && month.month == currentMonth) {
                    hasCurrentMonth = true
                    break
                }
            }
            if (!hasCurrentMonth) {
                val cal = Calendar.getInstance()
                calendar.time = dateTo
                date = MonthModel()
                date.year = getFormattedValue("yyyy", cal.time)
                date.month = getFormattedValue("MMM", cal.time)
                date.monthOfYear = getFormattedValue("MM", cal.time)
                date.displayValue = getFormattedValue("MMM yyyy", cal.time)
                date.startDate =
                    date.year + "-" + date.monthOfYear + "-" + calendar.getActualMinimum(Calendar.DATE)
                //date.endDate = date.year + "-" + date.monthOfYear + "-" + getFormattedValue("dd", calendar.time)
                date.endDate = endDate
                months.add(date)
            }

            //Utilities.printData("MonthlyGroupList--->",months,false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return months
    }

    private fun getFormattedValue(format: String, date: Date): String {
        return SimpleDateFormat(format, Locale.ENGLISH).format(date)
    }

    fun getDatesInMonth(year: Int, month: Int): MutableList<CalenderTrackModel> {
        Utilities.printLogError("$year - $month")
        val fmt = SimpleDateFormat(DateHelper.SERVER_DATE_YYYYMMDD, Locale.ENGLISH)
        val cal = Calendar.getInstance()
        cal.clear()
        cal.set(year, month - 1, 1)
        val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val trackDaysList: MutableList<CalenderTrackModel> = mutableListOf()
        var trackDate: CalenderTrackModel
        for (i in 0 until daysInMonth) {
            //Utilities.printLogError(fmt.format(cal.time))
            trackDate = CalenderTrackModel()
            trackDate.trackDate = getFormattedValue("d", cal.time)
            trackDate.trackWeekDay = getFormattedValue("EEE", cal.time)
            trackDate.trackMonthYear = getFormattedValue("MMM yyyy", cal.time)
            trackDate.trackDisplayDate = getFormattedValue("dd MMM yyyy", cal.time)
            trackDate.trackServerDate = getFormattedValue("yyyy-MM-dd", cal.time)
            trackDaysList.add(trackDate)
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }
        return trackDaysList
    }

    fun getAllMonthsOfYear(year: Int): MutableList<MonthModel> {
        val allMonths: MutableList<MonthModel> = mutableListOf()
        val calendar = Calendar.getInstance()
        calendar[Calendar.YEAR] = year
        calendar[Calendar.MONTH] = 0
        calendar[Calendar.DAY_OF_MONTH] = 1
        var month: MonthModel
        for (i in 0..11) {
            month = MonthModel()
            month.year = getFormattedValue("yyyy", calendar.time)
            month.month = getFormattedValue("MMM", calendar.time)
            month.monthOfYear = getFormattedValue("MM", calendar.time)
            month.displayValue = getFormattedValue("MMM yyyy", calendar.time)
            month.startDate = ""
            month.endDate = ""
            allMonths.add(month)
            calendar.add(Calendar.MONTH, 1)
            calendar[Calendar.DAY_OF_MONTH] = 1
        }
        Utilities.printData("getAllMonthsOfYear($year)", allMonths, true)
        return allMonths
    }

    fun showWeightDialog(
        weight: Double,
        listener: CalculateWaterIntakeFragment,
        context: Context,
        savedUnit: String = "kg"
    ) {
        val data = ParameterDataModel()
        data.title = context.resources.getString(R.string.WEIGHT)
        data.value = " - - "
        if (Utilities.isNullOrEmptyOrZero(weight.toString())) {
            data.finalValue = "50.0"
        } else {
            data.finalValue = weight.toString()
        }

        if (!savedUnit.equals("kg", true)) {
            data.unit = context.resources.getString(R.string.LBS)
        } else {
            data.unit = context.resources.getString(R.string.KG)
        }
        data.code = "WEIGHT"
        val heightWeightDialog = HeightWeightDialog(context, listener, "Weight", data)
        heightWeightDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        heightWeightDialog.show()
    }

    fun showWaterIntakeDialog(target: Int, listener: AddWaterIntakeBottomSheet, context: Context) {
        val data = ParameterDataModel()
        data.title = context.resources.getString(R.string.DURATION)
        data.value = " - - "
        if (Utilities.isNullOrEmptyOrZero(target.toString())) {
            data.finalValue = "100.0"
        } else {
            data.finalValue = target.toString()
        }
        data.minRange = 10.0
        data.maxRange = 5000.0
        data.unit = context.resources.getString(R.string.ML)
        data.code = "INTAKE"
        val dialog = ParameterValuePickerDialog(context, listener, "Intake", data)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

}