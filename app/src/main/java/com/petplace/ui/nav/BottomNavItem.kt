package com.petplace.ui.nav
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

    sealed class BottomNavItem(
        val title: String,
        val icon: ImageVector,
        val route: Route)
    {
        data object HomeButton :
            BottomNavItem("Início", Icons.Default.Search, Route.Home)

        data object MapButton :
            BottomNavItem("Mapa", Icons.Default.Map, Route.Map)

//        data object ProfileButton :
//            BottomNavItem("Perfil", Icons.Default.Person, Route.Profile)
//        data object PetsButton  :
//            BottomNavItem("Pets", Icons.Default.Pets, Route.Pets)

        data object HostingButton  :
            BottomNavItem("Hospedagens", Icons.Default.Home, Route.Hosting)
        data object BookingButton  :
            BottomNavItem("Reservas", Icons.Default.Book, Route.Booking)
    }
