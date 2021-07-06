package com.example.purpleguide.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.purpleguide.MainActivity
import com.example.purpleguide.R
import com.example.purpleguide.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase


class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var navController: NavController
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private val TAG = "login"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root

        auth = Firebase.auth
        firestore = FirebaseFirestore.getInstance()

        binding.tvSignUp.setOnClickListener {
            navController.navigate(R.id.action_loginFragment_to_signUpFragment)
        }

        binding.btnLogin.setOnClickListener {
            if (!binding.emailET.text.toString()
                    .equals("") && !binding.passwordET.text.toString().equals("")
            ) {
                Toast.makeText(context, "Logging in", Toast.LENGTH_SHORT).show()
                signin()
            } else {
                Toast.makeText(context, "Check all fields", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(context, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun signin() {
        val email = binding.emailET.text.toString().trim()
        val password = binding.passwordET.text.toString().trim()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    val documentReference = firestore.collection("Users")
                        .document(auth.currentUser!!.uid)
                    documentReference.get().addOnSuccessListener { document ->
                        if (document != null) {
                            val username = document.getString("userName")
                            Toast.makeText(context, "Welcome: " + username, Toast.LENGTH_SHORT).show()
                            val intent = Intent(context, MainActivity::class.java)
                            startActivity(intent)
                            requireActivity().finish()
                        }
                    }
                } else {
                    // If log in fails, display a message to the user.
                    Log.d(TAG, "loginWithEmail:failure", task.exception)
                    Toast.makeText(context, "Login failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
    }
}