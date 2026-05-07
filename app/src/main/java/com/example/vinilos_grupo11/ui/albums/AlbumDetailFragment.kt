package com.example.vinilos_grupo11.ui.albums

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.vinilos_grupo11.R
import com.example.vinilos_grupo11.application.VinilosApplication
import com.example.vinilos_grupo11.databinding.FragmentAlbumDetailBinding
import com.example.vinilos_grupo11.databinding.ItemTrackBinding
import com.example.vinilos_grupo11.models.AlbumDetail
import com.example.vinilos_grupo11.models.Track
import com.example.vinilos_grupo11.viewmodels.AlbumDetailViewModel

class AlbumDetailFragment : Fragment() {

    private var _binding: FragmentAlbumDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AlbumDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlbumDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val albumId = arguments?.getInt("albumId", -1) ?: -1
        if (albumId == -1) {
            binding.detailContent.visibility = View.GONE
            binding.tvDetailError.visibility = View.VISIBLE
            return
        }

        val app = requireActivity().application as VinilosApplication
        val repo = AlbumDetailViewModel.testRepositoryFactory?.invoke(app) ?: app.albumRepository
        viewModel = ViewModelProvider(
            this,
            AlbumDetailViewModel.Factory(app, repo, albumId)
        ).get(AlbumDetailViewModel::class.java)

        viewModel.albumDetail.observe(viewLifecycleOwner) { detail ->
            if (detail != null) renderAlbum(detail)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.detailLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.eventNetworkError.observe(viewLifecycleOwner) { isError ->
            binding.detailContent.visibility = if (isError) View.GONE else View.VISIBLE
            binding.tvDetailError.visibility = if (isError) View.VISIBLE else View.GONE

            if (isError && viewModel.isNetworkErrorShown.value == false) {
                Toast.makeText(
                    context,
                    getString(R.string.album_detail_error),
                    Toast.LENGTH_LONG
                ).show()
                viewModel.onNetworkErrorShown()
            }
        }
    }

    private fun renderAlbum(detail: AlbumDetail) {
        Glide.with(this)
            .load(detail.cover)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.ivAlbumCover)

        binding.tvAlbumYear.text = if (detail.releaseDate.length >= 4) {
            detail.releaseDate.take(4)
        } else {
            ""
        }

        binding.tvAlbumGenre.text = detail.genre.uppercase()

        binding.tvAlbumName.text = buildAlbumNameSpan(detail.name)

        val artistsText = detail.performers.joinToString(", ") { it.name }
        binding.tvAlbumArtists.text = artistsText

        binding.tvAlbumDescription.text = detail.description

        renderTracks(detail.tracks, artistsText)
    }

    private fun renderTracks(tracks: List<Track>, artistsText: String) {
        binding.trackListContainer.removeAllViews()

        binding.tvTracksSummary.text = getString(
            R.string.tracks_summary,
            tracks.size,
            sumDurations(tracks.map { it.duration })
        )

        val inflater = layoutInflater
        tracks.forEachIndexed { index, track ->
            val rowBinding = ItemTrackBinding.inflate(inflater, binding.trackListContainer, false)
            rowBinding.tvTrackNumber.text = String.format("%02d", index + 1)
            rowBinding.tvTrackName.text = track.name
            rowBinding.tvTrackArtist.text = artistsText
            rowBinding.tvTrackDuration.text = track.duration
            binding.trackListContainer.addView(rowBinding.root)
        }
    }

    private fun buildAlbumNameSpan(name: String): SpannableString {
        val span = SpannableString(name)
        if (name.isEmpty()) return span

        val whiteColor = ContextCompat.getColor(requireContext(), R.color.vinilos_text_primary)
        val salmonColor = ContextCompat.getColor(requireContext(), R.color.vinilos_salmon)

        val parts = name.split(" ", limit = 2)
        if (parts.size >= 2 && parts[1].isNotEmpty()) {
            val firstWordEnd = parts[0].length
            span.setSpan(
                ForegroundColorSpan(whiteColor),
                0,
                firstWordEnd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            span.setSpan(
                ForegroundColorSpan(salmonColor),
                firstWordEnd + 1,
                name.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        } else {
            span.setSpan(
                ForegroundColorSpan(whiteColor),
                0,
                name.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        return span
    }

    private fun sumDurations(durations: List<String>): String {
        var totalSeconds = 0
        for (d in durations) {
            val parts = d.split(":")
            if (parts.size == 2) {
                val mins = parts[0].toIntOrNull() ?: 0
                val secs = parts[1].toIntOrNull() ?: 0
                totalSeconds += mins * 60 + secs
            }
        }
        val mm = totalSeconds / 60
        val ss = totalSeconds % 60
        return String.format("%02d:%02d", mm, ss)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
