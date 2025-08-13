package azari.amirhossein.messenger.data.models

data class MessageItem(val message: Message) : ChatItem {
    override val id: String get() = message.id
}