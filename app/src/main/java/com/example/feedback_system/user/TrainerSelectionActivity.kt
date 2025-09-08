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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.feedback_system.R
import com.example.feedback_system.api.ApiClient
import com.example.feedback_system.models.Trainer
import com.example.feedback_system.utils.FeedbackSession
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
    val username = FeedbackSession.currentUsername ?: ""

    LaunchedEffect(Unit) {
        ApiClient.apiService.getTrainers().enqueue(object : Callback<List<Trainer>> {
            override fun onResponse(call: Call<List<Trainer>>, response: Response<List<Trainer>>) {
                if (response.isSuccessful) {
                    trainers = response.body() ?: emptyList()
                }
            }

            override fun onFailure(call: Call<List<Trainer>>, t: Throwable) {
                // Handle error
            }
        })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Select Trainer",
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

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
            }
        }
    }
}

@Composable
fun TrainerCard(trainer: Trainer, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.thank_you),
                contentDescription = "Trainer Image",
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = trainer.trainerName,
                fontSize = 18.sp
            )
        }
    }
}