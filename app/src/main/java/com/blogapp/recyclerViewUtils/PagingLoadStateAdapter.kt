package com.blogapp.recyclerViewUtils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.blogapp.R
import com.blogapp.databinding.ItemNetworkStateBinding

class PagingLoadStateAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<PagingLoadStateAdapter.NetworkStateItemViewHolder>() {

    class NetworkStateItemViewHolder(
        private val binding: ItemNetworkStateBinding,
        retryCallback: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.retryButton.setOnClickListener { retryCallback() }
        }

        fun bind(loadState: LoadState) {
            if (loadState is LoadState.Error) {
                binding.errorMsg.text = loadState.error.localizedMessage
            }
            binding.progressBar.isVisible = loadState is LoadState.Loading
            binding.errorMsg.isVisible = loadState is LoadState.Error
            binding.retryButton.isVisible = loadState is LoadState.Error
        }


        companion object {
            fun create(parent: ViewGroup, retryCallback: () -> Unit): NetworkStateItemViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_network_state, parent, false)
                val binding = ItemNetworkStateBinding.bind(view)
                return NetworkStateItemViewHolder(binding, retryCallback)
            }
        }

    }

    override fun onBindViewHolder(holder: NetworkStateItemViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ) = NetworkStateItemViewHolder.create(parent, retry)

}