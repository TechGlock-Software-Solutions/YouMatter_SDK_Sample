package com.techglock.health.app.home.common.extensions

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.techglock.health.app.R
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.databinding.DialogBannerAdsBinding
import com.techglock.health.app.home.adapter.SlidingSudBannerDashboardAdapter
import com.techglock.health.app.home.ui.HomeScreenFragment
import com.techglock.health.app.model.sudLifePolicy.PolicyProductsModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Objects
import java.util.Timer
import java.util.TimerTask


@SuppressLint("SetTextI18n")
fun HomeScreenFragment.showBannerDialog(
    campaignList: MutableList<PolicyProductsModel.PolicyProducts>,
    handleClick: (positiveClick: Int) -> Unit = { _ -> }
): Dialog {

    val customDialog = Dialog(this.requireContext())
    val dialogBinding = DataBindingUtil.inflate<DialogBannerAdsBinding>(
        LayoutInflater.from(this.requireContext()),
        R.layout.dialog_banner_ads,
        null,
        false
    )
    customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    customDialog.setContentView(dialogBinding.root)
    Objects.requireNonNull<Window>(customDialog.window)
        .setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    customDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    customDialog.setCancelable(false)

    val lpr = WindowManager.LayoutParams()
    lpr.copyFrom(customDialog.window!!.attributes)
    lpr.width = WindowManager.LayoutParams.MATCH_PARENT
    lpr.height = WindowManager.LayoutParams.WRAP_CONTENT
//    lpr.windowAnimations = R.style.bottomStyle
    lpr.gravity = Gravity.CENTER
    customDialog.window!!.attributes = lpr

    dialogBinding.closeIV.visibility = View.GONE

    this.requireActivity().lifecycleScope.launch(Dispatchers.Main) {
        delay(3000)
        dialogBinding.closeIV.visibility = View.VISIBLE
    }

    setUpSlidingViewPagerPopUp(campaignList, dialogBinding, this)

    dialogBinding.closeIV.setOnClickListener {
        customDialog.dismiss()
        handleClick(Constants.CLOSE_DIALOG)
    }

    customDialog.show()
    return customDialog
}

fun setUpSlidingViewPagerPopUp(
    campaignList: MutableList<PolicyProductsModel.PolicyProducts>,
    binding: DialogBannerAdsBinding,
    fragment: HomeScreenFragment
) {
    try {
        val slidingImageDots: Array<ImageView?>
        var currentPage = 0
        val slidingDotsCount: Int = campaignList.size
        slidingImageDots = arrayOfNulls(slidingDotsCount)
        val landingImagesAdapter = SlidingSudBannerDashboardAdapter(
            fragment.requireActivity(),
            slidingDotsCount,
            campaignList,
            fragment
        )

        binding.slidingViewPagerPopUp.apply {
            adapter = landingImagesAdapter
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    for (i in 0 until slidingDotsCount) {
                        slidingImageDots[i]?.setImageDrawable(
                            ContextCompat.getDrawable(
                                binding.slidingViewPagerPopUp.context,
                                R.drawable.dot_non_active
                            )
                        )
                    }
                    slidingImageDots[position]?.setImageDrawable(
                        ContextCompat.getDrawable(
                            binding.slidingViewPagerPopUp.context,
                            R.drawable.dot_active
                        )
                    )
                }
            })
        }

        if (slidingDotsCount > 1) {
            binding.sliderDotsPopUp.visibility = View.VISIBLE
            for (i in 0 until slidingDotsCount) {
                slidingImageDots[i] = ImageView(binding.slidingViewPagerPopUp.context)
                slidingImageDots[i]?.setImageDrawable(
                    ContextCompat.getDrawable(
                        binding.slidingViewPagerPopUp.context,
                        R.drawable.dot_non_active
                    )
                )
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(8, 0, 8, 0)
                binding.sliderDotsPopUp.addView(slidingImageDots[i], params)
            }

            slidingImageDots[0]?.setImageDrawable(
                ContextCompat.getDrawable(
                    binding.slidingViewPagerPopUp.context,
                    R.drawable.dot_active
                )
            )

            val handler = Handler(Looper.getMainLooper())
            val update = Runnable {
                if (currentPage == slidingDotsCount) {
                    currentPage = 0
                }
                binding.slidingViewPagerPopUp.setCurrentItem(currentPage++, true)
            }
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    handler.post(update)
                }
            }, 3000, 3000)
        } else {
            binding.sliderDotsPopUp.visibility = View.GONE
        }

    } catch (e: Exception) {
        e.printStackTrace()
    }
}



