package com.petplace.ui.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.petplace.MainViewModel
import com.petplace.ui.BookingsPage
import com.petplace.ui.EditProfilePage
import com.petplace.ui.HomePage
import com.petplace.ui.HostingDescriptionPage
import com.petplace.ui.HostingPage
import com.petplace.ui.MapPage
import com.petplace.ui.PetsPage
import com.petplace.ui.ProfilePage
import com.petplace.ui.RegisterPet

@Composable
fun MainNavHost(navController: NavHostController,viewModel: MainViewModel) {
    NavHost(navController, startDestination = Route.Home) {
        composable<Route.Home> { HomePage(viewModel = viewModel, navController = navController) }
        composable<Route.Map> { MapPage(viewModel = viewModel) }
        composable<Route.Profile> {
            ProfilePage(
                viewModel = viewModel,
                navController = navController
            )
        }
        composable<Route.Pets> { PetsPage(viewModel = viewModel, navController = navController) }
        composable<Route.Hosting> { HostingPage(viewModel = viewModel) }
        composable<Route.Booking> { BookingsPage(viewModel = viewModel) }
        composable<Route.EditProfile> {
            EditProfilePage(navController = navController, viewModel = viewModel)
        }
        composable<Route.HostingDescription> { backStackEntry ->
            val route: Route.HostingDescription = backStackEntry.toRoute()
            viewModel.selectHostingById(route.id)
            HostingDescriptionPage(viewModel = viewModel, navController = navController)
        }
        composable<Route.RegisterPet> {
            RegisterPet(navController = navController, viewModel = viewModel)
        }
    }
}