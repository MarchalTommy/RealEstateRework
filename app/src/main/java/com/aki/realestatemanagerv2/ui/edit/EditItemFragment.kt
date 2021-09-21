package com.aki.realestatemanagerv2.ui.edit

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment.DIRECTORY_PICTURES
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.aki.realestatemanagerv2.EstateApplication
import com.aki.realestatemanagerv2.R
import com.aki.realestatemanagerv2.database.entities.Address
import com.aki.realestatemanagerv2.database.entities.House
import com.aki.realestatemanagerv2.database.entities.Picture
import com.aki.realestatemanagerv2.databinding.FragmentEditBinding
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class EditItemFragment : Fragment() {
    private val viewModel: EditItemViewModel by viewModels {
        EditItemViewModelFactory((this.activity?.application as EstateApplication).repository)
    }
    private val args: EditItemFragmentArgs by navArgs()
    private var houseId = 0
    private lateinit var currentPhotoPath: String
    private lateinit var address: Address
    private lateinit var house: House

    //Keeping an instance of old media list in case the user cancel the modifications
    private val oldMediaList = ArrayList<Picture>()
    private val newMediaList: ArrayList<Picture> = ArrayList()
    private var _binding: FragmentEditBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditBinding.inflate(inflater, container, false)
        getHouseData()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.houseMediaRvDetail.adapter = EditMediaAdapter(newMediaList, ::removeOnClick)
        binding.houseMediaRvDetail.setHasFixedSize(true)
    }

    private fun getHouseData() {
        houseId = args.houseId
        viewModel.getHouseAndAddress(houseId).observe(viewLifecycleOwner, {
            if (it != null) {
                house = it.house
                address = it.address

                layoutInit()
            }
        })
    }

    private fun layoutInit() {
        // Pre-filling layout with estate data
        binding.bathroomsLayout.editText?.setText("${house.nbrBathrooms}")
        binding.bedroomsLayout.editText?.setText("${house.nbrBedrooms}")
        binding.roomsLayout.editText?.setText("${house.nbrRooms}")
        binding.surfaceLayout.editText?.setText("${house.size}")
        binding.descriptionLayout.editText?.setText(house.description)
        binding.locationWayLayout.editText?.setText(address.way)
        binding.locationCityLayout.editText?.setText(address.city)
        binding.locationZipLayout.editText?.setText("${address.zip}")
        binding.priceLayout.editText?.setText("${house.price}")
        binding.typeLayout.editText?.setText(house.type)

        viewModel.getPictures(houseId)
            .observe(viewLifecycleOwner, {
                oldMediaList.addAll(it)
                newMediaList.addAll(it)
                binding.houseMediaRvDetail.adapter?.notifyDataSetChanged()
            })
        binding.newMediaButton.setOnClickListener {
            val items = arrayOf("Camera", "Gallery")
            MaterialAlertDialogBuilder(
                this.requireContext(),
                R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_Centered
            )
                .setCancelable(true)
                .setTitle("Where is the photo ?")
                .setItems(items) { _, which ->
                    when (which) {
                        0 -> startCamera()
                        1 -> startGallery()
                    }
                }
                .show()
        }
        this.activity?.findViewById<FloatingActionButton>(R.id.fab)?.setOnClickListener {
            update()
        }
    }

    private fun update() {
        //Checking to add to Room added media
        val jobAdd: Job = lifecycleScope.launch(Dispatchers.IO) {
            for (media in newMediaList) {
                if (!oldMediaList.contains(media)) {
                    viewModel.insertPicture(media)
                }
            }
        }
        jobAdd.start()
        //Checking to remove from Room removed media
        val jobRemove: Job = lifecycleScope.launch(Dispatchers.IO) {
            for (media in oldMediaList) {
                if (!newMediaList.contains(media)) {
                    viewModel.removePicture(media)
                }
            }
        }
        jobRemove.start()
        //Making New mainUri from new media list
        val mainUri = if (newMediaList.isNotEmpty()) {
            newMediaList[0].uri
        } else {
            null
        }
        //House update
        house.price = "${binding.priceLayout.editText?.text}".toInt()
        house.type = "${binding.typeLayout.editText?.text}"
        house.size = "${binding.surfaceLayout.editText?.text}".toInt()
        house.nbrRooms = "${binding.roomsLayout.editText?.text}".toInt()
        house.nbrBedrooms = "${binding.bedroomsLayout.editText?.text}".toInt()
        house.nbrBathrooms = "${binding.bathroomsLayout.editText?.text}".toInt()
        house.description = "${binding.descriptionLayout.editText?.text}"
        house.mainUri = mainUri
        //Address update
        address.way = "${binding.locationWayLayout.editText?.text}"
        address.complement = "${binding.locationComplementLayout.editText?.text}"
        address.city = "${binding.locationCityLayout.editText?.text}"
        address.zip = "${binding.locationZipLayout.editText?.text}".toInt()

        CoroutineScope(Dispatchers.IO).launch {
            jobAdd.join()
            jobRemove.join()
            viewModel.updateHouse(house)
            viewModel.updateAddress(address)
        }
        runBlocking {
            jobAdd.join()
            jobRemove.join()
        }
        this.findNavController().navigateUp()
    }

    private fun removeOnClick(picture: Picture) {
        newMediaList.remove(picture)
        binding.houseMediaRvDetail.adapter?.notifyDataSetChanged()
    }

    private fun pictureListUpdate(picture: Picture) {
        newMediaList.add(picture)
        binding.houseMediaRvDetail.adapter?.notifyDataSetChanged()
    }

    // PICTURE STUFF
    // CAMERA
    private fun startCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(requireContext().packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    Toast.makeText(
                        requireContext(),
                        "An error has happened while trying to create the new file",
                        Toast.LENGTH_LONG
                    ).show()
                    null
                }
                //Continue only if the file was successfully created :
                photoFile?.also {
                    val photoUri: Uri = FileProvider.getUriForFile(
                        requireContext(),
                        "com.openclassrooms.realestatemanager.fileprovider",
                        photoFile
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                    cameraResultLauncher.launch(takePictureIntent)
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createImageFile(): File {
        val storageDir: File = requireActivity().getExternalFilesDir(DIRECTORY_PICTURES)!!
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            // Save the file : path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private fun galleryAddPic() {
        val file = File(currentPhotoPath)
        MediaScannerConnection.scanFile(requireContext(), arrayOf(file.toString()), null, null)
    }

    // GALLERY
    private fun startGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        val mimeTypes: ArrayList<String> = ArrayList(3)
        mimeTypes.add("image/jpeg")
        mimeTypes.add("image/png")
        mimeTypes.add("image/jpg")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        galleryResultLauncher.launch(intent)
    }

    // INTENTS RESULTS (onActivityResult is deprecated)
    private var galleryResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent = result.data!!
                mediaDialog(data.data!!)
            }
        }

    private var cameraResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                galleryAddPic()
                val bitmap: Bitmap = BitmapFactory.decodeFile(currentPhotoPath)
                val bytes = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
                val path: String = MediaStore.Images.Media.insertImage(
                    requireActivity().contentResolver,
                    bitmap,
                    "Title",
                    null
                )
                val uri = Uri.parse(path)
                mediaDialog(uri)
            }
        }

    // DIALOG TO VALIDATE THE PHOTO AND ADD TITLE
    private fun mediaDialog(uri: Uri) {
        val inflater: LayoutInflater = this.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.dialog_selected_media, null)
        val picTitleEditText = dialogView.findViewById<EditText>(R.id.pic_title_edit_text)
        val pic = dialogView.findViewById<ImageView>(R.id.select_pic_image_view)

        Glide.with(this)
            .load(uri)
            .into(pic)

        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle("Prepare your picture")
        dialogBuilder.setCancelable(false)
        dialogBuilder.setPositiveButton("Add this picture") { dialog, _ ->
            val newPic = Picture(uri.toString(), picTitleEditText.text.toString(), house.houseId)
            pictureListUpdate(newPic)
            dialog.dismiss()
        }
        dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        dialogBuilder.setView(dialogView)
        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }
}