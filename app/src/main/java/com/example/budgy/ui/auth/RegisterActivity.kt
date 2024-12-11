package com.example.budgy.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.budgy.R
import com.example.budgy.data.response.RegisterResponse
import com.example.budgy.data.retrofit.ApiConfig
import com.example.budgy.ui.helper.PreferenceHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var etNama: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Inisialisasi View
        etNama = findViewById(R.id.etNama)
        etPassword = findViewById(R.id.etPassword)
        btnRegister = findViewById(R.id.btnRegister)
        progressBar = findViewById(R.id.progressBar)

        // Set Listener pada Tombol Register
        btnRegister.setOnClickListener {
            val nama = etNama.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // Validasi Input
            if (nama.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Nama dan Password harus diisi", Toast.LENGTH_SHORT).show()
            } else {
                registerUser(nama, password)
            }
        }
    }

    private fun registerUser(nama: String, password: String) {
        // Tampilkan ProgressBar
        progressBar.visibility = View.VISIBLE

        // Buat Data untuk Dikirim ke API
        val userData = mapOf(
            "nama" to nama,
            "password" to password
        )

        // Panggil API menggunakan Retrofit
        val apiService = ApiConfig.getApiService(this)
        apiService.registerUser(userData).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val registerResponse = response.body()
                    Toast.makeText(
                        this@RegisterActivity,
                        registerResponse?.message ?: "Registrasi berhasil",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Pindah ke LoginActivity untuk login dan mendapatkan token
                    val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish() // Menutup RegisterActivity agar tidak kembali ke sini
                } else {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Gagal: ${response.code()} - ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@RegisterActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

