package com.techglock.health.app.security.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseActivity
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.NavigationConstants
import com.techglock.health.app.common.constants.PreferenceConstants
import com.techglock.health.app.common.extension.openAnotherActivity
import com.techglock.health.app.common.utils.LocaleHelper
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.ActivityAppIntroductionBinding
import com.techglock.health.app.model.home.LanguageModel
import com.techglock.health.app.security.ui_dialog.DialogLanguage
import com.techglock.health.app.security.viewmodel.StartupViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AppIntroductionActivity : BaseActivity(), DialogLanguage.OnLanguageClickListener {

    private val viewModel: StartupViewModel by lazy {
        ViewModelProvider(this)[StartupViewModel::class.java]
    }
    private lateinit var binding: ActivityAppIntroductionBinding

    private var myViewPagerAdapter: AppIntroViewPagerAdapter? = null
    private var dots: Array<ImageView?> = arrayOf()
    private val slidingDotsCount = 4
    private lateinit var layouts: IntArray

    private var dialogLanguage: DialogLanguage? = null

    override fun getViewModel(): BaseViewModel = viewModel

    @SuppressLint("ResourceType")
    override fun onCreateEvent(savedInstanceState: Bundle?) {
        binding = ActivityAppIntroductionBinding.inflate(layoutInflater)
        updateStatusBarColor(R.drawable.gradient_intro, false)
        setContentView(binding.root)
        initialise()
        setClickable()
    }

    private fun initialise() {
        // add few more layouts if you want
        layouts = intArrayOf(
            R.layout.intro_app_slide_1,
            R.layout.intro_app_slide_2,
            R.layout.intro_app_slide_3,
            R.layout.intro_app_slide_4
        )

        // adding bottom dots
        addBottomDots()

        myViewPagerAdapter = AppIntroViewPagerAdapter(this, layouts)
        binding.viewPager.adapter = myViewPagerAdapter
        //binding.viewPager.addOnPageChangeListener(viewPagerPageChangeListener)
    }

    private fun setClickable() {

        if (LocaleHelper.getLanguage(this) == "hi") {
            binding.toolBarView.txtLanguage.text = resources.getString(R.string.HINDI)
        } else {
            binding.toolBarView.txtLanguage.text = resources.getString(R.string.ENGLISH)
        }

        binding.toolBarView.tabLanguage.setOnClickListener {
            showLanguageSelectionDialog()
        }

        binding.toolBarView.txtSkip.setOnClickListener {
            navigateToLogin()
        }

        binding.btnNext.setOnClickListener {
            navigateToLogin()
        }

        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageSelected(position: Int) {
                Utilities.printLogError("SelectedPagePos--->$position")
                for (i in 0 until slidingDotsCount) {
                    dots[i]?.setImageDrawable(
                        ContextCompat.getDrawable(
                            binding.viewPager.context,
                            R.drawable.dot_non_active
                        )
                    )
                }
                dots[position]?.setImageDrawable(
                    ContextCompat.getDrawable(
                        binding.viewPager.context,
                        R.drawable.dot_active
                    )
                )
                if (position == (layouts.size - 1)) {
                    binding.toolBarView.txtSkip.visibility = View.GONE
                    binding.btnNext.visibility = View.VISIBLE
                    binding.imgNext.visibility = View.GONE
                } else {
                    binding.toolBarView.txtSkip.visibility = View.VISIBLE
                    binding.btnNext.visibility = View.GONE
                    binding.imgNext.visibility = View.VISIBLE
                }
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageScrollStateChanged(state: Int) {}

        })

        binding.imgNext.setOnClickListener {
            when (getCurrentScreen()) {
                (layouts.size - 2) -> {
                    scrollNextSlide()
                    //binding.btnNext.text = resources.getString(R.string.TAKE_ME_TO_APP)
                }

                (layouts.size - 1) -> {
                    navigateToLogin()
                }

                else -> {
                    scrollNextSlide()
                    //binding.btnNext.text = resources.getString(R.string.NEXT)
                }
            }
        }

    }

    private fun navigateToLogin() {
        if (Utilities.getBooleanPreference(PreferenceConstants.IS_DARWINBOX_DETAILS_AVAILABLE)) {
            viewModel.setFirstTimeUserFlag(false)
            openAnotherActivity(destination = NavigationConstants.SPLASH_SCREEN, clearTop = true)
        } else {
            openAnotherActivity(destination = NavigationConstants.LOGIN)
            finish()
        }
    }

    private fun showLanguageSelectionDialog() {
        dialogLanguage = DialogLanguage(this, this)
        dialogLanguage!!.show()
    }

    private fun getCurrentScreen(): Int {
        return binding.viewPager.currentItem
    }

    private fun scrollNextSlide() {
        binding.viewPager.setCurrentItem(getCurrentScreen() + 1, true)
    }

    private fun addBottomDots() {
        dots = arrayOfNulls(slidingDotsCount)
        binding.layoutDots.removeAllViews()
        for (i in 0 until slidingDotsCount) {
            dots[i] = ImageView(this)
            dots[i]?.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.dot_non_active))
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(8, 0, 8, 0)
            binding.layoutDots.addView(dots[i], params)
        }
        dots[0]?.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.dot_active))
    }

    private fun getItem(i: Int): Int {
        return binding.viewPager.currentItem + i
    }

    /**
     * View pager adapter
     */
    class AppIntroViewPagerAdapter(val context: Context, private val layouts: IntArray) :
        PagerAdapter() {
        //private var layoutInflater: LayoutInflater? = null
        override fun instantiateItem(container: ViewGroup, position: Int): Any {
//            layoutInflater = LayoutInflater.from(context)
            val view: View =
                LayoutInflater.from(context).inflate(layouts[position], container, false)
            container.addView(view)
            return view
        }

        override fun getCount(): Int {
            return layouts.size
        }

        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return view === obj
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            val view = `object` as View
            container.removeView(view)
        }
    }

    override fun onLanguageSelection(data: LanguageModel) {
        Utilities.printData("LanguageModel", data, true)
        Utilities.changeLanguage(data.languageCode, this@AppIntroductionActivity)
//        Utilities.logCleverTapChangeLanguage(data.languageCode, this@AppIntroductionActivity)
        recreate()
        binding.toolBarView.txtLanguage.text =
            Utilities.getLanguageNameConverted(data.languageCode, this@AppIntroductionActivity)
    }

}