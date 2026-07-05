package com.example.uas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uas.data.BookingData
import com.example.uas.data.HistoryItem
import com.example.uas.repository.BookingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BookingViewModel(
    private val repository: BookingRepository = BookingRepository()
) : ViewModel() {

    // Using stateIn to convert Flow to StateFlow, sharing it in viewModelScope
    val bookings: StateFlow<List<BookingData>> = repository.getBookingsFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val historyItems: StateFlow<List<HistoryItem>> = bookings.map { list ->
        list.map {
            HistoryItem(
                passengerName = it.passengerName,
                totalPrice = it.totalPrice,
                route = "${it.origin} >> ${it.destination}",
                departureDate = it.travelDate
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    suspend fun addBooking(booking: BookingData): Boolean {
        return repository.addBooking(booking)
    }
}
