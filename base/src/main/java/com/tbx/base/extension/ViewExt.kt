package com.tbx.base.extension

import android.animation.Animator
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.os.SystemClock
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.ViewOutlineProvider
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.textfield.TextInputEditText
import kotlin.math.min


/**
 * Created by Trinh BX on 01/02/2023.
 * Mail: trinhbx196@gmail.com.
 * Phone: +084 988 324 622.
 */
fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.setRadius(radius: Int) {
    val mRadius = dpToPx(radius)
    setRadiusPx(mRadius)
}

fun View.setRadius(radius: Float) {
    val mRadius = dpToPx(radius)
    setRadiusPx(mRadius)
}

fun View.setRadiusPx(radius: Int) {
    outlineProvider = object : ViewOutlineProvider() {
        override fun getOutline(view: View?, outline: Outline?) {
            if (view != null) {
                outline?.setRoundRect(0, 0, view.width, view.height, radius.toFloat())
            }
        }
    }
    clipToOutline = true
}

fun View.setRadiusPx(radius: Float) {
    outlineProvider = object : ViewOutlineProvider() {
        override fun getOutline(view: View?, outline: Outline?) {
            if (view != null) {
                outline?.setRoundRect(0, 0, view.width, view.height, radius)
            }
        }
    }
    clipToOutline = true
}


fun View.setRoundedHalfSize() {
    outlineProvider = object : ViewOutlineProvider() {
        override fun getOutline(view: View?, outline: Outline?) {
            if (view != null) {
                val size = min(view.width, view.height)
                outline?.setRoundRect(0, 0, view.width, view.height, size / 2f)
            }
        }
    }
    clipToOutline = true
}

fun View.setRadiusPercent(percent: Float) {
    if (percent <= 0) return
    outlineProvider = object : ViewOutlineProvider() {
        override fun getOutline(view: View?, outline: Outline?) {
            if (view != null) {
                val size = min(view.width, view.height)
                outline?.setRoundRect(0, 0, view.width, view.height, percent * size)
            }
        }
    }
    clipToOutline = true
}


fun View.setWidth(width: Int) {
    layoutParams.apply {
        this.width = width
        requestLayout()
    }
}

fun View.setHeight(height: Int) {
    layoutParams.apply {
        this.height = height
        requestLayout()
    }
}

fun View.setPaddingHorizontal(padding: Int) {
    setPadding(padding, paddingTop, padding, paddingBottom)
}

fun View.setPaddingVertical(padding: Int) {
    setPadding(paddingLeft, padding, paddingRight, padding)
}

fun pxToDp(px: Int): Int {
    return (px / Resources.getSystem().displayMetrics.density).toInt()
}

fun pxToDp(px: Float): Int {
    return (px / Resources.getSystem().displayMetrics.density).toInt()
}

fun dpToPx(dp: Int): Int {
    return (dp * Resources.getSystem().displayMetrics.density).toInt()
}

fun dpToPx(dp: Float): Float {
    return (dp * Resources.getSystem().displayMetrics.density)
}

fun spToPx(sp: Float): Float {
    return (sp * Resources.getSystem().displayMetrics.scaledDensity)
}

fun Context.spToPx(sp: Int): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        sp.toFloat(),
        Resources.getSystem().displayMetrics
    ).toInt()
}

fun Context.spToPx(sp: Float): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        sp,
        Resources.getSystem().displayMetrics
    ).toInt().toFloat()
}

fun Context.pxToSp(px: Float): Float {
    return px / resources.displayMetrics.scaledDensity
}


fun View.setOnSingleClickListener(listener: (View) -> Unit) {
    setOnClickListener(object : OnSingleClickListener() {
        override fun onSingleClick(v: View) {
            listener(v)
        }
    })
}

abstract class OnSingleClickListener : View.OnClickListener {
    companion object {
        private const val MIN_CLICK_INTERVAL: Long = 300
    }

