package com.cinevault.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.cinevault.R
import com.cinevault.data.local.MovieEntity
import com.cinevault.data.remote.model.MovieDetail
import com.cinevault.databinding.FragmentDetailsBinding
import com.cinevault.utils.UiState
import com.cinevault.utils.toast
import com.cinevault.viewmodel.MovieViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class DetailsFragment : Fragment() {
    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MovieViewModel by viewModels()
    private var currentDetail: MovieDetail? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentDetailsBinding.inflate(inflater, container, false).also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val movieId = arguments?.getInt("movieId") ?: return
        viewModel.loadMovieDetail(movieId)

        viewModel.movieDetail.observe(viewLifecycleOwner) { state ->
            binding.progressBar.isVisible = state is UiState.Loading
            binding.scrollView.isVisible = state is UiState.Success
            when (state) {
                is UiState.Success -> bindDetail(state.data)
                is UiState.Error -> toast(state.message)
                else -> {}
            }
        }

        viewModel.actionState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Success -> toast(state.data)
                is UiState.Error -> toast(state.message)
                else -> {}
            }
        }

        binding.btnAddWatched.setOnClickListener { addToList(MovieEntity.LIST_WATCHED) }
        binding.btnAddWatchlist.setOnClickListener { addToList(MovieEntity.LIST_WATCHLIST) }
        binding.btnAddPrivate.setOnClickListener { addToList(MovieEntity.LIST_PRIVATE) }
    }

    private fun bindDetail(movie: MovieDetail) {
        currentDetail = movie
        binding.tvTitle.text = movie.title
        binding.tvYear.text = movie.releaseYear
        binding.tvRating.text = String.format("%.1f", movie.voteAverage)
        binding.tvGenres.text = movie.genresText
        binding.tvOverview.text = movie.overview
        movie.runtime?.let { binding.tvRuntime.text = getString(R.string.runtime_minutes, it) }

        Glide.with(this).load(movie.backdropUrl).into(binding.ivBackdrop)
        Glide.with(this).load(movie.posterUrl).into(binding.ivPoster)
        binding.ivPoster.contentDescription = getString(R.string.poster_of, movie.title)
        binding.ivBackdrop.contentDescription = getString(R.string.backdrop_of, movie.title)
    }

    private fun addToList(listType: String) {
        val movie = currentDetail ?: return
        val entity = MovieEntity(
            tmdbId = movie.id.toString(),
            title = movie.title,
            overview = movie.overview,
            posterPath = movie.posterPath ?: "",
            releaseYear = movie.releaseYear,
            voteAverage = movie.voteAverage,
            listType = listType
        )
        viewModel.addToList(entity)
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
