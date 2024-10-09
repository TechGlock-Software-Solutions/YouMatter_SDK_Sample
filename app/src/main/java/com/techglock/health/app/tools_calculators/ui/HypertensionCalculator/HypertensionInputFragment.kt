package com.techglock.health.app.tools_calculators.ui.HypertensionCalculator

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.ArrayMap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.CheckBox
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseFragment
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.CleverTapConstants
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.utils.*
import com.techglock.health.app.common.utils.Utilities.isKeyboardOpen
import com.techglock.health.app.common.view.SpinnerAdapter
import com.techglock.health.app.common.view.SpinnerModel
import com.techglock.health.app.databinding.DialogInputParameterBinding
import com.techglock.health.app.databinding.FragmentHypertensionInputBinding
import com.techglock.health.app.model.toolscalculators.UserInfoModel
import com.techglock.health.app.tools_calculators.adapter.ParameterAdapter
import com.techglock.health.app.tools_calculators.model.Answer
import com.techglock.health.app.tools_calculators.model.CalculatorDataSingleton
import com.techglock.health.app.tools_calculators.ui.HealthConditionDialog
import com.techglock.health.app.tools_calculators.viewmodel.ToolsCalculatorsViewModel
import com.techglock.health.app.tools_calculators.views.SystolicDiastolicDialogManager
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class HypertensionInputFragment : BaseFragment(),
    ParameterAdapter.ParameterOnClickListener,
    SystolicDiastolicDialogManager.OnDialogValueListener, HeightWeightDialog.OnDialogValueListener,
    HealthConditionDialog.OnHealthConditionValueListener {

    private lateinit var binding: FragmentHypertensionInputBinding

    private val viewModel: ToolsCalculatorsViewModel by lazy {
        ViewModelProvider(this)[ToolsCalculatorsViewModel::class.java]
    }

    private var calculatorDataSingleton: CalculatorDataSingleton? = null
    private var selectionList = ArrayList<String>()

    private var quizID = ""
    private var participationID = ""
    private var genderAdapter: SpinnerAdapter? = null
    private var parameterAdapter: ParameterAdapter? = null
    private var genderList = ArrayList<SpinnerModel>()
    private var paramList: MutableList<ParameterDataModel> = mutableListOf()
    private var answerArrayMap = ArrayMap<String, Answer>()
    private var userPreferenceModel = UserInfoModel()
    private var dialogInput: Dialog? = null
    private lateinit var dialogBinding: DialogInputParameterBinding

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // callback to Handle back button event
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                calculatorDataSingleton!!.clearData()
                if (requireActivity().intent.hasExtra(Constants.TO)) {
                    val extra = requireActivity().intent.getStringExtra(Constants.TO)
                    Utilities.printLog("CalculatorTO=> $extra")
                    if (extra.equals("DASH", true)) {
                        findNavController().navigateUp()
                    } else {
                        UserInfoModel.getInstance()!!.isDataLoaded = false
                        requireActivity().finish()
                    }
                }
                if (requireActivity().intent.hasExtra(Constants.SCREEN) && !Utilities.isNullOrEmpty(
                        requireActivity().intent.getStringExtra(Constants.SCREEN)
                    )
                ) {
                    findNavController().navigateUp()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHypertensionInputBinding.inflate(inflater, container, false)
        try {
            calculatorDataSingleton = CalculatorDataSingleton.getInstance()!!
            quizID = calculatorDataSingleton!!.quizId
            participationID = calculatorDataSingleton!!.participantID
            Utilities.printLogError("QuizID,ParticipationID---> $quizID , $participationID")
            initialise()
            setClickable()
            loadUserData()
//            FirebaseHelper.logScreenEvent(FirebaseConstants.HYPERTENSION_CALCULATOR_SCREEN)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return binding.root
    }

    private fun initialise() {
        CleverTapHelper.pushEvent(
            requireContext(),
            CleverTapConstants.HYPERTENSION_CALCULATOR_SCREEN
        )
        userPreferenceModel = UserInfoModel.getInstance()!!
        selectionList = calculatorDataSingleton!!.healthConditionSelection

        genderList = viewModel.dataHandler.getGenderList()
        genderAdapter = SpinnerAdapter(requireContext(), genderList)
        binding.spinnerGender.adapter = genderAdapter

        //dialogInput = Dialog(requireContext(), R.style.NoTitleDialog)
        //dialogInput!!.setContentView(R.layout.dialog_input_parameter)
        dialogInput = Dialog(requireContext(), R.style.NoTitleDialog)
        dialogBinding = DialogInputParameterBinding.inflate(layoutInflater)
        dialogInput!!.setContentView(dialogBinding.root)

        dialogInput!!.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialogInput!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogInput!!.window!!.currentFocus

        paramList = viewModel.dataHandler.getParameterList("BMI", this)
        parameterAdapter = ParameterAdapter(paramList, this, requireContext())
        binding.paramRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.paramRecyclerView.setExpanded(true)
        binding.paramRecyclerView.adapter = parameterAdapter

        var answer = Answer("WEIGHT", "0", "0")
        answerArrayMap["WEIGHT"] = answer
        answer = Answer("HEIGHT", "0", "0")
        answerArrayMap["HEIGHT"] = answer

        viewModel.hypertensionSaveResp.observe(viewLifecycleOwner) {}

        binding.swBpMedication.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.txtCheckedBpMedication.text = resources.getString(R.string.YES)
                binding.txtCheckedBpMedication.tag = "Yes"
            } else {
                binding.txtCheckedBpMedication.text = resources.getString(R.string.NO)
                binding.txtCheckedBpMedication.tag = "No"
            }
        }

        binding.swSmoke.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.txtCheckedSmoke.text = resources.getString(R.string.YES)
                binding.txtCheckedSmoke.tag = "Yes"
            } else {
                binding.txtCheckedSmoke.text = resources.getString(R.string.NO)
                binding.txtCheckedSmoke.tag = "No"
            }
        }

    }

    private fun loadUserData() {
        if (!Utilities.isNullOrEmptyOrZero(userPreferenceModel.getAge())) {
            binding.edtAge.setText(UserInfoModel.getInstance()!!.getAge())
        }
        if (UserInfoModel.getInstance()!!.isMale) {
            binding.spinnerGender.setSelection(0)
        } else {
            binding.spinnerGender.setSelection(1)
        }
        try {
            Utilities.printLog("LayoutSelection => ${binding.laySelection.childCount} :: ${binding.laySelection.childCount}")
            for (i in 0 until binding.laySelection.childCount) {
                val tag: String = binding.laySelection.getChildAt(i).tag.toString()
                for (str in selectionList) {
                    (binding.laySelection.getChildAt(i) as CheckBox).isChecked =
                        tag.equals(str, ignoreCase = true)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getAnswerList(): ArrayList<Answer> {
        val answers: ArrayList<Answer> = ArrayList()
        for ((_, value) in answerArrayMap) {
            answers.add(value)
        }
        return answers
    }

    private fun setClickable() {

        binding.spinnerGender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                Utilities.printLog("Selected Item:: $position")
                binding.txtGender.text = genderList[position].name
                for (i in genderList.indices) {
                    genderList[i].selection = i == position
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.txtGender.setOnClickListener {
            binding.spinnerGender.performClick()
        }

        binding.btnCalculate.setOnClickListener {
            if (validateParameter()) {
                saveParameter()
                calculatorDataSingleton!!.answerArrayMap = answerArrayMap
                calculatorDataSingleton!!.userPreferences = userPreferenceModel
                viewModel.callHypertensionSaveResponseApi(
                    "First",
                    participationID,
                    quizID,
                    getAnswerList()
                )
            }
        }

    }

    private fun saveParameter() {
        var answer: Answer
        for (i in paramList.indices) {
            when (paramList[i].code) {
                "HEIGHT" -> {
                    answer = Answer("HEIGHT", paramList[i].finalValue, paramList[i].value)
                    answerArrayMap["HEIGHT"] = answer
                }

                "WEIGHT" -> {
                    answer = Answer("WEIGHT", paramList[i].finalValue, paramList[i].value)
                    answerArrayMap["WEIGHT"] = answer
                }

                "SYSTOLIC_BP" -> {
                    answer = Answer("SYSTOLIC_BP", paramList[i].finalValue, paramList[i].value)
                    answerArrayMap["SYSTOLIC_BP"] = answer
                }

                "DIASTOLIC_BP" -> {
                    answer = Answer("DIASTOLIC_BP", paramList[i].finalValue, paramList[i].value)
                    answerArrayMap["DIASTOLIC_BP"] = answer
                }
            }
        }

        selectionList.clear()
        for (i in 0 until binding.laySelection.childCount) {
            //selectionList.add(""+i)
            if ((binding.laySelection.getChildAt(i) as CheckBox).isChecked) {
                Utilities.printLog("Item=>Selected:: " + binding.laySelection.getChildAt(i).tag)
                selectionList.add(binding.laySelection.getChildAt(i).tag.toString())
            }
        }
        calculatorDataSingleton?.healthConditionSelection = selectionList
        var answerCodes = "NONE"
        if (calculatorDataSingleton?.healthConditionSelection.isNullOrEmpty()) {
            answer = Answer("HHILL", answerCodes, "")
            answerArrayMap["HHILL"] = answer
        } else {
            answerCodes = ""
            for (item in calculatorDataSingleton!!.healthConditionSelection) {
                answerCodes = "$answerCodes$item,"
            }
            answer = Answer("HHILL", answerCodes, "")
            answerArrayMap["HHILL"] = answer
        }

        answer = Answer("GENDER", binding.txtGender.tag.toString(), "0")
        answerArrayMap["GENDER"] = answer
        answer = Answer("TRTHYPBP", binding.txtCheckedBpMedication.tag.toString(), "0")
        answerArrayMap["TRTHYPBP"] = answer
        answer = Answer("SMOKING", binding.txtCheckedSmoke.tag.toString(), "0")
        answerArrayMap["SMOKING"] = answer
        answer = Answer("AGE", binding.edtAge.text.toString(), "0")
        answerArrayMap["AGE"] = answer

        calculatorDataSingleton!!.personAge = binding.edtAge.text.toString()
        userPreferenceModel.isMale =
            !binding.txtGender.tag.toString().equals("female", ignoreCase = true)
    }

    private fun saveUserPreference(parameterDataModel: ParameterDataModel) {
        when (parameterDataModel.code) {
            "HEIGHT" -> userPreferenceModel.setHeight(parameterDataModel.finalValue)
            "WEIGHT" -> userPreferenceModel.setWeight(parameterDataModel.finalValue)
            "TOTAL_CHOL" -> userPreferenceModel.setCholesterol(parameterDataModel.finalValue)
            "HDL" -> userPreferenceModel.setHdl(parameterDataModel.finalValue)
            "SYSTOLIC_BP" -> userPreferenceModel.setSystolicBp(parameterDataModel.finalValue)
            "DIASTOLIC_BP" -> userPreferenceModel.setDiastolicBp(parameterDataModel.finalValue)
        }
    }

    private fun validateParameter(): Boolean {
        var isValid = false
        if (binding.edtAge.text.toString().equals("", ignoreCase = true)) {
            Utilities.toastMessageShort(context, resources.getString(R.string.PLEASE_PROVIDE_AGE))
            isValid = false
        } else if (!binding.edtAge.text.toString().equals("", ignoreCase = true)) {
            try {
                val age: Double = binding.edtAge.text.toString().toDouble()
                if (age > 17 && age <= 74) {
                    isValid = true
                    userPreferenceModel.setAge("" + age.toInt())
                } else {
                    isValid = false
                    Utilities.toastMessageShort(
                        context,
                        resources.getString(R.string.ENTER_AGE_BETWEEN_18_AND_74_YEARS)
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (isValid) {
            for (i in paramList.indices) {
                if (paramList[i].code.equals("HEIGHT", ignoreCase = true)) {
                    if (!Utilities.isNullOrEmptyOrZero(paramList[i].finalValue)) {
                        isValid = true
                        saveUserPreference(paramList[i])
                    } else {
                        isValid = false
                        Utilities.toastMessageShort(
                            context,
                            "${resources.getString(R.string.PLEASE_FILL_HEIGHT_DETAILS)}."
                        )
                        break
                    }
                } else if (paramList[i].code.equals("WEIGHT", ignoreCase = true)) {
                    if (!Utilities.isNullOrEmptyOrZero(paramList[i].finalValue)) {
                        isValid = true
                        saveUserPreference(paramList[i])
                    } else {
                        isValid = false
                        Utilities.toastMessageShort(
                            context,
                            "${resources.getString(R.string.PLEASE_FILL_WEIGHT_DETAILS)}."
                        )
                        break
                    }
                } else if (!paramList[i].code.equals("HEIGHT", ignoreCase = true)
                    && !paramList[i].code.equals("WEIGHT", ignoreCase = true)
                ) {
                    if (!paramList[i].finalValue.equals("", ignoreCase = true)) {
                        Utilities.printLogError("paramList=>${paramList[i].finalValue}")
                        val `val`: Double = paramList[i].finalValue.toDouble()
                        if (`val` >= paramList[i].minRange && `val` <= paramList[i].maxRange) {
                            isValid = true
                            saveUserPreference(paramList[i])
                        } else {
                            isValid = false
                            Utilities.toastMessageShort(
                                context,
                                paramList[i].title + " ${resources.getString(R.string.SHOULD_BE_BETWEEN)} "
                                        + paramList[i].minRange + " ${resources.getString(R.string.TO)} " + paramList[i].maxRange
                            )
                            break
                        }
                    } else {
                        isValid = false
                        Utilities.toastMessageShort(
                            context,
                            "${resources.getString(R.string.PLEASE_FILL)} " + paramList[i].title
                        )
                        break
                    }
                }
            }
        }
        return isValid
    }

    override fun onParameterClick(parameterDataModel: ParameterDataModel, position: Int) {
        binding.edtAge.clearFocus()
        if (parameterDataModel.code.equals("HEIGHT", ignoreCase = true)) {
            if (viewModel.getPreference("HEIGHT") == "cm") {
                parameterAdapter!!.paramList[0].unit = resources.getString(R.string.CM)
            } else {
                parameterAdapter!!.paramList[0].unit = resources.getString(R.string.FEET_INCH)
            }
            val heightWeightDialog = HeightWeightDialog(
                requireContext(),
                this,
                "Height",
                parameterAdapter!!.paramList[0]
            )
            heightWeightDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            heightWeightDialog.show()
        } else if (parameterDataModel.code.equals("WEIGHT", ignoreCase = true)) {
            if (viewModel.getPreference("WEIGHT") == "kg") {
                parameterAdapter!!.paramList[1].unit = resources.getString(R.string.KG)
            } else {
                parameterAdapter!!.paramList[1].unit = resources.getString(R.string.LBS)
            }
            val heightWeightDialog = HeightWeightDialog(
                requireContext(),
                this,
                "Weight",
                parameterAdapter!!.paramList[1]
            )
            heightWeightDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            heightWeightDialog.show()
        } else if (parameterDataModel.code.equals("SYSTOLIC_BP", ignoreCase = true)
            || parameterDataModel.code.equals("DIASTOLIC_BP", ignoreCase = true)
        ) {
            val customDialogManager = SystolicDiastolicDialogManager(
                requireContext(),
                this,
                parameterAdapter!!.paramList,
                position
            )
            customDialogManager.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            customDialogManager.show()
        } else {
            showInputDialog(parameterDataModel, position)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showInputDialog(param: ParameterDataModel, position: Int) {
        dialogBinding.titleInput.text = param.title
        dialogBinding.txtMessage.text =
            "${resources.getString(R.string.ENTER_YOUR)} " + param.title.lowercase() + " ${
                resources.getString(R.string.PARAMETER_VALUE)
            }."

        dialogBinding.inpLayoutInput.hint = param.minRange.toString() + " - " + param.maxRange
        dialogBinding.inpLayoutInput.setText(param.finalValue)
        paramList = parameterAdapter!!.paramList

        dialogBinding.btnSaveInput.setOnClickListener {
            if ((requireActivity()).isKeyboardOpen()) {
                KeyboardUtils.toggleSoftInput(requireContext())
            }
            if (!Utilities.isNullOrEmpty(dialogBinding.inpLayoutInput.text.toString())) {
                val value: Double = dialogBinding.inpLayoutInput.text.toString().toDouble()
                if (value >= param.minRange && value <= param.maxRange) {
                    paramList[position].value = dialogBinding.inpLayoutInput.text.toString()
                    paramList[position].finalValue = dialogBinding.inpLayoutInput.text.toString()
                    parameterAdapter!!.notifyDataSetChanged()
                    //parameterAdapter!!.updateList(paramList)
                    dialogInput!!.dismiss()
                } else {
                    Utilities.toastMessageShort(
                        context,
                        "${resources.getString(R.string.PLEASE_INPUT_VALUE_BETWEEN)} " + param.minRange + " ${
                            resources.getString(R.string.TO)
                        } " + param.maxRange
                    )
                }
            } else {
                dialogInput!!.dismiss()
            }
        }

        dialogBinding.imgCloseInput.setOnClickListener {
            dialogInput!!.dismiss()
        }
        dialogInput!!.show()
    }

    override fun onDialogValueListener(systolic: String, diastolic: String) {
        paramList = parameterAdapter!!.paramList
        Utilities.printLogError("systolic,diastolic=>$systolic,$diastolic")
        if (!systolic.equals("", ignoreCase = true)) {
            paramList[2].value = systolic
            paramList[2].finalValue = systolic
        }
        if (!diastolic.equals("", ignoreCase = true)) {
            paramList[3].value = diastolic
            paramList[3].finalValue = diastolic
        }
        parameterAdapter!!.notifyDataSetChanged()
        if ((requireActivity()).isKeyboardOpen()) {
            KeyboardUtils.toggleSoftInput(requireContext())
        }
    }

    override fun onDialogValueListener(
        dialogType: String,
        height: String,
        weight: String,
        unit: String,
        visibleValue: String
    ) {
        viewModel.updateUserPreference(unit)
        paramList = parameterAdapter!!.paramList
        if (dialogType.equals("Height", ignoreCase = true)) {
            paramList[0].unit = unit
            paramList[0].value = visibleValue
            paramList[0].finalValue = height
        } else {
            paramList[1].unit = unit
            paramList[1].value = visibleValue
            paramList[1].finalValue = weight
        }
        parameterAdapter!!.notifyDataSetChanged()
        //parameterAdapter!!.updateList(paramList)
    }

    @SuppressLint("SetTextI18n")
    override fun onHealthConditionValueListener() {
        Utilities.printLogError("Selected :: ${calculatorDataSingleton!!.healthConditionSelection.size}")
        //binding.txtSelection.text = "( ${resources.getString(R.string.HEALTH_CONDITION_RESULT)} ${calculatorDataSingleton!!.healthConditionSelection.size} ${resources.getString(R.string.HEALTH_CONDITION)} )"
    }

    override fun onResume() {
        super.onResume()
        //binding.txtSelection.text = "( ${resources.getString(R.string.HEALTH_CONDITION_RESULT)} ${calculatorDataSingleton!!.healthConditionSelection.size} ${resources.getString(R.string.HEALTH_CONDITION)} )"
    }
}
