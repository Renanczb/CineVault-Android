package com.cinevault.ui.lists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cinevault.R
import com.cinevault.data.local.MovieEntity
import com.cinevault.databinding.FragmentListsBinding
import com.cinevault.utils.UiState
import com.cinevault.utils.toast
import com.cinevault.viewmodel.MovieViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ListsFragment : Fragment() {
    private var _binding: FragmentListsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MovieViewModel by viewModels()
    private lateinit var adapter: ListAdapter
    private var listType: String = MovieEntity.LIST_WATCHED

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentListsBinding.inflate(inflater, container, false).also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listType = arguments?.getString("listType") ?: MovieEntity.LIST_WATCHED

        binding.tvTitle.text = when (listType) {
            MovieEntity.LIST_WATCHED -> getString(R.string.list_watched)
            MovieEntity.LIST_WATCHLIST -> getString(R.string.list_watchlist)
            else -> getString(R.string.list_private)
        }

        adapter = ListAdapter(
            onMovieClick = { movie ->
                findNavController().navigate(R.id.action_listsFragment_to_detailsFragment,
                    Bundle().apply { putInt("movieId", movie.tmdbId.toInt()) })
            },
            onRemove = { movie -> confirmRemove(movie) },
            onMove = { movie -> showMoveDialog(movie) }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        viewModel.myList.observe(viewLifecycleOwner) { state ->
            binding.progressBar.isVisible = state is UiState.Loading
            binding.tvEmpty.isVisible = state is UiState.Success && state.data.isEmpty()
            when (state) {
                is UiState.Success -> adapter.submitList(state.data)
                is UiState.Error -> toast(state.message)
                else -> {}
            }
        }

        viewModel.actionState.observe(viewLifecycleOwner) { state ->
            if (state is UiState.Success) toast(state.data)
        }

        viewModel.loadList(listType)
    }

    private fun confirmRemove(movie: MovieEntity) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.remove_movie))
            .setMessage(getString(R.string.confirm_remove, movie.title))
            .setPositiveButton(getString(R.string.remove)) { _, _ -> viewModel.removeFromList(movie.tmdbId, listType) }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun showMoveDialog(movie: MovieEntity) {
        val options = when (listType) {
            MovieEntity.LIST_WATCHED -> arrayOf(getString(R.string.list_watchlist), getString(R.string.list_private))
            MovieEntity.LIST_WATCHLIST -> arrayOf(getString(R.string.list_watched), getString(R.string.list_private))
            else -> arrayOf(getString(R.string.list_watched), getString(R.string.list_watchlist))
        }
        val targets = when (listType) {
            MovieEntity.LIST_WATCHED -> arrayOf(MovieEntity.LIST_WATCHLIST, MovieEntity.LIST_PRIVATE)
            MovieEntity.LIST_WATCHLIST -> arrayOf(MovieEntity.LIST_WATCHED, MovieEntity.LIST_PRIVATE)
            else -> arrayOf(MovieEntity.LIST_WATCHED, MovieEntity.LIST_WATCHLIST)
        }
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.move_to))
            .setItems(options) { _, i -> viewModel.moveMovie(movie, listType, targets[i]) }
            .show()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
