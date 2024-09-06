package com.example.mobilt_java23_oliver_nilsson_lifecycle_v4

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register_page)

        auth = FirebaseAuth.getInstance()

        val userEmail = findViewById<EditText>(R.id.registerEmailInput)
        val userPassword = findViewById<EditText>(R.id.registerPasswordInput)
        val registerButton = findViewById<Button>(R.id.registerButton)
        val goBackButton = findViewById<Button>(R.id.goBackButton)

        goBackButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        registerButton.setOnClickListener {
            val email = userEmail.text.toString()
            val password = userPassword.text.toString()

            // Kontrollerar så fälten inte är tomma vid registreringen
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Input your email and password", Toast.LENGTH_SHORT).show()
            } else {
                registerUser(email, password)
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // Registreringsfuktion där jag sparar datan i "users" med hjälp av hash
    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("Oliver", "createUserWithEmail:success")

                    val db = FirebaseFirestore.getInstance()

                    val userId = auth.currentUser?.uid

                    val userData = hashMapOf(
                        "email" to email,
                        "userId" to userId
                    )


                    if (userId != null) {
                        Log.d("Oliver", "User ID: $userId")

                        db.collection("users").document(userId)
                            .set(userData)
                            .addOnSuccessListener {
                                Log.d("Oliver", "User data saved successfully")

                                val intent = Intent(this, InformationActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Log.w("Oliver", "Error saving user data", e)
                                Toast.makeText(this, "Failed to save user data", Toast.LENGTH_SHORT)
                                    .show()
                            }
                    }
                } else {
                    Log.w("Oliver", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}