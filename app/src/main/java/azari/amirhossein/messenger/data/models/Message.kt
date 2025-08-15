package azari.amirhossein.messenger.data.models

import azari.amirhossein.messenger.utils.MessageStatus

data class Message(
    val id: String = "",
    val senderName: String = "",
    val text: String = "",
    val timestamp: Long = 0L,
    val status: MessageStatus = MessageStatus.SENT,

    val replyToId: String? = null,
    val replyToText: String? = null,
    val replyToSender: String? = null
)

