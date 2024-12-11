package com.example.budgy.data.response

import com.google.gson.annotations.SerializedName

data class PostTargetResponse(

	@field:SerializedName("nominal")
	val nominal: Int? = null,

	@field:SerializedName("tanggal")
	val tanggal: String? = null
)
