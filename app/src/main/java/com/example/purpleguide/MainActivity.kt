package com.example.purpleguide

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.purpleguide.Activities.AddPlaceActivity
import com.example.purpleguide.Activities.LoginActivity
import com.example.purpleguide.Adapters.PlacesAdapter
import com.example.purpleguide.Models.Places
import com.example.purpleguide.databinding.ActivityMainBinding
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var userID: String

    private lateinit var collectionReference: CollectionReference

    private val TAG = "main"

    private lateinit var options: FirestoreRecyclerOptions<Places>
    private lateinit var adapter: PlacesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root



        auth = FirebaseAuth.getInstance()
        firestore = Firebase.firestore
        userID = auth.uid.toString()

        collectionReference = firestore.collection("Places")

        binding.btnAddPlace.setOnClickListener {
            val intent = Intent(this, AddPlaceActivity::class.java)
            startActivity(intent)
        }

        initData()

        setContentView(view)
    }

    private fun initData() {
        val query = collectionReference.orderBy("placeName")

        options = FirestoreRecyclerOptions.Builder<Places>()
            .setQuery(query, Places::class.java)
            .setLifecycleOwner(this)
            .build()

        initRecycler()
    }

    private fun initRecycler() {
        binding.placesRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = PlacesAdapter(options, this)
        binding.placesRecyclerView.adapter = adapter
        adapter.startListening()
    }

    override fun onStart() {
        super.onStart()
        if (adapter != null)
            adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        if (adapter != null)
            adapter.stopListening()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.log_out -> {
                Toast.makeText(this, "Logging Out", Toast.LENGTH_SHORT).show()
                logout()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logout() {
        Firebase.auth.signOut()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}