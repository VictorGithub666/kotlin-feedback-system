package com.example.feedback_system.user

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.feedback_system.R
import com.example.feedback_system.api.ApiClient
import com.example.feedback_system.models.Question
import com.example.feedback_system.utils.FeedbackSession
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class QuestionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuestionScreen()
        }
    }
}

@Composable
fun QuestionScreen() {
    val context = LocalContext.current
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var selectedRating by remember { mutableStateOf(0) }
    var questions by remember { mutableStateOf<List<Question>>(emptyList()) }

    val trainerName = FeedbackSession.selectedTrainerName ?: "Unknown"
    val module = FeedbackSession.selectedModuleName ?: "Unknown"
    val username = FeedbackSession.currentUsername ?: "Anonymous"

    // Load questions from API
    LaunchedEffect(Unit) {
        ApiClient.apiService.getQuestions().enqueue(object : Callback<List<Question>> {
            override fun onResponse(call: Call<List<Question>>, response: Response<List<Question>>) {
                if (response.isSuccessful) {
                    questions = response.body() ?: emptyList()
                } else {
                    Toast.makeText(context, "Failed to load questions (response error)", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Question>>, t: Throwable) {
                Toast.makeText(context, "Failed to load questions", Toast.LENGTH_SHORT).show()
            }
        })
    }

    if (questions.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (currentQuestionIndex >= questions.size) {
        // Go to preview screen
        LaunchedEffect(Unit) {
            context.startActivity(Intent(context, FeedbackPreviewActivity::class.java))
        }
        return
    }

    val currentQuestion = questions[currentQuestionIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Question ${currentQuestionIndex + 1} of ${questions.size}",
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = currentQuestion.questionText,
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Row(
            modifier = Modifier
                .padding(bottom = 32.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            for (i in 1..5) {
                Image(
                    painter = painterResource(
                        id = when (i) {
                            1 -> R.drawable.rating1
                            2 -> R.drawable.rating2
                            3 -> R.drawable.rating3
                            4 -> R.drawable.rating4
                            else -> R.drawable.rating5
                        }
                    ),
                    contentDescription = "Rating $i",
                    modifier = Modifier
                        .size(64.dp)
                        .clickable {
                            selectedRating = i
                        }
                )
            }
        }

        if (selectedRating > 0) {
            Text(
                text = "Selected: $selectedRating",
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                // Save answer for current question
                FeedbackSession.questionAnswers[currentQuestion.questionNumber] = selectedRating
                selectedRating = 0
                currentQuestionIndex++
            },
            enabled = selectedRating > 0,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (currentQuestionIndex < questions.size - 1) "Next" else "Preview Feedback"
            )
        }
    }
}
