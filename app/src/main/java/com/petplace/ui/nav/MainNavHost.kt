package com.petplace.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.petplace.MainViewModel
import com.petplace.ui.BookingsPage
import com.petplace.ui.EditPetProfile
import com.petplace.ui.EditProfilePage
import com.petplace.ui.FavoritePage
import com.petplace.ui.HomePage
import com.petplace.ui.HostingDescriptionPage
import com.petplace.ui.host.HostingPage
import com.petplace.ui.MapPage
import com.petplace.ui.PetProfilePage
import com.petplace.ui.PetsPage
import com.petplace.ui.ProfilePage
import com.petplace.ui.RegisterPet
import com.petplace.ui.BookingsPage
import com.petplace.ui.host.BookingsPageHost
import com.petplace.ui.host.EditHostingPage
import com.petplace.ui.host.HostingDescriptionPageHost
import com.petplace.ui.host.RegisterHosting

@Composable
fun MainNavHost(navController: NavHostController,viewModel: MainViewModel) {
    NavHost(navController, startDestination = Route.Home) {
        composable<Route.Home> { HomePage(viewModel = viewModel, navController = navController) }
        composable<Route.Map> { MapPage(viewModel = viewModel, navController = navController) }
        composable<Route.Favorite> { FavoritePage(viewModel = viewModel, navController = navController) }

        composable<Route.Profile> {
            ProfilePage(
                isUserHost = viewModel.isHostMode,
                viewModel = viewModel,
                navController = navController
            )
        }
        composable<Route.Pets> { PetsPage(viewModel = viewModel, navController = navController) }
        composable<Route.Hostings> { HostingPage(viewModel = viewModel, navController = navController) }
        composable<Route.Booking> { BookingsPage(viewModel = viewModel) }
        composable<Route.EditProfile> {
            EditProfilePage(navController = navController, viewModel = viewModel)
        }
        composable<Route.HostingDescription> { backStackEntry ->
            val route: Route.HostingDescription = backStackEntry.toRoute()
            viewModel.selectHostingById(route.id)
            HostingDescriptionPage(viewModel = viewModel, navController = navController)
        }

        composable<Route.HostingDescriptionHost> { backStackEntry ->
            val route: Route.HostingDescriptionHost = backStackEntry.toRoute()
            viewModel.selectHostingById(route.id)
            HostingDescriptionPageHost(viewModel = viewModel, navController = navController)
        }

        composable<Route.EditHosting> {
            EditHostingPage(navController = navController, viewModel = viewModel)
        }

        composable<Route.RegisterPet> {
            RegisterPet(navController = navController, viewModel = viewModel)
        }
        composable<Route.PetProfilePage> { backStackEntry ->
            val route: Route.PetProfilePage = backStackEntry.toRoute()

            PetProfilePage(
                navController = navController,
                viewModel = viewModel,
                petId = route.id
            )
        }

        composable<Route.EditPetProfile> { backStackEntry ->
            val route: Route.EditPetProfile = backStackEntry.toRoute()

            EditPetProfile(
                navController = navController,
                viewModel = viewModel,
                petId = route.id
            )
        }


        composable<Route.BookingHost> { BookingsPageHost(viewModel = viewModel, navController = navController) }//TODO
        composable<Route.RegisterHosting> { RegisterHosting(viewModel = viewModel, navController = navController) }//TODO
    }
}