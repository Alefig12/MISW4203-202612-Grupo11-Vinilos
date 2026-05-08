package com.example.vinilos_grupo11.ui.collectors

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.vinilos_grupo11.R
import com.example.vinilos_grupo11.databinding.FragmentCollectorsBinding
import com.example.vinilos_grupo11.viewmodels.CollectorsViewModel

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

        setupHeader()

        val adapter = CollectorAdapter { collectorId ->
            val bundle = Bundle().apply {
                putInt("collectorId", collectorId)
            }
            findNavController().navigate(
                R.id.action_collectorListFragment_to_collectorDetailFragment,
                bundle
            )
        }
        binding.rvCollectors.adapter = adapter
        binding.rvCollectors.layoutManager = GridLayoutManager(requireContext(), 2)

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.hasError.observe(viewLifecycleOwner) { hasError ->
            binding.rvCollectors.visibility = if (hasError) View.GONE else View.VISIBLE
            binding.tvCollectorsError.visibility = if (hasError) View.VISIBLE else View.GONE
        }

        viewModel.collectors.observe(viewLifecycleOwner) { collectors ->
            adapter.submitList(collectors)
        }

        viewModel.loadCollectors()
    }

    private fun setupHeader() {
        val sharedPref = requireActivity().getSharedPreferences("VinilosPrefs", Context.MODE_PRIVATE)
        val userRole = sharedPref.getString("user_role", "Visitante") ?: "Visitante"
        binding.tvCollectorsHeader.text = getString(R.string.collectors_catalog_header, userRole)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
