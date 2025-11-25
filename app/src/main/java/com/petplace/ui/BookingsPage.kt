package com.petplace.ui

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.petplace.MainViewModel
import com.petplace.model.Booking
import com.petplace.model.Status

@Composable
fun BookingsPage(modifier: Modifier = Modifier, viewModel: MainViewModel) {
    val bookingList = viewModel.booking
    val activity = LocalActivity.current as Activity

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        item {
            Text(
                text = "Reservas",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF419D78),
                textAlign = TextAlign.Start,
                fontSize = 30.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
        item {
            Row (modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)){
                Surface(
                    color = Color.Transparent,
                    border = BorderStroke(width = 2.dp, color = getStatusColor(Status.PROXIMA)),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = Status.PROXIMA.status,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Surface(
                    color = Color.Transparent,
                    border = BorderStroke(width = 2.dp, color = getStatusColor(Status.EMANDAMENTO)),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = Status.EMANDAMENTO.status,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.weight(1f))
                Surface(
                    color = Color.Transparent,
                    border = BorderStroke(width = 2.dp, color = getStatusColor(Status.CONCLUIDA)),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = Status.CONCLUIDA.status,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Surface(
                    color = Color.Transparent,
                    border = BorderStroke(width = 2.dp, color = getStatusColor(Status.CANCELADA)),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = Status.CANCELADA.status,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

            }
        }
        items(bookingList, key = { it.id }) { item ->
            BookingCardItem(
                booking = item,
                onClick = {
                    Toast.makeText(activity, "${item.hosting.name}", Toast.LENGTH_LONG).show()
                })
        }
    }
}

@Composable
fun BookingCardItem(
    booking: Booking,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
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
        Column (
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
                        /*
                        Image(
                            painter = painterResource(id = R.drawable.sua_foto),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        */
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
                            text = "${booking.checkIn} - ${booking.checkOut}",
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
                    text = "${booking.days} diária(s), ${booking.pet.size} pet(s)",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                OutlinedButton(
                    onClick = {  },
                    border = BorderStroke(1.dp, Color(0xFF419D78)),
                    shape = RoundedCornerShape(50),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                    modifier = Modifier
                        .width(130.dp)
                        .padding( 8.dp)
                        .height(32.dp)
                ) {
                    Text(text = "Ver Detalhes", color = Color(0xFF419D78), fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedButton(
                    onClick = { },
                    border = BorderStroke(1.dp, Color(0xFF419D78)),
                    shape = RoundedCornerShape(50),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                    modifier = Modifier
                        .width(130.dp)
                        .padding( 8.dp)
                        .height(32.dp)
                ) {
                    Text(text = "Cancelar", color = Color(0xFF419D78), fontSize = 12.sp)
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
