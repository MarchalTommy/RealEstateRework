package com.aki.realestatemanagerv2.ui.mainList

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aki.realestatemanagerv2.EstateApplication
import com.aki.realestatemanagerv2.R
import com.aki.realestatemanagerv2.Utils
import com.aki.realestatemanagerv2.database.entities.House
import com.aki.realestatemanagerv2.database.entities.relations.HouseAndAddress
import com.aki.realestatemanagerv2.databinding.FragmentItemListBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar

class ListFragment : Fragment() {

    private val viewModel: ListViewModel by viewModels {
        ListViewModelFactory((this.activity?.application as EstateApplication).repository)
    }

    private lateinit var navController: NavController
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ListFragmentAdapter
    private lateinit var thisContext: Context
    private var housesAndAddresses = ArrayList<HouseAndAddress>()
    private var filteredEstates = ArrayList<HouseAndAddress>()
    private var isFiltered = false
    private var _binding: FragmentItemListBinding? = null
    private val binding get() = _binding!!
    private var itemDetailFragmentContainer: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentItemListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        navController = requireActivity().findNavController(R.id.nav_host_fragment_item_detail)

        val mainFab = requireActivity().findViewById<FloatingActionButton>(R.id.fab)
        mainFab.setOnClickListener {
            navController.navigate(R.id.addListItemFragment)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.loadingList.isIndeterminate = true
        binding.loadingList.visibility = View.VISIBLE
        thisContext = this.requireContext()
        recyclerView = binding.itemList

        itemDetailFragmentContainer = view.findViewById(R.id.item_detail_nav_container)

        for (estate in housesAndAddresses) {
            viewModel.updateHouse(estate.house)
        }
        getLocalHouses()
        setItemTouchHelper()
        initFilters()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    //Getting Room data
    private fun getLocalHouses() {
        Log.d(TAG, "getLocalHouses: Room call started, now fetching houses...")
        viewModel.allHousesAndAddresses.observe(viewLifecycleOwner, {
            for (estate in it) {
                if (estate.house.price == 0) {
                    viewModel.removeHouse(estate.house)
                    estate.address.let { it1 -> viewModel.removeAddress(it1) }
                }
            }
            housesAndAddresses.clear()
            housesAndAddresses.addAll(it)
            prepareAdapter(housesAndAddresses)
            binding.loadingList.visibility = View.GONE
        })
    }

    //Setting the recyclerView with an ItemTouchHelper for the swipe
    private fun prepareAdapter(dataSet: List<HouseAndAddress>) {
        adapter = ListFragmentAdapter(dataSet, ::listOnClick)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(thisContext)
        recyclerView.setHasFixedSize(true)
    }

    private fun setItemTouchHelper() {
        //Swipe on item to sold or un-sold it
        val itemTouchHelperCallback =
            object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    val estate: House = adapter.dataSet[position].house
                    if (direction == ItemTouchHelper.RIGHT) {
                        estate.stillAvailable = false
                        estate.dateSell = Utils.getTimestampFromDate(Utils.getTodayDate())
                        adapter.notifyItemChanged(position)
                        recyclerView.scrollToPosition(position)
                        Snackbar.make(
                            binding.root,
                            "Estate sold successfully.",
                            Snackbar.LENGTH_LONG
                        ).apply {
                            setAction("UNDO") {
                                estate.stillAvailable = true
                                estate.dateSell = 0L
                                adapter.notifyItemChanged(position)
                                recyclerView.scrollToPosition(position)
                            }
                            setActionTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.blue_700
                                )
                            )
                        }.setAnchorView(R.id.bottom_app_bar)
                            .show()
                    } else {
                        estate.stillAvailable = true
                        estate.dateSell = 0L
                        adapter.notifyItemChanged(position)
                        recyclerView.scrollToPosition(position)
                        Snackbar.make(
                            binding.root,
                            "Estate successfully marked as not sold.",
                            Snackbar.LENGTH_LONG
                        ).setAnchorView(R.id.bottom_app_bar).show()
                    }
                    viewModel.updateHouse(estate)
                }
            }
        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(recyclerView)
        }
    }

    private fun initFilters() {
        viewModel.getFilterQuery().observe(viewLifecycleOwner, {
            if (it != null) {
                viewModel.filterList(it).observe(viewLifecycleOwner, { filteredList ->
                    if (filteredList != null) {
                        isFiltered = true
                        filteredEstates = filteredList as ArrayList<HouseAndAddress>
                        if (filteredEstates.isNotEmpty()) {
                            prepareAdapter(filteredEstates)
                        } else {
                            Snackbar.make(
                                requireView(),
                                "No estate found matching your filters.",
                                LENGTH_LONG
                            ).setAnchorView(R.id.bottom_app_bar)
                                .show()
                        }
                    }
                })
            } else {
                prepareAdapter(housesAndAddresses)
            }
        })
    }

    //OnClick for the items of the recyclerView
    private fun listOnClick(houseId: Int) {
        if (itemDetailFragmentContainer != null) {
            viewModel.setSelectedHouseId(houseId)
            itemDetailFragmentContainer!!.findNavController()
                .navigate(R.id.detailFragment)
        } else {
            viewModel.setSelectedHouseId(houseId)
            navController.navigate(R.id.action_listFragment_to_detailFragment)
        }
    }
}