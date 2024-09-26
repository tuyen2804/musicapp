package com.hlt.music

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.hlt.music.adapter.MusicAdapter
import com.hlt.music.databinding.FragmentHomeBinding
import com.hlt.music.viewmodel.MusicViewModel

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val musicViewModel: MusicViewModel by viewModels()
    private lateinit var musicAdapter: MusicAdapter

    // Biến quản lý yêu cầu quyền
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Khi quyền được cấp, tải danh sách nhạc
            musicViewModel.loadMusicFiles()
        } else {
            // Nếu quyền bị từ chối, thông báo cho người dùng
            Log.d("HomeFragment", "Quyền bị từ chối.")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Thiết lập RecyclerView và Adapter
        musicAdapter = MusicAdapter(emptyList()) { musicFile ->
            val musicUri = musicFile.data
            Log.d("HomeFragment", "Playing music URI: $musicUri")

            val fragment = PlayMusicFragment().apply {
                arguments = Bundle().apply {
                    putString("musicUri", musicUri) // Chuyển bài nhạc
                }
            }
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.viewFragment, fragment)
                .addToBackStack(null)
                .commit()
        }

        binding.listMusic.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = musicAdapter
        }

        // Quan sát dữ liệu từ ViewModel và cập nhật UI khi có nhạc
        musicViewModel.musicFiles.observe(viewLifecycleOwner) { musicFiles ->
            if (musicFiles.isNotEmpty()) {
                musicAdapter.updateMusicList(musicFiles)
            } else {
                Log.d("HomeFragment", "Không tìm thấy nhạc.")
            }
        }

        // Kiểm tra quyền và tải nhạc nếu có quyền
        checkPermissionAndLoadMusic()
    }

    // Kiểm tra và yêu cầu quyền READ_MEDIA_AUDIO hoặc READ_EXTERNAL_STORAGE dựa trên phiên bản Android
    private fun checkPermissionAndLoadMusic() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_MEDIA_AUDIO
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Nếu quyền đã được cấp, load danh sách nhạc
                musicViewModel.loadMusicFiles()
            } else {
                // Yêu cầu quyền truy cập nhạc
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_AUDIO)
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Nếu quyền đã được cấp, load danh sách nhạc
                musicViewModel.loadMusicFiles()
            } else {
                // Yêu cầu quyền truy cập bộ nhớ ngoài
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }
}
