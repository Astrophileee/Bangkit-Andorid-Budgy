package com.example.budgy.data.response

import com.google.gson.annotations.SerializedName

data class PostRekomendasiResponse(

	@field:SerializedName("saldo")
	val saldo: Int? = null,

	@field:SerializedName("target")
	val target: Int? = null
)
