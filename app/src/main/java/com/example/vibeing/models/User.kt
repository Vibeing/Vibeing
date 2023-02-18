package com.example.vibeing.models

@Suppress("unused")
class User(val fullName: String, val email: String, val gender: String, val dob: String) {
    constructor() : this("", "", "", "")

    var profilePic = ""
    var coverPic = ""
    var bio = ""
}