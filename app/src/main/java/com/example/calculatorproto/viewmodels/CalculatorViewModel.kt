package com.example.calculatorproto.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calculatorproto.misc.CalculatorToken
import com.example.calculatorproto.misc.FirestoreAccessor
import kotlinx.coroutines.launch

class CalculatorViewModel: ViewModel() {

    private var currentExpression by mutableStateOf("0")
    private var currentResult by mutableStateOf("0")
    private var firestoreAccessor = FirestoreAccessor()
    private var isCurrentExpressionSaved = false
    private lateinit var uid: String

    fun setUid(uid: String) {
        this.uid = uid
    }

    fun getStringExpression() : String {
        return currentExpression
    }

    fun getEvaluated() : String {
        return when (currentResult) {
            "Infinity" -> "∞"
            "NaN" -> "Error"
            else -> currentResult
        }
    }

    fun updateExpression(token: CalculatorToken) {
        when (token) {
            CalculatorToken.ONE,
            CalculatorToken.TWO,
            CalculatorToken.THREE,
            CalculatorToken.FOUR,
            CalculatorToken.FIVE,
            CalculatorToken.SIX,
            CalculatorToken.SEVEN,
            CalculatorToken.EIGHT,
            CalculatorToken.NINE,
            CalculatorToken.SIN,
            CalculatorToken.COS,
            CalculatorToken.TAN,
            CalculatorToken.COT,
            CalculatorToken.SQRT,
            CalculatorToken.OPEN_PARENTHESES,
            CalculatorToken.CLOSE_PARENTHESES,
            CalculatorToken.ZERO -> {
                isCurrentExpressionSaved = false
                addDigit(token.displaySymbol)
            }

            CalculatorToken.CLEAR -> {
                if (!isCurrentExpressionSaved) {
                    viewModelScope.launch {
                        firestoreAccessor
                            .addHistoryEntry(uid, currentExpression)
                        isCurrentExpressionSaved = true
                    }
                }
                clearExpression()
            }

            CalculatorToken.BACKSPACE ->
                clearLastSymbolOfExpression()

            CalculatorToken.MULTIPLY,
            CalculatorToken.SUBTRACT,
            CalculatorToken.DIVIDE,
            CalculatorToken.ADD -> {
                addDigit(token.displaySymbol)
                return
            }

            CalculatorToken.FRACTION ->
                addFraction()

            CalculatorToken.EQUALS ->
                {
                    if (!isCurrentExpressionSaved) {
                        viewModelScope.launch {
                            firestoreAccessor
                                .addHistoryEntry(uid, currentExpression)
                            isCurrentExpressionSaved = true
                        }
                    }

                    if (currentResult != "Error") {
                        currentExpression = currentResult
                    }

                }
        }

        try {
            evaluateExpression()
        } catch (_: Exception) {
            currentResult= "Error"
        }
    }

    private fun addDigit(symbol: String) {
        if (currentExpression == "0") {
            currentExpression = symbol
        } else {
            currentExpression += symbol
        }
    }

    private fun addFraction() {
        if (!currentExpression.last().isDigit()) {
            return
        }
        if (currentExpression.last().toString() != CalculatorToken.FRACTION.symbol)  {
            currentExpression += CalculatorToken.FRACTION.symbol;
        }
    }

    private fun clearExpression() {
        currentExpression = "0"
    }

    private fun clearLastSymbolOfExpression() {
        currentExpression = currentExpression.dropLast(1)

        if (currentExpression.isEmpty()) {
            currentExpression = "0"
        }
    }

    private fun prepareForEvaluation(raw: String): String {
        return raw
            .replace("sin", "Math.sin")
            .replace("cos", "Math.cos")
            .replace("tan", "Math.tan")
            .replace("cot", "cot")
            .replace("sqrt", "Math.sqrt")
            .replace(CalculatorToken.SUBTRACT.displaySymbol, CalculatorToken.SUBTRACT.symbol)
            .replace(CalculatorToken.MULTIPLY.displaySymbol, CalculatorToken.MULTIPLY.symbol)
            .replace(CalculatorToken.DIVIDE.displaySymbol, CalculatorToken.DIVIDE.symbol)
    }

    private fun evaluateExpression() {
        val context = org.mozilla.javascript.Context.enter()
        context.optimizationLevel = -1
        val scope = context.initStandardObjects()

        val prepared = prepareForEvaluation(currentExpression)

        val cotFunc =
            "function cot(x) {" +
            "   return 1/Math.tan(x);" +
            "}"


        currentResult = try {

            val tmp = context
                .evaluateString(scope, cotFunc + prepared, "JavaScript", 1, null)

            if (tmp is Double) {
                tmp.toString()
            } else {
                "Error"
            }

        } catch (_: Exception) {
            "Error"
        } finally {
            org.mozilla.javascript.Context.exit()
        }
    }
}