package com.example.vinilos_grupo11.ui.collectors

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.example.vinilos_grupo11.R
import com.example.vinilos_grupo11.databinding.ItemCollectorBinding
import com.example.vinilos_grupo11.models.Collector

class CollectorAdapter(
    private val onCollectorClick: (Int) -> Unit = {}
) : ListAdapter<Collector, CollectorAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: ItemCollectorBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(collector: Collector) {
            binding.root.contentDescription = binding.root.context.getString(
                R.string.cd_collector_item, collector.name
            )
            binding.root.setOnClickListener { onCollectorClick(collector.id) }
            binding.tvCollectorName.text = collector.name
            binding.tvNoPhoto.visibility = View.VISIBLE
            binding.ivCollectorImage.setImageResource(android.R.color.transparent)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCollectorBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<Collector>() {
        override fun areItemsTheSame(oldItem: Collector, newItem: Collector) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Collector, newItem: Collector) =
            oldItem == newItem
    }
}
