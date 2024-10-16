package com.techglock.health.app.tools_calculators.ui.DiabetesCalculator

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.ArrayMap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
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
import com.techglock.health.app.common.utils.CleverTapHelper
import com.techglock.health.app.common.utils.ParameterDataModel
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.common.view.SpinnerAdapter
import com.techglock.health.app.common.view.SpinnerModel
import com.techglock.health.app.databinding.FragmentDiabetesCalculatorInputBinding
import com.techglock.health.app.model.toolscalculators.UserInfoModel
import com.techglock.health.app.tools_calculators.adapter.ParameterAdapter
import com.techglock.health.app.tools_calculators.model.Answer
import com.techglock.health.app.tools_calculators.model.CalculatorDataSingleton
import com.techglock.health.app.tools_calculators.model.DiabetesCalculatorData
import com.techglock.health.app.tools_calculators.viewmodel.ToolsCalculatorsViewModel
import com.techglock.health.app.tools_calculators.views.WaistDialogManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DiabetesCalculatorInputFragment : BaseFragment(),
    ParameterAdapter.ParameterOnClickListener,
    WaistDialogManager.OnDialogValueListener {

    private lateinit var binding: FragmentDiabetesCalculatorInputBinding
    private val viewModel: ToolsCalculatorsViewModel by lazy {
        ViewModelProvider(this)[ToolsCalculatorsViewModel::class.java]
    }

    private var calculatorDataSingleton: CalculatorDataSingleton? = null

    private var quizID = ""
    private var participationID = ""
    private var genderAdapter: SpinnerAdapter? = null
    private var modelAdapter: SpinnerAdapter? = null
    private var parameterAdapter: ParameterAdapter? = null
    private var genderList = ArrayList<SpinnerModel>()
    private var ageGroupList = ArrayList<SpinnerModel>()
    private var paramList: MutableList<ParameterDataModel> = mutableListOf()
    private var answerArrayMap = ArrayMap<String, Answer>()
    private var userPreferenceModel = UserInfoModel()

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // callback to Handle back button event
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                calculatorDataSingleton!!.clearData()
                if (requireActivity().intent.hasExtra(Constants.TO)) {
                    val extra = requireActivity().intent.getStringExtra(Constants.TO)
                    Utilities.printLog("CalculatorTO=> " + extra)
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
        binding = FragmentDiabetesCalculatorInputBinding.inflate(inflater, container, false)
        try {
            calculatorDataSingleton = CalculatorDataSingleton.getInstance()!!
            quizID = calculatorDataSingleton!!.quizId
            participationID = calculatorDataSingleton!!.participantID
            Utilities.printLogError("QuizID,ParticipationID---> $quizID , $participationID")
            initialise()
            setClickable()
            loadUserData()
//            FirebaseHelper.logScreenEvent(FirebaseConstants.DIABETES_CALCULATOR_SCREEN)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return binding.root
    }

    private fun initialise() {
        CleverTapHelper.pushEvent(requireContext(), CleverTapConstants.DIABETES_CALCULATOR_SCREEN)
        userPreferenceModel = UserInfoModel.getInstance()!!

        ageGroupList = viewModel.dataHandler.getAgeGroupList()
        modelAdapter = SpinnerAdapter(requireContext(), ageGroupList)
        binding.spAgeGroup.adapter = modelAdapter

        genderList = viewModel.dataHandler.getGenderList()
        genderAdapter = SpinnerAdapter(requireContext(), genderList)
        binding.spinnerGender.adapter = genderAdapter

        paramList = viewModel.dataHandler.getDiabetesParameterList()
        parameterAdapter = ParameterAdapter(paramList, this, requireContext())
        binding.paramRecyclerView.layoutManager = GridLayoutManager(context, 2)
        binding.paramRecyclerView.adapter = parameterAdapter

        binding.txtCheckedOrigin.tag = "No"
        binding.txtCheckedDiabetes.tag = "No"
        binding.txtCheckedBloodSugar.tag = "No"
        binding.txtCheckedBpMedication.tag = "No"
        binding.txtCheckedSmoke.tag = "No"
        binding.txtCheckedFruits.tag = "No"
        binding.txtCheckedExercise.tag = "No"

//        binding.spAgeGroup.tag = "AGEGROUP"
//        binding.spinnerGender.tag = "GENDER"

//        binding.txtCheckedOrigin.tag = "ORIGIN"
//        binding.txtCheckedDiabetes.tag = "FAMILYHEALTH"
//        binding.txtCheckedBloodSugar.tag = "BLOODGLUCOSE"
//        binding.txtCheckedBpMedication.tag = "HIGHBLOODPRESSUREMEDICATION"
//        binding.txtCheckedSmoke.tag = "SMOKINGHABITS"
//        binding.txtCheckedFruits.tag = "FRUITHABITS"
//        binding.txtCheckedExercise.tag = "PHYSICALEXERCISE"

        viewModel.diabetesSaveResp.observe(viewLifecycleOwner) {}

        binding.swOrigin.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.txtCheckedOrigin.text = resources.getString(R.string.YES)
                binding.txtCheckedOrigin.tag = "Yes"
            } else {
                binding.txtCheckedOrigin.text = resources.getString(R.string.NO)
                binding.txtCheckedOrigin.tag = "No"
            }
        }

        binding.swDiabetes.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.txtCheckedDiabetes.text = resources.getString(R.string.YES)
                binding.txtCheckedDiabetes.tag = "Yes"
            } else {
                binding.txtCheckedDiabetes.text = resources.getString(R.string.NO)
                binding.txtCheckedDiabetes.tag = "No"
            }
        }

        binding.swBloodSugar.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.txtCheckedBloodSugar.text = resources.getString(R.string.YES)
                binding.txtCheckedBloodSugar.tag = "Yes"
            } else {
                binding.txtCheckedBloodSugar.text = resources.getString(R.string.NO)
                binding.txtCheckedBloodSugar.tag = "No"
            }
        }

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

        binding.swFruits.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.txtCheckedFruits.text = resources.getString(R.string.YES)
                binding.txtCheckedFruits.tag = "Yes"
            } else {
                binding.txtCheckedFruits.text = resources.getString(R.string.NO)
                binding.txtCheckedFruits.tag = "No"
            }
        }

        binding.swExercise.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.txtCheckedExercise.text = resources.getString(R.string.YES)
                binding.txtCheckedExercise.tag = "Yes"
            } else {
                binding.txtCheckedExercise.text = resources.getString(R.string.NO)
                binding.txtCheckedExercise.tag = "No"
            }
        }
    }

    private fun loadUserData() {
        if (!Utilities.isNullOrEmpty(UserInfoModel.getInstance()!!.getAge())) {
            val age = UserInfoModel.getInstance()!!.getAge().toDouble()
            when {
                age < 35 -> {
                    binding.spAgeGroup.setSelection(0)
                }

                age < 45 -> {
                    binding.spAgeGroup.setSelection(1)
                }

                age < 55 -> {
                    binding.spAgeGroup.setSelection(2)
                }

                age < 65 -> {
                    binding.spAgeGroup.setSelection(3)
                }

                else -> {
                    binding.spAgeGroup.setSelection(4)
                }
            }
        }
        if (UserInfoModel.getInstance()!!.isMale) {
            binding.spinnerGender.setSelection(0)
        } else {
            binding.spinnerGender.setSelection(1)
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

        binding.spAgeGroup.onItemSelectedListener = object : OnItemSelectedListener {

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                Utilities.printLog("Selected Item:: $position")
                binding.txtAgeGroup.text = ageGroupList[position].name
                for (i in ageGroupList.indices) {
                    ageGroupList[i].selection = i == position
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.txtGender.setOnClickListener {
            binding.spinnerGender.performClick()
        }

        binding.txtAgeGroup.setOnClickListener {
            binding.spAgeGroup.performClick()
        }

        binding.btnCalculateDiabetes.setOnClickListener {
            if (validate()) {
                prepareAnswerArray()
                viewModel.callDiabetesSaveResponseApi(participationID, quizID, getAnswerList())
            }
        }

    }

    private fun getAnswerList(): ArrayList<Answer> {
        val answers: ArrayList<Answer> = ArrayList()
        for ((_, value) in answerArrayMap) {
            answers.add(value)
        }
        return answers
    }

    private fun validate(): Boolean {
        var isValid = false
        try {
            if (paramList[0].finalValue.isNotEmpty()) {
                val `val`: Double = paramList[0].finalValue.toDouble() / 2.54
                if (`val` >= paramList[0].minRange && `val` <= paramList[0].maxRange) {
                    isValid = true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (!isValid) {
            Utilities.toastMessageShort(
                context, resources.getString(R.string.PLEASE_ENTER_WAIST_SIZE_BETWEEN)
                        + " " + paramList[0].minRange + " " + resources.getString(R.string.TO) + " " + paramList[0].maxRange + resources.getString(
                    R.string.INCH
                )
            )

        }
        return isValid
    }

    private fun prepareAnswerArray() {
        try {
            var answer: Answer
            val dibData = DiabetesCalculatorData()

            var option =
                dibData.getDiabetesCodeData("AGEGROUP")[binding.spAgeGroup.selectedItemPosition]
            answer = Answer("AGEGROUP", option.code, option.score)
            answerArrayMap[answer.questionCode] = answer

            option =
                dibData.getDiabetesCodeData("GENDER")[binding.spinnerGender.selectedItemPosition]
            answer = Answer("GENDER", option.code, option.score)
            answerArrayMap[answer.questionCode] = answer

            var position = if (binding.txtCheckedOrigin.tag.toString()
                    .equals("Yes", ignoreCase = true)
            ) 0 else 1
            option = dibData.getDiabetesCodeData("ORIGIN")[position]
            answer = Answer("ORIGIN", option.code, option.score)
            answerArrayMap[answer.questionCode] = answer

            position = if (binding.txtCheckedDiabetes.tag.toString()
                    .equals("Yes", ignoreCase = true)
            ) 0 else 1
            option = dibData.getDiabetesCodeData("FAMILYHEALTH")[position]
            answer = Answer("FAMILYHEALTH", option.code, option.score)
            answerArrayMap[answer.questionCode] = answer

            position = if (binding.txtCheckedBloodSugar.tag.toString()
                    .equals("Yes", ignoreCase = true)
            ) 0 else 1
            option = dibData.getDiabetesCodeData("BLOODGLUCOSE")[position]
            answer = Answer("BLOODGLUCOSE", option.code, option.score)
            answerArrayMap[answer.questionCode] = answer

            position = if (binding.txtCheckedBpMedication.tag.toString()
                    .equals("Yes", ignoreCase = true)
            ) 0 else 1
            option = dibData.getDiabetesCodeData("HIGHBLOODPRESSUREMEDICATION")[position]
            answer = Answer("HIGHBLOODPRESSUREMEDICATION", option.code, option.score)
            answerArrayMap[answer.questionCode] = answer

            position = if (binding.txtCheckedSmoke.tag.toString()
                    .equals("Yes", ignoreCase = true)
            ) 0 else 1
            option = dibData.getDiabetesCodeData("SMOKINGHABITS")[position]
            answer = Answer("SMOKINGHABITS", option.code, option.score)
            answerArrayMap[answer.questionCode] = answer

            position = if (binding.txtCheckedFruits.tag.toString()
                    .equals("Yes", ignoreCase = true)
            ) 0 else 1
            option = dibData.getDiabetesCodeData("FRUITHABITS")[position]
            answer = Answer("FRUITHABITS", option.code, option.score)
            answerArrayMap[answer.questionCode] = answer

            position = if (binding.txtCheckedExercise.tag.toString()
                    .equals("Yes", ignoreCase = true)
            ) 0 else 1
            option = dibData.getDiabetesCodeData("PHYSICALEXERCISE")[position]
            answer = Answer("PHYSICALEXERCISE", option.code, option.score)
            answerArrayMap[answer.questionCode] = answer

            answer = Answer(
                "WAISTMEASUREMENT",
                paramList[0].finalValue,
                "" + calculateWaistScore(paramList[0].finalValue.toDouble())
            )
            answerArrayMap["WAISTMEASUREMENT"] = answer
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun calculateWaistScore(waist: Double): Int {
        val waistValue = waist // * 2.54
        if (answerArrayMap["GENDER"]!!.answerCode.equals("Male", ignoreCase = true)) {
            if (answerArrayMap["ORIGIN"]!!.answerCode.equals(
                    "rdbAsianOriginYes",
                    ignoreCase = true
                )
            ) {
                if (waistValue in 90.0..100.0) {
                    return 4
                } else if (waistValue > 100) {
                    return 7
                }
            } else if (answerArrayMap["ORIGIN"]!!.answerCode.equals(
                    "rdbAsianOriginNo",
                    ignoreCase = true
                )
            ) {
                if (waistValue in 102.0..110.0) {
                    return 4
                } else if (waistValue > 110) {
                    return 7
                }
            }
        } else if (answerArrayMap["GENDER"]!!.answerCode.equals("Female", ignoreCase = true)) {
            if (answerArrayMap["ORIGIN"]!!.answerCode.equals(
                    "rdbAsianOriginYes",
                    ignoreCase = true
                )
            ) {
                if (waistValue in 80.0..90.0) {
                    return 4
                } else if (waistValue > 90) {
                    return 7
                }
            } else if (answerArrayMap["ORIGIN"]!!.answerCode.equals(
                    "rdbAsianOriginNo",
                    ignoreCase = true
                )
            ) {
                if (waistValue in 88.0..100.0) {
                    return 4
                } else if (waistValue > 100) {
                    return 7
                }
            }
        }
        return 0
    }

    override fun onParameterClick(parameterDataModel: ParameterDataModel, position: Int) {
        val waistDialogManager =
            WaistDialogManager(requireContext(), this, parameterAdapter!!.paramList[0])
        waistDialogManager.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        waistDialogManager.show()
    }

    override fun onDialogValueListener(inch: String?, centimeter: String?, unit: String?) {
        paramList = parameterAdapter!!.paramList
        Utilities.printLog("InchValue=>$inch")
        if (unit.equals("Inch", ignoreCase = true)) {
            paramList[0].value = inch!!
            Utilities.printLogError("Value=>$inch")
        } else {
            paramList[0].value = centimeter!!
            Utilities.printLogError("Value=>$centimeter")
        }
        paramList[0].unit = unit!!
        paramList[0].finalValue = centimeter!!
        parameterAdapter!!.notifyDataSetChanged()
        //parameterAdapter!!.updateList(paramList)
    }

}
