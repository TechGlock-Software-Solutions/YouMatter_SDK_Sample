package com.techglock.health.app.home.ui.nimeya

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseFragment
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.CleverTapConstants
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.utils.CleverTapHelper
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.FragmentRiskoMeterInputBinding
import com.techglock.health.app.home.adapter.RiskoMeterOptionAdapter
import com.techglock.health.app.home.common.NimeyaSingleton
import com.techglock.health.app.home.viewmodel.NimeyaViewModel
import com.techglock.health.app.model.nimeya.GetRiskoMeterModel
import com.techglock.health.app.repository.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RiskoMeterInputFragment : BaseFragment(), RiskoMeterOptionAdapter.OnRiskoMeterAnswerListener {

    private val viewModel: NimeyaViewModel by lazy {
        ViewModelProvider(this)[NimeyaViewModel::class.java]
    }
    private lateinit var binding: FragmentRiskoMeterInputBinding

    private var riskoMeterOptionAdapter: RiskoMeterOptionAdapter? = null
    private var animation: LayoutAnimationController? = null
    private var nimeyaSingleton = NimeyaSingleton.getInstance()!!

    private var from = ""
    private var currentPos = 0
    private var riskoMeter = GetRiskoMeterModel.Data()
    private val riskoMeterList: MutableList<GetRiskoMeterModel.Data> = mutableListOf()
    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // callback to Handle back button event
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                nimeyaSingleton.clearData()
                requireActivity().finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRiskoMeterInputBinding.inflate(inflater, container, false)
        try {
            arguments?.let {
                if (it.containsKey(Constants.FROM)) {
                    from = it.getString(Constants.FROM)!!
                }
                Utilities.printLogError("from--->$from")
            }
            initialise()
            setClickable()
            registerObserver()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return binding.root
    }

    private fun initialise() {
        CleverTapHelper.pushEvent(requireContext(), CleverTapConstants.YM_NIMEYA_RISKO_METER)
        animation = AnimationUtils.loadLayoutAnimation(
            requireContext(),
            R.anim.layout_animation_slide_from_bottom
        )
        binding.layoutMain.visibility = View.GONE
        binding.btnPrevious.visibility = View.INVISIBLE
        binding.btnNext.visibility = View.GONE
        viewModel.callgetRiskoMeterApi()
    }

    private fun setClickable() {
        binding.btnPrevious.setOnClickListener {
            Utilities.printLogError("currentPos----->$currentPos")
            if (currentPos > 0) {
                currentPos--
                setDataAndProgress(currentPos)
                if (currentPos == 0) {
                    binding.btnPrevious.visibility = View.INVISIBLE
                } else {
                    binding.btnPrevious.visibility = View.VISIBLE
                }
                binding.btnNext.text = "Next"
            }
        }

        binding.btnNext.setOnClickListener {
            Utilities.printLogError("currentPos----->$currentPos")
            if (validate()) {
                binding.btnPrevious.visibility = View.VISIBLE
                if (currentPos == riskoMeterList.size - 2 || currentPos == riskoMeterList.size - 1) {
                    binding.btnNext.text = "Submit"
                } else {
                    binding.btnNext.text = "Next"
                }
                if (currentPos == riskoMeterList.size - 1) {
                    validateFinal()
                } else {
                    currentPos++
                    setDataAndProgress(currentPos)
                }
            } else {
                Utilities.toastMessageShort(requireContext(), "Please select option")
            }
        }
    }

    private fun setDataAndProgress(position: Int) {
        currentPos = position
        riskoMeter = riskoMeterList[position]
        Utilities.printLogError("currentPosChanged----->$currentPos")

        //binding.indicatorProgress.progress = position
        Utilities.setProgressWithAnimation(binding.indicatorProgress, position + 1, 700)
        binding.txtProgress.text = "Question ${position + 1}/${riskoMeterList.size}"
        binding.txtQuestion.text = "${position + 1}) " + riskoMeter.question
        riskoMeterOptionAdapter = RiskoMeterOptionAdapter(riskoMeter.id!!, requireContext(), this)
        binding.rvRiskoMeterOptions.layoutAnimation = animation
        binding.rvRiskoMeterOptions.setExpanded(true)
        binding.rvRiskoMeterOptions.adapter = riskoMeterOptionAdapter
        setPreviousAnswer()
    }

    private fun setPreviousAnswer() {
        var selectedTag = ""
        for (i in riskoMeterList[currentPos].answers) {
            if (i.isSelected) {
                selectedTag = i.id!!
            }
        }
        Utilities.printLogError("SelectedTag----->$selectedTag")
        val list = riskoMeter.answers
        if (!Utilities.isNullOrEmpty(selectedTag)) {
            for (i in list) {
                if (i.id == selectedTag) {
                    Utilities.printLogError("        Tag----->$selectedTag")
                    i.isSelected = true
                } else {
                    i.isSelected = false
                }
            }

        }
        riskoMeterOptionAdapter!!.updateList(list)
        binding.rvRiskoMeterOptions.scheduleLayoutAnimation()
    }

    private fun registerObserver() {
        viewModel.getRiskoMeter.observe(viewLifecycleOwner) {
            if (it.status == Resource.Status.SUCCESS) {
                val list = it.data!!.getRiskoMeter.data
                if (list.isNotEmpty()) {
                    riskoMeterList.clear()
                    riskoMeterList.addAll(list)
                    binding.indicatorProgress.max = list.size
                    Utilities.printLogError("currentPos----->0")
                    setDataAndProgress(0)
                    binding.btnNext.visibility = View.VISIBLE
                    binding.layoutMain.visibility = View.VISIBLE
                }
            }
        }
        viewModel.saveRiskoMeter.observe(viewLifecycleOwner) {}
    }

    private fun validate(): Boolean {
        var isSelected = false
        for (j in riskoMeter.answers) {
            if (j.isSelected) {
                isSelected = true
            }
        }
        return isSelected
    }

    private fun validateFinal() {
        val list = riskoMeterList
        val totalQues = list.size
        var selectedQues = 0
        for (i in 0 until totalQues) {
            for (j in list[i].answers) {
                if (j.isSelected) {
                    selectedQues++
                }
            }
        }
        if (totalQues == selectedQues) {
            //binding.indicatorProgress.progress = riskoMeterList.size
            //Utilities.setProgressWithAnimation(binding.indicatorProgress,riskoMeterList.size,700)
            //binding.txtProgress.text = "${riskoMeterList.size}/${riskoMeterList.size} Completed"
            viewModel.callSaveRiskoMeterApi(binding.btnNext, riskoMeterList)
        } else {
            Utilities.toastMessageShort(requireContext(), "Please answer all the questions")
        }
    }

    override fun onRiskoMeterAnswerClick(
        position: Int,
        quesCode: String,
        answer: GetRiskoMeterModel.Answer
    ) {
        Utilities.printLogError("QuestionCode----->$quesCode")
        val ansCode = answer.id
        Utilities.printLogError("AnswerCode----->$ansCode")
        Utilities.printLogError("AnswerDesc----->${answer.answer}")
        for (k in 0 until riskoMeterOptionAdapter!!.riskoMeterOptionList.size) {
            if (riskoMeterOptionAdapter!!.riskoMeterOptionList[k].id == ansCode) {
                riskoMeterOptionAdapter!!.riskoMeterOptionList[k].isSelected = true
            } else {
                riskoMeterOptionAdapter!!.riskoMeterOptionList[k].isSelected = false
            }
        }
        riskoMeterOptionAdapter!!.refresh()
        for (i in 0 until riskoMeterList.size) {
            if (riskoMeterList[i].id == quesCode) {
                for (j in riskoMeterList[i].answers) {
                    if (j.id == ansCode) {
                        j.isSelected = true
                    } else {
                        j.isSelected = false
                    }
                }
            }
        }
    }

}