package com.example.budgy

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.budgy.databinding.ActivityMainBinding
import com.example.budgy.ui.home.HomeFragment
import com.example.budgy.ui.profile.ProfileFragment
import com.example.budgy.ui.stat.PengeluaranFragment
import com.example.budgy.ui.transaksi.TransaksiPengeluaranFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mengakses elemen dengan binding
        val bottomNavigationView = binding.bottomNavMenu
        val fabAdd = binding.fabAdd

        supportFragmentManager.addOnBackStackChangedListener {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
            if (currentFragment is HomeFragment) {
                binding.fabAdd.show()
            } else {
                binding.fabAdd.hide()
            }
        }

        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
            bottomNavigationView.selectedItemId = R.id.nav_home
            fabAdd.show()
        }

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_stat -> {
                    loadFragment(PengeluaranFragment())
                    fabAdd.hide()
                }
                R.id.nav_home -> {
                    loadFragment(HomeFragment())
                    fabAdd.show()
                }
                R.id.nav_profile -> {
                    loadFragment(ProfileFragment())
                    fabAdd.hide()
                }
            }
            true
        }

        fabAdd.setOnClickListener {
            // Panggil fragment form transaksi
            loadFragment(TransaksiPengeluaranFragment())
            // Sembunyikan FAB saat berada di halaman transaksi
            fabAdd.hide()
        }
    }


    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null) // Tambahkan ke backstack untuk navigasi balik
        transaction.commit()
    }
}
