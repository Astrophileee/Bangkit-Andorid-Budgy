package com.example.budgy.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
import com.example.budgy.data.response.PostRekomendasiResponse
import com.example.budgy.data.response.RekomendasiResponse
import com.example.budgy.data.response.RekomendasiResponseItem
import com.example.budgy.data.response.TargetResponse
import com.example.budgy.ui.adapter.PencatatanAdapter
import com.example.budgy.ui.adapter.PencatatanItem
import com.example.budgy.data.retrofit.ApiConfig
import com.example.budgy.data.retrofit.RekomendasiApiConfig
import com.example.budgy.ui.helper.PreferenceHelper
import com.example.budgy.ui.transaksi.EditTransaksiPendapatanFragment
import com.example.budgy.ui.transaksi.EditTransaksiPengeluaranFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var monthYearTextView: TextView
    private lateinit var userGreetingTextView: TextView
    private lateinit var saldoTextView: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var pencatatanAdapter: PencatatanAdapter
    private lateinit var RekomendasiTextView: TextView
    private lateinit var btnGenerate: Button

    private val calendar: Calendar = Calendar.getInstance()

    private lateinit var pendapatanTextView: TextView
    private lateinit var pengeluaranTextView: TextView
    private lateinit var targetTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        monthYearTextView = view.findViewById(R.id.monthYear)
        userGreetingTextView = view.findViewById(R.id.userGreetingTextView)
        saldoTextView = view.findViewById(R.id.tvSaldo)
        pendapatanTextView = view.findViewById(R.id.tvPendapatan)
        pengeluaranTextView = view.findViewById(R.id.tvPengeluaran)
        recyclerView = view.findViewById(R.id.rvPencatatan)
        targetTextView = view.findViewById(R.id.targetTextView)
        RekomendasiTextView = view.findViewById(R.id.tvRekomendasi)
        btnGenerate = view.findViewById(R.id.btnGenerate)

        val leftArrow: ImageButton = view.findViewById(R.id.leftArrow)
        val rightArrow: ImageButton = view.findViewById(R.id.rightArrow)

        pencatatanAdapter = PencatatanAdapter(mutableListOf()) { item ->
            if (item.type == "Pengeluaran") {
                navigateToEditPengeluaran(item)
            } else if (item.type == "Pendapatan") {
                navigateToEditPendapatan(item)
            }
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = pencatatanAdapter

        updateMonthYear()
        toggleGenerateButtonVisibility(btnGenerate)

        leftArrow.setOnClickListener { changeMonth(-1) }
        rightArrow.setOnClickListener { changeMonth(1) }

        btnGenerate.setOnClickListener {
            generateRekomendasi()
        }

        displayUserGreeting()

        loadCombinedData()
        loadTargetData()
        loadRekomendasiData()

        return view
    }

    private fun toggleGenerateButtonVisibility(btnGenerate: Button) {
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        val selectedMonth = calendar.get(Calendar.MONTH)
        val selectedYear = calendar.get(Calendar.YEAR)

        if (currentMonth == selectedMonth && currentYear == selectedYear) {
            btnGenerate.visibility = View.VISIBLE
        } else {
            btnGenerate.visibility = View.GONE
        }
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
        loadRekomendasiData()
        toggleGenerateButtonVisibility(btnGenerate)
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

        var totalPendapatan = 0.0
        var totalPengeluaran = 0.0

        filteredData.forEach { item ->
            val nominal = item.nominal
                ?.replace("Rp", "")
                ?.replace(",", "")
                ?.toDoubleOrNull() ?: 0.0

            if (item.type == "Pendapatan") {
                totalPendapatan += nominal
            } else if (item.type == "Pengeluaran") {
                totalPengeluaran += nominal
            }
        }

        pendapatanTextView.text = formatCurrency(totalPendapatan)
        pengeluaranTextView.text = formatCurrency(totalPengeluaran)
        saldoTextView.text = formatCurrency(totalPendapatan - totalPengeluaran)

        val groupedData = filteredData.groupBy { item ->
            val date = SimpleDateFormat("yyyy-MM-dd", Locale("id", "ID")).parse(item.tanggal)
            dateFormat.format(date ?: Date())
        }

        val sortedGroupedData = groupedData.toSortedMap(compareByDescending { date ->
            SimpleDateFormat("dd EEE", Locale("id", "ID")).parse(date)?.time ?: 0L
        })

        val groupedList = mutableListOf<PencatatanItem>()
        sortedGroupedData.forEach { (date, items) ->
            groupedList.add(PencatatanItem(tanggal = date))
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
                    Log.d("HomeFragment", "Response Body: $targetResponse")

                    val targetList = targetResponse?.data?.filterNotNull() ?: emptyList()
                    Log.d("HomeFragment", "Target List: $targetList")

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
        inputDateFormat.timeZone = TimeZone.getTimeZone("UTC")

        Log.d("HomeFragment", "Raw Targets: $targets")

        return targets
            .filter { it.tanggal != null }
            .onEach { Log.d("HomeFragment", "Filtered Target: $it") }
            .groupBy { target ->
                try {
                    val date = inputDateFormat.parse(target.tanggal!!)
                    outputDateFormat.format(date)
                } catch (e: Exception) {
                    Log.e("HomeFragment", "Date Parsing Error: ${e.message}")
                    null
                }
            }
            .onEach { (key, value) -> Log.d("HomeFragment", "Grouped Target: $key -> $value") }
            .filterKeys { it != null }
            .mapValues { (_, group) ->
                group.maxByOrNull { target ->
                    try {
                        inputDateFormat.parse(target.tanggal!!)?.time ?: 0L
                    } catch (e: Exception) {
                        0L
                    }
                }!!
            }
            .onEach { (key, value) -> Log.d("HomeFragment", "Processed Target: $key -> $value") }
            .mapKeys { it.key!! }
    }



    private fun displayLastTarget(lastTargetPerMonth: Map<String, DataItem>) {
        val selectedMonthYear = SimpleDateFormat("yyyy-MM", Locale("id", "ID")).format(calendar.time)
        Log.d("HomeFragment", "Selected Month-Year: $selectedMonthYear")

        val target = lastTargetPerMonth[selectedMonthYear]
        Log.d("HomeFragment", "Target for $selectedMonthYear: $target")

        val targetAmount = target?.nominal?.toDoubleOrNull() ?: 0.0
        Log.d("HomeFragment", "Target Amount: $targetAmount")

        targetTextView.text = formatCurrency(targetAmount)
    }




    private fun formatCurrency(amount: Double): String {
        val localeID = Locale("id", "ID")
        val formatter = java.text.NumberFormat.getCurrencyInstance(localeID)
        return formatter.format(amount).replace("Rp", "Rp ").trim()
    }

    private fun navigateToEditPengeluaran(item: PencatatanItem) {
        val fragment = EditTransaksiPengeluaranFragment()

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

    private fun loadRekomendasiData() {
        val apiService = RekomendasiApiConfig.getApiService(requireContext())
        val selectedMonthYear = SimpleDateFormat("yyyy-MM", Locale("id", "ID")).format(calendar.time)
        val rekomendasiCall = apiService.getRekomendasi()

        rekomendasiCall.enqueue(object : Callback<List<RekomendasiResponseItem>> {
            override fun onResponse(call: Call<List<RekomendasiResponseItem>>, response: Response<List<RekomendasiResponseItem>>) {
                if (response.isSuccessful) {
                    val rekomendasiList = response.body()?.filterNotNull() ?: emptyList()
                    Log.d("RekomendasiData", "Data Rekomendasi diterima: $rekomendasiList")
                    val latestRekomendasiPerMonth = processRekomendasiData(rekomendasiList)
                    Log.d("ProcessedData", "Data Rekomendasi yang diproses: $latestRekomendasiPerMonth")
                    displayLatestRekomendasi(latestRekomendasiPerMonth, selectedMonthYear)
                } else {
                    Toast.makeText(requireContext(), "Gagal memuat data rekomendasi", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<RekomendasiResponseItem>>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }






    private fun processRekomendasiData(rekomendasiList: List<RekomendasiResponseItem>): Map<String, RekomendasiResponseItem> {
        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale("id", "ID"))
        val outputDateFormat = SimpleDateFormat("yyyy-MM", Locale("id", "ID"))

        return rekomendasiList
            .filter { it.tanggal != null }
            .groupBy { item ->
                try {
                    val date = inputDateFormat.parse(item.tanggal!!)
                    outputDateFormat.format(date)
                } catch (e: Exception) {
                    null
                }
            }
            .filterKeys { it != null }
            .mapKeys { it.key ?: "" }
            .mapValues { (_, group) ->
                group.maxByOrNull { item ->
                    val tanggal = item.tanggal
                    if (tanggal != null) {
                        try {
                            inputDateFormat.parse(tanggal)?.time ?: 0L
                        } catch (e: Exception) {
                            0L
                        }
                    } else {
                        0L
                    }
                }!!
            }
    }




    private fun displayLatestRekomendasi(latestRekomendasiPerMonth: Map<String, RekomendasiResponseItem>, selectedMonthYear: String) {
        Log.d("SelectedMonthYear", "Selected month and year: $selectedMonthYear")

        val latestRekomendasi = latestRekomendasiPerMonth[selectedMonthYear]
        Log.d("LatestRekomendasi", "Latest rekomendasi for the selected month and year: $latestRekomendasi")

        if (latestRekomendasi != null) {
            val nominal = latestRekomendasi.nominalRekomendasi?.toDoubleOrNull() ?: 0.0
            RekomendasiTextView.text = formatCurrency(nominal)
        } else {
            RekomendasiTextView.text = "Rp 0"
        }
    }

    private fun generateRekomendasi() {
        val saldo = saldoTextView.text
            .toString()
            .replace("Rp", "")
            .replace(".", "")
            .replace(",00", "")
            .trim()
            .toIntOrNull() ?: 0

        val target = targetTextView.text
            .toString()
            .replace("Rp", "")
            .replace(".", "")
            .replace(",00", "")
            .trim()
            .toIntOrNull() ?: 0


        val postRekomendasi = PostRekomendasiResponse(
            saldo = saldo,
            target = target
        )

        val apiService = RekomendasiApiConfig.getApiService(requireContext())
        val call = apiService.postRekomendasi(postRekomendasi)

        call.enqueue(object : Callback<PostRekomendasiResponse> {
            override fun onResponse(call: Call<PostRekomendasiResponse>, response: Response<PostRekomendasiResponse>) {
                if (response.isSuccessful) {
                    Log.d("KirimRekomendasi", "Saldo: $saldo, Target: $target")

                    Toast.makeText(requireContext(), "Rekomendasi berhasil dikirim!", Toast.LENGTH_SHORT).show()
                    loadRekomendasiData()
                } else {
                    Toast.makeText(requireContext(), "Gagal mengirim rekomendasi: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PostRekomendasiResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }








}
