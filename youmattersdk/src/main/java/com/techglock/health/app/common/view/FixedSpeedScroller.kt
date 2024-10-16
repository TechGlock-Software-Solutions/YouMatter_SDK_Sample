package com.techglock.health.app.common.view

import android.content.Context
import android.view.animation.Interpolator

import android.widget.Scroller

/**
 * <pre>
 *     author : jake
 *     time   : 2018/11/20
 * function : Control ViewPager sliding speed
 *     version: 1.0
 * </pre>
 *
 * Override the startScroll method, ignoring the passed duration value
 */
class FixedSpeedScroller : Scroller {

    var mDuration = 600

    constructor (context: Context) : super(context)

    constructor (context: Context, interpolator: Interpolator) : super(context, interpolator)

    constructor (context: Context, interpolator: Interpolator, flywheel: Boolean) : super(
        context,
        interpolator,
        flywheel
    )

    override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int) {
        super.startScroll(startX, startY, dx, dy, mDuration)
    }

    override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int, duration: Int) {
        super.startScroll(startX, startY, dx, dy, mDuration)
    }


    fun getScrollDuration(): Int {
        return mDuration
    }

    fun setScrollDuration(duration: Int) {
        mDuration = duration
    }
}