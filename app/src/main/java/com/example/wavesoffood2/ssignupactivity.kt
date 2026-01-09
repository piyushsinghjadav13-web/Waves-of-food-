package com.example.wavesoffood2

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.wavesoffood2.databinding.ActivitySsignupactivityBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase

class Ssignupactivity : AppCompatActivity() {

    private val binding: ActivitySsignupactivityBinding by lazy {
        ActivitySsignupactivityBinding.inflate(layoutInflater)
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        // ⭐ AUTO LOGIN CHECK (Already logged in → MainActivity)
        if (auth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()

        // ---------------- GOOGLE SIGN-IN SETUP ----------------
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // ---------------- GOOGLE SIGN-IN BUTTON ----------------
        binding.googleSignupBtn.setOnClickListener {

            // To always show account chooser popup
            googleSignInClient.signOut().addOnCompleteListener {
                val intent = googleSignInClient.signInIntent
                googleLauncher.launch(intent)
            }
        }

        // ---------------- ALREADY HAVE ACCOUNT → LoginActivity ----------------
        binding.aleradyhaveaccount.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        // ---------------- NORMAL EMAIL SIGNUP ----------------
        binding.account.setOnClickListener {
            val name = binding.name1.text.toString()
            val email = binding.editTextTextEmailAddress2.text.toString()
            val password = binding.editTextTextPassword2.text.toString()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        val uid = auth.currentUser?.uid ?: ""
                        val userData = HashMap<String, Any>()
                        userData["name"] = name
                        userData["email"] = email
                        userData["uid"] = uid

                        database.reference.child("users")
                            .child(uid)
                            .setValue(userData)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Signup Successful!", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, MainActivity::class.java))
                                finish()
                            }

                    } else {
                        Toast.makeText(
                            this,
                            "Signup Failed: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    // ---------------- GOOGLE SIGN-IN RESULT HANDLER ----------------
    private val googleLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

            if (task.isSuccessful) {
                val account = task.result
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)

                auth.signInWithCredential(credential)
                    .addOnCompleteListener { authTask ->
                        if (authTask.isSuccessful) {

                            val user = auth.currentUser
                            val uid = user?.uid ?: ""

                            // Save user data in database only first time
                            val userData = HashMap<String, Any>()
                            userData["name"] = user?.displayName ?: ""
                            userData["email"] = user?.email ?: ""
                            userData["uid"] = uid

                            database.reference.child("users")
                                .child(uid)
                                .setValue(userData)

                            Toast.makeText(this, "Google Signup Successful!", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()

                        } else {
                            Toast.makeText(this, "Google Sign-In Failed!", Toast.LENGTH_SHORT).show()
                        }
                    }

            } else {
                Toast.makeText(this, "Google Sign-In Failed!", Toast.LENGTH_SHORT).show()
            }
        }
}
