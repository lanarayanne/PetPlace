package com.petplace.ui.host

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.navigation.NavController
import com.petplace.MainViewModel
import com.petplace.model.Booking
import com.petplace.model.Status
import com.petplace.ui.BookingCardItem
import com.petplace.ui.BookingsContent
import com.petplace.ui.EmptyStateMessage
import com.petplace.ui.StatusBadge
import com.petplace.ui.formatDate
import com.petplace.ui.getStatusColor
import com.petplace.ui.theme.PrimaryPink
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun BookingsPageHost(modifier: Modifier = Modifier, viewModel: MainViewModel, navController: NavController) {
    val bookingList = viewModel.hostBookings

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
            text = "Reservas Recebidas",
            fontWeight = FontWeight.Bold,
            color = PrimaryPink,
            textAlign = TextAlign.Start,
            fontSize = 30.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        BookingsContent(
            bookingList = filteredList,
            isUserHost = true,
            viewModel = viewModel,
            selectedStatus = selectedStatus,
            onFilterSelected = { status ->
                selectedStatus = if (selectedStatus == status) null else status
            },
            activity = activity
        )
    }
}

@Composable
fun BookingCardItemHost(
    booking: Booking,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onMessageClick: () -> Unit = {},
    onCancelClick: () -> Unit = {}
) {
    val statusColor = getStatusColor(booking.status)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                Text(
                    text = booking.client.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )

                Text(
                    text = booking.hosting.name,
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(1.dp))

                Surface(
                    color = statusColor,
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.wrapContentSize()
                ) {
                    Text(
                        text = booking.status.status,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 1.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "R$ ${String.format("%.2f", booking.value)}",
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black,
                    fontSize = 18.sp
                )

                Text(
                    text = "${formatDate(booking.checkIn)} - ${formatDate(booking.checkOut)}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Text(
                    text = "${booking.days} diárias, ${booking.pets.size} pets",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.End
            ) {
                HostActionButton(text = "Ver Detalhes", color = PrimaryPink, onClick = onClick)
                HostActionButton(text = "Mensagem", color = PrimaryPink, onClick = onMessageClick)
                if (booking.status == Status.PROXIMA || booking.status == Status.EMANDAMENTO) {
                    HostActionButton(
                        text = "Cancelar",
                        color = PrimaryPink,
                        onClick = onCancelClick
                    )
                }
            }
        }
    }
}

@Composable
fun HostActionButton(
    text: String,
    color: Color,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        border = BorderStroke(1.dp, color),
        shape = RoundedCornerShape(50),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
        modifier = Modifier
            .height(32.dp)
            .widthIn(min = 120.dp)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}