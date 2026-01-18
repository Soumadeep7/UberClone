package com.source.uberclone.utils

import com.source.uberclone.models.DriverInfoModel

object Constants {

    fun buildWelcomeMessage(): String {
        return StringBuilder("Welcome, ")
            .append(currentUser?.firstName)
            .append(" ")
            .append(currentUser?.lastName)
            .toString()
    }

    var currentUser: DriverInfoModel? = null
    const val DRIVER_INFO_REFERENCE = "DriverInfo"
}

