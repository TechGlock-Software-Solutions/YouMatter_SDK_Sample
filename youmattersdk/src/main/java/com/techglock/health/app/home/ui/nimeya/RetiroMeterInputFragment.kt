package com.techglock.health.app.home.ui.nimeya

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseFragment
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.CleverTapConstants
import com.techglock.health.app.common.utils.CleverTapHelper
import com.techglock.health.app.common.utils.DateHelper
import com.techglock.health.app.common.utils.DialogHelper
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.FragmentRetiroMeterInputBinding
import com.techglock.health.app.home.common.NimeyaSingleton
import com.techglock.health.app.home.viewmodel.NimeyaViewModel
import com.techglock.health.app.model.nimeya.SaveRetiroMeterModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class RetiroMeterInputFragment : BaseFragment() {

    private val viewModel: NimeyaViewModel by lazy {
        ViewModelProvider(this)[NimeyaViewModel::class.java]
    }
    private lateinit var binding: FragmentRetiroMeterInputBinding

    private val cal = Calendar.getInstance()

    //private val calMin = Calendar.getInstance()
    private val df1 = SimpleDateFormat(DateHelper.SERVER_DATE_YYYYMMDD, Locale.ENGLISH)
    private var selectedDate = ""
    private var nimeyaSingleton = NimeyaSingleton.getInstance()!!

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // callback to Handle back button event
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                nimeyaSingleton.clearData()
                requireActivity().finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRetiroMeterInputBinding.inflate(inflater, container, false)
        try {
            initialise()
            setClickable()
            registerObserver()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun initialise() {
        CleverTapHelper.pushEvent(requireContext(), CleverTapConstants.YM_NIMEYA_RETIRO_METER)
        selectedDate = viewModel.dob
        binding.txtDob.text = DateHelper.convertDateSourceToDestination(
            selectedDate,
            DateHelper.SERVER_DATE_YYYYMMDD,
            DateHelper.DATEFORMAT_DDMMMYYYY_NEW
        ) + " (${
            DateHelper.calculatePersonAge(
                DateHelper.formatDateValue(
                    DateHelper.DISPLAY_DATE_DDMMMYYYY,
                    selectedDate
                )!!, requireContext()
            )
        })"
        //binding.txtDob.text = DateHelper.convertDateSourceToDestination(selectedDate,DateHelper.SERVER_DATE_YYYYMMDD,DateHelper.DATEFORMAT_DDMMMYYYY_NEW)
    }

    private fun setClickable() {

        binding.layoutDate.setOnClickListener {
            showDatePicker()
        }

        binding.btnSubmit.setOnClickListener {
            validate()
            //findNavController().navigate(R.id.action_retiroMeterInputFragment_to_retiroMeterResultFragment)
        }

    }

    private fun registerObserver() {
        viewModel.saveRetiroMeter.observe(viewLifecycleOwner) {}
    }

    private fun validate() {
        Utilities.printLogError("dob--->$selectedDate")
        val yourAge = DateHelper.calculatePersonAge(
            DateHelper.formatDateValue(
                DateHelper.DISPLAY_DATE_DDMMMYYYY,
                selectedDate
            )!!, requireContext()
        ).split(" ").toTypedArray()[0].toInt()
        val retirementAge = binding.edtRetirementAge.text.toString()
        val currentMonthlyIncome = binding.edtCurrentMonthlyIncome.text.toString()
        val desiredMonthlyRetirementIncome =
            binding.edtDesiredMonthlyRetirementIncome.text.toString()

        val pfDeposits = binding.edtPfDeposits.text.toString()
        val employeeProvidentFund = binding.edtEmployeeProvidentFund.text.toString()
        val nationalPensionScheme = binding.edtNatinalPensionScheme.text.toString()
        val equityMutualFundPensionInsurance =
            binding.edtEquityMutualFundPensionInsurance.text.toString()

        val pfDepositsRetirementSchemes = binding.edtPfDepositsRetirementSchemes.text.toString()
        val employeeProvidentFundRetirementSchemes =
            binding.edtEmployeeProvidentFundRetirementSchemes.text.toString()
        val nationalPensionSchemeRetirementSchemes =
            binding.edtNatinalPensionSchemeRetirementSchemes.text.toString()
        val equityMutualFundPensionInsuranceRetirementSchemes =
            binding.edtEquityMutualFundPensionInsuranceRetirementSchemes.text.toString()

        if (Utilities.isNullOrEmpty(selectedDate)) {
            Utilities.toastMessageShort(requireContext(), "Please select date of birth")
        } else if (Utilities.isNullOrEmptyOrZero(retirementAge)) {
            Utilities.toastMessageShort(requireContext(), "Please enter retirement age")
        } else if (retirementAge.toInt() > 85) {
            Utilities.toastMessageShort(requireContext(), "Retirement age must be less than 85")
        } else if (retirementAge.toInt() < yourAge) {
            Utilities.toastMessageShort(
                requireContext(),
                "Retirement age should be greater than your age"
            )
        } else if (Utilities.isNullOrEmpty(desiredMonthlyRetirementIncome)) {
            Utilities.toastMessageShort(
                requireContext(),
                "Please enter desired monthly retirement income"
            )
        } else if (Utilities.isNullOrEmpty(currentMonthlyIncome)) {
            Utilities.toastMessageShort(requireContext(), "Please enter current monthly income")
        } else if (Utilities.isNullOrEmpty(pfDeposits)) {
            Utilities.toastMessageShort(
                requireContext(),
                "Please enter amount in Public Provident Fund/Deposits"
            )
        } else if (Utilities.isNullOrEmpty(employeeProvidentFund)) {
            Utilities.toastMessageShort(
                requireContext(),
                "Please enter amount in Employee Provident Fund"
            )
        } else if (Utilities.isNullOrEmpty(nationalPensionScheme)) {
            Utilities.toastMessageShort(
                requireContext(),
                "Please enter amount in Natinal Pension Scheme"
            )
        } else if (Utilities.isNullOrEmpty(equityMutualFundPensionInsurance)) {
            Utilities.toastMessageShort(
                requireContext(),
                "Please enter amount in Equities/Mutual Fund/Deferred Pension/Insurance"
            )
        } else if (Utilities.isNullOrEmpty(pfDepositsRetirementSchemes)) {
            Utilities.toastMessageShort(
                requireContext(),
                "Please enter amount in Public Provident Fund/Deposits in Retirement Schemes"
            )
        } else if (Utilities.isNullOrEmpty(employeeProvidentFundRetirementSchemes)) {
            Utilities.toastMessageShort(
                requireContext(),
                "Please enter amount in Employee Provident Fund in Retirement Schemes"
            )
        } else if (Utilities.isNullOrEmpty(nationalPensionSchemeRetirementSchemes)) {
            Utilities.toastMessageShort(
                requireContext(),
                "Please enter amount in Natinal Pension Scheme in Retirement Schemes"
            )
        } else if (Utilities.isNullOrEmpty(equityMutualFundPensionInsuranceRetirementSchemes)) {
            Utilities.toastMessageShort(
                requireContext(),
                "Please enter amount in Equities/Mutual Fund/Deferred Pension/Insurance in Retirement Schemes"
            )
        } else {
            viewModel.callSaveRetiroMeterApi(binding.btnSubmit, gatherData())
            //findNavController().navigate(R.id.action_retiroMeterInputFragment_to_retiroMeterResultFragment)
        }
    }

    private fun gatherData(): SaveRetiroMeterModel.JSONDataRequest {
        val data = SaveRetiroMeterModel.JSONDataRequest(
            personID = viewModel.personId.toInt(),
            accountID = viewModel.accountID.toInt(),
            dateTime = DateHelper.currentDateAsStringyyyyMMdd + "T" + DateHelper.currentTimeAs_hh_mm_ss,

            dateOfBirth = selectedDate,
            plannedRetirementAge = binding.edtRetirementAge.text.toString().toInt(),
            monthlyIncome = binding.edtCurrentMonthlyIncome.text.toString().toInt(),
            desiredRetirementIncome = binding.edtDesiredMonthlyRetirementIncome.text.toString()
                .toInt(),

            retirementSavings = SaveRetiroMeterModel.RetirementSavings(
                ppf = binding.edtPfDeposits.text.toString().toInt(),
                epf = binding.edtEmployeeProvidentFund.text.toString().toInt(),
                nps = binding.edtNatinalPensionScheme.text.toString().toInt(),
                equityMutualFunds = binding.edtEquityMutualFundPensionInsurance.text.toString()
                    .toInt(),
            ),
            monthlySavingsRate = SaveRetiroMeterModel.MonthlySavingsRate(
                ppf = binding.edtPfDepositsRetirementSchemes.text.toString().toInt(),
                epf = binding.edtEmployeeProvidentFundRetirementSchemes.text.toString().toInt(),
                nps = binding.edtNatinalPensionSchemeRetirementSchemes.text.toString().toInt(),
                equityMutualFunds = binding.edtEquityMutualFundPensionInsuranceRetirementSchemes.text.toString()
                    .toInt(),
            )
        )
        return data
    }

    private fun showDatePicker() {
        try {
            if (!Utilities.isNullOrEmpty(selectedDate)) {
                cal.time = df1.parse(selectedDate)!!
            }
            //calMin.time = df1.parse(viewModel.joiningDate)!!
            DialogHelper().showDatePickerDialog(resources.getString(R.string.SELECT_DATE),
                requireContext(),
                cal,
                null,
                Calendar.getInstance(),
                object : DialogHelper.DateListener {
                    @SuppressLint("SetTextI18n")
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
                        binding.txtDob.text = "${
                            DateHelper.formatDateValue(
                                DateHelper.DATEFORMAT_DDMMMYYYY_NEW,
                                date
                            )
                        } (${
                            DateHelper.calculatePersonAge(
                                DateHelper.formatDateValue(
                                    DateHelper.DISPLAY_DATE_DDMMMYYYY,
                                    selectedDate
                                )!!, requireContext()
                            )
                        })"
                        //binding.txtDob.text = DateHelper.formatDateValue(DateHelper.DATEFORMAT_DDMMMYYYY_NEW, date)
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}