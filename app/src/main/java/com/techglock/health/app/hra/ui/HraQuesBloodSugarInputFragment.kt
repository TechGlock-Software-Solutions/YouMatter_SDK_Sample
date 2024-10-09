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
import com.techglock.health.app.databinding.FragmentHraQuesBloodSugarInputBinding
import com.techglock.health.app.hra.common.HraDataSingleton
import com.techglock.health.app.hra.common.HraHelper
import com.techglock.health.app.hra.viewmodel.HraViewModel
import com.techglock.health.app.model.entity.TrackParameterMaster
import com.techglock.health.app.model.hra.Option
import com.techglock.health.app.model.hra.Question
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HraQuesBloodSugarInputFragment(val qCode: String) : BaseFragment() {

    private lateinit var binding: FragmentHraQuesBloodSugarInputBinding
    private val viewModel: HraViewModel by lazy {
        ViewModelProvider(this)[HraViewModel::class.java]
    }

    private var questionData = Question()
    private val hraDataSingleton = HraDataSingleton.getInstance()!!
    private var viewPagerActivity: HraQuestionsActivity? = null
    private var selectedOptionList: MutableList<Option> = mutableListOf()
    private var prevAnsList: MutableList<Option> = mutableListOf()
    private var isBsExist = false

    private var allParamList: MutableList<TrackParameterMaster.Parameter> = mutableListOf()

    private var isRa = false
    private var isFs = false
    private var isPm = false
    private var isHba1c = false

    private var paramCodeRa = "DIAB_RA"
    private var paramCodeFs = "DIAB_FS"
    private var paramCodePm = "DIAB_PM"
    private var paramCodeHba1c = "DIAB_HBA1C"

    private var rs = TrackParameterMaster.Parameter()
    private var fs = TrackParameterMaster.Parameter()
    private var ps = TrackParameterMaster.Parameter()
    private var hb = TrackParameterMaster.Parameter()

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
        binding = FragmentHraQuesBloodSugarInputBinding.inflate(inflater, container, false)

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
        viewModel.getParameterDataByProfileCode("DIABETIC")

        /*        val filters = arrayOfNulls<InputFilter>(1)
                filters[0] = LengthFilter(4) //Filter to 4 characters
                val decimalFilters = arrayOfNulls<InputFilter>(1)
                decimalFilters[0] = DecimalDigitsInputFilter(5, 1) //Filter to 4 characters and 1 decimal point*/

        val decimalValueFilter = DecimalValueFilter(true)
        decimalValueFilter.setDigits(2)
        val generalDecimalFilter = arrayOf(decimalValueFilter, InputFilter.LengthFilter(6))

        binding.randomBs.setInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED)
        binding.fastingBs.setInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED)
        binding.postMealBs.setInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED)
        binding.hab1cBs.setInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED)

        binding.randomBs.editText!!.filters = generalDecimalFilter
        binding.fastingBs.editText!!.filters = generalDecimalFilter
        binding.postMealBs.editText!!.filters = generalDecimalFilter
        binding.hab1cBs.editText!!.filters = generalDecimalFilter

        //binding.layRandomBs.setImage(R.drawable.img_diabetes)
        //binding.layFastingBs.setImage(R.drawable.img_diabetes)
        //binding.layPostMealBs.setImage(R.drawable.img_diabetes)
        //binding.layHbA1cBs.setImage(R.drawable.img_diabetes)

        /*        binding.randomBs.editText!!.addTextChangedListener(textWatcher)
                binding.fastingBs.editText!!.addTextChangedListener(textWatcher)
                binding.postMealBs.editText!!.addTextChangedListener(textWatcher)
                binding.hab1cBs.editText!!.addTextChangedListener(textWatcher)*/
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

            binding.imgQues.setImageResource(it.bgImage)
            binding.txtQues.setHtmlTextFromId(it.question)
            if (it != null) {
                if (toProceed) {
                    questionData = it
                    hraDataSingleton.question = it
                    toProceed = false
                }
            }
        }

        viewModel.labParameter.observe(viewLifecycleOwner) {
            if (it != null) {
                Utilities.printData("allParamList", allParamList, true)
                allParamList.clear()
                allParamList.addAll(it)
                showHideFields(prevAnsList)
            }
        }

        viewModel.labDetailsSavedResponse.observe(viewLifecycleOwner) {
            if (it != null) {
                val labDetailsList = it
                Utilities.printLog("HRALabBsDetailsFromDB----> $labDetailsList")
                if (labDetailsList.any { labDetail ->
                        prevAnsList.any { option ->
                            option.answerCode.equals(labDetail.ParameterCode, ignoreCase = true)
                        }
                    }) {
                    for (record in labDetailsList) {
                        setSavedData(record.ParameterCode, record.LabValue!!)
                    }
                    isBsExist = true
                }
                if (!isBsExist) {
                    Utilities.printLog("Blood Sugar Details does not Exist.")
                    viewModel.callGetLabRecordsBloodSugar(true, viewPagerActivity!!.personId)
                }
            }
        }

        viewModel.labRecordsBs.observe(viewLifecycleOwner) {
            if (it != null) {
                val labRecords = it.LabRecords!!
                if (labRecords.isNotEmpty()) {
                    val list = HraHelper.filterLabRecords(labRecords)
                    Utilities.printLog("HRALabBsDetailsFromServer----> $list")
                    for (record in list) {
                        setSavedData(record.ParameterCode!!, record.Value!!)
                        viewModel.saveHRALabDetailsBasedOnType(
                            "SUGAR",
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
                    if ( !Utilities.isNullOrEmpty(binding.randomBs.getValue()) ||
                        !Utilities.isNullOrEmpty(binding.fastingBs.getValue()) ||
                        !Utilities.isNullOrEmpty(binding.postMealBs.getValue()) ||
                        !Utilities.isNullOrEmpty(binding.hab1cBs.getValue())) {
                        if ( validateValuesAndSaveData() ) {
                            Utilities.hideKeyboard(requireActivity())
                            hraDataSingleton.previousAnsList[viewPagerActivity!!.getCurrentScreen() ] = selectedOptionList

                            viewModel.saveResponse("KNWDIANUM","85_YES","Yes",questionData.category,questionData.tabName,"")
                            clearUnselectedLabValues()
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
                    "KNWDIANUM",
                    "85_YES",
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
                paramCodeRa -> binding.randomBs.setValue(value.toDouble().toInt().toString())
                paramCodeFs -> binding.fastingBs.setValue(value.toDouble().toInt().toString())
                paramCodePm -> binding.postMealBs.setValue(value.toDouble().toInt().toString())
                paramCodeHba1c -> binding.hab1cBs.setValue(value.toDouble().toInt().toString())
            }
        }
    }

    private fun clearUnselectedLabValues() {
        if (!isRa) {
            viewModel.clearHRALabValue(paramCodeRa)
        }
        if (!isFs) {
            viewModel.clearHRALabValue(paramCodeFs)
        }
        if (!isPm) {
            viewModel.clearHRALabValue(paramCodePm)
        }
        if (!isHba1c) {
            viewModel.clearHRALabValue(paramCodeHba1c)
        }
    }

    private fun validateValuesAndSaveData(): Boolean {
        selectedOptionList.clear()
        val rbs = binding.randomBs.getValue()
        val fbs = binding.fastingBs.getValue()
        val pbs = binding.postMealBs.getValue()
        val hab1cbs = binding.hab1cBs.getValue()

        if (binding.randomBs.visibility == View.VISIBLE) {
            if (Utilities.isNullOrEmpty(rbs)) {
                Utilities.toastMessageShort(
                    requireContext(),
                    resources.getString(R.string.PLEASE_ENTER_VALID_RANDOM_SUGAR_VALUE)
                )
                return false
            } else {
                if (rbs.toDouble() < rs.minPermissibleValue!!.toDouble() || rbs.toDouble() > rs.maxPermissibleValue!!.toDouble()) {
                    Utilities.toastMessageShort(
                        requireContext(),
                        "${resources.getString(R.string.RANDOM_SUGAR_VALUE_MUST_BE_BETWEEN)} "
                                + rs.minPermissibleValue + "-" + rs.maxPermissibleValue
                    )
                    return false
                } else {
                    isRa = true
                    viewModel.saveHRALabDetails(paramCodeRa, rbs, rs.unit!!)
                    selectedOptionList.add(
                        Option(
                            rs.description + " : $rbs " + rs.unit,
                            paramCodeRa,
                            true
                        )
                    )
                }
            }
        } else {
            viewModel.clearHRALabValue(paramCodeRa)
        }

        if (binding.fastingBs.visibility == View.VISIBLE) {
            if (Utilities.isNullOrEmpty(fbs)) {
                Utilities.toastMessageShort(
                    requireContext(),
                    resources.getString(R.string.PLEASE_ENTER_VALID_FASTING_SUGAR_VALUE)
                )
                return false
            } else {
                if (fbs.toDouble() < fs.minPermissibleValue!!.toDouble() || fbs.toDouble() > fs.maxPermissibleValue!!.toDouble()) {
                    Utilities.toastMessageShort(
                        requireContext(),
                        "${resources.getString(R.string.FASTING_SUGAR_VALUE_MUST_BE_BETWEEN)} "
                                + fs.minPermissibleValue + "-" + fs.maxPermissibleValue
                    )
                    return false
                } else {
                    isFs = true
                    viewModel.saveHRALabDetails(paramCodeFs, fbs, fs.unit!!)
                    selectedOptionList.add(
                        Option(
                            fs.description + " : $fbs " + fs.unit,
                            paramCodeFs,
                            true
                        )
                    )
                }
            }
        } else {
            viewModel.clearHRALabValue(paramCodeFs)
        }

        if (binding.postMealBs.visibility == View.VISIBLE) {
            if (Utilities.isNullOrEmpty(pbs)) {
                Utilities.toastMessageShort(
                    requireContext(),
                    resources.getString(R.string.PLEASE_ENTER_VALID_POST_MEAL_BLOOD_SUGAR_VALUE)
                )
                return false
            } else {
                if (pbs.toDouble() < ps.minPermissibleValue!!.toDouble() || pbs.toDouble() > ps.maxPermissibleValue!!.toDouble()) {
                    Utilities.toastMessageShort(
                        requireContext(),
                        "${resources.getString(R.string.POST_MEAL_BLOOD_SUGAR_VALUE_MUST_BE_BETWEEN)} "
                                + ps.minPermissibleValue + "-" + ps.maxPermissibleValue
                    )
                    return false
                } else {
                    isPm = true
                    viewModel.saveHRALabDetails(paramCodePm, pbs, ps.unit!!)
                    selectedOptionList.add(
                        Option(
                            ps.description + " : $pbs " + ps.unit,
                            paramCodePm,
                            true
                        )
                    )
                }
            }
        } else {
            viewModel.clearHRALabValue(paramCodePm)
        }

        if (binding.hab1cBs.visibility == View.VISIBLE) {
            if (Utilities.isNullOrEmpty(hab1cbs)) {
                Utilities.toastMessageShort(
                    requireContext(),
                    resources.getString(R.string.PLEASE_ENTER_VALID_HBA1C_SUGAR_VALUE)
                )
                return false
            } else {
                if (hab1cbs.toDouble() < hb.minPermissibleValue!!.toDouble() || hab1cbs.toDouble() > hb.maxPermissibleValue!!.toDouble()) {
                    Utilities.toastMessageShort(
                        requireContext(),
                        "${resources.getString(R.string.HBA1C_VALUE_MUST_BE_BETWEEN)} "
                                + hb.minPermissibleValue + "-" + hb.maxPermissibleValue
                    )
                    return false
                } else {
                    isHba1c = true
                    viewModel.saveHRALabDetails(paramCodeHba1c, hab1cbs, hb.unit!!)
                    selectedOptionList.add(
                        Option(
                            hb.description + " : $hab1cbs " + hb.unit,
                            paramCodeHba1c,
                            true
                        )
                    )
                }
            }
        } else {
            viewModel.clearHRALabValue(paramCodeHba1c)
        }

        return true
    }

    private fun showHideFields(prevAnsList: MutableList<Option>) {

        if (prevAnsList.any { it.answerCode.equals(paramCodeRa, ignoreCase = true) }) {
            rs = allParamList.find { it.code.equals(paramCodeRa, true) }!!
            Utilities.printData("paramCodeRa", rs, true)
            //binding.lblQuesRandomBs.text = rs.description
            binding.randomBs.setTitle(rs.description!!)
            binding.randomBs.setHint("" + rs.minPermissibleValue + " - " + rs.maxPermissibleValue)
            binding.randomBs.setUnit(rs.unit!!)
            binding.randomBs.visibility = View.VISIBLE
        } else {
            binding.randomBs.visibility = View.GONE
        }

        if (prevAnsList.any { it.answerCode.equals(paramCodeFs, ignoreCase = true) }) {
            fs = allParamList.find { it.code.equals(paramCodeFs, true) }!!
            Utilities.printData("paramCodeFs", fs, true)
            //binding.lblQuesFastingBs.text = fs.description
            binding.fastingBs.setTitle(fs.description!!)
            binding.fastingBs.setHint("" + fs.minPermissibleValue + " - " + fs.maxPermissibleValue)
            binding.fastingBs.setUnit(fs.unit!!)
            binding.fastingBs.visibility = View.VISIBLE
        } else {
            binding.fastingBs.visibility = View.GONE
        }

        if (prevAnsList.any { it.answerCode.equals(paramCodePm, ignoreCase = true) }) {
            ps = allParamList.find { it.code.equals(paramCodePm, true) }!!
            Utilities.printData("paramCodePm", ps, true)
            //binding.lblQuesPostMealBs.text = ps.description
            binding.postMealBs.setTitle(ps.description!!)
            binding.postMealBs.setHint("" + ps.minPermissibleValue + " - " + ps.maxPermissibleValue)
            binding.postMealBs.setUnit(ps.unit!!)
            binding.postMealBs.visibility = View.VISIBLE
        } else {
            binding.postMealBs.visibility = View.GONE
        }

        if (prevAnsList.any { it.answerCode.equals(paramCodeHba1c, ignoreCase = true) }) {
            hb = allParamList.find { it.code.equals(paramCodeHba1c, true) }!!
            Utilities.printData("paramCodeHba1c", hb, true)
            //binding.lblQuesHbA1cBs.text = hb.description
            binding.hab1cBs.setTitle(hb.description!!)
            binding.hab1cBs.setHint("" + hb.minPermissibleValue + " - " + hb.maxPermissibleValue)
            binding.hab1cBs.setUnit(hb.unit!!)
            binding.hab1cBs.visibility = View.VISIBLE
        } else {
            binding.hab1cBs.visibility = View.GONE
        }

    }

    private fun enableOrDisableNextBtn() {
        var toEnable = true
        if (binding.randomBs.visibility == View.VISIBLE && Utilities.isNullOrEmptyOrZero(binding.randomBs.getValue())) {
            toEnable = false
        }
        if (binding.fastingBs.visibility == View.VISIBLE && Utilities.isNullOrEmptyOrZero(binding.fastingBs.getValue())) {
            toEnable = false
        }
        if (binding.postMealBs.visibility == View.VISIBLE && Utilities.isNullOrEmptyOrZero(binding.postMealBs.getValue())) {
            toEnable = false
        }
        if (binding.hab1cBs.visibility == View.VISIBLE && Utilities.isNullOrEmptyOrZero(binding.hab1cBs.getValue())) {
            toEnable = false
        }

        binding.btnNext.isEnabled = toEnable

    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.removeSource(qCode)
    }

}