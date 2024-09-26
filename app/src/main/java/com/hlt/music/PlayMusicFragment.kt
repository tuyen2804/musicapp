package com.hlt.music

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hlt.music.databinding.FragmentPlayMusicBinding
import com.hlt.music.service.MusicService

class PlayMusicFragment : Fragment() {

    private lateinit var binding: FragmentPlayMusicBinding
    private var musicService: MusicService? = null
    private var isBound = false
    private var musicUri: String? = null

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            val service = (binder as MusicService.MusicBinder).getService()
            musicService = service
            isBound = true

            musicUri?.let {
                Log.d("PlayMusicFragment", "Service connected, playing music: $it")
                musicService?.playMusic(it)
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlayMusicBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        musicUri = arguments?.getString("musicUri")

        if (musicUri == null) {
            Log.e("PlayMusicFragment", "Không có URI bài nhạc.")
            return
        }

        // Khởi động và bind tới MusicService
        val intent = Intent(requireContext(), MusicService::class.java).apply {
            putExtra("musicUri", musicUri)
        }
        requireActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        requireActivity().startService(intent) // Bắt đầu Service nếu chưa chạy

        // Dừng nhạc khi nhấn nút stop
        binding.btnPlayMusic.setOnClickListener {
            Log.d("PlayMusicFragment", "Stopping music")
            musicService?.stopMusic()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (isBound) {
            requireActivity().unbindService(serviceConnection)
            isBound = false
        }
    }
}
