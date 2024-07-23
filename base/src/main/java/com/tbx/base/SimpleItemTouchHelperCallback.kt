package com.tbx.base

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

/**
 * @author
 * Created by Trinh BX.
 * Mail: trinhbx196@gmail.com.
 * Phone: +084 988 324 622.
 * @since 14/05/2024
 */
class SimpleItemTouchHelperCallback(
    private val listener: ItemTouchListener
) :
    ItemTouchHelper.Callback() {

    private var isDragParent = true

    private var canSwipe = true
    private var canDrag = true

    private var odlMovePos = -1

    fun setIsDragParent(isDrag: Boolean) {
        isDragParent = isDrag
    }

    fun enableSwipe(canSwipe: Boolean) {
        this.canSwipe = canSwipe
    }

    fun enableDrag(canDrag: Boolean) {
        this.canDrag = canDrag
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return canSwipe
    }

    override fun isLongPressDragEnabled(): Boolean {
        return canDrag
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlag = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlag = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        return makeMovementFlags(dragFlag, swipeFlag)
    }


    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        if (odlMovePos < 0) odlMovePos = viewHolder.adapterPosition
        listener.onMove(viewHolder.adapterPosition, target.adapterPosition)
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        listener.swipe(viewHolder.adapterPosition, direction)
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            viewHolder?.itemView?.alpha = 0.5f
        }
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        val newMovePos = viewHolder.adapterPosition
        if (odlMovePos >= 0 && newMovePos != odlMovePos) {
            listener.onMoveFinished(odlMovePos, newMovePos)
        }
        odlMovePos = -1
        viewHolder.itemView.alpha = 1f
    }
}
