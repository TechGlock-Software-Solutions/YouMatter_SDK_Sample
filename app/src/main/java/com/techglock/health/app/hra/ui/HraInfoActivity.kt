package com.techglock.health.app.hra.ui

import android.content.Context
import android.content.Intent
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
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.ActivityHraInfoBinding
import com.techglock.health.app.hra.HraHomeActivity
import com.techglock.health.app.hra.viewmodel.HraViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HraInfoActivity : BaseActivity() {

    private val viewModel: HraViewModel by lazy {
        ViewModelProvider(this)[HraViewModel::class.java]
    }
    private lateinit var binding: ActivityHraInfoBinding
    private var myViewPagerAdapter: MyViewPagerAdapter? = null
    private var dots: Array<ImageView?> = arrayOf()
    private val slidingDotsCount = 2
    lateinit var layouts: IntArray

    override fun getViewModel(): BaseViewModel = viewModel
    override fun onCreateEvent(savedInstanceState: Bundle?) {
        binding = ActivityHraInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initialise()
        setClickable()
    }

    private fun initialise() {
        // add few more layouts if you want
        layouts = intArrayOf(
            R.layout.layout_hra_intro_one,
            R.layout.layout_hra_intro_two
        )

        // adding bottom dots
        addBottomDots()

        myViewPagerAdapter = MyViewPagerAdapter(this, layouts)
        binding.viewPager.adapter = myViewPagerAdapter
    }

    private fun setClickable() {

        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageSelected(position: Int) {
                Utilities.printLogError("SelectedPagePos--->$position")

                for (i in 0 until slidingDotsCount) {
                    dots[i]?.setImageDrawable(
                        ContextCompat.getDrawable(
                            binding.viewPager.context,
                            R.drawable.dot_light_blue
                        )
                    )
                }
                dots[position]?.setImageDrawable(
                    ContextCompat.getDrawable(
                        binding.viewPager.context,
                        R.drawable.dot_white
                    )
                )

                if (position == (layouts.size - 1)) {
                    binding.btnNext.text = resources.getString(R.string.DONE)
                } else {
                    binding.btnNext.text = resources.getString(R.string.NEXT)
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

        binding.btnNext.setOnClickListener {
            when (getCurrentScreen()) {
                (layouts.size - 2) -> {
                    scrollNextSlide()
                    binding.btnNext.text = resources.getString(R.string.DONE)
                }

                (layouts.size - 1) -> {
//                    openAnotherActivity(destination = NavigationConstants.HRA_HOME)
                    startActivity(Intent(this, HraHomeActivity::class.java))
                    finish()
                }

                else -> {
                    scrollNextSlide()
                    binding.btnNext.text = resources.getString(R.string.NEXT)
                }
            }
        }

    }

    fun getCurrentScreen(): Int {
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
            dots[i]?.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.dot_light_blue))
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(8, 0, 8, 0)
            binding.layoutDots.addView(dots[i], params)
        }
        dots[0]?.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.dot_white))
    }

    private fun getItem(i: Int): Int {
        return binding.viewPager.currentItem + i
    }

    /**
     * View pager adapter
     */
    class MyViewPagerAdapter(val context: Context, private val layouts: IntArray) : PagerAdapter() {
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
}