package com.techglock.health.app.hra.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseFragment
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.utils.CalculateParameters
import com.techglock.health.app.common.utils.HeightWeightDialog
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.FragmentHraQuesBmiBinding
import com.techglock.health.app.home.views.HomeBinding.setImageView
import com.techglock.health.app.hra.common.HRAConstants
import com.techglock.health.app.hra.common.HraDataSingleton
import com.techglock.health.app.hra.common.HraHelper
import com.techglock.health.app.hra.common.Observations
import com.techglock.health.app.hra.common.Validations
import com.techglock.health.app.hra.viewmodel.HraViewModel
import com.techglock.health.app.model.hra.Option
import com.techglock.health.app.model.hra.Question
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class HraQuesBmiFragment(val qCode: String) : BaseFragment(),
    HeightWeightDialog.OnDialogValueListener {

    private lateinit var binding: FragmentHraQuesBmiBinding
    private val viewModel: HraViewModel by lazy {
        ViewModelProvider(this)[HraViewModel::class.java]
    }

    private var toShow: Boolean = true
    private var bmi: Double = 0.0
    private var height: Double = 0.0

    //private var weight: Double = 50.0
    private var weight: Double = 0.0
    private var questionData = Question()
    private val hraDataSingleton = HraDataSingleton.getInstance()!!
    private var selectedOptionList: MutableList<Option> = mutableListOf()
    private var viewPagerActivity: HraQuestionsActivity? = null

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHraQuesBmiBinding.inflate(inflater, container, false)

        if (userVisibleHint) {
            try {
                initialise()
                //setPreviousAnsData()
                checkBMIExist()
                registerObservers()
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
                //setPreviousAnsData()
                checkBMIExist()
                registerObservers()
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

        binding.layHeight.setNonEditable()
        //binding.layHeight.setImage(R.drawable.img_height)
        binding.layHeight.setTitle(resources.getString(R.string.HEIGHT))
        binding.layHeight.setUnit(resources.getString(R.string.CM))

        binding.layWeight.setNonEditable()
        //binding.layWeight.setImage(R.drawable.img_weight)
        binding.layWeight.setTitle(resources.getString(R.string.WEIGHT))
        binding.layWeight.setUnit(resources.getString(R.string.KG))


        //binding.btnNext.isEnabled = false
    }

    /*    private fun setPreviousAnsData() {
            binding.rgPrevious.removeAllViews()
            val prevAnsList: MutableList<Option> = mutableListOf()
            if (!Utilities.isNullOrEmpty(viewPagerActivity!!.personName)
                && !Utilities.isNullOrEmpty(viewPagerActivity!!.personId)) {
                prevAnsList.add(Option(viewPagerActivity!!.personName, viewPagerActivity!!.personId, true))
            }
            Utilities.printLogError("prevAnsList---> $prevAnsList")
            if (prevAnsList.isNotEmpty()) {
                viewModel.setPreviousAnswersList(prevAnsList)
            }
        }*/

    private fun checkBMIExist() {
        viewModel.getHRAVitalDetails()
    }

    private fun registerObservers() {

        var toProceed = true
        viewModel.quesData.observe(viewLifecycleOwner) {
            it?.let {
                binding.imgQues.setImageView(it.bgImage)
                binding.txtQues.setHtmlTextFromId(it.question)
                if (toProceed) {
                    questionData = it
                    hraDataSingleton.question = it
                    toProceed = false
                }
            }

        }

        viewModel.vitalDetailsSavedResponse.observe(viewLifecycleOwner) { vitalDetails ->
            if (vitalDetails != null) {
                var isHeight = true
                var isWeight = true
                var isBmi = true
                //Utilities.printLog("HraVitalDetails--->$vitalDetails")
                val bmiDetails = vitalDetails.filter { vital ->
                    vital.VitalsKey.equals(HRAConstants.VitalKey_Height, ignoreCase = true)
                            || vital.VitalsKey.equals(
                        HRAConstants.VitalKey_Weight,
                        ignoreCase = true
                    )
                            || vital.VitalsKey.equals(HRAConstants.VitalKey_BMI, ignoreCase = true)
                }
                Utilities.printLog("HRAVitalDetails----> $bmiDetails")
                for (vital in bmiDetails) {
                    Utilities.printLog("VitalParameter , VitalValue---->" + vital.VitalsKey + " , " + vital.VitalsValue)
                    if (vital.VitalsKey.equals(HRAConstants.VitalKey_Height, ignoreCase = true)
                        && !Utilities.isNullOrEmpty(vital.VitalsValue)
                    ) {
                        height = vital.VitalsValue.toDouble()
                        toShow = false
                        isHeight = false

                        if (Utilities.isNullOrEmpty(hraDataSingleton.heightUnit)) {
                            binding.layHeight.setValue(height.toInt().toString())
                            binding.layHeight.setUnit(resources.getString(R.string.CM))
                        } else {
                            binding.layHeight.setValue(hraDataSingleton.heightValue)
                            binding.layHeight.setUnit(hraDataSingleton.heightUnit)
                        }
                    }
                    if (vital.VitalsKey.equals(HRAConstants.VitalKey_Weight, ignoreCase = true)
                        && !Utilities.isNullOrEmpty(vital.VitalsValue)
                    ) {
                        weight = vital.VitalsValue.toDouble()
                        isWeight = false

                        if (Utilities.isNullOrEmpty(hraDataSingleton.weightUnit)) {
                            binding.layWeight.setValue(weight.toString())
                            binding.layWeight.setUnit(resources.getString(R.string.KG))
                        } else {
                            binding.layWeight.setValue(hraDataSingleton.weightValue)
                            binding.layWeight.setUnit(hraDataSingleton.weightUnit)
                        }
                    }
                    if (vital.VitalsKey.equals(HRAConstants.VitalKey_BMI, ignoreCase = true)
                        && !Utilities.isNullOrEmpty(vital.VitalsValue)
                    ) {
                        isBmi = false

                        if (Utilities.isNullOrEmpty(hraDataSingleton.bmiValue)) {
                            bmi = vital.VitalsValue.toDouble()
                        } else {
                            bmi = hraDataSingleton.bmiValue.toDouble()
                        }
                    }
                }
                /*                if (Utilities.isNullOrEmptyOrZero(height.toString())
                                    && Utilities.isNullOrEmptyOrZero(weight.toString())
                                    && Utilities.isNullOrEmptyOrZero(bmi.toString())) {*/
                if (isHeight && isWeight && isBmi) {
                    Utilities.printLogError("BMI not Exist...!!!")
                    viewModel.callIsBMIExist(true, viewPagerActivity!!.personId)
                } else {
                    Observations.setBMIResult(
                        bmi.toString(),
                        binding.txtObservation,
                        binding.imgInfo,
                        requireContext()
                    )
                    enableNextBtn()
                }
            }
        }

        viewModel.bmiDetails.observe(viewLifecycleOwner) {
            if (it != null) {
                val bmiDetails = it
                if (!Utilities.isNullOrEmptyOrZero(bmiDetails.BMI.Height)
                    && !Utilities.isNullOrEmptyOrZero(bmiDetails.BMI.Height)
                ) {
                    try {
                        height = bmiDetails.BMI.Height!!.toDouble()
                        weight = bmiDetails.BMI.Weight!!.toDouble()

                        if (Utilities.isNullOrEmpty(hraDataSingleton.heightUnit)) {
                            binding.layHeight.setValue(height.toInt().toString())
                            binding.layHeight.setUnit(resources.getString(R.string.CM))
                        } else {
                            binding.layHeight.setValue(hraDataSingleton.heightValue)
                            binding.layHeight.setUnit(hraDataSingleton.heightUnit)
                        }

                        if (Utilities.isNullOrEmpty(hraDataSingleton.weightUnit)) {
                            binding.layWeight.setValue(weight.toInt().toString())
                            binding.layWeight.setUnit(resources.getString(R.string.KG))
                        } else {
                            binding.layWeight.setValue(hraDataSingleton.weightValue)
                            binding.layWeight.setUnit(hraDataSingleton.weightUnit)
                        }

                        if (Utilities.isNullOrEmpty(hraDataSingleton.bmiValue)) {
                            Observations.setBMIResult(
                                bmiDetails.BMI.Value!!,
                                binding.txtObservation,
                                binding.imgInfo,
                                requireContext()
                            )
                        } else {
                            Observations.setBMIResult(
                                hraDataSingleton.bmiValue,
                                binding.txtObservation,
                                binding.imgInfo,
                                requireContext()
                            )
                        }

                        enableNextBtn()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setClickable() {

        /*        binding.rgPrevious.setOnCheckedChangeListener { _, _ ->
                    viewPagerActivity!!.backToSelectFamilyMember()
                }*/

        binding.btnNext.setOnClickListener {

            if (Utilities.isNullOrEmptyOrZero(height.toString())) {
                Utilities.toastMessageShort(
                    requireContext(),
                    resources.getString(R.string.PLEASE_SELECT_HEIGHT)
                )
            } else if (Utilities.isNullOrEmptyOrZero(weight.toString())) {
                Utilities.toastMessageShort(
                    requireContext(),
                    resources.getString(R.string.PLEASE_SELECT_WEIGHT)
                )
            } else {
                if (Validations.validationBMI(height, weight, requireContext())) {
                    saveResponseForNextScreen()
                    viewModel.saveBMIDetails(height, weight, getBMI().toDouble())
                    viewModel.removeSource(qCode)
                    viewPagerActivity!!.setCurrentScreen(viewPagerActivity!!.getCurrentScreen() + 1)
                }
            }

        }

        binding.layHeight.setOnClickListener {
            HraHelper.showHeightDialog(
                height.toInt(),
                binding.layHeight,
                this@HraQuesBmiFragment,
                requireContext(),
                viewModel.getPreference("HEIGHT")
            )
        }

        binding.layHeight.editText!!.setOnClickListener {
            HraHelper.showHeightDialog(
                height.toInt(),
                binding.layHeight,
                this@HraQuesBmiFragment,
                requireContext(),
                viewModel.getPreference("HEIGHT")
            )
        }

        /*        binding.layHeight.editText!!.setOnFocusChangeListener { v, hasFocus ->
                    if (hasFocus && toShow) {
                        HraHelper.showHeightDialog(height.toInt(), binding.layHeight, this@HraQuesBmiFragment, requireContext(),viewModel.getPreference("HEIGHT"))
                    }
                }*/

        binding.layWeight.setOnClickListener {
            HraHelper.showWeightDialog(
                weight,
                binding.layWeight,
                this@HraQuesBmiFragment,
                requireContext(),
                viewModel.getPreference("WEIGHT")
            )
        }

        binding.layWeight.editText!!.setOnClickListener {
            HraHelper.showWeightDialog(
                weight,
                binding.layWeight,
                this@HraQuesBmiFragment,
                requireContext(),
                viewModel.getPreference("WEIGHT")
            )
        }

        binding.layWeight.editText!!.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                HraHelper.showWeightDialog(
                    weight,
                    binding.layWeight,
                    this@HraQuesBmiFragment,
                    requireContext(),
                    viewModel.getPreference("WEIGHT")
                )
            }
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
        if (dialogType.equals("Height", ignoreCase = true)) {
            this.height = height.toDouble()
            binding.layHeight.setValue(visibleValue)
            binding.layHeight.setUnit(unit.lowercase())
            hraDataSingleton.heightValue = visibleValue
            hraDataSingleton.heightUnit = unit.lowercase()
            Utilities.printLog("Height::visibleValue----> $height , $visibleValue")
        } else {
            this.weight = weight.toDouble()
            binding.layWeight.setValue(visibleValue)
            binding.layWeight.setUnit(unit.lowercase())
            hraDataSingleton.weightValue = visibleValue
            hraDataSingleton.weightUnit = unit.lowercase()
            Utilities.printLog("Weight::visibleValue----> $weight , $visibleValue")
        }
        if (!Utilities.isNullOrEmptyOrZero(binding.layHeight.getValue())
            && !Utilities.isNullOrEmptyOrZero(binding.layWeight.getValue())
        ) {
            val bmi = getBMI()
            hraDataSingleton.bmiValue = bmi
            Observations.setBMIResult(
                bmi,
                binding.txtObservation,
                binding.imgInfo,
                requireContext()
            )
            enableNextBtn()
        }
    }

    private fun getBMI(): String {
        Utilities.printLog("height,weight----> $height , $weight")
        var bmi = String.format(
            "%.1f",
            CalculateParameters.roundOffPrecision(CalculateParameters.getBMI(height, weight), 1)
        )
        Utilities.printLogError("bmi---->$bmi")
        if (bmi.contains(",", true)) {
            bmi = bmi.replace(",", ".", true)
        }
        Utilities.printLogError("bmi---->$bmi" + " :: " + bmi.replace(",", ".", true))
        return bmi
    }

    private fun saveResponseForNextScreen() {
        selectedOptionList = mutableListOf()
        selectedOptionList.add(
            Option(
                resources.getString(R.string.HEIGHT) + " : ${height.toInt()} " + resources.getString(
                    R.string.CM
                ), Constants.HEIGHT, true
            )
        )
        selectedOptionList.add(
            Option(
                resources.getString(R.string.WEIGHT) + " : $weight " + resources.getString(
                    R.string.KG
                ), Constants.WEIGHT, true
            )
        )
        hraDataSingleton.previousAnsList[viewPagerActivity!!.getCurrentScreen()] =
            selectedOptionList
    }

    private fun enableNextBtn() {
        binding.btnNext.isEnabled = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.removeSource(qCode)
    }

}