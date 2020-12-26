package com.example.MyCalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.KeyEvent

import android.view.View
import android.widget.EditText
import android.widget.TextView

@Throws(Exception::class)
fun precedence(op : String) : Int {
    when (op) {
        ")" -> return -1
        "+" -> return 1
        "-" -> return 1
        "*" -> return 2
        "/" -> return 2
        "%" -> return 2
    }
    throw Exception("Invalid operator: $op")
}

@Throws(Exception::class)
fun calculate(v1 : Double, op : String, v2 : Double) : Double {
    when (op) {
        "+" -> return v1 + v2
        "-" -> return v1 - v2
        "*" -> return v1 * v2
        "/" -> return v1 / v2
        "%" -> return v1 % v2
    }
    throw Exception("Invalid operator: $op")
}

data class Expr (
    var has_operator : Boolean,
    var operator : String,
    var precedence : Int,
    var residue : String,
    val expression : String)

fun throw_show_residue(expr : Expr, msg : String) : Nothing {

    val sz_before = expr.expression.length - expr.residue.length

    val before = expr.expression.substring(0, sz_before)

    throw Exception("$msg: $before<?>${expr.residue}")
}

fun shift_expr(expr : Expr, cnt : Int) : String {

    if (expr.residue.length < cnt)
        throw_show_residue(expr, "Invalid shift: (cnt: $cnt)")

    val res = expr.residue.substring(0, cnt)
    expr.residue = expr.residue.substring(cnt).trimStart()
    return res
}

fun read_operator(expr : Expr) {

    if (expr.has_operator)
        return

    if (expr.residue.isEmpty())
        expr.precedence = -2

    else {

        val operatorsOneChar: String = "+-*/%)"

        if (operatorsOneChar.indexOf(expr.residue[0]) >= 0) {
            expr.operator = shift_expr(expr, 1)
            expr.precedence = precedence(expr.operator)
        } else
            throw_show_residue(expr, "Expected operator")
    }

    expr.has_operator = true
}

fun read_number(expr : Expr) : Double {

    val regex = "\\d+(\\.\\d+)?".toRegex()

    val match = regex.find(expr.residue)

    if (match != null)
        return shift_expr(expr, match.value.length).toDouble()

    throw_show_residue(expr, "Expected number")
}

fun get_value(expr : Expr, prev_precedence : Int) : Double {

    var result : Double

    if (expr.residue[0] == '(') {
        shift_expr(expr, 1)
        result = get_value(expr, 0)

        if (!expr.has_operator || expr.operator != ")")
            throw_show_residue(expr, "Expected brace")

    } else {
        result = read_number(expr)
    }

    expr.has_operator = false;

    return recu_eval_expr(result, expr, prev_precedence)
}

fun recu_eval_expr(first : Double, expr : Expr, prev_precedence: Int) : Double {

    read_operator(expr)

    // приоритет при начале выражения == 0,
    //   приоритеты закрывающей скобки и конца выражения меньше нуля,
    //   как следствие, эти ситуации здесь отработаются возвратом

    if (expr.precedence <= prev_precedence)
        return first

    val operator = expr.operator
    val precedence = expr.precedence

    val second : Double = get_value(expr, precedence)

    val subresult = calculate(first, operator, second)

    return recu_eval_expr(subresult, expr, prev_precedence)
}

fun eval_expr(string_expr : String) : Double {

    var expr = Expr(false, "", 0, string_expr, string_expr)

    val result = get_value(expr, 0)

    if (expr.residue.isNotEmpty())
        throw_show_residue(expr, "Invalid expression")

    return result
}

fun eval_expr_throw(string_expr : String) : String {
    return try {
        eval_expr(string_expr).toString()
    } catch (e : Exception) {
        e.message.toString()
    }
}

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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