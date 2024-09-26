package com.hlt.music.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hlt.music.R
import com.hlt.music.model.Music

class MusicAdapter(
    private var musicList: List<Music>,
    private val onPlayClick: (Music) -> Unit
) : RecyclerView.Adapter<MusicAdapter.MusicViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_music, parent, false)
        return MusicViewHolder(view)
    }

    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        val musicFile = musicList[position]
        holder.title.text = musicFile.title

        // Khi nhấn vào biểu tượng phát nhạc
        holder.playIcon.setOnClickListener {
            onPlayClick(musicFile)
        }
    }

    override fun getItemCount(): Int {
        return musicList.size
    }

    fun updateMusicList(newMusicList: List<Music>) {
        musicList = newMusicList
        notifyDataSetChanged()
    }

    class MusicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.txtNameMusic)
        val playIcon: ImageView = itemView.findViewById(R.id.btnPlayMusic)
    }
}
