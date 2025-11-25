package com.villagemanagement.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.villagemanagement.data.model.HelpRequest
import com.villagemanagement.utils.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HelpRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val helpCollection = firestore.collection("help_requests")

    suspend fun createHelpRequest(request: HelpRequest): Resource<String> {
        return try {
            val document = helpCollection.document()
            val requestWithId = request.copy(id = document.id)
            document.set(requestWithId).await()
            Resource.Success(document.id)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to create help request")
        }
    }

    suspend fun getHelpRequestsByVillage(villageId: String): Resource<List<HelpRequest>> {
        return try {
            val snapshot = helpCollection
                .whereEqualTo("villageId", villageId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            val requests = snapshot.toObjects(HelpRequest::class.java)
            Resource.Success(requests)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to fetch help requests")
        }
    }

    fun observeHelpRequestsByVillage(villageId: String): Flow<Resource<List<HelpRequest>>> = callbackFlow {
        trySend(Resource.Loading())
        val listener = helpCollection
            .whereEqualTo("villageId", villageId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error.message ?: "Error observing requests"))
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val requests = snapshot.toObjects(HelpRequest::class.java)
                    trySend(Resource.Success(requests))
                }
            }
        awaitClose { listener.remove() }
    }
}
