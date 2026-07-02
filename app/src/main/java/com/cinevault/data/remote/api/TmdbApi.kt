package com.cinevault.data.remote.api
import com.cinevault.data.remote.model.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApi {
    @GET("search/movie")
    suspend fun searchMovies(@Query("query") query: String, @Query("language") language: String = "pt-BR", @Query("page") page: Int = 1): MovieSearchResponse

    @GET("movie/{id}")
    suspend fun getMovieDetail(@Path("id") id: Int, @Query("language") language: String = "pt-BR"): MovieDetail

    @GET("movie/{id}/credits")
    suspend fun getMovieCredits(@Path("id") id: Int, @Query("language") language: String = "pt-BR"): CreditsResponse
}
