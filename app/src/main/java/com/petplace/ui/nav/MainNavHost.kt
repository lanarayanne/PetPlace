package com.petplace.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.petplace.ui.BookingsPage
import com.petplace.ui.HomePage
import com.petplace.ui.HostingPage
import com.petplace.ui.PetsPage
import com.petplace.ui.ProfilePage

@Composable
fun MainNavHost(navController: NavHostController) {
    NavHost(navController, startDestination = Route.Home) {
        composable<Route.Home> { HomePage()  }
        composable<Route.Profile> { ProfilePage() }
        composable<Route.Pets>  { PetsPage() }
        composable<Route.Hosting>  { HostingPage() }
        composable<Route.Booking>  { BookingsPage() }
    }
}