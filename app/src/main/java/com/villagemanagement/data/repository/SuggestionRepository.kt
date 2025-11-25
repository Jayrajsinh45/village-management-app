package com.villagemanagement.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.villagemanagement.data.model.Suggestion
import com.villagemanagement.utils.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SuggestionRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val suggestionCollection = firestore.collection("suggestions")

    suspend fun createSuggestion(suggestion: Suggestion): Resource<String> {
        return try {
            val document = suggestionCollection.document()
            val suggestionWithId = suggestion.copy(id = document.id)
            document.set(suggestionWithId).await()
            Resource.Success(document.id)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to create suggestion")
        }
    }

    fun observeSuggestionsByVillage(villageId: String): Flow<Resource<List<Suggestion>>> = callbackFlow {
        trySend(Resource.Loading)
        val listener = suggestionCollection
            .whereEqualTo("villageId", villageId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error.message ?: "Error observing suggestions"))
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val suggestions = snapshot.toObjects(Suggestion::class.java)
                    trySend(Resource.Success(suggestions))
                }
            }
        awaitClose { listener.remove() }
    }
}
