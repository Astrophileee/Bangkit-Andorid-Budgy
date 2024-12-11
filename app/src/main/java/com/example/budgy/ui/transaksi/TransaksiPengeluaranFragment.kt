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
import com.example.budgy.data.response.PostPengeluaranData
import com.example.budgy.data.response.PostPengeluaranResponse
import com.example.budgy.data.retrofit.ApiConfig
import com.example.budgy.databinding.FragmentTransaksiPengeluaranBinding
import com.example.budgy.ui.home.HomeFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class TransaksiPengeluaranFragment : Fragment() {
    private var _binding: FragmentTransaksiPengeluaranBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransaksiPengeluaranBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.btnBack.setOnClickListener {
            val homeFragment = HomeFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, homeFragment)
                .addToBackStack(null)
                .commit()
        }

        binding.btnPendapatan.setOnClickListener {
            val transaksiPendapatanFragment = TransaksiPendapatanFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, transaksiPendapatanFragment)
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

        setupCategoryDropdown()

        binding.etDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(requireContext(),
                { _, selectedYear, selectedMonth, selectedDay ->
                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val selectedDate = Calendar.getInstance().apply {
                        set(selectedYear, selectedMonth, selectedDay)
                    }.time
                    val formattedDate = sdf.format(selectedDate)
                    binding.etDate.setText(formattedDate)
                }, year, month, day)
            datePickerDialog.show()
        }

        binding.btnSave.setOnClickListener {
            val nominalText = binding.etTotal.text.toString()
            val kategoriText = binding.spCategory.text.toString()
            val tanggal = binding.etDate.text.toString()

            val nominal = nominalText.toIntOrNull()

            if (nominal != null && kategoriText.isNotEmpty() && tanggal.isNotEmpty()) {
                val categories = resources.getStringArray(R.array.kategori_pengeluaran)
                val kategoriId = categories.indexOf(kategoriText)
                Log.d("TransaksiPengeluaran", "Kategori Text: $kategoriText, Kategori ID: $kategoriId")
                if (kategoriId != -1) {
                    sendPengeluaranData(nominal, kategoriId, tanggal)
                } else {
                    Toast.makeText(requireContext(), "Kategori tidak valid", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Semua field harus diisi dengan benar", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun setupCategoryDropdown() {
        val categories = resources.getStringArray(R.array.kategori_pengeluaran)
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

    private fun sendPengeluaranData(nominal: Int, kategoriId: Int, tanggal: String) {

        val postPengeluaranData = PostPengeluaranData(
            kategoriId = kategoriId,
            nominal = nominal.toString(),
            tanggal = tanggal
        )

        val apiService = ApiConfig.getApiService(requireContext())
        apiService.postPengeluaran(postPengeluaranData).enqueue(object :
            Callback<PostPengeluaranResponse> {
            override fun onResponse(call: Call<PostPengeluaranResponse>, response: Response<PostPengeluaranResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Data berhasil dikirim", Toast.LENGTH_SHORT).show()
                    val homeFragment = HomeFragment()
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, homeFragment)
                        .addToBackStack(null)
                        .commit()
                } else {
                    Log.e("TransaksiPengeluaran", "Error: ${response.errorBody()?.string()}")
                    Toast.makeText(requireContext(), "Gagal mengirim data: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PostPengeluaranResponse>, t: Throwable) {
                Log.e("TransaksiPengeluaran", "Failure: ${t.message}")
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
