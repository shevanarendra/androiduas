package com.example.uas.repository

import com.example.uas.data.BookingData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class BookingRepository {
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val db: FirebaseDatabase by lazy { FirebaseDatabase.getInstance() }

    suspend fun addBooking(booking: BookingData): Boolean {
        val uid = auth.currentUser?.uid ?: return false
        return try {
            val ref = db.getReference("bookings").child(uid).push()
            val bookingWithId = booking.copy(id = ref.key ?: "")
            ref.setValue(bookingWithId).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun getBookingsFlow(): Flow<List<BookingData>> = callbackFlow {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<BookingData>()
                for (child in snapshot.children) {
                    val booking = child.getValue(BookingData::class.java)
                    if (booking != null) {
                        list.add(booking)
                    }
                }
                trySend(list)
            }

            override fun onCancelled(error: DatabaseError) {
                // Log.e("Firebase", "Gagal membaca riwayat", error.toException())
            }
        }

        val ref = db.getReference("bookings").child(uid)
        ref.addValueEventListener(listener)

        awaitClose {
            ref.removeEventListener(listener)
        }
    }
}
