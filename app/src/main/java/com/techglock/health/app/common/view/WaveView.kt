package com.techglock.health.app.common.view

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PixelFormat
import android.graphics.Point
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.util.AttributeSet
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.annotation.Keep
import androidx.core.content.ContextCompat
import com.techglock.health.app.R
import com.techglock.health.app.common.utils.DensityUtil.dip2px
import com.techglock.health.app.common.utils.DensityUtil.px2dip
import kotlin.math.roundToInt

class WaveView : View {
    private val TAG = "WaveView"
    private var mContext: Context? = null
    private var mPaint: Paint? = null
    private var wavePaint: Paint? = null

    /**
     * The wave of the normal grain
     */
    private var wavePath: Path? = null

    /**
     * The wave of the rolling grain
     */
    private var shadPath: Path? = null

    //mode
    private var WAVE_COLOR = Color.parseColor("#00ceff") // Color for wave
    private var BG_COLOR = Color.WHITE // Color for view of background

    private var WAVE_BG = R.drawable.img_water_drop

    /**
     * the width and height for view  < width and height is 300 dpi by default >>
     */
    private var VIEW_WIDTH = 0f
    private var VIEW_HEIGHT = 0f
    private var VIEW_WIDTH_TMP = 0f
    private var VIEW_HEIGHT_TMP = 0f

    /**
     * the width and height for wave  < Width is half the width of view , Height is auto >>
     */
    private var WAVE_WIDTH = 0f
    private var WAVE_HEIGHT = 0f
    private var mode = MODE_DRAWABLE //default shape is circle

    /**
     * pointList : normal wave of  original collection point
     * shadpointList : rolling wave of  original collection point
     */
    private val pointList: MutableList<Point> = ArrayList()
    private val shadpointList: MutableList<Point> = ArrayList()

    /**
     * < Sign control variables >>
     */
    private var isInitPoint = true // Init original collection point
    private var isStartAnimation = false // The first time for start the flowingAnimation
    private var isDone = false // whether to end
    private var isMeasure = false // The first time for measure view
    private var isCompleteLayout = false //just action when drawing finish
    var isHasWindowFocus = true // is hasWindowFocus

    /**
     * < value >>
     */
    private var dy = 0f // height of the rise
    private var old_dy = 0f //height of the rise  ,often change
    private var sum_dy = 0f // defalut height
    private var beforDy = 0f //The last time the height of the rise
    private var dx = 0f // Distance for Horizontal-Moving < normal wave >
    private var shd_dx = 0f // Distance for Horizontal-Moving < rolling wave >
    private val runRatio = 1.5f
    private val isSlow = 0x01
    private val isNormal = 0x02
    private val isFast = 0x03
    private var speed = SPEED_NORMAL // default speed
    private var curSpeedMode = isNormal

