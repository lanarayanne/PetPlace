package com.petplace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.petplace.ui.HomePage
import com.petplace.ui.nav.BottomNavBar
import com.petplace.ui.nav.BottomNavItem
import com.petplace.ui.nav.MainNavHost
import com.petplace.ui.theme.PetPlaceTheme
import androidx.navigation.NavDestination.Companion.hasRoute
import com.petplace.db.fb.FBDatabase
import com.petplace.ui.nav.Route



class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val fbDB = remember { FBDatabase() }
            val viewModel : MainViewModel = viewModel(factory = MainViewModelFactory(fbDB))

            val navController = rememberNavController()
            // viewModel : MainViewModel by viewModels()

            val currentRoute = navController.currentBackStackEntryAsState()
            //val showButton = currentRoute.value?.destination?.hasRoute(Route.Home::class) == true
            val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission(), onResult = {} )

            PetPlaceTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {  },

                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color(0xFF419D78),
                                actionIconContentColor = Color.White,
                                titleContentColor = Color.White,
                                navigationIconContentColor = Color.White
                            ),

                            actions = {
//                                IconButton( onClick = { Firebase.auth.signOut()
                                //                                finish() ) } ) {
//                                    Icon(
//                                        imageVector =
//                                            Icons.AutoMirrored.Filled.ExitToApp,
//                                        contentDescription = "Localized description"
//                                    )
//                                }
                                IconButton( onClick = {  } ) {
                                    Icon(
                                        imageVector =
                                            Icons.Default.Menu,
                                        contentDescription = "Menu"
                                    )
                                }
//                                IconButton( onClick = {  } ) {
//                                    Icon(
//                                        imageVector =
//                                            Icons.Default.Home,
//                                        contentDescription = "Mudar para hospedeiro"
//                                    )
//                                }
                            }
                        )
                    },
                    bottomBar = {
                        val items = listOf(
                            BottomNavItem.HomeButton,
                            BottomNavItem.MapButton,
//                            BottomNavItem.ProfileButton,
//                            BottomNavItem.PetsButton,
                            BottomNavItem.BookingButton
                        )
                        BottomNavBar(navController = navController, items)
                    },
//                    floatingActionButton = {
//                        FloatingActionButton(onClick = { }) {
//                            Icon(Icons.Default.Add, contentDescription = "Adicionar")
//                        }
//                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        launcher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
                        MainNavHost(navController = navController, viewModel)
                    }
                }
            }
        }
    }
}

