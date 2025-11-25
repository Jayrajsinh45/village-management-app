package com.villagemanagement.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.villagemanagement.data.model.Village
import com.villagemanagement.utils.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VillageRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    /**
     * Create a new village (Super Admin only)
     */
    suspend fun createVillage(village: Village): Resource<String> {
        return try {
            val docRef = firestore.collection("villages")
                .add(village)
                .await()
            
            Resource.Success(docRef.id)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to create village")
        }
    }

    /**
     * Update village information
     */
    suspend fun updateVillage(villageId: String, updates: Map<String, Any>): Resource<Unit> {
        return try {
            firestore.collection("villages")
                .document(villageId)
                .update(updates)
                .await()
            
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update village")
        }
    }

    /**
     * Get village by ID
     */
    suspend fun getVillageById(villageId: String): Resource<Village> {
        return try {
            val doc = firestore.collection("villages")
                .document(villageId)
                .get()
                .await()
            
            val village = doc.toObject(Village::class.java)
                ?: throw Exception("Village not found")
            
            Resource.Success(village)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to fetch village")
        }
    }

    /**
     * Get all villages
     */
    suspend fun getAllVillages(): Resource<List<Village>> {
        return try {
            val snapshot = firestore.collection("villages")
                .whereEqualTo("isActive", true)
                .orderBy("name", Query.Direction.ASCENDING)
                .get()
                .await()
            
            val villages = snapshot.toObjects(Village::class.java)
            Resource.Success(villages)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to fetch villages")
        }
    }

    /**
     * Observe all villages in real-time
     */
    fun observeAllVillages(): Flow<Resource<List<Village>>> = callbackFlow {
        val listener = firestore.collection("villages")
            .whereEqualTo("isActive", true)
            .orderBy("name", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error.message ?: "Error observing villages"))
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val villages = snapshot.toObjects(Village::class.java)
                    trySend(Resource.Success(villages))
                } else {
                    trySend(Resource.Error("No data available"))
                }
            }

        awaitClose { listener.remove() }
    }

    /**
     * Observe specific village
     */
    fun observeVillage(villageId: String): Flow<Resource<Village>> = callbackFlow {
        val listener = firestore.collection("villages")
            .document(villageId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error.message ?: "Error observing village"))
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val village = snapshot.toObject(Village::class.java)
                    if (village != null) {
                        trySend(Resource.Success(village))
                    } else {
                        trySend(Resource.Error("Failed to parse village data"))
                    }
                } else {
                    trySend(Resource.Error("Village not found"))
                }
            }

        awaitClose { listener.remove() }
    }

    /**
     * Search villages by name
     */
    suspend fun searchVillages(query: String): Resource<List<Village>> {
        return try {
            val snapshot = firestore.collection("villages")
                .whereEqualTo("isActive", true)
                .orderBy("name")
                .startAt(query)
                .endAt(query + "\uf8ff")
                .get()
                .await()
            
            val villages = snapshot.toObjects(Village::class.java)
            Resource.Success(villages)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to search villages")
        }
    }

    /**
     * Delete village (soft delete)
     */
    suspend fun deleteVillage(villageId: String): Resource<Unit> {
        return try {
            firestore.collection("villages")
                .document(villageId)
                .update("isActive", false)
                .await()
            
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete village")
        }
    }

    /**
     * Update village population count
     */
    suspend fun updatePopulation(villageId: String, population: Int): Resource<Unit> {
        return try {
            firestore.collection("villages")
                .document(villageId)
                .update("population", population)
                .await()
            
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update population")
        }
    }
}
