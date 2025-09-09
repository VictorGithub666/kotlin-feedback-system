package com.example.feedback_system.admin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.feedback_system.api.ApiClient
import com.example.feedback_system.models.Question
import com.example.feedback_system.ui.theme.FeedbacksystemTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.SocketTimeoutException
import java.net.ConnectException
import javax.net.ssl.SSLHandshakeException
import java.io.IOException

class QuestionsListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FeedbacksystemTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    QuestionsApp()
                }
            }
        }
    }
}

@Composable
fun QuestionsApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "questionsList") {
        composable("questionsList") {
            QuestionsListScreen(
                onAddQuestion = { navController.navigate("addQuestion") },
                onEditQuestion = { questionId ->
                    navController.navigate("editQuestion/$questionId")
                }
            )
        }
        composable("addQuestion") {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.White
            ) {
                AddEditQuestionScreen(
                    isEditMode = false,
                    onBack = { navController.popBackStack() }
                )
            }
        }
        composable("editQuestion/{questionId}") { backStackEntry ->
            val questionId = backStackEntry.arguments?.getString("questionId")?.toIntOrNull()
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.White
            ) {
                AddEditQuestionScreen(
                    isEditMode = true,
                    questionId = questionId,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionsListScreen(
    onAddQuestion: () -> Unit,
    onEditQuestion: (Int) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var questions by remember { mutableStateOf<List<Question>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedQuestion by remember { mutableStateOf<Question?>(null) }

    // Fetch questions on launch
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                val response = ApiClient.apiService.getAllQuestions().execute()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        questions = response.body() ?: emptyList()
                    } else {
                        // Fallback to select-questions if getAllQuestions fails
                        val fallbackResponse = ApiClient.apiService.getQuestions().execute()
                        if (fallbackResponse.isSuccessful) {
                            questions = fallbackResponse.body() ?: emptyList()
                        }
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
                        "Manage Questions",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { (context as QuestionsListActivity).finish() }) {
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
                    onClick = onAddQuestion,
                    containerColor = Color(0xFFD32F2F),
                    contentColor = Color.White,

                    ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Question")
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
        } else if (questions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No questions found",
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
                items(questions) { question ->
                    QuestionItem(
                        question = question,
                        onEdit = { question.id?.let { onEditQuestion(it) } },
                        onDelete = {
                            question.id?.let {
                                selectedQuestion = question
                                showDeleteDialog = true
                            }
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
            title = { Text("Delete Question") },
            text = { Text("Are you sure you want to delete this question?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch(Dispatchers.IO) {
                            selectedQuestion?.id?.let { id ->
                                try {
                                    val response = ApiClient.apiService.deleteQuestion(id).execute()
                                    withContext(Dispatchers.Main) {
                                        if (response.isSuccessful) {
                                            questions = questions.filter { it.id != id }
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
fun QuestionItem(
    question: Question,
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
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Q${question.questionNumber}: ${question.questionText}",
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
fun AddEditQuestionScreen(
    isEditMode: Boolean,
    questionId: Int? = null,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var questionText by remember { mutableStateOf("") }
    var questionNumber by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // If editing, load question data
    LaunchedEffect(isEditMode) {
        if (isEditMode && questionId != null) {
            withContext(Dispatchers.IO) {
                try {
                    val response = ApiClient.apiService.getQuestion(questionId).execute()
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            response.body()?.let { question ->
                                questionText = question.questionText
                                questionNumber = question.questionNumber.toString()
                            }
                        } else {
                            errorMessage = "Failed to load question: ${response.code()}"
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        errorMessage = "Error loading question: ${e.message}"
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
                        if (isEditMode) "Edit Question" else "Add Question",
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

            OutlinedTextField(
                value = questionNumber,
                onValueChange = { questionNumber = it },
                label = { Text("Question Number", color = Color.Black) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedBorderColor = Color(0xFFD32F2F),
                    unfocusedBorderColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = questionText,
                onValueChange = { questionText = it },
                label = { Text("Question Text", color = Color.Black) },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
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
                    if (questionNumber.toIntOrNull() == null) {
                        errorMessage = "Please enter a valid question number"
                        return@Button
                    }

                    isLoading = true
                    errorMessage = null

                    scope.launch(Dispatchers.IO) {
                        try {
                            val questionData = mapOf(
                                "questionText" to questionText,
                                "questionNumber" to questionNumber.toInt()
                            )

                            val response = if (isEditMode && questionId != null) {
                                ApiClient.apiService.updateQuestion(questionId, questionData).execute()
                            } else {
                                ApiClient.apiService.createQuestion(questionData).execute()
                            }

                            withContext(Dispatchers.Main) {
                                if (response.isSuccessful) {
                                    onBack()
                                } else {
                                    val errorCode = response.code()
                                    val errorBody = response.errorBody()?.string() ?: "No error body"
                                    errorMessage = "Server error: $errorCode - $errorBody"
                                }
                            }
                        } catch (e: SocketTimeoutException) {
                            withContext(Dispatchers.Main) {
                                errorMessage = "Connection timeout. Please check your network."
                            }
                        } catch (e: ConnectException) {
                            withContext(Dispatchers.Main) {
                                errorMessage = "Cannot connect to server. Check if server is running."
                            }
                        } catch (e: SSLHandshakeException) {
                            withContext(Dispatchers.Main) {
                                errorMessage = "SSL error. Try using HTTPS or check certificate."
                            }
                        } catch (e: IOException) {
                            withContext(Dispatchers.Main) {
                                errorMessage = "Network error: ${e.message ?: "Unknown IO error"}"
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                errorMessage = "Unexpected error: ${e.message}"
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
                enabled = questionText.isNotBlank() && questionNumber.isNotBlank() && !isLoading,
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
                    Text(if (isEditMode) "Update Question" else "Add Question")
                }
            }


        }
    }
}