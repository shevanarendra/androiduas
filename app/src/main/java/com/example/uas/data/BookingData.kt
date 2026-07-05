package com.example.uas.data

import androidx.compose.runtime.mutableStateListOf

data class User(
    val name: String,
    val email: String,
    val phone: String,
    val password: String
)

data class BookingData(
    val id: Int = 0,
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

object AppData {
    val users = mutableStateListOf<User>()
    val bookings = mutableStateListOf<BookingData>()
    var currentUser: User? = null

    init {
        users.add(User("Joey Tribbiani", "joey@email.com", "08123456789", "123456"))
        users.add(User("Anya Geraldine", "anya@email.com", "08567890123", "123456"))
    }

    fun register(name: String, email: String, phone: String, password: String): Boolean {
        if (users.any { it.email == email }) return false
        users.add(User(name, email, phone, password))
        return true
    }

    fun login(email: String, password: String): User? {
        val user = users.find { it.email == email && it.password == password }
        currentUser = user
        return user
    }

    fun logout() {
        currentUser = null
    }

    fun addBooking(booking: BookingData) {
        bookings.add(booking)
    }

    fun getBookings(): List<BookingData> = bookings.toList()

    fun getHistoryItems(): List<HistoryItem> = bookings.map {
        HistoryItem(
            passengerName = it.passengerName,
            totalPrice = it.totalPrice,
            route = "${it.origin} >> ${it.destination}",
            departureDate = it.travelDate
        )
    }
}
