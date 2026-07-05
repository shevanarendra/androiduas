package com.example.uas.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uas.viewmodel.AuthViewModel
import com.example.uas.data.User

private val PrimaryIndigo = Color(0xFF4F46E5)
private val BackgroundGray = Color(0xFFF8F9FA)
private val SurfaceWhite = Color(0xFFFFFFFF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    onLogout: () -> Unit
) {
    val user by authViewModel.currentUser.collectAsState()
    val scope = rememberCoroutineScope()
    var showEditDialog by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf(user?.name ?: "") }
    var editPhone by remember { mutableStateOf(user?.phone ?: "") }

    var showPasswordDialog by remember { mutableStateOf(false) }
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmNewPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var passwordMessage by remember { mutableStateOf("") }
    var passwordIsError by remember { mutableStateOf(false) }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Profil", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Nama Lengkap") },
                        leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryIndigo,
                            focusedLabelColor = PrimaryIndigo,
                            focusedTextColor = PrimaryIndigo,
                            unfocusedTextColor = Color(0xFF1E293B)
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = editPhone,
                        onValueChange = { editPhone = it },
                        label = { Text("Nomor Telepon") },
                        leadingIcon = { Icon(Icons.Filled.Phone, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryIndigo,
                            focusedLabelColor = PrimaryIndigo,
                            focusedTextColor = PrimaryIndigo,
                            unfocusedTextColor = Color(0xFF1E293B)
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            authViewModel.updateProfile(editName, editPhone)
                            showEditDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Batal", color = Color.Gray)
                }
            },
            containerColor = SurfaceWhite,
            shape = RoundedCornerShape(20.dp)
        )
    }

    if (showPasswordDialog) {
        AlertDialog(
            onDismissRequest = { 
                showPasswordDialog = false
                oldPassword = ""
                newPassword = ""
                confirmNewPassword = ""
                passwordMessage = ""
            },
            title = { Text("Ubah Kata Sandi", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = oldPassword,
                        onValueChange = { oldPassword = it; passwordMessage = "" },
                        label = { Text("Kata Sandi Lama") },
                        leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryIndigo, focusedLabelColor = PrimaryIndigo, focusedTextColor = PrimaryIndigo, unfocusedTextColor = Color(0xFF1E293B))
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it; passwordMessage = "" },
                        label = { Text("Kata Sandi Baru") },
                        leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryIndigo, focusedLabelColor = PrimaryIndigo, focusedTextColor = PrimaryIndigo, unfocusedTextColor = Color(0xFF1E293B))
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = confirmNewPassword,
                        onValueChange = { confirmNewPassword = it; passwordMessage = "" },
                        label = { Text("Konfirmasi Kata Sandi") },
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
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryIndigo, focusedLabelColor = PrimaryIndigo, focusedTextColor = PrimaryIndigo, unfocusedTextColor = Color(0xFF1E293B))
                    )
                    if (passwordMessage.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = passwordMessage, 
                            color = if (passwordIsError) Color.Red else Color(0xFF4CAF50), 
                            fontSize = 12.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
                            passwordMessage = "Semua field harus diisi"
                            passwordIsError = true
                        } else if (newPassword != confirmNewPassword) {
                            passwordMessage = "Kata sandi baru tidak cocok"
                            passwordIsError = true
                        } else if (newPassword.length < 6) {
                            passwordMessage = "Kata sandi minimal 6 karakter"
                            passwordIsError = true
                        } else {
                            scope.launch {
                                val success = authViewModel.changePassword(oldPassword, newPassword)
                                if (success) {
                                    passwordMessage = "Kata sandi berhasil diubah!"
                                    passwordIsError = false
                                    oldPassword = ""
                                    newPassword = ""
                                    confirmNewPassword = ""
                                } else {
                                    passwordMessage = "Kata sandi lama salah"
                                    passwordIsError = true
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showPasswordDialog = false
                    oldPassword = ""
                    newPassword = ""
                    confirmNewPassword = ""
                    passwordMessage = ""
                }) {
                    Text("Tutup", color = Color.Gray)
                }
            },
            containerColor = SurfaceWhite,
            shape = RoundedCornerShape(20.dp)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
    ) {
        TopAppBar(
            title = {
                Text(
                    "Profil",
                    fontWeight = FontWeight.Bold
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = PrimaryIndigo,
                titleContentColor = Color.White
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(PrimaryIndigo.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(PrimaryIndigo),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(50.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = user?.name ?: "Guest",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = user?.email ?: "-",
                fontSize = 15.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = user?.phone ?: "-",
                fontSize = 15.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(40.dp))

            ProfileMenuItem(
                icon = Icons.Filled.Create,
                title = "Edit Profil",
                subtitle = "Ubah informasi pribadi Anda",
                onClick = {
                    editName = user?.name ?: ""
                    editPhone = user?.phone ?: ""
                    showEditDialog = true
                }
            )

            ProfileMenuItem(
                icon = Icons.Filled.Lock,
                title = "Ubah Kata Sandi",
                subtitle = "Perbarui keamanan akun Anda",
                onClick = { showPasswordDialog = true }
            )

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = { onLogout() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Icon(Icons.Filled.Close, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Keluar dari Akun", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(90.dp))
        }
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(PrimaryIndigo.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = PrimaryIndigo,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = Color.LightGray
            )
        }
    }
}
