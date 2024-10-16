package com.techglock.health.app.track_parameter.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseFragment
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.CleverTapConstants
import com.techglock.health.app.common.utils.CleverTapHelper
import com.techglock.health.app.common.utils.DefaultNotificationDialog
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.common.utils.showDialog
import com.techglock.health.app.databinding.FragmentParametersDetailHistoryBinding
import com.techglock.health.app.model.entity.TrackParameterMaster
import com.techglock.health.app.model.parameter.MonthYear
import com.techglock.health.app.model.parameter.ParameterProfile
import com.techglock.health.app.model.parameter.ParentProfileModel
import com.techglock.health.app.track_parameter.adapter.ExpandableProfileAdapter
import com.techglock.health.app.track_parameter.adapter.PreviousMonthsAdapter
import com.techglock.health.app.track_parameter.viewmodel.HistoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class DetailHistoryFragment : BaseFragment(), PreviousMonthsAdapter.OnMonthClickListener,
    DefaultNotificationDialog.OnDialogValueListener {

    private val viewModel: HistoryViewModel by lazy {
        ViewModelProvider(this)[HistoryViewModel::class.java]
    }
    private lateinit var binding: FragmentParametersDetailHistoryBinding

    private var previousMonthsAdapter: PreviousMonthsAdapter? = null
    private var expandableProfileAdapter: ExpandableProfileAdapter? = null
    private var previousMonthsList = ArrayList<MonthYear>()
    private var mapProfilesAll: MutableList<ParameterProfile> = mutableListOf()
    private var parentDataList: MutableList<ParentProfileModel> = mutableListOf()
    private var month = ""
    private var year = ""
    private var previousMonth = 0

    //private var from = ""
    private var cal = Calendar.getInstance()
    private var animation: LayoutAnimationController? = null
    private var toProceed = true

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentParametersDetailHistoryBinding.inflate(inflater, container, false)
        try {
            CleverTapHelper.pushEvent(
                requireContext(),
                CleverTapConstants.TRACK_PARAMETER_COMPLETE_HISTORY_SCREEN
            )
            setMonthYearView()
            setExpandableList()
            registerObserver()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return binding.root
    }

    private fun setMonthYearView() {
        animation =
            AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_slide_from_bottom)
        cal.time = Date()
        for (i in 5 downTo 0) {
            cal.time = Date()
            cal.add(Calendar.MONTH, -i)
            month = getFormattedValue("MMMM", cal.time)
            year = getFormattedValue("YYYY", cal.time)
            val monthYear = MonthYear()
            monthYear.month = month
            monthYear.year = year
            previousMonthsList.add(monthYear)
            Utilities.printLog("$month-$year")
        }
        previousMonthsList.reverse()

        month = previousMonthsList[0].month.substring(0, 3)
        year = previousMonthsList[0].year

        previousMonthsAdapter = PreviousMonthsAdapter(previousMonthsList, this, requireContext())
        binding.rvPreviousMonths.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvPreviousMonths.adapter = previousMonthsAdapter
        binding.txtSelectedMonthYear.text =
            "${Utilities.getMonthConverted(previousMonthsList[0].month, requireContext())} $year"
    }

    @SuppressLint("SetTextI18n")
    private fun setExpandableList() {
        viewModel.getAllProfileCodes()
        expandableProfileAdapter = ExpandableProfileAdapter(requireContext(), this)
        binding.rvExpandableProfile.layoutManager = LinearLayoutManager(context)
        binding.rvExpandableProfile.setExpanded(true)
        binding.rvExpandableProfile.layoutAnimation = animation
        binding.rvExpandableProfile.setHasFixedSize(true)
        binding.rvExpandableProfile.adapter = expandableProfileAdapter
        //viewModel.getParentDataList(mapProfilesAll,month,year)
    }

    private fun registerObserver() {

        viewModel.listAllProfiles.observe(viewLifecycleOwner) {
            if (it != null) {
                mapProfilesAll.clear()
                mapProfilesAll.addAll(it)
                Utilities.printData("allProfileCodes", it)
                viewModel.getParentDataList(mapProfilesAll, month, year)
            }
        }

        viewModel.parentDataList.observe(viewLifecycleOwner) {
            if (it != null) {
                parentDataList.clear()
                parentDataList = it.toMutableList()
                toProceed = false
                //Utilities.printLogError("parentProfilesList---> ${parentDataList.size}")
                val filteredList = getFilteredData(parentDataList)
                Utilities.printData("filteredList", filteredList, false)
                if (filteredList.size > 0) {
                    binding.rvExpandableProfile.layoutAnimation = animation
                    expandableProfileAdapter!!.updateData(filteredList)
                    binding.rvExpandableProfile.scheduleLayoutAnimation()
                    binding.rvExpandableProfile.visibility = View.VISIBLE
                    binding.layoutNoHistory.visibility = View.GONE
                } else {
                    binding.rvExpandableProfile.visibility = View.GONE
                    binding.layoutNoHistory.visibility = View.VISIBLE
                }
            }
        }

    }

    @SuppressLint("SetTextI18n")
    override fun onMonthSelection(yearMonth: MonthYear, newMonthPosition: Int) {
        if (previousMonth != newMonthPosition) {
            previousMonth = newMonthPosition
            Utilities.printLogError("SelectedMonth=>$previousMonth")
            month = previousMonthsList[newMonthPosition].month.substring(0, 3)
            year = previousMonthsList[newMonthPosition].year
            binding.txtSelectedMonthYear.text = Utilities.getMonthConverted(
                previousMonthsList[newMonthPosition].month,
                requireContext()
            ) + " " + year
            viewModel.getParentDataList(mapProfilesAll, month, year)
        }
    }

    private fun getFilteredData(list: MutableList<ParentProfileModel>): MutableList<ParentProfileModel> {
        try {
            for (item in list) {
                Utilities.printLogError("${item.profileCode}--> ${item.childParameterList.size}")
                val param = item.childParameterList.filter { hist ->
                    !hist.parameterCode.equals("WBC", true)
                            && !hist.parameterCode.equals("DLC", true)
                }.toMutableList()
                item.childParameterList = param
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    private fun getFormattedValue(format: String, date: Date): String {
        return SimpleDateFormat(format, Locale.ENGLISH).format(date)
    }

    fun showDetailsDialog(parameter: TrackParameterMaster.History, color: Int) {

        var value = ""
        var obs = ""
        var unit = ""
        if (parameter.profileCode.equals("URINE", ignoreCase = true)) {
            if (!Utilities.isNullOrEmpty(parameter.textValue)) {
                value = parameter.textValue!!
            }
        } else {
            if (!Utilities.isNullOrEmpty(parameter.value.toString())) {
                value = parameter.value.toString()
            }
        }

        if (!Utilities.isNullOrEmpty(parameter.observation)) {
            if (!parameter.observation.equals("NA", ignoreCase = true)) {
                obs = parameter.observation!!
            }
        }

        if (!Utilities.isNullOrEmpty(parameter.unit)) {
            unit = parameter.unit!!
        }

        val msg = if (!Utilities.isNullOrEmpty(obs)) {
            "$value  $unit \n $obs \n (${parameter.recordDate})"
        } else {
            "$value  $unit \n (${parameter.recordDate})"
        }
        showDialog(
            listener = this,
            title = "${parameter.description} ",
            message = msg,
            showLeftBtn = false
        )
    }

    override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) {}

}