package com.petplace.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.petplace.MainViewModel
import com.petplace.model.SearchCriteria
import androidx.compose.runtime.setValue


@Composable
fun HostingDescriptionPage(
    viewModel: MainViewModel,
    navController: NavController,
) {
    val item = viewModel.selectedHosting
    val searchCriteria = viewModel.currentSearch
    var showModal by remember { mutableStateOf(false) }

    if (item != null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()))
        {
//            Button(onClick = { navController.popBackStack() }) {
//                Text("Voltar")
//            }
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ){
                Text(
                    text = item.name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Icon(
                    imageVector = Icons.Outlined.FavoriteBorder,
                    contentDescription = "Favoritar",
                    tint = Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                val yellowColor = Color(0xFFFFC107)
                repeat(5) { index ->
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = if (index < item.rating) yellowColor else Color.LightGray,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Text(
                text = "${item.reviewsCount} avaliações",
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ){
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray)
                ) {
                    /*
                    Image(
                        painter = painterResource(id = preview.picture),
                        contentDescription = "Foto do local",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    */

                }
                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray)
                ) {
                    /*
                    Image(
                        painter = painterResource(id = preview.picture),
                        contentDescription = "Foto do local",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    */

                }
            }

            Row (
                //modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "Check-in",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    Text(
                        text = searchCriteria.startDate.toString(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF419D78)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Check-out",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    Text(
                        text = searchCriteria.endDate.toString(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF419D78)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Hóspedes",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    Text(
                        text = searchCriteria.petsCount.toString(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF419D78)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Preço total para ${searchCriteria.dailyRates}, ${searchCriteria.petsCount} pets",
                fontSize = 12.sp,
                color = Color.Gray
            )
            Text(
                text = formatCurrency(searchCriteria.value),
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Descrição",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            Text(
                text = item.description,
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(12.dp))


            Text(
                text = "Localização",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            Text(
                text = "Localização....TODO", //TODO
                fontSize = 12.sp,
                color = Color.Gray
            )
            /*TODO: MAPA*/

            Spacer(modifier = Modifier.height(12.dp))


            Text(
                text = "Serviços",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            Text(
                text = "Serviços....TODO", //TODO
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(12.dp))


            /*TODO: Avaliações*/

            Button(
                onClick = {
                    showModal = true
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF419D78))
            ) {
                Text("Reservar", color = Color.White)
            }

            if (showModal) {
                AlertDialog(
                    onDismissRequest = {
                        showModal = false
                    },
                    containerColor = Color.White,
                    title = {
                        Text(
                            text = "Confirmar Reserva",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF419D78)
                        )
                    },
                    text = {
                        Column (
                        ) {
                            Text(
                                text = "Você deseja confirmar a reserva em ${item.name}?",
                                color = Color.Black)

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Entrada: ${searchCriteria.startDate}",
                                color = Color.Black)
                            Text(
                                text = "Saída: ${searchCriteria.endDate}",
                                color = Color.Black)
                            Text(
                                text = "Valor Total: ${formatCurrency(searchCriteria.value)}",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 8.dp),
                                color = Color.Black
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.saveBooking(
                                    onSuccess = {
                                        showModal = false
                                        Toast.makeText(navController.context, "Reserva realizada!", Toast.LENGTH_SHORT).show()
                                        navController.popBackStack()
                                    },
                                    onError = { erro ->
                                        Toast.makeText(navController.context, erro, Toast.LENGTH_LONG).show()
                                    }
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF419D78))
                        ) {
                            Text("Confirmar")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { showModal = false }
                        ) {
                            Text("Cancelar", color = Color.Gray)
                        }
                    }
                )
            }
        }
    } else {
        Text("Hospedagem não encontrada")
    }
}