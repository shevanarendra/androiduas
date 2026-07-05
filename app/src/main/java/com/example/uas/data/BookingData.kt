package com.example.uas.data

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.tasks.await

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

object AppData {
    val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    val db: FirebaseDatabase by lazy { FirebaseDatabase.getInstance() }

    val bookings = mutableStateListOf<BookingData>()
    var currentUser by mutableStateOf<User?>(null)
    
    private var bookingsListener: ValueEventListener? = null

    init {
        val firebaseUser = auth.currentUser
        if (firebaseUser != null) {
            fetchUserData(firebaseUser.uid)
            listenToBookings(firebaseUser.uid)
        }
    }

    private fun fetchUserData(uid: String) {
        db.getReference("users").child(uid).get().addOnSuccessListener { snapshot ->
            val user = snapshot.getValue(User::class.java)
            if (user != null) {
                currentUser = user
            }
        }
    }

    private fun listenToBookings(uid: String) {
        bookingsListener?.let { db.getReference("bookings").child(uid).removeEventListener(it) }
        bookings.clear()

        bookingsListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                bookings.clear()
                for (child in snapshot.children) {
                    val booking = child.getValue(BookingData::class.java)
                    if (booking != null) {
                        bookings.add(booking)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Gagal membaca riwayat", error.toException())
            }
        }
        db.getReference("bookings").child(uid).addValueEventListener(bookingsListener!!)
    }

    suspend fun register(name: String, email: String, phone: String, password: String): Boolean {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: return false

            val user = User(name = name, email = email, phone = phone)
            db.getReference("users").child(uid).setValue(user).await()

            currentUser = user
            listenToBookings(uid)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun login(email: String, password: String): User? {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: return null

            val snapshot = db.getReference("users").child(uid).get().await()
            val user = snapshot.getValue(User::class.java)
            if (user != null) {
                currentUser = user
                listenToBookings(uid)
                user
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun logout() {
        auth.signOut()
        currentUser = null
        bookingsListener?.let { listener ->
            auth.uid?.let { uid ->
                db.getReference("bookings").child(uid).removeEventListener(listener)
            }
        }
        bookings.clear()
    }

    suspend fun changePassword(oldPassword: String, newPassword: String): Boolean {
        val fUser = auth.currentUser ?: return false
        val email = fUser.email ?: return false
        return try {
            val credential = com.google.firebase.auth.EmailAuthProvider.getCredential(email, oldPassword)
            fUser.reauthenticate(credential).await()
            fUser.updatePassword(newPassword).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun updateProfile(name: String, phone: String): Boolean {
        val uid = auth.currentUser?.uid ?: return false
        val user = currentUser ?: return false
        return try {
            val updatedUser = user.copy(name = name, phone = phone)
            db.getReference("users").child(uid).setValue(updatedUser).await()
            currentUser = updatedUser
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

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
