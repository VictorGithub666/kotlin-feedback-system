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
import com.example.feedback_system.admin.AdminDashboardActivity
import com.example.feedback_system.api.ApiClient
import com.example.feedback_system.ui.theme.FeedbacksystemTheme
import com.example.feedback_system.ui.theme.PasswordTextField
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.feedback_system.ui.theme.RedButton
import com.example.feedback_system.ui.theme.StyledOutlinedTextField


class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FeedbacksystemTheme {
                RegisterScreen()
            }
        }
    }
}

@Composable
fun RegisterScreen() {
    val context = LocalContext.current
    var username by remember { mutableStateOf("") }
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

        StyledOutlinedTextField(value = username, onValueChange = { username = it }, label = "Username")
        Spacer(modifier = Modifier.height(8.dp))

        StyledOutlinedTextField(value = email, onValueChange = { email = it }, label = "Email")
        Spacer(modifier = Modifier.height(8.dp))


        PasswordTextField(value = password, onValueChange = { password = it })

        Spacer(modifier = Modifier.height(16.dp))

        RedButton(text = "Register") {
            val data = mapOf("username" to username, "email" to email, "password" to password)
            ApiClient.apiService.register(data).enqueue(object : Callback<Any> {
                override fun onResponse(call: Call<Any>, response: Response<Any>) {
                    Toast.makeText(context, "Registered!", Toast.LENGTH_SHORT).show()
                    context.startActivity(Intent(context, LoginActivity::class.java))
                }

                override fun onFailure(call: Call<Any>, t: Throwable) {
                    Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }
    }
}
