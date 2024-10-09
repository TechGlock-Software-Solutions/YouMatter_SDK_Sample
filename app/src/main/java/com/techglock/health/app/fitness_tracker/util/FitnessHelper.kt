package com.techglock.health.app.fitness_tracker.util

import android.content.Context
import com.techglock.health.app.R
import com.techglock.health.app.common.utils.DateHelper
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.model.fitness.MonthModel
import com.techglock.health.app.model.fitness.WeekModel
import com.techglock.health.app.model.fitness.YearModel
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class FitnessHelper @Inject constructor(private val context: Context) {

    fun getAllWeeklyGroupListFromJoiningToCurrentDate(
        joiningDate: String,
        endDate: String,
        fm: String
    ): MutableList<WeekModel> {
        val allDatesList: MutableList<WeekModel> = mutableListOf()
        try {
            val df1: DateFormat = SimpleDateFormat(fm, Locale.ENGLISH)
            val fromDate = df1.parse(joiningDate)
            val toDate = df1.parse(endDate)
            Utilities.printLog("fromDate---> $fromDate")
            Utilities.printLog("toDate-----> $toDate")
            var isStart = true
            val cal = Calendar.getInstance()
            cal.time = fromDate
            var week = WeekModel()
            //allDatesList.add(week)
            while (cal.time.time <= toDate.time) {
                if (isStart || cal[Calendar.DAY_OF_WEEK] == Calendar.MONDAY) {
                    isStart = false
                    week = WeekModel()
                    week.startDate = getFormattedValue("yyyy-MM-dd", cal.time)
                    week.left = getFormattedValue("dd MMM", cal.time)
                }
                if (cal[Calendar.DAY_OF_WEEK] == Calendar.SUNDAY) {
                    week.endDate = getFormattedValue("yyyy-MM-dd", cal.time)
                    week.right = getFormattedValue("dd MMM", cal.time)
                    allDatesList.add(week)
                }
                cal.add(Calendar.DATE, 1)
            }
            //Utilities.printData("allDatesList--->",allDatesList,false)
            val currentDate =
                DateHelper.stringDateToDate("yyyy-MM-dd", DateHelper.currentDateAsStringyyyyMMdd)
            if (allDatesList.isNotEmpty()) {
                val lastDate = DateHelper.stringDateToDate(
                    "yyyy-MM-dd",
                    allDatesList[allDatesList.size - 1].endDate
                )
                Utilities.printLog("lastDate---> $lastDate")
                Utilities.printLog("currentDate---> $currentDate")
                if (lastDate != currentDate) {
                    val c = Calendar.getInstance()
                    c.time = lastDate
                    c.add(Calendar.DATE, 1)
                    week = WeekModel()
                    week.startDate = getFormattedValue("yyyy-MM-dd", c.time)
                    week.left = getFormattedValue("dd MMM", c.time)
                    week.endDate = DateHelper.currentDateAsStringyyyyMMdd
                    week.right = getFormattedValue("dd MMM", currentDate)
                    allDatesList.add(week)
                }
            } else {
                //Joined in Current Week
                val lastDate = DateHelper.stringDateToDate("yyyy-MM-dd", joiningDate)
                Utilities.printLog("lastDate---> $lastDate")
                Utilities.printLog("currentDate---> $currentDate")
                val c = Calendar.getInstance()
                c.time = lastDate
                week = WeekModel()
                week.startDate = getFormattedValue("yyyy-MM-dd", c.time)
                week.left = getFormattedValue("dd MMM", c.time)
                week.endDate = DateHelper.currentDateAsStringyyyyMMdd
                week.right = getFormattedValue("dd MMM", currentDate)
                allDatesList.add(week)
            }

            Utilities.printData("WeeklyGroupList--->", allDatesList, false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return allDatesList
    }

    /*    fun getAllWeeklyGroupListFromJoiningToCurrentDate(startDate: String,
                                                          endDate: String,
                                                          fm: String): MutableList<WeekModel> {
            val allDatesList: MutableList<WeekModel> = mutableListOf()
            try {
                val df1: DateFormat = SimpleDateFormat(fm, Locale.ENGLISH)
                val fromDate = df1.parse(startDate)
                val toDate = df1.parse(endDate)
                Utilities.printLog("fromDate---> $fromDate")
                Utilities.printLog("toDate-----> $toDate")
                var isStart = true
                val cal = Calendar.getInstance()
                cal.time = fromDate
                var week = WeekModel()
                //allDatesList.add(week)
                while (cal.time.time <= toDate.time) {
                    if (isStart || cal[Calendar.DAY_OF_WEEK] == Calendar.MONDAY) {
                        isStart = false
                        week = WeekModel()
                        week.startDate = getFormattedValue("yyyy-MM-dd", cal.time)
                        week.left = getFormattedValue("dd MMM", cal.time)
                    }
                    if (cal[Calendar.DAY_OF_WEEK] == Calendar.SUNDAY) {
                        week.endDate = getFormattedValue("yyyy-MM-dd", cal.time)
                        week.right = getFormattedValue("dd MMM", cal.time)
                        allDatesList.add(week)
                    }
                    cal.add(Calendar.DATE, 1)
                }
                Utilities.printData("allDatesList--->",allDatesList,false)
                var lastDate = DateHelper.stringDateToDate("yyyy-MM-dd", DateHelper.currentDateAsStringyyyyMMdd)
                if ( allDatesList.isNotEmpty() ) {
                    lastDate = DateHelper.stringDateToDate("yyyy-MM-dd", allDatesList[allDatesList.size - 1].endDate)
                }
                val currentDate = DateHelper.stringDateToDate("yyyy-MM-dd", DateHelper.currentDateAsStringyyyyMMdd)
                Utilities.printLog("lastDate---> $lastDate")
                Utilities.printLog("currentDate---> $currentDate")
                if (lastDate != currentDate) {
                    val c = Calendar.getInstance()
                    c.time = lastDate
                    c.add(Calendar.DATE, 1)
                    week = WeekModel()
                    week.startDate = getFormattedValue("yyyy-MM-dd", c.time)
                    week.left = getFormattedValue("dd MMM", c.time)
                    week.endDate = DateHelper.currentDateAsStringyyyyMMdd
                    week.right = getFormattedValue("dd MMM", currentDate)
                    allDatesList.add(week)
                } else {
                    week = WeekModel()
                    week.startDate = DateHelper.currentDateAsStringyyyyMMdd
                    week.left = getFormattedValue("dd MMM", currentDate)
                    week.endDate = DateHelper.currentDateAsStringyyyyMMdd
                    week.right = getFormattedValue("dd MMM", currentDate)
                    allDatesList.add(week)
                }
                Utilities.printData("WeeklyGroupList--->",allDatesList,false)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return allDatesList
        }*/

    fun getAllMonthsListFromJoiningToCurrentDate(
        startDate: String,
        endDate: String,
        fm: String
    ): MutableList<MonthModel> {
        val months: MutableList<MonthModel> = mutableListOf()
        try {
            val df1: DateFormat = SimpleDateFormat(fm, Locale.ENGLISH)
            val dateFrom = df1.parse(startDate)
            val dateTo = df1.parse(endDate)
            //Utilities.printLog"dateFrom---> $dateFrom")
            //Utilities.printLog("dateTo---> $dateTo")
            val calendar = Calendar.getInstance()
            calendar.time = dateFrom
            var isStart = true
            var date = MonthModel()
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

            Utilities.printData("MonthlyGroupList--->", months, false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return months
    }

    fun getAllYearsListFromJoiningToCurrentDate(
        startDate: String,
        endDate: String,
        fm: String
    ): MutableList<YearModel> {
        val years: MutableList<YearModel> = mutableListOf()
        try {
            val df1: DateFormat = SimpleDateFormat(fm, Locale.ENGLISH)
            val dateFrom = df1.parse(startDate)
            val dateTo = df1.parse(endDate)
            //Utilities.printLog("dateFrom---> $dateFrom")
            //Utilities.printLog("dateTo---> $dateTo")
            val calendar = Calendar.getInstance()
            calendar.time = dateFrom
            var isStart = true
            val yearsBetween: Int = DateHelper.getDifferenceYears(startDate, endDate, fm) + 1
            //Utilities.printLog("yearsBetween---> $yearsBetween")
            var date = YearModel()
            //years.add(date)
            for (i in 0 until yearsBetween) {
                date = YearModel()
                val month: String = getFormattedValue("MM", calendar.time)
                date.year = getFormattedValue("yyyy", calendar.time)
                if (isStart) {
                    date.startDate = startDate
                    isStart = false
                } else {
                    date.startDate =
                        date.year + "-" + month + "-" + calendar.getActualMinimum(Calendar.DATE)
                }
                calendar[Calendar.MONTH] = 11
                date.endDate = date.year + "-" + getFormattedValue(
                    "MM",
                    calendar.time
                ) + "-" + calendar.getActualMaximum(Calendar.DATE)
                years.add(date)
                calendar.add(Calendar.YEAR, 1)
                calendar[Calendar.MONTH] = 0
                calendar[Calendar.DAY_OF_MONTH] = 1
            }
            years[years.size - 1].endDate = endDate
            Utilities.printData("YearlyGroupList--->", years, true)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return years
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
            month.monthOfYear = getFormattedValue("yyyy-MM", calendar.time)
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

    fun getWeeklyDayList(): MutableList<WeekDay> {
        val list: MutableList<WeekDay> = mutableListOf()
        val cal = Calendar.getInstance()
        for (i in 6 downTo 0) {
            cal.time = Date()
            cal.add(Calendar.DAY_OF_YEAR, -i)
            val date = WeekDay()
            date.dayOfWeek = getFormattedValue("EEE", cal.time)
            date.dayOfMonth = getFormattedValue("dd", cal.time)
            date.dateString = getFormattedValue("yyyy-MM-dd", cal.time)
            date.date = cal.time
            // i=0 -> true, else false
            date.isToday = i == 0
            list.add(date)
        }
        return list
    }

    fun getMonthlyDayList(): MutableList<WeekDay> {
        val list: MutableList<WeekDay> = mutableListOf()
        val cal = Calendar.getInstance()
        for (i in 29 downTo 0) {
            cal.time = Date()
            cal.add(Calendar.DAY_OF_YEAR, -i)
            val date = WeekDay()
            date.dayOfWeek = getFormattedValue("EEE", cal.time)
            date.dayOfMonth = getFormattedValue("dd", cal.time)
            date.dateString = getFormattedValue("yyyy-MM-dd", cal.time)
            date.date = cal.time
            // i=0 -> true, else false
            date.isToday = i == 0
            list.add(date)
        }
        return list
    }

    fun getActiveTime(stepCount: Int): String {
        //Utilities.printLog("stepCount---> $stepCount")
        val timeFactor = 100 //avg 100 steps per minute
        //val activeTime = ( stepCount / activityTimeFactor)
        //activeTime = if (activeTime <= 0) 1 else activeTime // At least 1 min
        //return activeTime.toString() + " " + context.resources.getString(R.string.MIN)
        val at1 = (stepCount / timeFactor).toString()
        val at2 = (stepCount % timeFactor).toString()
        return DateHelper.getHourMinFromStrMinutes("$at1.$at2")
    }

    fun getCaloriesWithUnit(calories: String): String {
        return calories + " " + context.resources.getString(R.string.KCAL)
    }

    fun getFormattedValue(format: String, date: Date): String {
        return SimpleDateFormat(format, Locale.ENGLISH).format(date)
    }

    fun convertMtrToKmsValueNew(strValue: String): String {
        var dist = ""
        try {
            var convertedValue = strValue.toDouble()
            convertedValue *= 0.001
            dist = Utilities.roundOffPrecision(convertedValue, 2).toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return dist
    }

    fun getColorBasedOnType(type: String): Int {
        var color: Int = R.color.colorPrimary
        /*        if (!Utilities.isNullOrEmpty(type)) {
                    when(type) {
                        Constants.STEPS_COUNT -> {
                            color = R.color.vivantRed
                        }
                        Constants.DISTANCE -> {
                            color = R.color.vivantPurple
                        }
                        Constants.CALORIES -> {
                            color = R.color.vivant_orange_yellow
                        }
                    }
                }*/
        return color
    }

    /*    fun getAllDatesBetween(startDate: String, endDate: String, fm: String) {
        val allDatesList: MutableList<TempModel> = mutableListOf()
        try {
            val df1: DateFormat = SimpleDateFormat(fm)
            val fromDate: Date = df1.parse(startDate)!!
            val toDate: Date = df1.parse(endDate)!!
            //Utilities.printLog("fromDate,toDate---> $fromDate,$toDate")

            val cal = Calendar.getInstance()
            cal.time = fromDate
            var d = TempModel()
            while (cal.time.time <= toDate.time) {
                d = TempModel()
                d.displayValue = getFormattedValue(DateHelper.DATEFORMAT_DDMMMYYYY_NEW, cal.time)
                d.dayOfWeek = getFormattedValue("EEE", cal.time)
                allDatesList.add(d)
                cal.add(Calendar.DATE, 1)
            }
            Utilities.printData("AllDatesBetween",allDatesList,false)
        } catch ( e :Exception ) {
            e.printStackTrace()
        }
    }*/

}