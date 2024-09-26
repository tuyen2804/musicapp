package com.hlt.music.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.hlt.music.HomeActivity
import com.hlt.music.R

class MusicService : Service() {

    private val binder = MusicBinder()
    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying: Boolean = false

    inner class MusicBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val musicUri = intent?.getStringExtra("musicUri")
        Log.d("MusicService", "Received URI: $musicUri")

        if (musicUri != null) {
            playMusic(musicUri)
        }

        // Hiển thị thông báo foreground để duy trì service
        startForeground(1, createNotification())

        return START_STICKY // Đảm bảo service tiếp tục chạy
    }

    private fun createNotification(): Notification {
        val notificationChannelId = "MUSIC_SERVICE_CHANNEL"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                notificationChannelId,
                "Music Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val pendingIntent: PendingIntent = Intent(this, HomeActivity::class.java).let { notificationIntent ->
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
        }

        return NotificationCompat.Builder(this, notificationChannelId)
            .setContentTitle("Playing Music")
            .setContentText("Your music is playing")
            .setSmallIcon(R.drawable.ic_startms)
            .setContentIntent(pendingIntent)
            .build()
    }

    fun playMusic(musicUri: String) {
        try {
            if (mediaPlayer == null) {
                Log.d("MusicService", "Playing new song: $musicUri")
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(musicUri)
                    prepare()
                    start()
                }
                isPlaying = true
            } else {
                mediaPlayer?.start() // Nếu đã có MediaPlayer, tiếp tục phát nhạc
                Log.d("MusicService", "Resuming playback")
            }
        } catch (e: Exception) {
            Log.e("MusicService", "Error playing music", e)
        }
    }

    fun stopMusic() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        isPlaying = false
    }

    override fun onDestroy() {
        super.onDestroy()
        stopMusic()
    }
}
