package com.example.feedback_system.user

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.feedback_system.R
import com.example.feedback_system.admin.AdminDashboardActivity
import com.example.feedback_system.api.ApiClient
import com.example.feedback_system.ui.theme.FeedbacksystemTheme
import com.example.feedback_system.ui.theme.PasswordTextField
import com.example.feedback_system.ui.theme.RedButton
import com.example.feedback_system.ui.theme.StyledOutlinedTextField
import com.example.feedback_system.utils.FeedbackSession
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FeedbacksystemTheme {
                LoginScreen()
            }
        }
    }
}

@Composable
fun LoginScreen() {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

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

        StyledOutlinedTextField(value = email, onValueChange = { email = it }, label = "Email")
        Spacer(modifier = Modifier.height(8.dp))

        PasswordTextField(value = password, onValueChange = { password = it })

        Spacer(modifier = Modifier.height(8.dp))

        // Forgot Password Link
        Text(
            text = "Forgot Password?",
            modifier = Modifier
                .align(Alignment.End)
                .clickable {
//                    val intent = Intent(context, ForgotPasswordActivity::class.java)
//                    context.startActivity(intent)
                },
            color = Color.Red,
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        RedButton(text = "Login") {
            val data = mapOf("email" to email, "password" to password)
            ApiClient.apiService.login(data).enqueue(object : Callback<Map<String, Any>> {
                override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null) {
                            println("DEBUG: Full response: $responseBody")

                            // Parse the user object correctly
                            val userMap = responseBody["user"] as? Map<String, Any>
                            if (userMap != null) {
                                val username = userMap["username"] as? String ?: ""
                                val role = (userMap["role"] as? Number)?.toInt() ?: 0

                                println("DEBUG: Username: $username, Role: $role")

                                FeedbackSession.currentUsername = username
                                FeedbackSession.userRole = role

                                Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()

                                // Route based on role
                                val intent = if (role == 1) {
                                    Intent(context, AdminDashboardActivity::class.java)
                                } else {
                                    Intent(context, UserDashboardActivity::class.java)
                                }
                                context.startActivity(intent)
                            } else {
                                Toast.makeText(context, "User data not found in response", Toast.LENGTH_SHORT).show()
                                println("DEBUG: User map is null")
                            }
                        } else {
                            Toast.makeText(context, "Empty response body", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Invalid credentials - Code: ${response.code()}", Toast.LENGTH_SHORT).show()
                        println("DEBUG: Response error: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                    Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                    println("DEBUG: Network error: ${t.message}")
                    t.printStackTrace()
                }
            })
        }
    }
}