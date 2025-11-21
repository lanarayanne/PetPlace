package com.petplace.model

data class User (
    val name : String,
    val email : String,
    val phone : String,
    val address : String,
    val password : String,
    val pet : List<Pet>? =null,
    val hosting: List<Hosting>? =null
)