package com.example.budgy.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.budgy.R
import java.text.NumberFormat
import java.util.*

data class PencatatanItem(
    val id: Int? = null,
    val nominal: String? = null, // Nominal item (null untuk header)
    val kategori: String? = null, // Kategori item (null untuk header)
    val type: String? = null, // Tipe (Pendapatan/Pengeluaran), null untuk header
    val tanggal: String // Tanggal item atau header
)

class PencatatanAdapter(private var listPencatatan: MutableList<PencatatanItem> ,
                        private val onItemClicked: (PencatatanItem) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_ITEM = 1
    }

    // ViewHolder untuk Header
    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
    }

    // ViewHolder untuk Item
    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvType: TextView = itemView.findViewById(R.id.tvType)
        val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
    }

    override fun getItemViewType(position: Int): Int {
        return if (listPencatatan[position].nominal == null) {
            VIEW_TYPE_HEADER
        } else {
            VIEW_TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_HEADER) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_header, parent, false)
            HeaderViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_pencatatan, parent, false)
            ItemViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val pencatatan = listPencatatan[position]

        if (holder is HeaderViewHolder) {
            // Bind data untuk Header
            holder.tvDate.text = pencatatan.tanggal
        } else if (holder is ItemViewHolder) {
            // Bind data untuk Item
            holder.tvType.text = pencatatan.type
            holder.tvCategory.text = pencatatan.kategori

            // Format nominal dengan "Rp"
            val formattedNominal = formatCurrency(pencatatan.nominal)
            holder.tvAmount.text = formattedNominal

            // Atur warna berdasarkan tipe (Pendapatan/Pengeluaran)
            val color = if (pencatatan.type == "Pendapatan") R.color.blue else R.color.red
            holder.tvAmount.setTextColor(holder.itemView.context.getColor(color))
            holder.itemView.setOnClickListener {
                onItemClicked(pencatatan)
            }
        }
    }

    override fun getItemCount(): Int = listPencatatan.size

    // Fungsi untuk memperbarui data di adapter
    fun updateData(newData: List<PencatatanItem>) {
        listPencatatan.clear()
        listPencatatan.addAll(newData)
        notifyDataSetChanged()
    }

    // Fungsi untuk memformat nominal dengan prefix "Rp"
    private fun formatCurrency(nominal: String?): String {
        val amount = nominal?.toDoubleOrNull() ?: 0.0 // Langsung konversi tanpa menghapus titik
        val localeID = Locale("id", "ID")
        val formatter = NumberFormat.getCurrencyInstance(localeID)
        formatter.maximumFractionDigits = 2 // Menampilkan dua angka desimal
        formatter.minimumFractionDigits = 2 // Menjaga dua angka desimal tetap ada
        return formatter.format(amount).replace("Rp", "Rp ").trim()
    }



}
