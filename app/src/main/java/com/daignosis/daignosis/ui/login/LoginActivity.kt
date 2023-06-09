package com.daignosis.daignosis.ui.login

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.daignosis.daignosis.R
import com.daignosis.daignosis.databinding.ActivityLoginBinding
import com.daignosis.daignosis.ui.forgotpw.ForgotPwActivity
import com.daignosis.daignosis.ui.main.MainActivity
import com.daignosis.daignosis.ui.register.RegisterActivity
import com.daignosis.daignosis.utils.Result
import com.daignosis.daignosis.utils.Util.isValidEmail
import com.daignosis.daignosis.utils.ViewModelFactory
import com.google.android.material.snackbar.Snackbar

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "pref")
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()

        loginViewModel = ViewModelProvider(
            this,
            ViewModelFactory(this)
        )[LoginViewModel::class.java]

        binding.btnLoginpage.setOnClickListener {
            login()
        }

        binding.btnRegis.setOnClickListener{
            startActivity(Intent(this,RegisterActivity::class.java))
        }

        binding.btnForgot.setOnClickListener {
            startActivity(Intent(this,ForgotPwActivity::class.java))
        }
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

    private fun validPassword(): String? {
        val passwordText = binding.edtLoginPw.text.toString()
        if (passwordText.length < 8) {
            return "Minimum 8 Character Password"
        }
        return null
    }

    private fun validEmail(): String? {
        val email = binding.edtLoginEmail.text.toString()
        if (!email.isValidEmail()){
            return "Format Email Tidak Valid"
        }
        return null
    }

    private fun login() {
        binding.emailTextLayout.helperText = validEmail()
        binding.passwordTextLayout.helperText = validPassword()

        val validEmail = binding.emailTextLayout.helperText == null
        val validPassword = binding.passwordTextLayout.helperText == null

        if (validEmail && validPassword) {
            binding.progressBar.visibility = View.VISIBLE
            signupAuth()
            resetForm()

        }
        else
            Toast.makeText(this, "Invalid Form", Toast.LENGTH_SHORT).show()
    }

    private fun signupAuth() {
        val email = binding.edtLoginEmail.text.toString()
        val password = binding.edtLoginPw.text.toString()

        loginViewModel.login(email, password).observe(this@LoginActivity) {
            when(it) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    Snackbar.make(
                        window.decorView.rootView,
                        getString(R.string.snack_login),
                        Snackbar.LENGTH_SHORT
                    ).show()
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Snackbar.make(
                        window.decorView.rootView,
                        getString(R.string.snack_loginError),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun resetForm() {
        binding.edtLoginEmail.text = null
        binding.edtLoginPw.text = null

        binding.emailTextLayout.helperText = " "
        binding.passwordTextLayout.helperText = " "
    }
}