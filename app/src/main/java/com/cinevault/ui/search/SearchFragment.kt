package com.cinevault.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cinevault.R
import com.cinevault.databinding.FragmentSearchBinding
import com.cinevault.utils.UiState
import com.cinevault.viewmodel.MovieViewModel

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MovieViewModel by viewModels()
    private lateinit var adapter: SearchAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentSearchBinding.inflate(inflater, container, false).also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = SearchAdapter { movie ->
            findNavController().navigate(R.id.action_searchFragment_to_detailsFragment,
                Bundle().apply { putInt("movieId", movie.id) })
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        binding.etSearch.addTextChangedListener { viewModel.searchMovies(it.toString()) }

        viewModel.searchResults.observe(viewLifecycleOwner) { state ->
            binding.progressBar.isVisible = state is UiState.Loading
            binding.tvEmpty.isVisible = state is UiState.Success && state.data.isEmpty() && !binding.etSearch.text.isNullOrEmpty()
            when (state) {
                is UiState.Success -> adapter.submitList(state.data)
                is UiState.Error -> binding.tvEmpty.apply { isVisible = true; text = state.message }
                else -> {}
            }
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
