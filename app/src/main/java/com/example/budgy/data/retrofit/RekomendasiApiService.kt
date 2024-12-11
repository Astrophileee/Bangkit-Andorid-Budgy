package com.example.budgy.data.retrofit


import com.example.budgy.data.response.PostRekomendasiResponse
import com.example.budgy.data.response.RekomendasiResponseItem
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface RekomendasiApiService {

    @GET("rekomendasi")
    fun getRekomendasi(): Call<List<RekomendasiResponseItem>>

    @POST("generate_rekomendasi")
    fun postRekomendasi(
        @Body PostRekomendasiResponse: PostRekomendasiResponse
    ): Call<PostRekomendasiResponse>

}