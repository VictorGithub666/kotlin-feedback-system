package com.example.feedback_system.admin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.feedback_system.api.ApiClient
import com.example.feedback_system.models.Trainer
import com.example.feedback_system.ui.theme.FeedbacksystemTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody


class TrainersListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FeedbacksystemTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    TrainersApp()
                }
            }
        }
    }
}

@Composable
fun TrainersApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "trainersList") {
        composable("trainersList") {
            TrainersListScreen(
                onAddTrainer = { navController.navigate("addTrainer") },
                onEditTrainer = { trainerId ->
                    navController.navigate("editTrainer/$trainerId")
                }
            )
        }
        composable("addTrainer") {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.White
            ) {
                AddEditTrainerScreen(
                    isEditMode = false,
                    onBack = { navController.popBackStack() }
                )
            }
        }
        composable("editTrainer/{trainerId}") { backStackEntry ->
            val trainerId = backStackEntry.arguments?.getString("trainerId")?.toIntOrNull()
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.White
            ) {
                AddEditTrainerScreen(
                    isEditMode = true,
                    trainerId = trainerId,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainersListScreen(
    onAddTrainer: () -> Unit,
    onEditTrainer: (Int) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var trainers by remember { mutableStateOf<List<Trainer>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedTrainer by remember { mutableStateOf<Trainer?>(null) }

    // Fetch trainers on launch
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

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Manage Trainers",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { (context as TrainersListActivity).finish() }) {
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
        },
        floatingActionButton = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.BottomStart
            ) {
                FloatingActionButton(
                    onClick = onAddTrainer,
                    containerColor = Color(0xFFD32F2F),
                    contentColor = Color.White,
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Trainer")
                }
            }
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFFD32F2F))
            }
        } else if (trainers.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No trainers found",
                    color = Color.Black
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                items(trainers) { trainer ->
                    TrainerItem(
                        trainer = trainer,
                        onEdit = { trainer.id.let { onEditTrainer(it) } },
                        onDelete = {
                            selectedTrainer = trainer
                            showDeleteDialog = true
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }

    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Trainer") },
            text = { Text("Are you sure you want to delete this trainer?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch(Dispatchers.IO) {
                            selectedTrainer?.id?.let { id ->
                                try {
                                    val response = ApiClient.apiService.deleteTrainer(id).execute()
                                    withContext(Dispatchers.Main) {
                                        if (response.isSuccessful) {
                                            trainers = trainers.filter { it.id != id }
                                        }
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                            withContext(Dispatchers.Main) {
                                showDeleteDialog = false
                            }
                        }
                    }
                ) {
                    Text("Delete", color = Color(0xFFD32F2F))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun TrainerItem(
    trainer: Trainer,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Trainer Image
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

            IconButton(onClick = onEdit) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = Color(0xFFD32F2F)
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color(0xFFD32F2F)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTrainerScreen(
    isEditMode: Boolean,
    trainerId: Int? = null,
    onBack: () -> Unit
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var trainerName by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var currentServerImageUrl by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val getContent = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedImageUri = it }
    }

    // If editing, load trainer data
    LaunchedEffect(isEditMode) {
        if (isEditMode && trainerId != null) {
            withContext(Dispatchers.IO) {
                try {
                    val response = ApiClient.apiService.getTrainer(trainerId).execute()
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            response.body()?.let { trainer ->
                                trainerName = trainer.trainerName
                                currentServerImageUrl = trainer.trainerImg
                                // Don't set selectedImageUri for server URLs
                                // We'll handle server images differently
                            }
                        } else {
                            errorMessage = "Failed to load trainer: ${response.code()}"
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        errorMessage = "Error loading trainer: ${e.message}"
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        if (isEditMode) "Edit Trainer" else "Add Trainer",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
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
                    .padding(16.dp)
            ) {
                // Error message
                errorMessage?.let { message ->
                    Text(
                        text = message,
                        color = Color.Red,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                // Image selection section
                Text(
                    text = "Trainer Image",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Display current image (either selected or server image)
                    if (selectedImageUri != null) {
                        // Show selected local image
                        Image(
                            painter = rememberAsyncImagePainter(selectedImageUri),
                            contentDescription = "Selected Image",
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else if (!currentServerImageUrl.isNullOrEmpty()) {
                        // Show server image
                        Image(
                            painter = rememberAsyncImagePainter(
                                ImageRequest.Builder(LocalContext.current)
                                    .data(currentServerImageUrl)
                                    .build()
                            ),
                            contentDescription = "Current Trainer Image",
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        // Show placeholder
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No Image", color = Color.DarkGray)
                        }
                    }

                    // Image selection button
                    Button(
                        onClick = { getContent.launch("image/*") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFD32F2F),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Change Image")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = trainerName,
                    onValueChange = { trainerName = it },
                    label = { Text("Trainer Name", color = Color.Black) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedBorderColor = Color(0xFFD32F2F),
                        unfocusedBorderColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (trainerName.isBlank()) {
                            errorMessage = "Please enter a trainer name"
                            return@Button
                        }

                        isLoading = true
                        errorMessage = null

                        scope.launch(Dispatchers.IO) {
                            try {
                                if (selectedImageUri != null) {
                                    // Handle NEW image upload
                                    val uri = selectedImageUri!!
                                    val inputStream = context.contentResolver.openInputStream(uri)
                                    val file =
                                        File.createTempFile("trainer_img", ".jpg", context.cacheDir)
                                    FileOutputStream(file).use { output ->
                                        inputStream?.copyTo(output)
                                    }

                                    val mediaType = "image/jpeg".toMediaType()
                                    val requestFile = file.asRequestBody(mediaType)

                                    val imagePart = MultipartBody.Part.createFormData(
                                        "trainerImg",
                                        file.name,
                                        requestFile
                                    )
                                    val namePart = RequestBody.create(
                                        MultipartBody.FORM,
                                        trainerName
                                    )

                                    val response = if (isEditMode && trainerId != null) {
                                        ApiClient.apiService.updateTrainerWithImage(
                                            trainerId,
                                            namePart,
                                            imagePart
                                        ).execute()
                                    } else {
                                        ApiClient.apiService.createTrainerWithImage(
                                            namePart,
                                            imagePart
                                        ).execute()
                                    }

                                    withContext(Dispatchers.Main) {
                                        if (response.isSuccessful) {
                                            onBack()
                                        } else {
                                            errorMessage = "Server error: ${response.code()}"
                                        }
                                    }
                                } else {
                                    // No NEW image selected - use text-only update
                                    val trainerData = mapOf(
                                        "trainerName" to trainerName
                                    )

                                    val response = if (isEditMode && trainerId != null) {
                                        ApiClient.apiService.updateTrainer(trainerId, trainerData)
                                            .execute()
                                    } else {
                                        ApiClient.apiService.createTrainer(trainerData).execute()
                                    }

                                    withContext(Dispatchers.Main) {
                                        if (response.isSuccessful) {
                                            onBack()
                                        } else {
                                            errorMessage = "Server error: ${response.code()}"
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
                                    errorMessage = "Error: ${e.message}"
                                    e.printStackTrace()
                                }
                            } finally {
                                withContext(Dispatchers.Main) {
                                    isLoading = false
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    enabled = trainerName.isNotBlank() && !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD32F2F),
                        contentColor = Color.White
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(if (isEditMode) "Update Trainer" else "Add Trainer")
                    }
                }
            }
        }
    }
}