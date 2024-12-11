package com.example.budgy.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.budgy.R
import com.example.budgy.ui.auth.LoginActivity
import com.example.budgy.ui.auth.RegisterActivity
import com.example.budgy.ui.helper.PreferenceHelper


class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Menemukan tombol logout
        val btnLogout: Button = view.findViewById(R.id.btnLogout)

        // Menambahkan listener untuk tombol logout
        btnLogout.setOnClickListener {
            // Menghapus status login
            PreferenceHelper.saveIsLoggedIn(requireContext(), false)

            // Pindah ke aktivitas RegisterActivity (atau SplashActivity jika Anda lebih memilihnya)
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)

            // Menghentikan aktivitas saat ini (optional)
            activity?.finish()
        }

        return view
    }
}