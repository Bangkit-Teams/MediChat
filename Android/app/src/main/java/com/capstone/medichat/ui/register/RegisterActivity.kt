package com.capstone.medichat.ui.register

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.capstone.medichat.R
import com.capstone.medichat.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance()

        setupView()
        setupAction()

        binding.emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (!isValidEmail(s.toString())) {
                    binding.emailEditText.error = getString(R.string.email_error)
                } else {
                    binding.emailEditText.error = null
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern =
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}(?:\\.\\p{L}{2,})?\$"
        return email.matches(emailPattern.toRegex())
    }

    private fun setupView() {
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.signupButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (validateForm(name, email, password)) {
                signUp(email, password)
            }
        }
    }

    private fun validateForm(name: String, email: String, password: String): Boolean {
        var valid = true

        if (name.isEmpty()) {
            binding.nameEditText.error = getString(R.string.name_error)
            valid = false
        } else {
            binding.nameEditText.error = null
        }

        if (email.isEmpty() || !isValidEmail(email)) {
            binding.emailEditText.error = getString(R.string.email_error)
            valid = false
        } else {
            binding.emailEditText.error = null
        }

        if (password.isEmpty() || password.length < 6) {
            binding.passwordEditText.error = getString(R.string.password_error)
            valid = false
        } else {
            binding.passwordEditText.error = null
        }

        return valid
    }

    private fun signUp(email: String, password: String) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = mAuth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    showToast(task.exception?.message ?: "Sign Up Failed")
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            // User is signed in, show a success message or navigate to another activity
            showToast("Registration Successful")
            // Navigate to your desired activity
        } else {
            // User is signed out, show a sign-in button
            showToast("Registration Failed")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
