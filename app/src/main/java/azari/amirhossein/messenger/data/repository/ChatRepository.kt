package azari.amirhossein.messenger.data.repository

import azari.amirhossein.messenger.data.models.Message
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(private val dbRef: DatabaseReference) {

    /**
     * Sends a message to the Firebase Realtime Database.
     *
     * This function generates a unique key for the message using `dbRef.push().key`.
     * If a key cannot be generated, it throws an Exception.
     * Otherwise, it sets the message data (with the generated key as its ID)
     * at the location specified by the generated key under the `dbRef`.
     * This operation is performed asynchronously and waits for completion using `await()`.
     *
     * @param message The [Message] object to be sent.
     * @throws Exception if a Firebase key cannot be generated.
     */
    suspend fun sendMessage(message: Message) {
        val key = dbRef.push().key ?: throw Exception("Cannot get Firebase key")
        dbRef.child(key).setValue(message.copy(id = key)).await()
    }

    /**
     * Retrieves a flow of messages from the Firebase Realtime Database.
     *
     * The messages are ordered by their timestamp in ascending order.
     * The flow emits a new list of messages whenever the data changes in the database.
     *
     * @return A Flow that emits a list of [Message] objects.
     *         The flow will close if a database error occurs.
     */
    fun getMessages(): Flow<List<Message>> = callbackFlow {
        val listener = object : ValueEventListener {

            /**
             * Called when the data at the specified database location changes.
             *
             * This method is triggered whenever there is an update, addition, or deletion
             * of data in the Firebase Realtime Database node that this listener is attached to.
             *
             */
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = snapshot.children.mapNotNull {
                    val msg = it.getValue(Message::class.java)
                    msg
                }.sortedBy { it.timestamp }
                val result = trySend(messages).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        dbRef.orderByChild("timestamp").addValueEventListener(listener)
        awaitClose {
            dbRef.removeEventListener(listener)
        }
    }
}

