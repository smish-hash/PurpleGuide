package com.example.purpleguide.Adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.purpleguide.Adapters.PlacesAdapter.ViewHolder
import com.example.purpleguide.Models.Places
import com.example.purpleguide.R
import com.example.purpleguide.databinding.LocationItemBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class PlacesAdapter(options: FirestoreRecyclerOptions<Places>, context: Context) :
    FirestoreRecyclerAdapter<Places, ViewHolder>(options) {
    private lateinit var binding: LocationItemBinding
    private var context = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = LocationItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Places) {
        holder.apply {
            name.text = model.placeName
            type.text = model.placeType
            description.text = model.placeDescription
            Glide.with(context).load(Uri.parse(model.placeImage)).into(image)
        }
    }

    inner class ViewHolder(containerView: View) : RecyclerView.ViewHolder(containerView) {
        lateinit var name: TextView
        lateinit var type: TextView
        lateinit var description: TextView
        lateinit var image: ImageView

        init {
            name = binding.tvPlaceName
            type = binding.tvPlaceType
            description = binding.tvPlaceDescription
            image = binding.ivPlaceImage
        }

    }
}