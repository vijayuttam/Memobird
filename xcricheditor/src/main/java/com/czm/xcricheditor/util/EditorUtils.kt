package com.czm.xcricheditor.util

import android.app.Activity
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.DisplayMetrics
import android.view.View
import android.view.inputmethod.InputMethodManager

/**
 * Created by deejan on 12/9/17.
 */

const val IMAGE_SRC_REGEX = "<img[^<>]*?\\ssrc=['\"]?(.*?)['\"].*?>"
var TAB_BOTTOM_TYPE = 0
var TAB_TOP_TYPE = 0
var deviceDataInited = false
var displayMetricsDensity: Float = 0F
var displayMetricsWidthPixels: Int = 0
var displayMetricsHeightPixels: Int = 0
var SCREEN_WIDTH_PX_CACHE = -1
var SCREEN_HEIGHT_PX_CACHE = -1


fun initDeviceData(context: Context) {
    if (context.resources != null && context.resources.displayMetrics != null) {
        displayMetricsDensity = context.resources.displayMetrics.density
        displayMetricsWidthPixels = context.resources.displayMetrics.widthPixels
        displayMetricsHeightPixels = context.resources.displayMetrics.heightPixels
    }
    deviceDataInited = true
}

fun dip2px(context: Context, dipValue: Float): Int {
    if (!deviceDataInited) {
        initDeviceData(context)
    }
    return (dipValue * displayMetricsDensity + 0.5F).toInt()
}

fun px2dip(context: Context, pxValue: Float): Int {
    if (!deviceDataInited) {
        initDeviceData(context)
    }
    return (pxValue / displayMetricsDensity + 0.5F).toInt()
}

fun getScreenWidthPx(context: Context): Int {
    if (SCREEN_WIDTH_PX_CACHE < 0) {
        val display = (context as Activity).windowManager.defaultDisplay
        SCREEN_WIDTH_PX_CACHE = display.width
    }

    return SCREEN_WIDTH_PX_CACHE
}

fun getScreenHeightPx(context: Context): Int {
    if (SCREEN_HEIGHT_PX_CACHE < 0) {
        val display = (context as Activity).windowManager.defaultDisplay
        SCREEN_HEIGHT_PX_CACHE = display.height
    }

    return SCREEN_HEIGHT_PX_CACHE
}

fun getCenterXChild(recyclerView: RecyclerView): View? {
    val childCount = recyclerView.childCount
    if (childCount > 0) {
        for (i in 0 until childCount) {
            val child = recyclerView.getChildAt(i)
            if (isChildInCenterX(recyclerView, child)) {
                return child
            }
        }
    }
    return null
}

fun getCenterXChildPosition(recyclerView: RecyclerView): Int {
    val childCount = recyclerView.childCount
    if (childCount > 0) {
        for (i in 0 until childCount) {
            val child = recyclerView.getChildAt(i)
            if (isChildInCenterX(recyclerView, child)) {
                return recyclerView.getChildAdapterPosition(child)
            }
        }
    }
    return childCount
}

fun getCenterYChild(recyclerView: RecyclerView): View? {
    val childCount = recyclerView.childCount
    if (childCount > 0) {
        for (i in 0 until childCount) {
            val child = recyclerView.getChildAt(i)
            if (isChildInCenterY(recyclerView, child)) {
                return child
            }
        }
    }
    return null
}

fun getCenterYChildPosition(recyclerView: RecyclerView): Int {
    val childCount = recyclerView.childCount
    if (childCount > 0) {
        for (i in 0 until childCount) {
            val child = recyclerView.getChildAt(i)
            if (isChildInCenterY(recyclerView, child)) {
                return recyclerView.getChildAdapterPosition(child)
            }
        }
    }

    return childCount
}

fun isChildInCenterX(recyclerView: RecyclerView, view: View): Boolean {
    val childCount = recyclerView.childCount
    val lvLocationOnScreen = IntArray(2)
    val vLocationOnScreen = IntArray(2)
    recyclerView.getLocationOnScreen(lvLocationOnScreen)
    val middleX = lvLocationOnScreen[0] + recyclerView.width / 2
    if (childCount > 0) {
        view.getLocationOnScreen(vLocationOnScreen)
        if (vLocationOnScreen[0] <= middleX && vLocationOnScreen[0] + view.width >= middleX) {
            return true
        }
    }

    return false
}

fun isChildInCenterY(recyclerView: RecyclerView, view: View): Boolean {
    val childCount = recyclerView.childCount
    val lvLocationOnScreen = IntArray(2)
    val vLocationOnScreen = IntArray(2)
    recyclerView.getLocationOnScreen(lvLocationOnScreen)
    val middleY = lvLocationOnScreen[1] + recyclerView.height / 2
    if (childCount > 0) {
        view.getLocationOnScreen(vLocationOnScreen)
        if (vLocationOnScreen[1] <= middleY && vLocationOnScreen[1] + view.height >= middleY) {
            return true
        }
    }

    return false
}

//Hide the keyboard
fun hideSoftKeyboard(context: Context, view: View) {
    try {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    } catch (e: Exception) {
        e.printStackTrace()
    }

}

//Display the keyboard
fun showSoftKeyboard(context: Context, view: View) {
    try {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        // If the input method is turned on, it turns off and opens if it is not open
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED)
    } catch (e: Exception) {
        e.printStackTrace()
    }

}