package com.techglock.health.app.hra.ui

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
import com.techglock.health.app.common.constants.NavigationConstants
import com.techglock.health.app.common.extension.openAnotherActivity
import com.techglock.health.app.common.utils.DefaultNotificationDialog
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.common.utils.showDialog
import com.techglock.health.app.databinding.FragmentSelectFamilyMemberBinding
import com.techglock.health.app.hra.adapter.FamilyMembersAdapter
import com.techglock.health.app.hra.common.HraDataSingleton
import com.techglock.health.app.hra.viewmodel.HraViewModel
import com.techglock.health.app.model.entity.UserRelatives
import com.techglock.health.app.model.hra.Option
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectFamilyMemberFragment : BaseFragment(), DefaultNotificationDialog.OnDialogValueListener,
    FamilyMembersAdapter.OnRelativeClickListener {

    private val viewModel: HraViewModel by lazy {
        ViewModelProvider(this)[HraViewModel::class.java]
    }
    private lateinit var binding: FragmentSelectFamilyMemberBinding

    private val hraDataSingleton = HraDataSingleton.getInstance()!!
    private var selectedOptionList: MutableList<Option> = mutableListOf()

    private var screen = ""
    private var relativeId = ""
    private var relativeName = ""
    private var familyMembersAdapter: FamilyMembersAdapter? = null

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            // callback to Handle back button event
            val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.action_selectFamilyMemberFragment_to_introductionFragment)
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
        binding = FragmentSelectFamilyMemberBinding.inflate(inflater, container, false)
        try {
            arguments?.let {
                screen = it.getString(Constants.SCREEN, "")!!
                Utilities.printLogError("screen--->$screen")
            }
            initialise()
            registerObservers()
            setClickable()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return binding.root
    }

    private fun initialise() {
        /*        if ( from == Constants.FRESH ) {
                    viewModel.getSelfUserDetails()
                } else {
                    viewModel.getUserRelatives()
                }*/
        //viewModel.getUserRelatives(screen)
        viewModel.getSelfUserDetails()

        if (viewModel.adminPersonId != viewModel.personId) {
            //binding.layoutAddFamilyMember.visibility = View.INVISIBLE
        } else {
            //binding.layoutAddFamilyMember.visibility = View.VISIBLE
        }

        familyMembersAdapter = FamilyMembersAdapter(viewModel, requireContext(), this)
        binding.rvFamilyMembers.adapter = familyMembersAdapter

        binding.btnStartHra.isEnabled = false
    }

    private fun registerObservers() {
        viewModel.hraStart.observe(viewLifecycleOwner) { }
        viewModel.userRelativesList.observe(viewLifecycleOwner) {
            if (it != null) {
                familyMembersAdapter!!.updateRelativeList(it.toMutableList())
            }
        }
    }

    private fun setClickable() {

        binding.btnStartHra.setOnClickListener {
            if (!Utilities.isNullOrEmptyOrZero(relativeId)) {
                Utilities.printLogError("Selected RelativeId,RelativeName--->$relativeId,$relativeName")
                if (viewModel.personId != relativeId) {
                    showSwitchProfileDialog()
                } else {
                    viewModel.clearSavedQuestionsData()
                    viewModel.startHra(relativeId, relativeName)
                }
            } else {
                Utilities.toastMessageShort(
                    requireContext(),
                    resources.getString(R.string.PLEASE_SELECT_FAMILY_MEMBER)
                )
            }
        }

        binding.layoutAddFamilyMember.setOnClickListener {
            openAnotherActivity(destination = NavigationConstants.FAMILY_PROFILE) {
                putString(Constants.FROM, Constants.HRA)
            }
        }

        /*        binding.rgSelection.setOnCheckedChangeListener { _, _ ->
            relativeId = ViewUtils.getRadioSelectedValueTag(binding.rgSelection)
            relativeName = ViewUtils.getRadioButtonSelectedValue(binding.rgSelection)
            if (!Utilities.isNullOrEmptyOrZero(relativeId)) {
                Utilities.printLogError("Selected RelativeId,RelativeName--->$relativeId,$relativeName")
                if (viewModel.personId != relativeId) {
                    showSwitchProfileDialog()
                } else {
                    viewModel.clearSavedQuestionsData()
                    viewModel.startHra(relativeId, relativeName)
                }
            }
        }*/

    }

    private fun showSwitchProfileDialog() {
        showDialog(
            listener = this,
            title = resources.getString(R.string.SWITCH_PROFILE),
            message = resources.getString(R.string.SWITCH_PROFILE_DESC),
            rightText = resources.getString(R.string.PROCEED)
        )
    }

    override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) {
        if (isButtonRight) {
            viewModel.switchProfile(relativeId)
            viewModel.startHra(relativeId, relativeName)
        }
    }

    override fun onResume() {
        super.onResume()
        //binding.rgSelection.removeAllViews()
        //viewModel.getUserRelatives(screen)
        viewModel.getSelfUserDetails()
    }

    override fun onRelativeSelection(position: Int, relative: UserRelatives) {
        relativeId = relative.relativeID
        relativeName = relative.firstName
        binding.btnStartHra.isEnabled = true
    }

}