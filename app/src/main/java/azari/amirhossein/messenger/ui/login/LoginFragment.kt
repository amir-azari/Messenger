package azari.amirhossein.messenger.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import azari.amirhossein.messenger.R
import azari.amirhossein.messenger.databinding.FragmentLoginBinding
import azari.amirhossein.messenger.utils.hideKeyboard
import azari.amirhossein.messenger.viewmodel.ChatViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    // Binding
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    // View model
    private val viewModel: ChatViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentLoginBinding.bind(view)

        binding.btnLogin.setOnClickListener {
            // Get username from edit text
            val name = binding.etName.text.toString().trim()
            if (name.isNotEmpty()) {
                viewModel.saveUsername(name)

                // Navigate to chat fragment
                val action = LoginFragmentDirections.actionLoginFragmentToChatFragment(name)
                findNavController().navigate(action)
                hideKeyboard()

            } else {
                Toast.makeText(requireContext(), "Enter your name", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}