package com.petplace.ui.nav

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object Home : Route

    @Serializable
    data object Map : Route

    @Serializable
    data object Profile : Route

    @Serializable
    data object Pets : Route

    @Serializable
    data object Hosting : Route

    @Serializable
    data object Booking : Route

    @Serializable
    data object EditProfile : Route
    @Serializable
    data class HostingDescription(val id: String) : Route

    @Serializable
    data object RegisterPet : Route

    @Serializable
    data object Favorite : Route
}