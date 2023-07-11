package com.fyc.android.hrapp

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private lateinit var frame: Rect

    private val paint = Paint().apply {

        style = Paint.Style.FILL

        color = ResourcesCompat.getColor(resources, R.color.black, null)

        isAntiAlias = true

        isDither = true

        textAlign = Paint.Align.CENTER

        textSize = 55.0f

    }

    init {

        isClickable = true

    }

    override fun performClick(): Boolean {
        if (super.performClick()) return true

        invalidate()
        return true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val inset = 0

        frame = Rect(inset, inset, width , height )


    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawRect(frame,paint)

    }

}