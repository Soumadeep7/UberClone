package com.source.uberclone

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import android.view.View
import com.source.uberclone.utils.Constants
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import androidx.appcompat.app.AlertDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.ValueEventListener
import com.source.uberclone.models.DriverInfoModel
import com.source.uberclone.ui.HomeActivity

class SplashScreenActivity : AppCompatActivity() {

    companion object {
        private val LOGIN_REQUEST_CODE = 1510
    }

    private lateinit var providers: List<AuthUI.IdpConfig>
    private lateinit var firebaseAuthUI: FirebaseAuth
    private lateinit var listener: FirebaseAuth.AuthStateListener

    private lateinit var getResult: ActivityResultLauncher<Intent>
    private lateinit var database: FirebaseDatabase
    private lateinit var driverInfoRef: DatabaseReference
    private lateinit var progressBar: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        init()

        progressBar = findViewById(R.id.progress_bar)

        getResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == RESULT_OK) {
                Toast.makeText(this, "Login success", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        displaySplashScreen()
    }

    override fun onStop() {
        firebaseAuthUI.removeAuthStateListener(listener)
        super.onStop()
    }

    private fun displaySplashScreen() {
        firebaseAuthUI.addAuthStateListener(listener)
    }


    private fun init() {
        database = FirebaseDatabase.getInstance()
        driverInfoRef = database.getReference(Constants.DRIVER_INFO_REFERENCE)


        providers = listOf(
            AuthUI.IdpConfig.PhoneBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        firebaseAuthUI = FirebaseAuth.getInstance()

        listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                checkUserFromFireBase()
            } else {
                showLoginLayout()
            }
        }
    }

    private fun showLoginLayout() {
        val authMethodPickerLayout = AuthMethodPickerLayout.Builder(R.layout.sign_in_layout)
            .setPhoneButtonId(R.id.button_phone_sign_in)
            .setGoogleButtonId(R.id.button_google_sign_in)
            .build()

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAuthMethodPickerLayout(authMethodPickerLayout)
            .setTheme(R.style.LoginTheme)
            .setAvailableProviders(providers)
            .setIsSmartLockEnabled(false)
            .build()

        getResult.launch(signInIntent)

    }

    private fun checkUserFromFireBase() {
        driverInfoRef.
            child(FirebaseAuth.getInstance().currentUser!!.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val model = snapshot.getValue(DriverInfoModel::class.java)
                            goToHomeActivity(model)

                        } else{
                            showRegisterLayout()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@SplashScreenActivity, error.message, Toast.LENGTH_SHORT).show()

                    }

                })
        }
    private fun goToHomeActivity(model: DriverInfoModel?) {
        Constants.currentUser = model
        startActivity(Intent(this@SplashScreenActivity, HomeActivity::class.java))
        finish()
    }


    private fun showRegisterLayout(){
        val builder = AlertDialog.Builder(this, R.style.DialogTheme)
        val itemView = LayoutInflater.from(this).inflate(R.layout.register_layout, null)

        val edit_text_name = itemView.findViewById<EditText>(R.id.edit_text_first_name) as TextInputEditText
        val edit_text_last_name = itemView.findViewById<EditText>(R.id.edit_text_last_name) as TextInputEditText
        val edit_text_phone_number = itemView.findViewById<EditText>(R.id.edit_text_phone_number) as TextInputEditText

        val buttonContinue = itemView.findViewById<Button>(R.id.button_register) as Button

        if (FirebaseAuth.getInstance().currentUser!!.phoneNumber != null
            && !TextUtils.isDigitsOnly(FirebaseAuth.getInstance().currentUser!!.phoneNumber)) {
            edit_text_phone_number.setText(FirebaseAuth.getInstance().currentUser!!.phoneNumber)
        }

        builder.setView(itemView)
        val dialog = builder.create()
        dialog.show()

        buttonContinue.setOnClickListener {
            if (TextUtils.isDigitsOnly(edit_text_name.text.toString())) {
                Toast.makeText(this@SplashScreenActivity, "Please enter a First name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener


            } else if (TextUtils.isDigitsOnly(edit_text_last_name.text.toString())) {
                Toast.makeText(this@SplashScreenActivity, "Please enter a Last name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener


            } else if (TextUtils.isEmpty(edit_text_phone_number.text.toString())) {
                Toast.makeText(this@SplashScreenActivity, "Please enter a Phone number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener

            } else {
                val model = DriverInfoModel(
                    edit_text_name.text.toString(),
                    edit_text_last_name.text.toString(),
                    edit_text_phone_number.text.toString(),
                    0.0
                )

                driverInfoRef.child(FirebaseAuth.getInstance().currentUser!!.uid)
                    .setValue(model).addOnFailureListener {
                        Toast.makeText(this@SplashScreenActivity, "${it.message}", Toast.LENGTH_SHORT)
                            .show()
                            dialog.dismiss()
                    }.addOnSuccessListener {
                        Toast.makeText(this@SplashScreenActivity, "Register Successfully", Toast.LENGTH_SHORT)
                            .show()
                        dialog.dismiss()

                        goToHomeActivity(model)
                        progressBar.visibility = View.GONE
                    }
            }
        }

    }
}
