package com.example.vinilos_grupo11.ui.artists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vinilos_grupo11.databinding.FragmentArtistsBinding
import com.example.vinilos_grupo11.viewmodels.ArtistViewModel

class ArtistsFragment : Fragment() {

    private var _binding: FragmentArtistsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ArtistViewModel by viewModels {
        ArtistViewModel.factory(requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArtistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ArtistListAdapter()
        binding.rvArtists.adapter = adapter
        binding.rvArtists.layoutManager = LinearLayoutManager(requireContext())

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.hasError.observe(viewLifecycleOwner) { hasError ->
            binding.tvError.visibility = if (hasError) View.VISIBLE else View.GONE
            binding.rvArtists.visibility = if (hasError) View.GONE else View.VISIBLE
        }

        viewModel.artists.observe(viewLifecycleOwner) { artists ->
            adapter.submitList(artists)
        }

        viewModel.loadArtists()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
