package com.example.feedback_system.user

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.feedback_system.utils.FeedbackSession

class FeedbackPreviewActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FeedbackPreviewScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackPreviewScreen() {
    val context = LocalContext.current
    val username = FeedbackSession.currentUsername ?: "N/A"
    val trainer = FeedbackSession.selectedTrainerName ?: "N/A"
    val module = FeedbackSession.selectedModuleName ?: "N/A"
    val answers = FeedbackSession.questionAnswers.toSortedMap()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Feedback Preview",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text("Username: $username", fontSize = 16.sp)
            Text("Trainer: $trainer", fontSize = 16.sp)
            Text("Module: $module", fontSize = 16.sp)

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(answers.entries.toList()) { entry ->
                    Text("Question ${entry.key}: Rating ${entry.value}", fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    context.startActivity(Intent(context, FeedbackSubmitActivity::class.java))
                }
            ) {
                Text("Submit Feedback")
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    (context as? android.app.Activity)?.finish()
                }
            ) {
                Text("Go Back")
            }
        }
    }
}