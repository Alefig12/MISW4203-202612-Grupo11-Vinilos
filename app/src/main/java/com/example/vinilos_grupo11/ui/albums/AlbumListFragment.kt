package com.example.vinilos_grupo11.ui.albums

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.vinilos_grupo11.R
import com.example.vinilos_grupo11.application.VinilosApplication
import com.example.vinilos_grupo11.databinding.FragmentAlbumListBinding
import com.example.vinilos_grupo11.viewmodels.AlbumListViewModel

class AlbumListFragment: Fragment() {
    private var _binding: FragmentAlbumListBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AlbumListViewModel
    private lateinit var adapter: AlbumListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlbumListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupHeaderAndTopBar()

        adapter = AlbumListAdapter()
        binding.albumsRecyclerView.layoutManager = GridLayoutManager(context, 2)
        binding.albumsRecyclerView.adapter = adapter

        adapter.onAlbumClick = { albumId ->
            val bundle = bundleOf("albumId" to albumId)
            findNavController().navigate(
                R.id.action_albumListFragment_to_albumDetailFragment,
                bundle
            )
        }

        val app = requireActivity().application as VinilosApplication
        val repo = AlbumListViewModel.testRepositoryFactory?.invoke(app) ?: app.albumRepository
        viewModel = ViewModelProvider(
            this,
            AlbumListViewModel.Factory(app, repo)
        ).get(AlbumListViewModel::class.java)

        viewModel.albums.observe(viewLifecycleOwner) { albums ->
            adapter.albums = albums
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.loadingSpinner.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.eventNetworkError.observe(viewLifecycleOwner) { isError ->
            binding.albumsRecyclerView.visibility = if (isError) View.GONE else View.VISIBLE
            binding.tvAlbumsError.visibility = if (isError) View.VISIBLE else View.GONE

            if (isError && !viewModel.isNetworkErrorShown.value!!) {
                Toast.makeText(context, getString(R.string.error_connection), Toast.LENGTH_LONG).show()
                viewModel.onNetworkErrorShown()
            }
        }
    }

    private fun setupHeaderAndTopBar() {
        val sharedPref = requireActivity().getSharedPreferences("VinilosPrefs", Context.MODE_PRIVATE)
        val userRole = sharedPref.getString("user_role", "Visitante") ?: "Visitante"

        binding.tvCatalogHeader.text = getString(R.string.catalog_header, userRole)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
