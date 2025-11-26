package com.villagemanagement.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.villagemanagement.data.model.User
import com.villagemanagement.data.model.UserRole
import com.villagemanagement.utils.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    
    val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    /**
     * Register new user with email and password
     */
    suspend fun registerUser(
        email: String,
        password: String,
        name: String,
        phone: String,
        role: UserRole = UserRole.RESIDENT,
        villageId: String = ""
    ): Resource<User> {
        return try {
            // Create Firebase Auth user
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: throw Exception("User creation failed")

            // Update display name
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()
            firebaseUser.updateProfile(profileUpdates).await()

            // Create user document in Firestore
            val user = User(
                id = firebaseUser.uid,
                email = email,
                phone = phone,
                name = name,
                role = role.name,
                villageId = villageId
            )

            firestore.collection("users")
                .document(firebaseUser.uid)
                .set(user)
                .await()

            Resource.Success(user)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Registration failed")
        }
    }

    /**
     * Login with email and password
     */
    suspend fun loginUser(email: String, password: String): Resource<User> {
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: throw Exception("Login failed")

            // Fetch user data from Firestore
            val userDoc = firestore.collection("users")
                .document(firebaseUser.uid)
                .get()
                .await()

            val user = userDoc.toObject(User::class.java)
                ?: throw Exception("User data not found")

            if (!user.isActive) {
                throw Exception("Account is deactivated")
            }

            Resource.Success(user)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Login failed")
        }
    }

    /**
     * Logout current user
     */
    fun logout() {
        firebaseAuth.signOut()
    }

    /**
     * Get current user data from Firestore
     */
    suspend fun getCurrentUserData(): Resource<User> {
        return try {
            val uid = currentUser?.uid ?: throw Exception("No user logged in")
            
            val userDoc = firestore.collection("users")
                .document(uid)
                .get()
                .await()

            val user = userDoc.toObject(User::class.java)
                ?: throw Exception("User data not found")

            Resource.Success(user)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to fetch user data")
        }
    }

    /**
     * Observe current user data changes
     */
    fun observeCurrentUser(): Flow<Resource<User>> = callbackFlow {
        val uid = currentUser?.uid
        if (uid == null) {
            trySend(Resource.Error("No user logged in"))
            close()
            return@callbackFlow
        }

        val listener = firestore.collection("users")
            .document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error.message ?: "Error observing user"))
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val user = snapshot.toObject(User::class.java)
                    if (user != null) {
                        trySend(Resource.Success(user))
                    } else {
                        trySend(Resource.Error("Failed to parse user data"))
                    }
                } else {
                    trySend(Resource.Error("User data not found"))
                }
            }

        awaitClose { listener.remove() }
    }

    /**
     * Update user profile
     */
    suspend fun updateUserProfile(
        name: String? = null,
        phone: String? = null,
        profilePhotoUrl: String? = null
    ): Resource<Unit> {
        return try {
            val uid = currentUser?.uid ?: throw Exception("No user logged in")
            
            val updates = mutableMapOf<String, Any>()
            name?.let { updates["name"] = it }
            phone?.let { updates["phone"] = it }
            profilePhotoUrl?.let { updates["profilePhotoUrl"] = it }
            
            if (updates.isNotEmpty()) {
                firestore.collection("users")
                    .document(uid)
                    .update(updates)
                    .await()
            }

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update profile")
        }
    }

    /**
     * Send password reset email
     */
    suspend fun sendPasswordResetEmail(email: String): Resource<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to send reset email")
        }
    }

    /**
     * Check if user is logged in
     */
    fun isUserLoggedIn(): Boolean {
        return currentUser != null
    }
}
