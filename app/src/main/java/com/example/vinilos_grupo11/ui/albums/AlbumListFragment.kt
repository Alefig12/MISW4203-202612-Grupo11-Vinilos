package com.example.vinilos_grupo11.ui.albums

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
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

        //1. Configurar el RecyclerView con su adapter
        adapter = AlbumListAdapter()
        binding.albumsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.albumsRecyclerView.adapter = adapter

        //2. Obtener el ViewModel via Factory
        val app = requireActivity().application as VinilosApplication
        val repo = AlbumListViewModel.testRepositoryFactory?.invoke(app) ?: app.albumRepository
        viewModel = ViewModelProvider(
            this,
            AlbumListViewModel.Factory(app, repo)
        ).get(AlbumListViewModel::class.java)

        // 3. Observar los datos (observer para que actualicen)
        viewModel.albums.observe(viewLifecycleOwner) { albums ->
            adapter.albums=albums
        }

        viewModel.eventNetworkError.observe(viewLifecycleOwner) { isError ->
            if (isError && !viewModel.isNetworkErrorShown.value!!) {
                Toast.makeText(context, "Error de conexión", Toast.LENGTH_LONG).show()
                viewModel.onNetworkErrorShown()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
