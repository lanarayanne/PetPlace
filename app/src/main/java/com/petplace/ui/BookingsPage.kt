package com.petplace.ui

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.petplace.MainViewModel
import com.petplace.model.Booking
import com.petplace.model.Status
import com.petplace.ui.host.BookingCardItemHost
import com.petplace.ui.theme.PrimaryPink
import com.petplace.ui.theme.PrimaryGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun BookingsPage(modifier: Modifier = Modifier, viewModel: MainViewModel) {
    val bookingList = viewModel.booking
    val activity = LocalActivity.current as Activity
    var selectedStatus by remember { mutableStateOf<Status?>(null) }

    val filteredList = if (selectedStatus == null) {
        bookingList
    } else {
        bookingList.filter { it.status == selectedStatus }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Text(
            text = "Reservas",
            fontWeight = FontWeight.Bold,
            color = PrimaryGreen,
            textAlign = TextAlign.Start,
            fontSize = 30.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        BookingsContent(
            bookingList = filteredList,
            selectedStatus = selectedStatus,
            viewModel = viewModel,
            isUserHost = false,
            onFilterSelected = { status ->
                selectedStatus = if (selectedStatus == status) null else status
            },
            activity = activity
        )

    }
}



@Composable
fun BookingsContent(
    bookingList: List<Booking>,
    isUserHost: Boolean,
    viewModel: MainViewModel,
    selectedStatus: Status?,
    onFilterSelected: (Status) -> Unit,
    activity: Activity) {

    var selectedBookingForDetails by remember { mutableStateOf<Booking?>(null) }
    
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                StatusBadge(Status.PROXIMA, selectedStatus == Status.PROXIMA) { onFilterSelected(Status.PROXIMA) }
                Spacer(modifier = Modifier.weight(1f))
                StatusBadge(Status.EMANDAMENTO, selectedStatus == Status.EMANDAMENTO) { onFilterSelected(Status.EMANDAMENTO) }
                Spacer(modifier = Modifier.weight(1f))
                StatusBadge(Status.CONCLUIDA, selectedStatus == Status.CONCLUIDA) { onFilterSelected(Status.CONCLUIDA) }
                Spacer(modifier = Modifier.weight(1f))
                StatusBadge(Status.CANCELADA, selectedStatus == Status.CANCELADA) { onFilterSelected(Status.CANCELADA) }
            }
        }

        if (bookingList.isEmpty()) {
            item {
                Box(modifier = Modifier.fillParentMaxSize()) {
                    EmptyStateMessage()
                }
            }
        } else {
            if(!isUserHost) {
                items(bookingList, key = { it.id }) { item ->
                    BookingCardItem(
                        booking = item,
                        onClick = {
                            selectedBookingForDetails = item                        },
                        onCancelClick = {
                            viewModel.cancelBooking(
                                booking = item,
                                onSuccess = {
                                    Toast.makeText(
                                        activity,
                                        "Reserva cancelada!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                onError = {}
                            )
                        }
                        )

                }
            } else {
                items(bookingList, key = { it.id }) { item ->
                    BookingCardItemHost(
                        booking = item,
                        onClick = {
                            selectedBookingForDetails = item                        },
                        onCancelClick = {
                            viewModel.cancelBooking(
                                booking = item,
                                onSuccess = {
                                    Toast.makeText(activity, "Reserva cancelada!", Toast.LENGTH_SHORT).show()
                                },
                                onError = { erro ->
                                    Toast.makeText(activity, erro, Toast.LENGTH_LONG).show()
                                }
                            )
                        }
                        )
                }
            }
        }

//        items(bookingList, key = { it.id }) { item ->
//            BookingCardItem(
//                booking = item,
//                onClick = {
//                    Toast.makeText(activity, "${item.hosting.name}", Toast.LENGTH_LONG).show()
//                })
//        }
    }

    selectedBookingForDetails?.let { booking ->
        BookingDetailsModal(
            booking = booking,
            isUserHost = isUserHost,
            onDismiss = { selectedBookingForDetails = null }
        )
    }
}

@Composable
fun EmptyStateMessage() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.CalendarToday,
            contentDescription = "Sem reservas",
            tint = Color.LightGray,
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Você ainda não tem reservas.",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray
        )
        Text(
            text = "Que tal encontrar um lugar para seu pet?",
            fontSize = 14.sp,
            color = Color.LightGray
        )
    }
}

@Composable
fun StatusBadge(
    status: Status,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val color = getStatusColor(status)
    val backgroundColor = if (isSelected) color else Color.Transparent
    val contentColor = if (isSelected) Color.White else Color.Black
    val borderColor = if (isSelected) Color.Transparent else color

    Surface(
        color = backgroundColor,
        border = BorderStroke(width = 2.dp, color = borderColor),
        shape = RoundedCornerShape(50),
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .clickable { onClick() }
    ) {
        Text(
            text = status.status,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
            fontSize = 12.sp,
            color = Color.Black
        )
    }
}

