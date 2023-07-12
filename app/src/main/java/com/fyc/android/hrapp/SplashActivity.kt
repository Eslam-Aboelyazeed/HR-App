package com.fyc.android.hrapp

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.core.os.postDelayed

@Suppress("DEPRECATION")
class SplashActivity : AppCompatActivity() {

    private lateinit var animator0: TheAnimation

    private lateinit var loadingRec: LoadingRec

    //private lateinit var loadingBtn: LoadingButton

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        loadingRec = findViewById(R.id.loading_rec)

       // loadingBtn = findViewById(R.id.loading_btn)

       // loadingBtn.isClickable = false

        loadingRec.isClickable = false

        // This is used to hide the status bar and make
        // the splash screen as a full screen activity.
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        // we used the postDelayed(Runnable, time) method
        // to send a message with a delayed time.
        //Normal Handler is deprecated , so we have to change the code little bit

        animator0 = TheAnimation(loadingRec, -360f, loadingRec.getTheWidth())

        animator0.duration = 3000
        animator0.repeatCount = 1
        loadingRec.startAnimation(animator0)

//        Handler(Looper.getMainLooper()).postDelayed({
//            loadingRec.startAnimation(animator0)
//        }, 1000)


        // Handler().postDelayed({
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000) // 3000 is the delayed time in milliseconds.
    }
}