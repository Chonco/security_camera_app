package com.example.securitycamera.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.securitycamera.databinding.ReportCardLayoutBinding
import com.example.securitycamera.model.Report
import com.example.securitycamera.util.GlideApp
import com.google.firebase.storage.FirebaseStorage

class ReportAdapter(
    private val context: Context,
    private val onReportClicked: (Report) -> Unit
) : ListAdapter<Report, ReportAdapter.ReportViewHolder>(DiffCallback) {

    private val firebaseStorageReference = FirebaseStorage.getInstance().reference

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Report>() {
            override fun areItemsTheSame(oldItem: Report, newItem: Report): Boolean {
                return oldItem.createdAt == newItem.createdAt
            }

            override fun areContentsTheSame(oldItem: Report, newItem: Report): Boolean {
                return oldItem.imageURL == newItem.imageURL
            }
        }
    }

    inner class ReportViewHolder(private var binding: ReportCardLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(report: Report) {
            binding.apply {
                fromUserHolder.text = if (report.byUser == true) "Por Usuario" else "Por detector"
                dateHolder.text = report.createdAt
            }

            GlideApp.with(context)
                .load(firebaseStorageReference.child(report.imageURL!!))
                .into(binding.imageHolder)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReportAdapter.ReportViewHolder {
        val viewHolder = ReportViewHolder(
            ReportCardLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.bindingAdapterPosition
            onReportClicked(getItem(position))
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: ReportAdapter.ReportViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}