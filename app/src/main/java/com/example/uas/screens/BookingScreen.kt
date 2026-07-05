package com.example.uas.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uas.data.AppData
import com.example.uas.data.BookingData
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(onBookingSuccess: () -> Unit = {}) {
    var selectedTransport by remember { mutableStateOf("Pesawat") }
    var origin by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    var adultCount by remember { mutableIntStateOf(1) }
    var childCount by remember { mutableIntStateOf(0) }
    var selectedClass by remember { mutableStateOf("Ekonomi") }
    var travelDate by remember { mutableStateOf("") }
    var passengerName by remember { mutableStateOf("") }
    var classExpanded by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val transportTypes = listOf(
        TransportOption("Pesawat", "Transportasi Udara", Icons.Filled.Star),
        TransportOption("Kapal Laut", "Transportasi Laut", Icons.Filled.Place),
        TransportOption("Kereta", "Transportasi Darat", Icons.Filled.Home)
    )

    val classOptions = listOf("Ekonomi", "Eksekutif")

    val calculatePrice = {
        val basePrice = when (selectedTransport) {
            "Pesawat" -> 800000
            "Kapal Laut" -> 500000
            "Kereta" -> 400000
            else -> 500000
        }
        val classMultiplier = if (selectedClass == "Eksekutif") 1.5 else 1.0
        val totalPassengers = adultCount + childCount
        val childDiscount = childCount * 0.5
        val total = (basePrice * classMultiplier * (adultCount + childDiscount)).toInt()
        "Rp ${"%,d".format(total).replace(",", ".")}"
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = { Text("Berhasil!", fontWeight = FontWeight.Bold) },
            text = { Text("Tiket berhasil dipesan! Silakan cek halaman Riwayat.") },
            confirmButton = {
                TextButton(onClick = {
                    showSuccessDialog = false
                    onBookingSuccess()
                }) {
                    Text("Lihat Riwayat")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSuccessDialog = false }) {
                    Text("Tutup")
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = "Selamat Datang, ${AppData.currentUser?.name ?: "Guest"}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1565C0)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Silakan pesan tiket perjalanan Anda",
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Pemesanan Tiket",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = passengerName,
                onValueChange = { passengerName = it },
                label = { Text("Nama Penumpang") },
                leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Pilih Transportasi",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                transportTypes.forEach { transport ->
                    TransportCard(
                        modifier = Modifier.weight(1f),
                        title = transport.name,
                        subtitle = transport.subtitle,
                        icon = transport.icon,
                        isSelected = selectedTransport == transport.name,
                        onClick = { selectedTransport = transport.name }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = origin,
                onValueChange = { origin = it },
                label = { Text("Kota asal") },
                leadingIcon = { Icon(Icons.Filled.Place, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(onClick = {
                    val temp = origin
                    origin = destination
                    destination = temp
                }) {
                    Icon(
                        Icons.Filled.List,
                        contentDescription = "Tukar Rute",
                        tint = Color(0xFF1565C0)
                    )
                }
            }

            OutlinedTextField(
                value = destination,
                onValueChange = { destination = it },
                label = { Text("Kota tujuan") },
                leadingIcon = { Icon(Icons.Filled.Place, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Dewasa (18+ Tahun)", fontSize = 12.sp)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = { if (adultCount > 1) adultCount-- },
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFE3F2FD))
                        ) {
                            Icon(Icons.Filled.Remove, contentDescription = "Kurang", modifier = Modifier.size(16.dp))
                        }
                        Text("$adultCount", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        IconButton(
                            onClick = { adultCount++ },
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF1565C0))
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = "Tambah", modifier = Modifier.size(16.dp), tint = Color.White)
                        }
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text("Anak-anak (0-17 Tahun)", fontSize = 12.sp)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = { if (childCount > 0) childCount-- },
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFE3F2FD))
                        ) {
                            Icon(Icons.Filled.Remove, contentDescription = "Kurang", modifier = Modifier.size(16.dp))
                        }
                        Text("$childCount", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        IconButton(
                            onClick = { childCount++ },
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF1565C0))
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = "Tambah", modifier = Modifier.size(16.dp), tint = Color.White)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Kelas", fontSize = 14.sp, fontWeight = FontWeight.Medium)

            Spacer(modifier = Modifier.height(8.dp))

            ExposedDropdownMenuBox(
                expanded = classExpanded,
                onExpandedChange = { classExpanded = it }
            ) {
                OutlinedTextField(
                    value = selectedClass,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = classExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(12.dp)
                )

                ExposedDropdownMenu(
                    expanded = classExpanded,
                    onDismissRequest = { classExpanded = false }
                ) {
                    classOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                selectedClass = option
                                classExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = travelDate,
                onValueChange = { travelDate = it },
                label = { Text("Tanggal Keberangkatan") },
                leadingIcon = { Icon(Icons.Filled.DateRange, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                placeholder = { Text("DD/MM/YYYY") },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Estimasi Harga", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = calculatePrice(),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1565C0)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    when {
                        passengerName.isEmpty() -> {
                            scope.launch { snackbarHostState.showSnackbar("Nama penumpang harus diisi") }
                        }
                        origin.isEmpty() -> {
                            scope.launch { snackbarHostState.showSnackbar("Kota asal harus diisi") }
                        }
                        destination.isEmpty() -> {
                            scope.launch { snackbarHostState.showSnackbar("Kota tujuan harus diisi") }
                        }
                        travelDate.isEmpty() -> {
                            scope.launch { snackbarHostState.showSnackbar("Tanggal keberangkatan harus diisi") }
                        }
                        else -> {
                            val booking = BookingData(
                                id = AppData.bookings.size + 1,
                                transportType = selectedTransport,
                                origin = origin,
                                destination = destination,
                                adultPassengers = adultCount,
                                childPassengers = childCount,
                                travelClass = selectedClass,
                                travelDate = travelDate,
                                passengerName = passengerName,
                                totalPrice = calculatePrice()
                            )
                            AppData.addBooking(booking)
                            showSuccessDialog = true

                            passengerName = ""
                            origin = ""
                            destination = ""
                            adultCount = 1
                            childCount = 0
                            selectedClass = "Ekonomi"
                            travelDate = ""
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
            ) {
                Text("Pesan Sekarang", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun TransportCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(100.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF1565C0) else Color(0xFFF5F5F5)
        ),
        border = if (isSelected) null else CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                icon,
                contentDescription = title,
                tint = if (isSelected) Color.White else Color(0xFF1565C0),
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else Color.Black
            )
            Text(
                subtitle,
                fontSize = 8.sp,
                color = if (isSelected) Color.White.copy(alpha = 0.8f) else Color.Gray
            )
        }
    }
}

data class TransportOption(
    val name: String,
    val subtitle: String,
    val icon: ImageVector
)
