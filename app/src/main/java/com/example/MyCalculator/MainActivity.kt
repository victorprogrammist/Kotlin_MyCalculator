package com.example.MyCalculator

import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        supportActionBar?.hide()

//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main)

        var hist = findViewById<TextView>(R.id.textHistory)
        hist.movementMethod = ScrollingMovementMethod()

        var ed = findViewById<EditText>(R.id.editExpr)
        ed.setOnKeyListener(View.OnKeyListener {
            v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                make_calc(v)
                return@OnKeyListener true
            }
            false
        })
    }

    fun make_help(v : View) {
        val intent = Intent(this, SecondActivity::class.java)
        startActivity(intent)
    }

    fun make_clear(v : View) {
        var ed = findViewById<EditText>(R.id.editExpr)
        ed.text.clear()
    }

    fun make_calc(v : View) {

        var ed = findViewById<EditText>(R.id.editExpr)
        val expr = ed.text.toString()
        if (expr.isEmpty())
            return

        var hist = findViewById<TextView>(R.id.textHistory)

        if (!hist.text.isEmpty())
            hist.append("\n")

        val result = eval_expr_throw(expr)

        hist.append(ed.text)
        hist.append("\n====> $result")
    }
}