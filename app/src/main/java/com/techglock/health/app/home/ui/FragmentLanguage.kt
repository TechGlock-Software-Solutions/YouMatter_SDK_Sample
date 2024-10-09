package com.techglock.health.app.home.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseFragment
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.extension.preventDoubleClick
import com.techglock.health.app.common.utils.AppColorHelper
import com.techglock.health.app.common.utils.LocaleHelper
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.ActivityLanguageBinding
import com.techglock.health.app.home.adapter.LanguageAdapter
import com.techglock.health.app.home.viewmodel.BackgroundCallViewModel
import com.techglock.health.app.home.viewmodel.SettingsViewModel
import com.techglock.health.app.model.home.LanguageModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentLanguage : BaseFragment(), LanguageAdapter.OnItemClickListener {

    private val viewModel: SettingsViewModel by lazy {
        ViewModelProvider(this)[SettingsViewModel::class.java]
    }
    private val backGroundCallViewModel: BackgroundCallViewModel by lazy {
        ViewModelProvider(this)[BackgroundCallViewModel::class.java]
    }
    private lateinit var binding: ActivityLanguageBinding

    private val appColorHelper = AppColorHelper.instance!!

    private var from = ""
    private var languageAdapter: LanguageAdapter? = null
    private var languageDataSet: LanguageModel? = null

    override fun getViewModel(): BaseViewModel = viewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = ActivityLanguageBinding.inflate(layoutInflater)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as HomeMainActivity).setToolbarInfo(
            4,
            true,
            showBottomNavigation = false,
            showToolBar = false,
            isView3 = false,
            title = resources.getString(R.string.PROFILE),
            updateStatusBgColor = R.drawable.gradient_language
        )

        initialise()
    }

    private fun initialise() {
        val languageList = viewModel.getLanguageList(requireContext())

        for (i in languageList.indices) {
            if (languageList[i].selectionStatus) {
                languageDataSet = languageList[i]
            }
        }
        languageAdapter = LanguageAdapter(requireContext(), this)
        binding.rvLanguageList.adapter = languageAdapter
        languageAdapter!!.updateList(viewModel.getLanguageList(requireContext()))

        binding.btnSave.setOnClickListener {
            binding.btnSave.preventDoubleClick()
            LocaleHelper.setFragLocale(requireContext(), languageDataSet!!.languageCode)
            Utilities.logCleverTapChangeLanguage(languageDataSet!!.languageCode, requireContext())
//            (activity as HomeMainActivity).recreate()

            (activity as HomeMainActivity).onBackPressedDispatcher.onBackPressed()
        }

        /*        binding.backIV.setOnClickListener {
                    (activity as HomeMainActivity).onBackPressedDispatcher.onBackPressed()
                }*/
    }

    override fun onItemSelection(position: Int, data: LanguageModel) {
        Utilities.printLogError("LanguageCode---> " + data.language + " :: " + data.languageCode)
        languageDataSet = data
    }


}