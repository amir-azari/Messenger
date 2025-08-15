package azari.amirhossein.messenger.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import azari.amirhossein.messenger.data.models.Message
import azari.amirhossein.messenger.databinding.FragmentChatBinding
import azari.amirhossein.messenger.viewmodel.ChatViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatFragment : Fragment() {
    // Binding
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    // View model
    private val viewModel: ChatViewModel by viewModels()

    // Adapter
    private lateinit var adapter: MessageAdapter

    private val args: ChatFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up recycler view
        setupRecyclerView()

        //load messages
        viewModel.loadMessages()

        // Observe the replyMessage LiveData from the ViewModel
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.replyMessage.collect { message ->
                    if (message != null) {
                        // If there's a message to reply to, show the preview bar
                        binding.replyPreviewLayout.visibility = View.VISIBLE
                        binding.replySender.text = message.senderName
                        binding.replyText.text = message.text
                    } else {
                        // Otherwise, hide it
                        binding.replyPreviewLayout.visibility = View.GONE
                    }
                }
            }
        }

        // Handle closing the reply preview
        binding.btnCloseReply.setOnClickListener {
            viewModel.clearReplyMessage()
        }
        // Observe messages
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.messages.collect { list ->
                    adapter.submitList(list)
                    if (list.isNotEmpty()){
                        binding.tvNoMessage.visibility = View.GONE

                    }else {
                        binding.tvNoMessage.visibility = View.VISIBLE

                    }
                }
            }
        }

        // Send message button
        binding.btnSend.setOnClickListener {
            val text = binding.etMessage.text.toString().trim()
            if (text.isNotEmpty()) {
                viewModel.sendMessage(args.username, text)
                binding.etMessage.text?.clear()
            }
        }

    }
    private fun setupRecyclerView() {
        adapter = MessageAdapter(args.username) { message ->
            viewModel.setReplyMessage(message)
        }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.stackFromEnd = true
        binding.recyclerView.layoutManager = layoutManager

        // Scroll to bottom on new messages
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                binding.recyclerView.scrollToPosition(adapter.itemCount - 1)
            }
        })
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
