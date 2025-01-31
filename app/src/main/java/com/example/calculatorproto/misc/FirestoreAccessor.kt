package com.example.calculatorproto.misc

import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import java.time.Instant
import java.time.format.DateTimeFormatter

class FirestoreAccessor {

    private val database = Firebase.firestore

    suspend fun addHistoryEntry(uid: String, operation: String) {
        var deviceDoc = getDeviceDoc(uid)

        if (deviceDoc == null) {
            deviceDoc = addDeviceDoc(uid)
        }

        val historyRef = deviceDoc?.get("history") as DocumentReference
        historyRef
            .update("entries",
                FieldValue.arrayUnion(
                    hashMapOf(
                        "operation" to operation,
                        "date" to DateTimeFormatter.ISO_INSTANT.format(Instant.now())
                    )
                )
            )
            .await()
    }

    suspend fun getHistoryEntries(uid: String): History {
        var deviceDoc = getDeviceDoc(uid)

        if (deviceDoc == null) {
            deviceDoc = addDeviceDoc(uid)
        }

        val historyRef = deviceDoc?.get("history") as DocumentReference
        val result = historyRef.get().await()

        val r = result.toObject(History::class.java)!!
        r.entries = r.entries!!.reversed()

        return r;
    }

    private suspend fun addDeviceDoc(uid: String): DocumentSnapshot? {
        var historyRef: DocumentReference? = null
        var deviceRef: DocumentReference? = null

        database
            .collection("history")
            .add(
                hashMapOf(
                    "entries" to listOf<Any?>()
                )
            )
            .addOnSuccessListener { ref ->
                historyRef = ref
            }
            .await()

        database
            .collection("devices")
            .add(
                hashMapOf(
                    "id" to uid,
                    "history" to historyRef
                )
            )
            .addOnSuccessListener { ref ->
                deviceRef = ref
            }
            .await()

        return deviceRef!!.get().await()
    }

    private suspend fun getDeviceDoc(uid: String): DocumentSnapshot? {
        var deviceDoc: DocumentSnapshot? = null
        database
            .collection("devices")
            .whereEqualTo("id", uid)
            .get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.isEmpty) {
                    deviceDoc = snapshot.documents[0]
                }
            }
            .addOnFailureListener { exception ->
                println(exception)
            }
            .await()

        return deviceDoc
    }

}

data class History (var entries: List<HistoryEntry>? = null) {

}

data class HistoryEntry (var operation: String? = null, var date: String? = null) {

}