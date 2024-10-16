package com.techglock.health.app.home.ui.sudLifePolicy

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.lifecycle.ViewModelProvider
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseFragment
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.CleverTapConstants
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.extension.replaceFragment
import com.techglock.health.app.common.utils.CleverTapHelper
import com.techglock.health.app.common.utils.DateHelper
import com.techglock.health.app.common.utils.DefaultNotificationDialog
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.common.utils.showDialog
import com.techglock.health.app.databinding.FragmentSudLifePolicyDetailsBinding
import com.techglock.health.app.home.adapter.SudFundDetailsAdapter
import com.techglock.health.app.home.common.PolicyDataSingleton
import com.techglock.health.app.home.ui.HomeMainActivity
import com.techglock.health.app.home.viewmodel.SudLifePolicyViewModel
import com.techglock.health.app.repository.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SudLifePolicyDetailsFragment : BaseFragment(),
    DefaultNotificationDialog.OnDialogValueListener {

    private val viewModel: SudLifePolicyViewModel by lazy {
        ViewModelProvider(this)[SudLifePolicyViewModel::class.java]
    }
    private lateinit var binding: FragmentSudLifePolicyDetailsBinding

    var policyDataSingleton: PolicyDataSingleton? = null
    private var sudFundDetailsAdapter: SudFundDetailsAdapter? = null
    private var animation: LayoutAnimationController? = null
    private var isFundVisible = true
    var from = ""
    private var isLoaded = false

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            from = it.getString(Constants.FROM, "")!!
            Utilities.printLogError("from--->$from")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSudLifePolicyDetailsBinding.inflate(inflater, container, false)
        try {
            initialise()
            registerObserver()
            setClickable()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as HomeMainActivity).setToolbarInfo(
            5,
            showBottomNavigation = false,
            showAppLogo = false,
            title = resources.getString(R.string.TITLE_POLICY_DETAILS)
        )

    }

    @SuppressLint("SetTextI18n")
    private fun initialise() {
        CleverTapHelper.pushEvent(requireContext(), CleverTapConstants.POLICY_DETAILS_SCREEN)
        animation = AnimationUtils.loadLayoutAnimation(
            requireContext(), R.anim.layout_animation_slide_from_bottom
        )
        binding.cardFundDetails.visibility = View.GONE
        policyDataSingleton = PolicyDataSingleton.getInstance()!!

        binding.lblTotalFundValue.visibility = View.GONE
        binding.txtTotalFundValue.visibility = View.GONE

        sudFundDetailsAdapter = SudFundDetailsAdapter(requireContext())
        binding.rvFundDetails.layoutAnimation = animation
        binding.rvFundDetails.adapter = sudFundDetailsAdapter

        if (Utilities.isNullOrEmpty(policyDataSingleton!!.policyDetails.policyNo)) {
            viewModel.callPolicyDetailsByPolicyNumberApi(policyDataSingleton!!.kypDetails.policyNumber!!)
        } else {
            binding.txtPolicyStatus.text = policyDataSingleton!!.policyDetails.policyStatus
            binding.txtPolicyTerm.text = policyDataSingleton!!.policyDetails.policyTerm
        }
        isLoaded = false
        if (policyDataSingleton!!.fundDetails.isEmpty()) {
            viewModel.callSudGetFundDetailApi(policyDataSingleton!!.kypDetails.policyNumber!!)
        } else {
            sudFundDetailsAdapter!!.updateList(policyDataSingleton!!.fundDetails)
            binding.rvFundDetails.scheduleLayoutAnimation()
        }

        binding.txtPolicyName.text = policyDataSingleton!!.kypDetails.contractTypeDesc
        binding.txtPolicyHolderName.text =
            Utilities.convertStringToPascalCase(policyDataSingleton!!.kypDetails.dear!!)
                .replace(".", "")
        binding.txtRiskCommencementDate.text =
            DateHelper.formatDateValueInReadableFormat(policyDataSingleton!!.kypDetails.rCDDate!!)
        binding.txtPolicyNumber.text = policyDataSingleton!!.kypDetails.policyNumber
        binding.txtSumAssured.text =
            "${resources.getString(R.string.INDIAN_RUPEE)} ${policyDataSingleton!!.kypDetails.basicSumAssured!!}"
        //binding.txtPolicyType.text = policyDataSingleton!!.policyDetails.planName
        //binding.txtPolicyTerm.text = policyDataSingleton!!.policyDetails.policyTerm
        binding.txtPolicyPremiumPaymentTerm.text =
            policyDataSingleton!!.kypDetails.premiumPayingTerm
        binding.txtPolicyMaturityDate.text =
            DateHelper.formatDateValueInReadableFormat(policyDataSingleton!!.kypDetails.maturityDate!!)
        binding.txtNextPremiumDueDate.text =
            DateHelper.formatDateValueInReadableFormat(policyDataSingleton!!.kypDetails.nextPremiumDueDate!!)
        binding.txtPremiumFrequency.text =
            "${resources.getString(R.string.INDIAN_RUPEE)} ${policyDataSingleton!!.kypDetails.totalPremiumAmount}/${policyDataSingleton!!.kypDetails.modeOfPremiumPayment}"
    }

    @SuppressLint("SetTextI18n")
    private fun registerObserver() {
        viewModel.policyDetailsByPolicyNumber.observe(viewLifecycleOwner) {
            if (it.status == Resource.Status.SUCCESS) {
                if (it.data!!.result.isNotEmpty()) {
                    policyDataSingleton!!.policyDetails = it.data.result[0]
                    binding.txtPolicyStatus.text = policyDataSingleton!!.policyDetails.policyStatus
                    binding.txtPolicyTerm.text = policyDataSingleton!!.policyDetails.policyTerm
                    if (Utilities.getPayPremiumButtonStateByStatus(policyDataSingleton!!.policyDetails.policyStatus!!)) {
                        binding.btnPayPremium.visibility = View.VISIBLE
                    } else {
                        binding.btnPayPremium.visibility = View.INVISIBLE
                    }
                }
            }
        }

        viewModel.sudFundDetails.observe(viewLifecycleOwner) {
            if (it.status == Resource.Status.SUCCESS) {
                if (it.data!!.result.status == "1") {
                    policyDataSingleton!!.fundDetails = it.data.result.fundDetails.toMutableList()
                    binding.cardFundDetails.visibility = View.VISIBLE
                    binding.rvFundDetails.layoutAnimation = animation
                    sudFundDetailsAdapter!!.updateList(it.data.result.fundDetails)
                    binding.rvFundDetails.scheduleLayoutAnimation()/*                    binding.txtTotalFundValue.text = "${resources.getString(R.string.INDIAN_RUPEE)} ${Utilities.formatNumberWithComma(Utilities.roundOffPrecision(it.fundDetails[0].totalFundValue!!.toDouble(),2))}"
                                        binding.txtUnitsAllocated.text = Utilities.roundOffPrecision(it.fundDetails[0].unitsAllocated!!.toDouble(),2).toString()
                                        binding.txtNav.text = Utilities.roundOffPrecision(it.fundDetails[0].nav!!.toDouble(),2).toString()
                                        binding.txtNavDate.text = it.fundDetails[0].navDate*/
                    binding.lblTotalFundValue.visibility = View.VISIBLE
                    binding.txtTotalFundValue.visibility = View.VISIBLE
                    binding.txtTotalFundValue.text =
                        "${resources.getString(R.string.INDIAN_RUPEE)} ${
                            Utilities.formatNumberDecimalWithComma(
                                Utilities.roundOffPrecisionToTwoDecimalPlaces(
                                    it.data.result.fundDetails[0].totalFundValue!!
                                )
                            )
                        }"
                } else {
                    binding.lblTotalFundValue.visibility = View.GONE
                    binding.txtTotalFundValue.visibility = View.GONE
                }
            }
        }
        viewModel.sudGetKypTemplate.observe(viewLifecycleOwner) {
            if (it.status == Resource.Status.SUCCESS) {
                if (it.data!!.result.success) {
                    showDialog(
                        listener = this@SudLifePolicyDetailsFragment,
                        //imgResource = R.drawable.img_tick_green,
                        imgResource = R.raw.success_tick, isLottieImg = true,
                        //message = resources.getString(R.string.KYP_SUCCESS_MSG),
                        //leftText = resources.getString(R.string.CANCEL),
                        rightText = resources.getString(R.string.OK), showLeftBtn = false
                    )
                }
            }
        }
        viewModel.sudGetPayPremium.observe(viewLifecycleOwner) {
            if (it.status == Resource.Status.SUCCESS) {
                Utilities.printLogError("isLoaded--->$isLoaded")
                if (isLoaded) {
                    val data = HashMap<String, Any>()
                    data[CleverTapConstants.POLICY_NUMBER] =
                        policyDataSingleton!!.kypDetails.policyNumber!!
                    CleverTapHelper.pushEventWithProperties(
                        requireContext(), CleverTapConstants.POLICY_PAY_YOUR_PREMIUM, data
                    )
                    if (!Utilities.isNullOrEmpty(it.data!!.result.shortlUrl)) {
                        Utilities.redirectToChrome(it.data.result.shortlUrl!!, requireContext())
                    } else {
                        Utilities.toastMessageShort(
                            requireContext(), it.data.result.responseMesage!!
                        )
                    }
                }
            }
        }

        viewModel.sudKypPdf.observe(viewLifecycleOwner) { }
    }

    private fun setClickable() {
        binding.btnPayPremium.setOnClickListener {
            //Utilities.toastMessageShort(requireContext(),resources.getString(R.string.COMING_SOON))
            isLoaded = true
            viewModel.callSudGetPayPremiumApi(policyDataSingleton!!.kypDetails.policyNumber!!)
        }
        binding.imgOptions.setOnClickListener {
            showBottomSheet()
        }
        binding.imgDropDown.setOnClickListener {
            if (isFundVisible) {
                binding.imgDropDown.setImageResource(R.drawable.ic_up_arrow)
                binding.rvFundDetails.visibility = View.GONE
            } else {
                binding.imgDropDown.setImageResource(R.drawable.ic_down_arrow)
                binding.rvFundDetails.visibility = View.VISIBLE
                binding.rvFundDetails.scheduleLayoutAnimation()
            }
            isFundVisible = !isFundVisible
        }
    }

    private fun showBottomSheet() {
        val modalBottomSheet =
            ModalBottomSheetSudPolicy(object : ModalBottomSheetSudPolicy.OnItemClickListener {
                override fun onClick(code: String) {
                    when (code) {
                        Constants.PREMIUM_RECEIPT -> {
//                            findNavController().navigate(R.id.action_sudLifePolicyDetailsFragment_to_viewReceiptFragment)
                            (activity as HomeMainActivity).replaceFragment(ViewReceiptFragment())
                        }

                        Constants.KNOW_YOUR_POLICY -> {
                            showKypPasswordDialog()
                            //viewModel.callSudKypPdfApi(policyDataSingleton!!.kypDetails.policyNumber!!)
                            /*                        viewModel.callSudGetKypTemplateApi(policyDataSingleton!!.kypDetails.policyNumber!!)
                                                    showDialog(
                                                        listener = this@SudLifePolicyDetailsFragment,
                                                        message = resources.getString(R.string.KYP_PROGRESS_MSG),
                                                        rightText = resources.getString(R.string.OK),
                                                        showLeftBtn = false)*/
                        }
                    }
                }

            })
        modalBottomSheet.show(
            requireActivity().supportFragmentManager, ModalBottomSheetSudPolicy.TAG
        )
    }

    private fun showKypPasswordDialog() {
        val dialogData = DefaultNotificationDialog.DialogData()
        dialogData.title = ""
        dialogData.message = resources.getString(R.string.KYP_PASSWORD_FORMAT)
        dialogData.btnLeftName = resources.getString(R.string.CANCEL)
        dialogData.btnRightName = resources.getString(R.string.DOWNLOAD)
        val defaultNotificationDialog = DefaultNotificationDialog(
            requireContext(),
            object : DefaultNotificationDialog.OnDialogValueListener {
                override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) {
                    if (isButtonRight) {
                        viewModel.callSudKypPdfApi(policyDataSingleton!!.kypDetails.policyNumber!!)
                    }
                }
            },
            dialogData
        )
        defaultNotificationDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        defaultNotificationDialog.show()
    }

    override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) {}

}