package com.iss7gli7.MyCalculator

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SecondActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        supportActionBar?.hide()
        setContentView(R.layout.activity_second)

        var hlp = findViewById<TextView>(R.id.viewHelp)
        hlp.movementMethod = ScrollingMovementMethod()

        val btbk = findViewById<Button>(R.id.buttonBack)
        btbk.setOnClickListener { onBackPressed() }
    }
}

