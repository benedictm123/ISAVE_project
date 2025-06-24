package com.example.isave.Activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.isave.Adapters.BadgesAdapter
import com.example.isave.DatabaseHelpers.ExpenseDatabaseHelper
import com.example.isave.Utils.BadgeConstants
import com.example.isave.databinding.ActivityBadgesBinding
import java.text.SimpleDateFormat
import java.util.*

class BadgesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBadgesBinding
    private lateinit var expenseDatabaseHelper: ExpenseDatabaseHelper
    private lateinit var badgesAdapter: BadgesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBadgesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        expenseDatabaseHelper = ExpenseDatabaseHelper(this)
        val earnedUserBadges = expenseDatabaseHelper.getAllUserBadges()

        val displayBadges = earnedUserBadges.mapNotNull { userBadge ->
            BadgeConstants.getBadgeById(userBadge.badgeId)?.copy(
                description = "${BadgeConstants.getBadgeById(userBadge.badgeId)?.description}\n" +
                        "Earned on: ${SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(userBadge.earnedDate))}"
            )
        }

        badgesAdapter = BadgesAdapter(displayBadges)
        binding.badgesRecyclerView.layoutManager = GridLayoutManager(this, 2)
        binding.badgesRecyclerView.adapter = badgesAdapter

        binding.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}
