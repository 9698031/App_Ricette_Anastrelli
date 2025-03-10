package com.example.app_ricette

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import retrofit2.http.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Add the MealsResponse data class that was missing
@Parcelize
data class MealsResponse(
    val meals: List<Meal>?
) : Parcelable

interface MealApiService {
    @GET("filter.php?a=Italian")
    suspend fun getItalianMeals(): MealsResponse

    @GET("random.php")
    suspend fun getRandomMeal(): MealsResponse

    @GET("lookup.php")
    suspend fun getMealDetails(@Query("i") id: String): MealsResponse
}

object RetrofitInstance {
    private const val BASE_URL = "https://www.themealdb.com/api/json/v1/1/"

    val api: MealApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MealApiService::class.java)
    }
}