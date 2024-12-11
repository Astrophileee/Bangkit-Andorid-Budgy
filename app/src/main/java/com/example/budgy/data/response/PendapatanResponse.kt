package com.example.budgy.data.response

import com.google.gson.annotations.SerializedName

data class PendapatanResponse(

	@field:SerializedName("data")
	val data: List<PendapatanItem?>? = null,

	@field:SerializedName("success")
	val success: Boolean? = null
)

data class PendapatanItem(

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
