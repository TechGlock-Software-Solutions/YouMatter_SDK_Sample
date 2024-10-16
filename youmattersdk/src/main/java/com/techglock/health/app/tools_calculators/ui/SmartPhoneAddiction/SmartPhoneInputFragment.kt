package com.techglock.health.app.tools_calculators.ui.SmartPhoneAddiction

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.util.ArrayMap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseFragment
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.CleverTapConstants
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.utils.CleverTapHelper
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.FragmentSmartPhoneInputBinding
import com.techglock.health.app.model.toolscalculators.UserInfoModel
import com.techglock.health.app.tools_calculators.common.DataHandler
import com.techglock.health.app.tools_calculators.model.Answer
import com.techglock.health.app.tools_calculators.model.CalculatorDataSingleton
import com.techglock.health.app.tools_calculators.model.Question
import com.techglock.health.app.tools_calculators.model.SmartPhoneAddictionData
import com.techglock.health.app.tools_calculators.viewmodel.ToolsCalculatorsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SmartPhoneInputFragment : BaseFragment() {

    private lateinit var binding: FragmentSmartPhoneInputBinding
    private val viewModel: ToolsCalculatorsViewModel by lazy {
        ViewModelProvider(this)[ToolsCalculatorsViewModel::class.java]
    }
    private var calculatorDataSingleton: CalculatorDataSingleton? = null

    private var quizID = ""
    private var participationID = ""
    private var score = "1"
    private var currentQuestion = "FIRST"
    private val totalQues = 11
    private var quesNo = 0
    private var answerArrayMap: ArrayMap<String, Answer>? = ArrayMap()
    private var question: Question? = null
    private var dataHandler: DataHandler? = null
    private var smartPhoneAddictionData: SmartPhoneAddictionData? = null

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utilities.printLog("QuizID,ParticipationID---> $quizID , $participationID")

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
        binding = FragmentSmartPhoneInputBinding.inflate(inflater, container, false)
        try {
            calculatorDataSingleton = CalculatorDataSingleton.getInstance()!!
            quizID = calculatorDataSingleton!!.quizId
            participationID = calculatorDataSingleton!!.participantID
            Utilities.printLog("QuizID,ParticipationID---> $quizID , $participationID")
            initialise()
            setClickable()
//            FirebaseHelper.logScreenEvent(FirebaseConstants.SMART_PHONE_CALCULATOR_SCREEN)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun initialise() {
        CleverTapHelper.pushEvent(
            requireContext(),
            CleverTapConstants.SMART_PHONE_ADDICTION_CALCULATOR_SCREEN
        )
        binding.txtProgress.text = "0/$totalQues ${resources.getString(R.string.COMPLETED)}"
        dataHandler = DataHandler(requireContext())
        smartPhoneAddictionData = SmartPhoneAddictionData()
        currentQuestion = dataHandler!!.getSmartPhoneAddictionNextQuestion(currentQuestion)
        question =
            smartPhoneAddictionData!!.getSmartPhoneAddictionData(currentQuestion, requireContext())

        binding.txtQuestion.text = question!!.question

        if (currentQuestion.equals("ADDIC11", ignoreCase = true)) {
            binding.btnBack.text = resources.getString(R.string.PREVIOUS)
            binding.btnBack.visibility = View.VISIBLE
            loadPreviousData()
        }
        binding.btnBack.visibility = View.INVISIBLE
        binding.btnFinish.isEnabled = false
        viewModel.smartPhoneSaveResp.observe(viewLifecycleOwner) {}
        setTag(-1, true)
    }

    private fun setClickable() {

        binding.layoutNever1.setOnClickListener {
            selectNever()
            autoNext()
        }

        binding.layoutSometimes1.setOnClickListener {
            selectSometime()
            autoNext()
        }

        binding.layoutOften.setOnClickListener {
            selectOften()
            autoNext()
        }

        binding.layoutAlways1.setOnClickListener {
            selectAlways()
            autoNext()
        }

        binding.btnFinish.setOnClickListener {
            // Make Api call.
            //val value = calculateScore()
            val answers = getAnswerList()
            if (!answers.isNullOrEmpty()) {
                viewModel.callSmartPhoneSaveResponseApi(participationID, quizID, answers)
            }
        }

        binding.btnBack.setOnClickListener {
            currentQuestion = dataHandler!!.getSmartPhoneAddictionPrevious(currentQuestion)
            updatePreviousProgress()
            binding.btnNext.text = resources.getString(R.string.NEXT)
            binding.btnNext.visibility = View.VISIBLE
            binding.btnFinish.isEnabled = false
            if (currentQuestion.equals("FIRST", ignoreCase = true)) {
                binding.btnBack.visibility = View.INVISIBLE
            } else {
                question = smartPhoneAddictionData!!.getSmartPhoneAddictionData(
                    currentQuestion,
                    requireContext()
                )
                loadPreviousData()
                if (currentQuestion.equals("ADDIC1", ignoreCase = true)) {
                    binding.btnBack.visibility = View.INVISIBLE
                }
            }
        }

        binding.btnNext.setOnClickListener {
            if (validateData()) {
                updateArrayMap()
                binding.btnBack.visibility = View.VISIBLE
                currentQuestion = dataHandler!!.getSmartPhoneAddictionNextQuestion(currentQuestion)

                if (question!!.isLast) {
                    //binding.btnNext.text = resources.getString(R.string.THATS_ALL)
                    binding.btnNext.visibility = View.INVISIBLE
                    binding.btnFinish.isEnabled = true
                } else {
                    binding.btnNext.visibility = View.VISIBLE
                    binding.btnFinish.isEnabled = false
                    updateNextProgress()
                    question = smartPhoneAddictionData!!.getSmartPhoneAddictionData(
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

    private fun setTag(id: Int, isDefault: Boolean) {
        if (isDefault) {
            binding.imgAlways.tag = "0"
            binding.imgSometime.tag = "0"
            binding.imgOften.tag = "0"
            binding.imgNever.tag = "0"
            return
        }

        when (id) {
            R.id.img_always -> {
                binding.imgAlways.tag = "1"
                binding.imgSometime.tag = "0"
                binding.imgOften.tag = "0"
                binding.imgNever.tag = "0"
            }

            R.id.img_sometime -> {
                binding.imgAlways.tag = "0"
                binding.imgSometime.tag = "1"
                binding.imgOften.tag = "0"
                binding.imgNever.tag = "0"
            }

            R.id.img_often -> {
                binding.imgAlways.tag = "0"
                binding.imgSometime.tag = "0"
                binding.imgOften.tag = "1"
                binding.imgNever.tag = "0"
            }

            R.id.img_never -> {
                binding.imgAlways.tag = "0"
                binding.imgSometime.tag = "0"
                binding.imgOften.tag = "0"
                binding.imgNever.tag = "1"
            }
        }

    }

    private fun selectNever() {
        setTag(binding.imgNever.id, false)
        score = "1"
        binding.layoutNever1.background =
            ResourcesCompat.getDrawable(resources, R.drawable.btn_fill_dark_green, null)
        binding.layoutSometimes1.background =
            ResourcesCompat.getDrawable(resources, R.drawable.btn_border_light_green, null)
        binding.layoutOften1.background =
            ResourcesCompat.getDrawable(resources, R.drawable.btn_border_orange, null)
        binding.layoutAlways1.background =
            ResourcesCompat.getDrawable(resources, R.drawable.btn_border_red, null)
    }

    private fun selectSometime() {
        setTag(binding.imgSometime.id, false)
        score = "3"
        binding.layoutNever1.background =
            ResourcesCompat.getDrawable(resources, R.drawable.btn_border_dark_green, null)
        binding.layoutSometimes1.background =
            ResourcesCompat.getDrawable(resources, R.drawable.btn_fill_light_green, null)
        binding.layoutOften1.background =
            ResourcesCompat.getDrawable(resources, R.drawable.btn_border_orange, null)
        binding.layoutAlways1.background =
            ResourcesCompat.getDrawable(resources, R.drawable.btn_border_red, null)
    }

    private fun selectOften() {
        setTag(binding.imgOften.id, false)
        score = "4"
        binding.layoutNever1.background =
            ResourcesCompat.getDrawable(resources, R.drawable.btn_border_dark_green, null)
        binding.layoutSometimes1.background =
            ResourcesCompat.getDrawable(resources, R.drawable.btn_border_light_green, null)
        binding.layoutOften1.background =
            ResourcesCompat.getDrawable(resources, R.drawable.btn_fill_orange, null)
        binding.layoutAlways1.background =
            ResourcesCompat.getDrawable(resources, R.drawable.btn_border_red, null)
    }

    private fun selectAlways() {
        setTag(binding.imgAlways.id, false)
        score = "5"
        binding.layoutNever1.background =
            ResourcesCompat.getDrawable(resources, R.drawable.btn_border_dark_green, null)
        binding.layoutSometimes1.background =
            ResourcesCompat.getDrawable(resources, R.drawable.btn_border_light_green, null)
        binding.layoutOften1.background =
            ResourcesCompat.getDrawable(resources, R.drawable.btn_border_orange, null)
        binding.layoutAlways1.background =
            ResourcesCompat.getDrawable(resources, R.drawable.btn_fill_red, null)
    }

    private fun autoNext() {
        if (currentQuestion.equals("ADDIC11", ignoreCase = true)) {
            binding.btnNext.text = resources.getString(R.string.THATS_ALL)
            binding.btnNext.visibility = View.INVISIBLE
            binding.btnFinish.isEnabled = true
            updateNextProgress()
        }
        val handler = Handler()
        handler.postDelayed({ //Do something after 100ms
            binding.btnNext.performClick()
        }, 300)
        /*        else {
                    val handler = Handler()
                    handler.postDelayed({ //Do something after 100ms
                        binding.btnNext.performClick()
                    }, 300)
                }*/
    }

    private fun validateData(): Boolean {
        return if (binding.imgAlways.tag.toString().equals("0", ignoreCase = true)
            && binding.imgNever.tag.toString().equals("0", ignoreCase = true)
            && binding.imgOften.tag.toString().equals("0", ignoreCase = true)
            && binding.imgSometime.tag.toString().equals("0", ignoreCase = true)
        ) {
            Utilities.toastMessageShort(context, resources.getString(R.string.PLEASE_SELECT_OPTION))
            false
        } else {
            true
        }
    }

    private fun loadPreviousData() {
        binding.txtQuestion.text = question!!.question
        if (answerArrayMap!!.containsKey(question!!.getqCode())) {
            when (answerArrayMap!![question!!.getqCode()]!!.value) {
                "1" -> selectNever()
                "3" -> selectSometime()
                "4" -> selectOften()
                "5" -> selectAlways()
            }
            if (question!!.isLast) {
                binding.btnFinish.isEnabled = true
            }
        } else {
            updateQuestion()
        }
    }

    private fun calculateScore(): String {
        var totalScore = 0
        try {
            for (key in answerArrayMap!!.keys) {
                totalScore += answerArrayMap!![key]!!.value.toInt()
                Utilities.printLog("DATA :: " + key + "/" + answerArrayMap!![key]!!.value)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Utilities.printLog("Total Score :: $totalScore")
        return totalScore.toString()
    }

    private fun getAnswerList(): ArrayList<Answer> {
        val answers: ArrayList<Answer> = ArrayList()
        for ((_, value) in answerArrayMap!!) {
            answers.add(value)
        }
        //Utilities.printLog("AnswerList=>" +answers)
        return answers
    }

    private fun updateArrayMap() {
        val answer = Answer()
        answer.questionCode = question!!.getqCode()
        answer.answerCode = question!!.getqCode()
        answer.value = score
        Utilities.printLog("UPDATE:: " + question!!.getqCode() + " :: " + score)
        answerArrayMap!![question!!.getqCode()] = answer
    }

    private fun updateQuestion() {
        binding.txtQuestion.text = question!!.question
        // Default Selection
        setTag(binding.txtQuestion.id, true)

        binding.layoutNever1.background =
            ResourcesCompat.getDrawable(resources, R.drawable.btn_border_dark_green, null)
        binding.layoutSometimes1.background =
            ResourcesCompat.getDrawable(resources, R.drawable.btn_border_light_green, null)
        binding.layoutOften1.background =
            ResourcesCompat.getDrawable(resources, R.drawable.btn_border_orange, null)
        binding.layoutAlways1.background =
            ResourcesCompat.getDrawable(resources, R.drawable.btn_border_red, null)
    }

}
