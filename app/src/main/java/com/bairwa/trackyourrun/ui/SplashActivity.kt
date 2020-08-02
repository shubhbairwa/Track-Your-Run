package com.bairwa.trackyourrun.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import com.bairwa.trackyourrun.R
import kotlinx.android.synthetic.main.activity_splash.*
import java.time.Duration

class SplashActivity : AppCompatActivity() {

    lateinit var topAnim: Animation
    lateinit var bottomAnim: Animation
    lateinit var rightAnim:Animation
    override fun onBackPressed() {
//        super.onBackPressed()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_splash)
supportActionBar?.hide()
        topAnim = AnimationUtils.loadAnimation(this, R.anim.slide_in_animation)


            bottomAnim = AnimationUtils.loadAnimation(applicationContext, R.anim.bottom_animation)
rightAnim=AnimationUtils.loadAnimation(applicationContext,R.anim.slide_in_right)




//        imageView2.animation = topAnim
imageView2.animation=topAnim
        textView.animation=bottomAnim
imageView.animation=rightAnim

        val splash: Thread = object : Thread() {
            override fun run() {
                try {
                    sleep(3000)
                    val intent = Intent(this@SplashActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } catch (e: Exception) {
                }
            }
        }
        splash.start()

    }



    }





