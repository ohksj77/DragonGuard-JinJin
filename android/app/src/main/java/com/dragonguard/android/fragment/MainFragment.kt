package com.dragonguard.android.fragment

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.view.marginBottom
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.dragonguard.android.R
import com.dragonguard.android.activity.basic.MainActivity
import com.dragonguard.android.activity.basic.TokenHistoryActivity
import com.dragonguard.android.activity.search.SearchActivity
import com.dragonguard.android.databinding.FragmentMainBinding
import com.dragonguard.android.model.UserInfoModel
import com.dragonguard.android.adapters.UserActivityAdapter
import com.dragonguard.android.viewmodel.Viewmodel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class MainFragment(private val token: String, private var info: UserInfoModel) : Fragment() {
    private lateinit var binding: FragmentMainBinding
    private var viewmodel = Viewmodel()
    private var repeat = false
    private var refresh = true
//    private var menuItem: MenuItem? = null
    val handler= Handler(Looper.getMainLooper()){
        setPage()
        repeat = true
        true
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
        binding.mainFragViewmodel = viewmodel
//        val main = activity as MainActivity
//        main.setSupportActionBar(binding.toolbar)
//        main.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        binding.toolbar.inflateMenu(R.menu.refresh)
        binding.githubProfile.clipToOutline = true
        binding.tokenFrame.setOnClickListener {
            val intent = Intent(requireActivity(), TokenHistoryActivity::class.java)
            intent.putExtra("token", token)
            startActivity(intent)
        }

        drawInfo()
        CoroutineScope(Dispatchers.IO).launch{
            if(!repeat) {
                while(true){
                    Thread.sleep(3000)
                    handler.sendEmptyMessage(0)
                }
            }
        }
    }

    private fun drawInfo() {
        val main = activity as MainActivity
        val layoutParams = binding.mainFrame.layoutParams as FrameLayout.LayoutParams
        layoutParams.bottomMargin = main.getNavSize()
        binding.mainFrame.layoutParams = layoutParams
        if (info.github_id!!.isNotBlank()) {
            binding.userId.text = info.github_id
        }
        if(!requireActivity().isFinishing) {
            Glide.with(binding.githubProfile).load(info.profile_image)
                .into(binding.githubProfile)
        }

        when(info.tier) {
            "BRONZE" ->{
                binding.tierImg.setBackgroundResource(R.drawable.bronze)
            }
            "SILVER" ->{
                binding.tierImg.setBackgroundResource(R.drawable.silver)
            }
            "GOLD" ->{
                binding.tierImg.setBackgroundResource(R.drawable.gold)
            }
            "PLATINUM" ->{
                binding.tierImg.setBackgroundResource(R.drawable.platinum)
            }
            "DIAMOND" ->{
                binding.tierImg.setBackgroundResource(R.drawable.diamond)
            }
        }
        viewmodel.onSearchClickListener.observe(requireActivity(), Observer {
            if(viewmodel.onSearchClickListener.value == true) {
                val intent = Intent(requireActivity(), SearchActivity::class.java)
                intent.putExtra("token", token)
                startActivity(intent)
            }
        } )
        if (info.token_amount != null) {
            binding.tokenAmount.text = info.token_amount.toString()
        }
        val typeList = listOf("commits", "issues", "pullRequests", "review")
        if(info.organization != null) {
            binding.userOrgName.text = info.organization
        }
        val userActivity = HashMap<String, Int>()
        userActivity.put("commits", info.commits!!)
        userActivity.put("issues", info.issues!!)
        userActivity.put("pullRequests", info.pull_requests!!)
        info.reviews?.let {
            userActivity.put("review", it)
        }
        Log.d("map", "hashMap: $userActivity")
        binding.userUtil.adapter = UserActivityAdapter(userActivity, typeList, requireContext())
        binding.userUtil.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        if(info.organization == null) {
            binding.mainOrgFrame.visibility = View.GONE
        } else {
            when(info.organization_rank) {
                1 -> {
                    when(info.member_github_ids?.size){
                        1 -> {
                            binding.user2Githubid.text = info.member_github_ids!![0]
                            binding.user2Ranking.text = "1"
                        }
                        2 -> {
                            binding.user2Githubid.text = info.member_github_ids!![0]
                            binding.user2Ranking.text = "1"
                            binding.user3Githubid.text = info.member_github_ids!![1]
                            binding.user3Ranking.text = "2"
                        }
                        3 -> {
                            binding.user2Githubid.text = info.member_github_ids!![0]
                            binding.user2Ranking.text = "1"
                            binding.user3Githubid.text = info.member_github_ids!![1]
                            binding.user3Ranking.text = "2"
                        }
                    }
                }
                else -> {
                    when(info.member_github_ids?.size){
                        2 -> {
                            binding.user1Githubid.text = info.member_github_ids!![0]
                            binding.user1Ranking.text = info.organization_rank!!.minus(1).toString()
                            binding.user2Githubid.text = info.member_github_ids!![1]
                            binding.user2Ranking.text = info.organization_rank!!.toString()
                        }
                        3 -> {
                            when(info.is_last) {
                                true -> {
                                    binding.user2Githubid.text = info.member_github_ids!![0]
                                    binding.user2Ranking.text = info.organization_rank!!.minus(1).toString()
                                    binding.user3Githubid.text = info.member_github_ids!![2]
                                    binding.user3Ranking.text = info.organization_rank!!.toString()
                                }
                                false -> {
                                    binding.user1Githubid.text = info.member_github_ids!![0]
                                    binding.user1Ranking.text = info.organization_rank!!.minus(1).toString()
                                    binding.user2Githubid.text = info.member_github_ids!![1]
                                    binding.user2Ranking.text = info.organization_rank!!.toString()
                                    binding.user3Githubid.text = info.member_github_ids!![2]
                                    binding.user3Ranking.text = info.organization_rank!!.plus(1).toString()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        inflater.inflate(R.menu.refresh, binding.toolbar.menu)
//        menuItem = menu.findItem(R.id.refresh_button)
//        menuItem?.icon?.alpha = 64
//        super.onCreateOptionsMenu(menu, inflater)
//    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.refresh_button -> {
//                if(refresh) {
//                    refresh = false
//                    val coroutine = CoroutineScope(Dispatchers.Main)
//                    coroutine.launch {
//                        if(this@MainFragment.isResumed) {
//                            Log.d("refresh", "refresh main!!")
//                            val resultRepoDeferred = coroutine.async(Dispatchers.IO) {
//                                viewmodel.updateUserInfo(token)
//                            }
//                            val resultRepo = resultRepoDeferred.await()
//                            info = resultRepo
//                            drawInfo()
//                        }
//                    }
//                }
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }

    private fun setPage(){
        binding.userUtil.setCurrentItem((binding.userUtil.currentItem+1)%4,false)
    }

}