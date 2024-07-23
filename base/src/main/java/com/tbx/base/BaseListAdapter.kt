package com.tbx.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

/**
 * Created by Trinh BX on 02/02/2023.
 * Mail: trinhbx196@gmail.com.
 * Phone: +084 988 324 622.
 */
abstract class BaseListAdapter<VB : ViewBinding, T>(var data: ArrayList<T> = arrayListOf(), diffUtil: DiffUtil.ItemCallback<T>) :
    ListAdapter<T,RecyclerView.ViewHolder>(diffUtil) {
    companion object {
        const val ITEM_DATA = 0
        const val ITEM_ADS = 1
    }

    abstract fun binding(inflater: LayoutInflater, parent: ViewGroup, attachToParent: Boolean, viewType: Int): VB

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val binding = binding(LayoutInflater.from(parent.context), parent, false, viewType)

        return BaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as BaseListAdapter<*, *>.BaseViewHolder).onBind()
    }

    override fun getItemCount(): Int {
        return data.size
    }

    open inner class BaseViewHolder(var binding: VB) : RecyclerView.ViewHolder(binding.root) {
        fun onBind() {
            onBindData(binding, layoutPosition)
        }
    }

    abstract fun onBindData(binding: VB, position: Int)

    fun isEndPosition(position: Int): Boolean {
        return position >= itemCount - 1
    }
}