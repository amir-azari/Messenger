package azari.amirhossein.messenger.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import azari.amirhossein.messenger.data.models.ChatItem
import azari.amirhossein.messenger.data.models.DateHeaderItem
import azari.amirhossein.messenger.data.models.Message
import azari.amirhossein.messenger.data.models.MessageItem
import azari.amirhossein.messenger.data.repository.ChatRepository
import azari.amirhossein.messenger.data.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(private val repository: ChatRepository , private val userPreferencesRepository: UserPreferencesRepository ) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatItem>>(emptyList())
    val messages: StateFlow<List<ChatItem>> = _messages

    private val _replyMessage = MutableStateFlow<Message?>(null)
    val replyMessage: StateFlow<Message?> = _replyMessage

    //Loads messages from the repository and updates the UI.
    fun loadMessages() {
        viewModelScope.launch {
            repository.getMessages().collect { messagesList ->
                _messages.value = addDateHeadersToList(messagesList)
            }
        }
    }
    // Saves the given username to the UserPreferencesRepository.
    fun saveUsername(name: String) {
        viewModelScope.launch {
            userPreferencesRepository.saveUsername(name)
        }
    }

    // Adds date headers to a list of messages.
    private fun addDateHeadersToList(messages: List<Message>): List<ChatItem> {
        val itemsWithHeaders = mutableListOf<ChatItem>()
        val groupedMessages = messages.groupBy {
            toDayIdentifier(it.timestamp)
        }

        groupedMessages.keys.sorted().forEach { dateKey ->
            val messagesOnThisDay = groupedMessages[dateKey]
            if (messagesOnThisDay != null) {
                val firstMessageTimestamp = messagesOnThisDay.first().timestamp
                itemsWithHeaders.add(
                    DateHeaderItem(
                        date = formatDateForHeader(firstMessageTimestamp)
                    )
                )
                messagesOnThisDay.forEach { message ->
                    itemsWithHeaders.add(MessageItem(message))
                }
            }
        }
        return itemsWithHeaders
    }


    // Converts a timestamp to a day identifier string
    private fun toDayIdentifier(timestamp: Long): String {
        val sdf = SimpleDateFormat("yyyy-D", Locale.US) // D = Day of year
        return sdf.format(Date(timestamp))
    }

    // Converts a timestamp to a date string
    private fun formatDateForHeader(timestamp: Long): String {
        val messageDate = Calendar.getInstance().apply {
            timeInMillis = timestamp
        }
        val today = Calendar.getInstance()
        val yesterday = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -1)
        }

        return when {
            isSameDay(messageDate, today) -> "Today"
            isSameDay(messageDate, yesterday) -> "Yesterday"
            else -> {
                val sdf = SimpleDateFormat("d MMM yyyy", Locale("en", "IR"))
                sdf.format(Date(timestamp))
            }
        }
    }

    // Checks if two Calendar objects represent the same day.
    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }


    // Sends a message to the repository.
    fun sendMessage(senderName: String, text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            val currentReply = _replyMessage.value
            val message = Message(
                senderName = senderName,
                text = text,
                timestamp = System.currentTimeMillis(),
                replyToId = currentReply?.id,
                replyToText = currentReply?.text,
                replyToSender = currentReply?.senderName
            )
            repository.sendMessage(message)
            // Clear reply after sending
            clearReplyMessage()
        }
    }

    // Select a message for replying
    fun setReplyMessage(message: Message) {
        _replyMessage.value = message
    }

    // Clear reply message
    fun clearReplyMessage() {
        _replyMessage.value = null
    }
}
