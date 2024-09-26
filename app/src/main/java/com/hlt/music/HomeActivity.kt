package com.hlt.music

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.hlt.music.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Khởi tạo fragment đầu tiên là FragmentHome
        replaceFragment(HomeFragment())

        // Lắng nghe sự kiện nhấn nút Home và Play
        binding.btnHome.setOnClickListener {
            replaceFragment(HomeFragment())
        }

        binding.btnPlay.setOnClickListener {
            replaceFragment(PlayMusicFragment())
        }
    }

    // Hàm để thay thế Fragment
    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.viewFragment, fragment)
        fragmentTransaction.commit()
    }
}
