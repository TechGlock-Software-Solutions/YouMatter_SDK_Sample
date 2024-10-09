package com.techglock.health.app.home.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.techglock.health.app.R
import com.techglock.health.app.common.constants.CleverTapConstants
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.extension.changeColor
import com.techglock.health.app.common.extension.checkString
import com.techglock.health.app.common.extension.setSpanString
import com.techglock.health.app.common.utils.CleverTapHelper
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.FragmentSlidingSudBannerDashboardBinding
import com.techglock.health.app.home.common.OnPolicyBannerListener
import com.techglock.health.app.home.ui.WebViews.FeedUpdateActivity
import com.techglock.health.app.home.viewmodel.DashboardViewModel
import com.techglock.health.app.model.sudLifePolicy.PolicyProductsModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SlidingSudBannerDashboardFragment(
    private val listener: OnPolicyBannerListener,
    private val campaignDetailsList: MutableList<PolicyProductsModel.PolicyProducts>,
    val position: Int) : Fragment(R.layout.fragment_sliding_sud_banner_dashboard) {

    private lateinit var binding: FragmentSlidingSudBannerDashboardBinding
    private val viewModel: DashboardViewModel by lazy {
        ViewModelProvider(this)[DashboardViewModel::class.java]
    }
    private var banner = PolicyProductsModel.PolicyProducts()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSlidingSudBannerDashboardBinding.inflate(inflater, container, false)
        initialise()
        setClickable()
        return binding.root
    }

    fun initialise() {
        banner = campaignDetailsList[position]
        Utilities.printData("banner", banner, true)
        if (!Utilities.isNullOrEmpty(banner.productImageURL)) {
            Utilities.loadImageUrl(banner.productImageURL, binding.imgBanner)
        }

        if (!Utilities.isNullOrEmpty(banner.disclaimerText)) {
            binding.disclaimerTV.text = "${banner.disclaimerText} Disclaimer"
            binding.disclaimerTV.setSpanString(
                binding.disclaimerTV.checkString(),
                banner.disclaimerText.length,
                showBold = true,
                isUnderlineText = true,
                color = R.color.blue
            ) {
                listener.onPolicyBannerClick(Constants.CLOSE_DIALOG)
                val intent = Intent(requireContext(), FeedUpdateActivity::class.java)
                intent.putExtra(Constants.TITLE, "Disclaimer")
                intent.putExtra(Constants.WEB_URL, banner.disclaimerUrl)
                startActivity(intent)
            }
        }
    }

    fun setClickable() {

        binding.disclaimerCB.setOnCheckedChangeListener { _, _ ->
            binding.clickHereToKnowTV.changeColor(binding.disclaimerCB.isChecked)
        }

        binding.clickHereToKnowTV.setOnClickListener {
            if (binding.disclaimerCB.isChecked) {
                dashboardBannerClick()
                listener.onPolicyBannerClick(Constants.CLICK_TO_KNOW_MORE)
            }
        }

        binding.shareBanner.setOnClickListener {
            Utilities.printData("PolicyProduct", banner, true)
            dashboardBannerShare()
            listener.onPolicyBannerClick(Constants.SHARE_BANNER)
        }

    }

    private fun dashboardBannerClick() {
        Utilities.printData("PolicyProduct", banner, true)
        val url = banner.productRedirectionURL
        val data = HashMap<String, Any>()
        data[CleverTapConstants.PRODUCT_CODE] = banner.productCode
        data[CleverTapConstants.USER_TYPE] = Utilities.getEmployeeType()
        data[CleverTapConstants.PHONE] = Utilities.getUserPhoneNumber()
        when (Utilities.getEmployeeType()) {
            Constants.SUD_LIFE -> {
                data[CleverTapConstants.EMPLOYEE_ID] = Utilities.getEmployeeID()
            }
        }
        /*when(banner.productCode) {
            Constants.CENTURION -> {
                url = Utilities.getCenturionRedirectionUrlBasedOnEmployeeTypeFinal(banner.productRedirectionURL?:"",
                    Utilities.getEmployeeType(), Utilities.getUserPhoneNumber(), Utilities.getEmployeeID())
                data[CleverTapConstants.USER_TYPE] = Utilities.getEmployeeType()
                data[CleverTapConstants.PHONE] = Utilities.getUserPhoneNumber()
                when (Utilities.getEmployeeType()) {
                    Constants.SUD_LIFE -> {
                        data[CleverTapConstants.EMPLOYEE_ID] = Utilities.getEmployeeID()
                    }
                }
                CleverTapHelper.pushEventWithProperties(requireContext(),CleverTapConstants.POLICY_CENTURION_BANNER, data)
            }
            Constants.SMART_HEALTH_PRODUCT -> {
                url = banner.productRedirectionURL
                data[CleverTapConstants.USER_TYPE] = Utilities.getEmployeeType()
                data[CleverTapConstants.PHONE] = Utilities.getUserPhoneNumber()

                when (Utilities.getEmployeeType()) {
                    Constants.SUD_LIFE -> {
                        data[CleverTapConstants.EMPLOYEE_ID] = Utilities.getEmployeeID()
                    }
                }
                CleverTapHelper.pushEventWithProperties(requireContext(),CleverTapConstants.POLICY_SMART_HEALTH_PRODUCT_BANNER,data)
            }
        }*/
        CleverTapHelper.pushEventWithProperties(requireContext(),CleverTapConstants.POLICY_BANNER, data)
        Utilities.printLogError("RedirectionUrl--->${url}")
        if (!Utilities.isNullOrEmpty(url)) {
            Utilities.redirectToChrome(url, requireContext())
        }
    }

    private fun dashboardBannerShare() {
        when (banner.productCode) {
            Constants.CENTURION -> CleverTapHelper.pushEvent(requireContext(), CleverTapConstants.SHARE_CENTURION_BANNER)
            Constants.SMART_HEALTH_PRODUCT -> CleverTapHelper.pushEvent(requireContext(), CleverTapConstants.SHARE_SMART_HEALTH_PRODUCT_BANNER)
        }
        if (!Utilities.isNullOrEmpty(banner.productShareURL)) {
            viewModel.shareBannerWithFriends(requireContext(), banner)
        }
    }

}