package cn.yhsh.surfaceviewmodel

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
    }

    fun tPage(view: View?) {
        startActivity(Intent(this, SurfaceViewDynDrawActivity::class.java))
    }

    fun mathPage(view: View) {
        startActivity(Intent(this, MathActivity::class.java))
    }

    fun ballPage(view: View) {
        startActivity(Intent(this, BallActivity::class.java))
    }
    fun animationPage(view: View) {
        startActivity(Intent(this, AnimationSurfaceActivity::class.java))
    }
}