package com.example.uas.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current
    var hasAttemptedBiometric by remember { mutableStateOf(false) }

    androidx.compose.runtime.LaunchedEffect(Unit) {
        if (com.example.uas.data.AppData.auth.currentUser != null && !hasAttemptedBiometric) {
            hasAttemptedBiometric = true
            val fragmentActivity = context as? androidx.fragment.app.FragmentActivity
            if (fragmentActivity != null) {
                com.example.uas.utils.BiometricHelper.showBiometricPrompt(
                    activity = fragmentActivity,
                    onSuccess = {
                        onLoginSuccess()
                    },
                    onFailed = {
                        // user can fallback to standard login
                    }
                )
            } else {
                onLoginSuccess() // Fallback if not FragmentActivity
            }
        }
    }

    val primaryColor = Color(0xFF4F46E5)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(primaryColor.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.Person,
                contentDescription = null,
                tint = primaryColor,
                modifier = Modifier.size(40.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Selamat Datang",
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF1E293B)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Silakan masuk untuk melanjutkan pemesanan tiket perjalanan Anda",
            fontSize = 15.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; errorMessage = "" },
                    label = { Text("Email") },
                    leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryColor,
                        focusedLabelColor = primaryColor,
                        focusedTextColor = primaryColor,
                        unfocusedTextColor = Color(0xFF1E293B)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; errorMessage = "" },
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = "Toggle Password"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryColor,
                        focusedLabelColor = primaryColor,
                        focusedTextColor = primaryColor,
                        unfocusedTextColor = Color(0xFF1E293B)
                    )
                )

                if (errorMessage.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = errorMessage, color = Color.Red, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (email.isEmpty() || password.isEmpty()) {
                            errorMessage = "Email dan password harus diisi"
                        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            errorMessage = "Format email tidak valid"
                        } else {
                            isLoading = true
                            scope.launch {
                                val user = com.example.uas.data.AppData.login(email, password)
                                isLoading = false
                                if (user != null) {
                                    onLoginSuccess()
                                } else {
                                    errorMessage = "Email atau password salah"
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                    enabled = !isLoading
                ) {
                    Text(if (isLoading) "Memproses..." else "Masuk", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Belum punya akun? ", fontSize = 15.sp, color = Color.Gray)
            Text(
                text = "Daftar Sekarang",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = primaryColor,
                modifier = Modifier.clickable { onNavigateToRegister() }
            )
        }
    }
}
