package com.example.purpleguide.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.purpleguide.R
import com.example.purpleguide.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)
    }
}