package com.example.budgy.data.retrofit

import com.example.budgy.data.response.DeletePendapatanResponse
import com.example.budgy.data.response.DeletePengeluaranResponse
import com.example.budgy.data.response.LoginResponse
import com.example.budgy.data.response.PendapatanResponse
import com.example.budgy.data.response.PengeluaranResponse
import com.example.budgy.data.response.PostPendapatanData
import com.example.budgy.data.response.PostPendapatanResponse
import com.example.budgy.data.response.PostPengeluaranData
import com.example.budgy.data.response.PostPengeluaranResponse
import com.example.budgy.data.response.PostTargetResponse
import com.example.budgy.data.response.PutDataPendapatan
import com.example.budgy.data.response.PutDataPengeluaran
import com.example.budgy.data.response.PutPendapatanResponse
import com.example.budgy.data.response.PutPengeluaranResponse
import com.example.budgy.data.response.RegisterResponse
import com.example.budgy.data.response.TargetResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    @POST("api/auth/register")
    fun registerUser(
        @Body userData: Map<String, String>
    ): Call<RegisterResponse>

    @POST("api/auth/login")
    fun loginUser(
        @Body userData: Map<String, String>
    ): Call<LoginResponse>

    @GET("api/pendapatan")
    fun getPendapatan(): Call<PendapatanResponse>

    @POST("api/pendapatan")
    fun postPendapatan(
        @Body postPendapatanData: PostPendapatanData
    ): Call<PostPendapatanResponse>

    @PUT("api/pendapatan/{id}")
    fun putPendapatan(
        @Path("id") id: Int,
        @Body PutPendapatanData: PutDataPendapatan
    ): Call<PutPendapatanResponse>

    @DELETE("api/pendapatan/{id}")
    fun deletePendapatan(
        @Path("id") id: Int
    ): Call<DeletePendapatanResponse>

    @GET("api/pengeluaran")
    fun getPengeluaran(): Call<PengeluaranResponse>

    @POST("api/pengeluaran")
    fun postPengeluaran(
        @Body postPengeluaranData: PostPengeluaranData
    ): Call<PostPengeluaranResponse>

    @PUT("api/pengeluaran/{id}")
    fun putPengeluaran(
        @Path("id") id: Int,
        @Body PutPengeluaranData: PutDataPengeluaran
    ): Call<PutPengeluaranResponse>

    @DELETE("api/pengeluaran/{id}")
    fun deletePengeluaran(
        @Path("id") id: Int
    ): Call<DeletePengeluaranResponse>

    @GET("api/target")
    fun getTarget(): Call<TargetResponse>

    @POST("api/target")
    fun postTarget(
        @Body PostTargetResponse: PostTargetResponse
    ): Call<PostTargetResponse>

}