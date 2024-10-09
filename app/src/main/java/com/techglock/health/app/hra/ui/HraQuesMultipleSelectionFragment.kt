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
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.FragmentHraQuesMultipleSelectionBinding
import com.techglock.health.app.home.views.HomeBinding.setImageView
import com.techglock.health.app.hra.common.HraDataSingleton
import com.techglock.health.app.hra.common.HraHelper
import com.techglock.health.app.hra.common.Validations
import com.techglock.health.app.hra.viewmodel.HraViewModel
import com.techglock.health.app.model.hra.Option
import com.techglock.health.app.model.hra.Question
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HraQuesMultipleSelectionFragment(val qCode: String) : BaseFragment() {

    private lateinit var binding: FragmentHraQuesMultipleSelectionBinding
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
        binding = FragmentHraQuesMultipleSelectionBinding.inflate(inflater, container, false)
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
        enableNextBtn(false)
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
                binding.imgQues.setImageView(it.bgImage)
                binding.txtQues.setHtmlTextFromId(it.question)
                if (toProceed) {
                    //Utilities.printLog("OptionList-----> " + it.optionList)
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
                    loadPreviousSelectedData()
                    toProceed = false
                }
            }
        }

        viewModel.selectedOptionList.observe(viewLifecycleOwner) {

            it?.let {
                try {
                    Handler(Looper.getMainLooper()).postDelayed({
                        if (it.isNotEmpty()) {
                            //Utilities.printLogError("SelectedOptionList----->$it")
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
                    }, 500)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun loadPreviousSelectedData() {
        when (qCode) {
            "KNWDIANUM", "KNWLIPNUM" -> {
                val list = hraDataSingleton.getPrevAnsList(viewPagerActivity!!.getCurrentScreen())
                Utilities.printData("pvlist", list, true)
                if (list.isNotEmpty()) {
                    viewModel.getSelectedOptionListForLabQues(list, qCode)
                }
            }

            "EDS" -> {
                viewModel.getEdsSelectedOptionList(qCode)
            }

            else -> viewModel.getSelectedOptionListWithQuestionCode(qCode)
        }
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
            //SUGAR
            "KNWDIANUM" -> {
                Utilities.printLog("Inside---> $qCode")
                //if (selectedOptions.any { it.description.equals(resources.getString(R.string.NONE), ignoreCase = true) }) {
                if (selectedOptions.isEmpty() || selectedOptions.any {
                        it.answerCode.contains("DONT", ignoreCase = true)
                                || it.answerCode.contains("NONE", ignoreCase = true)
                    }) {
                    viewModel.saveResponse(
                        qCode,
                        "85_NO",
                        "No",
                        questionData.category,
                        questionData.tabName,
                        ""
                    )
                    viewModel.clearHRALabValuesBasedOnType("SUGAR")
                    viewPagerActivity!!.setCurrentScreen(viewPagerActivity!!.getCurrentScreen() + 2)
                } else {
                    viewPagerActivity!!.setCurrentScreen(viewPagerActivity!!.getCurrentScreen() + 1)
                }
            }

            //LIPID
            "KNWLIPNUM" -> {
                Utilities.printLog("Inside---> $qCode")
                //if (selectedOptions.any { it.description.equals(resources.getString(R.string.NONE), ignoreCase = true) }) {
                if (selectedOptions.isEmpty() || selectedOptions.any {
                        it.answerCode.contains("DONT", ignoreCase = true)
                                || it.answerCode.contains("NONE", ignoreCase = true)
                    }) {
                    viewModel.saveResponse(
                        qCode,
                        "84_NO",
                        "No",
                        questionData.category,
                        questionData.tabName,
                        ""
                    )
                    viewModel.clearHRALabValuesBasedOnType("LIPID")
                    viewPagerActivity!!.setCurrentScreen(viewPagerActivity!!.getCurrentScreen() + 2)
                } else {
                    viewPagerActivity!!.setCurrentScreen(viewPagerActivity!!.getCurrentScreen() + 1)
                }
            }

            "EDS" -> {
                Utilities.printLog("Inside---> $qCode")
                viewModel.saveResponseEDS(
                    qCode,
                    questionData.tabName,
                    questionData.category,
                    selectedOptions
                )
                viewPagerActivity!!.setCurrentScreen(viewPagerActivity!!.getCurrentScreen() + 1)
            }

            else -> {
                Utilities.printLog("Inside---> $qCode")
                saveMultipleResponseInDb(selectedOptions)
                viewPagerActivity!!.setCurrentScreen(viewPagerActivity!!.getCurrentScreen() + 1)
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

            //SUGAR
            "KNWDIANUM" -> {
                if (selectedOptions.isNotEmpty()) {
                    //if ( selectedOptions.any { it.description.equals(resources.getString(R.string.NONE), ignoreCase = true) } ) {
                    if (selectedOptions.any {
                            it.answerCode.contains("DONT", ignoreCase = true)
                                    || it.answerCode.contains("NONE", ignoreCase = true)
                        }) {
                        viewModel.saveResponse(
                            qCode,
                            "85_NO",
                            "No",
                            questionData.category,
                            questionData.tabName,
                            ""
                        )
                        viewModel.clearHRALabValuesBasedOnType("SUGAR")
                    }
                }
                viewPagerActivity!!.setCurrentScreen(viewPagerActivity!!.getCurrentScreen() - 1)
            }

            //LIPID
            "KNWLIPNUM" -> {
                if (selectedOptions.isNotEmpty()) {
                    //if ( selectedOptions.any { it.description.equals(resources.getString(R.string.NONE), ignoreCase = true) } ) {
                    if (selectedOptions.any {
                            it.answerCode.contains("DONT", ignoreCase = true)
                                    || it.answerCode.contains("NONE", ignoreCase = true)
                        }) {
                        viewModel.saveResponse(
                            qCode,
                            "84_NO",
                            "No",
                            questionData.category,
                            questionData.tabName,
                            ""
                        )
                        viewModel.clearHRALabValuesBasedOnType("LIPID")
                    }
                }
                //if (prevAnsList.any { it.description.equals(resources.getString(R.string.NONE), ignoreCase = true) }) {
                if (prevAnsList.any {
                        it.answerCode.contains("DONT", ignoreCase = true)
                                || it.answerCode.contains("NONE", ignoreCase = true)
                    }) {
                    viewModel.saveResponse(
                        qCode,
                        "84_NO",
                        "No",
                        questionData.category,
                        questionData.tabName,
                        ""
                    )
                    viewPagerActivity!!.setCurrentScreen(viewPagerActivity!!.getCurrentScreen() - 2)
                } else {
                    viewPagerActivity!!.setCurrentScreen(viewPagerActivity!!.getCurrentScreen() - 1)
                }
            }

            "HHILL" -> {
                saveMultipleResponseInDb(selectedOptions)
                //if (prevAnsList.any { it.description.equals(resources.getString(R.string.NONE), ignoreCase = true) }) {
                if (prevAnsList.any {
                        it.answerCode.contains("DONT", ignoreCase = true)
                                || it.answerCode.contains("NONE", ignoreCase = true)
                    }) {
                    viewPagerActivity!!.setCurrentScreen(viewPagerActivity!!.getCurrentScreen() - 2)
                } else {
                    viewPagerActivity!!.setCurrentScreen(viewPagerActivity!!.getCurrentScreen() - 1)
                }
            }

            "EDS" -> {
                if (selectedOptions.isNotEmpty()) {
                    viewModel.saveResponseEDS(
                        qCode,
                        questionData.tabName,
                        questionData.category,
                        selectedOptions
                    )
                }
                viewPagerActivity!!.setCurrentScreen(viewPagerActivity!!.getCurrentScreen() - 1)
            }

            else -> {
                saveMultipleResponseInDb(selectedOptions)
                viewPagerActivity!!.setCurrentScreen(viewPagerActivity!!.getCurrentScreen() - 1)
            }

        }
    }

    private fun refreshSelectedOptionList() {
        hraDataSingleton.selectedOptionList.clear()
        hraDataSingleton.selectedOptionList.addAll(selectedOptionList)
        //Utilities.printData("selectedOptionList",hraDataSingleton.selectedOptionList,true)
    }

    private fun saveResponseForNextScreen(selectedOptions: MutableList<Option>) {
        if (selectedOptions.isNotEmpty()) {
            hraDataSingleton.previousAnsList[viewPagerActivity!!.getCurrentScreen()] =
                selectedOptions
        }
    }

    private fun saveMultipleResponseInDb(selectedOptions: MutableList<Option>) {
        if (selectedOptions.isNotEmpty()) {
            viewModel.saveMultipleOptionResponses(
                qCode,
                selectedOptions,
                questionData.category,
                questionData.tabName,
                ""
            )
        }
    }

    private fun enableNextBtn(enable: Boolean) {
        //binding.btnNext.isEnabled = enable
    }

    private var checkChangeListener = CompoundButton.OnCheckedChangeListener { view, isChecked ->
        selectedOptionList[view.id].isSelected = isChecked
        if (isChecked && !view.text.toString()
                .equals(resources.getString(R.string.NONE), ignoreCase = true)
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
        if (selectedOptionList.any { it.isSelected }) {
            enableNextBtn(true)
        } else {
            enableNextBtn(false)
        }
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
        nextButtonClick()
    }

}