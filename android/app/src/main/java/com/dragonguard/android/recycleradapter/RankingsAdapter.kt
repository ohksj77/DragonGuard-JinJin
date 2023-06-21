package com.dragonguard.android.recycleradapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dragonguard.android.R
import com.dragonguard.android.databinding.RankingListBinding
import com.dragonguard.android.model.rankings.*

class RankingsAdapter(private val rankings: List<*>): RecyclerView.Adapter<RankingsAdapter.ViewHolder>()  {
    private lateinit var binding: RankingListBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankingsAdapter.ViewHolder {
        binding = RankingListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding.root)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(data1: Any?){
            when(data1) {
                is TotalUsersRankingsModel -> {
                    binding.eachRanking.text = data1.ranking.toString()
                    Glide.with(binding.eachProfile).load(data1.profileImage)
                        .into(binding.eachProfile)
                    binding.rankingGithubId.text = data1.githubId
                    binding.rankingContribute.text = data1.tokens.toString()
                    when(data1.tier) {
                        "BRONZE" -> {
                            binding.rankingItem.setBackgroundResource(R.drawable.shadow_bronze)
                        }
                        "SILVER" -> {
                            binding.rankingItem.setBackgroundResource(R.drawable.shadow_silver)
                        }
                        "GOLD" -> {
                            binding.rankingItem.setBackgroundResource(R.drawable.shadow_gold)
                        }
                        "PLATINUM" -> {
                            binding.rankingItem.setBackgroundResource(R.drawable.shadow_platinum)
                        }
                        "DIAMOND" -> {
                            binding.rankingItem.setBackgroundResource(R.drawable.shadow_diamond)
                        }
                        else -> {
                            binding.rankingItem.setBackgroundResource(R.drawable.shadow)
                        }
                    }
                }
                is OrgInternalRankingsModel -> {
                    binding.eachRanking.text = data1.ranking.toString()
                    Glide.with(binding.eachProfile).load(data1.profileImage)
                        .into(binding.eachProfile)
                    binding.rankingGithubId.text = data1.githubId
                    binding.rankingContribute.text = data1.tokens.toString()
                }
                is TotalOrganizationModel -> {
                    binding.eachRanking.text = data1.ranking.toString()
                    binding.rankingGithubId.text = data1.name
                    when(data1.organizationType) {
                        "COMPANY" -> {
                            binding.eachProfile.setImageResource(R.drawable.company)
                        }
                        "UNIVERSITY" -> {
                            binding.eachProfile.setImageResource(R.drawable.university)
                        }
                        "HIGH_SCHOOL" -> {
                            binding.eachProfile.setImageResource(R.drawable.high_school)
                        }
                        else -> {

                        }
                    }
                    binding.rankingContribute.text = data1.tokenSum.toString()
                }
            }
        }

    }
    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int = rankings.size

    override fun onBindViewHolder(holder: RankingsAdapter.ViewHolder, position: Int) {
        holder.bind(rankings[position])
    }
}