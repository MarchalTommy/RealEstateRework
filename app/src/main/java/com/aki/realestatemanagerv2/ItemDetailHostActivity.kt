package com.aki.realestatemanagerv2

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.aki.realestatemanagerv2.databinding.ActivityItemDetailBinding
import com.aki.realestatemanagerv2.viewmodel.SharedViewModel
import com.aki.realestatemanagerv2.viewmodel.Transition
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomsheet.BottomSheetBehavior

class ItemDetailHostActivity : AppCompatActivity() {

    private val sharedViewModel: SharedViewModel by viewModels()
    private lateinit var binding: ActivityItemDetailBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_item_detail) as NavHostFragment
        navController = navHostFragment.navController

        setupFab()
        setupBottomBar()
        test2()
        test()
    }

    // TODO: 07/09/2021 RENAME FUN + VERIFY NAV HERE OR IN FRAG
    private fun test2() {
        sharedViewModel.elementClicked.observe(this, {
            when (it) {
                Transition.LIST_DETAIL -> binding.fab.apply {
                    val animAddToEdit = AnimatedVectorDrawableCompat.create(
                        this.context,
                        R.drawable.avd_add_to_edit
                    )
                    setImageDrawable(animAddToEdit)
                    val fabAnim = this.drawable as AnimatedVectorDrawableCompat
                    fabAnim.start()
                }

                Transition.DETAIL_LIST -> binding.fab.apply {
                    val animEditToAdd = AnimatedVectorDrawableCompat.create(
                        this.context,
                        R.drawable.avd_edit_to_add
                    )
                    setImageDrawable(animEditToAdd)
                    val fabAnim = this.drawable as AnimatedVectorDrawableCompat
                    fabAnim.start()
                }

                Transition.LIST_ADD -> binding.fab.apply {
                    val animAddToCheck = AnimatedVectorDrawableCompat.create(
                        this.context,
                        R.drawable.avd_add_to_check
                    )
                    setImageDrawable(animAddToCheck)
                    val fabAnim = this.drawable as AnimatedVectorDrawableCompat
                    fabAnim.start()
                }

                Transition.ADD_LIST -> binding.fab.apply {
                    val animCheckToAdd = AnimatedVectorDrawableCompat.create(
                        this.context,
                        R.drawable.avd_check_to_add
                    )
                    setImageDrawable(animCheckToAdd)
                    val fabAnim = this.drawable as AnimatedVectorDrawableCompat
                    fabAnim.start()
                }

                Transition.DETAIL_EDIT -> binding.fab.apply {
                    val animEditToCheck = AnimatedVectorDrawableCompat.create(
                        this.context,
                        R.drawable.avd_edit_to_check
                    )
                    setImageDrawable(animEditToCheck)
                    val fabAnim = this.drawable as AnimatedVectorDrawableCompat
                    fabAnim.start()
                }

                Transition.EDIT_DETAIL -> binding.fab.apply {
                    val animCheckToEdit = AnimatedVectorDrawableCompat.create(
                        this.context,
                        R.drawable.avd_check_to_edit
                    )
                    setImageDrawable(animCheckToEdit)
                    val fabAnim = this.drawable as AnimatedVectorDrawableCompat
                    fabAnim.start()
                }
            }
        })
    }

    private fun test() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.addListItemFragment -> {
                    binding.bottomAppBar.performHide()
                }
                R.id.listFragment -> {
                    binding.fab.apply {
                        val animAddToCheck = AnimatedVectorDrawableCompat.create(
                            this.context,
                            R.drawable.avd_add_to_check
                        )
                        setOnClickListener {
                            navController.navigate(R.id.addListItemFragment)
                            setImageDrawable(animAddToCheck)
                            val fabAnim = this.drawable as AnimatedVectorDrawableCompat
                            fabAnim.start()
                        }
                    }
                    binding.bottomAppBar.performShow()
                }
                R.id.detailFragment -> {
                    binding.fab.apply {
                        val animEditToCheck = AnimatedVectorDrawableCompat.create(
                            this.context,
                            R.drawable.avd_edit_to_check
                        )
                        setImageDrawable(animEditToCheck)
                    }
                    binding.bottomAppBar.performHide()
                }
                R.id.editItemFragment2 -> {
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
        binding.fab.apply {
            setShowMotionSpecResource(R.animator.fab_show)
            setHideMotionSpecResource(R.animator.fab_hide)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_item_detail)
        return navController.navigateUp()
                || super.onSupportNavigateUp()
    }
}