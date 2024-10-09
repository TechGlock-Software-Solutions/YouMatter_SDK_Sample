package com.techglock.health.app.track_parameter.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseFragment
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.SelectParameterFragmentBinding
import com.techglock.health.app.track_parameter.adapter.SelectParameterAdapter
import com.techglock.health.app.track_parameter.viewmodel.DashboardViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectParameterFragment : BaseFragment() {

    private val viewModel: DashboardViewModel by lazy {
        ViewModelProvider(this)[DashboardViewModel::class.java]
    }
    private lateinit var binding: SelectParameterFragmentBinding

    private var selectParameterAdapter: SelectParameterAdapter? = null

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SelectParameterFragmentBinding.inflate(inflater, container, false)
        initialise()
        setClickable()
        return binding.root
    }

    private fun initialise() {
        //viewModel.getAllProfileCodes()
        binding.rvSelectParameters.layoutManager =
            androidx.recyclerview.widget.GridLayoutManager(context, 3)

        viewModel.getAllProfilesWithRecentSelectionList()
        selectParameterAdapter = SelectParameterAdapter(requireContext(), viewModel)
        binding.rvSelectParameters.adapter = selectParameterAdapter



        viewModel.listAllProfiles.observe(viewLifecycleOwner) {
            selectParameterAdapter!!.updateData(it)
        }
    }

    private fun setClickable() {

        binding.btnNextSelectParameters.setOnClickListener {
            if (validate()) {
                viewModel.saveSelectedParameter(selectParameterAdapter!!.dataList)
                val selectedProfiles = selectParameterAdapter!!.getSelectedParameterList()
                Utilities.printLog("SelectedProfiles=> $selectedProfiles")
                viewModel.navigateParam(
                    SelectParameterFragmentDirections.actionSelectProfileFragmentToUpdateParameterFragment(
                        selectedProfiles[0].profileCode, "false"
                    )
                )
                //findNavController().navigate(R.id.action_selectProfileFragment_to_updateParameterFragment)
            } else {
                Utilities.toastMessageShort(
                    requireContext(),
                    resources.getString(R.string.MSG_SELECT_PROFILE_TO_PROCEED)
                )
            }
        }

    }

    private fun validate(): Boolean {
        var isValid = false
        for (profile in selectParameterAdapter!!.dataList) {
            if (profile.isSelection) {
                isValid = true
                break
            }
        }
        Utilities.printLogError("isValid--->$isValid")
        return isValid
    }


}