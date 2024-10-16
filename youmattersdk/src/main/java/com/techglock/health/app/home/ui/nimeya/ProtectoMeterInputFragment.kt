package com.techglock.health.app.home.ui.nimeya

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseFragment
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.CleverTapConstants
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.utils.CleverTapHelper
import com.techglock.health.app.common.utils.DateHelper
import com.techglock.health.app.common.utils.DialogHelper
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.FragmentProtectoMeterInputBinding
import com.techglock.health.app.home.adapter.NimeyaFamilyMemberAdapter
import com.techglock.health.app.home.common.NimeyaSingleton
import com.techglock.health.app.home.viewmodel.NimeyaViewModel
import com.techglock.health.app.model.nimeya.SaveProtectoMeterModel
import com.techglock.health.app.model.nimeya.SaveProtectoMeterModel.FamilyDetail
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class ProtectoMeterInputFragment : BaseFragment(), NimeyaFamilyMemberAdapter.OnDeleteClickListener,
    NimeyaFamilyMemberAdapter.OnEditClickListener, DialogFamilyMember.OnAddEditClickListener {

    private val viewModel: NimeyaViewModel by lazy {
        ViewModelProvider(this)[NimeyaViewModel::class.java]
    }
    private lateinit var binding: FragmentProtectoMeterInputBinding

    private var nimeyaFamilyMemberAdapter: NimeyaFamilyMemberAdapter? = null
    private var animation: LayoutAnimationController? = null

    private var from = ""
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
        binding = FragmentProtectoMeterInputBinding.inflate(inflater, container, false)
        try {
            arguments?.let {
                if (it.containsKey(Constants.FROM)) {
                    from = it.getString(Constants.FROM)!!
                }
                Utilities.printLogError("from--->$from")
            }
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
        CleverTapHelper.pushEvent(requireContext(), CleverTapConstants.YM_NIMEYA_PROTECTO_METER)
        animation = AnimationUtils.loadLayoutAnimation(
            requireContext(),
            R.anim.layout_animation_slide_from_bottom
        )

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

        nimeyaFamilyMemberAdapter = NimeyaFamilyMemberAdapter(requireContext(), this, this)
        binding.rvFamilyMembers.setExpanded(true)
        binding.rvFamilyMembers.layoutAnimation = animation
        binding.rvFamilyMembers.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvFamilyMembers.adapter = nimeyaFamilyMemberAdapter

        val list = nimeyaSingleton.protectoMeterHistory.familyDetails
        val familyMemberList: MutableList<FamilyDetail> = mutableListOf()
        if (list.isNotEmpty()) {
            for (i in 0 until list.size) {
                familyMemberList.add(
                    FamilyDetail(
                        id = i,
                        name = list[i].name,
                        relation = list[i].relation,
                        dob = list[i].dob,
                        age = list[i].age,
                        isDependent = list[i].isDependent,
                        monthlyExpense = list[i].monthlyExpense
                    )
                )
            }
        }
        if (familyMemberList.isNotEmpty()) {
            nimeyaFamilyMemberAdapter!!.updateList(familyMemberList)
            binding.rvFamilyMembers.visibility = View.VISIBLE
            binding.txtNoMember.visibility = View.GONE
        } else {
            binding.rvFamilyMembers.visibility = View.GONE
            binding.txtNoMember.visibility = View.VISIBLE
        }
    }

    private fun setClickable() {

        binding.layoutDate.setOnClickListener {
            showDatePicker()
        }

        binding.btnAdd.setOnClickListener {
            val dialogUpdateApp = DialogFamilyMember(requireContext(), FamilyDetail(), this)
            dialogUpdateApp.show()
        }

        binding.btnSubmit.setOnClickListener {
            validate()
        }

    }

    private fun registerObserver() {
        viewModel.saveProtectoMeter.observe(viewLifecycleOwner) {}
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
        val income = binding.edtIncome.text.toString()
        val livingExpense = binding.edtLivingExpense.text.toString()
        val expenseParents = binding.edtExpenseParents.text.toString()
        val totalSumAssuredLifeInsurance = binding.edtTotalSumAssuredLifeInsurance.text.toString()
        val totalSumAssuredHealthInsurance =
            binding.edtTotalSumAssuredHealthInsurance.text.toString()
        val outstandingHomeLoans = binding.edtOutstandingHomeLoans.text.toString()
        val otherDebts = binding.edtOtherDebts.text.toString()
        val edtCurrentInvestment = binding.edtCurrentInvestment.text.toString()

        if (Utilities.isNullOrEmpty(selectedDate)) {
            Utilities.toastMessageShort(requireContext(), "Please select date of birth")
        } else if (Utilities.isNullOrEmptyOrZero(retirementAge)) {
            Utilities.toastMessageShort(requireContext(), "Please enter retirement age")
        } else if (retirementAge.toInt() < yourAge) {
            Utilities.toastMessageShort(
                requireContext(),
                "Retirement age should be greater than your age"
            )
        } else if (Utilities.isNullOrEmpty(income)) {
            Utilities.toastMessageShort(requireContext(), "Please enter your income")
        } else if (Utilities.isNullOrEmpty(livingExpense)) {
            Utilities.toastMessageShort(requireContext(), "Please enter your living expenses")
        } else if (Utilities.isNullOrEmpty(expenseParents)) {
            Utilities.toastMessageShort(
                requireContext(),
                "Please enter expense towards Dependant Parents"
            )
        } else if (Utilities.isNullOrEmpty(totalSumAssuredLifeInsurance)) {
            Utilities.toastMessageShort(
                requireContext(),
                "Please enter total Sum Assured on your existing life insurance policies"
            )
        } else if (Utilities.isNullOrEmpty(totalSumAssuredHealthInsurance)) {
            Utilities.toastMessageShort(
                requireContext(),
                "Please enter total Sum Assured on your existing health insurance policies"
            )
        } else if (Utilities.isNullOrEmpty(outstandingHomeLoans)) {
            Utilities.toastMessageShort(
                requireContext(),
                "Please enter Home Loan Balance/Outstanding"
            )
        } else if (Utilities.isNullOrEmpty(otherDebts)) {
            Utilities.toastMessageShort(requireContext(), "Please enter other debts")
        } else if (Utilities.isNullOrEmpty(edtCurrentInvestment)) {
            Utilities.toastMessageShort(requireContext(), "Please enter current investment")
        } else {
            viewModel.callSaveProtectoMeterApi(binding.btnSubmit, gatherData())
        }
    }

    private fun gatherData(): SaveProtectoMeterModel.JSONDataRequest {
        val familyMemberList: MutableList<FamilyDetail> = mutableListOf()
        if (nimeyaFamilyMemberAdapter!!.familyMemberList.isNotEmpty()) {
            familyMemberList.addAll(nimeyaFamilyMemberAdapter!!.familyMemberList)
            /*            for ( i in familyMemberList ) {
                            i.dob = DateHelper.convertDateSourceToDestination(i.dob,DateHelper.SERVER_DATE_YYYYMMDD,"dd MMMM, yyyy")
                        }*/
        }
        val data = SaveProtectoMeterModel.JSONDataRequest(
            personID = viewModel.personId.toInt(),
            accountID = viewModel.accountID.toInt(),
            dateTime = DateHelper.currentDateAsStringyyyyMMdd + "T" + DateHelper.currentTimeAs_hh_mm_ss,
            dateOfBirth = selectedDate,
            monthlyIncome = binding.edtIncome.text.toString().toInt(),
            monthlyExpenses = binding.edtLivingExpense.text.toString().toInt(),
            parentsMonthlyCost = binding.edtExpenseParents.text.toString().toInt(),
            lifeInsuranceCoverage = binding.edtTotalSumAssuredLifeInsurance.text.toString().toInt(),
            healthInsuranceCoverage = binding.edtTotalSumAssuredHealthInsurance.text.toString()
                .toInt(),
            outstandingLoans = binding.edtOutstandingHomeLoans.text.toString().toInt(),
            otherLiabilities = binding.edtOtherDebts.text.toString().toInt(),
            investmentPortfolio = binding.edtCurrentInvestment.text.toString().toInt(),
            retirementAge = binding.edtRetirementAge.text.toString().toInt(),
            familyDetails = familyMemberList
        )
        return data
    }

    override fun onDeleteClick(familyMemberList: MutableList<FamilyDetail>) {
        checkList(familyMemberList)
    }

    override fun onEditClick(familyMember: FamilyDetail) {
        val dialogUpdateApp = DialogFamilyMember(requireContext(), familyMember, this)
        dialogUpdateApp.show()
    }

    override fun onAddEditClick(from: String, familyMember: FamilyDetail) {
        Utilities.printLogError("from--->$from")
        binding.rvFamilyMembers.layoutAnimation = animation
        binding.rvFamilyMembers.scheduleLayoutAnimation()
        when (from) {
            Constants.ADD -> {
                if (nimeyaFamilyMemberAdapter!!.familyMemberList.size == 0) {
                    familyMember.id = 0
                } else {
                    familyMember.id = nimeyaFamilyMemberAdapter!!.familyMemberList.size
                }
                Utilities.printData("familyMember", familyMember, true)
                nimeyaFamilyMemberAdapter!!.familyMemberList.add(familyMember)
                checkList(nimeyaFamilyMemberAdapter!!.familyMemberList)
                binding.rvFamilyMembers.layoutManager!!.scrollToPosition(nimeyaFamilyMemberAdapter!!.familyMemberList.size - 1)
            }

            Constants.UPDATE -> {
                for (i in nimeyaFamilyMemberAdapter!!.familyMemberList) {
                    if (i.id == familyMember.id) {
                        i.name = familyMember.name
                        i.relation = familyMember.relation
                        i.dob = familyMember.dob
                        i.age = familyMember.age
                        i.isDependent = familyMember.isDependent
                    }
                }
                Utilities.printData("familyMember", familyMember, true)
            }
        }
        nimeyaFamilyMemberAdapter!!.refresh()
        Utilities.printData(
            "familyMemberListChanged",
            nimeyaFamilyMemberAdapter!!.familyMemberList,
            true
        )
    }

    private fun checkList(familyMemberList: MutableList<FamilyDetail>) {
        if (familyMemberList.isNotEmpty()) {
            binding.rvFamilyMembers.visibility = View.VISIBLE
            binding.txtNoMember.visibility = View.GONE
            binding.rvFamilyMembers.layoutAnimation = animation
            binding.rvFamilyMembers.scheduleLayoutAnimation()
        } else {
            binding.rvFamilyMembers.visibility = View.GONE
            binding.txtNoMember.visibility = View.VISIBLE
        }
    }

    private fun showDatePicker() {
        try {
            if (!Utilities.isNullOrEmpty(selectedDate)) {
                cal.time = df1.parse(selectedDate)!!
            }
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