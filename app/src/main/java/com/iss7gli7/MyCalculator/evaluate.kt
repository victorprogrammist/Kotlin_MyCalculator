
package com.iss7gli7.MyCalculator

@Throws(Exception::class)
fun precedence(op : String) : Pair<Int,Int> {
    // result - (precedence, count params)
    when (op) {
        ")" -> return -2 to 0
        "=" -> return 0 to 1
        "+" -> return 1 to 2
        "-" -> return 1 to 2
        "*" -> return 2 to 2
        "/" -> return 2 to 2
        "%" -> return 2 to 2
        "^" -> return 3 to 2
        "log" -> return 3 to 2
        "!" -> return 4 to 1
    }

    throw_err()
}

@Throws(Exception::class)
fun calculate_monadic(v1 : Double, op : String) : Double {

    when (op) {
        "=" -> return v1
        "!" -> return gammaLanczos(v1 + 1.0)
    }

    throw_err()
}

@Throws(Exception::class)
fun calculate_dyadic(v1 : Double, op : String, v2 : Double, reverse_order: Boolean) : Double {

    if (reverse_order)
        return calculate_dyadic(v2, op, v1, false)

    when (op) {
        "+" -> return v1 + v2
        "-" -> return v1 - v2
        "*" -> return v1 * v2
        "/" -> return v1 / v2
        "%" -> return v1 % v2
        "log" -> return Math.log(v2) / Math.log(v1)
        "^" -> return Math.pow(v1, v2)
    }

    throw_err()
}

fun throw_err() : Nothing {
    throw Exception("Err")
}

//**************************************************************
data class Expr (
    var residue : String,
    var has_operator : Boolean = false,
    var operator : String = "",
    var precedence : Int = 0,
    var count_params : Int = 0,
    var reverse_order : Boolean = false,
)

fun shift_expr(expr : Expr, cnt : Int) : String {

    if (expr.residue.length < cnt)
        throw Exception("Invalid shift: (cnt: $cnt)")

    val res = expr.residue.substring(0, cnt)
    expr.residue = expr.residue.substring(cnt).trimStart()
    return res
}

fun shift_if(expr : Expr, tmpl : String) : Boolean {

    if (!expr.residue.startsWith(tmpl, true))
        return false

    shift_expr(expr, tmpl.length)
    return true
}

fun read_operator(expr : Expr) {

    if (expr.has_operator)
        return

    expr.has_operator = true
    expr.reverse_order = false
    expr.count_params = 0

    if (expr.residue.isEmpty()) {
        expr.operator = "EOE"
        expr.precedence = -2
        return
    }

    if (shift_if(expr, "\'"))
        expr.reverse_order = true

    if (expr.residue.isEmpty())
        throw_err()

    var sz_op : Int
    if (!expr.residue[0].isLetter())
        sz_op = 1
    else
        sz_op = length_name(expr.residue)

    val op = expr.residue.substring(0, sz_op)

    val(prec,cnt) = precedence(op)
    expr.precedence = prec
    expr.count_params = cnt
    expr.operator = op

    shift_expr(expr, sz_op)
}

fun read_number(expr : Expr) : Double {

    val regex = "^\\d+(\\.\\d+)?".toRegex()

    val match = regex.find(expr.residue)

    if (match != null)
        return shift_expr(expr, match.value.length).toDouble()

    throw_err()
}

fun length_name(s : String) : Int {
    val regex = "^[a-z][a-z0-9_]*".toRegex(RegexOption.IGNORE_CASE)
    return regex.find(s)?.value?.length ?: 0
}

fun get_value(expr : Expr, prev_precedence : Int) : Double {

    if (expr.residue.isEmpty())
        throw_err()

    var result : Double

    if (!shift_if(expr, "("))
        result = read_number(expr)

    else {
        result = get_value(expr, 0)

        if (!expr.has_operator || expr.operator != ")")
            throw_err()
    }

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
    val reverse = expr.reverse_order
    expr.has_operator = false;

    var subresult : Double

    if (expr.count_params == 1)
        subresult = calculate_monadic(first, operator)

    else {
        val second: Double = get_value(expr, precedence)
        subresult = calculate_dyadic(first, operator, second, reverse)
    }

    return recu_eval_expr(subresult, expr, prev_precedence)
}

fun eval_expr(string_expr : String) : Double {

    var expr = Expr(string_expr)

    try {
        val result = get_value(expr, -1)

        if (expr.residue.isNotEmpty())
            throw Exception("Err")

        return result

    } catch (e : Exception) {

        val sz_before = string_expr.length - expr.residue.length
        val before = string_expr.substring(0, sz_before)

        val msg = e.message.toString()
        throw Exception("$msg: $before<?>${expr.residue}")
    }
}

fun eval_expr_throw(string_expr : String) : String {

    var res : String

    try {
        res = eval_expr(string_expr).toString()
    } catch (e : Exception) {
        return e.message.toString()
    }

    fun cut_last(s : String) : String = s.substring(0, s.length-1)

    fun cut_last(s : String, c : Char) : String {
        return if (s.isNotEmpty() && s.last() == c)
            cut_last(cut_last(s), c)
        else
            s
    }

    if (res.contains('.'))
        res = cut_last(res, '0')

    return cut_last(res, '.')
}
