package com.example.calculatorproto.misc

enum class CalculatorToken(val symbol: String, val displaySymbol: String = symbol) {
    ONE("1"),
    TWO("2"),
    THREE("3"),
    FOUR("4"),
    FIVE("5"),
    SIX("6"),
    SEVEN("7"),
    EIGHT("8"),
    NINE("9"),
    ZERO("0"),

    CLEAR("C"),
    BACKSPACE("⌫"),
    ADD("+"),
    MULTIPLY("*", "×"),
    SUBTRACT("-","–"),
    DIVIDE("/", "÷"),
    FRACTION("."),
    EQUALS("="),

    SIN("sin", "sin"),
    COS("cos", "cos"),
    TAN("tan", "tan"),
    COT("cot", "cot"),
    SQRT("sqrt", "sqrt"),

    OPEN_PARENTHESES("("),
    CLOSE_PARENTHESES(")"),
}