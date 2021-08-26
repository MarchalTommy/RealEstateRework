package com.aki.realestatemanagerv2

import android.content.ContentValues.TAG
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.aki.realestatemanagerv2.databinding.ActivityItemDetailBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior

class ItemDetailHostActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private var isAdd = true
    private lateinit var binding: ActivityItemDetailBinding

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_item_detail) as NavHostFragment
        val navController = navHostFragment.navController
//        appBarConfiguration = AppBarConfiguration(navController.graph)
//
//        setupActionBarWithNavController(navController, appBarConfiguration)

        setupFab()
        setupBottomBar()
    }

    private fun setupBottomBar() {
        val bottomSheetCallback =
            object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                        binding.bottomAppBar.replaceMenu(R.menu.bottom_sheet_up_menu)
                        hideBottomSheet()
                    } else if (newState == BottomSheetBehavior.STATE_EXPANDED || newState == BottomSheetBehavior.STATE_DRAGGING || newState == BottomSheetBehavior.STATE_HALF_EXPANDED) {
                        binding.bottomAppBar.replaceMenu(R.menu.bottom_sheet_up_menu)
                    }
                    Log.d(TAG, "onStateChanged: State Changed! newState = $newState")
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
                binding.apply {
                    binding.bottomAppBarChevron.rotation = 180F
                    binding.fab.hide()
                    binding.bottomAppBarTitle.visibility = View.GONE
                }
                BottomSheetBehavior.from(binding.bottomSheet).apply {
                    peekHeight = 350
                    this.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                }
            }
        }
    }

    private fun hideBottomSheet() {
        BottomSheetBehavior.from(binding.bottomSheet).apply {
            isHideable = true
            this.state = BottomSheetBehavior.STATE_HIDDEN
        }
        binding.apply {
            bottomAppBarChevron.rotation = 0F
            fab.show()
            bottomAppBarTitle.visibility = View.VISIBLE
            bottomAppBar.replaceMenu(R.menu.bottom_bar_menu)
        }
    }

    private fun setupFab() {
        val animEditToAdd = AnimatedVectorDrawableCompat.create(this, R.drawable.avd_edit_to_add)
        val animAddToEdit = AnimatedVectorDrawableCompat.create(this, R.drawable.avd_add_to_edit)
        binding.fab.apply {
            if (isAdd) {
                setImageDrawable(animAddToEdit)
            } else {
                setImageDrawable(animEditToAdd)
            }
            setShowMotionSpecResource(R.animator.fab_show)
            setHideMotionSpecResource(R.animator.fab_hide)
        }

        binding.fab.setOnClickListener {
            if (isAdd) {
                isAdd = false
                binding.fab.setImageDrawable(animAddToEdit)
            } else {
                isAdd = true
                binding.fab.setImageDrawable(animEditToAdd)
            }
            val fabAnim = binding.fab.drawable as AnimatedVectorDrawableCompat
            fabAnim.start()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_item_detail)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}