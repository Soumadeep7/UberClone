package com.source.uberclone.ui

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.source.uberclone.R
import com.source.uberclone.SplashScreenActivity
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import android.content.Intent
import android.widget.TextView
import androidx.navigation.NavController
import com.source.uberclone.databinding.ActivityHomeBinding
import com.source.uberclone.utils.Constants

class HomeActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityHomeBinding
    private lateinit var navView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarHome.toolbar)

        binding.appBarHome.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .setAnchorView(binding.appBarHome.fab)
                .show()
        }

        drawerLayout = binding.drawerLayout
        navView = binding.navView
        init()

        val navController = findNavController(R.id.nav_host_fragment_content_home)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_gallery,
                R.id.nav_slideshow
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        init()

    }

    private fun init() {
        navView.setNavigationItemSelectedListener { it ->
            val  builder = AlertDialog.Builder(this@HomeActivity)
            builder.setTitle("Are you sure you want to exit?")
                .setNegativeButton("No"){dialogue, _ -> dialogue.dismiss()}
                .setPositiveButton("SIGN OUT") { dialogue, _ ->
                    finish()
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this@HomeActivity, SplashScreenActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }.setCancelable(false)

            val dialog = builder.create()
            dialog.setOnShowListener {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setTextColor(resources.getColor(android.R.color.holo_red_dark))
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                    .setTextColor(resources.getColor(R.color.colorAccent))
            }
            dialog.show()
            true
        }
        val headerView = navView.getHeaderView(0)
        val textName = headerView.findViewById<TextView>(R.id.text_view_name)
        val textViewStar = headerView.findViewById<TextView>(R.id.text_view_rating)
        val textPhone = headerView.findViewById<TextView>(R.id.text_view_phone)

        textName.text = Constants.buildWelcomeMessage()
        textViewStar.text = java.lang.String.valueOf(Constants.currentUser?.rating)
        textPhone.text = Constants.currentUser?.phoneNumber


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.home, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_home)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
