package com.techglock.health.app.home.ui.sudLifePolicy

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseFragment
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.CleverTapConstants
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.extension.replaceFragment
import com.techglock.health.app.common.taptargetview.TapTarget
import com.techglock.health.app.common.taptargetview.TapTargetSequence
import com.techglock.health.app.common.utils.CleverTapHelper
import com.techglock.health.app.common.utils.DefaultNotificationDialog
import com.techglock.health.app.common.utils.LocaleHelper
import com.techglock.health.app.common.utils.PermissionUtil
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.FragmentSudLifePolicyDashboardBinding
import com.techglock.health.app.home.adapter.PolicyDownloadsAdapter
import com.techglock.health.app.home.adapter.SlidingSudBannerAdapter
import com.techglock.health.app.home.adapter.SudPolicyListAdapter
import com.techglock.health.app.home.common.DataHandler
import com.techglock.health.app.home.common.PolicyDataSingleton
import com.techglock.health.app.home.ui.HomeMainActivity
import com.techglock.health.app.home.ui.WebViews.FeedUpdateActivity
import com.techglock.health.app.home.viewmodel.SudLifePolicyViewModel
import com.techglock.health.app.model.sudLifePolicy.PolicyProductsModel
import com.techglock.health.app.model.sudLifePolicy.SudKYPModel
import com.techglock.health.app.model.sudLifePolicy.SudKYPModel.KYP
import com.techglock.health.app.repository.utils.Resource
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import java.util.Timer
import java.util.TimerTask

