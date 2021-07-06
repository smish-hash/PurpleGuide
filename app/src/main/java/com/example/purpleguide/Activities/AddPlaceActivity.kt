package com.example.purpleguide.Activities

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.purpleguide.databinding.ActivityAddPlaceBinding
import java.io.ByteArrayOutputStream

class AddPlaceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddPlaceBinding

    private lateinit var Name: String
    private lateinit var Type: String
    private lateinit var Description: String
    private var Image: Bitmap? = null
    private var chosenImage: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPlaceBinding.inflate(layoutInflater)
        val view = binding.root

        binding.ivImage.setOnClickListener {
            selectImage()
        }

        binding.btnNext.setOnClickListener {
            if (!binding.NameET.text.toString().equals("") && !binding.typeET.text.toString()
                    .equals("")
                && !binding.DesciptionET.text.toString().equals("")
            ) {
                next()
            } else {
                Toast.makeText(this, "Fields cannot be blank", Toast.LENGTH_SHORT).show()
            }
        }


        setContentView(view)
    }

    private fun selectImage() {
        if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 2)
        } else {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 1)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 2) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, 1)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            val selected = data.data
            try {
                chosenImage = MediaStore.Images.Media.getBitmap(this.contentResolver, selected)
                binding.ivImage.setImageBitmap(chosenImage)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    private fun next() {
        Name = binding.NameET.text.toString().trim()
        Type = binding.typeET.text.toString().trim()
        Description = binding.DesciptionET.text.toString().trim()
        Image = chosenImage


        var bStream = ByteArrayOutputStream()
        if (Image != null) {
            Image!!.compress(Bitmap.CompressFormat.PNG, 100, bStream)
            var byteArray = bStream.toByteArray()

            val intent = Intent(this, MapActivity::class.java)
            intent.putExtra("Name", Name)
            intent.putExtra("Type", Type)
            intent.putExtra("Description", Description)
            intent.putExtra("Image", byteArray)
            startActivity(intent)
        } else {
            Toast.makeText(this, "Choose an image", Toast.LENGTH_SHORT).show()
        }
    }
}