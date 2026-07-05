package com.example.uas.repository

import com.example.uas.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val db: FirebaseDatabase by lazy { FirebaseDatabase.getInstance() }

    val currentFirebaseUser = auth.currentUser

    suspend fun register(name: String, email: String, phone: String, password: String): User? {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: return null

            val user = User(name = name, email = email, phone = phone)
            db.getReference("users").child(uid).setValue(user).await()

            user
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun login(email: String, password: String): User? {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: return null

            val snapshot = db.getReference("users").child(uid).get().await()
            snapshot.getValue(User::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun fetchCurrentUser(): User? {
        val uid = auth.currentUser?.uid ?: return null
        return try {
            val snapshot = db.getReference("users").child(uid).get().await()
            snapshot.getValue(User::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun logout() {
        auth.signOut()
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

    suspend fun updateProfile(name: String, phone: String): User? {
        val uid = auth.currentUser?.uid ?: return null
        return try {
            val snapshot = db.getReference("users").child(uid).get().await()
            val oldUser = snapshot.getValue(User::class.java) ?: return null
            val updatedUser = oldUser.copy(name = name, phone = phone)
            db.getReference("users").child(uid).setValue(updatedUser).await()
            updatedUser
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
