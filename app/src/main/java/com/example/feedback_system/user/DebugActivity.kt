// DebugActivity.kt
package com.example.feedback_system.user

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.feedback_system.utils.FeedbackSession
import com.example.feedback_system.ui.theme.FeedbacksystemTheme

class DebugActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FeedbacksystemTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Debug Info",
                        fontSize = 24.sp
                    )
                    Text(
                        text = "Username: ${FeedbackSession.currentUsername ?: "None"}",
                        fontSize = 16.sp
                    )
                    Text(
                        text = "User Role: ${FeedbackSession.userRole}",
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Role Type: ${if (FeedbackSession.userRole == 1) "Admin" else "User"}",
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}