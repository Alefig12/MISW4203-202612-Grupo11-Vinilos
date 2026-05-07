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
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.vinilos_grupo11.R
import com.example.vinilos_grupo11.ui.albums.AlbumListAdapter
import androidx.recyclerview.widget.GridLayoutManager
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

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
        
        val adapter = AlbumListAdapter().apply {
            showArtistName = false
        }
        binding.rvArtistAlbums.adapter = adapter
        binding.rvArtistAlbums.layoutManager = GridLayoutManager(requireContext(), 2)

        viewModel.artist.observe(viewLifecycleOwner) { artist ->
            artist?.let {
                Log.d("ArtistDetailFragment", "Datos del artista recibidos: ${it.name}")
                binding.tvArtistName.text = it.name
                
                // Formatear la fecha de nacimiento
                val formattedDate = try {
                    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                    inputFormat.timeZone = TimeZone.getTimeZone("UTC")
                    val date = inputFormat.parse(it.birthDate)
                    
                    val outputFormat = if (Locale.getDefault().language == "es") {
                        SimpleDateFormat("MMMM d 'de' yyyy", Locale.getDefault())
                    } else {
                        SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
                    }
                    
                    date?.let { d -> 
                        outputFormat.format(d).replaceFirstChar { char -> char.uppercase() }
                    } ?: it.birthDate
                } catch (e: Exception) {
                    it.birthDate
                }

                // Formatear Fecha | Descripción (Salmon y Bold en el XML)
                val detailText = "$formattedDate | ${it.description}"
                binding.tvArtistDetail.text = detailText

                // Cargar imagen con Glide
                Glide.with(this)
                    .load(it.image)
                    .placeholder(R.drawable.placeholder_no_photo)
                    .error(R.drawable.placeholder_no_photo)
                    .transform(CenterCrop(), RoundedCorners(24))
                    .into(binding.ivArtistImage)

                // Lista de álbumes con el mismo estilo que el catálogo
                if (it.albums.isNotEmpty()) {
                    adapter.albums = it.albums
                    binding.tvAlbumsHeader.visibility = View.VISIBLE
                    binding.rvArtistAlbums.visibility = View.VISIBLE
                } else {
                    binding.tvAlbumsHeader.visibility = View.GONE
                    binding.rvArtistAlbums.visibility = View.GONE
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
