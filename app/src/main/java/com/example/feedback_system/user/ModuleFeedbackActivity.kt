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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.feedback_system.R
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModuleFeedbackScreen() {
    val context = LocalContext.current
    val username = FeedbackSession.currentUsername ?: ""

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Module Feedback",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.Red
                        )
                    )
                },
                navigationIcon = {
                    // Logo on the left
                    Image(
                        painter = painterResource(id = R.drawable.istlogo),
                        contentDescription = "IST Logo",
                        modifier = Modifier.size(40.dp)
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFFFFFFF)
                )
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Module Feedback",
                    fontSize = 24.sp,
                    modifier = Modifier.padding(bottom = 32.dp),
                    color = Color.Red
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
    }
}