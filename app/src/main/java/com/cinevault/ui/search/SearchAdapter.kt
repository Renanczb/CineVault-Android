package com.cinevault.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cinevault.R
import com.cinevault.data.remote.model.MovieResult
import com.cinevault.databinding.ItemMovieBinding

class SearchAdapter(private val onClick: (MovieResult) -> Unit) :
    ListAdapter<MovieResult, SearchAdapter.VH>(DIFF) {

    inner class VH(val binding: ItemMovieBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: MovieResult) {
            binding.tvTitle.text = movie.title
            binding.tvYear.text = movie.releaseYear
            binding.tvRating.text = String.format("%.1f", movie.voteAverage)
            Glide.with(binding.root).load(movie.posterUrl).placeholder(R.drawable.ic_movie_placeholder)
                .into(binding.ivPoster)
            binding.ivPoster.contentDescription = binding.root.context.getString(R.string.poster_of, movie.title)
            binding.root.setOnClickListener { onClick(movie) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<MovieResult>() {
            override fun areItemsTheSame(a: MovieResult, b: MovieResult) = a.id == b.id
            override fun areContentsTheSame(a: MovieResult, b: MovieResult) = a == b
        }
    }
}
