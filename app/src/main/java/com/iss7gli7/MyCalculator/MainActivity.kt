package com.iss7gli7.MyCalculator

import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.view.*
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var v_expr : EditText
    private lateinit var v_hist : RecyclerView
    private lateinit var adapt : MyAdapter
    private lateinit var itemTouchHeler : ItemTouchHelper

    fun initListHistory() {

        adapt = object : MyAdapter() {
            override fun OnTouch(v : View) {
                v_expr.setText(dataExpr(v_hist.getChildAdapterPosition(v)))
            }
        }

        v_hist = findViewById(R.id.listHistory)
        v_hist.adapter = adapt
        v_hist.layoutManager = LinearLayoutManager(applicationContext)

        val touch = object : MySwipeToDelete(this) {
            override fun onSwiped(vh : RecyclerView.ViewHolder) {
                adapt.delItem(vh.adapterPosition)
            }
        }

        itemTouchHeler = ItemTouchHelper(touch)
        itemTouchHeler.attachToRecyclerView(v_hist)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)

        initListHistory()

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
        if (expr.isEmpty() || expr == adapt.lastExpr())
            return

        val pos = adapt.addItem(expr, eval_expr_throw(expr))
        v_hist.layoutManager?.scrollToPosition(pos)
    }
}