@AndroidEntryPoint
class SudLifePolicyDashboardFragment : BaseFragment(), SudPolicyListAdapter.OnPolicyClickListener,
    DefaultNotificationDialog.OnDialogValueListener,
    CreditLifeDialog.OnCreditLifeDialogListener,
    PolicyDownloadsAdapter.OnDownloadClickListener, HomeMainActivity.OnHelpClickListener {

    private val viewModel: SudLifePolicyViewModel by lazy {
        ViewModelProvider(this)[SudLifePolicyViewModel::class.java]
    }
    private lateinit var binding: FragmentSudLifePolicyDashboardBinding
    var tapTarget: TapTargetSequence? = null

    private var from = ""
    var policyListSizeFinal = 0
    var policyListSize = 0
    private var policyDownloadsAdapter: PolicyDownloadsAdapter? = null
    private var sudPolicyListAdapter: SudPolicyListAdapter? = null
    val sudPolicyList: MutableList<SudKYPModel.KYP> = mutableListOf()
    var policyDataSingleton: PolicyDataSingleton? = null
    private val permissionUtil = PermissionUtil
    private var currentPage = 0
    private var slidingImageDots: Array<ImageView?> = arrayOf()
    private var slidingDotsCount = 0
    private var creditLifeDialog: CreditLifeDialog? = null
    private var animation: LayoutAnimationController? = null

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            from = it.getString(Constants.FROM, "")!!
            Utilities.printLogError("from--->$from")
        }
        when (from) {
            Constants.PROFILE -> {
                val bundle = Bundle()
                bundle.putString(Constants.FROM, Constants.PROFILE)
//                findNavController().navigate(R.id.action_sudLifePolicyDashboardFragment_to_sudLifePolicyDetailsFragment,bundle)
                (activity as HomeMainActivity).replaceFragment(
                    SudLifePolicyDetailsFragment(),
                    bundle,
                    frameId = R.id.main_container
                )
            }
        }
        /*      // callback to Handle back button event
              val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
                  override fun handleOnBackPressed() {
                      policyDataSingleton!!.clearData()

                      (activity as HomeMainActivity).onBackPressedDispatcher.onBackPressed()
      //                requireActivity().finish()
                  }
              }
              requireActivity().onBackPressedDispatcher.addCallback(this, callback)*/
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as HomeMainActivity).setOnHelpClickListener(this)


        (activity as HomeMainActivity).setToolbarInfo(
            5,
            showAppLogo = false,
            showImgHelp = true,
            title = resources.getString(R.string.TITLE_POLICY_DASHBOARD)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSudLifePolicyDashboardBinding.inflate(inflater, container, false)
        try {
            policyDataSingleton = PolicyDataSingleton.getInstance()!!
            initialise()
            setClickable()
            //registerObserver()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        //playTutorial()
        if (viewModel.isFirstTimePolicyVisit()) {
            playTutorialPolicy()
            viewModel.setFirstTimePolicyVisitFlag(false)
        }
    }

    private fun initialise() {
        //CleverTapHelper.pushEvent(requireContext(), CleverTapConstants.POLICY_DASHBAORD_SCREEN)

        val view = Utilities.preferenceUtils.getPreference(Constants.POLICY_VIEW,"")
        Utilities.printLogError("view--->$view")
        if ( !Utilities.isNullOrEmpty(view)
            && view.equals(Constants.FEATURE_CODE_YOUR_POLICY_DOWNLOADS,ignoreCase = true) ) {
            Handler(Looper.getMainLooper()).postDelayed({
                binding.tabLayout.getTabAt(1)!!.select()
                showDownloadsView()
            }, 500)
        }
        Utilities.preferenceUtils.clearPreference(Constants.POLICY_VIEW)

        animation = AnimationUtils.loadLayoutAnimation(requireContext(), R.anim.layout_animation_slide_from_bottom)
        val customerCareNumber =
            "<u><a><B><font color='#000000'>" + resources.getString(R.string.CUSTOMER_CARE_NO) + "</font></B></a></u>"
        binding.txtSudCustomerCare.text =
            Html.fromHtml(resources.getString(R.string.CUSTOMER_CARE) + " " + customerCareNumber)

        val customerCareEmail =
            "<u><a><B><font color='#000000'>" + resources.getString(R.string.CUSTOMER_CARE_EMAIL) + "</font></B></a></u>"
        binding.txtSudCustomerEmail.text =
            Html.fromHtml(resources.getString(R.string.EMAIL_ID) + " " + customerCareEmail)

        startShimmer()
        //setUpSlidingViewPager(DataHandler(requireContext()).getSudBannersPolicySectionList())

        policyDownloadsAdapter = PolicyDownloadsAdapter(requireContext(), this)
        binding.rvPolicyDownload.adapter = policyDownloadsAdapter
        policyDownloadsAdapter!!.updateList(DataHandler(requireContext()).getPolicyDownloadsList())

        sudPolicyListAdapter = SudPolicyListAdapter(requireContext(), this)
        binding.rvDashboardPolicy.adapter = sudPolicyListAdapter

        if (policyDataSingleton!!.productsList.isEmpty()) {
            startProductsShimmer()
            viewModel.callPolicyProductsApi(this)
        } else {
            setUpSlidingViewPager(policyDataSingleton!!.productsList)
        }

        if (policyDataSingleton!!.policyList.isEmpty()) {
            viewModel.callSudPolicyByMobileNumberApi(this)
            registerObserverPolicyList()
        } else {
            notifyList(policyDataSingleton!!.policyList)
        }
        registerObserverPMJJBY()
        viewModel.policyProducts.observe(viewLifecycleOwner) { }
    }

    private fun playTutorialPolicy() {
        tapTarget = TapTargetSequence(requireActivity())
        tapTarget?.targets(
            TapTarget.forView(binding.imgSettings, resources.getString(R.string.TUTORIAL_SETTINGS), resources.getString(R.string.TUTORIAL_SETTINGS_DESC))
                .targetRadius(25)
                .setConfiguration(requireContext()),

            TapTarget.forView(binding.tabLayout.getTabAt(0)!!.view, resources.getString(R.string.TUTORIAL_POLICIES), resources.getString(R.string.TUTORIAL_POLICIES_DESC))
                .targetRadius(50)
                .setConfiguration(requireContext()),

            TapTarget.forView(binding.tabLayout.getTabAt(1)!!.view, resources.getString(R.string.TUTORIAL_DOWNLOADS), resources.getString(R.string.TUTORIAL_DOWNLOADS_DESC))
                .targetRadius(50)
                .setConfiguration(requireContext()),

            )?.listener(object : TapTargetSequence.Listener {
            // This listener will tell us when interesting(tm) events happen in regards
            // to the sequence
            override fun onSequenceFinish() {
                tapTarget?.cancel()
            }

            override fun onSequenceStep(lastTarget: TapTarget?, targetClicked: Boolean) {

            }

            override fun onSequenceCanceled(lastTarget: TapTarget?) {
                Utilities.printLogError("Sequence Cancelled")
            }

        })?.start()

    }

    private fun setClickable() {

        binding.tabLayout.addOnTabSelectedListener( object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                Utilities.printLogError("TabPosition--->${tab.position}")
                when(tab.position) {
                    0 -> {
                        showPolicyView()
                    }
                    1 -> {
                        showDownloadsView()
                    }
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        binding.imgSettings.setOnClickListener {
            showBottomSheet()
        }

        binding.txtSudCustomerCare.setOnClickListener {
            callCustomerCare()
        }

        binding.txtSudCustomerEmail.setOnClickListener {
            Utilities.openEmailClientIntent(requireContext(), Constants.SUD_LIFE_CUSTOMER_CARE_EMAIL)
        }

        binding.btnCustomerPortal.setOnClickListener {
            CleverTapHelper.pushEvent(requireContext(),CleverTapConstants.CUSTOMER_PORTAL_LINK)
            val intent = Intent(requireContext(), FeedUpdateActivity::class.java)
            intent.putExtra(Constants.FROM, "POLICY")
            intent.putExtra(Constants.TITLE, "SUD Life Policy")
            intent.putExtra(Constants.WEB_URL, Constants.SUD_LIFE_CUSTOMER_PORTAL_URL)
            startActivity(intent)
        }

        val localResource = LocaleHelper.getLocalizedResources(requireContext(), Locale(LocaleHelper.getLanguage(requireContext())))!!

        binding.infoTV.text=localResource.getString(R.string.FOR_DETAILED_INFORMATION_ABOUT_YOUR_POLICY_PLEASE_CLICK_HERE)

/*        binding.infoTV.setSpanString(
            binding.infoTV.checkString(),
            //if(LocaleHelper.getLanguage(requireContext())=="en") 50 else if(LocaleHelper.getLanguage(requireContext())=="hi") 53 else 58,
            if(LocaleHelper.getLanguage(requireContext())=="en") 60 else if(LocaleHelper.getLanguage(requireContext())=="hi") 65 else 60,
            showBold = true,
            isUnderlineText = true,
            color = R.color.blue) {
            CleverTapHelper.pushEvent(requireContext(),CleverTapConstants.CUSTOMER_PORTAL_LINK)
            val intent = Intent(requireContext(),FeedUpdateActivity::class.java)
            intent.putExtra(Constants.FROM, "POLICY")
            intent.putExtra(Constants.TITLE, "SUD Life Policy")
            intent.putExtra(Constants.WEB_URL,"https://customer.sudlife.in")
            startActivity(intent)
        }*/



        binding.fabWhatsApp.setOnClickListener {
            launchWhatsApp()
        }

    }

    fun showPolicyView() {
        binding.layoutPolicies.visibility = View.VISIBLE
        binding.layoutDownloads.visibility = View.GONE
        binding.rvDashboardPolicy.layoutAnimation = animation
        binding.rvDashboardPolicy.scheduleLayoutAnimation()
    }

    fun showDownloadsView() {
        binding.layoutPolicies.visibility = View.GONE
        binding.layoutDownloads.visibility = View.VISIBLE
        binding.rvPolicyDownload.layoutAnimation = animation
        binding.rvPolicyDownload.scheduleLayoutAnimation()
    }

    private fun callCustomerCare() {
        CleverTapHelper.pushEvent(requireContext(),CleverTapConstants.POLICY_SUD_CUSTOMER_CARE)
        Utilities.openPhoneDialerWithNumber(requireContext(),Constants.SUD_LIFE_TOLL_FREE_NUMBER)
    }

    private fun launchWhatsApp() {
        CleverTapHelper.pushEvent(requireContext(),CleverTapConstants.POLICY_SUD_WHATS_APP_BOT)
        if ( Utilities.appInstalledOrNot("com.whatsapp",requireContext()) ) {
            Utilities.openWhatsApp(requireContext(),"${Constants.SUD_LIFE_WHATS_APP_BOT_URL}?text=Hi")
        } else {
            Utilities.toastMessageShort(requireContext(),resources.getString(R.string.ERROR_WHATS_NOT_INSTALLED))
        }
    }

    private fun registerObserverPolicyList() {
        viewModel.sudPolicyByMobileNumber.observe(viewLifecycleOwner) {
            if (it.status == Resource.Status.SUCCESS) {
                policyListSize = 0
                sudPolicyList.clear()
                sudPolicyListAdapter!!.updateList(sudPolicyList)
            }
        }
    }

    private fun registerObserverPMJJBY() {
        viewModel.sudPMJJBYCoiBase.observe(viewLifecycleOwner) { }
        viewModel.sudGroupCoiApi.observe(viewLifecycleOwner) { }
    }

    fun setUpSlidingViewPager(campaignList: MutableList<PolicyProductsModel.PolicyProducts>) {
        try {
            //val campaignDetailsList = campaignList.filter { it.campaignID == "1" }.toMutableList()
            //campaignList.sortBy { it.displayOrder }
            slidingDotsCount = campaignList.size
            slidingImageDots = arrayOfNulls(slidingDotsCount)
            val landingImagesAdapter =
                SlidingSudBannerAdapter(requireActivity(), slidingDotsCount, campaignList)

            binding.slidingViewPager.apply {
                adapter = landingImagesAdapter
                registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        for (i in 0 until slidingDotsCount) {
                            slidingImageDots[i]?.setImageDrawable(ContextCompat.getDrawable(binding.slidingViewPager.context, R.drawable.dot_non_active))
                        }
                        slidingImageDots[position]?.setImageDrawable(ContextCompat.getDrawable(binding.slidingViewPager.context, R.drawable.dot_active))
                    }
                })
            }

            if (slidingDotsCount > 1) {
                for (i in 0 until slidingDotsCount) {
                    slidingImageDots[i] = ImageView(binding.slidingViewPager.context)
                    slidingImageDots[i]?.setImageDrawable(ContextCompat.getDrawable(binding.slidingViewPager.context, R.drawable.dot_non_active))
                    val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    params.setMargins(8, 0, 8, 0)
                    binding.sliderDots.addView(slidingImageDots[i], params)
                }

                slidingImageDots[0]?.setImageDrawable(ContextCompat.getDrawable(binding.slidingViewPager.context, R.drawable.dot_active))

                val handler = Handler(Looper.getMainLooper())
                val update = Runnable {
                    if (currentPage == slidingDotsCount) {
                        currentPage = 0
                    }
                    binding.slidingViewPager.setCurrentItem(currentPage++, true)
                }
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        handler.post(update)
                    }
                }, 3000, 3000)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun addPolicyInList(policy: KYP) {
        if (!sudPolicyList.contains(policy)) {
            sudPolicyList.add(policy)
            policyListSize++
        }
        Utilities.printLogError("policyListSizeFinal--->$policyListSizeFinal")
        Utilities.printLogError("policyListSize--->$policyListSize")
        if (policyListSizeFinal == policyListSize) {
            notifyList(sudPolicyList)
            policyDataSingleton!!.policyList = sudPolicyList
        }
    }

    fun notifyList(list: MutableList<KYP>) {
        list.sortBy { it.policyNumber }
        sudPolicyListAdapter!!.updateList(list)
        stopShimmer()
    }

    override fun onPolicyClick(kypDetails: KYP) {
        policyDataSingleton!!.kypDetails = kypDetails
//        findNavController().navigate(R.id.action_sudLifePolicyDashboardFragment_to_sudLifePolicyDetailsFragment)
        (activity as HomeMainActivity).replaceFragment(SudLifePolicyDetailsFragment(), frameId = R.id.main_container)
    }

    fun showNoDataView() {
        binding.rvDashboardPolicy.visibility = View.GONE
        binding.layoutNoPolicy.visibility = View.VISIBLE
        stopShimmer()
    }

    private fun startShimmer() {
        binding.layoutDashboardPolicyShimmer.startShimmer()
        binding.layoutDashboardPolicyShimmer.visibility = View.VISIBLE
    }

    private fun stopShimmer() {
        binding.layoutDashboardPolicyShimmer.stopShimmer()
        binding.layoutDashboardPolicyShimmer.visibility = View.GONE
    }

    private fun showPMJJBYPasswordDialog() {
        val dialogData = DefaultNotificationDialog.DialogData()
        dialogData.title = ""
        dialogData.message = "<a>" + resources.getString(R.string.PMJJBY_PASSWORD_FORMAT1) + " <a><B><font color='#000000'>" + resources.getString(
                R.string.PMJJBY_PASSWORD_FORMAT2
            ) + "</font></B></a> " + resources.getString(R.string.PMJJBY_PASSWORD_FORMAT3) + "<br/>" + resources.getString(
                R.string.PMJJBY_PASSWORD_FORMAT4
            ) + "</a>"
        dialogData.btnLeftName = resources.getString(R.string.CANCEL)
        dialogData.btnRightName = resources.getString(R.string.DOWNLOAD)
        val defaultNotificationDialog = DefaultNotificationDialog(
            requireContext(),
            object : DefaultNotificationDialog.OnDialogValueListener {
                override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) {
                    if (isButtonRight) {
                        viewModel.callSudGetPMJJBYCoiBaseApi(this@SudLifePolicyDashboardFragment)
                    }
                }
            }, dialogData)
        defaultNotificationDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        defaultNotificationDialog.show()
    }

    private fun showBottomSheet() {
        val modalBottomSheet = ModalBottomSheetSudPolicySettings(object :
            ModalBottomSheetSudPolicySettings.OnSettingsOptionClickListener {
            override fun onSettingsOptionClick(code: String) {
                when (code) {
                    Constants.CHANGE_POLICY_MOBILE_NUMBER -> {
//                        openAnotherActivity(destination = NavigationConstants.SUD_LIFE_POLICY_AUTHENTICATION)
                        (activity as HomeMainActivity).replaceFragment(FragmentSudPolicyAuthentication(), frameId = R.id.main_container)
                    }

                    Constants.CALL_CUSTOMER_CARE -> {
                        callCustomerCare()
                    }

                    Constants.WHATS_APP_BOT -> {
                        launchWhatsApp()
                    }
                }
            }

        }, viewModel)
        modalBottomSheet.show(requireActivity().supportFragmentManager, ModalBottomSheetSudPolicySettings.TAG)
    }

    private fun showCreditLifeDialog() {
        creditLifeDialog = CreditLifeDialog(requireContext(), this)
        creditLifeDialog!!.show()
    }

    override fun onCreditLifeDialogSelection(parameter: String, value: String) {
        Utilities.printLogError("$parameter--->$value")
        Utilities.toastMessageShort(requireContext(), "$parameter--->$value")
    }

    override fun onDownloadClick(policy: DataHandler.SudPolicyDownloadModel) {
        Utilities.printLogError("PolicyCode--->${policy.code}")
        when (policy.code) {
            Constants.POLICY_PMJJBY -> {
                val permissionResult: Boolean = permissionUtil.checkStoragePermission(object :
                    PermissionUtil.AppPermissionListener {
                    override fun isPermissionGranted(isGranted: Boolean) {
                        Utilities.printLogError("$isGranted")
                        if (isGranted) {
                            showPMJJBYPasswordDialog()
                        }
                    }
                }, requireContext())
                if (permissionResult) {
                    showPMJJBYPasswordDialog()
                }
            }

            Constants.POLICY_GROUP_COI -> {
                val permissionResult: Boolean = permissionUtil.checkStoragePermission(object :
                    PermissionUtil.AppPermissionListener {
                    override fun isPermissionGranted(isGranted: Boolean) {
                        Utilities.printLogError("$isGranted")
                        if (isGranted) {
                            viewModel.callSudGroupCoiApi(this@SudLifePolicyDashboardFragment)
                        }
                    }
                }, requireContext())
                if (permissionResult) {
                    viewModel.callSudGroupCoiApi(this@SudLifePolicyDashboardFragment)
                }
            }

            Constants.POLICY_CREDIT_LIFE -> {
                val permissionResult: Boolean = permissionUtil.checkStoragePermission(object :
                    PermissionUtil.AppPermissionListener {
                    override fun isPermissionGranted(isGranted: Boolean) {
                        Utilities.printLogError("$isGranted")
                        if (isGranted) {
                            showCreditLifeDialog()
                        }
                    }
                }, requireContext())
                if (permissionResult) {
                    showCreditLifeDialog()
                }
            }
        }
    }

    override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) {}
    override fun onHelpClick() {
        playTutorialPolicy()
    }

    private fun startProductsShimmer() {
        binding.layoutProductsShimmer.startShimmer()
        binding.layoutProductsShimmer.visibility = View.VISIBLE
        binding.cardBanner.visibility = View.GONE
    }

    fun stopProductsShimmer() {
        binding.layoutProductsShimmer.stopShimmer()
        binding.layoutProductsShimmer.visibility = View.GONE
        binding.cardBanner.visibility = View.VISIBLE
    }
}