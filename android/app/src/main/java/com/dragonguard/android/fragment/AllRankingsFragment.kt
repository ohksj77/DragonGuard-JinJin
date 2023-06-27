package com.dragonguard.android.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dragonguard.android.R
import com.dragonguard.android.activity.profile.UserProfileActivity
import com.dragonguard.android.databinding.FragmentAllRankingsBinding
import com.dragonguard.android.model.rankings.*
import com.dragonguard.android.adapters.RankingsAdapter
import com.dragonguard.android.viewmodel.Viewmodel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class AllRankingsFragment(private val token: String, private val rankingType: String) : Fragment() {
    private lateinit var binding: FragmentAllRankingsBinding
    private var viewmodel = Viewmodel()
    private val size = 20
    private var page = 0
    private var position = 0
    private var ranking = 0
    private var type = ""
    private var usersRanking = ArrayList<TotalUsersRankingsModel>()
    private var changed = true
    private lateinit var rankingsAdapter: RankingsAdapter
    private var totalOrgRankings = ArrayList<TotalOrganizationModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAllRankingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when(rankingType) {
            "total" -> {
                getTotalUsersRanking(page, size)
            }
            "company" -> {
                type = "COMPANY"
                typeOrgRankings()
            }
            "university" -> {
                type = "UNIVERSITY"
                typeOrgRankings()
            }
            "high_school" -> {
                type = "HIGH_SCHOOL"
                typeOrgRankings()
            }
            "etc" -> {
                type = "ETC"
                typeOrgRankings()
            }
        }
    }

    private fun getTotalUsersRanking(page: Int, size: Int) {
        binding.rankingLottie.visibility = View.VISIBLE
        binding.rankingLottie.playAnimation()
        val coroutine = CoroutineScope(Dispatchers.Main)
        coroutine.launch {
            if(!this@AllRankingsFragment.isRemoving) {
                val resultDeferred = coroutine.async(Dispatchers.IO) {
                    viewmodel.getTotalUserRanking(page, size, token)
                }
                val result = resultDeferred.await()
                checkRankings(result)
            }
        }
    }

    private fun checkRankings(result: ArrayList<TotalUsersRankingModelItem>) {
//        Toast.makeText(applicationContext, "개수 : ${result.size}",Toast.LENGTH_SHORT).show()
        if(result.isNotEmpty()) {
            result.forEach {
                if(it.tokens != null) {
                    if(ranking != 0) {
                        if(usersRanking[ranking-1].tokens == it.tokens) {
                            usersRanking.add(TotalUsersRankingsModel(it.tokens,it.github_id,it.id,it.name,it.tier,usersRanking[ranking-1].ranking, it.profile_image))
                        } else {
                            usersRanking.add(TotalUsersRankingsModel(it.tokens,it.github_id,it.id,it.name,it.tier,ranking+1, it.profile_image))
                        }
                    } else {
                        usersRanking.add(TotalUsersRankingsModel(it.tokens,it.github_id,it.id,it.name,it.tier,1, it.profile_image))
                    }
//                Log.d("유져", "랭킹 ${ranking+1} 추가")
                    ranking++
                }
            }
            initRecycler()
        } else {
            if(changed) {
                changed = false
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({getTotalUsersRanking(page, size)}, 4000)
            } else {
                binding.rankingLottie.pauseAnimation()
                binding.rankingLottie.visibility = View.GONE
            }
        }
    }

    private fun initRecycler() {
        binding.eachRankings.setItemViewCacheSize(usersRanking.size)
//        Toast.makeText(applicationContext, "개수 : ${usersRanking.size}",Toast.LENGTH_SHORT).show()
        if(page == 0) {
            when( usersRanking.size) {
                1 -> {
                    profileBackground(usersRanking[0], 1)
                }
                2 -> {
                    profileBackground(usersRanking[0], 1)
                    profileBackground(usersRanking[1], 2)
                }
                3 -> {
                    profileBackground(usersRanking[0], 1)
                    profileBackground(usersRanking[1], 2)
                    profileBackground(usersRanking[2], 3)
                }
                else -> {
                    profileBackground(usersRanking[0], 1)
                    profileBackground(usersRanking[1], 2)
                    profileBackground(usersRanking[2], 3)

                    usersRanking.removeFirst()
                    usersRanking.removeFirst()
                    usersRanking.removeFirst()

                    rankingsAdapter = RankingsAdapter(usersRanking)
                    binding.eachRankings.adapter = rankingsAdapter
                    binding.eachRankings.layoutManager = LinearLayoutManager(requireContext())
//            totalUserRankingAdapter.notifyDataSetChanged()
                    binding.eachRankings.visibility = View.VISIBLE
                }
            }
        }
        binding.eachRankings.adapter?.notifyDataSetChanged()
        page++
        Log.d("api 횟수", "$page 페이지 검색")
        binding.rankingLottie.pauseAnimation()
        binding.rankingLottie.visibility = View.GONE
        initScrollListener()
    }

    private fun profileBackground(model: TotalUsersRankingsModel, number: Int) {
        binding.firstProfile.clipToOutline = true
        binding.secondProfile.clipToOutline = true
        binding.thirdProfile.clipToOutline = true
        when(number) {
            1 -> {
                when(model.tier) {
                    "BRONZE" -> {
                        binding.firstFrame.setBackgroundResource(R.drawable.shadow_bronze)

                    }
                    "SILVER" -> {
                        binding.firstFrame.setBackgroundResource(R.drawable.shadow_silver)
                    }
                    "GOLD" -> {
                        binding.firstFrame.setBackgroundResource(R.drawable.shadow_gold)
                    }
                    "PLATINUM" -> {
                        binding.firstFrame.setBackgroundResource(R.drawable.shadow_platinum)
                    }
                    "DIAMOND" -> {
                        binding.firstFrame.setBackgroundResource(R.drawable.shadow_diamond)
                    }
                    else -> {
                        binding.firstFrame.setBackgroundResource(R.drawable.shadow)
                    }
                }
                binding.firstId.text = model.github_id
                Glide.with(binding.firstProfile).load(model.profile_image)
                    .into(binding.firstProfile)
                binding.firstContribute.text = model.tokens.toString()
                binding.firstFrame.setOnClickListener {
                    val intent = Intent(requireContext(), UserProfileActivity::class.java)
                    intent.putExtra("userName", model.github_id)
                    intent.putExtra("token", token)
                    startActivity(intent)
                }
                binding.firstRanker.visibility = View.VISIBLE
                binding.topRankings.visibility = View.VISIBLE
            }
            2 -> {
                when(model.tier) {
                    "BRONZE" -> {
                        binding.secondFrame.setBackgroundResource(R.drawable.shadow_bronze)

                    }
                    "SILVER" -> {
                        binding.secondFrame.setBackgroundResource(R.drawable.shadow_silver)
                    }
                    "GOLD" -> {
                        binding.secondFrame.setBackgroundResource(R.drawable.shadow_gold)
                    }
                    "PLATINUM" -> {
                        binding.secondFrame.setBackgroundResource(R.drawable.shadow_platinum)
                    }
                    "DIAMOND" -> {
                        binding.secondFrame.setBackgroundResource(R.drawable.shadow_diamond)
                    }
                    else -> {
                        binding.secondFrame.setBackgroundResource(R.drawable.shadow)
                    }
                }
                binding.secondId.text = model.github_id
                Glide.with(binding.secondProfile).load(model.profile_image)
                    .into(binding.secondProfile)
                binding.secondContribute.text = model.tokens.toString()
                binding.secondFrame.setOnClickListener {
                    val intent = Intent(requireContext(), UserProfileActivity::class.java)
                    intent.putExtra("userName", model.github_id)
                    intent.putExtra("token", token)
                    startActivity(intent)
                }
                binding.secondRanker.visibility = View.VISIBLE
                binding.topRankings.visibility = View.VISIBLE
            }
            3 -> {
                when(model.tier) {
                    "BRONZE" -> {
                        binding.thirdFrame.setBackgroundResource(R.drawable.shadow_bronze)

                    }
                    "SILVER" -> {
                        binding.thirdFrame.setBackgroundResource(R.drawable.shadow_silver)
                    }
                    "GOLD" -> {
                        binding.thirdFrame.setBackgroundResource(R.drawable.shadow_gold)
                    }
                    "PLATINUM" -> {
                        binding.thirdFrame.setBackgroundResource(R.drawable.shadow_platinum)
                    }
                    "DIAMOND" -> {
                        binding.thirdFrame.setBackgroundResource(R.drawable.shadow_diamond)
                    }
                    else -> {
                        binding.thirdFrame.setBackgroundResource(R.drawable.shadow)
                    }
                }
                binding.thirdId.text = model.github_id
                Glide.with(binding.thirdProfile).load(model.profile_image)
                    .into(binding.thirdProfile)
                binding.thirdContribute.text = model.tokens.toString()
                binding.thirdFrame.setOnClickListener {
                    val intent = Intent(requireContext(), UserProfileActivity::class.java)
                    intent.putExtra("userName", model.github_id)
                    intent.putExtra("token", token)
                    startActivity(intent)
                }
                binding.thirdRanker.visibility = View.VISIBLE
                binding.topRankings.visibility = View.VISIBLE
            }
        }
    }

    private fun loadMorePosts() {
        if (binding.rankingLottie.visibility == View.GONE) {
            binding.rankingLottie.visibility = View.VISIBLE
            binding.rankingLottie.playAnimation()
            changed = true
            CoroutineScope(Dispatchers.Main).launch {
                Log.d("api 시도", "getTotalUsersRanking 실행  load more")
                getTotalUsersRanking(page, size)
            }
        }
    }

    private fun initScrollListener() {
        binding.eachRankings.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = binding.eachRankings.layoutManager
                val lastVisibleItem = (layoutManager as LinearLayoutManager)
                    .findLastCompletelyVisibleItemPosition()
                val itemTotalCount = recyclerView.adapter!!.itemCount - 1
                position = recyclerView.adapter!!.itemCount - 1
                // 마지막으로 보여진 아이템 position 이
                // 전체 아이템 개수보다 1개 모자란 경우, 데이터를 loadMore 한다
                if (!binding.eachRankings.canScrollVertically(1) && lastVisibleItem == itemTotalCount) {
                    loadMorePosts()
                }
            }
        })
    }


    private fun typeOrgRankings() {
        binding.rankingLottie.visibility = View.VISIBLE
        binding.rankingLottie.playAnimation()
        val coroutine = CoroutineScope(Dispatchers.Main)
        coroutine.launch {
            if(!this@AllRankingsFragment.isRemoving) {
                val resultDeferred = coroutine.async(Dispatchers.IO) {
                    viewmodel.typeOrgRankings(type, page, token)
                }
                val result = resultDeferred.await()
                checkRankings(result)
            }
        }
    }

    private fun checkRankings(result: OrganizationRankingModel) {
        if (result.isNotEmpty()) {
            Log.d("조직 내 랭킹", "결과 : ${result[0].name}")
            result.forEach {
                Log.d("조직 내 랭킹", "결과 : ${it.name}")
                if (ranking != 0) {
                    if (totalOrgRankings[ranking - 1].token_sum == it.token_sum) {
                        totalOrgRankings.add(
                            TotalOrganizationModel(
                                it.email_endpoint, it.id, it.name, it.organization_type, it.token_sum,
                                totalOrgRankings[ranking - 1].ranking
                            )
                        )
                    } else {
                        totalOrgRankings.add(
                            TotalOrganizationModel(
                                it.email_endpoint,
                                it.id,
                                it.name,
                                it.organization_type,
                                it.token_sum,
                                ranking + 1
                            )
                        )
                    }
                } else {
                    totalOrgRankings.add(
                        TotalOrganizationModel(
                            it.email_endpoint,
                            it.id,
                            it.name,
                            it.organization_type,
                            it.token_sum,
                            1
                        )
                    )
                }
                Log.d("유져", "랭킹 ${ranking+1} 추가")
                ranking++
            }
            Log.d("뷰 보이기 전", "initrecycler 전")
            initOrgRecycler()

        } else {
            if (changed) {
                changed = false
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({ typeOrgRankings() }, 2000)
            } else {
                binding.rankingLottie.pauseAnimation()
                binding.rankingLottie.visibility = View.GONE
            }
        }
    }

    private fun initOrgRecycler() {
        Log.d("recycler", "initrecycler()")
        binding.eachRankings.setItemViewCacheSize(totalOrgRankings.size)
        if (page == 0) {
            rankingsAdapter = RankingsAdapter(totalOrgRankings)
            binding.eachRankings.adapter = rankingsAdapter
            binding.eachRankings.layoutManager = LinearLayoutManager(requireContext())
            binding.eachRankings.visibility = View.VISIBLE
        }
        binding.eachRankings.adapter?.notifyDataSetChanged()
        page++
        Log.d("api 횟수", "$page 페이지 검색")
        binding.rankingLottie.pauseAnimation()
        binding.rankingLottie.visibility = View.GONE
        initOrgScrollListener()
    }

    private fun loadOrgMorePosts() {
        if (binding.rankingLottie.visibility == View.GONE) {
            binding.rankingLottie.visibility = View.VISIBLE
            binding.rankingLottie.playAnimation()
            changed = true
            CoroutineScope(Dispatchers.Main).launch {
                Log.d("api 시도", "getTotalUsersRanking 실행  load more")
                typeOrgRankings()
            }
        }
    }

    private fun initOrgScrollListener() {
        binding.eachRankings.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = binding.eachRankings.layoutManager
                val lastVisibleItem = (layoutManager as LinearLayoutManager)
                    .findLastCompletelyVisibleItemPosition()
                val itemTotalCount = recyclerView.adapter!!.itemCount - 1
                position = recyclerView.adapter!!.itemCount - 1
                // 마지막으로 보여진 아이템 position 이
                // 전체 아이템 개수보다 1개 모자란 경우, 데이터를 loadMore 한다
                if (!binding.eachRankings.canScrollVertically(1) && lastVisibleItem == itemTotalCount) {
                    loadOrgMorePosts()
                }
            }
        })
    }
}