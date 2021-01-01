package com.iss7gli7.MyCalculator

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


abstract class MyAdapter : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    private val listData : ArrayList<Pair<String,String>> = arrayListOf()

    abstract fun OnTouch(v : View)

    val lstnr = object : View.OnClickListener {
        override fun onClick(v: View?) {
            OnTouch( v ?: return )
        }
    }

    fun dataExpr(pos : Int): String = listData[pos].first
    fun dataResult(pos : Int) : String = listData[pos].second

    fun lastExpr() : String? {
        return if (listData.isEmpty())
            null
        else
            listData.last().first
    }

    class ViewHolder(v : View) : RecyclerView.ViewHolder(v) {
        val textExpr : TextView = v.findViewById(R.id.textExpression)
        val textResult : TextView = v.findViewById(R.id.textResult)
    }

    override fun onCreateViewHolder(vg : ViewGroup, _viewType : Int) : ViewHolder {
        val view = LayoutInflater.from(vg.context)
                .inflate(R.layout.text_row_item, vg, false)
        view.setOnClickListener(lstnr)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(vh : ViewHolder, pos : Int) {
        with (listData[pos]) {
            vh.textExpr.setText(first)
            vh.textResult.setText(second)
        }
    }

    override fun getItemCount(): Int = listData.size

    fun addItem(expr : String, res : String, pos : Int = listData.size) : Int {
        listData.add(pos, expr to res)
        notifyItemInserted(pos)
        return pos
    }

    fun delItem(pos : Int) {
        listData.removeAt(pos)
        notifyItemRemoved(pos)
    }
}