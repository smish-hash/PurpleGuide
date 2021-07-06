package com.example.purpleguide.Activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.purpleguide.MainActivity
import com.example.purpleguide.Models.Places
import com.example.purpleguide.R
import com.example.purpleguide.databinding.ActivityMapBinding

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapBinding

    var locationManager : LocationManager? = null
    var locationListener : LocationListener? = null
    var latitude = ""
    var longitude = ""

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var userID: String

    private lateinit var uploadTask: UploadTask

    private val TAG = "save"

    private lateinit var Name: String
    private lateinit var Type: String
    private lateinit var Description: String
    private lateinit var ImageDownloadUri: Uri
    private lateinit var ImageArray: ByteArray




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()
        userID = auth.uid.toString()

        var intent: Intent = getIntent()
        Name = intent.getStringExtra("Name").toString()
        Type = intent.getStringExtra("Type").toString()
        Description = intent.getStringExtra("Description").toString()
        ImageArray = intent.getByteArrayExtra("Image")!!

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_map, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.btn_save -> {
                Toast.makeText(this, "Saving Data", Toast.LENGTH_SHORT).show()
                uploadPhoto()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun uploadPhoto() {
        val storageReference = firebaseStorage.reference.child("Places/" + firestore.collection("Places").get())
        uploadTask = storageReference.putBytes(ImageArray)

        uploadTask.addOnSuccessListener {
            Toast.makeText(this, "Photo Uploaded", Toast.LENGTH_SHORT).show()
        }

        var uriTask = uploadTask.continueWithTask { task ->
            if (task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            storageReference.downloadUrl
        }.addOnCompleteListener{ task ->
            if (task.isSuccessful) {
                ImageDownloadUri = task.result!!
                saveData()
            }
        }

    }

    private fun saveData() {
        val collectionReference = firestore.collection("Places")
        val id = collectionReference.document().id

        val place = Places(userID, id, Name, Type, Description, ImageDownloadUri.toString(), latitude, longitude)
        collectionReference.document(id).set(place)
            .addOnCompleteListener {
                Toast.makeText(this, "Place Added", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setOnMapLongClickListener(myListener)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationListener=object : LocationListener{
            override fun onLocationChanged(p0: Location) {
                if(p0!=null)
                {
                    var userLocation=LatLng(p0.latitude, p0.longitude)
                    mMap.addMarker(MarkerOptions().position(userLocation).title("Your Location"))
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,17f))
                }
            }

            override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {

            }

            override fun onProviderEnabled(p0: String) {

            }

            override fun onProviderDisabled(p0: String) {

            }

        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),1)
        } else {
            locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER,2,2f,locationListener!!)
            mMap.clear()
            var lastLocation = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            var lastUserLocation = LatLng(lastLocation!!.latitude,lastLocation.longitude)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation,17f))

        }

    }

    val myListener = object : GoogleMap.OnMapLongClickListener {
        override fun onMapLongClick(p0: LatLng?) {

            mMap.clear()

            mMap.addMarker(MarkerOptions().position(p0!!).title(Name))

            latitude = p0.latitude.toString()
            longitude = p0.longitude.toString()

            Toast.makeText(this@MapActivity,"Now Save This Place!: " + Name,Toast.LENGTH_LONG).show()

        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        if (grantResults.size > 0) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER,2,2f,
                    locationListener!!
                )

            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}