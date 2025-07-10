package com.jibhong.fursuitController

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.master_page)
//        window.decorView.systemUiVisibility = (
//        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKYo
//            or View.SYSTEM_UI_FLAG_FULLSCREEN
//            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//        )


        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)

        viewPager.adapter = ViewPagerAdapter(this)

        val tabTitles = arrayOf("Quick", "Connect", "Fan", "LED")

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

    }
}