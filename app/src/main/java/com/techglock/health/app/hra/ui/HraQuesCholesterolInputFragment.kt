package com.techglock.health.app.hra.ui

import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseFragment
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.utils.DecimalValueFilter
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.FragmentHraQuesCholestrolInputBinding
import com.techglock.health.app.hra.common.HraDataSingleton
import com.techglock.health.app.hra.common.HraHelper
import com.techglock.health.app.hra.viewmodel.HraViewModel
import com.techglock.health.app.model.entity.TrackParameterMaster
import com.techglock.health.app.model.hra.Option
import com.techglock.health.app.model.hra.Question
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HraQuesCholesterolInputFragment(val qCode: String) : BaseFragment() {

    private lateinit var binding: FragmentHraQuesCholestrolInputBinding
    private val viewModel: HraViewModel by lazy {
        ViewModelProvider(this)[HraViewModel::class.java]
    }

    private var questionData = Question()
    private val hraDataSingleton = HraDataSingleton.getInstance()!!
    private var viewPagerActivity: HraQuestionsActivity? = null
    private var selectedOptionList: MutableList<Option> = mutableListOf()
    private var prevAnsList: MutableList<Option> = mutableListOf()
    private var isCholesterolExist = false

    private var allParamList: MutableList<TrackParameterMaster.Parameter> = mutableListOf()

    private var isTotalChol = false
    private var isHdl = false
    private var isLdl = false
    private var isTry = false
    private var isVldl = false

    private var paramCodeTotalChol = "CHOL_TOTAL"
    private var paramCodeHdl = "CHOL_HDL"
    private var paramCodeLdl = "CHOL_LDL"
    private var paramCodeTry = "CHOL_TRY"
    private var paramCodeVldl = "CHOL_VLDL"

    private var cholTotal = TrackParameterMaster.Parameter()
    private var hdl = TrackParameterMaster.Parameter()
    private var ldl = TrackParameterMaster.Parameter()
    private var triglyceride = TrackParameterMaster.Parameter()
    private var vldl = TrackParameterMaster.Parameter()

    /*    private val textWatcher: TextWatcher = object : TextWatcher {

            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun afterTextChanged(editable: Editable) {
                enableOrDisableNextBtn()
            }

        }*/

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHraQuesCholestrolInputBinding.inflate(inflater, container, false)

        if (userVisibleHint) {
            try {
                initialise()
                setPreviousAnsData()
                checkLabDetailsExist()
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
                setPreviousAnsData()
                checkLabDetailsExist()
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
        viewModel.getParameterDataByProfileCode("LIPID")

        /*        val filters = arrayOfNulls<InputFilter>(1)
                filters[0] = InputFilter.LengthFilter(4) //Filter to 4 characters*/

        val decimalValueFilter = DecimalValueFilter(true)
        decimalValueFilter.setDigits(2)
        val generalDecimalFilter = arrayOf(decimalValueFilter, InputFilter.LengthFilter(6))

        binding.layTotalChol.setInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED)
        binding.layHdl.setInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED)
        binding.layLdl.setInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED)
        binding.layTriglycerides.setInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED)
        binding.layVldl.setInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED)

        binding.layTotalChol.editText!!.filters = generalDecimalFilter
        binding.layHdl.editText!!.filters = generalDecimalFilter
        binding.layLdl.editText!!.filters = generalDecimalFilter
        binding.layTriglycerides.editText!!.filters = generalDecimalFilter
        binding.layVldl.editText!!.filters = generalDecimalFilter

        //binding.layTotalChol.setImage(R.drawable.img_cholesteroal)
        //binding.layHdl.setImage(R.drawable.img_cholesteroal)
        //binding.layLdl.setImage(R.drawable.img_cholesteroal)
        //binding.layTriglycerides.setImage(R.drawable.img_cholesteroal)
        //binding.layVldl.setImage(R.drawable.img_cholesteroal)

        /*        binding.layTotalChol.editText!!.addTextChangedListener(textWatcher)
                binding.layHdl.editText!!.addTextChangedListener(textWatcher)
                binding.layLdl.editText!!.addTextChangedListener(textWatcher)
                binding.layTriglycerides.editText!!.addTextChangedListener(textWatcher)
                binding.layVldl.editText!!.addTextChangedListener(textWatcher)*/
    }

    private fun setPreviousAnsData() {
        //binding.rgPrevious.removeAllViews()
        prevAnsList = hraDataSingleton.getPrevAnsList(viewPagerActivity!!.getCurrentScreen() - 1)
        Utilities.printLogError("prevAnsList---> $prevAnsList")
        if (prevAnsList.isNotEmpty()) {
            viewModel.setPreviousAnswersList(prevAnsList)
            //showHideFields(prevAnsList)
        }
    }

    private fun checkLabDetailsExist() {
        viewModel.getHRALabDetails()
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

        viewModel.labParameter.observe(viewLifecycleOwner) {
            if (it != null) {
                allParamList.clear()
                allParamList.addAll(it)
                showHideFields(prevAnsList)
            }
        }

        viewModel.labDetailsSavedResponse.observe(viewLifecycleOwner) {
            if (it != null) {
                val labDetailsList = it
                Utilities.printLog("HRALabCholDetailsFromDB----> $labDetailsList")
                if (labDetailsList.any { labDetail ->
                        prevAnsList.any { option ->
                            option.answerCode.equals(
                                labDetail.ParameterCode,
                                ignoreCase = true
                            )
                        }
                    }) {
                    for (record in labDetailsList) {
                        setSavedData(record.ParameterCode, record.LabValue!!)
                    }
                    isCholesterolExist = true
                }
                if (!isCholesterolExist) {
                    Utilities.printLog("Cholesterol Details does not Exist.")
                    viewModel.callGetLabRecordsCholesterol(true, viewPagerActivity!!.personId)
                }
            }
        }

        viewModel.labRecordsChol.observe(viewLifecycleOwner) {
            if (it != null) {
                val labRecords = it.LabRecords!!
                if (labRecords.isNotEmpty()) {
                    val list = HraHelper.filterLabRecords(labRecords)
                    Utilities.printLog("HRALabCholDetailsFromServer----> $list")
                    for (record in list) {
                        setSavedData(record.ParameterCode!!, record.Value!!)
                        viewModel.saveHRALabDetailsBasedOnType(
                            "LIPID",
                            record.ParameterCode!!,
                            record.Value!!,
                            record.Unit!!
                        )
                    }
                }
            }
        }

    }

    private fun setClickable() {

        /*        binding.rgPrevious.setOnCheckedChangeListener { _, _ ->
                    Utilities.hideKeyboard(requireActivity())
                    viewModel.removeSource(qCode)
                    viewPagerActivity!!.setCurrentScreen(viewPagerActivity!!.getCurrentScreen() - 1)
                }*/

        /*        binding.layoutPrevious.setOnClickListener {
                    if ( !Utilities.isNullOrEmpty(binding.layTotalChol.getValue()) ||
                        !Utilities.isNullOrEmpty(binding.layHdl.getValue()) ||
                        !Utilities.isNullOrEmpty(binding.layLdl.getValue()) ||
                        !Utilities.isNullOrEmpty(binding.layTriglycerides.getValue()) ||
                        !Utilities.isNullOrEmpty(binding.layVldl.getValue())) {
                        if ( validateValuesAndSaveData() ) {
                            Utilities.hideKeyboard(requireActivity())
                            hraDataSingleton.previousAnsList[viewPagerActivity!!.getCurrentScreen() ] = selectedOptionList

                            viewModel.saveResponse("KNWLIPNUM","84_YES","Yes",questionData.category,questionData.tabName,"")
                            clearUnselectedLabValues()
                            viewModel.removeSource(qCode)
                            viewPagerActivity!!.setCurrentScreen( viewPagerActivity!!.getCurrentScreen() - 1 )
                        }
                    } else {
                        Utilities.hideKeyboard(requireActivity())
                        viewModel.removeSource(qCode)
                        viewPagerActivity!!.setCurrentScreen( viewPagerActivity!!.getCurrentScreen() - 1 )
                    }
                }*/

        binding.layoutPrevious.setOnClickListener {
            Utilities.hideKeyboard(requireActivity())
            viewModel.removeSource(qCode)
            viewPagerActivity!!.setCurrentScreen(viewPagerActivity!!.getCurrentScreen() - 1)
        }

        binding.btnNext.setOnClickListener {
            if (validateValuesAndSaveData()) {
                Utilities.hideKeyboard(requireActivity())
                hraDataSingleton.previousAnsList[viewPagerActivity!!.getCurrentScreen()] =
                    selectedOptionList

                viewModel.saveResponse(
                    "KNWLIPNUM",
                    "84_YES",
                    "Yes",
                    questionData.category,
                    questionData.tabName,
                    ""
                )
                clearUnselectedLabValues()
                viewModel.removeSource(qCode)
                viewPagerActivity!!.setCurrentScreen(viewPagerActivity!!.getCurrentScreen() + 1)
            }
        }

    }

    private fun setSavedData(parameterCode: String, value: String) {
        if (!Utilities.isNullOrEmptyOrZero(value)) {
            when (parameterCode) {
                paramCodeTotalChol -> binding.layTotalChol.setValue(
                    value.toDouble().toInt().toString()
                )

                paramCodeHdl -> binding.layHdl.setValue(value.toDouble().toInt().toString())
                paramCodeLdl -> binding.layLdl.setValue(value.toDouble().toInt().toString())
                paramCodeTry -> binding.layTriglycerides.setValue(
                    value.toDouble().toInt().toString()
                )

                paramCodeVldl -> binding.layVldl.setValue(value.toDouble().toInt().toString())
            }
        }
    }

    private fun clearUnselectedLabValues() {
        if (!isTotalChol) {
            viewModel.clearHRALabValue(paramCodeTotalChol)
        }
        if (!isHdl) {
            viewModel.clearHRALabValue(paramCodeHdl)
        }
        if (!isLdl) {
            viewModel.clearHRALabValue(paramCodeLdl)
        }
        if (!isTry) {
            viewModel.clearHRALabValue(paramCodeTry)
        }
        if (!isVldl) {
            viewModel.clearHRALabValue(paramCodeVldl)
        }
    }

    private fun validateValuesAndSaveData(): Boolean {
        selectedOptionList.clear()
        val totalChol = binding.layTotalChol.getValue()
        val hdlChol = binding.layHdl.getValue()
        val ldlChol = binding.layLdl.getValue()
        val triglycerideChol = binding.layTriglycerides.getValue()
        val vldlChol = binding.layVldl.getValue()

        if (binding.layTotalChol.visibility == View.VISIBLE) {
            if (Utilities.isNullOrEmpty(totalChol)) {
                Utilities.toastMessageShort(
                    requireContext(),
                    resources.getString(R.string.PLEASE_ENTER_VALID_TOTAL_CHOLESTEROL_VALUE)
                )
                return false
            } else {
                if (totalChol.toDouble() < cholTotal.minPermissibleValue!!.toDouble() || totalChol.toDouble() > cholTotal.maxPermissibleValue!!.toDouble()) {
                    Utilities.toastMessageShort(
                        requireContext(),
                        "${resources.getString(R.string.TOTAL_CHOLESTEROL_VALUE_MUST_BE_BETWEEN)} "
                                + cholTotal.minPermissibleValue + "-" + cholTotal.maxPermissibleValue
                    )
                    return false
                } else {
                    isTotalChol = true
                    viewModel.saveHRALabDetails(paramCodeTotalChol, totalChol, cholTotal.unit!!)
                    selectedOptionList.add(
                        Option(
                            cholTotal.description + " : $totalChol " + cholTotal.unit,
                            paramCodeTotalChol,
                            true
                        )
                    )
                }
            }
        } else {
            viewModel.clearHRALabValue(paramCodeTotalChol)
        }

        if (binding.layHdl.visibility == View.VISIBLE) {
            if (Utilities.isNullOrEmpty(hdlChol)) {
                Utilities.toastMessageShort(
                    requireContext(),
                    resources.getString(R.string.PLEASE_ENTER_VALID_HDL_VALUE)
                )
                return false
            } else {
                if (hdlChol.toDouble() < hdl.minPermissibleValue!!.toDouble() || hdlChol.toDouble() > hdl.maxPermissibleValue!!.toDouble()) {
                    Utilities.toastMessageShort(
                        requireContext(),
                        "${resources.getString(R.string.HDL_VALUE_MUST_BE_BETWEEN)} "
                                + hdl.minPermissibleValue + "-" + hdl.maxPermissibleValue
                    )

                    return false
                } else {
                    isHdl = true
                    viewModel.saveHRALabDetails(paramCodeHdl, hdlChol, hdl.unit!!)
                    selectedOptionList.add(
                        Option(
                            hdl.description + " : $hdlChol " + hdl.unit,
                            paramCodeHdl,
                            true
                        )
                    )
                }
            }
        } else {
            viewModel.clearHRALabValue(paramCodeHdl)
        }

        if (binding.layLdl.visibility == View.VISIBLE) {
            if (Utilities.isNullOrEmpty(ldlChol)) {
                Utilities.toastMessageShort(
                    requireContext(),
                    resources.getString(R.string.PLEASE_ENTER_VALID_LDL_VALUE)
                )
                return false
            } else {
                if (ldlChol.toDouble() < ldl.minPermissibleValue!!.toDouble() || ldlChol.toDouble() > ldl.maxPermissibleValue!!.toDouble()) {
                    Utilities.toastMessageShort(
                        requireContext(),
                        "${resources.getString(R.string.LDL_VALUE_MUST_BE_BETWEEN)} "
                                + ldl.minPermissibleValue + "-" + ldl.maxPermissibleValue
                    )

                    return false
                } else {
                    isLdl = true
                    viewModel.saveHRALabDetails(paramCodeLdl, ldlChol, ldl.unit!!)
                    selectedOptionList.add(
                        Option(
                            ldl.description + " : $ldlChol " + ldl.unit,
                            paramCodeLdl,
                            true
                        )
                    )
                }
            }
        } else {
            viewModel.clearHRALabValue(paramCodeLdl)
        }

        if (binding.layTriglycerides.visibility == View.VISIBLE) {
            if (Utilities.isNullOrEmpty(triglycerideChol)) {
                Utilities.toastMessageShort(
                    requireContext(),
                    resources.getString(R.string.PLEASE_ENTER_VALID_TRIGLYCERIDES_VALUE)
                )
                return false
            } else {
                if (triglycerideChol.toDouble() < triglyceride.minPermissibleValue!!.toDouble() || triglycerideChol.toDouble() > triglyceride.maxPermissibleValue!!.toDouble()) {
                    Utilities.toastMessageShort(
                        requireContext(),
                        "${resources.getString(R.string.TRIGLYCERIDES_VALUE_MUST_BE_BETWEEN)} "
                                + triglyceride.minPermissibleValue + "-" + triglyceride.maxPermissibleValue
                    )
                    return false
                } else {
                    isTry = true
                    viewModel.saveHRALabDetails(paramCodeTry, triglycerideChol, triglyceride.unit!!)
                    selectedOptionList.add(
                        Option(
                            triglyceride.description + " : $triglycerideChol " + triglyceride.unit,
                            paramCodeTry,
                            true
                        )
                    )
                }
            }
        } else {
            viewModel.clearHRALabValue(paramCodeTry)
        }

        if (binding.layVldl.visibility == View.VISIBLE) {
            if (Utilities.isNullOrEmpty(vldlChol)) {
                Utilities.toastMessageShort(
                    requireContext(),
                    resources.getString(R.string.PLEASE_ENTER_VALID_VLDL_VALUE)
                )
                return false
            } else {
                if (vldlChol.toDouble() < vldl.minPermissibleValue!!.toDouble() || vldlChol.toDouble() > vldl.maxPermissibleValue!!.toDouble()) {
                    Utilities.toastMessageShort(
                        requireContext(),
                        "${resources.getString(R.string.VLDL_VALUE_MUST_BE_BETWEEN)} "
                                + vldl.minPermissibleValue + "-" + vldl.maxPermissibleValue
                    )
                    return false
                } else {
                    isVldl = true
                    viewModel.saveHRALabDetails(paramCodeVldl, vldlChol, vldl.unit!!)
                    selectedOptionList.add(
                        Option(
                            vldl.description + " : $vldlChol " + vldl.unit,
                            paramCodeVldl,
                            true
                        )
                    )
                }
            }
        } else {
            viewModel.clearHRALabValue(paramCodeVldl)
        }
        return true
    }

    private fun showHideFields(prevAnsList: MutableList<Option>) {

        if (prevAnsList.any { it.answerCode.equals(paramCodeTotalChol, ignoreCase = true) }) {
            cholTotal = allParamList.find { it.code.equals(paramCodeTotalChol, true) }!!
            Utilities.printData("paramCodeTotalChol", cholTotal, true)
            //binding.lblTotalChol.text = cholTotal.description
            binding.layTotalChol.setTitle(cholTotal.description!!)
            binding.layTotalChol.setHint("" + cholTotal.minPermissibleValue + " - " + cholTotal.maxPermissibleValue)
            binding.layTotalChol.setUnit(cholTotal.unit!!)
            binding.layTotalChol.visibility = View.VISIBLE
        } else {
            binding.layTotalChol.visibility = View.GONE
        }

        if (prevAnsList.any { it.answerCode.equals(paramCodeHdl, ignoreCase = true) }) {
            hdl = allParamList.find { it.code.equals(paramCodeHdl, true) }!!
            Utilities.printData("paramCodeHdl", hdl, true)
            //binding.lblHdl.text = hdl.description
            binding.layHdl.setTitle(hdl.description!!)
            binding.layHdl.setHint("" + hdl.minPermissibleValue + " - " + hdl.maxPermissibleValue)
            binding.layHdl.setUnit(hdl.unit!!)
            binding.layHdl.visibility = View.VISIBLE
        } else {
            binding.layHdl.visibility = View.GONE
        }

        if (prevAnsList.any { it.answerCode.equals(paramCodeLdl, ignoreCase = true) }) {
            ldl = allParamList.find { it.code.equals(paramCodeLdl, true) }!!
            Utilities.printData("paramCodeLdl", ldl, true)
            //binding.lblLdl.text = ldl.description
            binding.layLdl.setTitle(ldl.description!!)
            binding.layLdl.setHint("" + ldl.minPermissibleValue + " - " + ldl.maxPermissibleValue)
            binding.layLdl.setUnit(ldl.unit!!)
            binding.layLdl.visibility = View.VISIBLE
        } else {
            binding.layLdl.visibility = View.GONE
        }

        if (prevAnsList.any { it.answerCode.equals(paramCodeTry, ignoreCase = true) }) {
            triglyceride = allParamList.find { it.code.equals(paramCodeTry, true) }!!
            Utilities.printData("paramCodeTry", triglyceride, true)
            //binding.lblTriglycerides.text = triglyceride.description
            binding.layTriglycerides.setTitle(triglyceride.description!!)
            binding.layTriglycerides.setHint("" + triglyceride.minPermissibleValue + " - " + triglyceride.maxPermissibleValue)
            binding.layTriglycerides.setUnit(triglyceride.unit!!)
            binding.layTriglycerides.visibility = View.VISIBLE
        } else {
            binding.layTriglycerides.visibility = View.GONE
        }

        if (prevAnsList.any { it.answerCode.equals(paramCodeVldl, ignoreCase = true) }) {
            vldl = allParamList.find { it.code.equals(paramCodeVldl, true) }!!
            Utilities.printData("paramCodeVldl", vldl, true)
            //binding.lblVldl.text = vldl.description
            binding.layVldl.setTitle(vldl.description!!)
            binding.layVldl.setHint("" + vldl.minPermissibleValue + " - " + vldl.maxPermissibleValue)
            binding.layVldl.setUnit(vldl.unit!!)
            binding.layVldl.visibility = View.VISIBLE
        } else {
            binding.layVldl.visibility = View.GONE
        }

    }

    private fun enableOrDisableNextBtn() {
        var toEnable = true
        if (binding.layTotalChol.visibility == View.VISIBLE && Utilities.isNullOrEmptyOrZero(binding.layTotalChol.getValue())) {
            toEnable = false
        }
        if (binding.layHdl.visibility == View.VISIBLE && Utilities.isNullOrEmptyOrZero(binding.layHdl.getValue())) {
            toEnable = false
        }
        if (binding.layLdl.visibility == View.VISIBLE && Utilities.isNullOrEmptyOrZero(binding.layLdl.getValue())) {
            toEnable = false
        }
        if (binding.layTriglycerides.visibility == View.VISIBLE
            && Utilities.isNullOrEmptyOrZero(binding.layTriglycerides.getValue())
        ) {
            toEnable = false
        }
        if (binding.layVldl.visibility == View.VISIBLE && Utilities.isNullOrEmptyOrZero(binding.layVldl.getValue())) {
            toEnable = false
        }

        binding.btnNext.isEnabled = toEnable

    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.removeSource(qCode)
    }

}