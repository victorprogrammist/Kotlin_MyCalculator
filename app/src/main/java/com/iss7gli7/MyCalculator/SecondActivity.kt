package com.iss7gli7.MyCalculator

import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.view.Window
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
    }

    fun sendBack(v : View) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("expr", getIntent().getStringExtra("expr"))
        intent.putExtra("hist", getIntent().getStringExtra("hist"))
        startActivity(intent)
    }
}