    private var mLastClickTime: Long = 0
    abstract fun onSingleClick(v: View)
    override fun onClick(v: View) {
        val currentClickTime: Long = SystemClock.uptimeMillis()
        val elapsedTime = currentClickTime - mLastClickTime
        mLastClickTime = currentClickTime

        if (elapsedTime <= MIN_CLICK_INTERVAL) return
        onSingleClick(v)
    }
}

fun TextInputEditText.handleFocus() {
    imeOptions = EditorInfo.IME_ACTION_DONE
    setOnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            clearFocus()
        }
        false
    }
    setOnFocusChangeListener { _, hasFocus ->
        if (!hasFocus) {
            val inputManager: InputMethodManager =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }
}

fun TextInputEditText.setScrollMultiLines() {
    setOnTouchListener(View.OnTouchListener { v, event ->
        if (hasFocus()) {
            v.parent.requestDisallowInterceptTouchEvent(true)
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_SCROLL -> {
                    v.parent.requestDisallowInterceptTouchEvent(false)
                    return@OnTouchListener true
                }
            }
        }
        false
    })
}

fun View.isKeyboardVisible(): Boolean {
    val insets = ViewCompat.getRootWindowInsets(this)
    return insets?.isVisible(WindowInsetsCompat.Type.ime()) ?: false
}

fun TextView.changeSelectedGradientColor(isSelected: Boolean) {
    if (isSelected) {
        val textShader: Shader = LinearGradient(
            0f, 0f, width.toFloat(), textSize, intArrayOf(
                Color.parseColor("#FFC200"),
                Color.parseColor("#FA0026")
            ), null, Shader.TileMode.CLAMP
        )
        paint.shader = textShader
    } else {
        paint.shader = null
    }
}

fun TextView.setValueDateTodo() {
    val date = "<font color='red'>FRI</font> 23"
    setTextHtml(date)
}

fun View.loadBitmap(): Bitmap? {
    return try {
        val newW = measuredWidth
        val newH = measuredHeight
        val b = Bitmap.createBitmap(
            newW,
            newH,
            Bitmap.Config.ARGB_8888
        )
        val c = Canvas(b)
        layout(0, 0, newW, newH)
        draw(c)
        b
    } catch (e: Exception) {
        null
    }

}

fun Activity.getWidthScreen(): Int {
    val displayMetrics = DisplayMetrics()
    windowManager
        .defaultDisplay
        .getMetrics(displayMetrics)

    return displayMetrics.widthPixels
}

fun View.setAlphaWithSize(size: Int) {
    alpha = if (size > 0) {
        1f
    } else {
        0.6f
    }
}

fun Context.screenWidth(): Int {
    val displayMetrics: DisplayMetrics = resources.displayMetrics
    return displayMetrics.widthPixels
}

fun Context.screenHeight(): Int {
    val displayMetrics: DisplayMetrics = resources.displayMetrics
    return displayMetrics.heightPixels
}

fun ViewPager2.setCurrentItemWithDuration(
    item: Int,
    duration: Long,
    interpolator: TimeInterpolator = AccelerateDecelerateInterpolator(),
    pagePxWidth: Int = width, // Default value taken from getWidth() from ViewPager2 view
    onFinished: (() -> Unit)? = null
) {
    val pxToDrag: Int = pagePxWidth * (item - currentItem)
    val animator = ValueAnimator.ofInt(0, pxToDrag)
    var previousValue = 0
    animator.addUpdateListener { valueAnimator ->
        val currentValue = valueAnimator.animatedValue as Int
        val currentPxToDrag = (currentValue - previousValue).toFloat()
        fakeDragBy(-currentPxToDrag)
        previousValue = currentValue
    }
    animator.addListener(object : Animator.AnimatorListener {
        override fun onAnimationStart(animation: Animator) {
            beginFakeDrag()
        }

        override fun onAnimationEnd(animation: Animator) {
            if (endFakeDrag()){
                onFinished?.invoke()
            }
        }

        override fun onAnimationCancel(animation: Animator) { /* Ignored */
        }

        override fun onAnimationRepeat(animation: Animator) { /* Ignored */
        }
    })
    animator.interpolator = interpolator
    animator.duration = duration
    animator.start()
}