@Composable
fun BookingCardItem(
    booking: Booking,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    val statusColor = getStatusColor(booking.status)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.LightGray)
                ) {
                    // Imagem aqui
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = booking.hosting.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                    Text(
                        text = "${formatDate(booking.checkIn)} - ${formatDate(booking.checkOut)}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Surface(
                        color = statusColor,
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = booking.status.status,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
                            fontSize = 12.sp,
                            color = Color.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Total: R$${booking.value}",
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontSize = 16.sp
            )
            Text(
                text = "${booking.days} diária(s), ${booking.pets.size} pet(s)",
                fontSize = 14.sp,
                color = Color.Gray
            )

            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                OutlinedButton(
                    onClick = { onClick() },
                    border = BorderStroke(1.dp, PrimaryGreen),
                    shape = RoundedCornerShape(50),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 4.dp)
                        .height(32.dp)
                ) {
                    Text(text = "Ver Detalhes", color = PrimaryGreen, fontSize = 12.sp)
                }

                if (booking.status == Status.PROXIMA || booking.status == Status.EMANDAMENTO) {
                    OutlinedButton(
                        onClick = onCancelClick, // <-- 3. CHAME A FUNÇÃO AQUI
                        border = BorderStroke(1.dp, Color(0xFFF03737)), // Mudei a cor para vermelho para destacar que é cancelar
                        shape = RoundedCornerShape(50),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 4.dp)
                            .height(32.dp)
                    ) {
                        Text(text = "Cancelar", color = Color(0xFFF03737), fontSize = 12.sp)
                    }
                } else {
                    // Preenche o espaço para o botão "Ver Detalhes" não esticar
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

fun getStatusColor(status: Status): Color {
    return when (status) {
        Status.PROXIMA -> Color(0xFF7ADCE7)
        Status.EMANDAMENTO -> Color(0xFF7AE7C7)
        Status.CONCLUIDA -> Color(0xFF1F1F1F).copy(alpha = 0.3f)
        Status.CANCELADA -> Color(0xFFF03737).copy(alpha = 0.3f)
    }
}

fun formatDate(data: Any?): String {
    if (data == null) return "--/--/----"
    val formatoBrasileiro = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
    formatoBrasileiro.timeZone = TimeZone.getDefault()

    return try {
        when (data) {
            is Date -> formatoBrasileiro.format(data)
            is Long -> formatoBrasileiro.format(Date(data))
            is String -> {
                try {

                    val formatoAmericano = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US)

                    val dataObjeto = formatoAmericano.parse(data)

                    if (dataObjeto != null) {
                        formatoBrasileiro.format(dataObjeto)
                    } else {
                        data
                    }
                } catch (e: Exception) {
                    data
                }
            }
            else -> data.toString()
        }
    } catch (e: Exception) {
        "Erro"
    }
}

@Composable
fun BookingDetailsModal(
    booking: Booking,
    isUserHost: Boolean,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = {
            Text(
                text = "Detalhes da Reserva",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = if (isUserHost) PrimaryPink else PrimaryGreen
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "Hospedagem", fontWeight = FontWeight.Bold, color = Color.Black)
                Text(text = booking.hosting.name, color = Color.Gray)
                Text(text = "Endereço: ${booking.hosting.address ?: "Não informado"}", color = Color.Gray, fontSize = 14.sp)

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.LightGray.copy(alpha = 0.5f))

                if (isUserHost) {
                    Text(text = "Cliente", fontWeight = FontWeight.Bold, color = Color.Black)
                    Text(text = booking.client.name, color = Color.Gray)
                    Text(text = "Contato: ${booking.client.phone ?: "Não informado"}", color = Color.Gray, fontSize = 14.sp)
                } else {
                    Text(text = "Anfitrião", fontWeight = FontWeight.Bold, color = Color.Black)
                    Text(text = booking.host.name, color = Color.Gray)
                    Text(text = "Contato: ${booking.host.phone ?: "Não informado"}", color = Color.Gray, fontSize = 14.sp)
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.LightGray.copy(alpha = 0.5f))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Check-in:", fontWeight = FontWeight.Medium, color = Color.Black)
                    Text(formatDate(booking.checkIn), color = Color.Gray)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Check-out:", fontWeight = FontWeight.Medium, color = Color.Black)
                    Text(formatDate(booking.checkOut), color = Color.Gray)
                }
                Text("Duração: ${booking.days} diária(s)", color = Color.Gray, fontSize = 14.sp)

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.LightGray.copy(alpha = 0.5f))

                Text(text = "Pets (${booking.pets.size})", fontWeight = FontWeight.Bold, color = Color.Black)
                if (booking.pets.isEmpty()) {
                    Text("Nenhum pet selecionado.", color = Color.Gray, fontSize = 14.sp)
                } else {
                    booking.pets.forEach { pet ->
                        Text("- ${pet.name} (${pet.animal})", color = Color.Gray, fontSize = 14.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total:", fontWeight = FontWeight.Bold, color = Color.Black)
                    Text(
                        text = "R$ ${booking.value}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = if (isUserHost) PrimaryPink else PrimaryGreen
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isUserHost) PrimaryPink else PrimaryGreen
                )
            ) {
                Text("Fechar", color = Color.White)
            }
        }
    )
}