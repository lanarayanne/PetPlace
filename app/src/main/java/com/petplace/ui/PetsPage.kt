package com.petplace.ui

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.petplace.MainViewModel
import com.petplace.model.Pet
import com.petplace.ui.nav.Route
import com.petplace.ui.ui.theme.PrimaryGreen

@Composable
fun PetsPage(modifier: Modifier = Modifier, viewModel: MainViewModel, navController: NavController) {
    val petsList = viewModel.pets
    val activity = LocalActivity.current as Activity
    val user = viewModel.user
    val pets: List<Pet> = viewModel.user?.pets ?: emptyList()

    if(user != null && pets.isNotEmpty()){
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(Color.White),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Meus Pets",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF419D78),
                    textAlign = TextAlign.Start,
                    fontSize = 30.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )
            }
            items(petsList, key = { it.id }) { pet ->
                petItem(
                    pet = pet,
                    onClick = {
                        Toast.makeText(activity, "${pet.name}", Toast.LENGTH_LONG).show()
                    })
            }
        }
    } else {
        Column (
            modifier = Modifier
                .background(Color.White)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Text(
                text = "Cadastre seu Pet",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF419D78),
                textAlign = TextAlign.Center,
                fontSize = 30.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            Button (
                onClick = {
                    navController.navigate(Route.RegisterPet)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryGreen,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
            ) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar")
            }


        }


    }

}

@Composable
fun petItem(
    pet : Pet,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row (
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
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
        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .heightIn(110.dp),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = pet.name,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Black
            )
            Text(
                text = "${pet.animal.animal}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            Text(
                text = "${pet.age.faixaEtaria}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            pet.breed?.let { race ->
                Text(
                    text = "${pet.breed}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            }
        }

    }
}

