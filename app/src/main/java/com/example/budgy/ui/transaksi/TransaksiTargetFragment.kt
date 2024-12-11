package com.example.budgy.ui.transaksi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.budgy.R
import com.example.budgy.data.response.PostTargetResponse
import com.example.budgy.data.retrofit.ApiConfig
import com.example.budgy.databinding.FragmentTransaksiTargetBinding
import com.example.budgy.ui.home.HomeFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class TransaksiTargetFragment : Fragment() {
    private var _binding: FragmentTransaksiTargetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransaksiTargetBinding.inflate(inflater, container, false)
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
        binding.btnPengeluaran.setOnClickListener {
            val transaksiPengeluaranFragment = TransaksiPengeluaranFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, transaksiPengeluaranFragment)
                .addToBackStack(null)
                .commit()
        }

        binding.btnSave.setOnClickListener {
            val nominalText = binding.etTotal.text.toString()

            val nominal = nominalText.toIntOrNull()
            if (nominal != null) {
                postTarget(nominal)
            } else {
                Toast.makeText(requireContext(), "Nominal harus diisi dengan angka", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }



    private fun postTarget(nominal: Int) {
        val apiService = ApiConfig.getApiService(requireContext())
        val todayDateWithTime = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).format(Date())
        val postTargetRequest = PostTargetResponse(
            nominal = nominal,
            tanggal = todayDateWithTime
        )

        apiService.postTarget(postTargetRequest).enqueue(object : Callback<PostTargetResponse> {
            override fun onResponse(
                call: Call<PostTargetResponse>,
                response: Response<PostTargetResponse>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Target berhasil disimpan!", Toast.LENGTH_SHORT).show()

                    val homeFragment = HomeFragment()
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, homeFragment)
                        .addToBackStack(null)
                        .commit()
                } else {
                    Toast.makeText(requireContext(), "Gagal menyimpan target: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PostTargetResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Kesalahan jaringan: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
