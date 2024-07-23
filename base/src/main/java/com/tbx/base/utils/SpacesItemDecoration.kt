package com.tbx.base.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by Trinh BX on 02/02/2023.
 * Mail: trinhbx196@gmail.com.
 * Phone: +084 988 324 622.
 */
class SpacesItemDecoration(
    private val left: Int,
    private val top: Int,
    private val right: Int,
    private val bottom: Int, ): RecyclerView.ItemDecoration() {
    enum class Orientation{
        HORIZONTAL,VERTICAL
    }
    var spaceTwoHeads: Int = 0
    var orientation = Orientation.VERTICAL


    constructor(space:Int) : this(space, space, space, space)
    constructor(spaceHor:Int,spaceVer:Int): this(spaceHor, spaceVer, spaceHor, spaceVer)
    constructor(space:Int,spaceTwoHeads:Int,orientation: Orientation) : this(space, space, space, space){
        this.spaceTwoHeads = spaceTwoHeads
        this.orientation = orientation
    }


    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.left = this.left
        outRect.top = this.top
        outRect.right = this.right
        outRect.bottom = this.bottom
        val itemPosition = parent.getChildAdapterPosition(view);
        if (itemPosition == RecyclerView.NO_POSITION) {
            return
        }
        val itemCount = state.itemCount

        /** first position */
        if (itemPosition == 0) {
            if(spaceTwoHeads!=0){
                if(orientation== Orientation.VERTICAL){
                    outRect.top = spaceTwoHeads
                } else{
                    outRect.left = spaceTwoHeads
                }
            }
        }
        /** last position */
        else if (itemCount > 0 && itemPosition == itemCount - 1) {
            if(spaceTwoHeads!=0){
                if(orientation== Orientation.VERTICAL){
                    outRect.bottom = spaceTwoHeads
                } else{
                    outRect.right = spaceTwoHeads
                }
            }
        }
//        /** positions between first and last */
//        else { outRect.left = this.left
//            outRect.top = this.top
//            outRect.right = this.right
//            outRect.bottom = this.bottom
//
//        }
    }
}