package com.aki.realestatemanagerv2.ui.bottomFragment

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RadioButton
import androidx.core.view.isEmpty
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.sqlite.db.SimpleSQLiteQuery
import com.aki.realestatemanagerv2.EstateApplication
import com.aki.realestatemanagerv2.Utils
import com.aki.realestatemanagerv2.database.entities.relations.HouseAndAddress
import com.aki.realestatemanagerv2.databinding.FragmentBottomNavDrawerBinding
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt
import android.widget.ScrollView
import android.widget.Toast
import androidx.core.view.isNotEmpty
import com.aki.realestatemanagerv2.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar


class FilterFragment : Fragment() {

    private val viewModel: FilterViewModel by viewModels {
        FilterViewModelFactory((this.activity?.application as EstateApplication).repository)
    }
    private var housesAndAddresses = ArrayList<HouseAndAddress>()
    private var filteredEstates = ArrayList<HouseAndAddress>()
    private var _binding: FragmentBottomNavDrawerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomNavDrawerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getEstateList()
        binding.filterButton.setOnClickListener {
            setFilter()
            val test = requireActivity().findViewById<FrameLayout>(R.id.bottom_sheet)
            BottomSheetBehavior.from(test).state = BottomSheetBehavior.STATE_COLLAPSED
        }
        binding.removeFilterButton.setOnClickListener {
            removeFilter()
            Toast.makeText(requireContext(), "Filters removed", Toast.LENGTH_SHORT).show()
            val test = requireActivity().findViewById<FrameLayout>(R.id.bottom_sheet)
            BottomSheetBehavior.from(test).state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun getEstateList() {
        viewModel.allHousesAndAddresses.observe(viewLifecycleOwner, {
            if (it != null) {
                housesAndAddresses.clear()
                housesAndAddresses.addAll(it)
                viewModel.allHousesAndAddresses.removeObservers(viewLifecycleOwner)
                initFilterLayout()
            }
        })
    }

    private fun initFilterLayout() {
        binding.priceSlider.setLabelFormatter { value: Float ->
            val format = NumberFormat.getCurrencyInstance()
            format.maximumFractionDigits = 0
            format.currency = Currency.getInstance("USD")
            format.format(value.toDouble())
        }
        binding.sizeSlider.setLabelFormatter { value: Float ->
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
        binding.priceSlider.values = listOf(10000000.0f, 80000000.0f)
        binding.sizeSlider.values = listOf(350.0f, 2650.0f)
        binding.roomSlider.values = listOf(1f, moreRooms.toFloat())
        binding.bedroomSlider.values = listOf(1f, moreBedrooms.toFloat())
        binding.bathroomSlider.values = listOf(1f, moreBathrooms.toFloat())
        binding.roomSlider.valueTo = moreRooms.toFloat()
        binding.bedroomSlider.valueTo = moreBedrooms.toFloat()
        binding.bathroomSlider.valueTo = moreBathrooms.toFloat()
    }

    private fun removeFilter() {
        filteredEstates.clear()
        viewModel.updateFilterQuery(null)
    }

    //Preparing and showing the filter Dialog
    private fun setFilter() {
        val typeRadioButtonId = binding.filterRadiogroup.checkedRadioButtonId
        val typeRadio = binding.filterRadiogroup.findViewById<RadioButton>(typeRadioButtonId)
        val entryRadioButtonId = binding.radiogroupDateadded.checkedRadioButtonId
        val entryRadio = binding.radiogroupDateadded.findViewById<RadioButton>(entryRadioButtonId)
        val soldRadioButtonId = binding.radiogroupDatesold.checkedRadioButtonId
        val soldRadio = binding.radiogroupDatesold.findViewById<RadioButton>(soldRadioButtonId)

        var queryString = String()

        queryString += "SELECT * FROM house WHERE"
        //Price
        queryString += " price BETWEEN ${binding.priceSlider.values[0].toInt()} AND ${binding.priceSlider.values[1].toInt()}"
        //Size
        queryString += " AND size BETWEEN ${binding.sizeSlider.values[0].toInt()} AND ${binding.sizeSlider.values[1].toInt()}"
        //Rooms
        queryString += " AND nbrRooms BETWEEN ${binding.roomSlider.values[0].toInt()} AND ${binding.roomSlider.values[1].toInt()}"
        //Bedrooms
        queryString += " AND nbrBedrooms BETWEEN ${binding.bedroomSlider.values[0].toInt()} AND ${binding.bedroomSlider.values[1].toInt()}"
        //Bathrooms
        queryString += " AND nbrBathrooms BETWEEN ${binding.bathroomSlider.values[0].toInt()} AND ${binding.bathroomSlider.values[1].toInt()}"
        //Type
        if (!binding.radioAll.isChecked || typeRadio != null) {
            when (typeRadio) {
                binding.radioApartment -> {
                    queryString += " AND type = \"Apartment\""
                }
                binding.radioHouse -> {
                    queryString += " AND type = \"House\""
                }
                binding.radioMansion -> {
                    queryString += " AND type = \"Mansion\""
                }
                binding.radioVilla -> {
                    queryString += " AND type = \"Villa\""
                }
            }
        }
        //DateEntry
        if (entryRadio != null || !binding.dateaddedEditTextLayout.isEmpty()) {
            when (entryRadio) {
                binding.addedOndate -> {
                    val timestamp =
                        Utils.getTimestampFromDate(binding.dateaddedEditTextLayout.editText?.text.toString())
                    queryString += " AND dateEntryOnMarket = $timestamp"
                }
                binding.addedAfter -> {
                    val timestamp =
                        Utils.getTimestampFromDate(binding.dateaddedEditTextLayout.editText?.text.toString())
                    queryString += " AND dateEntryOnMarket >= $timestamp"
                }
                binding.addedBefore -> {
                    val timestamp =
                        Utils.getTimestampFromDate(binding.dateaddedEditTextLayout.editText?.text.toString())
                    queryString += " AND dateEntryOnMarket <= $timestamp"
                }
            }
        }
        //DateSold
        if (soldRadio != null || !binding.datesoldEditTextLayout.isEmpty()) {
             when (soldRadio) {
                binding.soldOndate -> {
                    val timestamp =
                        Utils.getTimestampFromDate(binding.dateaddedEditTextLayout.editText?.text.toString())
                    queryString += " AND dateSell = $timestamp"
                }
                binding.soldAfter -> {
                    val timestamp =
                        Utils.getTimestampFromDate(binding.dateaddedEditTextLayout.editText?.text.toString())
                    queryString += " AND dateSell >= $timestamp"
                }
                binding.soldBefore -> {
                    val timestamp =
                        Utils.getTimestampFromDate(binding.dateaddedEditTextLayout.editText?.text.toString())
                    queryString += " AND dateSell <= $timestamp"
                }
            }
        }
        //Picture Number
        if (binding.picturesEditTextLayout.editText?.text?.isEmpty() == false) {
            queryString += " AND nbrPic >= ${
                binding.picturesEditTextLayout.editText?.text.toString().toInt()
            }"
        }
        //Points of interest
        if (binding.chipPark.isChecked) queryString += " AND parkAround = 1"
        if (binding.chipSchool.isChecked) queryString += " AND schoolAround = 1"
        if (binding.chipRestaurant.isChecked) queryString += " AND restaurantAround = 1"
        if (binding.chipShop.isChecked) queryString += " AND shopAround = 1"
        if (binding.chipMuseum.isChecked) queryString += " AND museumAround = 1"
        if (binding.chipPool.isChecked) queryString += " AND publicPoolAround = 1"

        Log.d(TAG, "setFilter: HERE -> $queryString")
        val query = SimpleSQLiteQuery(queryString)

        viewModel.updateFilterQuery(query)
    }
}