package com.techglock.health.app.hra.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import androidx.lifecycle.ViewModelProvider
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseFragment
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.CleverTapConstants
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.utils.CleverTapHelper
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.FragmentHraQuesEdsCheckupBinding
import com.techglock.health.app.hra.common.HraDataSingleton
import com.techglock.health.app.hra.common.HraHelper
import com.techglock.health.app.hra.common.Validations
import com.techglock.health.app.hra.viewmodel.HraViewModel
import com.techglock.health.app.model.hra.Option
import com.techglock.health.app.model.hra.Question
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HraQuesExposeCheckupFragment(val qCode: String) : BaseFragment() {

    private lateinit var binding: FragmentHraQuesEdsCheckupBinding
    private val viewModel: HraViewModel by lazy {
        ViewModelProvider(this)[HraViewModel::class.java]
    }

    private var questionData = Question()
    private var viewPagerActivity: HraQuestionsActivity? = null
    private var prevAnsList: MutableList<Option> = mutableListOf()
    private val selectedOptionList: MutableList<Option> = mutableListOf()
    private val hraDataSingleton = HraDataSingleton.getInstance()!!

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHraQuesEdsCheckupBinding.inflate(inflater, container, false)
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
        selectedOptionList.clear()
        if (qCode.equals("CHECKUP", ignoreCase = true)) {
            binding.btnNext.text = resources.getString(R.string.THATS_ALL)
        }
    }

    private fun setPreviousAnsData() {
        //binding.rgPrevious.removeAllViews()
        prevAnsList = hraDataSingleton.getPrevAnsList(viewPagerActivity!!.getCurrentScreen() - 1)
        if (prevAnsList.isEmpty()) {
            prevAnsList =
                hraDataSingleton.getPrevAnsList(viewPagerActivity!!.getCurrentScreen() - 2)
        }
        //Utilities.printLogError("prevAnsList---> $prevAnsList")
        viewModel.setPreviousAnswersList(prevAnsList)
    }

    private fun loadData() {
        var toProceed = true
        viewModel.quesData.observe(viewLifecycleOwner) {
            if (it != null) {


                binding.imgQues.setImageResource(it.bgImage)
                binding.txtQues.setHtmlTextFromId(it.question)



                if (toProceed) {
                    Utilities.printLog("OptionList-----> " + it.optionList)
                    questionData = it
                    hraDataSingleton.question = it
                    selectedOptionList.addAll(it.optionList)
                    HraHelper.addButtonsMultiSelection(
                        it.optionList,
                        binding.optionContainer,
                        checkChangeListener,
                        onClickListener,
                        requireContext()
                    )
                    loadPreviousData(it.optionList)
                    toProceed = false
                }
            }
        }

        viewModel.selectedOptionList.observe(viewLifecycleOwner) {
            try {
                Handler(Looper.getMainLooper()).postDelayed({
                    if (it != null) {
                        if (it.isNotEmpty()) {
                            //Utilities.printLogError("SelectedOptionList----->$list")
                            for (i in 0 until binding.optionContainer.childCount) {
                                val chk = binding.optionContainer.getChildAt(i) as CheckBox
                                for (option in it) {
                                    //if ( option.AnswerCode.equals(chk.tag.toString().trim(), ignoreCase = true)) {
                                    //Utilities.printLogError("chk.tag----->${chk.tag}")
                                    if (chk.tag.toString()
                                            .contains(option.AnswerCode, ignoreCase = true)
                                    ) {
                                        Utilities.printLogError("isMatched----->${chk.tag} : ${option.AnswerCode}")
                                        chk.isChecked = true
                                    }
                                }
                            }
                        }
                    }
                }, 500)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        viewModel.submitHra.observe(viewLifecycleOwner) {
            if (it != null) {
                val wellnessScore = it.WellnessScoreSummary.WellnessScore
                if (!Utilities.isNullOrEmptyOrZero(wellnessScore)) {
                    CleverTapHelper.pushEvent(requireContext(), CleverTapConstants.HRA_COMPLETE)
                    viewPagerActivity!!.setCurrentScreen(viewPagerActivity!!.getCurrentScreen() + 1)
                }
            }
        }
    }

    private fun loadPreviousData(optionList: ArrayList<Option>) {
        viewModel.getSelectedOptionListWithCode(qCode, optionList)
    }

    private fun setClickable() {

        binding.layoutPrevious.setOnClickListener {
            previousRadioBtnOptionClick()
        }

        binding.btnNext.setOnClickListener {
            if (Validations.validateMultiSelectionOptions(binding.optionContainer)) {
                nextButtonClick()
            } else {
                Utilities.toastMessageShort(
                    context,
                    resources.getString(R.string.PLEASE_SELECT_OPTION)
                )
            }
        }

    }

    private fun nextButtonClick() {
        val selectedOptions = selectedOptionList.filter { it.isSelected }.toMutableList()
        Utilities.printLog("Saved_Options-----> $selectedOptions")
        saveResponseForNextScreen(selectedOptions)

        when (qCode) {
            "EXPOSE" -> {
                saveResponse(selectedOptions)
                viewPagerActivity!!.setCurrentScreen(viewPagerActivity!!.getCurrentScreen() + 1)
            }

            "CHECKUP" -> {
                viewModel.showLoader()
                saveResponse(selectedOptions)
                Handler(Looper.getMainLooper()).postDelayed({
                    viewModel.saveAndSubmitHRA(
                        viewPagerActivity!!.personId,
                        viewPagerActivity!!.hraTemplateId
                    )
                }, 1000)
            }
        }
        hraDataSingleton.selectedOptionList.clear()
    }

    private fun previousRadioBtnOptionClick() {
        //if ( validate() ) {
        val selectedOptions = selectedOptionList.filter { it.isSelected }.toMutableList()
        Utilities.printLog("Saved_Options-----> $selectedOptions")
        saveResponseForNextScreen(selectedOptions)

        when (qCode) {
            "EXPOSE", "CHECKUP" -> {
                if (selectedOptions.isNullOrEmpty()) {
                    val totalList = questionData.optionList.filter {
                        it.answerCode.contains(
                            ",",
                            ignoreCase = true
                        )
                    }
                    //if (selectedOptions.any { it.description.equals(resources.getString(R.string.NONE), ignoreCase = true) }) {
                    if (selectedOptions.any {
                            it.answerCode.contains("DONT", ignoreCase = true)
                                    || it.answerCode.contains("NONE", ignoreCase = true)
                        }) {
                        for (option in totalList) {
                            val data = option.answerCode.split(",")
                            viewModel.saveResponseOther(
                                data[0],
                                data[2],
                                option.description,
                                questionData.category,
                                questionData.tabName,
                                "",
                                qCode,
                                Constants.FALSE
                            )
                        }
                    } else {
                        //val list = selectedOptionList.filter { !it.description.equals("None", ignoreCase = true) }.toMutableList()
                        val list = selectedOptionList.filter {
                            !it.answerCode.contains("DONT", ignoreCase = true)
                                    || !it.answerCode.contains("NONE", ignoreCase = true)
                        }.toMutableList()
                        if (list.isNullOrEmpty()) {
                            for (option in list) {
                                val data = option.answerCode.split(",")
                                if (option.isSelected) {
                                    viewModel.saveResponseOther(
                                        data[0],
                                        data[1],
                                        option.description,
                                        questionData.category,
                                        questionData.tabName,
                                        "",
                                        qCode,
                                        Constants.TRUE
                                    )
                                } else {
                                    viewModel.saveResponseOther(
                                        data[0],
                                        data[2],
                                        option.description,
                                        questionData.category,
                                        questionData.tabName,
                                        "",
                                        qCode,
                                        Constants.FALSE
                                    )
                                }
                            }
                        }
                    }
                }

                if (prevAnsList.any { it.answerCode.equals("6_PHYSTRNO", ignoreCase = true) }) {
                    viewPagerActivity!!.setCurrentScreen(viewPagerActivity!!.getCurrentScreen() - 2)
                } else {
                    viewPagerActivity!!.setCurrentScreen(viewPagerActivity!!.getCurrentScreen() - 1)
                }

            }

        }
        /*            } else {
                        Utilities.toastMessageShort(context, "Please Select Option")
                    }*/
    }

    private fun saveResponse(selectedOptions: MutableList<Option>) {
        val totalList =
            questionData.optionList.filter { it.answerCode.contains(",", ignoreCase = true) }
        //if (selectedOptions.any { it.description.equals(resources.getString(R.string.NONE), ignoreCase = true) }) {
        if (selectedOptions.any {
                it.answerCode.contains("DONT", ignoreCase = true)
                        || it.answerCode.contains("NONE", ignoreCase = true)
            }) {
            for (option in totalList) {
                val data = option.answerCode.split(",")
                viewModel.saveResponseOther(
                    data[0],
                    data[2],
                    option.description,
                    questionData.category,
                    questionData.tabName,
                    "",
                    qCode,
                    Constants.FALSE
                )
            }
        } else {
            Utilities.printLog("selectedOptionList1-----> $selectedOptionList")
            //val list = selectedOptionList.filter { !it.description.equals("None", ignoreCase = true) }.toMutableList()
            val list =
                selectedOptionList.filter { !it.answerCode.equals("DONT", ignoreCase = true) }
                    .toMutableList()
            Utilities.printLog("selectedOptionList2-----> $list")
            for (option in list) {
                val data = option.answerCode.split(",")
                if (option.isSelected) {
                    viewModel.saveResponseOther(
                        data[0],
                        data[1],
                        option.description,
                        questionData.category,
                        questionData.tabName,
                        "",
                        qCode,
                        Constants.TRUE
                    )
                } else {
                    viewModel.saveResponseOther(
                        data[0],
                        data[2],
                        option.description,
                        questionData.category,
                        questionData.tabName,
                        "",
                        qCode,
                        Constants.FALSE
                    )
                }
            }
        }
    }

    private fun refreshSelectedOptionList() {
        hraDataSingleton.selectedOptionList.clear()
        hraDataSingleton.selectedOptionList.addAll(selectedOptionList)
    }

    private fun saveResponseForNextScreen(selectedOptions: MutableList<Option>) {
        if (selectedOptions.isNotEmpty()) {
            hraDataSingleton.previousAnsList[viewPagerActivity!!.getCurrentScreen()] =
                selectedOptions
        }
    }

    private var checkChangeListener = CompoundButton.OnCheckedChangeListener { view, isChecked ->
        selectedOptionList[view.id].isSelected = isChecked
        if (isChecked && !view.text.toString()
                .contains(resources.getString(R.string.NONE), ignoreCase = true)
        ) {
            //if ( isChecked && ( !view.tag.toString().equals("DONT", ignoreCase = true) || !view.tag.toString().equals("NONE", ignoreCase = true) ) ) {
            val none = binding.optionContainer.getChildAt(0) as CheckBox
            if (none.isChecked) {
                none.isChecked = false
            }
        }

        selectedOptionList.find {
            it.answerCode.contains("DONT", ignoreCase = true)
                    || it.answerCode.contains("NONE", ignoreCase = true)
        }!!.isSelected = false
        Utilities.printLogError("${view.text} : isChecked-----> $isChecked")
        refreshSelectedOptionList()
    }

    // This listener is only for "None" Option to Navigate directly on Next Screen
    private var onClickListener = View.OnClickListener {
        val view = it as CheckBox
        selectedOptionList[view.id].isSelected = view.isChecked
        refreshSelectedOptionList()
        //if (selectedOptionList.any { option -> option.isSelected && !option.description.equals("None", ignoreCase = true) }) {
        if (selectedOptionList.any { option ->
                option.isSelected && (!option.answerCode.contains("DONT", ignoreCase = true)
                        || !option.answerCode.contains("NONE", ignoreCase = true))
            }) {
            HraHelper.deselectExceptNone(binding.optionContainer)
        }
        selectedOptionList[0].isSelected = true
        if (!qCode.equals("CHECKUP", ignoreCase = true)) {
            nextButtonClick()
        }
    }

}