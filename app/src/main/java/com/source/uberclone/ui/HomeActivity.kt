package com.source.uberclone.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.uberclone.utils.UserUtils
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.source.uberclone.R
import com.source.uberclone.SplashScreenActivity
import com.source.uberclone.databinding.ActivityHomeBinding
import com.source.uberclone.utils.Constants
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class HomeActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityHomeBinding
    private lateinit var navView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navController: NavController

    private lateinit var imageAvatar: CircleImageView
    private lateinit var getResult: ActivityResultLauncher<Intent>
    private lateinit var imageUri: Uri

    private lateinit var waitingDialog: AlertDialog
    private lateinit var storageReference: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarHome.toolbar)

        drawerLayout = binding.drawerLayout
        navView = binding.navView

        storageReference = FirebaseStorage.getInstance().reference

        setupNavigation()
        setupHeader()
        setupImagePicker()
    }

    private fun setupNavigation() {
        navController = findNavController(R.id.nav_host_fragment_content_home)

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow),
            drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navView.setNavigationItemSelectedListener {
            showLogoutDialog()
            true
        }
    }

    private fun setupHeader() {
        waitingDialog = AlertDialog.Builder(this)
            .setMessage("Uploading...")
            .setCancelable(false)
            .create()

        val header = navView.getHeaderView(0)

        val textName = header.findViewById<TextView>(R.id.text_view_name)
        val textStar = header.findViewById<TextView>(R.id.text_view_rating)
        val textPhone = header.findViewById<TextView>(R.id.text_view_phone)
        imageAvatar = header.findViewById(R.id.profile_image)

        textName.text = Constants.buildWelcomeMessage()
        textStar.text = Constants.currentUser?.rating.toString()
        textPhone.text = Constants.currentUser?.phoneNumber

        if (!Constants.currentUser?.avatar.isNullOrEmpty()) {
            Picasso.get().load(Constants.currentUser?.avatar).into(imageAvatar)
        }

        imageAvatar.setOnClickListener { openImagePicker() }
    }

    private fun setupImagePicker() {
        getResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK && it.data != null) {
                    imageUri = it.data!!.data!!
                    imageAvatar.setImageURI(imageUri)
                    showUploadDialog()
                }
            }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        getResult.launch(intent)
    }

    private fun showUploadDialog() {
        AlertDialog.Builder(this)
            .setTitle("Change Avatar")
            .setMessage("Do you want to change avatar?")
            .setPositiveButton("CHANGE") { _, _ -> uploadAvatar() }
            .setNegativeButton("CANCEL", null)
            .show()
    }

    private fun uploadAvatar() {
        waitingDialog.show()

        val fileRef =
            storageReference.child("avatars/${FirebaseAuth.getInstance().uid}")

        fileRef.putFile(imageUri)
            .addOnSuccessListener {
                fileRef.downloadUrl.addOnSuccessListener { uri ->
                    val update = mutableMapOf<String, Any>()
                    update["avatar"] = uri.toString()
                    UserUtils.updateuser(drawerLayout, update)
                    waitingDialog.dismiss()
                }
            }
            .addOnProgressListener { snapshot ->
                val progress =
                    (100.0 * snapshot.bytesTransferred) / snapshot.totalByteCount
                waitingDialog.setMessage("Uploading: ${progress.toInt()}%")
            }
            .addOnCompleteListener {
                waitingDialog.dismiss()
            }
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Sign out?")
            .setPositiveButton("YES") { _, _ ->
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, SplashScreenActivity::class.java))
                finish()
            }
            .setNegativeButton("NO", null)
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.home, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)
    }
}
