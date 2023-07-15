package com.fyc.android.hrapp

import android.os.Build
import android.view.animation.Animation
import android.view.animation.Transformation
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.M)
class LoadingAnimation(lREC: Loading, nSweepAngle: Float): Animation(){

    private var lRec: Loading

    private var oSweepAngle: Float

    private var nSweepAngle: Float

//    private var oWidth: Float
//
//    private var nWidth: Float


    init {

        this.oSweepAngle = lREC.getTheAngle()
        this.nSweepAngle = nSweepAngle
//        this.oWidth = lREC.getTheWidth()
//        this.nWidth = nWidth
        this.lRec = lREC
    }

    override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
        super.applyTransformation(interpolatedTime, t)
        val ang = oSweepAngle +((nSweepAngle - oSweepAngle) * interpolatedTime)
//        val wid = oWidth + ((nWidth - oWidth) * interpolatedTime)


//        lRec.setTheBText("Loading")
        lRec.setTheAngle(ang)
//        lRec.setTheWidth(wid)
        lRec.requestLayout()

    }

}