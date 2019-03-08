package com.alexeymerov.unsplashviewer.data.server

import com.alexeymerov.unsplashviewer.data.entity.ImageEntity
import com.alexeymerov.unsplashviewer.data.server.pojo.response.SearchResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

const val FIRST_HEADER = "Accept-Version: v1"
const val SECOND_HEADER = "Authorization: Client-ID aab8d551cc9ac10781a02e86e2d80ad5039a1c0f346e0c3b6967c4674cf81f26"

interface ApiService {

    @Headers(FIRST_HEADER, SECOND_HEADER)
    @GET("/photos")
    fun getAll(@Query("page") page: Int = 1,
               @Query("per_page") countPerPage: Int = 50,
               @Query("order_by") order: String = "popular"): Single<Set<ImageEntity>>

    @Headers(FIRST_HEADER, SECOND_HEADER)
    @GET("/search/photos")
    fun search(@Query("query") query: String,
               @Query("page") page: Int = 1,
               @Query("per_page") countPerPage: Int = 50,
               @Query("order_by") order: String = "popular"): Single<SearchResponse>

}