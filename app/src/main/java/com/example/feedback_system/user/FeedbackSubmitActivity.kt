package com.example.feedback_system.user

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import com.example.feedback_system.utils.FeedbackSession
import com.example.feedback_system.user.SubmitAllFeedback


class FeedbackSubmitActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SubmitAllFeedback(
                username = FeedbackSession.currentUsername ?: "Unknown",
                trainerName = FeedbackSession.selectedTrainerName ?: "Unknown",
                module = FeedbackSession.selectedModuleName ?: "Unknown",
                onDone = {
                    startActivity(Intent(this, ThankYouActivity::class.java))
                    finish()
                }
            )
        }
    }
}
