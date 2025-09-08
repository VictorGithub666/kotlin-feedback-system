package com.example.feedback_system.user

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.feedback_system.api.ApiClient
import com.example.feedback_system.utils.FeedbackSession
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun SubmitAllFeedback(
    username: String,
    trainerName: String,
    module: String,
    onDone: () -> Unit
) {
    val context = LocalContext.current

    if (username == "Unknown" || trainerName == "Unknown" || module == "Unknown") {
        Toast.makeText(context, "Missing required feedback data", Toast.LENGTH_LONG).show()
        return
    }

    val submission = mutableMapOf<String, Any>(
        "username" to username,
        "trainerName" to trainerName,
        "module" to module
    )

    for (i in 1..10) {
        val rating = FeedbackSession.questionAnswers[i] ?: 0
        submission["question$i"] = rating
    }

    println("📤 Submitting Feedback: $submission")

    LaunchedEffect(Unit) {
        ApiClient.apiService.submitFeedback(submission).enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Feedback submitted!", Toast.LENGTH_SHORT).show()
                    FeedbackSession.reset()
                    onDone()
                } else {
                    Toast.makeText(context, "Error code: ${response.code()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}
