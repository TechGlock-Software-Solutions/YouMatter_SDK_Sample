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
import com.techglock.health.app.databinding.FragmentProtectoMeterResultBinding
import com.techglock.health.app.home.common.NimeyaSingleton
import com.techglock.health.app.home.viewmodel.NimeyaViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProtectoMeterResultFragment : BaseFragment() {

    private val viewModel: NimeyaViewModel by lazy {
        ViewModelProvider(this)[NimeyaViewModel::class.java]
    }
    private lateinit var binding: FragmentProtectoMeterResultBinding

    private var nimeyaSingleton = NimeyaSingleton.getInstance()!!
    private var from = ""
    override fun getViewModel(): BaseViewModel = viewModel

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
        binding = FragmentProtectoMeterResultBinding.inflate(inflater, container, false)
        try {
            initialise()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun initialise() {
        if (from.equals(Constants.NIMEYA_PROTECTO_METER_RESULT, ignoreCase = true)) {
            val protectoMeterHistory = nimeyaSingleton.protectoMeterHistory
            Utilities.printData("ProtectoMeterHistory", protectoMeterHistory, true)
            if (!Utilities.isNullOrEmpty(protectoMeterHistory.dateTime!!)) {
                binding.txtDate.text =
                    "${resources.getString(R.string.AS_ON)} " + DateHelper.convertDateSourceToDestination(
                        protectoMeterHistory.dateTime!!,
                        DateHelper.DATE_FORMAT_UTC,
                        DateHelper.DATEFORMAT_DDMMMYYYY_NEW
                    )
            } else {
                binding.txtDate.text =
                    "${resources.getString(R.string.AS_ON)} " + DateHelper.currentDateAsStringddMMMyyyyNew
            }
            binding.speedViewLifeInsurance.withTremble = false
            binding.speedViewLifeInsurance.speedTo(
                protectoMeterHistory.lifeInsuranceScore!!.toFloat(),
                1500
            )
            binding.txtScoreLifeInsurance.text =
                protectoMeterHistory.lifeInsuranceScore!!.toString()
            binding.txtRiskMeterLifeInsurance.text = protectoMeterHistory.lifeInsuranceScoreText
            binding.txtCurrentLifeInsuranceCoverValue.text =
                " :  ${resources.getString(R.string.INDIAN_RUPEE)} " + Utilities.formatNumberDecimalWithComma(
                    protectoMeterHistory.lifeInsuranceCover!!.toInt()
                )
            binding.txtAdditionalLifeInsuranceValue.text =
                " :  ${resources.getString(R.string.INDIAN_RUPEE)} " + Utilities.formatNumberDecimalWithComma(
                    protectoMeterHistory.lifeInsuranceNeed!!.toInt()
                )

            binding.speedViewHealthInsurance.withTremble = false
            binding.speedViewHealthInsurance.speedTo(
                protectoMeterHistory.healthInsuranceScore!!.toFloat(),
                1500
            )
            binding.txtScoreHealthInsurance.text =
                protectoMeterHistory.healthInsuranceScore!!.toString()
            binding.txtRiskMeterHealthInsurance.text = protectoMeterHistory.healthInsuranceScoreText
            binding.txtCurrentHealthInsuranceCoverValue.text =
                " :  ${resources.getString(R.string.INDIAN_RUPEE)} " + Utilities.formatNumberDecimalWithComma(
                    protectoMeterHistory.healthInsuranceCover!!.toInt()
                )
            binding.txtAdditionalHealthInsuranceValue.text =
                " :  ${resources.getString(R.string.INDIAN_RUPEE)} " + Utilities.formatNumberDecimalWithComma(
                    protectoMeterHistory.healthInsuranceNeed!!.toInt()
                )
        } else {
            val saveProtectoMeter = nimeyaSingleton.saveProtectoMeter
            binding.txtDate.text =
                "${resources.getString(R.string.AS_ON)} " + DateHelper.currentDateAsStringddMMMyyyyNew
            binding.speedViewLifeInsurance.withTremble = false
            binding.speedViewLifeInsurance.speedTo(
                saveProtectoMeter.data.lifeInsuranceScore!!.toFloat(),
                1500
            )
            binding.txtScoreLifeInsurance.text =
                saveProtectoMeter.data.lifeInsuranceScore!!.toString()
            binding.txtRiskMeterLifeInsurance.text = saveProtectoMeter.data.lifeInsuranceScoreText
            binding.txtCurrentLifeInsuranceCoverValue.text =
                " :  ${resources.getString(R.string.INDIAN_RUPEE)} " + Utilities.formatNumberDecimalWithComma(
                    saveProtectoMeter.data.lifeInsuranceCover!!
                )
            binding.txtAdditionalLifeInsuranceValue.text =
                " :  ${resources.getString(R.string.INDIAN_RUPEE)} " + Utilities.formatNumberDecimalWithComma(
                    saveProtectoMeter.data.lifeInsuranceNeed!!
                )

            binding.speedViewHealthInsurance.withTremble = false
            binding.speedViewHealthInsurance.speedTo(
                saveProtectoMeter.data.healthInsuranceScore!!.toFloat(),
                1500
            )
            binding.txtScoreHealthInsurance.text =
                saveProtectoMeter.data.healthInsuranceScore!!.toString()
            binding.txtRiskMeterHealthInsurance.text =
                saveProtectoMeter.data.healthInsuranceScoreText
            binding.txtCurrentHealthInsuranceCoverValue.text =
                " :  ${resources.getString(R.string.INDIAN_RUPEE)} " + Utilities.formatNumberDecimalWithComma(
                    saveProtectoMeter.data.healthInsuranceCover!!
                )
            binding.txtAdditionalHealthInsuranceValue.text =
                " :  ${resources.getString(R.string.INDIAN_RUPEE)} " + Utilities.formatNumberDecimalWithComma(
                    saveProtectoMeter.data.healthInsuranceNeed!!
                )
        }

        binding.btnRestart.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(Constants.FROM, Constants.NIMEYA_PROTECTO_METER_RESULT)
            findNavController().navigate(
                R.id.action_protectoMeterResultFragment_to_protectoMeterInputFragment,
                bundle
            )
        }
    }

}