package com.cinevault.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cinevault.data.local.MovieEntity
import com.cinevault.data.remote.model.MovieDetail
import com.cinevault.data.remote.model.MovieResult
import com.cinevault.repository.MovieRepository
import com.cinevault.utils.UiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MovieViewModel : ViewModel() {
    private val repo = MovieRepository()

    private val _searchResults = MutableLiveData<UiState<List<MovieResult>>>()
    val searchResults: LiveData<UiState<List<MovieResult>>> = _searchResults

    private val _movieDetail = MutableLiveData<UiState<MovieDetail>>()
    val movieDetail: LiveData<UiState<MovieDetail>> = _movieDetail

    private val _myList = MutableLiveData<UiState<List<MovieEntity>>>()
    val myList: LiveData<UiState<List<MovieEntity>>> = _myList

    private val _actionState = MutableLiveData<UiState<String>>()
    val actionState: LiveData<UiState<String>> = _actionState

    private var searchJob: Job? = null

    fun searchMovies(query: String) {
        if (query.isBlank()) { _searchResults.value = UiState.Success(emptyList()); return }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(400)
            _searchResults.value = UiState.Loading
            try {
                _searchResults.value = UiState.Success(repo.searchMovies(query))
            } catch (e: Exception) {
                _searchResults.value = UiState.Error(e.message ?: "Erro na busca")
            }
        }
    }

    fun loadMovieDetail(id: Int) {
        _movieDetail.value = UiState.Loading
        viewModelScope.launch {
            try {
                _movieDetail.value = UiState.Success(repo.getMovieDetail(id))
            } catch (e: Exception) {
                _movieDetail.value = UiState.Error(e.message ?: "Erro ao carregar filme")
            }
        }
    }

    fun loadList(listType: String) {
        _myList.value = UiState.Loading
        viewModelScope.launch {
            try {
                _myList.value = UiState.Success(repo.getMoviesByList(listType))
            } catch (e: Exception) {
                _myList.value = UiState.Error(e.message ?: "Erro ao carregar lista")
            }
        }
    }

    fun addToList(movie: MovieEntity) {
        viewModelScope.launch {
            try {
                repo.addMovieToList(movie)
                _actionState.value = UiState.Success("Filme adicionado!")
            } catch (e: Exception) {
                _actionState.value = UiState.Error(e.message ?: "Erro ao adicionar")
            }
        }
    }

    fun removeFromList(tmdbId: String, listType: String) {
        viewModelScope.launch {
            try {
                repo.removeMovie(tmdbId, listType)
                _actionState.value = UiState.Success("Filme removido!")
                loadList(listType)
            } catch (e: Exception) {
                _actionState.value = UiState.Error(e.message ?: "Erro ao remover")
            }
        }
    }

    fun moveMovie(movie: MovieEntity, fromList: String, toList: String) {
        viewModelScope.launch {
            try {
                repo.moveMovie(movie, fromList, toList)
                _actionState.value = UiState.Success("Filme movido!")
                loadList(fromList)
            } catch (e: Exception) {
                _actionState.value = UiState.Error(e.message ?: "Erro ao mover")
            }
        }
    }
}
