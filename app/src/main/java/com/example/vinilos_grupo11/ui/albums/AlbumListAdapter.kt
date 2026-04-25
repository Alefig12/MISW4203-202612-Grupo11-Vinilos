package com.example.vinilos_grupo11.ui.albums

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.vinilos_grupo11.R
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
        val album = albums[position]
        holder.binding.tvAlbumName.text = album.name
        holder.binding.tvAlbumArtist.text = album.genre // Usando género como placeholder de artista

        Glide.with(holder.itemView.context)
            .load(album.cover)
            .transition(DrawableTransitionOptions.withCrossFade())
            .placeholder(R.drawable.ic_launcher_background) // Placeholder temporal
            .error(R.drawable.ic_launcher_background)
            .into(holder.binding.ivAlbumCover)
    }

    override fun getItemCount(): Int = albums.size
    class AlbumViewHolder(val binding: ItemAlbumBinding): RecyclerView.ViewHolder(binding.root)
}