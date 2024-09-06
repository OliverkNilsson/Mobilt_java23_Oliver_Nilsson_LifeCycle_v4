package com.example.mobilt_java23_oliver_nilsson_lifecycle_v4

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class InformationActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_information)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        val saveButton = findViewById<Button>(R.id.saveButton)
        val menuButton = findViewById<Button>(R.id.menuButton)
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        loadUserData(userId)

        saveButton.setOnClickListener {
            saveUserData(userId)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // funktion där jag laddar in all data från databasen och fyller i alla fält med denna data
    private fun loadUserData(userId:String) {

        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val firstName = document.getString("firstName")
                    val lastName = document.getString("lastName")
                    val email = document.getString("email")
                    val phone = document.getString("phone")
                    val birthday = document.getString("birthday")
                    val hasDriversLicense = document.getBoolean("driversLicense") ?: false
                    val gender = document.getString("gender")

                    findViewById<EditText>(R.id.nameInput).setText(firstName)
                    findViewById<EditText>(R.id.lastnameInput).setText(lastName)
                    findViewById<EditText>(R.id.emailInput).setText(email)
                    findViewById<EditText>(R.id.phoneInput).setText(phone)
                    findViewById<EditText>(R.id.birthdayInput).setText(birthday)
                    findViewById<CheckBox>(R.id.driverslicenceCheck).isChecked = hasDriversLicense

                    when (gender) {
                        "Female" -> findViewById<RadioButton>(R.id.femaleRadioButton).isChecked = true
                        "Male" -> findViewById<RadioButton>(R.id.maleRadioButton).isChecked = true
                        "Other" -> findViewById<RadioButton>(R.id.otherRadioButton).isChecked = true
                    }
                } else {
                    Log.d("Firestore", "No data found for user")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("Firestore", "Error getting documents: ", exception)
            }

    }

    private fun saveUserData(userId:String) {

        val firstName = findViewById<EditText>(R.id.nameInput).text.toString()
        val lastName = findViewById<EditText>(R.id.lastnameInput).text.toString()
        val email = findViewById<EditText>(R.id.emailInput).text.toString()
        val phone = findViewById<EditText>(R.id.phoneInput).text.toString()
        val birthday = findViewById<EditText>(R.id.birthdayInput).text.toString()
        val hasDriversLicense = findViewById<CheckBox>(R.id.driverslicenceCheck).isChecked

        val gender = when {
            findViewById<RadioButton>(R.id.femaleRadioButton).isChecked -> "Female"
            findViewById<RadioButton>(R.id.maleRadioButton).isChecked -> "Male"
            else -> "Other"
        }

        val userData = hashMapOf(
            "firstName" to firstName,
            "lastName" to lastName,
            "email" to email,
            "phone" to phone,
            "birthday" to birthday,
            "driversLicense" to hasDriversLicense,
            "gender" to gender
        )

        db.collection("users").document(userId).set(userData)
            .addOnSuccessListener {
                Log.d("Oliver", "User data successfully saved!")
                Toast.makeText(this, "Data saved successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.w("Oliver", "Error saving user data", e)
                Toast.makeText(this, "Failed to save data", Toast.LENGTH_SHORT).show()
            }
    }
}