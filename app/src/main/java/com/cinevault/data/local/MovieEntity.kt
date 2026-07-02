package com.cinevault.data.local

data class MovieEntity(
    val tmdbId: String = "",
    val title: String = "",
    val overview: String = "",
    val posterPath: String = "",
    val releaseYear: String = "",
    val voteAverage: Double = 0.0,
    val listType: String = "",
    val addedAt: Long = System.currentTimeMillis()
) {
    val posterUrl: String get() = if (posterPath.isNotEmpty()) "https://image.tmdb.org/t/p/w500$posterPath" else ""

    companion object {
        const val LIST_WATCHED = "watched"
        const val LIST_WATCHLIST = "watchlist"
        const val LIST_PRIVATE = "private"
    }
}
