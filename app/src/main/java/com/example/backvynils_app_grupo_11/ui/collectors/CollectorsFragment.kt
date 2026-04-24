package com.example.backvynils_app_grupo_11.ui.collectors

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.backvynils_app_grupo_11.databinding.FragmentCollectorsBinding
import com.example.backvynils_app_grupo_11.viewmodel.CollectorsViewModel

class CollectorsFragment : Fragment() {

    private var _binding: FragmentCollectorsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CollectorsViewModel by viewModels {
        CollectorsViewModel.factory(requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCollectorsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = CollectorAdapter()
        binding.rvCollectors.adapter = adapter
        binding.rvCollectors.layoutManager = LinearLayoutManager(requireContext())

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.hasError.observe(viewLifecycleOwner) { hasError ->
            binding.tvError.visibility = if (hasError) View.VISIBLE else View.GONE
            binding.rvCollectors.visibility = if (hasError) View.GONE else View.VISIBLE
        }

        viewModel.collectors.observe(viewLifecycleOwner) { collectors ->
            adapter.submitList(collectors)
        }

        viewModel.loadCollectors()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
