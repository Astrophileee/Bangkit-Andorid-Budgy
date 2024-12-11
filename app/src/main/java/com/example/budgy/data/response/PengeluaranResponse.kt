package com.example.budgy.data.response

import com.google.gson.annotations.SerializedName

data class PengeluaranResponse(

	@field:SerializedName("data")
	val data: List<PengeluaranItem?>? = null,

	@field:SerializedName("success")
	val success: Boolean? = null
)

data class PengeluaranItem(

	@field:SerializedName("nominal")
	val nominal: String? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("kategori")
	val kategori: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("tanggal")
	val tanggal: String? = null
)
