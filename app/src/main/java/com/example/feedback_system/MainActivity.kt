package com.example.feedback_system

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.feedback_system.user.LoginActivity
import com.example.feedback_system.user.RegisterActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HomeScreen(
                onLogoClick = {
                    startActivity(Intent(this, LoginActivity::class.java))
                },
                onRegisterClick = {
                    startActivity(Intent(this, RegisterActivity::class.java))
                }
            )
        }
    }
}

@Composable
fun HomeScreen(onLogoClick: () -> Unit, onRegisterClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("IST Feedback System", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(24.dp))
        Image(
            painter = painterResource(id = R.drawable.istlogo),
            contentDescription = "IST Logo",
            modifier = Modifier
                .size(150.dp)
                .clickable { onLogoClick() }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Don't have an account? Register",
            modifier = Modifier.clickable { onRegisterClick() }
        )
    }
}