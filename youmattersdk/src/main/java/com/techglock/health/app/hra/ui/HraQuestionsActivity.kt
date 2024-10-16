package com.techglock.health.app.hra.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseActivity
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.utils.DefaultNotificationDialog
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.common.utils.showDialog
import com.techglock.health.app.databinding.ActivityHraQuestionsBinding
import com.techglock.health.app.home.ui.HomeMainActivity
import com.techglock.health.app.hra.adapter.HraQuestionsPagerAdapter
import com.techglock.health.app.hra.common.HraDataSingleton
import com.techglock.health.app.hra.viewmodel.HraViewModel
import com.techglock.health.app.model.hra.Option
import com.techglock.health.app.model.hra.Question
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HraQuestionsActivity : BaseActivity(), DefaultNotificationDialog.OnDialogValueListener {

    private lateinit var binding: ActivityHraQuestionsBinding
    private var viewPagerAdapter: HraQuestionsPagerAdapter? = null
    private val viewModel: HraViewModel by lazy {
        ViewModelProvider(this)[HraViewModel::class.java]
    }

    var personId = ""
    var personName = ""
    var hraTemplateId = ""
    private var isMale = false
    private var totalQuestions = 0
    private val hraDataSingleton = HraDataSingleton.getInstance()!!

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateEvent(savedInstanceState: Bundle?) {
        binding = ActivityHraQuestionsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        try {
            val i = intent
            if (i.hasExtra(Constants.PERSON_ID)) {
                personId = i.getStringExtra(Constants.PERSON_ID)!!
            }
            if (i.hasExtra(Constants.FIRST_NAME)) {
                personName = i.getStringExtra(Constants.FIRST_NAME)!!
            }
            if (i.hasExtra(Constants.HRA_TEMPLATE_ID)) {
                hraTemplateId = i.getStringExtra(Constants.HRA_TEMPLATE_ID)!!
            }
            Utilities.printLogError("PersonId,PersonName,TemplateId--->$personId,$personName,$hraTemplateId")
            initLayout()
            setupToolbar()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initLayout() {
        binding.txtProgress.text = "0%"
        isMale = viewModel.gender.equals("1", ignoreCase = true)
        totalQuestions = if (isMale) {
            26
        } else {
            27
        }
        viewPagerAdapter = HraQuestionsPagerAdapter(supportFragmentManager, isMale, totalQuestions)
        binding.viewPagerQuestions.adapter = viewPagerAdapter
        binding.viewPagerQuestions.setScrollDuration(600)

        binding.indicatorProgress.setOnTouchListener { _: View?, _: MotionEvent? -> true }

        binding.viewPagerQuestions.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageSelected(position: Int) {
                Utilities.printLogError("SelectedPagePos--->$position")
                updateProgress(position)

                if (position == (totalQuestions - 1)) {
                    binding.toolBarView.imgCancel.visibility = View.GONE
                } else {
                    binding.toolBarView.imgCancel.visibility = View.VISIBLE
                }

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageScrollStateChanged(state: Int) {}

        })
    }

    fun updateProgress(quesNo: Int) {
        val progress = (quesNo * 100) / (totalQuestions - 1)
        binding.indicatorProgress.progress = progress
        binding.txtProgress.text = "$progress%"
        Utilities.printLogError("Progress--->$progress")
    }

    fun setCurrentScreen(i: Int) {
        binding.viewPagerQuestions.setCurrentItem(i, true)
        //binding.viewPagerQuestions.currentItem = i
    }

    fun getCurrentScreen(): Int {
        return binding.viewPagerQuestions.currentItem
    }

    override fun onBackPressed() {
        val currentItem = binding.viewPagerQuestions.currentItem
        if (currentItem == 0) {
            viewModel.clearSavedQuestionsData()
            super.onBackPressed()
            //navigateToHomeScreen()
        } else {
            if (isMale) {
                when (binding.viewPagerQuestions.currentItem) {
                    // Condition for MultiSelection Fragments
                    2, 4, 6, 7, 8, 21, 23, 24 -> {
                        onMultiSelectionBackPressed(binding.viewPagerQuestions.currentItem)
                    }

                    25 -> {
                        navigateToHomeScreen()
                    }

                    else -> {
                        //viewPagerQuestions.setCurrentItem(currentItem - 1, true)
                        setCurrentScreen(currentItem - 1)
                    }
                }
            } else {
                when (binding.viewPagerQuestions.currentItem) {
                    // Condition for MultiSelection Fragments
                    2, 4, 6, 7, 8, 9, 22, 24, 25 -> {
                        onMultiSelectionBackPressed(binding.viewPagerQuestions.currentItem)
                    }

                    26 -> {
                        navigateToHomeScreen()
                    }

                    else -> {
                        //viewPagerQuestions.setCurrentItem(currentItem - 1, true)
                        setCurrentScreen(currentItem - 1)
                    }
                }
            }
        }
    }

    private fun onMultiSelectionBackPressed(currentScreen: Int) {
        Utilities.printLogError("*****MultipleSelectionFragment onBackPressed*****")
        val selectedOptions =
            hraDataSingleton.selectedOptionList.filter { it.isSelected }.toMutableList()
        val ques = hraDataSingleton.question
        Utilities.printLogError("qCode----->" + ques.qCode)
        Utilities.printLogError("Saved_Options-----> $selectedOptions")
        saveResponseForNextScreen(selectedOptions, currentScreen)

        //viewModel.saveNewMultipleOptionResponses(ques.qCode, selectedOptions, ques.category, ques.tabName, "")

        var prevList = hraDataSingleton.getPrevAnsList(currentScreen - 1)
        if (prevList.isEmpty()) {
            prevList = hraDataSingleton.getPrevAnsList(currentScreen - 2)
        }
        Utilities.printLogError("prevAnsList---> $prevList")

        when (ques.qCode) {

            "KNWDIANUM" -> {
                Utilities.printLog("Inside--->" + ques.qCode)
                //if (selectedOptions.any { it.description.equals(resources.getString(R.string.NONE), ignoreCase = true) }) {
                if (selectedOptions.any {
                        it.answerCode.contains("DONT", ignoreCase = true)
                                || it.answerCode.contains("NONE", ignoreCase = true)
                    }) {
                    viewModel.saveResponse(
                        "KNWDIANUM",
                        "85_NO",
                        "No",
                        ques.category,
                        ques.tabName,
                        ""
                    )
                    viewModel.clearHRALabValuesBasedOnType("SUGAR")
                }
                setCurrentScreen(currentScreen - 1)
            }

            "KNWLIPNUM" -> {
                Utilities.printLog("Inside--->" + ques.qCode)
                //if (selectedOptions.any { it.description.equals(resources.getString(R.string.NONE), ignoreCase = true) }) {
                if (selectedOptions.any {
                        it.answerCode.contains("DONT", ignoreCase = true)
                                || it.answerCode.contains("NONE", ignoreCase = true)
                    }) {
                    viewModel.saveResponse(
                        "KNWLIPNUM",
                        "84_NO",
                        "No",
                        ques.category,
                        ques.tabName,
                        ""
                    )
                    viewModel.clearHRALabValuesBasedOnType("LIPID")
                }
                //if (prevList.any { it.description.equals(resources.getString(R.string.NONE), ignoreCase = true) }) {
                if (prevList.any {
                        it.answerCode.contains("DONT", ignoreCase = true)
                                || it.answerCode.contains("NONE", ignoreCase = true)
                    }) {
                    viewModel.saveResponse(
                        "KNWLIPNUM",
                        "84_NO",
                        "No",
                        ques.category,
                        ques.tabName,
                        ""
                    )
                    setCurrentScreen(currentScreen - 2)
                } else {
                    setCurrentScreen(currentScreen - 1)
                }
            }

            "HHILL" -> {
                Utilities.printLog("Inside--->" + ques.qCode)
                saveMultipleResponseInDb(selectedOptions, ques)
                //if (prevList.any { it.description.equals(resources.getString(R.string.NONE), ignoreCase = true) }) {
                if (prevList.any {
                        it.answerCode.contains("DONT", ignoreCase = true)
                                || it.answerCode.contains("NONE", ignoreCase = true)
                    }) {
                    setCurrentScreen(currentScreen - 2)
                } else {
                    setCurrentScreen(currentScreen - 1)
                }
            }

            "EDS" -> {
                Utilities.printLog("Inside--->" + ques.qCode)
                viewModel.saveResponseEDS(ques.qCode, ques.tabName, ques.category, selectedOptions)
                setCurrentScreen(currentScreen - 1)
            }

            "EXPOSE", "CHECKUP" -> {
                Utilities.printLog("Inside--->" + ques.qCode)
                val totalList =
                    ques.optionList.filter { it.answerCode.contains(",", ignoreCase = true) }
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
                            ques.category,
                            ques.tabName,
                            "",
                            ques.qCode,
                            Constants.FALSE
                        )
                    }
                } else {
                    val list = hraDataSingleton.selectedOptionList.filter {
                        !it.answerCode.equals(
                            "DONT",
                            ignoreCase = true
                        )
                    }.toMutableList()
                    for (option in list) {
                        val data = option.answerCode.split(",")
                        if (option.isSelected) {
                            viewModel.saveResponseOther(
                                data[0],
                                data[1],
                                option.description,
                                ques.category,
                                ques.tabName,
                                "",
                                ques.qCode,
                                Constants.TRUE
                            )
                        } else {
                            viewModel.saveResponseOther(
                                data[0],
                                data[2],
                                option.description,
                                ques.category,
                                ques.tabName,
                                "",
                                ques.qCode,
                                Constants.FALSE
                            )
                        }
                    }
                }
                //setCurrentScreen(currentScreen - 1)
                if (prevList.any { it.answerCode.equals("6_PHYSTRNO", ignoreCase = true) }) {
                    setCurrentScreen(currentScreen - 2)
                } else {
                    setCurrentScreen(currentScreen - 1)
                }
            }

            else -> {
                Utilities.printLogError("Inside--->" + ques.qCode)
                saveMultipleResponseInDb(selectedOptions, ques)
                setCurrentScreen(currentScreen - 1)
            }
        }
        hraDataSingleton.selectedOptionList.clear()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolBarView.toolBarHra)
        binding.toolBarView.toolbarTitle.text = resources.getString(R.string.TITLE_HRA)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        //supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        //supportActionBar!!.setDisplayShowHomeEnabled(true)

        //toolBar_hra.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
        binding.toolBarView.imgCancel.setOnClickListener {
            showExitAssessmentDialog()
        }

        //supportActionBar?.setHomeAsUpIndicator(R.drawable.back_arrow)
        //binding.toolBarView.toolBarHra.navigationIcon?.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(appColorHelper.textColor, BlendModeCompat.SRC_ATOP)

        /*        binding.toolBarView.toolBarHra.setNavigationOnClickListener {
                    viewModel.clearSavedQuestionsData()
                    navigateToHomeScreen()
                }*/

    }

    fun showExitAssessmentDialog() {
        showDialog(
            listener = this,
            title = resources.getString(R.string.EXIT_ASSESSMENT),
            message = resources.getString(R.string.EXIT_ASSESSMENT_DESC),
            leftText = resources.getString(R.string.CANCEL),
            rightText = resources.getString(R.string.EXIT)
        )
    }

    override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) {
        if (isButtonRight) {
            viewModel.clearSavedQuestionsData()
            navigateToHomeScreen()
        }
    }

    private fun saveResponseForNextScreen(
        selectedOptions: MutableList<Option>,
        currentScreen: Int
    ) {
        if (selectedOptions.isNotEmpty()) {
            hraDataSingleton.previousAnsList[currentScreen] = selectedOptions
        }
    }

    private fun saveMultipleResponseInDb(selectedOptions: MutableList<Option>, ques: Question) {
        viewModel.saveMultipleOptionResponses(
            ques.qCode,
            selectedOptions,
            ques.category,
            ques.tabName,
            ""
        )
    }

    fun navigateToHomeScreen() {
        val intent = Intent(this,HomeMainActivity::class.java)
        startActivity(intent)
        //finish()
//        openAnotherActivity(destination = NavigationConstants.HOME, clearTop = true)
    }

    /*    override fun onBackPressed() {
            val currentItem = viewPagerQuestions.currentItem
            if (currentItem != 0) {
                viewPagerQuestions.setCurrentItem(viewPagerQuestions.currentItem - 1, true)
            } else {
                finish()
                viewModel.clearHraQuestionsTable()
            }
        }*/

}