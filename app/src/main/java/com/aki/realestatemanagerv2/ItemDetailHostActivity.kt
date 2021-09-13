package com.aki.realestatemanagerv2

import android.Manifest
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.aki.realestatemanagerv2.databinding.ActivityItemDetailBinding
import com.aki.realestatemanagerv2.ui.detail.DetailFragmentDirections
import com.aki.realestatemanagerv2.ui.mainList.ListFragmentDirections
import com.aki.realestatemanagerv2.viewmodel.SharedViewModel
import com.aki.realestatemanagerv2.viewmodel.Transition
import com.araujo.jordan.excuseme.ExcuseMe
import com.google.android.material.bottomsheet.BottomSheetBehavior

class ItemDetailHostActivity : AppCompatActivity() {

    // TODO: 10/09/2021 TAKE CARE ABOUT LOANS AND MENU ICONS 

    private val sharedViewModel: SharedViewModel by viewModels()
    private lateinit var binding: ActivityItemDetailBinding
    private lateinit var navController: NavController

    private lateinit var filterLayout: View
    private lateinit var loanLayout: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_item_detail) as NavHostFragment
        navController = navHostFragment.navController

        loanLayout = findViewById(R.id.loan_layout)
        filterLayout = findViewById(R.id.filter_layout)

        setupFab()
        setupBottomBar()
        fabAnimationManagement()
        navControllerManagement()
        bottomSheetManagement()
    }

    private fun fabAnimationManagement() {
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

    private fun navControllerManagement() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.addListItemFragment -> {
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
                    sharedViewModel.estateId.observe(this, {
                        binding.fab.apply {
                            val animEditToCheck = AnimatedVectorDrawableCompat.create(
                                this.context,
                                R.drawable.avd_edit_to_check
                            )
                            setImageDrawable(animEditToCheck)
                            val action =
                                DetailFragmentDirections.actionDetailFragmentToEditItemFragment(it)
                            setOnClickListener {
                                navController.navigate(action)
                                val fabAnim = this.drawable as AnimatedVectorDrawableCompat
                                fabAnim.start()
                            }
                        }
                    })
                    binding.bottomAppBar.performHide()
                }
                R.id.editItemFragment -> {
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
                        }
                        hideBottomSheet()
                    } else if (newState == BottomSheetBehavior.STATE_EXPANDED || newState == BottomSheetBehavior.STATE_DRAGGING || newState == BottomSheetBehavior.STATE_HALF_EXPANDED) {
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
                    peekHeight = 350
                    this.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                }
            }
        }

        binding.bottomAppBar.setOnMenuItemClickListener {
            if (it.itemId == R.id.map) {
                ExcuseMe.couldYouGive(this).permissionFor(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) { permissionStatus ->
                    if (permissionStatus.granted.contains(Manifest.permission.ACCESS_FINE_LOCATION) &&
                        permissionStatus.granted.contains(Manifest.permission.ACCESS_COARSE_LOCATION)
                    ) {
                        val action = ListFragmentDirections.actionListFragmentToMapFragment(true)
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
            } else {
//                when (it.getIcon()) {
//                    ActivityCompat.getDrawable(this, R.drawable.ic_filter_24dp) -> {
//                        filterLayout.visibility = View.VISIBLE
//                        loanLayout.visibility = View.GONE
//                        it.icon = ActivityCompat.getDrawable(this, R.drawable.ic_monetization)
//                    }
//                    ActivityCompat.getDrawable(this, R.drawable.ic_monetization) -> {
//                        filterLayout.visibility = View.GONE
//                        loanLayout.visibility = View.VISIBLE
//                        it.icon = ActivityCompat.getDrawable(this, R.drawable.ic_filter_24dp)
//                    }
//                }
                // TODO: 10/09/2021 TAG ? ENUM ?
                if (it.icon == ActivityCompat.getDrawable(this, R.drawable.ic_filter_24dp)) {
                    filterLayout.visibility = View.VISIBLE
                    loanLayout.visibility = View.GONE
                    it.icon = ActivityCompat.getDrawable(this, R.drawable.ic_monetization)
                } else if (it.icon == ActivityCompat.getDrawable(
                        this,
                        R.drawable.ic_monetization
                    )
                ) {
                    filterLayout.visibility = View.GONE
                    loanLayout.visibility = View.VISIBLE
                    it.icon = ActivityCompat.getDrawable(this, R.drawable.ic_filter_24dp)
                }
            }
            true
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

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_item_detail)
        return navController.navigateUp()
                || super.onSupportNavigateUp()
    }
}