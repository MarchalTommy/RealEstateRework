package com.aki.realestatemanagerv2.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.aki.realestatemanagerv2.ui.bottomFragment.FilterFragment
import com.aki.realestatemanagerv2.ui.bottomFragment.LoanSimFragment

class ViewPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FilterFragment()
            1 -> LoanSimFragment()
            else -> FilterFragment()
        }
    }
}