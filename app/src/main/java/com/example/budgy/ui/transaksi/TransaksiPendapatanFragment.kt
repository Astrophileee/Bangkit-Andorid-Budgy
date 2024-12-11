package com.example.budgy.ui.transaksi

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.budgy.R
import com.example.budgy.data.response.PostPendapatanData
import com.example.budgy.data.response.PostPendapatanResponse
import com.example.budgy.data.retrofit.ApiConfig
import com.example.budgy.databinding.FragmentTransaksiPendapatanBinding
import com.example.budgy.ui.home.HomeFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar
import java.util.Locale
import java.text.SimpleDateFormat

class TransaksiPendapatanFragment : Fragment() {
    private var _binding: FragmentTransaksiPendapatanBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransaksiPendapatanBinding.inflate(inflater, container, false)
        val view = binding.root

        // Handle back button click
        binding.btnBack.setOnClickListener {
            val homeFragment = HomeFragment() // Buat instance HomeFragment
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, homeFragment)
                .addToBackStack(null) // Tambahkan ke back stack jika diperlukan
                .commit()
        }

        binding.btnPengeluaran.setOnClickListener {
            val transaksiPengeluaranFragment = TransaksiPengeluaranFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, transaksiPengeluaranFragment)
                .addToBackStack(null)
                .commit()
        }
        binding.btnTarget.setOnClickListener {
            val transaksiTargetFragment = TransaksiTargetFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, transaksiTargetFragment)
                .addToBackStack(null)
                .commit()
        }

        // Setup dropdown kategori pendapatan
        setupCategoryDropdown()

        // Date Picker setup
        binding.etDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(requireContext(),
                { _, selectedYear, selectedMonth, selectedDay ->
                    // Format tanggal yang diinginkan (yyyy-MM-dd)
                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val selectedDate = Calendar.getInstance().apply {
                        set(selectedYear, selectedMonth, selectedDay)
                    }.time
                    val formattedDate = sdf.format(selectedDate)
                    binding.etDate.setText(formattedDate)
                }, year, month, day)
            datePickerDialog.show()
        }

        // Submit data ke API
        binding.btnSave.setOnClickListener {
            val nominalText = binding.etTotal.text.toString()
            val kategoriText = binding.spCategory.text.toString()
            val tanggal = binding.etDate.text.toString()

            // Pastikan nominal diubah menjadi Integer
            val nominal = nominalText.toIntOrNull()

            if (nominal != null && kategoriText.isNotEmpty() && tanggal.isNotEmpty()) {
                val categories = resources.getStringArray(R.array.kategori_pendapatan)
                val kategoriId = categories.indexOf(kategoriText)
                Log.d("TransaksiPendapatan", "Kategori Text: $kategoriText, Kategori ID: $kategoriId")
                if (kategoriId != -1) {
                    sendPendapatanData(nominal, kategoriId, tanggal)
                } else {
                    Toast.makeText(requireContext(), "Kategori tidak valid", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Semua field harus diisi dengan benar", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    // Fungsi untuk setup dropdown kategori
    private fun setupCategoryDropdown() {
        val categories = resources.getStringArray(R.array.kategori_pendapatan)
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            categories
        )
        binding.spCategory.setAdapter(adapter)
        binding.spCategory.setOnClickListener {
            binding.spCategory.showDropDown()
        }
    }

    // Fungsi untuk mengirim data pendapatan ke API
    // Fungsi untuk mengirim data pendapatan ke API
    private fun sendPendapatanData(nominal: Int, kategoriId: Int, tanggal: String) {

        // Membuat objek PostPendapatanData
        val postPendapatanData = PostPendapatanData(
            kategoriId = kategoriId,
            nominal = nominal.toString(),  // Konversi nominal menjadi string
            tanggal = tanggal
        )

        val apiService = ApiConfig.getApiService(requireContext())
        apiService.postPendapatan(postPendapatanData).enqueue(object : Callback<PostPendapatanResponse> {
            override fun onResponse(call: Call<PostPendapatanResponse>, response: Response<PostPendapatanResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Data berhasil dikirim", Toast.LENGTH_SHORT).show()
                    val homeFragment = HomeFragment() // Buat instance HomeFragment
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, homeFragment)
                        .addToBackStack(null) // Tambahkan ke back stack jika diperlukan
                        .commit()
                } else {
                    Log.e("TransaksiPendapatan", "Error: ${response.errorBody()?.string()}")
                    Toast.makeText(requireContext(), "Gagal mengirim data: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PostPendapatanResponse>, t: Throwable) {
                Log.e("TransaksiPendapatan", "Failure: ${t.message}")
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
