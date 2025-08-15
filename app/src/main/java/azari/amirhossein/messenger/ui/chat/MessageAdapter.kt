package azari.amirhossein.messenger.ui.chat

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import azari.amirhossein.messenger.R
import azari.amirhossein.messenger.data.models.ChatItem
import azari.amirhossein.messenger.data.models.DateHeaderItem
import azari.amirhossein.messenger.data.models.Message
import azari.amirhossein.messenger.data.models.MessageItem
import azari.amirhossein.messenger.databinding.ItemDateHeaderBinding
import azari.amirhossein.messenger.databinding.ItemMessageReceivedBinding
import azari.amirhossein.messenger.databinding.ItemMessageSentBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MessageAdapter(
    private val currentUser: String,
    private val onReplyClicked: (Message) -> Unit
) : ListAdapter<ChatItem, RecyclerView.ViewHolder>(DiffCallback()) {

    companion object {
        private const val VIEW_TYPE_SENT = 1
        private const val VIEW_TYPE_RECEIVED = 2
        const val VIEW_TYPE_DATE_HEADER = 3
    }

    // ViewHolders
    inner class DateHeaderViewHolder(private val binding: ItemDateHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(header: DateHeaderItem) {
            binding.tvDateHeader.text = header.date
        }
    }

    inner class SentMessageViewHolder(private val binding: ItemMessageSentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            @SuppressLint("ClickableViewAccessibility")
            // Handle touch events on the RecyclerView item
            binding.motionLayout.setOnTouchListener { view, event ->
                if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
                    if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                        val messageItem = getItem(bindingAdapterPosition) as MessageItem
                        val motionLayout = view as MotionLayout

                        if (motionLayout.progress > 0.9f) {
                            onReplyClicked(messageItem.message)
                        }
                        motionLayout.transitionToState(R.id.start)
                    }
                }
                return@setOnTouchListener false
            }
        }

        fun bind(message: Message) {
            binding.tvUser.isVisible = false
            binding.tvMessage.text = message.text
            if (message.replyToText != null && message.replyToSender != null) {
                binding.replyContainer.isVisible = true
                binding.replySender.text = message.replyToSender
                binding.replyText.text = message.replyToText
            } else {
                binding.replyContainer.isVisible = false
            }
        }
    }

    inner class ReceivedMessageViewHolder(private val binding: ItemMessageReceivedBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            @SuppressLint("ClickableViewAccessibility")
            // Handle touch events on the RecyclerView item
            binding.motionLayout.setOnTouchListener { view, event ->
                if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
                    if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                        val messageItem = getItem(bindingAdapterPosition) as MessageItem
                        val motionLayout = view as MotionLayout

                        if (motionLayout.progress > 0.9f) {
                            onReplyClicked(messageItem.message)
                        }
                        motionLayout.transitionToState(R.id.start)
                    }
                }
                return@setOnTouchListener false
            }
        }

        fun bind(message: Message) {
            binding.tvUser.text = message.senderName
            binding.tvMessage.text = message.text
            if (message.replyToText != null && message.replyToSender != null) {
                binding.replyContainer.isVisible = true
                binding.replySender.text = message.replyToSender
                binding.replyText.text = message.replyToText
            } else {
                binding.replyContainer.isVisible = false
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (val item = getItem(position)) {
            is DateHeaderItem -> VIEW_TYPE_DATE_HEADER
            is MessageItem -> {
                if (item.message.senderName == currentUser) VIEW_TYPE_SENT else VIEW_TYPE_RECEIVED
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_SENT -> {
                val binding = ItemMessageSentBinding.inflate(inflater, parent, false)
                SentMessageViewHolder(binding)
            }
            VIEW_TYPE_RECEIVED -> {
                val binding = ItemMessageReceivedBinding.inflate(inflater, parent, false)
                ReceivedMessageViewHolder(binding)
            }
            else -> { // VIEW_TYPE_DATE_HEADER
                val binding = ItemDateHeaderBinding.inflate(inflater, parent, false)
                DateHeaderViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is DateHeaderItem -> (holder as DateHeaderViewHolder).bind(item)
            is MessageItem -> {
                when (holder) {
                    is SentMessageViewHolder -> holder.bind(item.message)
                    is ReceivedMessageViewHolder -> holder.bind(item.message)
                }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ChatItem>() {
        override fun areItemsTheSame(oldItem: ChatItem, newItem: ChatItem) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: ChatItem, newItem: ChatItem) = oldItem == newItem
    }
}