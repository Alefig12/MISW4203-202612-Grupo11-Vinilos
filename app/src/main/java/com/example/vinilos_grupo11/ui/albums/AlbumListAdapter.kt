package com.example.vinilos_grupo11.ui.albums

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.vinilos_grupo11.R
import com.example.vinilos_grupo11.databinding.ItemAlbumBinding
import com.example.vinilos_grupo11.models.Album

class AlbumListAdapter : ListAdapter<Album, AlbumListAdapter.AlbumViewHolder>(DiffCallback()) {

    var showArtistName: Boolean = true
        set(value) {
            field = value
            notifyItemRangeChanged(0, itemCount)
        }

    var onAlbumClick: ((Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val binding = ItemAlbumBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AlbumViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        holder.bind(getItem(position), showArtistName, onAlbumClick)
    }

    class AlbumViewHolder(val binding: ItemAlbumBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(album: Album, showArtistName: Boolean, onAlbumClick: ((Int) -> Unit)?) {
            binding.tvAlbumName.text = album.name
            binding.tvAlbumArtist.text = album.genre
            binding.tvAlbumArtist.visibility = if (showArtistName) View.VISIBLE else View.GONE
            binding.tvNoPhoto.visibility = View.GONE

            binding.root.contentDescription = binding.root.context.getString(
                R.string.cd_album_item, album.name, album.genre
            )
            binding.root.setOnClickListener { onAlbumClick?.invoke(album.albumId) }

            Glide.with(binding.ivAlbumCover.context)
                .load(album.cover)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .transition(DrawableTransitionOptions.withCrossFade())
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.tvNoPhoto.visibility = View.VISIBLE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.tvNoPhoto.visibility = View.GONE
                        return false
                    }
                })
                .into(binding.ivAlbumCover)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Album>() {
        override fun areItemsTheSame(oldItem: Album, newItem: Album) =
            oldItem.albumId == newItem.albumId

        override fun areContentsTheSame(oldItem: Album, newItem: Album) =
            oldItem == newItem
    }
}
