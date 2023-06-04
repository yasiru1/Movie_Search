package com.yasiru.moviesearch.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.Disposable
import com.yasiru.moviesearch.databinding.ItemSearchBinding
import com.yasiru.moviesearch.network.SearchResult


typealias SearchClickListener = (SearchResult) -> Unit

class SearchAdapter : ListAdapter<SearchResult, SearchViewHolder>(SearchResultDiff()) {

    var listener: SearchClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return SearchViewHolder(ItemSearchBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        holder.itemView.setOnClickListener { listener?.invoke(item) }
    }
}

class SearchResultDiff : DiffUtil.ItemCallback<SearchResult>() {
    override fun areItemsTheSame(oldItem: SearchResult, newItem: SearchResult): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: SearchResult, newItem: SearchResult): Boolean {
        return true
    }
}

class SearchViewHolder(private val binding: ItemSearchBinding) :
    RecyclerView.ViewHolder(binding.root) {

    private var disposable: Disposable? = null

    fun bind(item: SearchResult) {
        disposable?.dispose()
        disposable = binding.cover.load(item.imageUrl)
        binding.title.text = item.title
        binding.description.text = item.description
    }
}
