package com.fyc.android.hrapp

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.withStyledAttributes

private var backgroundColor = 0
private var tsize = 0f

@RequiresApi(Build.VERSION_CODES.M)
class Loading @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var side = 0

    private var frame: Float = 0f

    private lateinit var pointPosition: PointF

    private var sweepAngle: Float = 0f

    private var w: Float = 0f

    private var bText: String = ""

    init {

        isClickable = true

        context.withStyledAttributes(attrs, R.styleable.LoadingRec) {

            backgroundColor = getColor(R.styleable.LoadingRec_backgroundColor,resources.getColor(R.color.black, null))
            tsize = getFloat(R.styleable.LoadingRec_tSize, 55.0f)
        }

    }

    private val arcPaint = Paint().apply {

        style = Paint.Style.FILL

        color = ResourcesCompat.getColor(resources, R.color.white, null)

        isAntiAlias = true

        isDither = true

        textAlign = Paint.Align.CENTER

        textSize = tsize


    }

    private val paint = Paint().apply {

        style = Paint.Style.FILL

        color = ResourcesCompat.getColor(resources, R.color.white, null)

        isAntiAlias = true

        isDither = true

        textAlign = Paint.Align.CENTER

        textSize = tsize

    }

    private val tPaint = Paint().apply {

        style = Paint.Style.FILL

        textAlign = Paint.Align.CENTER

        textSize = tsize

        color = ResourcesCompat.getColor(resources, R.color.white, null)

        isAntiAlias = true

        isDither = true
    }

    override fun performClick(): Boolean {
        if (super.performClick()) return true

        invalidate()
        return true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        frame = height.toFloat()

        pointPosition  = PointF((width / 2).toFloat(),((height / 2) + 16).toFloat())

        if (width > height) {
            side = height * 4/5
        } else {
            side = width * 4/5
        }

    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
//        canvas?.drawRect(0f, 0f, w, frame, paint)
//        canvas?.drawText(bText,pointPosition.x,pointPosition.y,tPaint)
        canvas?.drawArc(
            (width - (width/2))-(side/2).toFloat(),(height/2)-(side/2).toFloat(),
            (width - (width/2))+(side/2).toFloat(),(height/2)+(side/2).toFloat(),
            0f,sweepAngle,true,arcPaint)

    }

    fun setTheAngle(sAngle:Float){

        sweepAngle = sAngle
    }

    fun getTheAngle():Float{

        return sweepAngle
    }

    fun setTheWidth(wi:Float){

        w = wi
    }

    fun getTheWidth():Float{

        return w
    }

    fun setTheBText(bt:String){

        bText = bt
    }

}