package com.techglock.health.app.tools_calculators.ui.StressAndAnxietyCalculator

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.util.ArrayMap
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseFragment
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.CleverTapConstants
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.utils.CleverTapHelper
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.FragmentStressAndAnxietyInputBinding
import com.techglock.health.app.model.toolscalculators.UserInfoModel
import com.techglock.health.app.tools_calculators.common.DataHandler
import com.techglock.health.app.tools_calculators.model.Answer
import com.techglock.health.app.tools_calculators.model.CalculatorDataSingleton
import com.techglock.health.app.tools_calculators.model.Question
import com.techglock.health.app.tools_calculators.model.StressAndAnxietyData
import com.techglock.health.app.tools_calculators.viewmodel.ToolsCalculatorsViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class StressAndAnxietyInputFragment : BaseFragment() {

    private lateinit var binding: FragmentStressAndAnxietyInputBinding
    private val viewModel: ToolsCalculatorsViewModel by lazy {
        ViewModelProvider(this)[ToolsCalculatorsViewModel::class.java]
    }
    private var calculatorDataSingleton: CalculatorDataSingleton? = null

    private var quizID = ""
    private var participationID = ""
    private var currentQuestion = "FIRST"
    private var isPrevious = false
    private var question: Question? = null
    private var dataHandler: DataHandler? = null
    private val totalQues = 21
    private var quesNo = 0
    private var stressAndAnxietyData: StressAndAnxietyData? = null
    private var answerArrayMap: ArrayMap<String, Answer>? = ArrayMap()

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
        binding = FragmentStressAndAnxietyInputBinding.inflate(inflater, container, false)
        try {
            calculatorDataSingleton = CalculatorDataSingleton.getInstance()!!
            quizID = calculatorDataSingleton!!.quizId
            participationID = calculatorDataSingleton!!.participantID
            Utilities.printLog("QuizID,ParticipationID---> $quizID , $participationID")
            initialise()
            setClickable()
//            FirebaseHelper.logScreenEvent(FirebaseConstants.STRESS_CALCULATOR_SCREEN)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return binding.root
    }

    private fun initialise() {
        CleverTapHelper.pushEvent(
            requireContext(),
            CleverTapConstants.STRESS_AND_ANXIETY_CALCULATOR_SCREEN
        )
        binding.txtProgress.text = "0/$totalQues ${resources.getString(R.string.COMPLETED)}"
        dataHandler = DataHandler(requireContext())
        dataHandler!!.clearSequenceList()
        stressAndAnxietyData = StressAndAnxietyData()
        currentQuestion = dataHandler!!.getStressNextQuestion(currentQuestion)
        question = stressAndAnxietyData!!.getStressAssessmentData(currentQuestion, requireContext())

        binding.txtQuestion.text = question!!.question
        binding.btnBack.visibility = View.INVISIBLE
        binding.btnFinish.isEnabled = false

        viewModel.stressAndAnxietySaveResp.observe(viewLifecycleOwner) {}
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setClickable() {

        binding.rgOptions.setOnTouchListener { _: View?, _: MotionEvent? ->
            isPrevious = false
            true
        }
        binding.rbAlways.setOnTouchListener { _: View?, _: MotionEvent? ->
            isPrevious = false
            false
        }
        binding.rbNever.setOnTouchListener { _: View?, _: MotionEvent? ->
            isPrevious = false
            false
        }
        binding.rbOften.setOnTouchListener { _: View?, _: MotionEvent? ->
            isPrevious = false
            false
        }
        binding.rbSometimes.setOnTouchListener { _: View?, _: MotionEvent? ->
            isPrevious = false
            false
        }

        binding.rgOptions.setOnCheckedChangeListener { _: RadioGroup?, _: Int ->
            if (!isPrevious) {
                //if (!binding.btnNext.text.toString().trim { it <= ' ' }.equals("finish", ignoreCase = true)) {
                if (question!!.isLast) {
                    binding.btnNext.text = resources.getString(R.string.THATS_ALL)
                    binding.btnNext.visibility = View.INVISIBLE
                    binding.btnFinish.isEnabled = true
                    updateNextProgress()
                } else {
                    val handler = Handler()
                    handler.postDelayed({ //Do something after 100ms
                        binding.btnNext.performClick()
                        isPrevious = false
                    }, 300)
                }
            }
        }

        binding.btnFinish.setOnClickListener {
            // Make Api call.
            saveDetails()
            Utilities.printLog("AnswerList=> ${getAnswerList()}")
            val answers = getAnswerList()
            if (!answers.isNullOrEmpty()) {
                viewModel.callStressAndAnxietySaveResponseApi(
                    participationID,
                    quizID.toInt(),
                    answers
                )
            }
        }

        binding.btnBack.setOnClickListener {
            currentQuestion = dataHandler!!.getStressPreviousQuestion(currentQuestion)
            updatePreviousProgress()
            binding.btnNext.text = resources.getString(R.string.NEXT)
            binding.btnNext.visibility = View.VISIBLE
            binding.btnFinish.isEnabled = false
            if (currentQuestion.equals("FIRST", ignoreCase = true)) {
                binding.btnBack.visibility = View.INVISIBLE
            } else {
                question = stressAndAnxietyData!!.getStressAssessmentData(
                    currentQuestion,
                    requireContext()
                )
                loadPreviousData()
                if (currentQuestion.equals("DASS-21_D_LIFEMEANINGLESS", ignoreCase = true)) {
                    binding.btnBack.visibility = View.INVISIBLE
                }
            }
        }

        binding.btnNext.setOnClickListener {
            if (validate()) {
                binding.btnBack.visibility = View.VISIBLE
                if (question!!.isLast) {
                    //binding.btnNext.text = resources.getString(R.string.THATS_ALL)
                    binding.btnNext.visibility = View.INVISIBLE
                    binding.btnFinish.isEnabled = true
                } else {
                    binding.btnNext.visibility = View.VISIBLE
                    binding.btnFinish.isEnabled = false
                    saveDetails()
                    currentQuestion = dataHandler!!.getStressNextQuestion(currentQuestion)
                    updateNextProgress()
                    question = stressAndAnxietyData!!.getStressAssessmentData(
                        currentQuestion,
                        requireContext()
                    )
                    loadPreviousData()
                }
            }
        }

    }

    @SuppressLint("SetTextI18n")
    private fun updatePreviousProgress() {
        if (quesNo > 0 && quesNo in 1..totalQues) {
            quesNo -= 1
        }
        binding.indicatorProgress.progress = quesNo
        binding.txtProgress.text = "$quesNo /$totalQues ${resources.getString(R.string.COMPLETED)}"
    }

    @SuppressLint("SetTextI18n")
    private fun updateNextProgress() {
        if (quesNo in 0 until totalQues) {
            quesNo += 1
        }
        binding.indicatorProgress.progress = quesNo
        binding.txtProgress.text = "$quesNo /$totalQues ${resources.getString(R.string.COMPLETED)}"
        if (quesNo == totalQues) {
            binding.btnNext.visibility = View.INVISIBLE
        }
    }

    private fun getAnswerList(): ArrayList<Answer> {
        val answers: ArrayList<Answer> = ArrayList()
        for ((_, value) in answerArrayMap!!) {
            answers.add(value)
        }
        //Utilities.printLog("AnswerList=>" +answers)
        return answers
    }

    private fun validate(): Boolean {
        val isValid = false
        if (binding.rgOptions.checkedRadioButtonId != -1) {
            return true
        } else {
            Utilities.toastMessageShort(context, resources.getString(R.string.PLEASE_FILL_DETAILS))
        }
        return isValid
    }

    private fun saveDetails() {
        // get selected radio button from radioGroup
        val selectedId: Int = binding.rgOptions.checkedRadioButtonId
        // find the radiobutton by returned id
        val radioButton = binding.rgOptions.findViewById(selectedId) as RadioButton?
        if (radioButton != null) {
            val answer = Answer()
            answer.questionCode = question!!.getqCode()
            answer.answerCode = radioButton.tag as String
            answer.value = getScore(radioButton.tag as String)
            //Utilities.printLog("UPDATE:: " + question!!.getqCode() + " :: " + answer)
            answerArrayMap!![question!!.getqCode()] = answer
        }
    }

    private fun getScore(tag: String): String {
        var score = "0"
        when (tag) {
            "NEVER" -> score = "0"
            "SOMETIMES" -> score = "1"
            "OFTEN" -> score = "2"
            "ALMOSTALWAYS" -> score = "3"
        }
        return score
    }

    private fun loadPreviousData() {
        isPrevious = true
        binding.txtQuestion.text = question!!.question
        if (answerArrayMap!!.containsKey(question!!.getqCode())) {
            isPrevious = true

            when (answerArrayMap!![question!!.getqCode()]!!.answerCode) {

                binding.rbNever.tag.toString() -> binding.rbNever.isChecked = true

                binding.rbOften.tag.toString() -> binding.rbOften.isChecked = true

                binding.rbSometimes.tag.toString() -> binding.rbSometimes.isChecked = true

                binding.rbAlways.tag.toString() -> binding.rbAlways.isChecked = true

                else -> binding.rgOptions.clearCheck()
            }

        } else {
            binding.rgOptions.clearCheck()
        }
    }

}
