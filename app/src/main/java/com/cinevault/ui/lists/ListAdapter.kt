package com.cinevault.ui.lists

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cinevault.R
import com.cinevault.data.local.MovieEntity
import com.cinevault.databinding.ItemMovieListBinding

class ListAdapter(
    private val onMovieClick: (MovieEntity) -> Unit,
    private val onRemove: (MovieEntity) -> Unit,
    private val onMove: (MovieEntity) -> Unit
) : androidx.recyclerview.widget.ListAdapter<MovieEntity, ListAdapter.VH>(DIFF) {

    inner class VH(val binding: ItemMovieListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: MovieEntity) {
            binding.tvTitle.text = movie.title
            binding.tvYear.text = movie.releaseYear
            binding.tvRating.text = String.format("%.1f", movie.voteAverage)
            Glide.with(binding.root).load(movie.posterUrl).placeholder(R.drawable.ic_movie_placeholder).into(binding.ivPoster)
            binding.ivPoster.contentDescription = binding.root.context.getString(R.string.poster_of, movie.title)
            binding.root.setOnClickListener { onMovieClick(movie) }
            binding.btnRemove.setOnClickListener { onRemove(movie) }
            binding.btnMove.setOnClickListener { onMove(movie) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemMovieListBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<MovieEntity>() {
            override fun areItemsTheSame(a: MovieEntity, b: MovieEntity) = a.tmdbId == b.tmdbId
            override fun areContentsTheSame(a: MovieEntity, b: MovieEntity) = a == b
        }
    }
}
