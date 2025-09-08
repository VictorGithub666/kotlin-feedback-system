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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.feedback_system.R
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

        Spacer(modifier = Modifier.height(16.dp))

        RedButton(text = "Login") {
            val data = mapOf("email" to email, "password" to password)
            ApiClient.apiService.login(data).enqueue(object : Callback<Any> {
                override fun onResponse(call: Call<Any>, response: Response<Any>) {
                    if (response.isSuccessful) {
                        val responseBody = response.body() as? Map<*, *>
                        responseBody?.get("user")?.let { user ->
                            val userMap = user as? Map<*, *>
                            val username = userMap?.get("username")?.toString() ?: ""
                            FeedbackSession.currentUsername = username
                        }
                        Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
                        context.startActivity(Intent(context, UserDashboardActivity::class.java))
                    } else {
                        Toast.makeText(context, "Invalid credentials", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Any>, t: Throwable) {
                    Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }
    }
}