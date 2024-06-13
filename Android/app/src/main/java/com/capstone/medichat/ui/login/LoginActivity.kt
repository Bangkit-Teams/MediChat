package com.capstone.medichat.ui.login

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.capstone.medichat.data.preference.UserModel
import com.capstone.medichat.data.preference.UserPreference
import com.capstone.medichat.data.preference.dataStore
import com.capstone.medichat.databinding.ActivityLoginBinding
import com.capstone.medichat.ui.ViewModelFactory
import com.capstone.medichat.ui.main.MainActivity
import com.capstone.medichat.data.repo.Result
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            if (isUserLoggedIn()) {
                navigateToMain()
            } else {
                setupView()
                setupAction()
            }
        }
    }

    private suspend fun isUserLoggedIn(): Boolean {
        val userPreference = UserPreference.getInstance(dataStore)
        val session = userPreference.getSession().first()
        return session.isLogin
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                viewModel.login(email, password).observe(this) { result ->
                    if (result != null) {
                        when (result) {
                            is Result.Loading -> {
                                showLoading(true)
                            }

                            is Result.Success -> {
                                showLoading(false)
                                val loginResponse = result.data
                                showToast(loginResponse.message)

                                val userModel = UserModel(
                                    email = email,
                                    token = loginResponse.loginResult.token,
                                    password = password,
                                    isLogin = true
                                )
                                lifecycleScope.launch {
                                    saveUserSession(userModel)
                                }

                                AlertDialog.Builder(this).apply {
                                    setTitle("Success!")
                                    setMessage("Login success")
                                    setPositiveButton("Next") { _, _ ->
                                        navigateToMain()
                                    }
                                    create()
                                    show()
                                }
                            }

                            is Result.Error -> {
                                showLoading(false)
                                showToast(result.error)
                            }
                        }
                    }
                }
            } else {
                showToast("Email and Password cannot be empty")
            }
        }
    }

    private suspend fun saveUserSession(userModel: UserModel) {
        val userPreference = UserPreference.getInstance(dataStore)
        userPreference.saveSession(userModel)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
