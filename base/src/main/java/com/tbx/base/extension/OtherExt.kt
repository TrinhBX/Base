package com.tbx.base.extension

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.provider.Settings
import android.text.Editable
import android.text.Html
import android.text.Spanned
import android.text.TextPaint
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tbx.base.ItemTouchListener
import com.tbx.base.SimpleItemTouchHelperCallback
import com.tbx.base.utils.VersionUtils
import java.io.Serializable
import java.util.Locale

/**
 * Created by Trinh BX on 02/02/2023.
 * Mail: trinhbx196@gmail.com.
 * Phone: +084 988 324 622.
 */
inline fun <reified T: Parcelable> Bundle.getArgumentParcelable(key:String):T?{
    return try {
        if(VersionUtils.hasTiramisu()){
            getParcelable(key,T::class.java)
        } else{
            getParcelable(key) as? T?
        }
    } catch (e:Exception){null}
}
inline fun <reified T: Parcelable> Bundle.getArgumentParcelableArray(key:String):List<T>{
    return try {
        if(VersionUtils.hasTiramisu()){
            getParcelableArrayList(key,T::class.java)?.toList()?: listOf()
        } else{
            getParcelableArrayList(key)?: listOf()
        }
    } catch (e:Exception){
        listOf()
    }
}
inline fun <reified T: Serializable> Bundle.getArgumentSerializable(key:String):T?{
    return try {
        if(VersionUtils.hasTiramisu()){
            getSerializable(key,T::class.java)
        } else{
            getSerializable(key) as? T?
        }
    } catch (e:Exception){null}
}

inline fun <reified T: Parcelable> Intent.getDataParcelable(key:String):T?{
    return try {
        if(VersionUtils.hasTiramisu()){
            getParcelableExtra(key,T::class.java)
        } else{
            getParcelableExtra(key) as? T?
        }
    } catch (e:Exception){null}
}

inline fun <reified T: Parcelable> Intent.getDataParcelableArray(key:String):List<T>{
    return try {
        if(VersionUtils.hasTiramisu()){
            getParcelableArrayExtra(key,T::class.java)?.toList()?: listOf()
        } else{
            val list = arrayListOf<T>()
            getParcelableArrayListExtra<T>(key)?.map{
                if(it is T) list.add(it)
            }
            list
        }
    } catch (e:Exception){
        listOf()
    }
}

inline fun <reified T: Serializable> Intent.getDataSerializable(key:String):T?{
    return try {
        if(VersionUtils.hasTiramisu()){
            getSerializableExtra(key,T::class.java)
        } else{
            getSerializableExtra(key) as? T?
        }
    } catch (e:Exception){null}
}

fun TextView.setTextHtml(content:String){
    val sp =
        if (VersionUtils.hasNougat()) {
            Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(content)
        }
    setText(sp,TextView.BufferType.SPANNABLE)
}
fun createSpanned(content: String): Spanned {
    return if (VersionUtils.hasOreo()) {
        Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY)
    } else {
        Html.fromHtml(content)
    }
}

fun RecyclerView.onLoadMore(callback: () -> Unit) {
    var needLoadMore = false
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            val totalCountItem = (recyclerView.layoutManager as LinearLayoutManager).itemCount
            val visibleItemCount = (recyclerView.layoutManager as LinearLayoutManager).childCount
            val firstVisibleItem =
                (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            if ((dy > 0 || dx > 0) && totalCountItem - visibleItemCount <= firstVisibleItem) {
                needLoadMore = true
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                if (needLoadMore) {
                    callback.invoke()
                }
                needLoadMore = false
            }

        }
    })
}
fun RecyclerView.onLoadPrv(callback: () -> Unit) {
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            val totalCountItem = (recyclerView.layoutManager as LinearLayoutManager).itemCount
            val visibleItemCount = (recyclerView.layoutManager as LinearLayoutManager).childCount
            val firstVisibleItem =
                (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            if ((dy <= 0 || dx<=0)) {
                callback.invoke()
            }
        }
    })
}

fun Array<String>.convertToCMD():String {
    var stringCMD = ""
    this.forEachIndexed{
            i,value->
        stringCMD += if(i<size-1){
            "$value "
        } else{
            value
        }
    }
    return stringCMD
}

val Intent.ACTION_UPDATE_WG: String
    get() = "watch_action_update_wg"

val Intent.ACTION_CREATE_WG: String
    get() = "watch_action_create_widget"

/**
 * Handler how an element displays in lists containing banner and native ads.
 *
 * @param count is the number of elements containing data between two native ads.
 * @param spanCount is the number of dark elements in a row of RecycleView (for LayoutManager it is GridLayoutManager),
 * should be the divisor of count
 *
 * @sample handlerAdsGridLayout(10,2)
 */
fun RecyclerView.handlerAdsGridLayout(count:Int,spanCount:Int){
    if(count<0 || spanCount<=0) return
    if(layoutManager is GridLayoutManager){
        (layoutManager as GridLayoutManager).spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (position%(count+1)==0) {
                    spanCount
                } else{
                    1
                }
            }
        }
    }
}

private val TextPaint.textBounds: Rect
    get() = Rect()
fun TextPaint.measureTextHeight(text:String):Float{
    textBounds.set(0,0,0,0)
    getTextBounds(text, 0, text.length, textBounds)
    return textBounds.height().toFloat()
}
fun TextPaint.measureTextWidth(text:String):Float{
    return measureText(text)
}

fun Activity.checkSystemWritePermission(): Boolean {
    val retVal: Boolean = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        Settings.System.canWrite(this)
    } else {
        true
    }
    return retVal
}

fun Activity.requestSystemWritePermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
        intent.data = Uri.parse("package:$packageName")
        startActivity(intent)
    }
}

fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

inline fun <reified T>List<T>.subListFirstStart(countElement:Int):List<T>{
    return if(countElement<=0) listOf()
    else if(countElement>=size) this
    else {
        subList(0,countElement)
    }
}

fun String.upperCaseFirstChar():String{
    return lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
}
fun RecyclerView.addItemTouchCallback(
    adapter: com.tbx.base.BaseDragAdapter<*, *>,
    isDragParent: Boolean = false,
    canSwipe: Boolean = true,
    canDrag: Boolean = true
): ItemTouchHelper {
    val callback =
        SimpleItemTouchHelperCallback(object : ItemTouchListener {
            override fun onMove(oldPosition: Int, newPosition: Int) {
                adapter.onMove(oldPosition, newPosition)
            }

            override fun swipe(position: Int, direction: Int) {
                adapter.swipe(position, direction)
            }

            override fun onMoveFinished(oldPosition: Int, newPosition: Int) {
                adapter.onMoveFinished(oldPosition, newPosition)
            }
        })
    callback.setIsDragParent(isDragParent)

    callback.enableSwipe(canSwipe)
    callback.enableDrag(canDrag)

    val itemTouchHelper = ItemTouchHelper(callback)
    itemTouchHelper.attachToRecyclerView(this)
    return itemTouchHelper
}


