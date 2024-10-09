package com.techglock.health.app.home.ui.nimeya

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseFragment
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.FragmentNimeyaHomeBinding
import com.techglock.health.app.home.viewmodel.NimeyaViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NimeyaHomeFragment : BaseFragment() {

    private val viewModel: NimeyaViewModel by lazy {
        ViewModelProvider(this)[NimeyaViewModel::class.java]
    }
    private lateinit var binding: FragmentNimeyaHomeBinding

    private var to = ""
    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (requireActivity().intent.hasExtra(Constants.TO)) {
            to = requireActivity().intent.getStringExtra(Constants.TO)!!
        }
        Utilities.printLogError("To_2--->$to")
        val bundle = Bundle()
        when (to) {
            Constants.NIMEYA_RISKO_METER -> {
                Utilities.printLogError("RouteTo--->${Constants.NIMEYA_RISKO_METER}")
                bundle.putString(Constants.FROM, Constants.NIMEYA_RISKO_METER)
                findNavController().navigate(
                    R.id.action_nimeyaHomeFragment_to_riskoMeterInputFragment,
                    bundle
                )
            }

            Constants.NIMEYA_PROTECTO_METER -> {
                Utilities.printLogError("RouteTo--->${Constants.NIMEYA_PROTECTO_METER}")
                bundle.putString(Constants.FROM, Constants.NIMEYA_PROTECTO_METER)
                findNavController().navigate(
                    R.id.action_nimeyaHomeFragment_to_protectoMeterInputFragment,
                    bundle
                )
            }

            Constants.NIMEYA_RETIRO_METER -> {
                Utilities.printLogError("RouteTo--->${Constants.NIMEYA_RETIRO_METER}")
                bundle.putString(Constants.FROM, Constants.NIMEYA_RETIRO_METER)
                findNavController().navigate(
                    R.id.action_nimeyaHomeFragment_to_retiroMeterInputFragment,
                    bundle
                )
            }

            Constants.NIMEYA_RISKO_METER_RESULT -> {
                Utilities.printLogError("RouteTo--->${Constants.NIMEYA_RISKO_METER_RESULT}")
                bundle.putString(Constants.FROM, Constants.NIMEYA_RISKO_METER_RESULT)
                findNavController().navigate(
                    R.id.action_nimeyaHomeFragment_to_riskoMeterResultFragment,
                    bundle
                )
            }

            Constants.NIMEYA_PROTECTO_METER_RESULT -> {
                Utilities.printLogError("RouteTo--->${Constants.NIMEYA_PROTECTO_METER_RESULT}")
                bundle.putString(Constants.FROM, Constants.NIMEYA_PROTECTO_METER_RESULT)
                findNavController().navigate(
                    R.id.action_nimeyaHomeFragment_to_protectoMeterResultFragment,
                    bundle
                )
            }

            Constants.NIMEYA_RETIRO_METER_RESULT -> {
                Utilities.printLogError("RouteTo--->${Constants.NIMEYA_RETIRO_METER_RESULT}")
                bundle.putString(Constants.FROM, Constants.NIMEYA_RETIRO_METER_RESULT)
                findNavController().navigate(
                    R.id.action_nimeyaHomeFragment_to_retiroMeterResultFragment,
                    bundle
                )
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNimeyaHomeBinding.inflate(inflater, container, false)
        try {
            initialise()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return binding.root
    }

    private fun initialise() {

    }

}