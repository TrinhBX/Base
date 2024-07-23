package com.tbx.base

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import java.util.Collections

/**
 * @author
 * Created by Trinh BX.
 * Mail: trinhbx196@gmail.com.
 * Phone: +084 988 324 622.
 * @since 14/05/2024
 */
abstract class BaseDragAdapter<VB : ViewBinding, T>(data: ArrayList<T> = arrayListOf()) :
    BaseAdapter<VB, T>(data) {

    protected var itemTouchHelper: ItemTouchHelper? = null
    protected var recyclerView: RecyclerView? = null
    open fun onMove(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(data, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(data, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    open fun swipe(position: Int, direction: Int) {
        data.removeAt(position)
        notifyItemRemoved(position)
    }

    open fun onMoveFinished(oldPosition: Int, newPosition: Int) {}
}
