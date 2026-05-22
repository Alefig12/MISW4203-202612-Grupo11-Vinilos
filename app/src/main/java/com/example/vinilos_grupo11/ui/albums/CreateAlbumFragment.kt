package com.example.vinilos_grupo11.ui.albums

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.vinilos_grupo11.R
import com.example.vinilos_grupo11.databinding.FragmentCreateAlbumBinding
import com.example.vinilos_grupo11.models.AlbumCreateRequest
import com.example.vinilos_grupo11.viewmodels.CreateAlbumViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.Calendar

class CreateAlbumFragment : Fragment() {

    private var _binding: FragmentCreateAlbumBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CreateAlbumViewModel by viewModels {
        CreateAlbumViewModel.factory(requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateAlbumBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDropdowns()
        setupDatePicker()
        setupObservers()
        setupBackConfirmation()

        binding.btnSave.setOnClickListener { submitForm() }
    }

    private fun setupDropdowns() {
        val genres = resources.getStringArray(R.array.album_genres)
        val labels = resources.getStringArray(R.array.album_record_labels)

        binding.acvGenre.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, genres)
        )
        binding.acvRecordLabel.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, labels)
        )
    }

    private fun setupDatePicker() {
        binding.etDate.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    binding.etDate.setText(String.format("%04d-%02d-%02d", year, month + 1, day))
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).apply { datePicker.maxDate = System.currentTimeMillis() }.show()
        }
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnSave.isEnabled = !isLoading
        }

        viewModel.saveSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(context, getString(R.string.create_album_success), Toast.LENGTH_SHORT).show()
                findNavController().previousBackStackEntry
                    ?.savedStateHandle?.set("album_created", true)
                findNavController().popBackStack()
            }
        }

        viewModel.saveError.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                Toast.makeText(context, getString(R.string.create_album_error), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupBackConfirmation() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // Cuando la Activity se cierra programáticamente (finish() / ActivityScenario.close())
                    // el NavController re-dispara onBackPressed como parte del cleanup de la pila.
                    // En ese caso no mostramos el diálogo para no bloquear la destrucción.
                    if (requireActivity().isFinishing) {
                        isEnabled = false
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                        return
                    }
                    if (hasUnsavedChanges()) {
                        MaterialAlertDialogBuilder(requireContext())
                            .setTitle(R.string.create_album_unsaved_title)
                            .setMessage(R.string.create_album_unsaved_message)
                            .setPositiveButton(R.string.btn_exit) { _, _ ->
                                isEnabled = false
                                requireActivity().onBackPressedDispatcher.onBackPressed()
                            }
                            .setNegativeButton(R.string.btn_cancel, null)
                            .show()
                    } else {
                        isEnabled = false
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    }
                }
            }
        )
    }

    private fun hasUnsavedChanges(): Boolean =
        binding.etName.text?.isNotEmpty() == true ||
        binding.etCover.text?.isNotEmpty() == true ||
        binding.etDate.text?.isNotEmpty() == true ||
        binding.etDescription.text?.isNotEmpty() == true ||
        binding.acvGenre.text?.isNotEmpty() == true ||
        binding.acvRecordLabel.text?.isNotEmpty() == true

    private fun submitForm() {
        clearErrors()

        val name = binding.etName.text?.toString().orEmpty().trim()
        val cover = binding.etCover.text?.toString().orEmpty().trim()
        val date = binding.etDate.text?.toString().orEmpty().trim()
        val description = binding.etDescription.text?.toString().orEmpty().trim()
        val genre = binding.acvGenre.text?.toString().orEmpty().trim()
        val recordLabel = binding.acvRecordLabel.text?.toString().orEmpty().trim()

        if (!validateFields(name, cover, date, description, genre, recordLabel)) return

        val isoDate = "${date}T00:00:00.000Z"
        viewModel.saveAlbum(
            AlbumCreateRequest(
                name = name,
                cover = cover,
                releaseDate = isoDate,
                description = description,
                genre = genre,
                recordLabel = recordLabel
            )
        )
    }

    private fun validateFields(
        name: String, cover: String, date: String,
        description: String, genre: String, recordLabel: String
    ): Boolean {
        var valid = true

        if (name.isEmpty()) {
            binding.tilName.error = getString(R.string.create_album_validation_empty)
            valid = false
        }
        if (cover.isEmpty()) {
            binding.tilCover.error = getString(R.string.create_album_validation_empty)
            valid = false
        } else if (!cover.startsWith("http://") && !cover.startsWith("https://")) {
            binding.tilCover.error = getString(R.string.create_album_validation_url)
            valid = false
        }
        if (date.isEmpty() || !date.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) {
            binding.tilDate.error = getString(R.string.create_album_validation_date)
            valid = false
        } else {
            val parts = date.split("-")
            val albumCal = Calendar.getInstance().apply {
                set(parts[0].toInt(), parts[1].toInt() - 1, parts[2].toInt(), 0, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
            }
            if (albumCal.after(today)) {
                binding.tilDate.error = getString(R.string.create_album_validation_date_future)
                valid = false
            }
        }
        if (description.isEmpty()) {
            binding.tilDescription.error = getString(R.string.create_album_validation_empty)
            valid = false
        }
        if (genre.isEmpty()) {
            binding.tilGenre.error = getString(R.string.create_album_validation_empty)
            valid = false
        }
        if (recordLabel.isEmpty()) {
            binding.tilRecordLabel.error = getString(R.string.create_album_validation_empty)
            valid = false
        }
        return valid
    }

    private fun clearErrors() {
        binding.tilName.error = null
        binding.tilCover.error = null
        binding.tilDate.error = null
        binding.tilDescription.error = null
        binding.tilGenre.error = null
        binding.tilRecordLabel.error = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
