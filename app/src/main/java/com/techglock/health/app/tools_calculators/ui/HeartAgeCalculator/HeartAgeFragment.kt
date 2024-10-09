package com.techglock.health.app.tools_calculators.ui.HeartAgeCalculator

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.ArrayMap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
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
import com.techglock.health.app.databinding.FragmentHeartAgeBinding
import com.techglock.health.app.model.toolscalculators.UserInfoModel
import com.techglock.health.app.tools_calculators.adapter.ParameterAdapter
import com.techglock.health.app.tools_calculators.model.Answer
import com.techglock.health.app.tools_calculators.model.CalculatorDataSingleton
import com.techglock.health.app.tools_calculators.ui.HealthConditionDialog
import com.techglock.health.app.tools_calculators.viewmodel.ToolsCalculatorsViewModel
import com.techglock.health.app.tools_calculators.views.SystolicDiastolicDialogManager
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class HeartAgeFragment : BaseFragment(), ParameterAdapter.ParameterOnClickListener,
    SystolicDiastolicDialogManager.OnDialogValueListener, HeightWeightDialog.OnDialogValueListener,
    HealthConditionDialog.OnHealthConditionValueListener {

    private lateinit var binding: FragmentHeartAgeBinding
    private val viewModel: ToolsCalculatorsViewModel by lazy {
        ViewModelProvider(this)[ToolsCalculatorsViewModel::class.java]
    }

    private var calculatorDataSingleton: CalculatorDataSingleton? = null
    private var selectionList = ArrayList<String>()

    private var quizID = ""
    private var participationID = ""
    private var genderAdapter: SpinnerAdapter? = null
    private var modelAdapter: SpinnerAdapter? = null
    private var parameterAdapter: ParameterAdapter? = null
    private var genderList = ArrayList<SpinnerModel>()
    private var modelList = ArrayList<SpinnerModel>()
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
        binding = FragmentHeartAgeBinding.inflate(inflater, container, false)
        try {
            calculatorDataSingleton = CalculatorDataSingleton.getInstance()!!
            quizID = calculatorDataSingleton!!.quizId
            participationID = calculatorDataSingleton!!.participantID
            Utilities.printLog("QuizID,ParticipationID---> $quizID , $participationID")
            initialise()
            setClickable()
            loadUserData()
//            FirebaseHelper.logScreenEvent(FirebaseConstants.HEART_AGE_BMI_CALCULATOR_SCREEN)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return binding.root
    }

    private fun initialise() {
        CleverTapHelper.pushEvent(requireContext(), CleverTapConstants.HEART_AGE_CALCULATOR_SCREEN)
        userPreferenceModel = UserInfoModel.getInstance()!!
        selectionList = calculatorDataSingleton!!.healthConditionSelection

        modelList = viewModel.dataHandler.getModelList()
        modelAdapter = SpinnerAdapter(requireContext(), modelList)
        //binding.modelSpinner.adapter = modelAdapter

        genderList = viewModel.dataHandler.getGenderList()
        genderAdapter = SpinnerAdapter(requireContext(), genderList)
        binding.spinnerGender.adapter = genderAdapter

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
        CalculatorDataSingleton.getInstance()!!.heartAgeModel = "BMI"
        parameterAdapter = ParameterAdapter(paramList, this, requireContext())
        binding.paramRecyclerView.layoutManager = GridLayoutManager(context, 2)
        binding.paramRecyclerView.setExpanded(true)
        binding.paramRecyclerView.adapter = parameterAdapter

        var answer: Answer?
        answer = Answer("TOTAL_CHOL", "0", "0")
        answerArrayMap["TOTAL_CHOL"] = answer
        answer = Answer("HDL", "0", "0")
        answerArrayMap["HDL"] = answer
        answer = Answer("WEIGHT", "0", "0")
        answerArrayMap["WEIGHT"] = answer
        answer = Answer("HEIGHT", "0", "0")
        answerArrayMap["HEIGHT"] = answer

        viewModel.heartAgeSaveResp.observe(viewLifecycleOwner) {}

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

        binding.swDiabetic.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.txtCheckedDiabetic.text = resources.getString(R.string.YES)
                binding.txtCheckedDiabetic.tag = "Yes"
            } else {
                binding.txtCheckedDiabetic.text = resources.getString(R.string.NO)
                binding.txtCheckedDiabetic.tag = "No"
            }
        }

    }

    private fun loadUserData() {
        if (!Utilities.isNullOrEmpty(UserInfoModel.getInstance()!!.getAge())
            && !UserInfoModel.getInstance()!!.getAge().equals("0", ignoreCase = true)
        ) {
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

    private fun setClickable() {

        binding.spinnerGender.onItemSelectedListener = object : OnItemSelectedListener {

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                Utilities.printLog("Selected Item:: $position")
                binding.txtGender.text = genderList[position].name
                binding.txtGender.tag = genderList[position].code
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
                viewModel.callHeartAgeCalculateApi(
                    "First",
                    participationID,
                    quizID,
                    getAnswerList()
                )
            }
            /*            val bundle = Bundle()
                        bundle.putString(Constants.FROM, "Home")
                        it.findNavController().navigate(R.id.action_heartAgeFragment_to_heartSummaryFragment)*/
        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab) {
                updateData(tab.position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

    }

    private fun updateData(position: Int) {
        Utilities.printLog("Selected Item:: $position")
        if (position == 0) {
            modelList[0].selection = true
            modelList[1].selection = false
            paramList.clear()
            paramList.addAll(viewModel.dataHandler.getParameterList("BMI", this@HeartAgeFragment))
            //paramList = viewModel.dataHandler.getParameterList("BMI", this@HeartAgeFragment)
            parameterAdapter!!.updateList(paramList)
            CalculatorDataSingleton.getInstance()!!.heartAgeModel = "BMI"
        } else {
            modelList[0].selection = false
            modelList[1].selection = true
            paramList.clear()
            paramList.addAll(viewModel.dataHandler.getParameterList("LIPID", this@HeartAgeFragment))
            //paramList = viewModel.dataHandler.getParameterList("LIPID", this@HeartAgeFragment)
            parameterAdapter!!.updateList(paramList)
            CalculatorDataSingleton.getInstance()!!.heartAgeModel = "LIPID"
        }
    }

    private fun getAnswerList(): ArrayList<Answer> {
        val answers: ArrayList<Answer> = ArrayList()
        for ((_, value) in answerArrayMap) {
            answers.add(value)
        }
        return answers
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

                "TOTAL_CHOL" -> {
                    answer = Answer("TOTAL_CHOL", paramList[i].finalValue, paramList[i].value)
                    answerArrayMap["TOTAL_CHOL"] = answer
                }

                "HDL" -> {
                    answer = Answer("HDL", paramList[i].finalValue, paramList[i].value)
                    answerArrayMap["HDL"] = answer
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
        answer = Answer("DIABETIC", binding.txtCheckedDiabetic.tag.toString(), "0")
        answerArrayMap["DIABETIC"] = answer
        answer = Answer("SMOKING", binding.txtCheckedSmoke.tag.toString(), "0")
        answerArrayMap["SMOKING"] = answer
        answer = Answer("CHOL_LEVEL", "No", "0")
        answerArrayMap["CHOL_LEVEL"] = answer
        answer = Answer("AGE", binding.edtAge.text.toString(), "0")
        answerArrayMap["AGE"] = answer
        calculatorDataSingleton!!.personAge = binding.edtAge.text.toString()
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
                if (paramList[i].title.equals("Height", ignoreCase = true)) {
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
                } else if (paramList[i].title.equals("Weight", ignoreCase = true)) {
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

    @SuppressLint("SetTextI18n")
    private fun showInputDialog(param: ParameterDataModel, position: Int) {
        dialogBinding.titleInput.text = param.title
        dialogBinding.txtMessage.text =
            "${resources.getString(R.string.ENTER_YOUR)} " + param.title.toLowerCase(Locale.ROOT) + " ${
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
                        requireContext(),
                        "${resources.getString(R.string.PLEASE_INPUT_VALUE_BETWEEN)} "
                                + param.minRange + " ${resources.getString(R.string.TO)} " + param.maxRange
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
                this@HeartAgeFragment,
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
                this@HeartAgeFragment,
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
                this@HeartAgeFragment,
                parameterAdapter!!.paramList,
                position
            )
            customDialogManager.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            customDialogManager.show()
        } else {
            showInputDialog(parameterDataModel, position)
        }
    }

    fun hideKeyboardFrom(context: Context, view: View) {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onDialogValueListener(systolic: String, diastolic: String) {
        paramList = parameterAdapter!!.paramList
        Utilities.printLog("systolic,diastolic=>$systolic,$diastolic")
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
        Utilities.printLog("Selected :: ${calculatorDataSingleton!!.healthConditionSelection.size}")
    }

    override fun onResume() {
        super.onResume()
    }

}
