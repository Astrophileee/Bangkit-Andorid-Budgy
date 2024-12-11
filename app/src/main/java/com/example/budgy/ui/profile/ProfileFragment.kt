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
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val btnLogout: Button = view.findViewById(R.id.btnLogout)

        btnLogout.setOnClickListener {
            PreferenceHelper.saveIsLoggedIn(requireContext(), false)

            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)

            activity?.finish()
        }

        return view
    }
}