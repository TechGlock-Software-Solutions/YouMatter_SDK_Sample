package com.techglock.health.app.hra.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.lifecycle.ViewModelProvider
import com.techglock.health.app.common.base.BaseFragment
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.common.utils.ViewUtils
import com.techglock.health.app.databinding.FragmentHraQuesSingleSelectionBinding
import com.techglock.health.app.hra.common.HraDataSingleton
import com.techglock.health.app.hra.common.HraHelper
import com.techglock.health.app.hra.viewmodel.HraViewModel
import com.techglock.health.app.model.hra.Option
import com.techglock.health.app.model.hra.Question
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HraQuesSingleSelectionFragment(val qCode: String) : BaseFragment() {

    private lateinit var binding: FragmentHraQuesSingleSelectionBinding
    private val viewModel: HraViewModel by lazy {
        ViewModelProvider(this)[HraViewModel::class.java]
    }

    private var questionData = Question()
    private var viewPagerActivity: HraQuestionsActivity? = null
    private var prevAnsList: MutableList<Option> = mutableListOf()
    private val hraDataSingleton = HraDataSingleton.getInstance()!!
    private var optionList: ArrayList<Option> = arrayListOf()

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHraQuesSingleSelectionBinding.inflate(inflater, container, false)
        if (userVisibleHint) {
            try {
                initialise()
                setPreviousAnsData()
                loadData()
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
                loadData()
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


    }

    private fun setPreviousAnsData() {
        //binding.rgPrevious.removeAllViews()
        prevAnsList = hraDataSingleton.getPrevAnsList(viewPagerActivity!!.getCurrentScreen() - 1)
        Utilities.printLogError("prevAnsList---> $prevAnsList")
        if (prevAnsList.isNotEmpty()) {
            viewModel.setPreviousAnswersList(prevAnsList)
        }
    }

    private fun loadData() {

        var toProceed = true
        viewModel.quesData.observe(viewLifecycleOwner) { quesData ->
            if (quesData != null) {

                binding.imgQues.setImageResource(quesData.bgImage)
                binding.txtQues.setHtmlTextFromId(quesData.question)
                if (toProceed) {
                    Utilities.printLog("OptionList-----> " + quesData.optionList)
                    questionData = quesData
                    hraDataSingleton.question = quesData
                    optionList.clear()
                    optionList.addAll(quesData.optionList)
                    HraHelper.addRadioButtonsSingleSelection(
                        quesData.optionList,
                        binding.rgSelection,
                        listener,
                        requireContext()
                    )
                    loadPreviousSelectedData()
                    toProceed = false
                }
            }
        }

        viewModel.savedResponse.observe(viewLifecycleOwner) {
            try {
                Handler(Looper.getMainLooper()).postDelayed({
                    if (binding.rgSelection.childCount > 0 && it != null) {
                        Utilities.printLogError("SelectedTag----->$it")
                        ViewUtils.setRadioButtonCheckByTag(binding.rgSelection, it)
                        for (i in 0 until binding.rgSelection.childCount) {
                            val rb = binding.rgSelection.getChildAt(i) as RadioButton
                            if (rb.tag.toString() == it) {
                                Utilities.printLogError("Tag----->${rb.tag}")
                                rb.isChecked = true
                            }
                        }
                    }
                }, 500)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    private fun loadPreviousSelectedData() {
        viewModel.getResponse(qCode)
    }

    private fun setClickable() {

        binding.layoutPrevious.setOnClickListener {
            viewPagerActivity!!.setCurrentScreen(viewPagerActivity!!.getCurrentScreen() - 1)
        }

    }

    var listener = View.OnClickListener { view ->
/*        val rb = view as RadioButton
        if ( rb.isChecked ) {
            rb.setButtonDrawable(R.drawable.ic_circle_check)
        } else {
            rb.setButtonDrawable(null)
        }*/
        val ansCode = ViewUtils.getRadioSelectedValueTag(binding.rgSelection)
        val ansDesc = ViewUtils.getRadioButtonSelectedValue(binding.rgSelection)
        //val ansDescEng = resources.getString(optionList.find { it.answerCode == ansCode }!!.description)
        Utilities.printLogError("AnswerCode----->$ansCode")
        Utilities.printLogError("AnswerDesc----->$ansDesc")
        //Utilities.printLogError("AnsDescEng----->$ansDescEng")

        if (qCode == "SMOKECNT") {
            if (ansCode.contains("SMKCNT", ignoreCase = true)) {
                viewModel.saveResponse(
                    "HABIT",
                    "86_SMOKE",
                    "Yes",
                    questionData.category,
                    questionData.tabName,
                    ""
                )
                viewModel.saveResponse(
                    qCode,
                    ansCode,
                    ansDesc,
                    questionData.category,
                    questionData.tabName,
                    ""
                )
            } else {
                viewModel.saveResponse(
                    "HABIT",
                    "86_NONE",
                    "No",
                    questionData.category,
                    questionData.tabName,
                    ""
                )
            }
        } else {
            viewModel.saveResponse(
                qCode,
                ansCode,
                ansDesc,
                questionData.category,
                questionData.tabName,
                ""
            )
        }
        saveResponseForNextScreen(ansCode, ansDesc)

        when (qCode) {
            "PHYSTRES" -> {
                if (ansCode.equals("6_PHYSTRNO", ignoreCase = true)) {
                    viewPagerActivity!!.setCurrentScreen(viewPagerActivity!!.getCurrentScreen() + 2)
                } else {
                    viewPagerActivity!!.setCurrentScreen(viewPagerActivity!!.getCurrentScreen() + 1)
                }
            }

            else -> {
                viewPagerActivity!!.setCurrentScreen(viewPagerActivity!!.getCurrentScreen() + 1)
            }
        }
    }

    private fun saveResponseForNextScreen(ansCode: String, ansDesc: String) {
        val selectedOptionList: MutableList<Option> = mutableListOf()
        selectedOptionList.add(Option(ansDesc, ansCode, true))
        hraDataSingleton.previousAnsList[viewPagerActivity!!.getCurrentScreen()] =
            selectedOptionList
    }

}