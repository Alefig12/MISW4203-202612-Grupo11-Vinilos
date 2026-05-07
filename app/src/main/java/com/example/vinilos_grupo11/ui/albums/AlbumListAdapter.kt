package com.example.vinilos_grupo11.ui.albums

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.vinilos_grupo11.R
import com.example.vinilos_grupo11.databinding.ItemAlbumBinding
import com.example.vinilos_grupo11.models.Album

class AlbumListAdapter : RecyclerView.Adapter<AlbumListAdapter.AlbumViewHolder>() {

    var albums: List<Album> = emptyList()
        set(value) {
            field  = value
            notifyDataSetChanged()
        }

    var showArtistName: Boolean = true
        set(value) {
            field = value
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
        holder.binding.tvAlbumArtist.visibility = if (showArtistName) View.VISIBLE else View.GONE

        holder.binding.tvNoPhoto.visibility = View.GONE

        Glide.with(holder.itemView.context)
            .load(album.cover)
            .transition(DrawableTransitionOptions.withCrossFade())
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.binding.tvNoPhoto.visibility = View.VISIBLE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.binding.tvNoPhoto.visibility = View.GONE
                    return false
                }
            })
            .into(holder.binding.ivAlbumCover)
    }

    override fun getItemCount(): Int = albums.size
    class AlbumViewHolder(val binding: ItemAlbumBinding): RecyclerView.ViewHolder(binding.root)
}