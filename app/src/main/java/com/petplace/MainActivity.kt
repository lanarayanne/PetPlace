package com.petplace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.petplace.ui.HomePage
import com.petplace.ui.nav.BottomNavBar
import com.petplace.ui.nav.BottomNavItem
import com.petplace.ui.nav.MainNavHost
import com.petplace.ui.theme.PetPlaceTheme
import androidx.navigation.NavDestination.Companion.hasRoute
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.petplace.db.fb.FBDatabase
import com.petplace.ui.nav.Route
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val fbDB = remember { FBDatabase() }
            val viewModel: MainViewModel = viewModel(factory = MainViewModelFactory(fbDB))

            val navController = rememberNavController()
            // viewModel : MainViewModel by viewModels()

            val currentRoute = navController.currentBackStackEntryAsState()
            //val showButton = currentRoute.value?.destination?.hasRoute(Route.Home::class) == true
            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission(),
                onResult = {})

            PetPlaceTheme {
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    gesturesEnabled = drawerState.isOpen,
                    drawerContent = {
                        ModalDrawerSheet (
                            modifier = Modifier.width(260.dp),
//                            drawerContainerColor = Color.White)
                            drawerContainerColor = Color(0xFF419D78),
                            drawerContentColor = Color.White)
                            {
                            Spacer(Modifier.height(16.dp))

                            //Divider()

                            NavigationDrawerItem(
                                label = {
                                    Text(
                                        text = "Meu Perfil",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                        ) },
                                selected = false,
                                onClick = {
                                    scope.launch { drawerState.close() }
                                    navController.navigate(Route.Profile)
                                },
                                colors = NavigationDrawerItemDefaults.colors(
                                    unselectedContainerColor = Color.Transparent,
                                    unselectedTextColor = Color.White,
                                    unselectedIconColor = Color.White
                                )
                            )
                                NavigationDrawerItem(
                                    label = {
                                        Text(
                                            text = "Meus Pets",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp
                                        ) },
                                    selected = false,
                                    onClick = {
                                        scope.launch { drawerState.close() }
                                        navController.navigate(Route.Pets)
                                    },
                                    colors = NavigationDrawerItemDefaults.colors(
                                        unselectedContainerColor = Color.Transparent,
                                        unselectedTextColor = Color.White,
                                        unselectedIconColor = Color.White
                                    )
                                )

                                NavigationDrawerItem(
                                    label = {
                                        Text(
                                            text = "Mudar para Hospedeiro",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp
                                        ) },
                                    selected = false,
                                    onClick = {
                                        scope.launch { drawerState.close() }
                                    },
                                    colors = NavigationDrawerItemDefaults.colors(
                                        unselectedContainerColor = Color.Transparent,
                                        unselectedTextColor = Color.White,
                                        unselectedIconColor = Color.White
                                    )
                                )

                            NavigationDrawerItem(
                                label = {
                                    Text(
                                        text = "Sair",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    ) },
                                selected = false,
                                onClick = {
                                    Firebase.auth.signOut()
                                    scope.launch { drawerState.close() }
                                },
                                colors = NavigationDrawerItemDefaults.colors(
                                    unselectedContainerColor = Color.Transparent,
                                    unselectedTextColor = Color.White,
                                    unselectedIconColor = Color.White
                                )
                            )
                        }
                    }
                ) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                modifier = Modifier.height(70.dp),
                                title = { },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = Color(0xFF419D78),
                                    actionIconContentColor = Color.White,
                                    titleContentColor = Color.White,
                                    navigationIconContentColor = Color.White
                                ),
                                navigationIcon = {
                                    IconButton(onClick = {
                                        scope.launch {
                                            drawerState.open()
                                        }
                                    }) { Icon (
                                        imageVector = Icons.Default.Menu,
                                        contentDescription = "Menu"
                                    )}
                                },
                                actions = {
//                                IconButton( onClick = { Firebase.auth.signOut()
                                    //                                finish() ) } ) {
//                                    Icon(
//                                        imageVector =
//                                            Icons.AutoMirrored.Filled.ExitToApp,
//                                        contentDescription = "Localized description"
//                                    )
//                                }

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
}