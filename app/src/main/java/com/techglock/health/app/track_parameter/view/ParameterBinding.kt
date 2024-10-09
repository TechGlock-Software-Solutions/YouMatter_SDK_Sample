package com.techglock.health.app.track_parameter.view

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.DashPathEffect
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.techglock.health.app.R
import com.techglock.health.app.common.utils.DateHelper
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.model.entity.TrackParameterMaster
import com.techglock.health.app.model.parameter.DashboardParamGridModel
import com.techglock.health.app.model.parameter.ParameterProfile
import com.techglock.health.app.track_parameter.adapter.DashboardGridAdapter
import com.techglock.health.app.track_parameter.adapter.SelectParameterAdapter
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet

@SuppressLint("StaticFieldLeak")
object ParameterBinding {

    @BindingAdapter("app:dashboardList")
    @JvmStatic
    fun RecyclerView.setDashboardList(list: List<DashboardParamGridModel>?) {
        with(this.adapter as DashboardGridAdapter) {
            layoutManager = androidx.recyclerview.widget.GridLayoutManager(context, 3)
            list?.let { updateData(it) }
        }
    }

    @BindingAdapter("app:dashList")
    @JvmStatic
    fun RecyclerView.setDashList(list: List<DashboardParamGridModel>?) {
        with(this.adapter as DashboardGridAdapter) {
            layoutManager = androidx.recyclerview.widget.GridLayoutManager(context, 3)
            list?.let { updateData(it) }
        }
    }

    @BindingAdapter("app:allProfilesList")
    @JvmStatic
    fun RecyclerView.setAllProfilesList(resource: List<ParameterProfile>?) {
        with(this.adapter as SelectParameterAdapter) {
            layoutManager = androidx.recyclerview.widget.GridLayoutManager(context, 3)
            resource?.let { updateData(it) }
        }
    }


    @BindingAdapter("app:chartData")
    @JvmStatic
    fun chartData(view: LineChart, resource: List<TrackParameterMaster.History>?) {
        Utilities.printLog("LineChartData=> " + resource)
        with(view as LineChart) {
            resource?.let { initChart(view, it) }
        }
    }

    private fun initChart(
        chart: LineChart,
        list: List<TrackParameterMaster.History>
    ) {
        val data = list.reversed()
        // background color
        chart.setBackgroundColor(Color.WHITE)
        chart.description.isEnabled = false
        chart.setTouchEnabled(true)
        chart.setExtraOffsets(10f, 10f, 30f, 10f)
        // set listeners
        chart.setDrawGridBackground(false)

        // enable scaling and dragging
        chart.isDragEnabled = true
        chart.setScaleEnabled(true)
        chart.setScaleXEnabled(true)
        chart.setScaleYEnabled(false)

        // force pinch zoom along both axis
        chart.setPinchZoom(false)
        chart.isHorizontalScrollBarEnabled = true

        val xAxis: XAxis
        // // X-Axis Style // //
        xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.axisLineWidth = 2f
        xAxis.textSize = 10f
        xAxis.isGranularityEnabled = true
        xAxis.granularity = 1f
//        xAxis.setCenterAxisLabels(true)

        // vertical grid lines
        xAxis.enableGridDashedLine(10f, 10f, 0f)

        val yAxis: YAxis
        yAxis = chart.axisLeft
        // disable dual axis (only use LEFT axis)
        chart.axisRight.isEnabled = false
        // horizontal grid lines
        yAxis.enableGridDashedLine(0f, 0f, 0f)
        // axis range
        var max = 0
        var min = 0
        for (item in data) {
            if (min == 0 && max == 0) {
                min = item.value!!.toInt()
                max = item.value!!.toInt()
            }
            if (item.value!!.toInt() < min) {
                min = item.value!!.toInt()
            }
            if (item.value!!.toInt() > max) {
                max = item.value!!.toInt()
            }
        }
        if (max < 10) {
            yAxis.axisMaximum = 15f
        } else {
            yAxis.axisMaximum = (max + (max / 3)).toFloat()
        }
        yAxis.axisMinimum = 0f
        yAxis.textSize = 10f
        yAxis.textColor = chart.context!!.resources.getColor(R.color.textViewColor)

        if (data.size > 0) {
            setData(chart, data)
        } else {
            chart.clear()
        }
        val legend = chart.legend
        legend.isEnabled = false
        chart.invalidate()
    }

    private fun setData(
        chart: LineChart,
        data: List<TrackParameterMaster.History>
    ) {
        try {
            val count = data.size
            val xValueList = arrayListOf<String>()

            val values = ArrayList<Entry>()
            if (count > 0) {
                values.add(Entry(0f, -10f))
                xValueList.add("")
                for (i in 0 until count) {
                    val `val` = data.get(i).value!!.toFloat()
                    values.add(Entry(i.toFloat() + 1, `val`))
                    xValueList.add(
                        DateHelper.convertDateTimeValue(
                            data.get(i).recordDate.toString(),
                            DateHelper.DATE_FORMAT_UTC,
                            DateHelper.DISPLAY_DATE_DDMMMYYYY
                        ).toString()
                    )
                }
                values.add(Entry((count + 1).toFloat(), -10f))
                xValueList.add("")
            }

            val set1: LineDataSet
            if (chart.data != null && chart.data.dataSetCount > 0 || data.size == 0) {
                set1 = chart.data.getDataSetByIndex(0) as LineDataSet
                set1.values = values
                set1.notifyDataSetChanged()
                chart.data.notifyDataChanged()
                chart.notifyDataSetChanged()
            } else {
                // create a dataset and give it a type
                set1 = LineDataSet(values, "DataSet 1")
                set1.setDrawIcons(false)
                set1.setDrawValues(false)
                // draw dashed line
                set1.enableDashedLine(0f, 0f, 0f)

                // black lines and points
                set1.color = chart.context.resources!!.getColor(R.color.vivantGreen)
                set1.setCircleColor(chart.context.resources!!.getColor(R.color.vivantGreen))

                // line thickness and point size
                set1.lineWidth = 3f
                set1.circleRadius = 6f

                // draw points as solid circles
                set1.setDrawCircleHole(false)

                // customize legend entry
                set1.formLineWidth = 1f
                set1.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
                set1.formSize = 15f

                // text size of values
                set1.valueTextSize = 9f

                // draw selection line as dashed
                set1.enableDashedHighlightLine(10f, 5f, 0f)

                chart.xAxis.setValueFormatter(object : IAxisValueFormatter {
                    override fun getFormattedValue(value: Float, axis: AxisBase?): String {
                        try {
                            return xValueList.get(value.toInt())
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        return ""
                    }
                })

                val dataSets = ArrayList<ILineDataSet>()
                dataSets.add(set1) // add the data sets
                // create a data object with the data sets
                val data = LineData(dataSets)
                // set data
                chart.data = data
                chart.notifyDataSetChanged()
                chart.invalidate()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}