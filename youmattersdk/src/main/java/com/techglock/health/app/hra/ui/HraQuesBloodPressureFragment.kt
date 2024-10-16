package com.techglock.health.app.hra.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseFragment
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.FragmentHraQuesBloodPressureBinding
import com.techglock.health.app.hra.common.HRAConstants
import com.techglock.health.app.hra.common.HraDataSingleton
import com.techglock.health.app.hra.common.Observations
import com.techglock.health.app.hra.common.Validations
import com.techglock.health.app.hra.viewmodel.HraViewModel
import com.techglock.health.app.model.hra.Option
import com.techglock.health.app.model.hra.Question
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HraQuesBloodPressureFragment(val qCode: String) : BaseFragment() {

    private lateinit var binding: FragmentHraQuesBloodPressureBinding
    private val viewModel: HraViewModel by lazy {
        ViewModelProvider(this)[HraViewModel::class.java]
    }

    private var systolic = 0
    private var diastolic = 0
    private var questionData = Question()
    private val hraDataSingleton = HraDataSingleton.getInstance()!!
    private var viewPagerActivity: HraQuestionsActivity? = null
    private var selectedOptionList: MutableList<Option> = mutableListOf()

    private val textWatcher: TextWatcher = object : TextWatcher {

        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

        override fun afterTextChanged(editable: Editable) {
            updateValuesAndSetObs()
        }

    }

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHraQuesBloodPressureBinding.inflate(inflater, container, false)
        if (userVisibleHint) {
            try {
                initialise()
                //setPreviousAnsData()
                checkBpExist()
                registerObservers()
                setClickable()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return binding.root
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser && isResumed) {
            try {
                initialise()
                //setPreviousAnsData()
                checkBpExist()
                registerObservers()
                setClickable()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun initialise() {
        viewPagerActivity = (activity as HraQuestionsActivity)
        Utilities.printLogError("qCode----->$qCode")
        //Utilities.printLogError("CurrentPageNo--->" + viewPagerActivity!!.getCurrentScreen())
        viewModel.getHRAQuestionData(qCode)

        binding.laySystolic.setTitle(resources.getString(R.string.SYSTOLIC))
        //binding.laySystolic.setImage(R.drawable.img_systolic)
        binding.laySystolic.setUnit(resources.getString(R.string.MM_HG))
        binding.laySystolic.editText!!.addTextChangedListener(textWatcher)

        binding.layDiastolic.setTitle(resources.getString(R.string.DIASTOLIC))
        //binding.layDiastolic.setImage(R.drawable.img_systolic)
        binding.layDiastolic.setUnit(resources.getString(R.string.MM_HG))
        binding.layDiastolic.editText!!.addTextChangedListener(textWatcher)

        //enableNextBtn(false)
    }

    /*    private fun setPreviousAnsData() {
            binding.rgPrevious.removeAllViews()
            val prevAnsList =
                hraDataSingleton.getPrevAnsList(viewPagerActivity!!.getCurrentScreen() - 1)
            Utilities.printLogError("prevAnsList---> $prevAnsList")
            if (prevAnsList.isNotEmpty()) {
                viewModel.setPreviousAnswersList(prevAnsList)
            }
        }*/

    private fun checkBpExist() {
        viewModel.getHRAVitalDetails()
    }

    private fun registerObservers() {

        var toProceed = true
        viewModel.quesData.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.imgQues.setImageResource(it.bgImage)
                binding.txtQues.setHtmlTextFromId(it.question)
                if (toProceed) {
                    questionData = it
                    hraDataSingleton.question = it
                    toProceed = false
                }
            }
        }

        viewModel.vitalDetailsSavedResponse.observe(viewLifecycleOwner) { vitalDetails ->
            if (vitalDetails != null) {
                //Utilities.printLog("HraVitalDetails--->$vitalDetails")
                var strSystolic = ""
                var strDiastolic = ""
                val bpDetails = vitalDetails.filter { vital ->
                    vital.VitalsKey.equals(HRAConstants.VitalKey_SystolicBP, ignoreCase = true)
                            || vital.VitalsKey.equals(
                        HRAConstants.VitalKey_DiastolicBP,
                        ignoreCase = true
                    )
                }
                Utilities.printLog("HraVitalDetails--->$bpDetails")
                for (bp in bpDetails) {
                    if (bp.VitalsKey.equals(HRAConstants.VitalKey_SystolicBP, ignoreCase = true)) {
                        if (!Utilities.isNullOrEmptyOrZero(bp.VitalsValue)) {
                            binding.laySystolic.setValue(bp.VitalsValue)
                            strSystolic = bp.VitalsValue
                        }
                        if (bp.VitalsValue == "0") {
                            binding.laySystolic.setValue("")
                            strSystolic = "0"
                        }
                    }
                    if (bp.VitalsKey.equals(HRAConstants.VitalKey_DiastolicBP, ignoreCase = true)) {
                        if (!Utilities.isNullOrEmptyOrZero(bp.VitalsValue)) {
                            binding.layDiastolic.setValue(bp.VitalsValue)
                            strDiastolic = bp.VitalsValue
                        }
                        if (bp.VitalsValue == "0") {
                            binding.layDiastolic.setValue("")
                            strDiastolic = "0"
                        }
                    }
                }
                if (Utilities.isNullOrEmpty(strSystolic) && Utilities.isNullOrEmpty(strDiastolic)) {
                    Utilities.printLog("BP Details not Exist...!!!")
                    viewModel.callIsBPExist(true, viewPagerActivity!!.personId)
                }
            }
        }

        viewModel.bpDetails.observe(viewLifecycleOwner) { bpDetails ->
            if (bpDetails != null) {
                if (!Utilities.isNullOrEmptyOrZero(bpDetails.bloodPressure.Systolic) &&
                    !Utilities.isNullOrEmptyOrZero(bpDetails.bloodPressure.Diastolic)
                ) {
                    val systolic = bpDetails.bloodPressure.Systolic!!.toDouble().toInt()
                    val diastolic = bpDetails.bloodPressure.Diastolic!!.toDouble().toInt()
                    binding.laySystolic.setValue(systolic.toString())
                    binding.layDiastolic.setValue(diastolic.toString())
                }
            }
        }

    }

    private fun setClickable() {

        /*        binding.rgPrevious.setOnCheckedChangeListener { _, _ ->

                    if (!Utilities.isNullOrEmpty(binding.laySystolic.getValue()) ||
                        !Utilities.isNullOrEmpty(binding.layDiastolic.getValue())) {
                        if (Validations.validateBP(systolic, diastolic, requireContext())) {
                            Utilities.hideKeyboard(requireActivity())
                            saveResponseForNextScreen(true)

                            viewModel.saveResponse("KNWBPNUM", "86_YES", resources.getString(R.string.YES), questionData.category, questionData.tabName, "")
                            viewModel.saveBloodPressureDetails(systolic, diastolic)
                            viewModel.removeSource(qCode)
                            viewPagerActivity!!.setCurrentScreen(viewPagerActivity!!.getCurrentScreen() - 1)
                        }
                    } else {
                        Utilities.hideKeyboard(requireActivity())
                        viewModel.removeSource(qCode)
                        viewPagerActivity!!.setCurrentScreen(viewPagerActivity!!.getCurrentScreen() - 1)
                    }
                }*/

        binding.layoutPrevious.setOnClickListener {
            if (!Utilities.isNullOrEmpty(binding.laySystolic.getValue()) ||
                !Utilities.isNullOrEmpty(binding.layDiastolic.getValue())
            ) {
                if (Validations.validateBP(systolic, diastolic, requireContext())) {
                    Utilities.hideKeyboard(requireActivity())
                    saveResponseForNextScreen(true)

                    viewModel.saveResponse(
                        "KNWBPNUM",
                        "86_YES",
                        "Yes",
                        questionData.category,
                        questionData.tabName,
                        ""
                    )
                    viewModel.saveBloodPressureDetails(systolic, diastolic)
                    viewModel.removeSource(qCode)
                    viewPagerActivity!!.setCurrentScreen(viewPagerActivity!!.getCurrentScreen() - 1)
                }
            } else {
                Utilities.hideKeyboard(requireActivity())
                viewModel.removeSource(qCode)
                viewPagerActivity!!.setCurrentScreen(viewPagerActivity!!.getCurrentScreen() - 1)
            }
        }

        binding.btnNext.setOnClickListener {
            if (Utilities.isNullOrEmptyOrZero(systolic.toString())) {
                Utilities.toastMessageShort(
                    requireContext(),
                    resources.getString(R.string.PLEASE_INSERT_SYSTOLIC_BP_VALUE)
                )
            } else if (Utilities.isNullOrEmptyOrZero(diastolic.toString())) {
                Utilities.toastMessageShort(
                    requireContext(),
                    resources.getString(R.string.PLEASE_INSERT_DIASTOLIC_BP_VALUE)
                )
            } else {
                if (Validations.validateBP(systolic, diastolic, requireContext())) {
                    Utilities.hideKeyboard(requireActivity())
                    saveResponseForNextScreen(true)
                    viewModel.saveResponse(
                        "KNWBPNUM",
                        "86_YES",
                        "Yes",
                        questionData.category,
                        questionData.tabName,
                        ""
                    )
                    viewModel.saveBloodPressureDetails(systolic, diastolic)
                    viewModel.removeSource(qCode)
                    viewPagerActivity!!.setCurrentScreen(viewPagerActivity!!.getCurrentScreen() + 1)
                }
            }
        }

        binding.btnDontRemember.setOnClickListener {
            systolic = 0
            diastolic = 0
            Utilities.hideKeyboard(requireActivity())
            saveResponseForNextScreen(false)
            viewModel.saveResponse(
                "KNWBPNUM",
                "86_NO",
                "No",
                questionData.category,
                questionData.tabName,
                ""
            )
            viewModel.saveBloodPressureDetails(systolic, diastolic)
            viewPagerActivity!!.setCurrentScreen(viewPagerActivity!!.getCurrentScreen() + 1)
        }

    }

    private fun updateValuesAndSetObs() {
        try {
            systolic =
                if (!Utilities.isNullOrEmptyOrZero(binding.laySystolic.editText!!.text.toString())) {
                    binding.laySystolic.editText!!.text.toString().toDouble().toInt()
                } else {
                    0
                }
            diastolic =
                if (!Utilities.isNullOrEmptyOrZero(binding.layDiastolic.editText!!.text.toString())) {
                    binding.layDiastolic.editText!!.text.toString().toDouble().toInt()
                } else {
                    0
                }

            /*            if (!Utilities.isNullOrEmptyOrZero(systolic.toString())
                            && !Utilities.isNullOrEmptyOrZero(diastolic.toString())) {
                            enableNextBtn(true)
                        } else {
                            enableNextBtn(false)
                        }*/
            Observations.setBPResult(
                systolic,
                diastolic,
                binding.txtObservation,
                binding.imgInfo,
                requireContext()
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveResponseForNextScreen(isFilled: Boolean) {
        selectedOptionList = mutableListOf()
        if (isFilled) {
            selectedOptionList.add(
                Option(
                    resources.getString(R.string.SYSTOLIC) + " : $systolic " + resources.getString(
                        R.string.MM_HG
                    ), Constants.SYSTOLIC, true
                )
            )
            selectedOptionList.add(
                Option(
                    resources.getString(R.string.DIASTOLIC) + " : $diastolic " + resources.getString(
                        R.string.MM_HG
                    ), Constants.DIASTOLIC, true
                )
            )
        } else {
            selectedOptionList.add(Option(resources.getString(R.string.I_DONT_REMEMBER), "", true))

        }
        hraDataSingleton.previousAnsList[viewPagerActivity!!.getCurrentScreen()] =
            selectedOptionList
    }

    private fun enableNextBtn(enable: Boolean) {
        binding.btnNext.isEnabled = enable
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.removeSource(qCode)
    }

}