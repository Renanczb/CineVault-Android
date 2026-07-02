package com.cinevault.ui.private

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cinevault.R
import com.cinevault.data.local.MovieEntity
import com.cinevault.databinding.FragmentPrivateBinding
import com.cinevault.ui.lists.ListAdapter
import com.cinevault.utils.UiState
import com.cinevault.utils.toast
import com.cinevault.viewmodel.MovieViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PrivateFragment : Fragment() {
    private var _binding: FragmentPrivateBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MovieViewModel by viewModels()
    private lateinit var adapter: ListAdapter
    private var authenticated = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentPrivateBinding.inflate(inflater, container, false).also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ListAdapter(
            onMovieClick = { movie ->
                findNavController().navigate(R.id.action_privateFragment_to_detailsFragment,
                    Bundle().apply { putInt("movieId", movie.tmdbId.toInt()) })
            },
            onRemove = { movie ->
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.remove_movie))
                    .setMessage(getString(R.string.confirm_remove, movie.title))
                    .setPositiveButton(getString(R.string.remove)) { _, _ ->
                        viewModel.removeFromList(movie.tmdbId, MovieEntity.LIST_PRIVATE)
                    }
                    .setNegativeButton(getString(R.string.cancel), null).show()
            },
            onMove = { movie -> viewModel.moveMovie(movie, MovieEntity.LIST_PRIVATE, MovieEntity.LIST_WATCHED) }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        viewModel.myList.observe(viewLifecycleOwner) { state ->
            binding.progressBar.isVisible = state is UiState.Loading
            binding.tvEmpty.isVisible = state is UiState.Success && state.data.isEmpty()
            if (state is UiState.Success) adapter.submitList(state.data)
        }

        viewModel.actionState.observe(viewLifecycleOwner) { state ->
            if (state is UiState.Success) { toast(state.data); viewModel.loadList(MovieEntity.LIST_PRIVATE) }
        }

        if (!authenticated) showBiometricPrompt() else unlockContent()
    }

    private fun showBiometricPrompt() {
        val biometricManager = BiometricManager.from(requireContext())
        if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) != BiometricManager.BIOMETRIC_SUCCESS) {
            toast(getString(R.string.biometric_not_available)); unlockContent(); return
        }

        val executor = ContextCompat.getMainExecutor(requireContext())
        val prompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                authenticated = true; unlockContent()
            }
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                toast(errString.toString()); findNavController().navigateUp()
            }
            override fun onAuthenticationFailed() { toast(getString(R.string.biometric_failed)) }
        })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.biometric_title))
            .setSubtitle(getString(R.string.biometric_subtitle))
            .setNegativeButtonText(getString(R.string.cancel))
            .build()

        prompt.authenticate(promptInfo)
    }

    private fun unlockContent() {
        binding.layoutLocked.isVisible = false
        binding.recyclerView.isVisible = true
        viewModel.loadList(MovieEntity.LIST_PRIVATE)
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
