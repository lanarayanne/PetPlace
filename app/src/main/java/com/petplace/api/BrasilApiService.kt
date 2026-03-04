package com.petplace.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface BrasilApiService {
    @GET("api/cep/v2/{cep}")
    suspend fun getAddressByCep(@Path("cep") cep: String): BrasilApiResponse
}

object RetrofitClient {
    private const val BASE_URL = "https://brasilapi.com.br/"

    val brasilApi: BrasilApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BrasilApiService::class.java)
    }
}