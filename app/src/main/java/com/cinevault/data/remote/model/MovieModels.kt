package com.cinevault.data.remote.model
import com.google.gson.annotations.SerializedName

data class MovieSearchResponse(
    val page: Int,
    val results: List<MovieResult>,
    @SerializedName("total_results") val totalResults: Int,
    @SerializedName("total_pages") val totalPages: Int
)

data class MovieResult(
    val id: Int,
    val title: String,
    val overview: String,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("vote_average") val voteAverage: Double
) {
    val posterUrl: String get() = if (posterPath != null) "https://image.tmdb.org/t/p/w500$posterPath" else ""
    val releaseYear: String get() = releaseDate?.take(4) ?: "—"
}

data class MovieDetail(
    val id: Int,
    val title: String,
    val overview: String,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("vote_average") val voteAverage: Double,
    val runtime: Int?,
    val genres: List<Genre>
) {
    val posterUrl: String get() = if (posterPath != null) "https://image.tmdb.org/t/p/w500$posterPath" else ""
    val backdropUrl: String get() = if (backdropPath != null) "https://image.tmdb.org/t/p/w780$backdropPath" else ""
    val releaseYear: String get() = releaseDate?.take(4) ?: "—"
    val genresText: String get() = genres.joinToString(", ") { it.name }
}

data class Genre(val id: Int, val name: String)
data class CreditsResponse(val id: Int, val cast: List<CastMember>)
data class CastMember(val id: Int, val name: String, val character: String, @SerializedName("profile_path") val profilePath: String?)
