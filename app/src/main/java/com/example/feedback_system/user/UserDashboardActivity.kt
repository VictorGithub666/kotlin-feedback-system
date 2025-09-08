package com.example.feedback_system.user

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.feedback_system.R
import com.example.feedback_system.ui.theme.FeedbacksystemTheme
import com.example.feedback_system.ui.theme.RedButton
import com.example.feedback_system.utils.FeedbackSession

class UserDashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FeedbacksystemTheme {
                UserDashboardScreen()
            }
        }
    }
}

@Composable
fun UserDashboardScreen() {
    val context = LocalContext.current
    val username = FeedbackSession.currentUsername ?: ""

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.istlogo),
            contentDescription = "IST Logo",
            modifier = Modifier.size(150.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Welcome to IST Feedback System",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(32.dp))

        RedButton(
            text = "Provide Module Feedback"
        ) {
            val intent = Intent(context, ModuleFeedbackActivity::class.java).apply {
                putExtra("USERNAME", username)
            }
            context.startActivity(intent)
        }
    }
}