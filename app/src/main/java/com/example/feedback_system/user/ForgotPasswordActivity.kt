package com.example.feedback_system.user

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.feedback_system.R
import com.example.feedback_system.api.ApiClient
import com.example.feedback_system.ui.theme.FeedbacksystemTheme
import com.example.feedback_system.ui.theme.RedButton
import com.example.feedback_system.ui.theme.StyledOutlinedTextField
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FeedbacksystemTheme {
                ForgotPasswordScreen()
            }
        }
    }
}

@Composable
fun ForgotPasswordScreen() {
    val context = LocalContext.current
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.istlogo),
            contentDescription = "IST Logo",
            modifier = Modifier.size(150.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Forgot Password",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.Red
        )

        Spacer(modifier = Modifier.height(16.dp))

        StyledOutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = "Username"
        )

        Spacer(modifier = Modifier.height(8.dp))

        StyledOutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email"
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            RedButton(text = "Verify Credentials") {
                if (username.isBlank() || email.isBlank()) {
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    return@RedButton
                }

                isLoading = true
                val data = mapOf("username" to username, "email" to email)

                ApiClient.apiService.forgotPassword(data).enqueue(object : Callback<Map<String, Any>> {
                    override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                        isLoading = false
                        if (response.isSuccessful) {
                            val responseBody = response.body()
                            if (responseBody != null) {
                                val userId = (responseBody["userId"] as? Number)?.toInt()

                                if (userId != null) {
                                    val intent = Intent(context, ResetPasswordActivity::class.java).apply {
                                        putExtra("USER_ID", userId)
                                    }
                                    context.startActivity(intent)
                                }else {
                                    val message = responseBody?.get("message") as? String ?: "Invalid credentials"
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                val errorMsg = response.errorBody()?.string() ?: "Unknown error"
                                Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, "Invalid username or email", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                        isLoading = false
                        Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = {
                context.startActivity(Intent(context, LoginActivity::class.java))
            }
        ) {
            Text("Back to Login")
        }
    }
}