package com.cinevault.repository

import com.cinevault.data.local.MovieEntity
import com.cinevault.data.remote.api.RetrofitInstance
import com.cinevault.data.remote.model.MovieDetail
import com.cinevault.data.remote.model.MovieResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class MovieRepository {
    private val api = RetrofitInstance.api
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val uid get() = auth.currentUser?.uid ?: throw IllegalStateException("Usuário não autenticado")

    // ── TMDB ──
    suspend fun searchMovies(query: String): List<MovieResult> = api.searchMovies(query).results

    suspend fun getMovieDetail(id: Int): MovieDetail = api.getMovieDetail(id)

    // ── Firestore CRUD ──
    suspend fun addMovieToList(movie: MovieEntity) {
        db.collection("users").document(uid)
            .collection(movie.listType).document(movie.tmdbId)
            .set(movie).await()
    }

    suspend fun getMoviesByList(listType: String): List<MovieEntity> {
        val snap = db.collection("users").document(uid)
            .collection(listType).get().await()
        return snap.documents.mapNotNull { it.toObject(MovieEntity::class.java) }
    }

    suspend fun removeMovie(tmdbId: String, listType: String) {
        db.collection("users").document(uid)
            .collection(listType).document(tmdbId).delete().await()
    }

    suspend fun moveMovie(movie: MovieEntity, fromList: String, toList: String) {
        removeMovie(movie.tmdbId, fromList)
        addMovieToList(movie.copy(listType = toList))
    }

    suspend fun isMovieInList(tmdbId: String, listType: String): Boolean {
        val doc = db.collection("users").document(uid)
            .collection(listType).document(tmdbId).get().await()
        return doc.exists()
    }
}
