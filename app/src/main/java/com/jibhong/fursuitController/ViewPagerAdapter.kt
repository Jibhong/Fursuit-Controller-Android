package com.jibhong.fursuitController

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.jibhong.fursuitController.fragment.ConnectPage
import com.jibhong.fursuitController.fragment.FanPage
import com.jibhong.fursuitController.fragment.QuickPage
import com.jibhong.fursuitController.fragment.LedPage


class ViewPagerAdapter(activity: FragmentActivity): FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> QuickPage()
            1 -> ConnectPage()
            2 -> FanPage()
            3 -> LedPage()
            else -> throw IllegalStateException("Invalid position $position")
        }
    }
}
