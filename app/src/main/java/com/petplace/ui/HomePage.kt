package com.petplace.ui

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.petplace.MainViewModel
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
        item {
            SearchComposable(modifier = Modifier.fillMaxWidth())
        }
        item {
            Row (modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)){
                Text(
                    text = "${hostingList.size} Resultados",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Default.SwapVert,
                    contentDescription = "Favoritar",
                    tint = Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "${hostingList.size} Ordenar",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "Filtrar",
                    tint = Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "${hostingList.size} Filtrar",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
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

@Composable
fun SearchComposable(
    modifier: Modifier = Modifier

) {
    val activity = LocalActivity.current as Activity
    var location by rememberSaveable { mutableStateOf("") }
    var period by rememberSaveable { mutableStateOf("") }
    var animals by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = modifier
            .background(Color(0xFF419D78))
            .padding(horizontal = 16.dp)
            .imePadding(),
        horizontalAlignment = CenterHorizontally

    ) {
        Text(
            text = "PetPlace",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(10.dp))

        SearchInput(
            value = location,
            onValueChange = { location = it },
            placeholder = "Localização",
            icon = Icons.Default.LocationOn
        )

        Spacer(modifier = Modifier.height(10.dp))

        SearchInput(
            value = period,
            onValueChange = { period = it },
            placeholder = "Selecionar Período",
            icon = Icons.Default.DateRange
        )

        Spacer(modifier = Modifier.height(10.dp))

        SearchInput(
            value = animals,
            onValueChange = { animals = it },
            placeholder = "Selecionar Animais",
            icon = Icons.Default.Pets
        )

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth(fraction = 0.9f)
                .height(50.dp),
            shape = CircleShape,
            border = BorderStroke(1.dp, Color.White),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.White
            ),
            onClick = {
                Toast.makeText(activity, "Pesquisar", Toast.LENGTH_LONG).show()

            } ) {
            Text("Pesquisar")
        }
        Spacer(modifier = Modifier.height(10.dp))

    }

}

@Composable
fun SearchInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: ImageVector? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(text = placeholder, color = Color.Gray) },
        modifier = Modifier.fillMaxWidth(fraction = 0.9f),
        shape = CircleShape,
        textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black),
        singleLine = true, // Impede que o texto quebre linha

        // Adicionei suporte a ícone (opcional, mas recomendado)
        leadingIcon = if (icon != null) {
            { Icon(imageVector = icon, contentDescription = null, tint = Color.Gray) }
        } else null,

        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
        )
    )
}