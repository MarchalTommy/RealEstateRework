package com.aki.realestatemanagerv2

import android.Manifest
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.NestedScrollView
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import androidx.viewpager2.widget.ViewPager2
import com.aki.realestatemanagerv2.databinding.ActivityItemDetailBinding
import com.aki.realestatemanagerv2.ui.ViewPagerAdapter
import com.aki.realestatemanagerv2.ui.mainList.ListFragmentDirections
import com.araujo.jordan.excuseme.ExcuseMe
import com.google.android.material.bottomsheet.BottomSheetBehavior

class ItemDetailHostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityItemDetailBinding
    private lateinit var navController: NavController
    private var itemDetailFragmentContainer: View? = null
    private lateinit var viewpager: ViewPager2
    private var pagerState = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_item_detail) as NavHostFragment
        navController = navHostFragment.navController

        viewpager = findViewById(R.id.bottom_pager)

        itemDetailFragmentContainer = findViewById(R.id.item_detail_nav_container)

        setupFab()
        setupBottomBar()
        navControllerManagement()
        bottomSheetManagement()
    }

    private fun navControllerManagement() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.addListItemFragment -> {
                    binding.fab.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_check_24, null))
                    binding.bottomAppBar.performHide()
                }
                R.id.listFragment -> {
                    if (binding.bottomAppBar.menu.getItem(0).itemId == R.id.map) {
                        val animMap = AnimatedVectorDrawableCompat.create(
                            this,
                            R.drawable.avd_map_in_2
                        )
                        binding.bottomAppBar.menu.getItem(0).apply {
                            this.icon = animMap
                            val menuAnim = this.icon as AnimatedVectorDrawableCompat
                            menuAnim.start()
                            this.isEnabled = true
                        }
                    } else {
                        binding.bottomAppBar.menu.getItem(0).apply {
                            this.isEnabled = true
                        }
                    }
                    binding.fab.apply {
                        setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_add, null))
                        setOnClickListener {
                            navController.navigate(R.id.addListItemFragment)
                        }
                    }
                    binding.bottomAppBar.performShow()
                }
                R.id.detailFragment -> {
                    binding.fab.apply {
                        setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_edit, null))
                        setOnClickListener {
                            navController.navigate(R.id.editItemFragment)
                        }
                    }
                    binding.bottomAppBar.performHide()
                }
                R.id.editItemFragment -> {
                    binding.fab.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_check_24, null))
                    binding.bottomAppBar.performHide()
                }
            }
        }
    }

    private fun setupBottomBar() {
        val bottomSheetCallback =
            object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                        binding.bottomAppBar.apply {
                            val animLoanToMap = AnimatedVectorDrawableCompat.create(
                                this.context,
                                R.drawable.avd_loan_to_map
                            )
                            menu.getItem(0).icon = animLoanToMap
                            val loanToMapAnim = menu.getItem(0).icon as AnimatedVectorDrawableCompat
                            loanToMapAnim.start()
                            replaceMenu(R.menu.bottom_bar_menu)
                            performShow()
                        }
                        hideBottomSheet()
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                }
            }

        BottomSheetBehavior.from(binding.bottomSheet).apply {
            addBottomSheetCallback(bottomSheetCallback)
            peekHeight = 0
        }
        binding.bottomAppBar.replaceMenu(R.menu.bottom_bar_menu)
        binding.bottomAppBarContentContainer.setOnClickListener {
            if (binding.fab.isOrWillBeHidden) {
                hideBottomSheet()
            } else {
                binding.bottomAppBar.apply {
                    val animMapToLoan = AnimatedVectorDrawableCompat.create(
                        this.context,
                        R.drawable.avd_map_to_loan
                    )
                    menu.getItem(0).icon = animMapToLoan
                    val mapToLoanAnim = menu.getItem(0).icon as AnimatedVectorDrawableCompat
                    mapToLoanAnim.start()
                }
                binding.bottomAppBar.replaceMenu(R.menu.bottom_sheet_up_menu)
                binding.apply {
                    binding.bottomAppBarChevron.rotation = 180F
                    binding.fab.hide()
                }
                BottomSheetBehavior.from(binding.bottomSheet).apply {
                    this.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }
        }

        binding.bottomAppBar.setOnMenuItemClickListener {
            if (it.itemId == R.id.map) {
                if (!Utils.isTablet(baseContext)) {
                    ExcuseMe.couldYouGive(this).permissionFor(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) { permissionStatus ->
                        if (permissionStatus.granted.contains(Manifest.permission.ACCESS_FINE_LOCATION) &&
                            permissionStatus.granted.contains(Manifest.permission.ACCESS_COARSE_LOCATION)
                        ) {
                            val action =
                                ListFragmentDirections.actionListFragmentToMapFragment(true)
                            navController.navigate(action)

                            val animMap = AnimatedVectorDrawableCompat.create(
                                this,
                                R.drawable.avd_map_out_2
                            )
                            it.isEnabled = false
                            it.icon = animMap
                            val menuAnim = it.icon as AnimatedVectorDrawableCompat
                            menuAnim.start()
                        }
                    }
                }
            } else {
                when (pagerState) {
                    0 -> {
                        binding.bottomPager.currentItem = 1
                        pagerState = 1
                        it.icon = ActivityCompat.getDrawable(this, R.drawable.ic_filter_24dp)
                    }
                    1 -> {
                        binding.bottomPager.currentItem = 0
                        pagerState = 0
                        it.icon = ActivityCompat.getDrawable(this, R.drawable.ic_monetization)
                    }
                }
            }
            true
        }
    }

    private fun hideBottomSheet() {
        val scrollView = findViewById<NestedScrollView>(R.id.scroll_view)
        scrollView.scrollTo(0, (scrollView.top - 64))
        BottomSheetBehavior.from(binding.bottomSheet).apply {
            isHideable = true
            this.state = BottomSheetBehavior.STATE_HIDDEN
        }
        binding.apply {
            bottomAppBarChevron.rotation = 0F
            fab.show()
            bottomAppBar.replaceMenu(R.menu.bottom_bar_menu)
        }
    }

    private fun setupFab() {
        binding.fab.apply {
            setShowMotionSpecResource(R.animator.fab_show)
            setHideMotionSpecResource(R.animator.fab_hide)
        }
    }

    private fun bottomSheetManagement() {
        viewpager.adapter = ViewPagerAdapter(this)
        viewpager.isUserInputEnabled = false
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_item_detail)
        return navController.navigateUp()
                || super.onSupportNavigateUp()
    }
}