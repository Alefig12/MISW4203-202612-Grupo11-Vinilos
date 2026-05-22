package com.example.vinilos_grupo11.ui.albums

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.vinilos_grupo11.R
import com.example.vinilos_grupo11.application.VinilosApplication
import com.example.vinilos_grupo11.databinding.FragmentAddTrackBinding
import com.example.vinilos_grupo11.models.Track

class AddTrackFragment : Fragment() {

    private var _binding: FragmentAddTrackBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTrackBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val albumId = arguments?.getInt("albumId") ?: -1
        val albumName = arguments?.getString("albumName") ?: ""

        binding.etAlbumName.setText(albumName)

        binding.btnSaveTrack.setOnClickListener {
            if (validateForm()) {
                showConfirmSaveDialog(albumId)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleExit()
            }
        })
    }

    private fun showConfirmSaveDialog(albumId: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.title_confirm_save)
            .setMessage(R.string.msg_confirm_save)
            .setPositiveButton(R.string.btn_save_track) { _, _ ->
                saveTrack(albumId)
            }
            .setNegativeButton(R.string.btn_cancel, null)
            .show()
    }

    private fun hasChanges(): Boolean {
        return binding.etTrackName.text?.isNotEmpty() == true || 
               binding.etTrackDuration.text?.isNotEmpty() == true
    }

    private fun handleExit() {
        if (hasChanges()) {
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.title_confirm_exit)
                .setMessage(R.string.msg_confirm_exit)
                .setPositiveButton(R.string.btn_exit) { _, _ ->
                    findNavController().popBackStack()
                }
                .setNegativeButton(R.string.btn_cancel, null)
                .show()
        } else {
            findNavController().popBackStack()
        }
    }

    private fun validateForm(): Boolean {
        var isValid = true

        val name = binding.etTrackName.text.toString()
        if (name.isBlank()) {
            binding.tilTrackName.error = getString(R.string.error_field_required)
            isValid = false
        } else {
            binding.tilTrackName.error = null
        }

        val duration = binding.etTrackDuration.text.toString()
        val durationPattern = Regex("^([0-5]?[0-9]):([0-5][0-9])$")
        if (duration.isBlank()) {
            binding.tilTrackDuration.error = getString(R.string.error_field_required)
            isValid = false
        } else if (!durationPattern.matches(duration)) {
            binding.tilTrackDuration.error = getString(R.string.error_invalid_duration)
            isValid = false
        } else {
            binding.tilTrackDuration.error = null
        }

        return isValid
    }

    private fun saveTrack(albumId: Int) {
        val name = binding.etTrackName.text.toString()
        val duration = binding.etTrackDuration.text.toString()
        val track = Track(id = 0, name = name, duration = duration)

        binding.progressBar.visibility = View.VISIBLE
        binding.btnSaveTrack.isEnabled = false

        val app = requireActivity().application as VinilosApplication
        app.albumRepository.addTrackToAlbum(
            albumId,
            track,
            onSuccess = {
                Toast.makeText(context, R.string.msg_track_added_success, Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            },
            onError = {
                binding.progressBar.visibility = View.GONE
                binding.btnSaveTrack.isEnabled = true
                Toast.makeText(context, R.string.msg_track_added_error, Toast.LENGTH_SHORT).show()
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
