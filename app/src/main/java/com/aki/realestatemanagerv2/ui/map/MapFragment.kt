package com.aki.realestatemanagerv2.ui.map

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.aki.realestatemanagerv2.EstateApplication
import com.aki.realestatemanagerv2.R
import com.aki.realestatemanagerv2.Utils
import com.aki.realestatemanagerv2.database.entities.relations.HouseAndAddress
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MapFragment : Fragment() {

    private val args: MapFragmentArgs by navArgs()
    private var localisation = false
    private lateinit var supportMapFragment: SupportMapFragment
    private val viewModel: MapViewModel by viewModels {
        MapViewModelFactory((this.activity?.application as EstateApplication).repository)
    }
    private val housesAndAddresses = ArrayList<HouseAndAddress>()
    private lateinit var gMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        if (Utils.isTablet(requireContext())) {
            requireActivity().onBackPressedDispatcher.addCallback(this) {
                findNavController().navigate(R.id.listFragment)
                this.isEnabled = true
            }
        }

        localisation = args.localisation

        val rootView = inflater.inflate(R.layout.fragment_map_search, container, false)
        supportMapFragment = rootView.findFragment()

        initMap()

        return rootView
    }

    /*
     SuppressLint because permissions were asked at the click on the map toolbar icon
     if permissions not allowed, setMyLocationEnabled would be false
     */
    @SuppressLint("MissingPermission")
    private fun initMap() {
        supportMapFragment.getMapAsync { googleMap ->
            gMap = googleMap

            getEstateAddress()

            gMap.setMinZoomPreference(4.0f)
            gMap.setMaxZoomPreference(14.0f)
            gMap.setOnMarkerClickListener {
                for (estate in housesAndAddresses) {
                    if (it.snippet == estate.house.houseId.toString()) {
                        if (Utils.isTablet(requireContext())) {
                            viewModel.setSelectedHouseId(estate.house.houseId)
                            findNavController().navigate(R.id.detailFragment)
                        } else {
                            val action =
                                MapFragmentDirections.actionMapFragmentToDetailFragment(estate.house.houseId)
                            this.findNavController().navigate(action)
                        }
                    }
                }
                false
            }
            if (localisation) {
                googleMap.isMyLocationEnabled = localisation
            }
        }
    }

    private fun getLocationByAddress(context: Context, strAddress: String?): LatLng? {
        val coder = Geocoder(context)
        var address: List<Address>? = emptyList()
        lifecycleScope.launch(Dispatchers.IO) {
            address = coder.getFromLocationName(strAddress, 2) ?: null
        }
        return if (address?.isEmpty() == true || address == null) {
            Toast.makeText(
                requireContext(),
                "Some location hasn't been found, please make sure all addresses are correct",
                Toast.LENGTH_LONG
            ).show()
            null
        } else {
            val location = address!!.first()
            LatLng(location.latitude, location.longitude)
        }
    }

    private fun getEstateAddress() {
        viewModel.allHousesAndAddresses.observe(viewLifecycleOwner, {
            housesAndAddresses.addAll(it)
            for (estate in housesAndAddresses) {
                if (estate.house.stillAvailable) {
                    if (getLocationByAddress(
                            requireContext(),
                            "${estate.address.way}, ${estate.address.city}"
                        ) != null
                    ) {
                        val markerOption = MarkerOptions()
                        markerOption.title("AVAILABLE : ${estate.house.type}")
                            .snippet(estate.house.houseId.toString())
                            .icon(BitmapDescriptorFactory.defaultMarker(41f))
                            .position(
                                getLocationByAddress(
                                    requireContext(),
                                    "${estate.address.way}, ${estate.address.city}"
                                )!!
                            )
                        gMap.addMarker(markerOption)
                    }
                } else {
                    if (getLocationByAddress(
                            requireContext(),
                            "${estate.address.way}, ${estate.address.city}"
                        ) != null
                    ) {
                        val markerOption = MarkerOptions()
                        markerOption.title("SOLD : ${estate.house.type}")
                            .snippet(estate.house.houseId.toString())
                            .icon(BitmapDescriptorFactory.defaultMarker(189f))
                            .position(
                                getLocationByAddress(
                                    requireContext(),
                                    "${estate.address.way}, ${estate.address.city}"
                                )!!
                            )
                        gMap.addMarker(markerOption)
                    }
                }
            }
        })
    }
}