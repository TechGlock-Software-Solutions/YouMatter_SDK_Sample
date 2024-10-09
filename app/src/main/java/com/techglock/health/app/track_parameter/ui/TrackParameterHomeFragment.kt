package com.techglock.health.app.track_parameter.ui

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
import com.techglock.health.app.databinding.FragmentTrackParameterHomeBinding
import com.techglock.health.app.track_parameter.viewmodel.ParameterHomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrackParameterHomeFragment : BaseFragment() {

    private val viewModel: ParameterHomeViewModel by lazy {
        ViewModelProvider(this)[ParameterHomeViewModel::class.java]
    }
    private lateinit var binding: FragmentTrackParameterHomeBinding

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            // Callback to Handle back button event
            val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    //(activity as ParameterHomeActivity).routeToHomeScreen()
                    requireActivity().finish()
                }
            }
            requireActivity().onBackPressedDispatcher.addCallback(this, callback)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTrackParameterHomeBinding.inflate(inflater, container, false)
        initialise()
        setClickable()
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initialise() {

    }

    private fun setClickable() {

        binding.layoutSelectParam.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_selectProfileFragment)
        }

        binding.layoutUpdateParam.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_updateFragment)
        }

        binding.layoutDashboardParam.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_dashboardFragment)
        }

        binding.layoutHistoryParam.setOnClickListener {
            //findNavController().navigate(R.id.action_homeFragment_to_currentHistoryFragment)
            viewModel.navigateParam(
                TrackParameterHomeFragmentDirections.actionHomeFragmentToCurrentHistoryFragment(
                    "BMI",
                    "BMI"
                )
            )
        }

        /*        binding.imgBack.setOnClickListener {
                    requireActivity().onBackPressed()
                }*/

    }

}