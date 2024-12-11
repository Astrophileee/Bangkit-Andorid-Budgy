package com.example.budgy.data.response

import com.google.gson.annotations.SerializedName

data class PostPendapatanResponse(

	@field:SerializedName("data")
	val data: PostPendapatanData? = null,

	@field:SerializedName("success")
	val success: Boolean? = null
)

data class PostPendapatanData(

	@field:SerializedName("kategoriId")
	val kategoriId: Int? = null,

	@field:SerializedName("nominal")
	val nominal: String? = null,

	@field:SerializedName("tanggal")
	val tanggal: String? = null
)
