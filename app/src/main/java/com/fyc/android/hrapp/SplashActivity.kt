package com.fyc.android.hrapp

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.postDelayed

@Suppress("DEPRECATION")
class SplashActivity : AppCompatActivity() {

    private lateinit var animator0: TheAnimation

    private lateinit var loadingRec: LoadingRec

    //private lateinit var loadingBtn: LoadingButton

    private lateinit var splashLayout: ConstraintLayout

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        splashLayout = findViewById(R.id.splash_layout)

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

        //Toast.makeText(this, splashLayout.measuredWidth.toString(), Toast.LENGTH_LONG).show()

        animator0 = TheAnimation(loadingRec, -360f, loadingRec.width.toFloat())

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