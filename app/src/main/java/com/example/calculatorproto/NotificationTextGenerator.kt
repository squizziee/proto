package com.example.calculatorproto

import kotlin.random.Random

class NotificationTextGenerator {

    private val titles = listOf(
        "Come on, man",
        "Pssss...",
        "Ayo",
        "It`s time",
    )

    private val texts = listOf(
        "Come hit them numbers up",
        "Getting stale?",
        "Gotta keep up with them maths brother",
        "How 'bout press some buttons?",
    )

    fun generateTitle(): String {
        return titles[Random.nextInt(titles.size)]
    }

    fun generateText(): String {
        return texts[Random.nextInt(titles.size)]
    }

}