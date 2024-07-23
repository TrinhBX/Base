package com.tbx.base

/**
 * @author
 * Created by Trinh BX.
 * Mail: trinhbx196@gmail.com.
 * Phone: +084 988 324 622.
 * @since 14/05/2024
 */
interface ItemTouchListener {
    fun onMove(oldPosition: Int, newPosition: Int)

    fun swipe(position: Int, direction: Int)

    fun onMoveFinished(oldPosition: Int, newPosition: Int)

}