package com.source.uberclone.services

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.source.uberclone.utils.Constants
import com.example.uberclone.utils.UserUtils
import kotlin.random.Random

class MyFireBaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        if (FirebaseAuth.getInstance().currentUser != null) {
            UserUtils.updateToken(this, token)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val data = message.data
        if (data.isNotEmpty()) {
            Constants.showNotification(
                this,
                Random.nextInt(),
                data[Constants.NOTI_TITLE],
                data[Constants.NOTI_BODY],
                null
            )
        }
    }
}
