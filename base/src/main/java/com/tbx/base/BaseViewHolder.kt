package com.tbx.base

import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.core.view.marginBottom
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.core.view.marginTop
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(item: T)
    protected fun View.setScaleMargin(ratio:Float){
        if(ratio<0f || ratio>1f) return
        (layoutParams as? RelativeLayout.LayoutParams)
            ?: (layoutParams as? LinearLayout.LayoutParams)
            ?: (layoutParams as? FrameLayout.LayoutParams)?.let {
                it.marginStart = (marginStart * ratio).toInt()
                it.marginEnd = (marginEnd * ratio).toInt()
                it.topMargin = (marginTop * ratio).toInt()
                it.bottomMargin = (marginBottom * ratio).toInt()
            }
    }

    protected fun View.setScalePadding(ratio:Float){
        if(ratio<0f || ratio>1f) return
        val padLeft = (paddingStart * ratio).toInt()
        val padRight = (paddingEnd * ratio).toInt()
        val padTop = (paddingTop * ratio).toInt()
        val padBottom = (paddingBottom * ratio).toInt()
        setPadding(padLeft,padTop,padRight,padBottom)
    }

    protected fun View.setScaleSpace(ratio: Float){
        setScalePadding(ratio)
        setScaleMargin(ratio)
    }
}