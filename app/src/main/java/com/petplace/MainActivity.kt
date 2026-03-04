package com.petplace

import android.os.Bundle
import android.util.Log
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
import com.cloudinary.android.MediaManager
//import com.google.android.datatransport.BuildConfig
//import com.google.firebase.BuildConfig
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.petplace.db.fb.FBDatabase
import com.petplace.ui.nav.Route
import com.petplace.ui.theme.PrimaryPink
import com.petplace.ui.theme.PrimaryGreen
import kotlinx.coroutines.launch
//import com.google.android.libraries.places.api.Places


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            val config = mapOf(
                "cloud_name" to "dmmw9qfl1",
                "secure" to true
            )
            MediaManager.init(this, config)
        } catch (e: Exception) {
            // O try-catch evita que o app quebre se o MediaManager for iniciado duas vezes (ex: ao girar a tela)
        }


        enableEdgeToEdge()

        // Define a variable to hold the Places API key.
//        val apiKey = com.petplace.BuildConfig.PLACES_API_KEY
//
//        // Log an error if apiKey is not set.
//        if (apiKey.isEmpty() || apiKey == "DEFAULT_API_KEY") {
//            Log.e("Places test", "No api key")
//            finish()
//            return
//        }
//
//        // Initialize the SDK
//        Places.initializeWithNewPlacesApiEnabled(applicationContext, apiKey)

        // Create a new PlacesClient instance
//        val placesClient = Places.createClient(this)

        setContent {

            val fbDB = remember { FBDatabase() }
            val viewModel: MainViewModel = viewModel(factory = MainViewModelFactory(fbDB))

            val navController = rememberNavController()

            val currentRoute = navController.currentBackStackEntryAsState()
            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission(),
                onResult = {})

            val isHost = viewModel.isHostMode
            val primaryColor = if (isHost) PrimaryPink else PrimaryGreen



            PetPlaceTheme {
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    gesturesEnabled = drawerState.isOpen,
                    drawerContent = {
                        ModalDrawerSheet (
                            modifier = Modifier.width(260.dp),
                            drawerContainerColor = primaryColor,
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
                                            text = if (isHost) "Mudar para Hóspede" else "Mudar para Hospedeiro",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp
                                        ) },
                                    selected = false,
                                    onClick = {
                                        scope.launch { drawerState.close() }

                                        if (isHost) {
                                            viewModel.toggleAppMode()
                                            navController.navigate(Route.Home) {
//                                                popUpTo(0)
                                            }
                                        } else {
                                            viewModel.toggleAppMode()
                                            navController.navigate(Route.BookingHost) {
//                                                popUpTo(0)
                                            }
                                        }
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
                                    containerColor = primaryColor,
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
                            val guestItems = listOf(
                                BottomNavItem.HomeButton,
                                BottomNavItem.MapButton,
                                BottomNavItem.BookingButton
                            )

                            val hostItems = listOf(
                                BottomNavItem.BookingButtonHost,
                                BottomNavItem.HostingsButton
                            )

                            val currentItems = if (isHost) hostItems else guestItems

                            if (currentItems.isNotEmpty()) {
                                BottomNavBar(navController = navController, currentItems, color = primaryColor)
                            }
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