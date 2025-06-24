package com.example.isave.Utils

import com.example.isave.Models.Badge
import com.example.isave.R

object BadgeConstants {

    val ALL_BADGES = listOf(
        Badge(
            id = "budget_goal_met",
            name = "Budget Champion!",
            description = "Congratulations! You stayed within your budget goal!",
            iconResId = R.drawable.trophy // Use your trophy icon here
        )
        // Add other badges here if you want
    )

    fun getBadgeById(id: String): Badge? {
        return ALL_BADGES.find { it.id == id }
    }
}
