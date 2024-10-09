package com.techglock.health.app.home.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseFragment
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.CleverTapConstants
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.constants.NavigationConstants
import com.techglock.health.app.common.extension.openAnotherActivity
import com.techglock.health.app.common.utils.CleverTapHelper
import com.techglock.health.app.common.utils.PermissionUtil
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.FragmentHomeMainBinding
import com.techglock.health.app.home.common.HRAObservationModel
import com.techglock.health.app.home.viewmodel.DashboardViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentHomeMain : BaseFragment() {
    private lateinit var binding: FragmentHomeMainBinding
    private val viewModel: DashboardViewModel by lazy {
        ViewModelProvider(this)[DashboardViewModel::class.java]
    }
    private val permissionUtil = PermissionUtil
    private lateinit var fm: FragmentManager

    private val homeScreenFragment = HomeScreenFragment()

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeMainBinding.inflate(inflater, container, false)
        //binding.lifecycleOwner = viewLifecycleOwner
        initialise()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        when (childFragmentManager.findFragmentById(R.id.main_container)) {
            is HomeScreenFragment -> {
                (activity as HomeMainActivity).setNavigationId(R.id.page_1)
            }

            is FragmentTrackHealth -> {
                (activity as HomeMainActivity).setNavigationId(R.id.page_2)
            }

            is FragmentProfile -> {

                (activity as HomeMainActivity).setNavigationId(R.id.page_4)
            }
        }
    }

    private fun initialise() {
        fm = childFragmentManager
        val navigation: BottomNavigationView = binding.bottomNavigation
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        navigation.setOnNavigationItemReselectedListener { }
        fm.beginTransaction().replace(R.id.main_container, homeScreenFragment, "1").commit()
        (activity as HomeMainActivity).setBottomNavigationView(binding.bottomNavigation, fm)
        homeScreenFragment.setBottomNavigationView(binding.bottomNavigation, fm)

        navigation.background = null


        /*        binding.fab.setOnClickListener {
                    val permissionResult: Boolean = permissionUtil.checkAmahaWebviewPermissions(object : PermissionUtil.AppPermissionListener {
                        override fun isPermissionGranted(isGranted: Boolean) {
                            Utilities.printLogError("$isGranted")
                            if (isGranted) {
                                navigateToAmahaWebview("BOT_URL","Relief Bot")
                            }
                        }
                    }, requireContext())
                    if (permissionResult) {
                        navigateToAmahaWebview("BOT_URL","Relief Bot")
                    }
                }*/
    }


    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_1 -> {

                    fm.beginTransaction().replace(R.id.main_container, homeScreenFragment, "1")
                        .commit()

                    (activity as HomeMainActivity).setToolbarInfo(
                        0,
                        showAppLogo = true,

                        title = "",
                        showBg = true
                    )
                    return@OnNavigationItemSelectedListener true
                }

                R.id.page_2 -> {
                    fm.beginTransaction().replace(R.id.main_container, FragmentTrackHealth(), "2")
                        .commit()
                    (activity as HomeMainActivity).setToolbarInfo(
                        1,
                        showAppLogo = true,

                        title = resources.getString(R.string.TRACK)
                    )

                    return@OnNavigationItemSelectedListener true
                }

                R.id.page_5 -> {
                    //fm.beginTransaction().replace(R.id.main_container,FragmentChatBot(),"5").commit()
                    //(activity as HomeMainActivity).setToolbarInfo(2,showAppLogo = true,showSwitchProfileIcon = false,title = "")
                    CleverTapHelper.pushEvent(requireContext(), CleverTapConstants.SUD_POLICY)
                    if (viewModel.getOtpAuthenticatedStatus()) {
                        openAnotherActivity(destination = NavigationConstants.SUD_LIFE_POLICY)
                    } else {
                        openAnotherActivity(destination = NavigationConstants.SUD_LIFE_POLICY_AUTHENTICATION)
                    }
                    return@OnNavigationItemSelectedListener true
                }

                /*                R.id.page_3 -> {
                                    val permissionResult: Boolean = permissionUtil.checkAmahaWebviewPermissions(object : PermissionUtil.AppPermissionListener {
                                        override fun isPermissionGranted(isGranted: Boolean) {
                                            Utilities.printLogError("$isGranted")
                                            if (isGranted) {
                                                //fm.beginTransaction().replace(R.id.main_container,FragmentKnowledge(),"3").commit()
                                                //(activity as HomeMainActivity).setToolbarInfo(3,showAppLogo = false,false,title = resources.getString(R.string.MENTAL_WELLNESS),showBg = false)
                                                navigateToAmahaWebview("DASHBOARD_URL",resources.getString(R.string.MENTAL_WELLNESS))
                                            }
                                        }
                                    }, requireContext())
                                    if (permissionResult) {
                                        //fm.beginTransaction().replace(R.id.main_container,FragmentKnowledge(),"3").commit()
                                        //(activity as HomeMainActivity).setToolbarInfo(3,showAppLogo = false,false,title = resources.getString(R.string.MENTAL_WELLNESS),showBg = false)
                                        navigateToAmahaWebview("DASHBOARD_URL",resources.getString(R.string.MENTAL_WELLNESS))
                                    }
                                    return@OnNavigationItemSelectedListener true
                                }*/
                R.id.page_4 -> {
                    val permissionResult = permissionUtil.checkStoragePermission(object :
                        PermissionUtil.AppPermissionListener {
                        override fun isPermissionGranted(isGranted: Boolean) {
                            Utilities.printLogError("$isGranted")
                            if (isGranted) {
                                navigateToProfile()
                            }
                        }
                    }, requireContext())
                    if (permissionResult) {
                        navigateToProfile()
                    }
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    private fun navigateToAmahaWebview(moduleCode: String, title: String) {
        openAnotherActivity(destination = NavigationConstants.AMAHA_WEB_VIEW_SCREEN) {
            putString(Constants.MODULE_CODE, moduleCode)
            putString(Constants.TITLE, title)
        }
    }

    private fun navigateToProfile() {
        CleverTapHelper.pushEvent(requireContext(), CleverTapConstants.MY_PROFILE_SCREEN)
        fm.beginTransaction().replace(R.id.main_container, FragmentProfile(), "4").commit()
        (activity as HomeMainActivity).setToolbarInfo(
            4,
            true,
            title = resources.getString(R.string.PROFILE)
        )
    }

    object HRAData {
        var data: HRAObservationModel = HRAObservationModel()
    }


}