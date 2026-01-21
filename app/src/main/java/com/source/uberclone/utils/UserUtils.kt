
package com.example.uberclone.utils

import android.content.Context
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.source.uberclone.utils.Constants
import android.media.session.MediaSession.Token

object UserUtils {

    fun updateuser (
        view: View?,
        updateData: Map<String, Any>
    ) {
        FirebaseDatabase.getInstance()
            .getReference(Constants.DRIVER_INFO_REFERENCE)
            .child(FirebaseAuth.getInstance().currentUser?.uid!!)
            .updateChildren(updateData)
            .addOnSuccessListener {
                Snackbar.make(view!!,"Data Updated Successfully!",Snackbar.LENGTH_LONG).show()
            }.addOnFailureListener {
                Snackbar.make(view!!,it.message!!,Snackbar.LENGTH_LONG).show()
            }
    }
    fun updateToken(context: Context, token: String) {
        val database = FirebaseDatabase.getInstance()
            .getReference(Constants.TOKEN_REFERENCE)
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .setValue(token)
            .addOnFailureListener { e ->
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }
            .addOnSuccessListener {  }

    }

}
