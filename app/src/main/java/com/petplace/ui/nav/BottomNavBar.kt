package com.petplace.ui.nav

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.runtime.getValue


@Composable
fun BottomNavBar(navController: NavHostController, items : List<BottomNavItem>) {
    NavigationBar(
        contentColor = Color(0xFF419D78),
        containerColor = Color(0xFF419D78)
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination

        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                label = { Text(text = item.title, fontSize = 12.sp) },
                alwaysShowLabel = true,
                selected = currentRoute == item.route,

                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    unselectedIconColor = Color.White,
                    //unselectedIconColor = Color.White.copy(alpha = 0.6f),
                    selectedTextColor = Color.White,
                    unselectedTextColor = Color.White,
                    indicatorColor = Color.White
                ),

                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(0) {
                            inclusive = true
                        }
                        launchSingleTop = true
                        restoreState = false
                    }

//                    navController.navigate(item.route) {
//                        navController.graph.startDestinationRoute?.let {
//                            popUpTo(it) {
//                                saveState = true
//                            }
//                            restoreState = true
//                        }
//                        launchSingleTop = true
//                    }
                }
            )
        }
    }
}
