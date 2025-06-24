package com.example.isave.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.isave.Models.Badge
import com.example.isave.R

class BadgesAdapter(private val badges: List<Badge>) :
    RecyclerView.Adapter<BadgesAdapter.BadgeViewHolder>() {

    class BadgeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val badgeIcon: ImageView = itemView.findViewById(R.id.badgeIcon)
        val badgeName: TextView = itemView.findViewById(R.id.badgeName)
        val badgeDescription: TextView = itemView.findViewById(R.id.badgeDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BadgeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_badge, parent, false)
        return BadgeViewHolder(view)
    }

    override fun onBindViewHolder(holder: BadgeViewHolder, position: Int) {
        val badge = badges[position]
        holder.badgeName.text = badge.name
        holder.badgeDescription.text = badge.description
        holder.badgeIcon.setImageResource(badge.iconResId)
    }

    override fun getItemCount(): Int = badges.size
}
