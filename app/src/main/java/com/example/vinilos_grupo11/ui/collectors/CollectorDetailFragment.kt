package com.example.vinilos_grupo11.ui.collectors

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.vinilos_grupo11.R
import com.example.vinilos_grupo11.databinding.FragmentCollectorDetailBinding
import com.example.vinilos_grupo11.models.Collector
import com.example.vinilos_grupo11.ui.albums.AlbumListAdapter
import com.example.vinilos_grupo11.viewmodels.CollectorDetailViewModel

class CollectorDetailFragment : Fragment() {

    private var _binding: FragmentCollectorDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CollectorDetailViewModel by viewModels {
        CollectorDetailViewModel.factory(requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCollectorDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val collectorId = arguments?.getInt("collectorId") ?: -1

        val albumsAdapter = AlbumListAdapter().apply {
            showArtistName = false
        }
        binding.rvCollectorAlbums.adapter = albumsAdapter
        binding.rvCollectorAlbums.layoutManager = GridLayoutManager(requireContext(), 2)

        viewModel.collector.observe(viewLifecycleOwner) { collector ->
            collector?.let {
                renderCollector(it, albumsAdapter)
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.hasError.observe(viewLifecycleOwner) { hasError ->
            binding.contentGroup.visibility = if (hasError) View.GONE else View.VISIBLE
            binding.tvCollectorDetailError.visibility = if (hasError) View.VISIBLE else View.GONE
        }

        if (collectorId == -1) {
            binding.contentGroup.visibility = View.GONE
            binding.tvCollectorDetailError.visibility = View.VISIBLE
            return
        }

        viewModel.loadCollectorDetail(collectorId)
    }

    private fun renderCollector(collector: Collector, albumsAdapter: AlbumListAdapter) {
        binding.tvCollectorName.text = collector.name
        binding.tvCollectorEmailValue.text = collector.email
        binding.tvCollectorPhoneValue.text = collector.telephone

        if (collector.image.isBlank()) {
            binding.tvNoPhoto.visibility = View.VISIBLE
            binding.ivCollectorImage.setImageResource(R.drawable.placeholder_no_photo)
        } else {
            binding.tvNoPhoto.visibility = View.GONE
            Glide.with(this)
                .load(collector.image)
                .placeholder(R.drawable.placeholder_no_photo)
                .error(R.drawable.placeholder_no_photo)
                .transform(CenterCrop(), RoundedCorners(24))
                .into(binding.ivCollectorImage)
        }

        val albums = collector.favoriteAlbums
        albumsAdapter.albums = albums
        binding.tvAlbumsHeader.visibility = if (albums.isNotEmpty()) View.VISIBLE else View.GONE
        binding.rvCollectorAlbums.visibility = if (albums.isNotEmpty()) View.VISIBLE else View.GONE
        binding.tvNoAlbums.visibility = if (albums.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

