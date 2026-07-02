package com.cinevault.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.cinevault.R
import com.cinevault.data.local.MovieEntity
import com.cinevault.databinding.FragmentHomeBinding
import com.cinevault.utils.UiState
import com.cinevault.viewmodel.MovieViewModel
import com.google.firebase.auth.FirebaseAuth

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MovieViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentHomeBinding.inflate(inflater, container, false).also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userName = FirebaseAuth.getInstance().currentUser?.email?.substringBefore("@") ?: "Usuário"
        binding.tvWelcome.text = getString(R.string.welcome_user, userName)

        binding.cardWatched.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_listsFragment,
                Bundle().apply { putString("listType", MovieEntity.LIST_WATCHED) })
        }
        binding.cardWatchlist.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_listsFragment,
                Bundle().apply { putString("listType", MovieEntity.LIST_WATCHLIST) })
        }
        binding.cardPrivate.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_privateFragment)
        }
        binding.fabSearch.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }

        loadCounters()
    }

    override fun onResume() { super.onResume(); loadCounters() }

    private fun loadCounters() {
        viewModel.loadList(MovieEntity.LIST_WATCHED)
        viewModel.myList.observe(viewLifecycleOwner) { state ->
            if (state is UiState.Success) binding.tvWatchedCount.text = state.data.size.toString()
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
