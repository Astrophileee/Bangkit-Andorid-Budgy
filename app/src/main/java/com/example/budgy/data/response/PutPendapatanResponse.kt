package com.example.budgy.data.response

import com.google.gson.annotations.SerializedName

data class PutPendapatanResponse(

    @field:SerializedName("data")
    val data: PutDataPengeluaran? = null,

    @field:SerializedName("success")
    val success: Boolean? = null
)

data class PutDataPendapatan(

    @field:SerializedName("kategoriId")
    val kategoriId: Int? = null,

    @field:SerializedName("nominal")
    val nominal: Int? = null,

    @field:SerializedName("user_id")
    val userId: Int? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("tanggal")
    val tanggal: String? = null
)