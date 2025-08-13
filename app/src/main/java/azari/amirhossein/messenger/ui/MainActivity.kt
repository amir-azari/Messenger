package azari.amirhossein.messenger.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import azari.amirhossein.messenger.R
import azari.amirhossein.messenger.data.repository.UserPreferencesRepository // اسم پکیج را چک کنید
import azari.amirhossein.messenger.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var userPreferencesRepository: UserPreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val types = WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime()
            val allInsets = insets.getInsets(types)
            v.setPadding(allInsets.left, allInsets.top, allInsets.right, allInsets.bottom)
            insets
        }

        lifecycleScope.launch {
            val savedUsername = userPreferencesRepository.username.first()

            val navHostFragment = supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            val navController = navHostFragment.navController

            val navGraph = navController.navInflater.inflate(R.navigation.nav_main)

            /*
            * If no username is saved:
              Sets the start destination of the navigation graph to

            * If a username is saved:
     *        Sets the start destination of the navigation graph to
            */
            if (savedUsername.isNullOrEmpty()) {

                navGraph.setStartDestination(R.id.loginFragment)
                navController.graph = navGraph
            } else {
                navGraph.setStartDestination(R.id.chatFragment)
                val args = bundleOf("username" to savedUsername)
                navController.setGraph(navGraph, args)
            }
        }
    }
}