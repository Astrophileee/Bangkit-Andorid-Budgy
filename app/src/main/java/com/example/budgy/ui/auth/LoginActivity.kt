package com.example.budgy.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.budgy.MainActivity
import com.example.budgy.R
import com.example.budgy.data.response.LoginResponse
import com.example.budgy.data.retrofit.ApiConfig
import com.example.budgy.ui.helper.PreferenceHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var etNama: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var tvRegister: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etNama = findViewById(R.id.etNama)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        progressBar = findViewById(R.id.progressBar)
        tvRegister = findViewById(R.id.tvRegister)

        btnLogin.setOnClickListener {
            val nama = etNama.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (nama.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Nama dan Password harus diisi!", Toast.LENGTH_SHORT).show()
            } else {
                loginUser(nama, password)
            }
        }

        tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser(nama: String, password: String) {
        progressBar.visibility = View.VISIBLE

        val apiService = ApiConfig.getApiService(this)
        val userData = mapOf(
            "nama" to nama,
            "password" to password
        )

        apiService.loginUser(userData).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    Log.d("Login", "Response: ${loginResponse?.message} - Token: ${loginResponse?.token}")
                    Toast.makeText(
                        this@LoginActivity,
                        loginResponse?.message ?: "Login berhasil!",
                        Toast.LENGTH_SHORT
                    ).show()

                    loginResponse?.token?.let {
                        PreferenceHelper.saveToken(this@LoginActivity, it)
                        Log.d("LoginActivity", "Token disimpan: $it")
                    }

                    PreferenceHelper.saveIsLoggedIn(this@LoginActivity, true)
                    PreferenceHelper.saveUserName(this@LoginActivity, nama)

                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Log.e("Login", "Login gagal: ${response.code()} - ${response.message()}")
                    Toast.makeText(
                        this@LoginActivity,
                        "Gagal login: ${response.code()} - ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@LoginActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
