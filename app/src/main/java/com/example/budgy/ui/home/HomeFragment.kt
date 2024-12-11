package com.example.budgy.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.budgy.R
import com.example.budgy.data.response.DataItem
import com.example.budgy.data.response.PendapatanResponse
import com.example.budgy.data.response.PengeluaranResponse
import com.example.budgy.data.response.TargetResponse
import com.example.budgy.ui.adapter.PencatatanAdapter
import com.example.budgy.ui.adapter.PencatatanItem
import com.example.budgy.data.retrofit.ApiConfig
import com.example.budgy.ui.helper.PreferenceHelper
import com.example.budgy.ui.transaksi.EditTransaksiPendapatanFragment
import com.example.budgy.ui.transaksi.EditTransaksiPengeluaranFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var monthYearTextView: TextView
    private lateinit var userGreetingTextView: TextView
    private lateinit var saldoTextView: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var pencatatanAdapter: PencatatanAdapter

    private val calendar: Calendar = Calendar.getInstance()

    private lateinit var pendapatanTextView: TextView
    private lateinit var pengeluaranTextView: TextView
    private lateinit var targetTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Inisialisasi View
        monthYearTextView = view.findViewById(R.id.monthYear)
        userGreetingTextView = view.findViewById(R.id.userGreetingTextView)
        saldoTextView = view.findViewById(R.id.tvSaldo)
        pendapatanTextView = view.findViewById(R.id.tvPendapatan)
        pengeluaranTextView = view.findViewById(R.id.tvPengeluaran)
        recyclerView = view.findViewById(R.id.rvPencatatan)
        targetTextView = view.findViewById(R.id.targetTextView)

        val leftArrow: ImageButton = view.findViewById(R.id.leftArrow)
        val rightArrow: ImageButton = view.findViewById(R.id.rightArrow)

        // Setup RecyclerView
        pencatatanAdapter = PencatatanAdapter(mutableListOf()) { item ->
            if (item.type == "Pengeluaran") {
                navigateToEditPengeluaran(item)
            } else if (item.type == "Pendapatan") {
                navigateToEditPendapatan(item)
            }
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = pencatatanAdapter

        // Tampilkan bulan dan tahun saat ini
        updateMonthYear()

        // Set onClickListener untuk tombol
        leftArrow.setOnClickListener { changeMonth(-1) }
        rightArrow.setOnClickListener { changeMonth(1) }

        // Tampilkan nama pengguna
        displayUserGreeting()

        // Load data
        loadCombinedData()
        loadTargetData()

        return view
    }

    private fun updateMonthYear() {
        val dateFormat = SimpleDateFormat("MMMM yyyy", Locale("id", "ID"))
        monthYearTextView.text = dateFormat.format(calendar.time)
    }

    private fun changeMonth(amount: Int) {
        calendar.add(Calendar.MONTH, amount)
        updateMonthYear()
        loadCombinedData()
        loadTargetData()
    }

    private fun displayUserGreeting() {
        val isLoggedIn = PreferenceHelper.getIsLoggedIn(requireContext())
        val userName = PreferenceHelper.getUserName(requireContext())
        userGreetingTextView.text = if (isLoggedIn) {
            "Hello $userName!"
        } else {
            "Hello Guest!"
        }
    }

    private fun loadCombinedData() {
        val token = PreferenceHelper.getToken(requireContext())
        if (token.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Token tidak ditemukan, silakan login kembali", Toast.LENGTH_SHORT).show()
            return
        }

        val apiService = ApiConfig.getApiService(requireContext())
        val combinedList = mutableListOf<PencatatanItem>()

        // Panggil API secara paralel
        val pendapatanCall = apiService.getPendapatan()
        val pengeluaranCall = apiService.getPengeluaran()

        pendapatanCall.enqueue(object : Callback<PendapatanResponse> {
            override fun onResponse(call: Call<PendapatanResponse>, response: Response<PendapatanResponse>) {
                if (response.isSuccessful) {
                    val pendapatanList = response.body()?.data?.filterNotNull() ?: emptyList()
                    pendapatanList.forEach {
                        combinedList.add(
                            PencatatanItem(
                                id = it.id,
                                nominal = it.nominal.orEmpty(),
                                kategori = it.kategori.orEmpty(),
                                type = "Pendapatan",
                                tanggal = it.tanggal.orEmpty()
                            )
                        )
                    }
                    processGroupedData(combinedList)
                } else {
                    Toast.makeText(requireContext(), "Gagal memuat data pendapatan", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PendapatanResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

        pengeluaranCall.enqueue(object : Callback<PengeluaranResponse> {
            override fun onResponse(call: Call<PengeluaranResponse>, response: Response<PengeluaranResponse>) {
                if (response.isSuccessful) {
                    val pengeluaranList = response.body()?.data?.filterNotNull() ?: emptyList()
                    pengeluaranList.forEach {
                        combinedList.add(
                            PencatatanItem(
                                id = it.id,
                                nominal = it.nominal.orEmpty(),
                                kategori = it.kategori.orEmpty(),
                                type = "Pengeluaran",
                                tanggal = it.tanggal.orEmpty()
                            )
                        )
                    }
                    processGroupedData(combinedList)
                } else {
                    Toast.makeText(requireContext(), "Gagal memuat data pengeluaran", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PengeluaranResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun processGroupedData(data: List<PencatatanItem>) {
        val dateFormat = SimpleDateFormat("dd EEE", Locale("id", "ID"))
        val selectedMonth = calendar.get(Calendar.MONTH)
        val selectedYear = calendar.get(Calendar.YEAR)

        // Filter data berdasarkan bulan dan tahun yang dipilih
        val filteredData = data.filter { item ->
            val date = try {
                SimpleDateFormat("yyyy-MM-dd", Locale("id", "ID")).parse(item.tanggal)
            } catch (e: Exception) {
                null
            }
            date != null &&
                    date.month == selectedMonth &&
                    date.year + 1900 == selectedYear
        }

        // Hitung total pendapatan dan pengeluaran
        var totalPendapatan = 0.0
        var totalPengeluaran = 0.0

        filteredData.forEach { item ->
            val nominal = item.nominal
                ?.replace("Rp", "") // Hilangkan simbol Rupiah
                ?.replace(",", "") // Hilangkan koma (untuk format 1.000,00)
                ?.toDoubleOrNull() ?: 0.0 // Konversi ke Double tanpa menghapus titik desimal

            if (item.type == "Pendapatan") {
                totalPendapatan += nominal
            } else if (item.type == "Pengeluaran") {
                totalPengeluaran += nominal
            }
        }

        // Update tampilan pendapatan, pengeluaran, dan saldo
        pendapatanTextView.text = formatCurrency(totalPendapatan)
        pengeluaranTextView.text = formatCurrency(totalPengeluaran)
        saldoTextView.text = formatCurrency(totalPendapatan - totalPengeluaran)

        // Grupkan data berdasarkan tanggal
        val groupedData = filteredData.groupBy { item ->
            val date = SimpleDateFormat("yyyy-MM-dd", Locale("id", "ID")).parse(item.tanggal)
            dateFormat.format(date ?: Date())
        }

        // Urutkan data secara descending berdasarkan tanggal
        val sortedGroupedData = groupedData.toSortedMap(compareByDescending { date ->
            SimpleDateFormat("dd EEE", Locale("id", "ID")).parse(date)?.time ?: 0L
        })

        val groupedList = mutableListOf<PencatatanItem>()
        sortedGroupedData.forEach { (date, items) ->
            groupedList.add(PencatatanItem(tanggal = date)) // Header
            groupedList.addAll(items)
        }

        pencatatanAdapter.updateData(groupedList)
    }
    private fun loadTargetData() {
        val apiService = ApiConfig.getApiService(requireContext())
        val targetCall = apiService.getTarget()

        targetCall.enqueue(object : Callback<TargetResponse> {
            override fun onResponse(call: Call<TargetResponse>, response: Response<TargetResponse>) {
                if (response.isSuccessful) {
                    val targetResponse = response.body()
                    Log.d("HomeFragment", "Response Body: $targetResponse") // Log respons

                    val targetList = targetResponse?.data?.filterNotNull() ?: emptyList()
                    Log.d("HomeFragment", "Target List: $targetList") // Log data target

                    val lastTargetPerMonth = processTargetData(targetList)
                    displayLastTarget(lastTargetPerMonth)
                } else {
                    Toast.makeText(requireContext(), "Gagal memuat data target", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<TargetResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun processTargetData(targets: List<DataItem>): Map<String, DataItem> {
        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale("id", "ID"))
        val outputDateFormat = SimpleDateFormat("yyyy-MM", Locale("id", "ID"))
        inputDateFormat.timeZone = TimeZone.getTimeZone("UTC") // Set ke UTC

        Log.d("HomeFragment", "Raw Targets: $targets") // Log data target sebelum diproses

        return targets
            .filter { it.tanggal != null } // Filter DataItem dengan tanggal tidak null
            .onEach { Log.d("HomeFragment", "Filtered Target: $it") } // Tambahkan log
            .groupBy { target ->
                try {
                    val date = inputDateFormat.parse(target.tanggal!!)
                    outputDateFormat.format(date) // Konversi ke format 'yyyy-MM'
                } catch (e: Exception) {
                    Log.e("HomeFragment", "Date Parsing Error: ${e.message}")
                    null
                }
            }
            .onEach { (key, value) -> Log.d("HomeFragment", "Grouped Target: $key -> $value") } // Tambahkan log
            .filterKeys { it != null } // Hapus grup dengan kunci null
            .mapValues { (_, group) ->
                group.maxByOrNull { target ->
                    try {
                        inputDateFormat.parse(target.tanggal!!)?.time ?: 0L
                    } catch (e: Exception) {
                        0L
                    }
                }!!
            }
            .onEach { (key, value) -> Log.d("HomeFragment", "Processed Target: $key -> $value") } // Tambahkan log
            .mapKeys { it.key!! } // !! aman karena sudah difilter
    }



    private fun displayLastTarget(lastTargetPerMonth: Map<String, DataItem>) {
        val selectedMonthYear = SimpleDateFormat("yyyy-MM", Locale("id", "ID")).format(calendar.time)
        Log.d("HomeFragment", "Selected Month-Year: $selectedMonthYear") // Log bulan yang dipilih

        val target = lastTargetPerMonth[selectedMonthYear]
        Log.d("HomeFragment", "Target for $selectedMonthYear: $target") // Log target untuk bulan yang dipilih

        val targetAmount = target?.nominal?.toDoubleOrNull() ?: 0.0
        Log.d("HomeFragment", "Target Amount: $targetAmount") // Log jumlah target

        targetTextView.text = formatCurrency(targetAmount)
    }




    private fun formatCurrency(amount: Double): String {
        val localeID = Locale("id", "ID")
        val formatter = java.text.NumberFormat.getCurrencyInstance(localeID)
        return formatter.format(amount).replace("Rp", "Rp ").trim()
    }

    private fun navigateToEditPengeluaran(item: PencatatanItem) {
        val fragment = EditTransaksiPengeluaranFragment()

        // Format tanggal ke yyyy-MM-dd
        val formattedDate = try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale("id", "ID"))
            val date = inputFormat.parse(item.tanggal)
            inputFormat.format(date ?: Date())
        } catch (e: Exception) {
            item.tanggal
        }

        fragment.arguments = Bundle().apply {
            putInt("id", item.id ?: 0)
            putString("tanggal", formattedDate)
            putString("nominal", item.nominal?.toDoubleOrNull()?.toInt().toString())
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }


    private fun navigateToEditPendapatan(item: PencatatanItem) {
        val fragment = EditTransaksiPendapatanFragment()

        // Format tanggal ke yyyy-MM-dd
        val formattedDate = try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale("id", "ID"))
            val date = inputFormat.parse(item.tanggal)
            inputFormat.format(date ?: Date())
        } catch (e: Exception) {
            item.tanggal
        }

        fragment.arguments = Bundle().apply {
            putInt("id", item.id ?: 0)
            putString("tanggal", formattedDate)
            putString("nominal", item.nominal?.toDoubleOrNull()?.toInt().toString())
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

}
