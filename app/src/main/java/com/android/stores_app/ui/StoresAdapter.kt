package com.android.stores_app.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.stores_app.R
import com.android.stores_app.data.model.Stores
import com.android.stores_app.databinding.AdapterStoresBinding

class StoresAdapter :
    PagingDataAdapter<Stores, StoresAdapter.MyViewHolder>(PixabayItemDiffCallback()) {

    // OVERRIDE ---
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: AdapterStoresBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), R.layout.adapter_stores, parent, false
        )
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    override fun getItemViewType(position: Int): Int = position

    inner class MyViewHolder(private val binding: AdapterStoresBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(stores: Stores) {
            binding.stores = stores
        }
    }
}

class PixabayItemDiffCallback : DiffUtil.ItemCallback<Stores>() {
    override fun areItemsTheSame(oldItem: Stores, newItem: Stores): Boolean =
        oldItem.storesID == newItem.storesID

    override fun areContentsTheSame(oldItem: Stores, newItem: Stores): Boolean =
        oldItem == newItem
}