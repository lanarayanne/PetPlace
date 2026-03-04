package com.petplace.ui.host

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.petplace.MainViewModel
import com.petplace.ui.formatCurrency
import com.petplace.ui.nav.Route
import com.petplace.ui.theme.PrimaryPink

@Composable
fun HostingDescriptionPageHost(
    viewModel: MainViewModel,
    navController: NavController
) {
    val item = viewModel.selectedHosting

    if (item == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Hospedagem não encontrada", color = Color.Gray)
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Voltar",
                tint = PrimaryPink,
                modifier = Modifier.clickable { navController.popBackStack() }
            )

            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Editar",
                tint = PrimaryPink,
                modifier = Modifier.clickable {
                     navController.navigate(Route.EditHosting(item.id))
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            val primeiraFoto = item.pictures?.firstOrNull()

            if (primeiraFoto != null) {
                AsyncImage(
                    model = primeiraFoto,
                    contentDescription = "Foto da Hospedagem ${item.name}",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Sem foto",
                    tint = Color.White,
                    modifier = Modifier.size(60.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = item.name,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
            repeat(5) { index ->
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = if (index < item.rating) Color(0xFFFFC107) else Color.LightGray,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "${item.reviewsCount} avaliações",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            HostInfoColumn("Tipo", item.type.descricao, PrimaryPink)
            HostInfoColumn("Vagas Livres", item.vacancies.toString(), PrimaryPink)
            HostInfoColumn("Tamanho", "${item.size ?: "--"} m²", PrimaryPink)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Valor da Diária",
            fontSize = 12.sp,
            color = Color.Gray
        )
        Text(
            text = formatCurrency(item.dailyRate),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp), color = Color.LightGray.copy(alpha = 0.4f))

        Text(text = "Localização", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = PrimaryPink)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = item.address ?: "Endereço não cadastrado", fontSize = 14.sp, color = Color.DarkGray)
        Text(text = "CEP: ${item.cep}", fontSize = 14.sp, color = Color.Gray)
        if (!item.complement.isNullOrEmpty()) {
            Text(text = "Complemento: ${item.complement}", fontSize = 14.sp, color = Color.Gray)
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp), color = Color.LightGray.copy(alpha = 0.4f))

        Text(text = "Descrição", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = PrimaryPink)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = item.description.ifEmpty { "Nenhuma descrição fornecida." },
            fontSize = 14.sp,
            color = Color.DarkGray,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                 navController.navigate(Route.EditHosting(item.id))
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryPink),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(imageVector = Icons.Default.Edit, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Editar Hospedagem", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

@Composable
fun HostInfoColumn(title: String, value: String, highlightColor: Color) {
    Column {
        Text(
            text = title,
            fontSize = 12.sp,
            color = Color.Gray
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = highlightColor
        )
    }
}