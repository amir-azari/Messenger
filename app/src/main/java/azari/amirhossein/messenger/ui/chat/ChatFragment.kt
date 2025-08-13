package azari.amirhossein.messenger.ui.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnPreDraw
import androidx.core.view.updatePadding
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import azari.amirhossein.messenger.R
import azari.amirhossein.messenger.databinding.FragmentChatBinding
import azari.amirhossein.messenger.databinding.FragmentLoginBinding
import azari.amirhossein.messenger.ui.chat.MessageAdapter
import azari.amirhossein.messenger.viewmodel.ChatViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.math.max

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
    ): View? {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up recycler view
        setupRecyclerView()

        //load messages
        viewModel.loadMessages()

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
        adapter = MessageAdapter(args.username)
        binding.recyclerView.adapter = adapter
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
