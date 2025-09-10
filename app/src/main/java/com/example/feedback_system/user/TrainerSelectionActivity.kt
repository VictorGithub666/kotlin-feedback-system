package com.example.feedback_system.user

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.feedback_system.R
import com.example.feedback_system.api.ApiClient
import com.example.feedback_system.models.Trainer
import com.example.feedback_system.utils.FeedbackSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TrainerSelectionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get username from intent and store in session
        FeedbackSession.currentUsername = intent.getStringExtra("USERNAME") ?: ""

        setContent {
            TrainerSelectionScreen()
        }
    }
}

@Composable
fun TrainerSelectionScreen() {
    val context = LocalContext.current
    var trainers by remember { mutableStateOf<List<Trainer>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val username = FeedbackSession.currentUsername ?: ""

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                val response = ApiClient.apiService.getTrainers().execute()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        trainers = response.body() ?: emptyList()
                    }
                    isLoading = false
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    isLoading = false
                    e.printStackTrace()
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Select Trainer",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (trainers.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No trainers available")
            }
        } else {
            LazyColumn {
                items(trainers) { trainer ->
                    TrainerCard(
                        trainer = trainer,
                        onClick = {
                            FeedbackSession.selectedTrainerName = trainer.trainerName
                            val intent = Intent(context, ModuleSelectionActivity::class.java).apply {
                                putExtra("USERNAME", username)
                                putExtra("TRAINER_NAME", trainer.trainerName)
                            }
                            context.startActivity(intent)
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun TrainerCard(trainer: Trainer, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Trainer Image - using the same approach as admin side
            if (!trainer.trainerImg.isNullOrEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current)
                            .data(trainer.trainerImg)
                            .build()
                    ),
                    contentDescription = "Trainer Image",
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(16.dp))
            } else {
                // Placeholder image if no image is available
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current)
                            .data(R.drawable.thank_you) // Using the thank_you drawable as placeholder
                            .build()
                    ),
                    contentDescription = "Trainer Placeholder",
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(16.dp))
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = trainer.trainerName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black
                )
            }
        }
    }
}