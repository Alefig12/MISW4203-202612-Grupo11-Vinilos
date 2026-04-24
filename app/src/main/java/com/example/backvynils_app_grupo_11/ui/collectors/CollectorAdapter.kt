package com.example.backvynils_app_grupo_11.ui.collectors

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.backvynils_app_grupo_11.databinding.ItemCollectorBinding
import com.example.backvynils_app_grupo_11.model.Collector

class CollectorAdapter : ListAdapter<Collector, CollectorAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: ItemCollectorBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(collector: Collector) {
            binding.tvCollectorName.text = collector.name
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
