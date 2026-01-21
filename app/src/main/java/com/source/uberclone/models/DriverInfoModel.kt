package com.source.uberclone.models

data class DriverInfoModel(
    var firstName: String = "",
    var lastName: String = "",
    var phoneNumber: String = "",
    var rating: Double = 0.0,
    val avatar: String = ""
) {
    constructor() : this("", "", "", 0.0, "")
}
