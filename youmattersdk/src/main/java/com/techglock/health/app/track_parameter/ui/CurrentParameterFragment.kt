package com.techglock.health.app.track_parameter.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import androidx.constraintlayout.widget.Group
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseFragment
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.CleverTapConstants
import com.techglock.health.app.common.utils.AppColorHelper
import com.techglock.health.app.common.utils.CalculateParameters
import com.techglock.health.app.common.utils.CleverTapHelper
import com.techglock.health.app.common.utils.DateHelper
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.FragmentCurrentParamBinding
import com.techglock.health.app.model.entity.TrackParameterMaster
import com.techglock.health.app.model.parameter.ParameterListModel
import com.techglock.health.app.track_parameter.adapter.*
import com.techglock.health.app.track_parameter.util.TrackParameterHelper
import com.techglock.health.app.track_parameter.viewmodel.HistoryViewModel
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CurrentParameterFragment : BaseFragment(), ProfileSelectionAdapter.ProfileSelectionListener {

    private lateinit var binding: FragmentCurrentParamBinding

    private val viewModel: HistoryViewModel by lazy {
        ViewModelProvider(this)[HistoryViewModel::class.java]
    }

    private var selectedParameterCode: String = ""

    //var systolic = 0.0
    //var diastolic = 0.0
    var isFirstTime = true
    private var profileCode: String = "BMI"
    private val args: CurrentParameterFragmentArgs by navArgs()
    lateinit var savedParamAdapter: RevSavedParamAdapter
    lateinit var spinnerAdapter: ParameterSpinnerAdapter
    lateinit var paramDetailsTableAdapter: RevParamDetailsTableAdapter
    var profileSelectionAdapter: ProfileSelectionAdapter? = null

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCurrentParamBinding.inflate(inflater, container, false)
//        binding.viewModel = viewModel
//        binding.lifecycleOwner = viewLifecycleOwner
        try {
            initialise()
            setClickable()
            registerObserver()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return binding.root
    }

    private fun initialise() {

        CleverTapHelper.pushEvent(
            requireContext(),
            CleverTapConstants.TRACK_PARAMETER_HISTORY_SCREEN
        )
        profileCode = args.profileCode

        profileSelectionAdapter = ProfileSelectionAdapter(requireContext(), this, false)
        binding.rvProfileSelection.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvProfileSelection.adapter = profileSelectionAdapter

        if (profileCode.equals("BMI", true) || profileCode.equals(
                "BLOODPRESSURE",
                true
            ) || profileCode.equals("WHR", true)
        ) {
            binding.layoutSelectedParameterDetails.visibility = View.VISIBLE
        } else {
//            binding.layoutObservation.visibility = View.GONE
        }
        savedParamAdapter = RevSavedParamAdapter(requireContext())
        binding.rvSavedParameters.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvSavedParameters.adapter = savedParamAdapter
        viewModel.refreshSelectedParamList()

        savedParamAdapter.setOnItemClickListener {
            performParameterSelection(it)
        }


        binding.txtLastCheckedDate.setTextColor(AppColorHelper.instance!!.primaryColor())
//        binding.txtParamSpinner.background.setColorFilter(AppColorHelper.instance!!.secondaryColor(), PorterDuff.Mode.SRC_ATOP)

        spinnerAdapter = ParameterSpinnerAdapter(requireContext())
        binding.paramSpinner.adapter = spinnerAdapter

        paramDetailsTableAdapter = RevParamDetailsTableAdapter(requireContext())
        binding.rvParamHistory.layoutManager = LinearLayoutManager(context)
        binding.rvParamHistory.adapter = paramDetailsTableAdapter

        viewModel.spinnerHistoryLiveData.observe(viewLifecycleOwner) {
            spinnerAdapter = ParameterSpinnerAdapter(requireContext())
            binding.paramSpinner.adapter = spinnerAdapter
            if (it != null && it.isNotEmpty()) {
                val list = it.filter { item ->
                    !item.parameterCode.equals("WBC", true)
                            && !item.parameterCode.equals("DLC", true)
                }
                spinnerAdapter.updateList(list)
            } else {
                spinnerAdapter.updateList(it)
            }

            binding.paramSpinner.setSelection(0)
        }

        viewModel.savedParamList.observe(viewLifecycleOwner) { resource ->
            Utilities.printLog("BindingAdapter=> $resource")
            if (resource.isNotEmpty()) {
                val list = resource.filter { item ->
                    !item.parameterCode.equals(
                        "WBC",
                        true
                    ) && !item.parameterCode.equals("DLC", true)
                }
                savedParamAdapter.updateData(list)
            } else {
                savedParamAdapter.updateData(resource)
            }

        }

        viewModel.observationList.observe(viewLifecycleOwner) {
            it?.let {
                val adapter = RevObservationAdapter()
                binding.rvObsRanges.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                binding.rvObsRanges.adapter = adapter
                Utilities.printLog("BindingAdapter=> $it")
                adapter.updateData(it)
            }
        }
        viewModel.parameterHistoryByProfileCode(profileCode)
        //selectTabDetails()
    }


    /*    private fun performParameterSelection( history : TrackParameterMaster.History  ) {
            savedParamAdapter.notifyDataSetChanged()
            Utilities.printLog("Selection => "+history )

            if(!profileCode.equals("BMI",true)
                && !profileCode.equals("BLOODPRESSURE",true)
            //&& !profileCode.equals("WHR",true)
            ) {
                binding.layoutRanges.visibility = View.VISIBLE
                //viewModel.parameterObservationListByParameterCode(history.parameterCode)
                parameterSelection(history)
            } else {
                binding.layoutRanges.visibility = View.GONE
            }
        }*/

    @SuppressLint("NotifyDataSetChanged")
    private fun performParameterSelection(history: TrackParameterMaster.History) {
        savedParamAdapter.notifyDataSetChanged()
        //Utilities.printLog("Parameter=> ${it.parameterCode}")
        if (history.parameterCode == "BP_SYS" || history.parameterCode == "BP_DIA") {
            binding.rvObsRanges.visibility = View.GONE
        } else {
            binding.rvObsRanges.visibility = View.VISIBLE
            viewModel.parameterObservationListByParameterCode(history.parameterCode)
        }
        parameterSelection(history)
    }

    private fun registerObserver() {
        viewModel.paramBPHistory.observe(viewLifecycleOwner) {
            setBPChartData(it["BP_SYS"]!!, it["BP_DIA"]!!)
            setBpTableData(it["BP_SYS"]!!, it["BP_DIA"]!!)
        }

        viewModel.paramHistory.observe(viewLifecycleOwner) {
            setChartData(it)
            setTableData(it)
        }

        viewModel.savedParamList.observe(viewLifecycleOwner) {
            savedParamAdapter.selectedPosition = 0
            if (it.isNotEmpty()) {
                Utilities.printLog("Parameter 0 => ${it[0]}")
                viewModel.parameterObservationListByParameterCode(it[0].parameterCode)
                if (it[0].profileCode.equals("BLOODPRESSURE")) {
                    parameterSelection(savedParamAdapter.updateBloodPressureObservation(it)[0])
                } else if (it[0].profileCode.equals("WHR", true)) {
                    parameterSelection(getWHRItem(it))
                } else {
                    parameterSelection(it[0])
                }
                binding.layoutNoRecords.visibility = Group.GONE
                binding.layoutParameterResultDetails.visibility = Group.VISIBLE
            } else {
                binding.layoutNoRecords.visibility = Group.VISIBLE
                binding.layoutParameterResultDetails.visibility = Group.GONE
            }
        }

        /*        viewModel.savedParamList.observe(viewLifecycleOwner) {
                    savedParamAdapter.selectedPosition = 0
                    if (it.isNotEmpty()) {
                        viewModel.parameterObservationListByParameterCode(it[0].parameterCode)
                        if (it[0].profileCode.equals("BLOODPRESSURE")) {
                            parameterSelection(savedParamAdapter.updateBloodPressureObservation(it)[0])
                        } else {
                            parameterSelection(it[0])
                        }
                    }
                }*/

        viewModel.selectedParameter.observe(viewLifecycleOwner) {
            var counter = 0
            if (it.isNotEmpty()) {
                if (isFirstTime) {
                    for (item in it) {
                        if (item.profileCode.equals(profileCode, true)) {
                            break
                        } else {
                            counter++
                        }
                    }
                    profileSelectionAdapter!!.selectedPosition = counter
                    profileSelectionAdapter!!.updateData(it)
                    //viewModel.getParameterByProfileCodeAndDate(profileCode, serverDate)
                    viewModel.parameterHistoryByProfileCode(profileCode)
                    binding.rvProfileSelection.scrollToPosition(profileSelectionAdapter!!.selectedPosition)
                } else {
                    profileSelectionAdapter!!.updateData(it)
                }
            }
        }

    }

    private fun selectDetailTab() {
        binding.viewDetail.visibility = View.VISIBLE
        binding.viewGraph.visibility = View.INVISIBLE
        binding.cardDetailsView.visibility = View.VISIBLE
        binding.cardGraphView.visibility = View.GONE
    }

    private fun selectGraphTab() {
        binding.viewDetail.visibility = View.INVISIBLE
        binding.viewGraph.visibility = View.VISIBLE
        binding.cardDetailsView.visibility = View.GONE
        binding.cardGraphView.visibility = View.VISIBLE
    }

    override fun onProfileSelection(position: Int, profile: ParameterListModel.SelectedParameter) {

        //Utilities.printLog("Position: ${it.iconPosition} :: ${it.profileName}")
        //viewModel.refreshSelectedParamList()
        profileCode = profile.profileCode
        viewModel.parameterHistoryByProfileCode(profileCode)

        selectDetailTab()
        when (profile.profileCode) {
            "BMI", "BLOODPRESSURE" -> {
                savedParamAdapter.updateSelection(-1)
            }

            /*            "WHR" -> {
                            savedParamAdapter.updateSelection(-1)
                            viewModel.parameterObservationListByParameterCode("WHR")
                            binding.layoutRanges.visibility = View.VISIBLE
                        }*/

            else -> {
                savedParamAdapter.updateSelection(0)
            }
        }

    }

    private fun getWHRItem(list: List<TrackParameterMaster.History>?): TrackParameterMaster.History {
        for (item in list!!) {
            if (item.parameterCode == "WHR") {
                return item
            }
        }
        return list[0]
    }

    /*    private fun getWHRItem(list: List<TrackParameterMaster.History>?): TrackParameterMaster.History {
            for (item in list!!){
                if (item.parameterCode.equals("WHR")){
                    return item
                }
            }
            return list[0]
        }*/

    private fun setClickable() {

        binding.txtParamSpinner.setOnClickListener { v: View? ->
            //binding.txtParamSpinner.setTextColor(requireContext().resources.getColor(R.color.colorAccent))
            binding.paramSpinner.performClick()
        }

        binding.paramSpinner.onItemSelectedListener = object : OnItemSelectedListener {

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                if (spinnerAdapter.dataList.isNotEmpty()) {
                    spinnerAdapter.selectedPos = position
                    val name: String = spinnerAdapter.dataList[position].description!!
                    binding.txtParamSpinner.text = name
                    selectedParameterCode = spinnerAdapter.dataList[position].parameterCode
                    if (spinnerAdapter.dataList[position].unit != null) {
                        //                    String unit = spinnerParamList.get(position).getName()+" ("+spinnerParamList.get(position).getUnit()+")";
                        binding.graphUnit.text = spinnerAdapter.dataList[position].unit
                    } else {
                        binding.graphUnit.text = " - - "
                    }
                    Utilities.printLog("Selected Item:: $position,$name")
                    // Refresh Chart and table List
                    if (selectedParameterCode.isNotEmpty()) {
                        if (selectedParameterCode.equals("BP_SYS", ignoreCase = true)
                            || selectedParameterCode.equals("BP_DIA", ignoreCase = true)
                        ) {
                            viewModel.getBPParameterHistory()
                        } else {
                            viewModel.getParameterHistory(selectedParameterCode)
                        }
                    }

                    binding.txtParamSpinner.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                binding.txtParamSpinner.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )
            }
        }

        binding.tabDetail.setOnClickListener {
            selectDetailTab()
        }

        binding.tabGraph.setOnClickListener {
            selectGraphTab()
            viewModel.getSpinnerData(profileCode)
        }

        binding.btnHistory.setOnClickListener {
            viewModel.navigate(CurrentParameterFragmentDirections.actionCurrentParamToDetailHistoryFragment())
        }

        binding.btnUpdate.setOnClickListener {
            viewModel.navigate(
                CurrentParameterFragmentDirections.actionCurrentParamToUpdateParameterFragment(
                    profileCode,
                    "true"
                )
            )
        }

    }

    @SuppressLint("SetTextI18n")
    private fun parameterSelection(history: TrackParameterMaster.History) {

        try {
//        val recordDate:String = history.recordDate
            /*            if(!history.profileCode.equals("BMI",true) && !history.profileCode.equals("BLOODPRESSURE",true) && !history.profileCode.equals("WHR",true)) {
                            binding.txtLastCheckedDate.text =
                                "${resources.getString(R.string.UPDATED)} : " + history.recordDate.replace("-", " ", true)
                        }*/
//            val color =
//                TrackParameterHelper.getObservationColor(history.observation, history.profileCode!!)

            binding.txtLastCheckedDate.text = DateHelper.convertDateSourceToDestination(
                history.recordDate,
                DateHelper.DISPLAY_DATE_DDMMMYYYY,
                DateHelper.DATEFORMAT_DDMMMYYYY_NEW
            )

            if (history.observation.isNullOrEmpty()) {
                binding.txtParamObs.text = " -- "
                binding.layoutSelectedParameterDetails.visibility = View.GONE
            } else {
                binding.layoutSelectedParameterDetails.visibility = View.VISIBLE
                var value = ""
                var observation = ""
                if (profileCode.equals("BMI", true)) {
                    value = "<b><font color=" + TrackParameterHelper.getObservationColorInHex(
                        history.observation!!, "BMI"
                    ) + ">" + history.value + "</font></b>"
                    observation = "<b><font color=" + TrackParameterHelper.getObservationColorInHex(
                        history.observation!!, "BMI"
                    ) + ">" + TrackParameterHelper.getLocalizeObservation(
                        history.observation,
                        context
                    ) + "</font></b>"
                } else if (profileCode.equals("BLOODPRESSURE", true)) {
                    value = "<b><font color=" + TrackParameterHelper.getObservationColorInHex(
                        history.observation!!, "BLOODPRESSURE"
                    ) + ">" + history.textValue + "</font></b>"
                    observation = "<b><font color=" + TrackParameterHelper.getObservationColorInHex(
                        history.observation!!, "BLOODPRESSURE"
                    ) + ">" + TrackParameterHelper.getLocalizeObservation(
                        history.observation,
                        context
                    ) + "</font></b>"
                } else if (profileCode.equals("WHR", true)) {
                    value = "<b><font color=" + TrackParameterHelper.getObservationColorInHex(
                        history.observation!!, "WHR"
                    ) + ">" + history.value + "</font></b>"
                    observation = "<b><font color=" + TrackParameterHelper.getObservationColorInHex(
                        history.observation!!, "WHR"
                    ) + ">" + TrackParameterHelper.getLocalizeObservation(
                        history.observation,
                        context
                    ) + "</font></b>"
                } else {
                    value = "<b><font color=" + TrackParameterHelper.getObservationColorInHex(
                        history.observation!!, "WHR"
                    ) + ">" + history.value + "</font></b>"
                    observation = "<b><font color=" + TrackParameterHelper.getObservationColorInHex(
                        history.observation!!, "WHR"
                    ) + ">" + TrackParameterHelper.getLocalizeObservation(
                        history.observation,
                        context
                    ) + "</font></b>"
                }

                binding.txtParamTitle.text = history.description
                binding.txtParamValue.text = Html.fromHtml(value)

                binding.txtParamObs.text = Html.fromHtml(observation)

            }
            // Ranges not available for systolic and diastolic
            if (history.parameterCode.equals("BP_DIA", ignoreCase = true)
                || history.parameterCode.equals("BP_SYS", ignoreCase = true)
            ) {
                binding.layoutRanges.visibility = View.GONE
//                binding.layoutSelectedParameterDetails.setBackgroundColor(resources.getColor(TrackParameterHelper.getObservationColor(history.observation!!, "BLOODPRESSURE")))
                binding.txtParamTitle.text = resources.getString(R.string.SYSTOLIC_DIASTOLIC)
                //binding.txtParamValue.text = history.textValue

            } else {
                binding.layoutRanges.visibility = View.VISIBLE
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setBPChartData(
        sysList: List<TrackParameterMaster.History>,
        diaList: List<TrackParameterMaster.History>
    ) {
        try {

            if (sysList.isNotEmpty()) {
                if (sysList.size == 1) {
                    binding.barChart.visibility = View.VISIBLE
                    binding.graphParameters.visibility = View.INVISIBLE
                    binding.barChart.setTouchEnabled(true)
                    binding.barChart.isDoubleTapToZoomEnabled = false
                    binding.barChart.setPinchZoom(false)
                    binding.barChart.legend.isEnabled = true
                    binding.barChart.isHighlightPerTapEnabled = true
                    val xAxis: XAxis = binding.barChart.xAxis
                    xAxis.position = XAxis.XAxisPosition.BOTTOM
                    xAxis.textSize = 11f
                    xAxis.textColor = resources.getColor(R.color.vivant_questionsteel_grey)
                    xAxis.setDrawAxisLine(true)
                    xAxis.granularity = 1f
                    xAxis.setDrawGridLines(false)
                    xAxis.setCenterAxisLabels(true)
                    xAxis.axisMinimum = 0f
                    xAxis.valueFormatter = object : IAxisValueFormatter {

                        override fun getFormattedValue(value: Float, axis: AxisBase?): String? {
                            for (i in sysList.indices) {
                                if (value == i.toFloat()) {
                                    return DateHelper.formatDateValue(
                                        "dd-MMM-yy", sysList[i].recordDate
                                    )
                                }
                            }
                            return ""
                        }
                    }
                    val yAxis: YAxis = binding.barChart.axisLeft
                    yAxis.setDrawTopYLabelEntry(true)
                    yAxis.setDrawZeroLine(false)
                    yAxis.granularity = 1f
                    yAxis.axisMinimum = 0f
                    yAxis.spaceTop = 15f
                    val rightAxisRisk: YAxis = binding.barChart.axisRight
                    rightAxisRisk.setDrawGridLines(false)
                    rightAxisRisk.setDrawLabels(false)
                    val entries: MutableList<BarEntry> = ArrayList()
                    for (i in sysList.indices) {
                        if (sysList[i].value == null || sysList[i].value!!.toDouble() == 0.0) {
                            entries.add(BarEntry(i.toFloat(), 0f))
                        } else {
                            entries.add(BarEntry(i.toFloat(), sysList[i].value!!.toFloat()))
                        }
                    }
                    val entriesDia: MutableList<BarEntry> = ArrayList()
                    for (i in diaList.indices) {
                        if (diaList[i].value == null || diaList[i].value!!.toDouble() == 0.0) {
                            entriesDia.add(BarEntry(i.toFloat(), 0f))
                        } else {
                            entriesDia.add(BarEntry(i.toFloat(), diaList[i].value!!.toFloat()))
                        }
                    }
                    val set = BarDataSet(entries, resources.getString(R.string.SYSTOLIC))
                    set.color = ContextCompat.getColor(requireContext(), R.color.colorPrimary)
                    set.valueTextSize = 11f
                    val setDia = BarDataSet(entriesDia, resources.getString(R.string.DIASTOLIC))
                    setDia.color = ContextCompat.getColor(requireContext(), R.color.transparent)
                    setDia.valueTextSize = 11f
                    val groupSpace = 0.08f
                    val barSpace = 0.03f // x4 DataSet
                    val barWidth = 0.2f // x4 DataSet
                    // (0.02 + 0.45) * 2 + 0.06 = 1.00 -> interval per "group"
                    val data = BarData(set, setDia)
                    data.barWidth = barWidth // set the width of each bar
                    binding.barChart.data = data
                    binding.barChart.groupBars(
                        0f,
                        groupSpace,
                        barSpace
                    ) // perform the "explicit" grouping
                    binding.barChart.description.isEnabled = false
                    binding.barChart.animateXY(1000, 1000)
                    binding.barChart.invalidate() // refresh
                } else {
                    binding.barChart.visibility = View.INVISIBLE
                    binding.graphParameters.visibility = View.VISIBLE
                    binding.graphParameters.description.text = ""
                    binding.graphParameters.legend.isEnabled = true
                    binding.graphParameters.axisRight.isEnabled = false
                    binding.graphParameters.isDoubleTapToZoomEnabled = false
                    binding.graphParameters.setPinchZoom(false)
                    binding.graphParameters.setTouchEnabled(true)
                    binding.graphParameters.isHighlightPerTapEnabled = true
                    val yAxis: YAxis = binding.graphParameters.axisLeft
                    yAxis.setDrawTopYLabelEntry(true)
                    yAxis.setDrawZeroLine(true)
                    yAxis.granularity = 1f
                    yAxis.axisMinimum = 0f
                    val xAxis: XAxis = binding.graphParameters.xAxis
                    xAxis.position = XAxis.XAxisPosition.BOTTOM
                    xAxis.setDrawAxisLine(true)
                    xAxis.textSize = 9f
                    xAxis.granularity = 1f
                    xAxis.setDrawGridLines(false)
                    xAxis.axisMinimum = 0f
                    xAxis.valueFormatter = object : IAxisValueFormatter {
                        override fun getFormattedValue(value: Float, axis: AxisBase?): String? {
                            for (i in 1..sysList.size) {
                                if (value == i.toFloat()) {
                                    return DateHelper.formatDateValue(
                                        "dd-MMM", sysList[i - 1].recordDate
                                    )
                                }
                            }
                            return ""
                        }
                    }
                    val entries: MutableList<Entry> = ArrayList()
                    entries.add(Entry(0f, 0f))
                    for (i in sysList.indices) {
                        if (sysList[i].value == null || sysList[i].value!!.toDouble() == 0.0) {
                            entries.add(Entry((i + 1).toFloat(), 0f))
                        } else {
                            entries.add(Entry((i + 1).toFloat(), sysList[i].value!!.toFloat()))
                        }
                    }
                    val entriesDia: MutableList<Entry> = ArrayList()
                    entriesDia.add(Entry(0f, 0f))
                    for (i in diaList.indices) {
                        if (diaList[i].value == null || diaList[i].value!!.toDouble() == 0.0) {
                            entriesDia.add(Entry((i + 1).toFloat(), 0f))
                        } else {
                            entriesDia.add(Entry((i + 1).toFloat(), diaList[i].value!!.toFloat()))
                        }
                    }
                    val lineDataSet = LineDataSet(entries, resources.getString(R.string.SYSTOLIC))
                    lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
                    lineDataSet.setDrawFilled(true)
                    lineDataSet.setDrawValues(true)
                    lineDataSet.valueTextColor = resources.getColor(R.color.textViewColor)
                    lineDataSet.valueTextSize = 11f
                    lineDataSet.setDrawCircles(true)
                    lineDataSet.color =
                        ContextCompat.getColor(requireContext(), R.color.colorPrimary)
                    lineDataSet.setCircleColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorPrimary
                        )
                    )
                    lineDataSet.setCircleColorHole(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorPrimary
                        )
                    )
                    lineDataSet.fillAlpha = 255
//                    resources.getDrawable()
                    lineDataSet.fillDrawable =
                        ContextCompat.getDrawable(requireContext(), R.drawable.chart_fill)
                    val lineDataSetDia =
                        LineDataSet(entriesDia, resources.getString(R.string.DIASTOLIC))
                    lineDataSetDia.mode = LineDataSet.Mode.CUBIC_BEZIER
                    lineDataSetDia.setDrawFilled(true)
                    lineDataSetDia.setDrawValues(true)
                    lineDataSetDia.valueTextColor = ContextCompat.getColor(
                        requireContext(),
                        R.color.textViewColor
                    ) /*resources.getColor(R.color.textViewColor)*/
                    lineDataSetDia.valueTextSize = 11f
                    lineDataSetDia.color =
                        ContextCompat.getColor(requireContext(), R.color.secondary_yellow)
                    lineDataSetDia.fillAlpha = 255
                    lineDataSetDia.setDrawCircles(true)
                    lineDataSetDia.setCircleColorHole(resources.getColor(R.color.secondary_yellow))
                    lineDataSetDia.setCircleColor(resources.getColor(R.color.secondary_yellow))
                    lineDataSetDia.fillDrawable =
                        ContextCompat.getDrawable(requireContext(), R.drawable.chart_fill_orange)
//                        resources.getDrawable(R.drawable.chart_fill_orange)
                    val dataSets: MutableList<ILineDataSet> = ArrayList()
                    dataSets.add(lineDataSet)
                    dataSets.add(lineDataSetDia)
                    val lineData = LineData(dataSets)
                    binding.graphParameters.data = lineData
                    binding.graphParameters.invalidate()
                    binding.graphParameters.animateXY(1000, 1000)
                }
            } else {
                binding.graphParameters.clearValues()
                binding.graphParameters.notifyDataSetChanged()
                binding.graphParameters.setNoDataText(resources.getString(R.string.NO_DATA_AVAILABLE))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setChartData(recentDataList: List<TrackParameterMaster.History>) {
        try {
            if (recentDataList != null && recentDataList.isNotEmpty()) {
                if (recentDataList.size == 1) {
                    binding.barChart.visibility = View.VISIBLE
                    binding.graphParameters.visibility = View.INVISIBLE
                    binding.barChart.clear()
//                    binding.barChart.clearValues()
                    binding.barChart.invalidate()
                    binding.barChart.setTouchEnabled(true)
                    binding.barChart.isDoubleTapToZoomEnabled = false
                    binding.barChart.setPinchZoom(false)
                    binding.barChart.legend.isEnabled = true
                    binding.barChart.isHighlightPerTapEnabled = true
                    val xAxis: XAxis = binding.barChart.xAxis
                    xAxis.position = XAxis.XAxisPosition.BOTTOM
                    xAxis.textColor = resources.getColor(R.color.textViewColor)
                    xAxis.textSize = 9f
                    xAxis.setDrawAxisLine(true)
                    xAxis.granularity = 1f
                    xAxis.setDrawGridLines(false)
                    xAxis.labelCount = 1
                    xAxis.valueFormatter = object : IAxisValueFormatter {

                        override fun getFormattedValue(value: Float, axis: AxisBase?): String? {
                            for (i in recentDataList.indices) {
                                if (value == i.toFloat()) {
                                    return DateHelper.formatDateValue(
                                        "dd-MMM", recentDataList[i].recordDate
                                    )
                                }
                            }
                            return ""
                        }
                    }
                    val yAxis: YAxis = binding.barChart.axisLeft
                    yAxis.setDrawTopYLabelEntry(true)
                    yAxis.setDrawZeroLine(false)
                    yAxis.granularity = 1f
                    yAxis.axisMinimum = 0f
                    yAxis.spaceTop = 15f
                    val rightAxisRisk: YAxis = binding.barChart.axisRight
                    rightAxisRisk.setDrawGridLines(false)
                    rightAxisRisk.setDrawLabels(false)
                    val entries: MutableList<BarEntry> = ArrayList()
                    for (i in recentDataList.indices) {
                        if (recentDataList[i].value == null) {
                            entries.add(BarEntry(i.toFloat(), 0f))
                        } else {
                            entries.add(BarEntry(i.toFloat(), recentDataList[i].value!!.toFloat()))
                        }
                    }
                    val set = BarDataSet(entries, "Data")
                    set.color = ContextCompat.getColor(requireContext(), R.color.colorPrimary)
                    set.valueTextSize = 11f
                    val data = BarData(set)
                    data.barWidth = 0.4f // set custom bar width
                    binding.barChart.clear()
                    binding.barChart.data = data
                    binding.barChart.setFitBars(true) // make the x-axis fit exactly all bars
                    binding.barChart.description.isEnabled = false
                    binding.barChart.animateXY(2000, 2000)
                    binding.barChart.invalidate() // refresh
                } else {
                    binding.barChart.visibility = View.INVISIBLE
                    binding.graphParameters.visibility = View.VISIBLE
                    binding.graphParameters.description.text = ""
                    binding.graphParameters.legend.isEnabled = true
                    binding.graphParameters.axisRight.isEnabled = false
                    binding.graphParameters.isDoubleTapToZoomEnabled = false
                    binding.graphParameters.setPinchZoom(false)
                    binding.graphParameters.setTouchEnabled(true)
                    binding.graphParameters.isHighlightPerTapEnabled = true
                    val yAxis: YAxis = binding.graphParameters.axisLeft
                    yAxis.setDrawTopYLabelEntry(true)
                    yAxis.setDrawZeroLine(true)
                    yAxis.granularity = 1f
                    yAxis.axisMinimum = 0f
                    val xAxis: XAxis = binding.graphParameters.xAxis
                    xAxis.position = XAxis.XAxisPosition.BOTTOM
                    xAxis.setDrawAxisLine(true)
                    xAxis.granularity = 1f
                    xAxis.setDrawGridLines(false)
                    xAxis.axisMinimum = 0f
                    xAxis.valueFormatter = object : IAxisValueFormatter {
                        override fun getFormattedValue(value: Float, axis: AxisBase?): String? {
                            for (i in 1..recentDataList.size) {
                                if (value == i.toFloat()) {
                                    return DateHelper.formatDateValue(
                                        "dd-MMM", recentDataList[i - 1].recordDate
                                    )
                                }
                            }
                            return ""
                        }
                    }
                    val entries: MutableList<Entry> = ArrayList()
                    entries.add(Entry(0f, 0f))
                    for (i in recentDataList.indices) {
                        if (recentDataList[i].value == null) {
                            entries.add(Entry((i + 1).toFloat(), 0f))
                        } else {
                            entries.add(
                                Entry(
                                    (i + 1).toFloat(),
                                    recentDataList[i].value!!.toFloat()
                                )
                            )
                        }
                    }
                    val lineDataSet = LineDataSet(entries, "Data")
                    //lineDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                    lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
                    lineDataSet.setDrawFilled(true)
                    lineDataSet.setDrawValues(true)
                    lineDataSet.valueTextColor = resources.getColor(R.color.textViewColor)
                    lineDataSet.valueTextSize = 11f
                    //                    lineDataSet.setFillColor(ContextCompat.getColor(getContext(), R.color.vivant_heather));
                    lineDataSet.color =
                        ContextCompat.getColor(requireContext(), R.color.colorPrimary)
                    lineDataSet.fillAlpha = 255
                    lineDataSet.setDrawCircles(true)
                    lineDataSet.setCircleColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorPrimary
                        )
                    )
                    lineDataSet.setCircleColorHole(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorPrimary
                        )
                    )
                    lineDataSet.fillDrawable =
                        ContextCompat.getDrawable(requireContext(), R.drawable.chart_fill)
                    //resources.getDrawable(R.drawable.chart_fill)

                    val lineData = LineData(lineDataSet)
                    binding.graphParameters.data = lineData
                    binding.graphParameters.invalidate()
                    binding.graphParameters.animateXY(1000, 1000)
                }
            } else {
                binding.graphParameters.clearValues()
                binding.graphParameters.notifyDataSetChanged()
                binding.graphParameters.setNoDataText(resources.getString(R.string.NO_DATA_AVAILABLE))
                binding.graphParameters.setNoDataTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.dia_ichi_grey
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setTableData(recentDataList: List<TrackParameterMaster.History>) {
        try {
            Utilities.printLog("Recent DATA list : $selectedParameterCode$recentDataList")
            paramDetailsTableAdapter.updateData(recentDataList)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setBpTableData(
        sysList: List<TrackParameterMaster.History>,
        diaList: List<TrackParameterMaster.History>
    ) {
        try {
            val bpList = ArrayList<TrackParameterMaster.History>()

            bpList.addAll(sysList)
            var param: TrackParameterMaster.History
            var sys = 0
            var dia = 0
            for (i in bpList.indices) {
                try {
                    param = bpList.get(i)
                    sys = param.value!!.toDouble().toInt()
                    if (diaList != null && i < diaList.size) {
                        dia = diaList.get(i).value!!.toDouble().toInt()
                    }
                    param.parameterCode = "BP"
                    param.textValue = "$sys/$dia"
                    param.observation =
                        CalculateParameters.getBPObservation(sys, dia, requireContext())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            if (bpList != null) {
                //Utilities.printLog("Recent DATA list : $selectedParameterCode$bpList")
                Utilities.printData("Recent DATA list", bpList, true)
                paramDetailsTableAdapter.updateData(bpList)
            } else {
                paramDetailsTableAdapter.updateData(bpList)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
