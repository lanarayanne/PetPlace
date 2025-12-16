package com.petplace.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Favorite
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HostingDescriptionPage(
    viewModel: MainViewModel,
    navController: NavController
) {
    val item = viewModel.selectedHosting
    val searchCriteria = viewModel.currentSearch
    var showModal by remember { mutableStateOf(false) }

    if (item == null) {
        Text("Hospedagem não encontrada")
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            TextButton(onClick = { navController.popBackStack() }) {
                Text("Voltar", color = Color(0xFF419D78))
            }

            Icon(
                imageVector = if (viewModel.isFavorite(item.id))
                    Icons.Filled.Favorite
                else
                    Icons.Outlined.FavoriteBorder,
                contentDescription = "Favoritar",
                modifier = Modifier.clickable {
                    viewModel.toggleFavorite(item.id)
                }
            )
        }

        Text(
            text = item.name,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black

        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            repeat(5) { index ->
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = if (index < item.rating) Color(0xFFFFC107) else Color.LightGray,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "${item.reviewsCount} avaliações",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            InfoColumn("Check-in", formatDate(searchCriteria.startDate))
            InfoColumn("Check-out", formatDate(searchCriteria.endDate))
            InfoColumn("Pets", searchCriteria.petsCount.toString())
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Preço total",
            fontSize = 12.sp,
            color = Color.Gray
        )
        Text(
            text = formatCurrency(searchCriteria.value),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text= "Descrição",
            fontWeight = FontWeight.Medium,
            color = Color.Black)
        Text(
            text = item.description,
            fontSize = 12.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { showModal = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF419D78))
        ) {
            Text("Reservar", color = Color.White)
        }
    }

    if (showModal) {
        AlertDialog(
            onDismissRequest = { showModal = false },
            containerColor = Color.White,
            titleContentColor = Color.Black,
            textContentColor = Color.Gray,
            shape = RoundedCornerShape(16.dp),
            title = {
                Text(
                    text = "Confirmar Reserva",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color(0xFF419D78)
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Hospedagem:", fontWeight = FontWeight.Medium, color = Color.Black)
                        Text(
                            item.name,
                            color = Color.Gray,
                            modifier = Modifier.padding(start = 8.dp).weight(1f),
                            maxLines = 1
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Entrada:", fontWeight = FontWeight.Medium, color = Color.Black)
                        Text(formatDate(searchCriteria.startDate), color = Color.Gray)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Saída:", fontWeight = FontWeight.Medium, color = Color.Black)
                        Text(formatDate(searchCriteria.endDate), color = Color.Gray)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp)) // Fundo sutil
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total:", fontWeight = FontWeight.Bold, color = Color.Black)
                        Text(
                            text = formatCurrency(searchCriteria.value),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFF419D78)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.saveBooking(
                            onSuccess = {
                                showModal = false
                                navController.popBackStack()
                            },
                            onError = {}
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF419D78)), // Botão Verde
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Confirmar", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showModal = false }) {
                    Text("Cancelar", color = Color.Gray)
                }
            }
        )
    }
}

@Composable
fun InfoColumn(title: String, value: String) {
    Column {
        Text(
            text = title,
            fontSize = 12.sp,
            color = Color.Gray
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF419D78)
        )
    }
}

fun formatDate(date: Date?): String {
    if (date == null) return "--/--/----"
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
    return formatter.format(date)
}

