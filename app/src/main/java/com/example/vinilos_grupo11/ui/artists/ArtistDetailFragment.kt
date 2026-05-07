package com.example.vinilos_grupo11.ui.artists

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.vinilos_grupo11.databinding.FragmentArtistDetailBinding
import com.example.vinilos_grupo11.viewmodels.ArtistDetailViewModel

import com.bumptech.glide.Glide
import com.example.vinilos_grupo11.R

class ArtistDetailFragment : Fragment() {

    private var _binding: FragmentArtistDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ArtistDetailViewModel by viewModels {
        ArtistDetailViewModel.factory(requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArtistDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val artistId = arguments?.getInt("artistId") ?: -1
        
        viewModel.artist.observe(viewLifecycleOwner) { artist ->
            artist?.let {
                Log.d("ArtistDetailFragment", "Datos del artista recibidos: ${it.name}")
                binding.tvArtistName.text = it.name
                
                // Formatear Fecha | Descripción
                val detailText = "${it.birthDate} | ${it.description}"
                binding.tvArtistDetail.text = detailText

                // Cargar imagen con Glide
                Glide.with(this)
                    .load(it.image)
                    .placeholder(R.drawable.placeholder_no_photo)
                    .error(R.drawable.placeholder_no_photo)
                    .centerCrop()
                    .into(binding.ivArtistImage)

                // Lista de álbumes enumerados
                if (it.albums.isNotEmpty()) {
                    val albumsString = it.albums.mapIndexed { index, album ->
                        "${index + 1}. ${album.name}"
                    }.joinToString("\n")
                    binding.tvAlbumsList.text = albumsString
                    binding.tvAlbumsHeader.visibility = View.VISIBLE
                    binding.tvAlbumsList.visibility = View.VISIBLE
                } else {
                    binding.tvAlbumsHeader.visibility = View.GONE
                    binding.tvAlbumsList.visibility = View.GONE
                }
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.loadArtistDetail(artistId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
