package com.petplace.ui

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.petplace.MainViewModel
import com.petplace.getHostings
import com.petplace.model.PlacePreview
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

@Composable
fun HomePage(modifier: Modifier = Modifier, viewModel: MainViewModel) {
    val hostingList = viewModel.hosting
    val activity = LocalActivity.current as Activity

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        items(hostingList, key = { it.id }) { item ->
            PreviewItem(
                preview = item,
                onClick = {
                    Toast.makeText(activity, "${item.name}", Toast.LENGTH_LONG).show()
                })
        }
    }


}



@Composable
fun PreviewItem(
    preview: PlacePreview,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
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
                .padding(12.dp) // Padding interno do conteúdo do card
        ) {
            // --- IMAGEM ---
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(RoundedCornerShape(8.dp))
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

            // --- INFORMAÇÕES ---
            Column(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(110.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = preview.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
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
                                // Pinta de amarelo se o índice for menor que a nota
                                tint = if (index < preview.rating) yellowColor else Color.LightGray,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Text(
                        text = "${preview.evaluation} avaliações",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.LocationOn,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${preview.distance} m de distância",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "${preview.type} - ${preview.size} m²",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    Text(
                        text = formatCurrency(preview.value),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black
                    )
                    Text(
                        text = "${preview.dailyCount} diária, ${preview.petCount} pets",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

fun formatCurrency(amount: BigDecimal): String {
    val format = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    return format.format(amount)
}