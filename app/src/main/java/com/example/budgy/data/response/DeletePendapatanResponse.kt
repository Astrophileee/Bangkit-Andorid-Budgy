package com.example.budgy.data.response

import com.google.gson.annotations.SerializedName

data class DeletePendapatanResponse(
    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
)
