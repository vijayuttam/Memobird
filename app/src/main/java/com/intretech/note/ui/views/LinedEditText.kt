package com.intretech.note.ui.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log

/**
 * Created by vijaymaurya on 9/5/17.
 */
class LinedEditText @JvmOverloads constructor(context: Context,
                                              attrs: AttributeSet? = null,
                                              defStyle: Int = 0) :
        android.support.v7.widget.AppCompatEditText(context, attrs, defStyle) {

    private var drawLine: Int = 0
    private var lineDis: Float = 0.toFloat()
    private var mRect: Rect? = null
    private var mPaint: Paint? = null


    init {
        lineDis = 30f
        drawLine = this.minLines
        Log.d("行距", lineSpacingExtra.toString() + "")
        Log.d("getTextSize", textSize.toString() + "")
        Log.d("minLine", minLines.toString() + "")
        mRect = Rect()
        mPaint = Paint()
        mPaint!!.strokeWidth = 1.5f
        mPaint!!.style = Paint.Style.STROKE
        mPaint!!.color = 0x60000000
    }

    override fun onDraw(canvas: Canvas) {
        val count = lineCount
        val r = mRect
        val paint = mPaint
        var basicLine = 0
        for (i in 0..count - 1) {
            val baseLine = getLineBounds(i, r)
            basicLine = baseLine
            canvas.drawLine(r!!.left.toFloat(), baseLine + lineDis, r!!.right.toFloat(), baseLine + lineDis, paint)
        }
        if (count < drawLine) {
            for (j in 1..drawLine - 1) {
                val baseline = basicLine + j * lineHeight
                canvas.drawLine(r!!.left.toFloat(), baseline + lineDis, r!!.right.toFloat(), baseline + lineDis, paint)
            }
        }
        super.onDraw(canvas)
    }

    fun setNotesMinLines(lines: Int) {
        this.drawLine = lines
        minLines = lines
    }

}