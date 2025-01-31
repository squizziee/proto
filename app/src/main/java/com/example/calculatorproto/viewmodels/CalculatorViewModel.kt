package com.example.calculatorproto.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calculatorproto.misc.CalculatorToken
import com.example.calculatorproto.misc.FirestoreAccessor
import kotlinx.coroutines.launch
import java.util.Stack

class CalculatorViewModel: ViewModel() {

    private var currentExpression by mutableStateOf("0")
    private var currentResult by mutableStateOf("0")
    private var firestoreAccessor = FirestoreAccessor()
    private var isCurrentExpressionSaved = false
    private lateinit var uid: String

    private var supportedOperators = hashMapOf(
        CalculatorToken.ADD.symbol to Operator (symbol = CalculatorToken.ADD.symbol, 0),
        CalculatorToken.SUBTRACT.symbol to Operator (symbol = CalculatorToken.SUBTRACT.symbol, 0),
        CalculatorToken.MULTIPLY.symbol to Operator (symbol = CalculatorToken.MULTIPLY.symbol, 5),
        CalculatorToken.DIVIDE.symbol to Operator (symbol = CalculatorToken.DIVIDE.symbol, 5),
    )

    fun setUid(uid: String) {
        this.uid = uid
    }

    fun getStringExpression() : String {
        return currentExpression.replace(" ", "")
    }

    fun getEvaluated() : String {
        return if (currentResult == "Infinity") "∞" else currentResult
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
            CalculatorToken.ZERO -> {
                isCurrentExpressionSaved = false
                addDigit(token.symbol)
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

            CalculatorToken.ADD -> {
                addOperator(token.symbol)
                return
            }

            CalculatorToken.MULTIPLY -> {
                addOperator(token.symbol)
                return
            }

            CalculatorToken.SUBTRACT -> {
                addOperator(token.symbol)
                return
            }


            CalculatorToken.DIVIDE -> {
                addOperator(token.symbol)
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


                    if (currentResult != "Error")
                        currentExpression = currentResult
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

    private fun addOperator(symbol: String) {
        val trimmed = currentExpression.replace(" ", "")

        if (trimmed.length < 2) {
            currentExpression += " $symbol ";
            return;
        }

        if (supportedOperators.containsKey(currentExpression[currentExpression.length - 2].toString())) {
            currentExpression = currentExpression.dropLast(3);
        }
        currentExpression += " $symbol ";
    }

    private fun clearExpression() {
        currentExpression = "0"
    }

    private fun clearLastSymbolOfExpression() {
        currentExpression = if (currentExpression.last() == ' ')  {
            currentExpression.dropLast(3)
        } else {
            currentExpression.dropLast(1)
        }

        if (currentExpression.isEmpty()) {
            currentExpression = "0"
        }
    }

    private fun evaluateExpression() {
        val rpn = convertInfixToRPN(currentExpression);
        val stack = Stack<Double>();

        for (token in rpn) {
            if (!stack.isEmpty() && supportedOperators.containsKey(token)) {
                val right = stack.pop()
                val left = stack.pop()
                var result = 0.0

                when (token) {
                    CalculatorToken.ADD.symbol ->
                        result = left + right
                    CalculatorToken.SUBTRACT.symbol ->
                        result = left - right
                    CalculatorToken.MULTIPLY.symbol ->
                        result = left * right
                    CalculatorToken.DIVIDE.symbol ->
                        result = left / right
                }

                stack.push(result);
            }
            else {
                stack.push(token.toDouble())
            }
        }

        currentResult = stack.pop().toString()
    }

    private fun convertInfixToRPN(infix: String): MutableList<String> {
        val tokens = infix.split(" ");
        val result = mutableListOf<String>()
        val stack = Stack<String>()

        for (token in tokens) {
            if (supportedOperators.containsKey(token)) {
                while (!stack.isEmpty() && supportedOperators.containsKey(stack.peek())) {
                    val cOp = supportedOperators[token]
                    val lOp = supportedOperators[stack.peek()]
                    if (cOp!!.precedence <= lOp!!.precedence)
                    {
                        result.add(stack.pop())
                        continue
                    }
                    break
                }
                stack.push(token)
            } else {
                result.add(token)
            }

        }

        while (!stack.isEmpty()) {
            result.add(stack.pop());
        }

        return result;
    }
}

class Operator(var symbol: String, var precedence: Int) {

}