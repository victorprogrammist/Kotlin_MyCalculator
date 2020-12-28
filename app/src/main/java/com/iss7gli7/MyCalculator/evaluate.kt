
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
    throw Exception("Inv. operator: $op")
}

@Throws(Exception::class)
fun calculate_monadic(v1 : Double, op : String) : Double {

    when (op) {
        "=" -> return v1
        "!" -> return gammaLanczos(v1 + 1.0)
    }

    throw Exception("Inv. operator: $op")
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

    throw Exception("Inv. operator: $op")
}

//**************************************************************
data class Expr (
    val expression : String,
    var has_operator : Boolean = false,
    var operator : String = "",
    var precedence : Int = 0,
    var count_params : Int = 0,
    var reverse_order : Boolean = false,
    var residue : String = expression
)

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
        throw_show_residue(expr, "Syntax")

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

    throw_show_residue(expr, "Expected number")
}

fun length_name(s : String) : Int {
    val regex = "^[a-z][a-z0-9_]*".toRegex(RegexOption.IGNORE_CASE)
    return regex.find(s)?.value?.length ?: 0
}

fun get_value(expr : Expr, prev_precedence : Int) : Double {

    if (expr.residue.isEmpty())
        throw_show_residue(expr, "Fail")

    var result : Double

    if (!shift_if(expr, "("))
        result = read_number(expr)

    else {
        result = get_value(expr, 0)

        if (!expr.has_operator || expr.operator != ")")
            throw_show_residue(expr, "Expected brace")
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

    val result = get_value(expr, -1)

    if (expr.residue.isNotEmpty())
        throw_show_residue(expr, "Inv. expr.")

    return result
}

fun eval_expr_throw(string_expr : String) : String {
    return try {
        eval_expr(string_expr).toString()
    } catch (e : Exception) {
        e.message.toString()
    }
}
