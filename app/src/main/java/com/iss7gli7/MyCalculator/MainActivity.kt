package com.iss7gli7.MyCalculator

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

    var last_expr : String = ""
    lateinit var v_expr : EditText
    lateinit var v_hist : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        supportActionBar?.hide()

//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main)

        v_hist = findViewById<TextView>(R.id.textHistory)
        v_hist.movementMethod = ScrollingMovementMethod()

        v_expr = findViewById<EditText>(R.id.editExpr)
        v_expr.setOnKeyListener(View.OnKeyListener {
            v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                make_calc(v)
                return@OnKeyListener true
            }
            false
        })
    }

    fun make_help(v : View) =
        startActivity(Intent(this, SecondActivity::class.java))

    fun make_clear(v : View) =
            v_expr.text.clear()

    fun make_calc(v : View) {

        val expr = v_expr.text.toString().trim()
        if (expr.isEmpty() || expr == last_expr)
            return

        last_expr = expr

        val res = eval_expr_throw(expr)

        if (!v_hist.text.isEmpty())
            v_hist.append("\n")

        v_hist.append("$expr\n====> $res")
    }
}
