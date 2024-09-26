package com.hlt.music.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hlt.music.model.Music
import com.hlt.music.respository.MusicRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MusicViewModel(application: Application) : AndroidViewModel(application) {

    private val _musicFiles = MutableLiveData<List<Music>>()
    val musicFiles: LiveData<List<Music>> get() = _musicFiles

    private val musicRepository: MusicRepository = MusicRepository(application.applicationContext)

    // Hàm load nhạc chạy trên background thread (Dispatchers.IO)
    fun loadMusicFiles() {
        viewModelScope.launch(Dispatchers.IO) {
            val musicList = musicRepository.getMusicFiles()
            _musicFiles.postValue(musicList)
        }
    }
}
