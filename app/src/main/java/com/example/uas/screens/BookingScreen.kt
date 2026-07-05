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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.DirectionsBoat
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Train
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
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.example.uas.viewmodel.AuthViewModel
import com.example.uas.data.BookingData
import com.example.uas.data.User
import com.example.uas.viewmodel.BookingViewModel
import kotlinx.coroutines.launch

private val PrimaryIndigo = Color(0xFF4F46E5)
private val PrimaryLight = Color(0xFFEEF2FF)
private val BackgroundGray = Color(0xFFF8F9FA)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    authViewModel: AuthViewModel,
    bookingViewModel: BookingViewModel,
    onBookingSuccess: () -> Unit
) {
    val currentUser by authViewModel.currentUser.collectAsState()
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
        TransportOption("Pesawat", "Udara", Icons.Filled.Flight),
        TransportOption("Kapal", "Laut", Icons.Filled.DirectionsBoat),
        TransportOption("Kereta", "Darat", Icons.Filled.Train)
    )

    val classOptions = listOf("Ekonomi", "Eksekutif")

    val calculatePrice = {
        val basePrice = when (selectedTransport) {
            "Pesawat" -> 800000
            "Kapal" -> 500000
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
            title = { Text("Pemesanan Berhasil!", fontWeight = FontWeight.Bold) },
            text = { Text("Tiket perjalanan Anda berhasil dipesan. Anda dapat mengecek detail tiket di halaman Riwayat.") },
            confirmButton = {
                Button(onClick = {
                    showSuccessDialog = false
                    onBookingSuccess()
                }, colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo)) {
                    Text("Lihat Riwayat")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSuccessDialog = false }) {
                    Text("Tutup", color = Color.Gray)
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = BackgroundGray
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Text(
                text = "Halo, ${currentUser?.name?.split(" ")?.firstOrNull() ?: "Guest"}!",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1E293B)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Mau pergi ke mana hari ini?",
                fontSize = 15.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Detail Perjalanan",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = passengerName,
                        onValueChange = { passengerName = it },
                        label = { Text("Nama Penumpang") },
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

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Transportasi",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF64748B)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

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

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = origin,
                        onValueChange = { origin = it },
                        label = { Text("Kota Keberangkatan") },
                        leadingIcon = { Icon(Icons.Filled.Place, contentDescription = null) },
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

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        IconButton(
                            onClick = {
                                val temp = origin
                                origin = destination
                                destination = temp
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(PrimaryLight)
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.List,
                                contentDescription = "Tukar Rute",
                                tint = PrimaryIndigo,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    OutlinedTextField(
                        value = destination,
                        onValueChange = { destination = it },
                        label = { Text("Kota Tujuan") },
                        leadingIcon = { Icon(Icons.Filled.Place, contentDescription = null) },
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

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = travelDate,
                        onValueChange = { travelDate = it },
                        label = { Text("Tanggal Keberangkatan") },
                        leadingIcon = { Icon(Icons.Filled.DateRange, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        placeholder = { Text("DD/MM/YYYY") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryIndigo,
                            focusedLabelColor = PrimaryIndigo,
                            focusedTextColor = PrimaryIndigo,
                            unfocusedTextColor = Color(0xFF1E293B)
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                    
                    Text(
                        text = "Kelas Penumpang",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF64748B)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

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
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryIndigo,
                                focusedLabelColor = PrimaryIndigo,
                                focusedTextColor = Color(0xFF1E293B),
                                unfocusedTextColor = Color(0xFF1E293B)
                            )
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

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Dewasa (18+)", fontSize = 13.sp, color = Color(0xFF64748B))
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFF8F9FA))
                                    .padding(4.dp)
                            ) {
                                IconButton(
                                    onClick = { if (adultCount > 1) adultCount-- },
                                    modifier = Modifier.size(32.dp).clip(CircleShape).background(Color.White)
                                ) {
                                    Icon(Icons.Filled.Remove, contentDescription = "Kurang", modifier = Modifier.size(16.dp), tint = PrimaryIndigo)
                                }
                                Text("$adultCount", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                IconButton(
                                    onClick = { adultCount++ },
                                    modifier = Modifier.size(32.dp).clip(CircleShape).background(PrimaryIndigo)
                                ) {
                                    Icon(Icons.Filled.Add, contentDescription = "Tambah", modifier = Modifier.size(16.dp), tint = Color.White)
                                }
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text("Anak (0-17)", fontSize = 13.sp, color = Color(0xFF64748B))
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFF8F9FA))
                                    .padding(4.dp)
                            ) {
                                IconButton(
                                    onClick = { if (childCount > 0) childCount-- },
                                    modifier = Modifier.size(32.dp).clip(CircleShape).background(Color.White)
                                ) {
                                    Icon(Icons.Filled.Remove, contentDescription = "Kurang", modifier = Modifier.size(16.dp), tint = PrimaryIndigo)
                                }
                                Text("$childCount", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                IconButton(
                                    onClick = { childCount++ },
                                    modifier = Modifier.size(32.dp).clip(CircleShape).background(PrimaryIndigo)
                                ) {
                                    Icon(Icons.Filled.Add, contentDescription = "Tambah", modifier = Modifier.size(16.dp), tint = Color.White)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = PrimaryIndigo)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Total Estimasi Harga", fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = calculatePrice(),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

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
                            scope.launch {
                                val booking = BookingData(
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
                                val success = bookingViewModel.addBooking(booking)
                                if (success) {
                                    showSuccessDialog = true

                                    passengerName = ""
                                    origin = ""
                                    destination = ""
                                    adultCount = 1
                                    childCount = 0
                                    selectedClass = "Ekonomi"
                                    travelDate = ""
                                } else {
                                    snackbarHostState.showSnackbar("Gagal membuat pesanan")
                                }
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text("Pesan Sekarang", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(90.dp))
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
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) PrimaryIndigo else Color(0xFFF8F9FA)
        ),
        border = if (isSelected) null else CardDefaults.outlinedCardBorder(true)
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
                tint = if (isSelected) Color.White else PrimaryIndigo,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else Color(0xFF1E293B)
            )
            Text(
                subtitle,
                fontSize = 10.sp,
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
