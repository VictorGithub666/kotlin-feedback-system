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
import com.example.feedback_system.ui.theme.PasswordTextField
import com.example.feedback_system.ui.theme.RedButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResetPasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userId = intent.getIntExtra("USER_ID", -1)

        setContent {
            FeedbacksystemTheme {
                ResetPasswordScreen(userId = userId)
            }
        }
    }
}

@Composable
fun ResetPasswordScreen(userId: Int) {
    val context = LocalContext.current
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
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
            text = "Reset Password",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.Red
        )

        Spacer(modifier = Modifier.height(16.dp))

        PasswordTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = "New Password"
        )

        Spacer(modifier = Modifier.height(8.dp))

        PasswordTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = "Confirm Password"
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            RedButton(text = "Reset Password") {
                if (newPassword.isBlank() || confirmPassword.isBlank()) {
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    return@RedButton
                }

                if (newPassword != confirmPassword) {
                    Toast.makeText(context, "Passwords don't match", Toast.LENGTH_SHORT).show()
                    return@RedButton
                }

                if (newPassword.length < 6) {
                    Toast.makeText(context, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                    return@RedButton
                }

                isLoading = true
                val data = mapOf(
                    "userId" to userId,
                    "newPassword" to newPassword
                )

                ApiClient.apiService.resetPassword(data).enqueue(object : Callback<Map<String, Any>> {
                    override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                        isLoading = false
                        if (response.isSuccessful) {
                            Toast.makeText(context, "Password reset successfully", Toast.LENGTH_SHORT).show()
                            context.startActivity(Intent(context, LoginActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            })
                        } else {
                            Toast.makeText(context, "Failed to reset password", Toast.LENGTH_SHORT).show()
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