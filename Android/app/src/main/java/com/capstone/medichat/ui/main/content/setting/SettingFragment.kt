package com.capstone.medichat.ui.main.content.setting

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.capstone.medichat.data.database.ChatDatabase
import com.capstone.medichat.data.database.ChatSaveMessageDao
import com.capstone.medichat.data.preference.UserPreference
import com.capstone.medichat.data.preference.dataStore
import com.capstone.medichat.databinding.FragmentSettingBinding
import com.capstone.medichat.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var userPreference: UserPreference
    private lateinit var chatSaveMessageDao: ChatSaveMessageDao
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Inisialisasi Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        // Inisialisasi SharedPreferences
        sharedPreferences = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
        val isDarkMode = sharedPreferences.getBoolean("DARK_MODE", false)
        binding.darkModeSwitch.isChecked = isDarkMode

        binding.darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                sharedPreferences.edit().putBoolean("DARK_MODE", true).apply()
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                sharedPreferences.edit().putBoolean("DARK_MODE", false).apply()
            }
        }

        binding.aboutUsArrow.setOnClickListener {
            navigateToAboutUsActivity()
        }

        binding.logoutContainer.setOnClickListener {
            logout()
        }

        // Initialize UserPreference
        userPreference = UserPreference.getInstance(requireContext().dataStore)

        // Initialize DAO
        chatSaveMessageDao = ChatDatabase.getDatabase(requireContext()).chatSaveMessageDao()

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.aboutUsContainer.setOnClickListener {
            navigateToAboutUsActivity()
        }

        binding.deleteHistoryContainer.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        // Update UI dengan data pengguna dari Firebase Auth
        updateUIWithUserData()
    }

    private fun updateUIWithUserData() {
        val currentUser: FirebaseUser? = firebaseAuth.currentUser
        if (currentUser != null) {
            val email = currentUser.email
            var displayName = currentUser.displayName

            if (displayName.isNullOrEmpty() && email != null) {
                // Menghilangkan bagian setelah '@' pada email untuk digunakan sebagai displayName
                displayName = email.substringBefore("@")
            }

            Log.d("SettingFragment", "Display Name: $displayName")
            Log.d("SettingFragment", "Email: $email")

            // Update nama pengguna dan email
            binding.userName.text = displayName ?: "User"
            binding.userEmail.text = email
        }
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Riwayat")
            .setMessage("Apakah Anda yakin ingin menghapus semua riwayat obrolan?")
            .setPositiveButton("Ya") { _, _ ->
                deleteChatHistory()
            }
            .setNegativeButton("Tidak", null)
            .show()
    }

    private fun deleteChatHistory() {
        CoroutineScope(Dispatchers.IO).launch {
            chatSaveMessageDao.deleteAllMessages()
            launch(Dispatchers.Main) {
                showDeletionSuccessMessage()
            }
        }
    }

    private fun showDeletionSuccessMessage() {
        AlertDialog.Builder(requireContext())
            .setTitle("Riwayat Dihapus")
            .setMessage("Semua riwayat obrolan telah berhasil dihapus,silahkan restart aplikasi untuk melihat perubahan.")
            .setPositiveButton("OK", null)
            .show()
    }

    private fun logout() {
        CoroutineScope(Dispatchers.IO).launch {
            userPreference.logout()
            launch(Dispatchers.Main) {
                firebaseAuth.signOut()
                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                requireActivity().finish()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun navigateToAboutUsActivity() {
        val intent = Intent(requireContext(), AboutUsActivity::class.java)
        startActivity(intent)
    }
}
