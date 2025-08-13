package azari.amirhossein.messenger.data.models

data class DateHeaderItem(val date: String) : ChatItem {
    override val id: String get() = date
}