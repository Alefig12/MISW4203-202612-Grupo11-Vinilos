package com.example.vinilos_grupo11.ui.albums

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.vinilos_grupo11.databinding.ItemAlbumBinding
import com.example.vinilos_grupo11.models.Album

class AlbumListAdapter : RecyclerView.Adapter<AlbumListAdapter.AlbumViewHolder>() {

    var albums: List<Album> = emptyList()
        set(value) {
            field  = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val binding = ItemAlbumBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AlbumViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        val album=albums[position]
        holder.binding.tvAlbumName.text = album.name
        holder.binding.tvAlbumGenre.text = album.genre
        holder.binding.tvAlbumDescription.text = album.description
    }

    override fun getItemCount(): Int = albums.size
    class AlbumViewHolder(val binding: ItemAlbumBinding): RecyclerView.ViewHolder(binding.root)
}