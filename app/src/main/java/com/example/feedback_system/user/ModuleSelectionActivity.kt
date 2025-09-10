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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.feedback_system.R
import com.example.feedback_system.api.ApiClient
import com.example.feedback_system.models.Module
import com.example.feedback_system.utils.FeedbackSession
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ModuleSelectionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ModuleSelectionScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModuleSelectionScreen() {
    val context = LocalContext.current
    var modules by remember { mutableStateOf<List<Module>>(emptyList()) }
    val trainerName = FeedbackSession.selectedTrainerName ?: "Unknown"

    LaunchedEffect(Unit) {
        ApiClient.apiService.getModules().enqueue(object : Callback<List<Module>> {
            override fun onResponse(call: Call<List<Module>>, response: Response<List<Module>>) {
                if (response.isSuccessful) {
                    modules = response.body() ?: emptyList()
                }
            }

            override fun onFailure(call: Call<List<Module>>, t: Throwable) {
                // Handle error
            }
        })
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Select Module",
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
            Text(
                text = "Select Module for $trainerName",
                fontSize = 24.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn {
                items(modules) { module ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable {
                                FeedbackSession.selectedModuleName = module.module
                                val intent = Intent(context, QuestionActivity::class.java).apply {
                                    putExtra("USERNAME", FeedbackSession.currentUsername)
                                    putExtra("TRAINER_NAME", FeedbackSession.selectedTrainerName)
                                    putExtra("MODULE_NAME", module.module)
                                }
                                context.startActivity(intent)
                            }
                    ) {
                        Text(
                            text = module.module,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}