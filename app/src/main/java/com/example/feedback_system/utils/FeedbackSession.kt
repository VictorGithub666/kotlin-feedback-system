// FeedbackSession.kt
package com.example.feedback_system.utils

object FeedbackSession {
    var currentUsername: String? = null
    var userRole: Int? = null // 0 = user, 1 = admin
    var selectedTrainerName: String? = null
    var selectedModuleName: String? = null
    val questionAnswers = mutableMapOf<Int, Int>() // questionNumber to rating

    fun reset() {
        currentUsername = null
        userRole = null
        selectedTrainerName = null
        selectedModuleName = null
        questionAnswers.clear()
    }
}