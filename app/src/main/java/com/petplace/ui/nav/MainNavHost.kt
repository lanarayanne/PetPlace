package com.petplace.ui.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.petplace.MainViewModel
import com.petplace.ui.BookingsPage
import com.petplace.ui.HomePage
import com.petplace.ui.HostingPage
import com.petplace.ui.PetsPage
import com.petplace.ui.ProfilePage

@Composable
fun MainNavHost(navController: NavHostController,viewModel: MainViewModel) {
    NavHost(navController, startDestination = Route.Home) {
        composable<Route.Home> { HomePage(viewModel = viewModel)  }
        composable<Route.Profile> { ProfilePage(viewModel = viewModel) }
        composable<Route.Pets>  { PetsPage(viewModel = viewModel) }
        composable<Route.Hosting>  { HostingPage(viewModel = viewModel) }
        composable<Route.Booking>  { BookingsPage(viewModel = viewModel) }
    }
}