package com.techglock.health.app.hra.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseFragment
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.constants.NavigationConstants
import com.techglock.health.app.common.extension.openAnotherActivity
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.FragmentIntroductionBinding
import com.techglock.health.app.hra.HraHomeActivity
import com.techglock.health.app.hra.viewmodel.HraViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IntroductionFragment : BaseFragment(), DialogNotificationHRA.OnRestartHRA {

    private val viewModel: HraViewModel by lazy {
        ViewModelProvider(this)[HraViewModel::class.java]
    }
    private lateinit var binding: FragmentIntroductionBinding

    private var screen = ""
    private var notificationMsg = ""

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            arguments?.let {
                screen = it.getString(Constants.SCREEN, "")!!
                notificationMsg = it.getString(Constants.NOTIFICATION_MESSAGE, "")!!
                Utilities.printLogError("screen,notificationMsg--->$screen,$notificationMsg")
            }
            when (screen) {
                "FAMILY_HRA" -> {
                    val bundle = Bundle()
                    bundle.putString(Constants.SCREEN, screen)
                    findNavController().navigate(
                        R.id.action_introductionFragment_to_selectFamilyMemberFragment,
                        bundle
                    )
                }

                "HRA_THREE" -> {
                    Utilities.printLogError("Show Pop Up")
                    val dialogUpdateApp =
                        DialogNotificationHRA(requireContext(), notificationMsg, this)
                    dialogUpdateApp.show()
                }
            }

            // Callback to Handle back button event
            val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    (activity as HraHomeActivity).finish()
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
        binding = FragmentIntroductionBinding.inflate(inflater, container, false)
        try {
            /*            arguments?.let {
                            screen = it.getString(Constants.SCREEN, "")!!
                            Utilities.printLogError("screen--->$screen")
                        }*/

            if (screen.equals(Constants.FRESH, ignoreCase = true)) {
                binding.btnSkipHra.visibility = View.VISIBLE
                viewModel.getParameterList()
            } else {
                binding.btnSkipHra.visibility = View.GONE
            }

            initialise()
            setClickable()
            registerObservers()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return binding.root
    }

    private fun initialise() {
        val welcomeMsg = resources.getString(R.string.TEXT_START_ASSESSMENT, viewModel.firstName)
        binding.lblIntroMsg.setHtmlText(welcomeMsg)

        /*        val statusCode = appConfigurationSingleton.hraHistory.statusCode
                if ( statusCode.equals("SUB",ignoreCase = true) ) {
                    binding.btnSkipHra.visibility = View.GONE
                } else {
                    binding.btnSkipHra.visibility = View.VISIBLE
                }*/

        /*        if ( from == Constants.FRESH ) {
                    binding.btnSkipHra.visibility = View.VISIBLE
                } else {
                    binding.btnSkipHra.visibility = View.GONE
                }*/
    }

    private fun setClickable() {

        binding.btnStartHra.setOnClickListener {
            Utilities.printLogError("personId--->${viewModel.personId}")
//            FirebaseHelper.logCustomFirebaseEvent(FirebaseConstants.HRA_INITIATED_EVENT)
            if (viewModel.adminPersonId != viewModel.personId) {
                viewModel.startHra(viewModel.personId, viewModel.firstName)
            } else {
                it.findNavController()
                    .navigate(R.id.action_introductionFragment_to_selectFamilyMemberFragment)
            }
        }

        binding.btnSkipHra.setOnClickListener {

//            navigateToHomeScreen()
        }

    }

    private fun registerObservers() {
        viewModel.hraStart.observe(viewLifecycleOwner) { }
        viewModel.paramList.observe(viewLifecycleOwner) { }
    }

    override fun onRestartHRAClick(restartHra: Boolean) {
        Utilities.printLogError("restartHra--->$restartHra")
        if (restartHra) {
            binding.btnStartHra.performClick()
        }
    }

    private fun navigateToHomeScreen() {
        openAnotherActivity(destination = NavigationConstants.HOME, clearTop = true)
    }

}