    /**
     * < the progress for waveview >>
     */
    private var mProgress: Long = 0 // The current progress
    private var curProgress: Long =
        0 // The current progress , in order to deal with some logical work
    private var max: Long = 0 // The max progress
    private var progressRatio = 0f // ratio < result = progress / max >
    private var progressListener: WaveProgressListener? = null
    private var reiseAnimator: ValueAnimator? = null
    private var flowingAnimato: ObjectAnimator? = null

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        if (attrs != null) {
            //attars
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.WaveView)
            val bgColor = typedArray.getColor(R.styleable.WaveView_waveBgColor, BG_COLOR)
            val pColor = typedArray.getColor(R.styleable.WaveView_waveProgressColor, WAVE_COLOR)
            val aMax = typedArray.getInt(R.styleable.WaveView_waveMax, max.toInt())
            val aP = typedArray.getInteger(R.styleable.WaveView_waveProgress, mProgress.toInt())
            BG_COLOR = bgColor
            WAVE_COLOR = pColor
            max = aMax.toLong()
            mProgress = aP.toLong()
            typedArray.recycle()
        }
        VIEW_WIDTH = dip2px(context, 300f).toFloat()
        VIEW_HEIGHT = dip2px(context, 300f).toFloat()
        wavePath = Path()
        shadPath = Path()
        wavePath!!.fillType = Path.FillType.EVEN_ODD
        //shadPath.setFillType(Path.FillType.EVEN_ODD);
        mContext = context
        mPaint = Paint()
        mPaint!!.color = BG_COLOR
        mPaint!!.strokeWidth = 1f
        mPaint!!.style = Paint.Style.FILL
        mPaint!!.isAntiAlias = true
        wavePaint = Paint()
        wavePaint!!.color = BG_COLOR
        wavePaint!!.strokeWidth = 1f
        wavePaint!!.style = Paint.Style.FILL
        wavePaint!!.isAntiAlias = true
        //        wavePaint.setAlpha(50);
        this.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {

            override fun onGlobalLayout() {
                setSpeed(speed)
                if (isHasWindowFocus) {
                    isCompleteLayout = true
                    val cP = max - mProgress
                    if (max >= mProgress) {
                        progressRatio = mProgress / max.toFloat()
                        dy = updateDyData().toFloat()
                        viewTreeObserver.removeOnGlobalLayoutListener(this)
                        isMeasure = true
                    }
                }
                if (isHasWindowFocus) {
                }
                VIEW_HEIGHT_TMP = VIEW_HEIGHT
                VIEW_WIDTH_TMP = VIEW_WIDTH
            }
        })
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        //Utilities.printLog(TAG,"onMeasure " +isHasWindowFocus);
        if (!isMeasure) {
            getRealWidthMeasureSpec(widthMeasureSpec)
            getRealHeightMeasureSpec(heightMeasureSpec)
            if (VIEW_HEIGHT > VIEW_WIDTH) {
                VIEW_WIDTH = VIEW_HEIGHT
            } else {
                VIEW_HEIGHT = VIEW_WIDTH
            }
            setMeasuredDimension(VIEW_WIDTH.toInt(), VIEW_HEIGHT.toInt())
        }
        initPoint()
    }

    /**
     * Initialize the original wave arts collection point , including normal wave ,rolling wave
     */
    private fun initPoint() {
        if (isInitPoint) {
            isInitPoint = false
            pointList.clear()
            shadpointList.clear()
            WAVE_WIDTH = (VIEW_WIDTH / 2.5).toFloat()
            //            WAVE_HEIGHT = (float) (VIEW_HEIGHT / 50);
            WAVE_HEIGHT = VIEW_HEIGHT / waveHeight
            dy = VIEW_HEIGHT //Started from the bottom, when the height is rise, dy gradually reduce
            //How many points calculated maximum support
            val n = (VIEW_WIDTH / WAVE_WIDTH).roundToInt()
            //start point for normal wave
            var startX = 0
            for (i in 0 until 4 * n + 1) {
                val point = Point()
                point.y = dy.toInt()
                if (i == 0) {
                    point.x = startX
                } else {
                    startX += WAVE_WIDTH.toInt()
                    point.x = startX
                }
                pointList.add(point)
            }
            // start point for rolling wave
            startX = VIEW_WIDTH.toInt()
            for (i in 0 until 4 * n + 1) {
                val point = Point()
                point.y = dy.toInt()
                if (i == 0) {
                    point.x = startX
                } else {
                    startX -= WAVE_WIDTH.toInt()
                    point.x = startX
                }
                shadpointList.add(point)
            }

            //change speed base on view_width
            SPEED_NORMAL = (px2dip(mContext!!, VIEW_WIDTH) / 20).toFloat()
            SPEED_SLOW = SPEED_NORMAL / 2
            SPEED_FAST = SPEED_NORMAL * 2
            SPEED_NORMAL = if (SPEED_NORMAL == 0f) 1f else SPEED_NORMAL
            SPEED_SLOW = if (SPEED_SLOW == 0f) 0.5f else SPEED_SLOW
            SPEED_FAST = if (SPEED_FAST == 0f) 2f else SPEED_FAST
            speed = when (curSpeedMode) {
                isSlow -> {
                    SPEED_SLOW
                }

                isFast -> {
                    SPEED_FAST
                }

                else -> {
                    SPEED_NORMAL
                }
            }
        }
    }

    fun setProgressListener(progressListener: WaveProgressListener?) {
        this.progressListener = progressListener
        isDone = false
    }

    private val waveHeight: Int
        get() = when (speed) {
            SPEED_FAST -> {
                30
            }

            SPEED_SLOW -> {
                70
            }

            else -> {
                50
            }
        }

    fun setSpeed(speed: Float) {
        if (speed == SPEED_FAST || speed == SPEED_NORMAL || speed == SPEED_SLOW) {
            curSpeedMode = when (speed) {
                SPEED_FAST -> {
                    isFast
                }

                SPEED_SLOW -> {
                    isSlow
                }

                else -> {
                    isNormal
                }
            }
            this.speed = speed
            dx = 0f
            shd_dx = 0f
            //rerefreshPoints();
        }
    }

    fun setMode(mode: String) {
        this.mode = mode
    }

    fun setMax(max: Long) {
        this.max = max
        isDone = false
    }

    fun setbgColor(color: Int) {
        BG_COLOR = color
    }

    fun setWaveColor(color: Int) {
        WAVE_COLOR = color
    }

    fun setImageDrawable(img: Int) {
        WAVE_BG = img
    }

    fun setWaveProgress(prog: Long) {
        var progress = prog
        mPaint!!.color = BG_COLOR
        mPaint!!.alpha = 255
        isDone = false
        if (progress > max) {
            progress = if (this.mProgress < max) max else return
        }
        if (flowingAnimato == null) flowingAnimation()
        if (reiseAnimator != null && reiseAnimator!!.isRunning) {
            //reiseAnimator.cancel();
            reiseAnimator!!.end()
        }
        this.mProgress = progress
        if (progress == 0L) {
            resetWave()
        }
        if (!isCompleteLayout) {
            return
        }
        val cP = max - progress
        if (max >= progress) {
            progressRatio = cP / max.toFloat()
            updateProgress()
        }
    }

    var progress: Float
        get() = mProgress.toFloat()
        set(progress) {
            setWaveProgress(progress.toLong())
        }

    @Keep
    fun setProgressWithAnimation(progress: Double, duration: Int) {
        val objectAnimator = ObjectAnimator.ofFloat(this, "progress", progress.toFloat())
        objectAnimator.duration = duration.toLong()
        objectAnimator.interpolator = DecelerateInterpolator()
        objectAnimator.start()
    }

    /*    fun getProgress(): Long {
            return mProgress
        }*/

    fun getMax(): Long {
        return max
    }

    /**
     * reset point set
     * < When in onDraw need to measure the initialization point set>>
     */
    private fun rerefreshPoints() {
        pointList.clear()
        shadpointList.clear()
        WAVE_HEIGHT = VIEW_HEIGHT / waveHeight

        val n = (VIEW_WIDTH / WAVE_WIDTH).roundToInt()
        var startX = (-dx).toInt()
        for (i in 0 until 4 * n + 1) {
            val point = Point()
            point.y = dy.toInt()
            if (i == 0) {
                point.x = startX
            } else {
                startX += WAVE_WIDTH.toInt()
                point.x = startX
            }
            pointList.add(point)
        }
        startX = VIEW_WIDTH.toInt()
        for (i in 0 until 4 * n + 1) {
            val point = Point()
            point.y = dy.toInt()
            if (i == 0) {
                point.x = startX
            } else {
                startX -= WAVE_WIDTH.toInt()
                point.x = startX
            }
            shadpointList.add(point)
        }
    }

    private fun resetWave() {
        isDone = false
        dy = VIEW_HEIGHT
        beforDy = 0f
    }

    private fun updateDyData(): Int {

        if (sum_dy == 0f && isHasWindowFocus) {
            sum_dy = VIEW_HEIGHT
        }
        old_dy = dy
        val offsetDy = (sum_dy - sum_dy * progressRatio - beforDy).toInt()
        beforDy = sum_dy - sum_dy * progressRatio
        return offsetDy
    }

    /**
     * In a second riseAnimation when set the progress !!
     * <second by second execution of progress></second>>
     */
    private fun updateProgress() {
        riseAnimation()
    }

    private fun getRealWidthMeasureSpec(widthMeasureSpec: Int): Int {
        val mode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        when (mode) {
            MeasureSpec.AT_MOST -> {
                //            VIEW_WIDTH = widthSize;
            }

            MeasureSpec.EXACTLY -> {
                VIEW_WIDTH = widthSize.toFloat()
            }

            MeasureSpec.UNSPECIFIED -> {
                //            VIEW_WIDTH = VIEW_WIDTH_TMP;
            }
        }
        return VIEW_WIDTH.toInt()
    }

    private fun getRealHeightMeasureSpec(heightMeasureSpec: Int): Int {
        val mode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        when (mode) {
            MeasureSpec.AT_MOST -> {
                //VIEW_HEIGHT = heightSize;
            }

            MeasureSpec.EXACTLY -> {
                VIEW_HEIGHT = heightSize.toFloat()
            }

            MeasureSpec.UNSPECIFIED -> {
                //VIEW_HEIGHT = VIEW_HEIGHT_TMP;
            }
        }
        if (!isHasWindowFocus) {
            updateDyData()
        } else {
            dy = VIEW_HEIGHT
            old_dy = dy
            sum_dy = dy
        }
        return VIEW_HEIGHT.toInt()
    }

    override fun onDraw(canvas: Canvas) {
        //To prevent repeated drawing
        wavePath!!.reset()
        shadPath!!.reset()
        val radius = VIEW_WIDTH / 2f
        //val saveFlags: Int = Canvas.MATRIX_SAVE_FLAG or Canvas.CLIP_SAVE_FLAG or Canvas.HAS_ALPHA_LAYER_SAVE_FLAG or Canvas.FULL_COLOR_LAYER_SAVE_FLAG or Canvas.CLIP_TO_LAYER_SAVE_FLAG
        val saveFlags: Int = Canvas.ALL_SAVE_FLAG
        canvas.saveLayer(0f, 0f, VIEW_WIDTH, VIEW_HEIGHT, null, saveFlags)

        // set shape
        if (mode == MODE_DRAWABLE) {
            drawableToBitamp(canvas)
        } else if (mode == MODE_RECT) {
            canvas.drawRect(0f, 0f, VIEW_WIDTH, VIEW_HEIGHT, mPaint!!)
        } else {
            canvas.drawCircle(VIEW_WIDTH / 2f, VIEW_HEIGHT / 2f, radius, mPaint!!)
        }
        wavePaint!!.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP)
        if (!isDone) {
            // drawing normal wave
            wavePaint!!.color = WAVE_COLOR
            wavePaint!!.alpha = 100
            var end1 = 0f
            for (i in pointList.indices) {
                val j = i + 1
                if (pointList.size > i) {
                    val start1 = pointList[i].x.toFloat()
                    wavePath!!.moveTo(start1, dy) //+dy
                    if (j % 2 == 0 && j >= 2) {
                        end1 = start1
                        wavePath!!.quadTo(
                            start1 + WAVE_WIDTH / 2,
                            dy + WAVE_HEIGHT,
                            start1 + WAVE_WIDTH,
                            dy
                        ) //+dy
                    } else {
                        end1 = start1
                        wavePath!!.quadTo(
                            start1 + WAVE_WIDTH / 2,
                            dy - WAVE_HEIGHT,
                            start1 + WAVE_WIDTH,
                            dy
                        )
                    }
                }
            }
            if (end1 >= VIEW_WIDTH) {
                wavePath!!.lineTo(VIEW_WIDTH, VIEW_HEIGHT)
                wavePath!!.lineTo(0f, VIEW_HEIGHT)
                wavePath!!.lineTo(0f, dy)
                wavePath!!.close()
                canvas.drawPath(wavePath!!, wavePaint!!)
            }

            // drawing rolling wave
            wavePaint!!.alpha = 200
            for (i in shadpointList.indices) {
                val j = i + 1
                if (shadpointList.size > i) {
                    val start1 = shadpointList[i].x + shd_dx
                    shadPath!!.moveTo(start1, dy) //+dy
                    if (j % 2 == 0 && j >= 2) {
                        end1 = start1
                        shadPath!!.quadTo(
                            start1 - WAVE_WIDTH / 2,
                            (dy + WAVE_HEIGHT * runRatio), start1 - WAVE_WIDTH, dy
                        ) //+dy
                    } else {
                        end1 = start1
                        shadPath!!.quadTo(
                            start1 - WAVE_WIDTH / 2,
                            (dy - WAVE_HEIGHT * runRatio), start1 - WAVE_WIDTH, dy
                        )
                    }
                }
            }
            if (end1 <= -VIEW_WIDTH) {
                shadPath!!.lineTo(0f, VIEW_HEIGHT)
                shadPath!!.lineTo(VIEW_WIDTH, VIEW_HEIGHT)
                shadPath!!.lineTo(VIEW_WIDTH, dy)
                shadPath!!.close()
                canvas.drawPath(shadPath!!, wavePaint!!)
            }

            // xfer
            wavePaint!!.xfermode = null
            canvas.restore()
            //        super.onDraw(canvas);

            // display listener for activity or fragment
            if (progressListener != null) {
                if (!isDone && curProgress != mProgress) {
                    progressListener!!.onProgress(mProgress == max, mProgress, max)
                    curProgress = mProgress
                }
                if (mProgress == max) {
                    isDone = true
                    //dy = -10;//In order to complete fill finally effect
                    //resetWave();
                }
            }
            if (isDone) doneAnimation()
        }
    }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        isHasWindowFocus = hasWindowFocus
        //        Utilities.printLog("yuan"," onWindowFocusChanged " + hasWindowFocus);
        if (!isDone) {
            if (!isStartAnimation) {
                isStartAnimation = true
                flowingAnimation()
            }
            if (!hasWindowFocus) {
                if (flowingAnimato != null) flowingAnimato!!.cancel()
                if (reiseAnimator != null) reiseAnimator!!.end()
                isMeasure = false
            } else {
                if (flowingAnimato != null && !flowingAnimato!!.isRunning) {
                    flowingAnimation()
                }
                if (reiseAnimator != null && !reiseAnimator!!.isRunning) {
                    setWaveProgress(mProgress)
                }
            }
        } else {
//        Utilities.printLog("yuan"," onWindowFocusChanged " + isDone);
            if (isHasWindowFocus) {
                doneAnimation()
            }
        }
    }

    private fun flowingAnimation() {
        flowingAnimato = ObjectAnimator.ofFloat(this, "wave", 0f, 100f).setDuration(100)
        flowingAnimato!!.repeatCount = Animation.INFINITE
        flowingAnimato!!.addUpdateListener {
            dx += speed
            shd_dx += speed / 2 //Half the speed of the normal waves

            if (shd_dx >= WAVE_WIDTH * 2) {
                shd_dx = 0f
            }
            if (dx >= WAVE_WIDTH * 2) {
                dx = 0f
            }
            rerefreshPoints()
            postInvalidate()
        }
        flowingAnimato!!.start()
    }

    private fun riseAnimation() {
        if (!isHasWindowFocus) {
            return
        }
        isMeasure = true
        if (dy > 0) {
            val offset = updateDyData().toFloat()
            reiseAnimator = ValueAnimator.ofFloat(0f, offset).setDuration(500)
            reiseAnimator!!.interpolator = LinearInterpolator()
            reiseAnimator!!.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animator: Animator) {}
                override fun onAnimationEnd(animator: Animator) {
                    dy = sum_dy - beforDy
                }

                override fun onAnimationCancel(animator: Animator) {}
                override fun onAnimationRepeat(animator: Animator) {}
            })
            reiseAnimator!!.addUpdateListener { valueAnimator ->
                val m = valueAnimator.animatedValue as Float
                val s = old_dy - m
                dy = s
            }
            reiseAnimator!!.start()
        }
    }

    private fun doneAnimation() {
        if (reiseAnimator != null) {
            reiseAnimator!!.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    justDone()
                }

                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
            reiseAnimator!!.end()
        } else {
            justDone()
        }
    }

    private fun justDone() {
        mPaint!!.color = WAVE_COLOR
        //mPaint.setAlpha(200);
        if (flowingAnimato != null && flowingAnimato!!.isRunning) {
            flowingAnimato!!.end()
            flowingAnimato = null
        } else invalidate()
    }

    private fun drawableToBitamp(canvas: Canvas) {
        val drawable = ContextCompat.getDrawable(mContext!!, WAVE_BG)
        val w = VIEW_WIDTH.toInt()
        val h = VIEW_HEIGHT.toInt()
        val config =
            if (drawable!!.opacity != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565
        val bitmap = Bitmap.createBitmap(w, h, config)
        canvas.drawBitmap(bitmap, 0f, 0f, mPaint)
        drawable.setBounds(0, 0, w, h)
        drawable.draw(canvas)
    }

    interface WaveProgressListener {
        fun onProgress(isDone: Boolean, progress: Long, max: Long)
    }

    companion object {
        /**
         * There are three kinds of waveview shapes(mode), including circle、rect and drawable
         * -- Drawable shape , you need to have default drawable
         */
        const val MODE_CIRCLE = "circle"
        const val MODE_RECT = "rect"
        const val MODE_DRAWABLE = "drawable"

        /**
         * There are three kinds of waveview mode of speed , including slow、normal and fast
         */
        var SPEED_SLOW = 10f // Slow speed
        var SPEED_NORMAL = 30f // normal speed
        var SPEED_FAST = 40f // fast speed
    }
}