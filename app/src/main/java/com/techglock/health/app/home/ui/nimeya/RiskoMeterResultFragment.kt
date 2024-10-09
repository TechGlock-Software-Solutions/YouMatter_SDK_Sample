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
import com.techglock.health.app.databinding.FragmentRiskoMeterResultBinding
import com.techglock.health.app.home.common.NimeyaSingleton
import com.techglock.health.app.home.viewmodel.NimeyaViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RiskoMeterResultFragment : BaseFragment() {

    private val viewModel: NimeyaViewModel by lazy {
        ViewModelProvider(this)[NimeyaViewModel::class.java]
    }
    private lateinit var binding: FragmentRiskoMeterResultBinding

    private var nimeyaSingleton = NimeyaSingleton.getInstance()!!
    private var from = ""

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (it.containsKey(Constants.FROM)) {
                from = it.getString(Constants.FROM)!!
            }
            Utilities.printLogError("from31--->$from")
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
        binding = FragmentRiskoMeterResultBinding.inflate(inflater, container, false)
        try {
            initialise()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun initialise() {
        if (from.equals(Constants.NIMEYA_RISKO_METER_RESULT, ignoreCase = true)) {
            val riskoMeterHistory = nimeyaSingleton.riskoMeterHistory
            Utilities.printData("RiskoMeterHistory", riskoMeterHistory, true)
            if (!Utilities.isNullOrEmpty(riskoMeterHistory.dateTime!!)) {
                binding.txtDate.text =
                    "${resources.getString(R.string.AS_ON)} " + DateHelper.convertDateSourceToDestination(
                        riskoMeterHistory.dateTime,
                        DateHelper.DATE_FORMAT_UTC,
                        DateHelper.DATEFORMAT_DDMMMYYYY_NEW
                    )
            } else {
                binding.txtDate.text =
                    "${resources.getString(R.string.AS_ON)} " + DateHelper.currentDateAsStringddMMMyyyyNew
            }
            binding.speedView.withTremble = false
            binding.speedView.speedTo(riskoMeterHistory.score!!.toFloat(), 1500)
            binding.txtScore.text = riskoMeterHistory.score.toString()
            binding.txtRiskMeter.text = riskoMeterHistory.riskMeter
            binding.txtRiskText.text = riskoMeterHistory.riskText
        } else {
            val saveRiskoMeter = nimeyaSingleton.saveRiskoMeter
            binding.txtDate.text =
                "${resources.getString(R.string.AS_ON)} " + DateHelper.currentDateAsStringddMMMyyyyNew
            binding.speedView.withTremble = false
            binding.speedView.speedTo(saveRiskoMeter.data.score!!.toFloat(), 1500)
            binding.txtScore.text = saveRiskoMeter.data.score.toString()
            binding.txtRiskMeter.text = saveRiskoMeter.data.riskMeter
            binding.txtRiskText.text = saveRiskoMeter.data.riskText
        }

        binding.btnRestart.setOnClickListener {
            findNavController().navigate(R.id.action_riskoMeterResultFragment_to_riskoMeterInputFragment)
        }
    }

}