package com.example.feedback_system.user

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.feedback_system.ui.theme.FeedbacksystemTheme
import com.example.feedback_system.ui.theme.RedButton
import com.example.feedback_system.utils.FeedbackSession

class ModuleFeedbackActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get username from intent and store in session
        FeedbackSession.currentUsername = intent.getStringExtra("USERNAME") ?: ""

        setContent {
            FeedbacksystemTheme {
                ModuleFeedbackScreen()
            }
        }
    }
}

@Composable
fun ModuleFeedbackScreen() {
    val context = LocalContext.current
    val username = FeedbackSession.currentUsername ?: ""

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Module Feedback",
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        RedButton(
            text = "Select Trainer"
        ) {
            val intent = Intent(context, TrainerSelectionActivity::class.java).apply {
                putExtra("USERNAME", username)
            }
            context.startActivity(intent)
        }
    }
}