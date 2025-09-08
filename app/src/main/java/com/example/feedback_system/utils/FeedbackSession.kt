package com.example.feedback_system.utils

object FeedbackSession {
    var currentUsername: String? = null
    var selectedTrainerName: String? = null
    var selectedModuleName: String? = null
    val questionAnswers = mutableMapOf<Int, Int>() // questionNumber to rating

    fun reset() {
        currentUsername = null
        selectedTrainerName = null
        selectedModuleName = null
        questionAnswers.clear()
    }
}