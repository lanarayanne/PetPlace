package com.petplace.ui

import android.app.Activity
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.petplace.MainActivity
import com.petplace.MainViewModel
import com.petplace.db.fb.FBDatabase
import com.petplace.model.Pet
import com.petplace.model.User
import com.petplace.ui.nav.Route
import com.petplace.ui.EditPetProfile
import com.petplace.ui.theme.PrimaryGreen
import com.petplace.ui.theme.PrimaryPink

@Composable
fun PetProfilePage(navController: NavController, modifier: Modifier = Modifier, viewModel: MainViewModel, petId: Int) {
    val petData = viewModel.user?.pets?.find { it.id == petId }

    if (petData != null) {
        PetData(
            pet = petData,
            modifier = modifier
                .fillMaxSize()
                .background(Color.White),
            navController = navController,
            viewModel = viewModel
        )
    } else {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }

    }

}

@Composable
fun PetData(
    pet: Pet,
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: MainViewModel
) {
    val activity = LocalActivity.current as Activity
    val scrollState = rememberScrollState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val isUserHost = viewModel.isHostMode

    Column(
        modifier=modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 40.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Voltar",
            tint = if (isUserHost) PrimaryPink else PrimaryGreen,
            modifier = Modifier
                .clickable { navController.popBackStack() }
        )

        Box(
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
                .align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center
        ) {
            if (pet.picture != null) {
                AsyncImage(
                    model = pet.picture,
                    contentDescription = "Foto de ${pet.name}",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Pets,
                    contentDescription = "Pet sem foto",
                    tint = Color.White,
                    modifier = Modifier.size(60.dp)
                )
            }
        }

//        Text(
//            text = pet.name,
//            fontWeight = FontWeight.Bold,
//            color = if (isUserHost) PrimaryPink else PrimaryGreen,
//            textAlign = TextAlign.Center,
//            fontSize = 30.sp
//        )

        Spacer(modifier = Modifier.height(32.dp))
        ProfileInfoRow(label = "Nome", value = pet.name)
        ProfileInfoRow(label = "Animal", value = pet.animal.toString())
        ProfileInfoRow(label = "Idade", value = pet.age.toString())
//        ProfileInfoRow(label = "Ano de Nascimento", value = pet.birthYear?.toString()?: "")
        ProfileInfoRow(label = "Peso", value = pet.weight.toString())
        ProfileInfoRow(label = "Raça", value = pet.breed.toString())
        ProfileInfoRow(label = "Cor", value = pet.color.toString())
        ProfileInfoRow(label = "Observações", value = pet.observations.toString())


        Spacer(modifier = Modifier.height(40.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp))
        {
            ProfileActionButton(
                isUserHost= isUserHost,
                text = "Editar") {
                navController.navigate(Route.EditPetProfile(pet.id))
            }
            ProfileActionButton(
                isUserHost = isUserHost,
                text = "Excluir Animal",
            ) {
                showDeleteDialog = true
            }
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Excluir Pet", color=Color.Black )},
                text = { Text("Tem certeza que deseja excluir este pet? Essa ação não pode ser desfeita.", color=Color.Black) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            FBDatabase.deletePet(
                                petId = pet.id,
                                onSuccess = {
                                    Toast.makeText(context, "Pet excluído.", Toast.LENGTH_SHORT).show()
                                    showDeleteDialog = false
                                    navController.popBackStack()
                                },
                                onFailure = {
                                    Toast.makeText(context, "Erro: ${it.message}", Toast.LENGTH_LONG).show()
                                    showDeleteDialog = false
                                }
                            )
                        }
                    ) {
                        Text("Sim, excluir", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancelar", color=Color.Black)
                    }
                },
                containerColor = Color.White
            )
        }
    }
}

