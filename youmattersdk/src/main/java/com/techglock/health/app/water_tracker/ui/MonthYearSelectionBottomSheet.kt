package com.techglock.health.app.water_tracker.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.recyclerview.widget.GridLayoutManager
import com.techglock.health.app.R
import com.techglock.health.app.common.utils.DateHelper
import com.techglock.health.app.databinding.BottomSheetMonthYearSelectionBinding
import com.techglock.health.app.model.fitness.MonthModel
import com.techglock.health.app.water_tracker.adapter.MonthAdapter
import com.techglock.health.app.water_tracker.viewmodel.WaterTrackerViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class MonthYearSelectionBottomSheet(
    var m: String,
    var y: Int,
    val viewModel: WaterTrackerViewModel,
    var listener: OnMonthYearClickListener
) : BottomSheetDialogFragment(), MonthAdapter.OnMonthListener {

    private lateinit var binding: BottomSheetMonthYearSelectionBinding

    private var monthAdapter: MonthAdapter? = null
    private var monthList: MutableList<MonthModel> = mutableListOf()

    private val currentYear = DateHelper.currentYearAsStringyyyy.toInt()
    private var year = y
    private var joiningYear = viewModel.joiningDate.split("-")[0].toInt()
    private var month: MonthModel? = null
    private var animation: LayoutAnimationController? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetMonthYearSelectionBinding.inflate(inflater, container, false)
        initialise()
        setClickable()
        return binding.root
    }

    private fun initialise() {

        animation = AnimationUtils.loadLayoutAnimation(
            context,
            R.anim.layout_animation_slide_from_bottom_grid
        )
        binding.txtYear.text = y.toString()
        monthList.clear()
        monthList.addAll(viewModel.waterTrackerHelper.getAllMonthsOfYear(y))

        if (y == currentYear) {
            binding.imgNext.visibility = View.INVISIBLE
        }
        if (y == joiningYear) {
            binding.imgBack.visibility = View.INVISIBLE
        }

        monthAdapter = MonthAdapter(viewModel, requireContext(), this)
        binding.rvMonth.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.rvMonth.setExpanded(true)
        binding.rvMonth.layoutAnimation = animation
        monthAdapter!!.updateList(monthList, m, y)
        binding.rvMonth.adapter = monthAdapter

    }

    private fun setClickable() {

        binding.imgBack.setOnClickListener {
            //if ( joiningYear <= binding.txtYear.text.toString().toInt() - 1 ) {
            if (binding.txtYear.text.toString().toInt() - 1 >= joiningYear) {
                year = binding.txtYear.text.toString().toInt() - 1
                binding.txtYear.text = year.toString()
                monthList.clear()
                monthList.addAll(viewModel.waterTrackerHelper.getAllMonthsOfYear(year))

                binding.rvMonth.layoutAnimation = animation
                monthAdapter!!.updateList(monthList, "", year)
                binding.rvMonth.scheduleLayoutAnimation()

                if (year == joiningYear) {
                    binding.imgBack.visibility = View.INVISIBLE
                    binding.imgNext.visibility = View.VISIBLE
                } else {
                    binding.imgBack.visibility = View.VISIBLE
                    binding.imgNext.visibility = View.VISIBLE
                }
            }
        }

        binding.imgNext.setOnClickListener {
            if (binding.txtYear.text.toString().toInt() + 1 <= currentYear) {
                year = binding.txtYear.text.toString().toInt() + 1
                binding.txtYear.text = year.toString()
                monthList.clear()
                monthList.addAll(viewModel.waterTrackerHelper.getAllMonthsOfYear(year))

                binding.rvMonth.layoutAnimation = animation
                monthAdapter!!.updateList(monthList, "", year)
                binding.rvMonth.scheduleLayoutAnimation()

                if (year == currentYear) {
                    binding.imgNext.visibility = View.INVISIBLE
                    binding.imgBack.visibility = View.VISIBLE
                } else {
                    binding.imgNext.visibility = View.VISIBLE
                    binding.imgBack.visibility = View.VISIBLE
                }
            }
        }

        binding.imgClose.setOnClickListener {
            dismiss()
        }

        binding.btnDoneIntake.setOnClickListener {
            dismiss()
            listener.onMonthYearClick(month!!)
        }

    }

    override fun getTheme(): Int {
        //return super.getTheme();
        return R.style.BottomSheetDialog
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //return super.onCreateDialog(savedInstanceState);
        return BottomSheetDialog(requireContext(), theme)
    }

    companion object {
        const val TAG = "MonthYearSelectionBottomSheet"
    }

    interface OnMonthYearClickListener {
        fun onMonthYearClick(month: MonthModel)
    }

    override fun onMonthSelection(position: Int, mon: MonthModel) {
        month = mon
    }

}