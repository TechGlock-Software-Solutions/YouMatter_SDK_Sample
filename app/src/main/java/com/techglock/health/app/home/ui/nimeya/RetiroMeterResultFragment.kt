package com.techglock.health.app.home.ui.nimeya

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseFragment
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.utils.DateHelper
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.FragmentRetiroMeterResultBinding
import com.techglock.health.app.home.common.NimeyaSingleton
import com.techglock.health.app.home.viewmodel.NimeyaViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RetiroMeterResultFragment : BaseFragment() {

    private val viewModel: NimeyaViewModel by lazy {
        ViewModelProvider(this)[NimeyaViewModel::class.java]
    }
    private lateinit var binding: FragmentRetiroMeterResultBinding
    override fun getViewModel(): BaseViewModel = viewModel

    private var nimeyaSingleton = NimeyaSingleton.getInstance()!!
    private var from = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (it.containsKey(Constants.FROM)) {
                from = it.getString(Constants.FROM)!!
            }
            Utilities.printLogError("from--->$from")
        }
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
        binding = FragmentRetiroMeterResultBinding.inflate(inflater, container, false)
        try {
            initialise()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun initialise() {
        if (from.equals(Constants.NIMEYA_RETIRO_METER_RESULT, ignoreCase = true)) {
            val retiroMeterHistory = nimeyaSingleton.retiroMeterHistory
            binding.txtDate.text =
                "${resources.getString(R.string.AS_ON)} " + DateHelper.convertDateSourceToDestination(
                    retiroMeterHistory.dateTime,
                    DateHelper.DATE_FORMAT_UTC,
                    DateHelper.DATEFORMAT_DDMMMYYYY_NEW
                )
            binding.speedView.withTremble = false
            binding.speedView.speedTo(retiroMeterHistory.score!!.toFloat(), 1500)
            binding.txtScore.text = retiroMeterHistory.score.toString()
            binding.txtRiskMeter.text = retiroMeterHistory.scoreText
            binding.txtRiskText.text = retiroMeterHistory.message

            binding.txtDesiredMonthlyIncomeValue.text =
                " : ${resources.getString(R.string.INDIAN_RUPEE)} ${
                    Utilities.formatNumberDecimalWithComma(retiroMeterHistory.yourDesireRetirementIncome!!)
                }"
            binding.txtProjectedMonthlyIncomeValue.text =
                " : ${resources.getString(R.string.INDIAN_RUPEE)} ${
                    Utilities.formatNumberDecimalWithComma(retiroMeterHistory.projectedMonthlyRetirementIncomePerMonth!!)
                }"
            binding.txtMonthlyRetirementSavingsValue.text =
                " : ${resources.getString(R.string.INDIAN_RUPEE)} ${
                    Utilities.formatNumberDecimalWithComma(retiroMeterHistory.requiredMonthlyRetirementSavings!!)
                }"
            binding.txtDepositSchemesValue.text =
                " : ${resources.getString(R.string.INDIAN_RUPEE)} ${
                    Utilities.formatNumberDecimalWithComma(retiroMeterHistory.requiredPpf!! + retiroMeterHistory.requiredEpf!!)
                }"
            binding.txtNationalPensionSchemesValue.text =
                " : ${resources.getString(R.string.INDIAN_RUPEE)} ${
                    Utilities.formatNumberDecimalWithComma(retiroMeterHistory.requiredNps!!)
                }"
            binding.txtEquityMutualFundsPensionInsuranceValue.text =
                " : ${resources.getString(R.string.INDIAN_RUPEE)} ${
                    Utilities.formatNumberDecimalWithComma(retiroMeterHistory.requiredEquityMutualFunds!!)
                }"
        } else {
            val saveRetiroMeter = nimeyaSingleton.saveRetiroMeter
            binding.txtDate.text =
                "${resources.getString(R.string.AS_ON)} " + DateHelper.currentDateAsStringddMMMyyyyNew
            binding.speedView.withTremble = false
            binding.speedView.speedTo(saveRetiroMeter.data.score!!.toFloat(), 1500)
            binding.txtScore.text = saveRetiroMeter.data.score.toString()
            binding.txtRiskMeter.text = saveRetiroMeter.data.scoreText
            binding.txtRiskText.text = saveRetiroMeter.data.message

            binding.txtDesiredMonthlyIncomeValue.text =
                " : ${resources.getString(R.string.INDIAN_RUPEE)} ${
                    Utilities.formatNumberDecimalWithComma(saveRetiroMeter.data.yourDesireRetirementIncome!!)
                }"
            binding.txtProjectedMonthlyIncomeValue.text =
                " : ${resources.getString(R.string.INDIAN_RUPEE)} ${
                    Utilities.formatNumberDecimalWithComma(saveRetiroMeter.data.projectedMonthlyRetirementIncomePerMonth!!)
                }"
            binding.txtMonthlyRetirementSavingsValue.text =
                " : ${resources.getString(R.string.INDIAN_RUPEE)} ${
                    Utilities.formatNumberDecimalWithComma(saveRetiroMeter.data.requiredMonthlyRetirementSavings!!)
                }"
            binding.txtDepositSchemesValue.text =
                " : ${resources.getString(R.string.INDIAN_RUPEE)} ${
                    Utilities.formatNumberDecimalWithComma(saveRetiroMeter.data.requiredPpf!! + saveRetiroMeter.data.requiredEpf!!)
                }"
            binding.txtNationalPensionSchemesValue.text =
                " : ${resources.getString(R.string.INDIAN_RUPEE)} ${
                    Utilities.formatNumberDecimalWithComma(saveRetiroMeter.data.requiredNps!!)
                }"
            binding.txtEquityMutualFundsPensionInsuranceValue.text =
                " : ${resources.getString(R.string.INDIAN_RUPEE)} ${
                    Utilities.formatNumberDecimalWithComma(saveRetiroMeter.data.requiredEquityMutualFunds!!)
                }"
        }

        binding.btnRestart.setOnClickListener {
            findNavController().navigate(R.id.action_retiroMeterResultFragment_to_retiroMeterInputFragment)
        }
    }

}