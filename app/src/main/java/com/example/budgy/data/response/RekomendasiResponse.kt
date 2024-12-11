package com.example.budgy.data.response

import com.google.gson.annotations.SerializedName

data class RekomendasiResponse(

	@field:SerializedName("RekomendasiResponse")
	val rekomendasiResponse: List<RekomendasiResponseItem?>? = null
)

data class RekomendasiResponseItem(

	@field:SerializedName("nominal_rekomendasi")
	val nominalRekomendasi: String? = null,

	@field:SerializedName("tanggal")
	val tanggal: String? = null
)
