package com.techglock.health.app.home.ui.sudLifePolicy

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.techglock.health.app.R
import com.techglock.health.app.common.constants.CleverTapConstants
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.utils.CleverTapHelper
import com.techglock.health.app.common.utils.DefaultNotificationDialog
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.FragmentSlidingSudBannerBinding
import com.techglock.health.app.home.viewmodel.DashboardViewModel
import com.techglock.health.app.model.sudLifePolicy.PolicyProductsModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SlidingSudBannerFragment @Inject constructor(
    private val campaignDetailsList: MutableList<PolicyProductsModel.PolicyProducts>,
    val position: Int
) : Fragment(R.layout.fragment_sliding_sud_banner) {

    private lateinit var binding: FragmentSlidingSudBannerBinding
    private val viewModel: DashboardViewModel by lazy {
        ViewModelProvider(this)[DashboardViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSlidingSudBannerBinding.inflate(inflater, container, false)
        initialise()
        return binding.root
    }

    fun initialise() {
        if (!Utilities.isNullOrEmpty(campaignDetailsList[position].productImageURL)) {
            Utilities.loadImageUrl(
                campaignDetailsList[position].productImageURL,
                binding.slidingImage
            )
        } else {
            when (campaignDetailsList[position].productCode) {
                Constants.SMART_HEALTH_PRODUCT -> {
                    binding.slidingImage.setImageResource(R.drawable.banner_smart_healthcare)
                }

                Constants.CENTURION -> {
                    binding.slidingImage.setImageResource(R.drawable.banner_centurion)
                }
            }
        }

        binding.slidingImage.setOnClickListener {
            when (campaignDetailsList[position].productCode) {
                Constants.CENTURY_ROYALE, Constants.CENTURY_GOLD, Constants.PROTECT_SHIELD_PLUS -> {
                    viewModel.callAddFeatureAccessLogApi(
                        campaignDetailsList[position].productCode,
                        campaignDetailsList[position].productName,
                        "SudBanner",
                        campaignDetailsList[position].productRedirectionURL
                    )
                }
            }
            redirectBasedOnBanner()
        }

        viewModel.addFeatureAccessLog.observe(viewLifecycleOwner) {}
    }

    private fun redirectBasedOnBanner() {
        when (campaignDetailsList[position].productCode) {
            Constants.SMART_HEALTH_PRODUCT -> {
                /* val data = HashMap<String, Any>()
                 data[CleverTapConstants.USER_TYPE] = Utilities.getEmployeeType()
                 CleverTapHelper.pushEventWithProperties(requireContext(),getEventByBannerCode(campaignDetailsList[position].productCode),data)
                 Utilities.printLogError("RedirectionUrl--->${campaignDetailsList[position].productRedirectionURL}")
                 Utilities.redirectToChrome(campaignDetailsList[position].productRedirectionURL,requireContext())*/
            }

            Constants.CENTURION -> {
                /*val data = HashMap<String, Any>()
                data[CleverTapConstants.USER_TYPE] = Utilities.getEmployeeType()
                CleverTapHelper.pushEventWithProperties(requireContext(),getEventByBannerCode(campaignDetailsList[position].bannerCode),data)
                Utilities.printLogError("RedirectionUrl--->${campaignDetailsList[position].redirectLink}")
                Utilities.redirectToChrome(campaignDetailsList[position].redirectLink,requireContext())*/
            }

            else -> showConsentDialog()
        }
    }

    private fun showConsentDialog() {
        val dialogData = DefaultNotificationDialog.DialogData()
        dialogData.message = resources.getString(R.string.POLICY_BANNER_CONSENT_MSG)
        dialogData.btnRightName = resources.getString(R.string.YES)
        dialogData.btnLeftName = resources.getString(R.string.NO)
        dialogData.showDismiss = false
        val defaultNotificationDialog = DefaultNotificationDialog(
            context,
            object : DefaultNotificationDialog.OnDialogValueListener {
                override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) {
                    val data = HashMap<String, Any>()
                    if (isButtonRight) {
                        data[CleverTapConstants.USER_CONSENT] = CleverTapConstants.YES
                        Utilities.printLogError("RedirectionUrl--->${campaignDetailsList[position].productRedirectionURL}")
                        Utilities.redirectToChrome(
                            campaignDetailsList[position].productRedirectionURL,
                            requireContext()
                        )
                    }
                    if (isButtonLeft) {
                        data[CleverTapConstants.USER_CONSENT] = CleverTapConstants.NO
                    }
                    CleverTapHelper.pushEventWithProperties(
                        requireContext(),
                        getEventByBannerCode(campaignDetailsList[position].productCode),
                        data
                    )
                }
            }, dialogData
        )
        defaultNotificationDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        defaultNotificationDialog.show()
    }

    private fun getEventByBannerCode(bannerCode: String): String {
        var event = ""
        when (bannerCode) {
            Constants.CENTURY_ROYALE -> event = CleverTapConstants.POLICY_CENTURY_ROYALE_BANNER
            Constants.CENTURY_GOLD -> event = CleverTapConstants.POLICY_CENTURY_GOLD_BANNER
            Constants.PROTECT_SHIELD_PLUS -> event =
                CleverTapConstants.POLICY_PROTECT_SHIELD_PLUS_BANNER

            Constants.SMART_HEALTH_PRODUCT -> event =
                CleverTapConstants.POLICY_SMART_HEALTH_PRODUCT_BANNER
//            Constants.CENTURION -> event = CleverTapConstants.POLICY_CENTURION_BANNER
        }
        return event
    }

}
