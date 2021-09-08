package com.aki.realestatemanagerv2.ui.mainList

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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
import com.aki.realestatemanagerv2.viewmodel.HouseViewModel
import com.aki.realestatemanagerv2.viewmodel.HouseViewModelFactory
import com.aki.realestatemanagerv2.viewmodel.SharedViewModel
import com.aki.realestatemanagerv2.viewmodel.Transition
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.slider.RangeSlider
import com.google.android.material.snackbar.Snackbar
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class ListFragment : Fragment() {

    // TODO: Créer reset filtres
    // TODO: Gérer filtres

    private val houseViewModel: HouseViewModel by viewModels {
        HouseViewModelFactory((this.activity?.application as EstateApplication).repository)
    }
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var navController: NavController
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ListFragmentAdapter
    private lateinit var thisContext: Context
    private var housesAndAddresses = ArrayList<HouseAndAddress>()
    private var filteredEstates = ArrayList<HouseAndAddress>()
    private var isFiltered = false
    private var _binding: FragmentItemListBinding? = null
    private val binding get() = _binding!!

    private lateinit var priceSlider: RangeSlider
    private lateinit var sizeSlider: RangeSlider
    private lateinit var roomSlider: RangeSlider
    private lateinit var bedroomSlider: RangeSlider
    private lateinit var bathroomSlider: RangeSlider
    private lateinit var typeRadioGroup: RadioGroup
    private lateinit var schoolChip: Chip
    private lateinit var shopChip: Chip
    private lateinit var parkChip: Chip
    private lateinit var museumChip: Chip
    private lateinit var poolChip: Chip
    private lateinit var restaurantChip: Chip
    private lateinit var dateSold: EditText
    private lateinit var dateAdded: EditText
    private lateinit var city: EditText
    private lateinit var minPic: EditText

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
        initFilterLayout()
        requireActivity().findViewById<MaterialButton>(R.id.filter_button).setOnClickListener {
            setFilter()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.loadingList.isIndeterminate = true
        binding.loadingList.visibility = View.VISIBLE
        thisContext = this.requireContext()
        recyclerView = binding.itemList

        for (estate in housesAndAddresses) {
            houseViewModel.updateHouse(estate.house)
        }
        getLocalHouses()
        setItemTouchHelper()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    //Getting Room data
    private fun getLocalHouses() {
        Log.d(TAG, "getLocalHouses: Room call started, now fetching houses...")
        houseViewModel.allHousesAndAddresses.observe(viewLifecycleOwner, {
            for (estate in it) {
                if (estate.house.price == 0) {
                    houseViewModel.removeHouse(estate.house)
                    houseViewModel.removeAddress(estate.address)
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
                        estate.dateSell = Utils.getTodayDate()
                        adapter.notifyItemChanged(position)
                        recyclerView.scrollToPosition(position)
                        Snackbar.make(
                            binding.root,
                            "Estate sold successfully.",
                            Snackbar.LENGTH_LONG
                        ).apply {
                            setAction("UNDO") {
                                estate.stillAvailable = true
                                estate.dateSell = " "
                                adapter.notifyItemChanged(position)
                                recyclerView.scrollToPosition(position)
                            }
                            setActionTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.reply_blue_700
                                )
                            )
                        }.show()
                    } else {
                        estate.stillAvailable = true
                        estate.dateSell = " "
                        adapter.notifyItemChanged(position)
                        recyclerView.scrollToPosition(position)
                        Snackbar.make(
                            binding.root,
                            "Estate successfully marked as not sold.",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    houseViewModel.updateHouse(estate)
                }
            }
        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(recyclerView)
        }
    }

    private fun filterList(dataSet: List<HouseAndAddress>) {
        adapter.dataSet = dataSet
        adapter.notifyDataSetChanged()
    }

    //OnClick for the items of the recyclerView
    private fun listOnClick(houseId: Int) {
        sharedViewModel.setIsClicked(Transition.LIST_DETAIL)
        val action = ListFragmentDirections.actionListFragmentToDetailFragment(houseId)
        navController.navigate(action)
    }


    private fun initFilterLayout() {
        priceSlider = requireActivity().findViewById(R.id.price_slider)
        sizeSlider = requireActivity().findViewById(R.id.size_slider)
        roomSlider = requireActivity().findViewById(R.id.room_slider)
        bedroomSlider = requireActivity().findViewById(R.id.bedroom_slider)
        bathroomSlider = requireActivity().findViewById(R.id.bathroom_slider)
        typeRadioGroup = requireActivity().findViewById(R.id.filter_radiogroup)
        schoolChip = requireActivity().findViewById(R.id.chip_school)
        shopChip = requireActivity().findViewById(R.id.chip_shop)
        parkChip = requireActivity().findViewById(R.id.chip_park)
        museumChip = requireActivity().findViewById(R.id.chip_museum)
        poolChip = requireActivity().findViewById(R.id.chip_pool)
        restaurantChip = requireActivity().findViewById(R.id.chip_restaurant)
        dateSold = requireActivity().findViewById(R.id.datesold_edit_text)
        dateAdded = requireActivity().findViewById(R.id.dateadded_edit_text)
        city = requireActivity().findViewById(R.id.city_edit_text)
        minPic = requireActivity().findViewById(R.id.pictures_edit_text)

        priceSlider.setLabelFormatter { value: Float ->
            val format = NumberFormat.getCurrencyInstance()
            format.maximumFractionDigits = 0
            format.currency = Currency.getInstance("USD")
            format.format(value.toDouble())
        }
        sizeSlider.setLabelFormatter { value: Float ->
            return@setLabelFormatter "${value.roundToInt()} sq m"
        }
        var moreRooms = 2
        var moreBedrooms = 2
        var moreBathrooms = 2
        for (estate in housesAndAddresses) {
            if (moreRooms <= estate.house.nbrRooms) {
                moreRooms = estate.house.nbrRooms
            }
            if (moreBedrooms <= estate.house.nbrBedrooms) {
                moreBedrooms = estate.house.nbrBedrooms
            }
            if (moreBathrooms <= estate.house.nbrBathrooms) {
                moreBathrooms = estate.house.nbrBathrooms
            }
        }
        priceSlider.values = listOf(10000000.0f, 80000000.0f)
        sizeSlider.values = listOf(350.0f, 2650.0f)
        roomSlider.values = listOf(1f, moreRooms.toFloat())
        bedroomSlider.values = listOf(1f, moreBedrooms.toFloat())
        bathroomSlider.values = listOf(1f, moreBathrooms.toFloat())
        roomSlider.valueTo = moreRooms.toFloat()
        bedroomSlider.valueTo = moreBedrooms.toFloat()
        bathroomSlider.valueTo = moreBathrooms.toFloat()
    }

    //    Preparing and showing the filter Dialog
    private fun setFilter() {
        val radioButtonId = typeRadioGroup.checkedRadioButtonId
        val radio = typeRadioGroup.findViewById<RadioButton>(radioButtonId)
        if (radio == null) {
            Toast.makeText(
                requireContext(),
                "You need to select an estate type !",
                Toast.LENGTH_LONG
            ).show()
        } else {
            houseViewModel.searchHouseAndAddress(
                priceMax = priceSlider.values[1].toInt(),
                priceMin = priceSlider.values[0].toInt(),
                sizeMax = sizeSlider.values[1].toInt(),
                sizeMin = sizeSlider.values[0].toInt(),
                roomMax = roomSlider.values[1].toInt(),
                roomMin = roomSlider.values[0].toInt(),
                bedroomMax = bedroomSlider.values[1].toInt(),
                bedroomMin = bedroomSlider.values[0].toInt(),
                bathroomMax = bathroomSlider.values[1].toInt(),
                bathroomMin = bathroomSlider.values[0].toInt(),
                type = radio.text as String,
                school = if (schoolChip.isChecked) 1 else 0,
                shop = if (shopChip.isChecked) 1 else 0,
                park = if (parkChip.isChecked) 1 else 0,
                museum = if (museumChip.isChecked) 1 else 0,
                restaurant = if (restaurantChip.isChecked) 1 else 0,
                pool = if (poolChip.isChecked) 1 else 0,
                nbrPic = if (minPic.text.toString().isEmpty()){0} else {minPic.text.toString().toInt()},
                dateEntry = dateAdded.text.toString(),
                dateSold = dateSold.text.toString()
            ).observe(viewLifecycleOwner, {
                filteredEstates.clear()
                filteredEstates.addAll(it)
                filterList(filteredEstates)
                housesAndAddresses.clear()
                housesAndAddresses.addAll(it)
                adapter.notifyDataSetChanged()
                isFiltered = true
            })
        }
    }
}