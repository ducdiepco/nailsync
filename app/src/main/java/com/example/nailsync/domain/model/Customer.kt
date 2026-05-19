package com.example.nailsync.domain.model

data class Customer(
    val id: Int = 0,
    val firstName: String,
    val lastName: String,
    val phone: String = "",
    val email: String = "",
    val dateOfBirth: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    val fullName: String get() = "$firstName $lastName".trim()
}
