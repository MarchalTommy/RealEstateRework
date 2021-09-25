package com.aki.realestatemanagerv2.ui.detail

import android.Manifest
import android.content.ContentValues.TAG
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.aki.realestatemanagerv2.EstateApplication
import com.aki.realestatemanagerv2.R
import com.aki.realestatemanagerv2.Utils
import com.aki.realestatemanagerv2.database.entities.Address
import com.aki.realestatemanagerv2.database.entities.Agent
import com.aki.realestatemanagerv2.database.entities.House
import com.aki.realestatemanagerv2.database.entities.Picture
import com.aki.realestatemanagerv2.databinding.FragmentItemDetailBinding
import com.araujo.jordan.excuseme.ExcuseMe
import com.bumptech.glide.Glide
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailFragment : Fragment() {

    private val viewModel: DetailViewModel by viewModels {
        DetailViewModelFactory((this.activity?.application as EstateApplication).repository)
    }
    private val args: DetailFragmentArgs by navArgs()
    private var houseId = 0
    private lateinit var mHouse: House
    private var address: Address? = null
    private var agent: Agent? = null
    private lateinit var navController: NavController
    private var _binding: FragmentItemDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentItemDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (Utils.isTablet(requireContext())) {
            val bottomBar = requireActivity().findViewById<BottomAppBar>(R.id.bottom_app_bar)
            bottomBar.menu.getItem(0).setOnMenuItemClickListener {
                ExcuseMe.couldYouGive(this).permissionFor(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) { permissionStatus ->
                    if (permissionStatus.granted.contains(Manifest.permission.ACCESS_FINE_LOCATION) &&
                        permissionStatus.granted.contains(Manifest.permission.ACCESS_COARSE_LOCATION)
                    ) {
                        val action =
                            DetailFragmentDirections.actionDetailFragmentToMapFragment2(true)
                        findNavController().navigate(action)
                    } else {
                        findNavController().navigate(R.id.mapFragment2)
                    }
                }
                true
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = this.findNavController()
        houseId = args.houseId
        if (Utils.isTablet(requireContext())) {
            requireActivity().onBackPressedDispatcher.addCallback(this) {
                houseId = 0
                binding.emptyLayout?.visibility = View.VISIBLE
                binding.fabDetail.visibility = View.GONE
                this.isEnabled = true
            }
        } else {
            requireActivity().onBackPressedDispatcher.addCallback(this) {
                navController.navigate(R.id.action_detailFragment_to_listFragment)
                this.isEnabled = true
            }
        }
        if (houseId != 0) {
            getDBData()
            viewModel.setSelectedHouseId(houseId)
        } else {
            getHouseId()
        }
        binding.editButton?.setOnClickListener {
            this.findNavController().navigate(R.id.editItemFragment)
        }
    }

    private fun getHouseId() {
        viewModel.getSelectedHouseId().observe(viewLifecycleOwner, {
            if (it != null) {
                houseId = it
                when (houseId) {
                    0 -> {
                        binding.emptyLayout?.visibility = View.VISIBLE
                        binding.fabDetail.visibility = View.GONE
                    }
                    else -> {
                        binding.emptyLayout?.visibility = View.GONE
                        binding.fabDetail.visibility = View.VISIBLE
                        getDBData()
                        viewModel.getSelectedHouseId().removeObservers(viewLifecycleOwner)
                    }
                }
            }
        })
    }

    private fun initLayout() {
        binding.detailDescription.text = mHouse.description
        binding.detailAddress.text = address.toString()
        binding.detailType.text = mHouse.type
        binding.detailPrice.text = mHouse.currencyFormatUS()
    }

    private fun finishLayout() {
        binding.detailAgent.text = agent!!.toString()
        val dateFormatted = Utils.getDateFromTimestamp(mHouse.dateEntryOnMarket)
        Log.d(TAG, "finishLayout: UNIX -> ${mHouse.dateEntryOnMarket}")
        Log.d(TAG, "finishLayout: DATE -> $dateFormatted")
        binding.detailDateAdded.text = "Added on ${dateFormatted}"
    }

    private fun getDrawables(): List<Drawable> {
        val sizeDrawable = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_surface)
        val roomDrawable = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_room)
        val bedroomDrawable = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_bedroom)
        val bathroomDrawable = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_bathroom)
        return listOf(sizeDrawable!!, roomDrawable!!, bedroomDrawable!!, bathroomDrawable!!)
    }

    private fun getDataSet(): List<Int> {
        val size = mHouse.size
        val nbrRooms = mHouse.nbrRooms
        val nbrBedrooms = mHouse.nbrBedrooms
        val nbrBathrooms = mHouse.nbrBathrooms
        return listOf(size, nbrRooms, nbrBedrooms, nbrBathrooms)
    }

    private fun getDBData() {
        viewModel.getHouseAndAddress(houseId).observe(viewLifecycleOwner, {
            if (it != null) {
                binding.emptyLayout?.visibility = View.GONE
                binding.fabDetail.visibility = View.VISIBLE
                mHouse = it.house
                address = it.address
                fabStaticMap()
                viewModel.getAgent(mHouse.agentId)
                    .observe(viewLifecycleOwner, { thisAgent ->
                        if (thisAgent != null) {
                            agent = thisAgent
                            initLayout()
                            finishLayout()
                        }
                    })
                initDataRecyclerView()
            } else {
                binding.emptyLayout?.visibility = View.VISIBLE
                binding.fabDetail.visibility = View.GONE
            }
        })

        viewModel.getPictures(houseId).observe(viewLifecycleOwner, {
            if (it != null) {
                initMediaRecyclerView(it)
            }
        })
    }

    private fun initMediaRecyclerView(dataSet: List<Picture>) {
        binding.detailMediaRv.adapter = PictureListAdapter(dataSet)
        binding.detailMediaRv.setHasFixedSize(true)
        binding.detailMediaRv.onFlingListener = null
        val snapHelper: SnapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.detailMediaRv)
    }

    private fun initDataRecyclerView() {
        binding.detailDataRv.adapter = DataDetailAdapter(getDataSet(), getDrawables())
        binding.detailDataRv.setHasFixedSize(true)
        binding.detailDataRv.onFlingListener = null
        val snapHelper: SnapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.detailDataRv)
    }

    private fun fabStaticMap() {
        val staticMapDialog = MaterialAlertDialogBuilder(
            requireContext(),
            R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_Centered
        )
            .setCancelable(true)
            .setView(R.layout.dialog_location)
            .create()

        staticMapDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val addressUrl = address?.toUrlReadyString()
        Log.d(TAG, "fabStaticMap: TEST ADDRESS :$addressUrl")
        val api = resources.getString(R.string.GOOGLE_MAPS_API_KEY)
        fun connectionIsAvailable() {
            val imageView = staticMapDialog.findViewById<ImageView>(R.id.static_map_view)
            Glide.with(requireContext())
                .load(viewModel.getStaticMap(addressUrl!!, api))
                .centerCrop()
                .into(imageView!!)
        }

        fun connectionUnavailable() {
            Snackbar.make(
                binding.root,
                "No internet connection available! Please verify that you have access to a network, or try again later.",
                Snackbar.LENGTH_LONG
            ).setAnchorView(R.id.bottom_app_bar).show()
        }

        binding.fabDetail.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                if (Utils.isOnline()) {
                    lifecycleScope.launch(Dispatchers.Main) {
                        connectionIsAvailable()
                    }
                } else {
                    lifecycleScope.launch(Dispatchers.Main) {
                        connectionUnavailable()
                    }
                }
            }
            staticMapDialog.show()
            val poi = staticMapDialog.findViewById<MaterialTextView>(R.id.poi)
            val poiText = StringBuilder()

            poiText.append(if (mHouse.schoolAround) "School\n" else "")
            poiText.append(if (mHouse.parkAround) "Park\n" else "")
            poiText.append(if (mHouse.museumAround) "Museum\n" else "")
            poiText.append(if (mHouse.shopAround) "Shop\n" else "")
            poiText.append(if (mHouse.publicPoolAround) "Public Swimming Pool\n" else "")
            poiText.append(if (mHouse.restaurantAround) "Square\n" else "")
            poi?.text = poiText.toString()
        }
    }
}