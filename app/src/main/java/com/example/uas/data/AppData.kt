package com.example.uas.data

data class User(
    val name: String = "",
    val email: String = "",
    val phone: String = ""
)

data class BookingData(
    val id: String = "",
    val transportType: String = "Pesawat",
    val origin: String = "",
    val destination: String = "",
    val adultPassengers: Int = 1,
    val childPassengers: Int = 0,
    val travelClass: String = "Ekonomi",
    val travelDate: String = "",
    val passengerName: String = "",
    val totalPrice: String = ""
)

data class HistoryItem(
    val passengerName: String,
    val totalPrice: String,
    val route: String,
    val departureDate: String
)
