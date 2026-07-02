package com.cinevault.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.cinevault.databinding.FragmentSettingsBinding
import com.cinevault.ui.auth.AuthActivity
import com.cinevault.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import java.util.Locale

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentSettingsBinding.inflate(inflater, container, false).also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvEmail.text = FirebaseAuth.getInstance().currentUser?.email ?: ""

        binding.switchLanguage.isChecked = Locale.getDefault().language == "en"
        binding.switchLanguage.setOnCheckedChangeListener { _, isChecked ->
            val locale = if (isChecked) Locale("en") else Locale("pt", "BR")
            Locale.setDefault(locale)
            val config = requireContext().resources.configuration
            config.setLocale(locale)
            requireContext().createConfigurationContext(config)
            activity?.recreate()
        }

        binding.btnLogout.setOnClickListener {
            authViewModel.logout()
            startActivity(Intent(requireContext(), AuthActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
