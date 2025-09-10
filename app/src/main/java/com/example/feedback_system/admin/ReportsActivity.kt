package com.example.feedback_system.admin

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.feedback_system.api.ApiClient
import com.example.feedback_system.ui.theme.FeedbacksystemTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReportsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FeedbacksystemTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    ReportsScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen() {
    val context = LocalContext.current
    var selectedReportType by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Reports",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { (context as ReportsActivity).finish() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFD32F2F)
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
            when (selectedReportType) {
                "" -> ReportSelectionScreen { selectedReportType = it }
                "trainer" -> TrainerReportsScreen { selectedReportType = "" }
                "module" -> ModuleReportsScreen { selectedReportType = "" }
            }
        }
    }
}

@Composable
fun ReportSelectionScreen(onReportSelected: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Trainer Reports Card
        ReportCard(
            title = "Trainer Reports",
            description = "View detailed reports for individual trainers",
            onClick = { onReportSelected("trainer") }
        )

        // Module Reports Card
        ReportCard(
            title = "Module Reports",
            description = "View module performance from best to poorest",
            onClick = { onReportSelected("module") }
        )
    }
}

@Composable
fun ReportCard(
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFD32F2F),
            contentColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )
            }
        }
    }
}

@Composable
fun TrainerReportsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var trainers by remember { mutableStateOf<List<Map<String, String>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedTrainer by remember { mutableStateOf<String?>(null) }
    var trainerPerformance by remember { mutableStateOf<Map<String, Any>?>(null) }

    // Fetch trainers on launch
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                val response = ApiClient.apiService.getTrainersForReports().execute()
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

    // Fetch trainer performance when selected
    LaunchedEffect(selectedTrainer) {
        if (selectedTrainer != null) {
            withContext(Dispatchers.IO) {
                try {
                    val response = ApiClient.apiService.getTrainerPerformance(selectedTrainer!!).execute()
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            trainerPerformance = response.body()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Back button
        TextButton(onClick = onBack) {
            Text("← Back to Reports", color = Color(0xFFD32F2F))
        }

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFFD32F2F))
            }
        } else if (selectedTrainer == null) {
            // Trainer selection list
            Text(
                "Select a Trainer:",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn {
                items(trainers) { trainer ->
                    TrainerReportItem(
                        trainerName = trainer["trainerName"] ?: "",
                        onClick = { selectedTrainer = trainer["trainerName"] }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        } else {
            // Trainer performance details
            TrainerPerformanceDetails(
                trainerName = selectedTrainer!!,
                performanceData = trainerPerformance,
                onBack = { selectedTrainer = null }
            )
        }
    }
}

@Composable
fun TrainerReportItem(trainerName: String, onClick: () -> Unit) {
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = trainerName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun TrainerPerformanceDetails(
    trainerName: String,
    performanceData: Map<String, Any>?,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Back button
        TextButton(onClick = onBack) {
            Text("← Back to Trainers", color = Color(0xFFD32F2F))
        }

        Text(
            "Performance Report for: $trainerName",
            style = MaterialTheme.typography.titleLarge,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (performanceData == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFFD32F2F))
            }
        } else {
            LazyColumn {
                item {
                    PerformanceMetric(
                        title = "Overall Performance",
                        value = performanceData["overallPerformance"]?.toString() ?: "N/A"
                    )
                }
                item {
                    PerformanceMetric(
                        title = "Module Understanding",
                        value = performanceData["moduleUnderstanding"]?.toString() ?: "N/A"
                    )
                }
                item {
                    PerformanceMetric(
                        title = "Trainer Interaction",
                        value = performanceData["trainerInteraction"]?.toString() ?: "N/A"
                    )
                }
                item {
                    PerformanceMetric(
                        title = "Trainer Punctuality",
                        value = performanceData["punctuality"]?.toString() ?: "N/A"
                    )
                }
                item {
                    PerformanceMetric(
                        title = "Module Content",
                        value = performanceData["moduleContent"]?.toString() ?: "N/A"
                    )
                }
                item {
                    PerformanceMetric(
                        title = "Trainer Assistance",
                        value = performanceData["assistance"]?.toString() ?: "N/A"
                    )
                }
                item {
                    PerformanceMetric(
                        title = "Trainer Hands On",
                        value = performanceData["handsOn"]?.toString() ?: "N/A"
                    )
                }
                item {
                    PerformanceMetric(
                        title = "Trainer Knowledge",
                        value = performanceData["knowledge"]?.toString() ?: "N/A"
                    )
                }
                item {
                    PerformanceMetric(
                        title = "Trainer Overall",
                        value = performanceData["overall"]?.toString() ?: "N/A"
                    )
                }
            }
        }
    }
}

@Composable
fun PerformanceMetric(title: String, value: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black
            )
            Text(
                text = if (value != "N/A") "$value%" else value,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = if (value != "N/A") {
                    val numericValue = value.toFloatOrNull() ?: 0f
                    when {
                        numericValue >= 80 -> Color(0xFF4CAF50) // Green for good performance
                        numericValue >= 60 -> Color(0xFFFFC107) // Amber for average performance
                        else -> Color(0xFFF44336) // Red for poor performance
                    }
                } else {
                    Color.Gray
                }
            )
        }
    }
}

@Composable
fun ModuleReportsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var modules by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Fetch modules on launch
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                val response = ApiClient.apiService.getModulePerformance().execute()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        modules = response.body() ?: emptyList()
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

    Column(modifier = Modifier.fillMaxSize()) {
        // Back button
        TextButton(onClick = onBack) {
            Text("← Back to Reports", color = Color(0xFFD32F2F))
        }

        Text(
            "Module Performance Reports (Best to Poorest):",
            style = MaterialTheme.typography.titleLarge,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFFD32F2F))
            }
        } else if (modules.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No module data available", color = Color.Black)
            }
        } else {
            LazyColumn {
                items(modules) { module ->
                    ModuleReportItem(
                        moduleName = module["module"]?.toString() ?: "",
                        performance = module["performancePercentage"]?.toString() ?: "N/A"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun ModuleReportItem(moduleName: String, performance: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = moduleName,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black
            )
            Text(
                text = if (performance != "N/A") "$performance%" else performance,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = if (performance != "N/A") {
                    val numericValue = performance.toFloatOrNull() ?: 0f
                    when {
                        numericValue >= 80 -> Color(0xFF4CAF50) // Green for good performance
                        numericValue >= 60 -> Color(0xFFFFC107) // Amber for average performance
                        else -> Color(0xFFF44336) // Red for poor performance
                    }
                } else {
                    Color.Gray
                }
            )
        }
    }
}