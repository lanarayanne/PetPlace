package com.petplace.ui.host

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.petplace.MainViewModel
import com.petplace.model.Hosting
import com.petplace.model.PlacePreview
import com.petplace.ui.PreviewItem
import com.petplace.ui.nav.Route
import com.petplace.ui.theme.PrimaryPink
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HostingPage(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    navController: NavController
) {
    LaunchedEffect(Unit) {
        viewModel.getHostingsByUser()
    }
    val hostingList = viewModel.userHostings

    val user = viewModel.user


    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Route.RegisterHosting) },
                containerColor = PrimaryPink,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar")
            }
        },
        topBar = {
            Text(
                text = "Minhas Hospedagens",
                color = PrimaryPink,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = modifier.padding(20.dp)
            )
        }
    ) { paddingValues ->

        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
        ) {

            if (hostingList.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(hostingList, key = { it.id }) { item ->
                        PreviewItem(
                            preview = item.toPlacePreview(),
                            showFavorite = false,
                            onClick = {
                                navController.navigate(Route.HostingDescriptionHost(item.id))
                            },
                            onFavoriteClick = {
                            }
                        )
                    }

                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Você ainda não possui hospedagens cadastradas.",
                        color = Color.Gray,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

fun Hosting.toPlacePreview(): PlacePreview {
    return PlacePreview(
        id = this.id.toString(),
        name = this.name,
        picture = this.pictures?.firstOrNull(),
        value = this.dailyRate,
        vacancies = this.vacancies,
        type = this.type.descricao,
        rating = this.rating.toInt(),
        evaluation = this.reviewsCount,

        lat = this.location?.latitude,
        lng = this.location?.longitude,
        size = this.size,
        distance = null,
        isFavorite = false,
        dailyCount = null,
        petCount = null
    )
}