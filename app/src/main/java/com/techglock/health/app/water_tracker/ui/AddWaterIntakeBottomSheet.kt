package com.techglock.health.app.water_tracker.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.techglock.health.app.R
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.utils.DateHelper
import com.techglock.health.app.common.utils.DialogHelper
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.BottomSheetAddWaterIntakeBinding
import com.techglock.health.app.water_tracker.adapter.DrinkQuantityAdapter
import com.techglock.health.app.water_tracker.adapter.DrinkTypeAdapter
import com.techglock.health.app.water_tracker.model.DrinkQuantityModel
import com.techglock.health.app.water_tracker.model.DrinkTypeModel
import com.techglock.health.app.water_tracker.viewmodel.WaterTrackerViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class AddWaterIntakeBottomSheet(
    val date: String,
    var listener: OnIntakeClickListener,
    val viewModel: WaterTrackerViewModel
) : BottomSheetDialogFragment(),
    DrinkTypeAdapter.OnDrinkTypeListener, DrinkQuantityAdapter.OnDrinkQuantityListener,
    ParameterValuePickerDialog.OnPickerDialogValueListener {

    private lateinit var binding: BottomSheetAddWaterIntakeBinding

    private var currentDate = DateHelper.currentDateAsStringyyyyMMdd
    private var selectedDate = date
    private var drinkTypeCode = "WATER"
    private var drinkQuantity = "100"
    private var drinkTypeAdapter: DrinkTypeAdapter? = null
    private var drinkQuantityAdapter: DrinkQuantityAdapter? = null

    //    private val viewModel.waterTrackerHelper: WaterTrackerHelper
    private val drinkTypeList = viewModel.waterTrackerHelper.getDrinkTypeList()

    private val df1 = SimpleDateFormat(DateHelper.SERVER_DATE_YYYYMMDD, Locale.ENGLISH)
    private val cal = Calendar.getInstance()
    private val calMin = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetAddWaterIntakeBinding.inflate(inflater, container, false)
        initialise()
        setClickable()
        return binding.root
    }

    private fun initialise() {

        binding.txtIntakeDate.text = DateHelper.currentDateAsStringddMMMyyyyNew

        drinkTypeAdapter = DrinkTypeAdapter(requireContext(), drinkTypeList, this)
        binding.rvDrinkType.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvDrinkType.adapter = drinkTypeAdapter

        drinkQuantityAdapter =
            DrinkQuantityAdapter(viewModel.waterTrackerHelper, requireContext(), this)
        binding.rvDrinkQuantity.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvDrinkQuantity.adapter = drinkQuantityAdapter
        drinkQuantityAdapter!!.updateList(viewModel.waterTrackerHelper.getDrinkQuantityList())

        viewModel.saveDailyWaterIntake.observe(viewLifecycleOwner) { }
    }

    private fun setClickable() {

        binding.layoutDate.setOnClickListener {
            showDatePicker()
        }

        binding.imgEdit.setOnClickListener {
            viewModel.waterTrackerHelper.showWaterIntakeDialog(
                0,
                this@AddWaterIntakeBottomSheet,
                requireContext()
            )
        }

        binding.btnDoneIntake.setOnClickListener {
            Utilities.printLogError("selectedDate--->$selectedDate")
            Utilities.printLogError("currentDate--->$currentDate")
            if (selectedDate == currentDate) {
                selectedDate = selectedDate + "T" + DateHelper.currentTimeAs_hh_mm_ss
                //selectedDate = "2022-06-03T18:45:00"
                Utilities.printLogError("selectedDate--->$selectedDate")
                viewModel.callSaveDailyWaterIntakeApi(
                    this,
                    listener,
                    drinkTypeCode,
                    drinkQuantity,
                    selectedDate,
                    Constants.TRUE
                )
            } else {
                selectedDate = DateHelper.convertDateSourceToDestination(
                    selectedDate,
                    DateHelper.SERVER_DATE_YYYYMMDD,
                    DateHelper.DATE_FORMAT_UTC
                )
                Utilities.printLogError("selectedDate--->$selectedDate")
                viewModel.callSaveDailyWaterIntakeApi(
                    this,
                    listener,
                    drinkTypeCode,
                    drinkQuantity,
                    selectedDate,
                    Constants.FALSE
                )
            }

            //dismiss()
            //listener.onIntakeClick(drinkTypeCode, drinkQuantity)
            //viewModel.callSaveWaterIntakeGoalApi2(this,"","false","","true", DateHelper.currentUTCDatetimeInMillisecAsString,"3500","CUSTOM")
        }

        binding.imgClose.setOnClickListener {
            dismiss()
        }

    }

    private fun showDatePicker() {
        try {
            cal.time = df1.parse(selectedDate)!!
            calMin.time = df1.parse(viewModel.joiningDate)!!
            DialogHelper().showDatePickerDialog(resources.getString(R.string.SELECT_DATE),
                requireContext(),
                cal,
                calMin,
                Calendar.getInstance(),

                object : DialogHelper.DateListener {

                    override fun onDateSet(
                        date: String,
                        year: String,
                        month: String,
                        dayOfMonth: String
                    ) {
                        selectedDate = DateHelper.convertDateSourceToDestination(
                            date,
                            DateHelper.DISPLAY_DATE_DDMMMYYYY,
                            DateHelper.SERVER_DATE_YYYYMMDD
                        )
                        Utilities.printLogError("SelectedDate--->$selectedDate")
                        //val date = year + "-" + (month + 1) + "-" + dayOfMonth
                        binding.txtIntakeDate.text =
                            DateHelper.formatDateValue(DateHelper.DATEFORMAT_DDMMMYYYY_NEW, date)
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
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
        const val TAG = "AddWaterIntakeBottomSheet"
    }

    interface OnIntakeClickListener {
        fun onIntakeClick(drinkCode: String, quantity: String)
    }

    override fun onDrinkTypeSelection(position: Int, drinkType: DrinkTypeModel) {
        drinkTypeCode = drinkType.drinkTypeCode
        Utilities.printLogError("Selected_Drink---> ${drinkType.drinkTypeCode}")
    }

    override fun onDrinkQuantitySelection(position: Int, quantity: DrinkQuantityModel) {
        drinkQuantity = quantity.quantity
        Utilities.printLogError("Selected_Quantity---> ${quantity.quantity}")
    }

    override fun onPickerDialogValueSelection(quantity: String) {
        drinkQuantity = quantity
        Utilities.printLogError("Selected_Quantity---> $quantity")
        drinkQuantityAdapter!!.addCustomDrinkQuantity(drinkQuantity)
        binding.rvDrinkQuantity.layoutManager!!.scrollToPosition(0)
    }

}

