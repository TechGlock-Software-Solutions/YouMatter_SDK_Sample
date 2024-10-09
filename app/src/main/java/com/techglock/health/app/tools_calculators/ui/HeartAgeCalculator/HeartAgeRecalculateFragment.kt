package com.techglock.health.app.tools_calculators.ui.HeartAgeCalculator

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.ArrayMap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseFragment
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.utils.HeightWeightDialog
import com.techglock.health.app.common.utils.ParameterDataModel
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.DialogInputParameterBinding
import com.techglock.health.app.databinding.FragmentHeartAgeRecalculateBinding
import com.techglock.health.app.model.toolscalculators.UserInfoModel
import com.techglock.health.app.tools_calculators.adapter.ParameterAdapter
import com.techglock.health.app.tools_calculators.model.Answer
import com.techglock.health.app.tools_calculators.model.CalculatorDataSingleton
import com.techglock.health.app.tools_calculators.viewmodel.ToolsCalculatorsViewModel
import com.techglock.health.app.tools_calculators.views.SystolicDiastolicDialogManager
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class HeartAgeRecalculateFragment : BaseFragment(),
    ParameterAdapter.ParameterOnClickListener,
    SystolicDiastolicDialogManager.OnDialogValueListener, HeightWeightDialog.OnDialogValueListener {

    private lateinit var binding: FragmentHeartAgeRecalculateBinding
    private val viewModel: ToolsCalculatorsViewModel by lazy {
        ViewModelProvider(this)[ToolsCalculatorsViewModel::class.java]
    }
    private var calculatorDataSingleton: CalculatorDataSingleton? = null

    private var quizID = ""
    private var participationID = ""
    private var parameterAdapter: ParameterAdapter? = null
    private var paramList: MutableList<ParameterDataModel> = mutableListOf()
    private var answerArrayMap = ArrayMap<String, Answer>()
    private var dialogInput: Dialog? = null
    private lateinit var dialogBinding: DialogInputParameterBinding

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHeartAgeRecalculateBinding.inflate(inflater, container, false)
        calculatorDataSingleton = CalculatorDataSingleton.getInstance()!!
        quizID = calculatorDataSingleton!!.quizId
        participationID = calculatorDataSingleton!!.participantID
        Utilities.printLogError("QuizID,ParticipationID---> $quizID , $participationID")
        initialise()
        setClickable()
        return binding.root
    }

    private fun initialise() {

        binding.txtHeartAgeValue.text = calculatorDataSingleton!!.heartAge
        binding.txtHeartRiskValue.text = calculatorDataSingleton!!.riskScorePercentage

        paramList =
            viewModel.dataHandler.getParameterList(calculatorDataSingleton!!.heartAgeModel, this)
        parameterAdapter = ParameterAdapter(paramList, this, requireContext())
        binding.paramRecyclerView.layoutManager = GridLayoutManager(context, 2)
        binding.paramRecyclerView.setExpanded(true)
        binding.paramRecyclerView.adapter = parameterAdapter

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

    private fun setClickable() {

        binding.btnCalculate.setOnClickListener {
            if (validateParameter()) {
                saveParameter()
                viewModel.callHeartAgeCalculateApi("", participationID, quizID, getAnswerList())
                /*                val bundle = Bundle()
                                bundle.putString(Constants.FROM, "Home")
                                it.findNavController().navigate(R.id.action_heartAgeRecalculateFragment_to_heartSummaryFragment)*/
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

        var answerCodes: String = "NONE"
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

        answer = if (UserInfoModel.getInstance()!!.isMale) {
            Answer("GENDER", "Male", "0")
        } else {
            Answer("GENDER", "Female", "0")
        }
        answerArrayMap["GENDER"] = answer
        answer = Answer("TRTHYPBP", binding.txtCheckedBpMedication.tag.toString(), "0")
        answerArrayMap["TRTHYPBP"] = answer
        answer = Answer("DIABETIC", binding.txtCheckedDiabetic.tag.toString(), "0")
        answerArrayMap["DIABETIC"] = answer
        answer = Answer("SMOKING", binding.txtCheckedSmoke.tag.toString(), "0")
        answerArrayMap["SMOKING"] = answer
        answer = Answer("CHOL_LEVEL", "No", "0")
        answerArrayMap["CHOL_LEVEL"] = answer

        if (!Utilities.isNullOrEmpty(UserInfoModel.getInstance()!!.getAge())
            && !UserInfoModel.getInstance()!!.getAge().equals("0", ignoreCase = true)
        ) {
            answer = Answer("AGE", UserInfoModel.getInstance()!!.getAge(), "0")
            answerArrayMap["AGE"] = answer
        }


    }

    private fun validateParameter(): Boolean {
        var isValid = false
        for (i in paramList.indices) {
            if (paramList[i].code.equals("HEIGHT", ignoreCase = true)) {
                if (!paramList[i].finalValue.equals("", ignoreCase = true)) {
                    isValid = true
                } else {
                    isValid = false
                    Utilities.toastMessageShort(
                        context,
                        "${resources.getString(R.string.PLEASE_FILL_HEIGHT_DETAILS)}."
                    )
                    break
                }
            } else if (paramList[i].code.equals("WEIGHT", ignoreCase = true)) {
                if (!paramList[i].finalValue.equals("", ignoreCase = true)) {
                    isValid = true
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
                        context, "${resources.getString(R.string.PLEASE_INPUT_VALUE_BETWEEN)} "
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

    override fun onParameterClick(parameterDataModel: ParameterDataModel, position: Int) {
        if (parameterDataModel.code.equals("HEIGHT", ignoreCase = true)) {
            if (viewModel.getPreference("HEIGHT") == "cm") {
                parameterAdapter!!.paramList[0].unit = resources.getString(R.string.CM)
            } else {
                parameterAdapter!!.paramList[0].unit = resources.getString(R.string.FEET_INCH)
            }
            val heightWeightDialog = HeightWeightDialog(
                requireContext(),
                this@HeartAgeRecalculateFragment,
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
                this@HeartAgeRecalculateFragment,
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
                this@HeartAgeRecalculateFragment,
                parameterAdapter!!.paramList,
                position
            )
            customDialogManager.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            customDialogManager.show()
        } else {
            showInputDialog(parameterDataModel, position)
        }
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
        //parameterAdapter!!.updateList(paramList)

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

}
