package com.tbx.base.utils

import com.google.android.material.appbar.AppBarLayout
import kotlin.math.abs


/**
 * @author Created by TrinhBX.
 * Mail: trinhbx196@gmail.com
 * Phone: +08 988324622
 * @since Date: 27/05/2024
 **/

abstract class AppBarStateChangeListener : AppBarLayout.OnOffsetChangedListener {
    enum class State {
        EXPANDED, COLLAPSED, SCROLL, IDLE
    }

    private var mCurrentState = State.SCROLL

    private var lastOffset = 0


    /**
     * 20s update a time
     */
    override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
        mCurrentState = if (verticalOffset == 0) {
            if (mCurrentState != State.EXPANDED) {
                onStateChanged(appBarLayout, State.EXPANDED)
            }
            State.EXPANDED
        } else if (abs(verticalOffset) >= appBarLayout.totalScrollRange) {
            if (mCurrentState != State.COLLAPSED) {
                onStateChanged(appBarLayout, State.COLLAPSED)
            }
            State.COLLAPSED
        } else {
            if (lastOffset != abs(verticalOffset)) {
                lastOffset = abs(verticalOffset)
                val hPercent =
                    ((appBarLayout.totalScrollRange.toFloat() - abs(verticalOffset)) / appBarLayout.totalScrollRange) * 100f
                onScrolling(appBarLayout, verticalOffset, hPercent.toInt())
                onStateChanged(appBarLayout, State.SCROLL)
                State.SCROLL
            } else {
                if (mCurrentState != State.IDLE) {
                    onStateChanged(appBarLayout, State.IDLE)
                }
                State.IDLE
            }
        }
    }

    abstract fun onStateChanged(
        appBarLayout: AppBarLayout,
        state: State
    )

    abstract fun onScrolling(appBarLayout: AppBarLayout, verticalOffset: Int, heightPercent: Int)
}