package cn.yhsh.surfaceviewmodel

import android.app.Activity
import android.os.Bundle
import android.view.WindowManager

class MathActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_math)
    }
}