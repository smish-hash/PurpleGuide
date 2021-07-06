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
import com.example.purpleguide.Models.UserModel
import com.example.purpleguide.R
import com.example.purpleguide.databinding.FragmentSignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class SignUpFragment : Fragment() {
    private lateinit var binding: FragmentSignUpBinding
    private lateinit var navController: NavController

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private val TAG = "signup"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        val view = binding.root

        auth = Firebase.auth
        firestore = FirebaseFirestore.getInstance()

        binding.tvLogin.setOnClickListener {
            navController.navigate(R.id.action_signUpFragment_to_loginFragment)
        }

        binding.btnSignup.setOnClickListener {
            if (!binding.emailET.text.toString()
                    .equals("") && !binding.passwordET.text.toString()
                    .equals("") && !binding.userNameET.text.toString().equals("")
            ) {
                Toast.makeText(context, "Signing up", Toast.LENGTH_SHORT).show()
                signUp()
            } else {
                Toast.makeText(context, "Check all fields", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        val currrentUser = auth.currentUser
        if (currrentUser != null) {
            val intent = Intent(context, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun signUp() {
        val email = binding.emailET.text.toString().trim()
        val password = binding.passwordET.text.toString().trim()
        val username = binding.userNameET.text.toString().trim()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
//                    Sign up success
                    val documentReference = firestore.collection("Users")
                        .document(auth.currentUser!!.uid)
                    val user = UserModel(auth.currentUser!!.uid, username, null)
                    documentReference.set(user).addOnCompleteListener {
                        Toast.makeText(context, "Welcome: " + username, Toast.LENGTH_SHORT).show()
                        val intent = Intent(context, MainActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    }

                } else {
                    // If sign in fails, display a message to the user.
                    Log.d(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        context, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
    }

}