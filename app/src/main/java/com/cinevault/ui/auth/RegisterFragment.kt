package com.cinevault.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.cinevault.R
import com.cinevault.databinding.FragmentRegisterBinding
import com.cinevault.utils.UiState
import com.cinevault.utils.toast
import com.cinevault.viewmodel.AuthViewModel

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentRegisterBinding.inflate(inflater, container, false).also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val pass = binding.etPassword.text.toString()
            val confirm = binding.etConfirmPassword.text.toString()
            if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) { toast(getString(R.string.fill_all_fields)); return@setOnClickListener }
            if (pass != confirm) { toast(getString(R.string.passwords_not_match)); return@setOnClickListener }
            viewModel.register(email, pass, name)
        }

        binding.tvLogin.setOnClickListener { findNavController().navigateUp() }

        viewModel.registerState.observe(viewLifecycleOwner) { state ->
            binding.progressBar.isVisible = state is UiState.Loading
            binding.btnRegister.isEnabled = state !is UiState.Loading
            when (state) {
                is UiState.Success -> (activity as? AuthActivity)?.navigateToMain()
                is UiState.Error -> toast(state.message)
                else -> {}
            }
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
