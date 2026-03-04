package com.petplace.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.petplace.MainViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.input.KeyboardType
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.BuildConfig
import com.petplace.db.fb.FBDatabase
import com.petplace.db.fb.toFBUser
import com.petplace.model.User
import com.petplace.ui.theme.PrimaryGreen
import com.petplace.ui.theme.PrimaryPink


@Composable
fun EditProfilePage(navController: NavController, viewModel: MainViewModel) {
    val user = viewModel.user
    val context = LocalContext.current
    val isUserHost = viewModel.isHostMode

    LaunchedEffect(Unit) {
        if (user != null) {
            viewModel.loadUserData(user)
        }
    }

    var name by remember { mutableStateOf(user?.name ?: "") }
    var phone by remember { mutableStateOf(user?.phone ?: "") }

//    var cep by remember { mutableStateOf(user?.cep ?: "") }
//    var address by remember { mutableStateOf(user?.address ?: "") }
    var addressText by remember { mutableStateOf(user?.address ?: "") }
//    var location by remember { mutableStateOf<LatLng?>(null) }

    val color = if (isUserHost) PrimaryPink else PrimaryGreen


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Voltar",
            tint = color,
            modifier = Modifier
                .align(Alignment.Start)
                .clickable { navController.popBackStack() }
        )

        Text(
            text = "Editar Perfil",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = color,
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nome") },
            modifier = Modifier.fillMaxWidth(),
            shape = CircleShape,
            textStyle = androidx.compose.ui.text.TextStyle(
                color = Color.Black,
                fontSize = 16.sp
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                cursorColor = color,
                focusedBorderColor = color,
                focusedLabelColor = color,
                unfocusedBorderColor = color,
                unfocusedLabelColor = color,
            )
        )

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Telefone") },
            modifier = Modifier.fillMaxWidth(),
            shape = CircleShape,
            textStyle = androidx.compose.ui.text.TextStyle(
                color = Color.Black,
                fontSize = 16.sp
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                cursorColor = color,
                focusedBorderColor = color,
                focusedLabelColor = color,
                unfocusedBorderColor = color,
                unfocusedLabelColor = color,
            )
        )

        OutlinedTextField(
            value = viewModel.userCepState,
            modifier = Modifier.fillMaxWidth(),
            shape = CircleShape,
            textStyle = androidx.compose.ui.text.TextStyle(
                color = Color.Black,
                fontSize = 16.sp
            ),

            onValueChange = {
                viewModel.userCepState = it
                if (viewModel.userCepError != null) viewModel.userCepError = null
                if (it.length >= 8) {
                    viewModel.fetchUserAddress(context,it)
                }
            },
            isError = viewModel.userCepError != null,
            supportingText = {
                if (viewModel.userCepError != null) {
                    Text(
                        text = viewModel.userCepError!!,
                        color = MaterialTheme.colorScheme.error // Ou Color.Red
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                cursorColor = color,
                focusedBorderColor = color,
                focusedLabelColor = color,
                unfocusedBorderColor = color,
                unfocusedLabelColor = color,

                errorBorderColor = Color.Red,
                errorLabelColor = Color.Red,
                errorCursorColor = Color.Red,
                errorTrailingIconColor = Color.Red
            ),

            label = { Text("CEP") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            trailingIcon = {
                if (viewModel.isLoadingCep) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    IconButton(onClick = { viewModel.fetchUserAddress(context, viewModel.userCepState) }) {
                        Icon(Icons.Default.Search, contentDescription = "Buscar")
                    }
                }
            }
        )

        OutlinedTextField(
            value = viewModel.userAddressState,
            onValueChange = { viewModel.userAddressState = it },
            label = { Text("Endereço") },
                    modifier = Modifier.fillMaxWidth(),
            shape = CircleShape,
            textStyle = androidx.compose.ui.text.TextStyle(
                color = Color.Black,
                fontSize = 16.sp
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                cursorColor = color,
                focusedBorderColor = color,
                focusedLabelColor = color,
                unfocusedBorderColor = color,
                unfocusedLabelColor = color,
            ),
        )


//        OutlinedTextField(
//            value = address,
//            onValueChange = { address = it },
//            label = { Text("Endereço") },
//            modifier = Modifier.fillMaxWidth(),
//            shape = CircleShape,
//            textStyle = androidx.compose.ui.text.TextStyle(
//                color = Color.Black,
//                fontSize = 16.sp
//            ),
//            colors = OutlinedTextFieldDefaults.colors(
//                focusedContainerColor = Color.White,
//                unfocusedContainerColor = Color.White,
//                disabledContainerColor = Color.White,
//                cursorColor = color,
//                focusedBorderColor = color,
//                focusedLabelColor = color,
//                unfocusedBorderColor = color,
//                unfocusedLabelColor = color,
//            )
//        )

        Spacer(modifier = Modifier.height(20.dp))



        Button(
            onClick = {
                if (user != null) {
                    FBDatabase.updateProfile(
                        name = name,
                        phone = phone,
                        cep = viewModel.userCepState,
                        address = viewModel.userAddressState,
                        lat = viewModel.userLatState,
                        lng = viewModel.userLngState,
                        onSuccess = {
                            Toast.makeText(context, "Dados atualizados com sucesso!", Toast.LENGTH_SHORT).show()

                            navController.popBackStack()
                        },

                        onFailure = {
                            Toast.makeText(context, "Erro ao salvar: ${it.message}", Toast.LENGTH_LONG).show()
                        }
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = color)
        ) {
            Text("Salvar Alterações", color = Color.White)
        }

        TextButton(onClick = { navController.popBackStack() }) {
            Text("Cancelar", color = Color.Gray)
        }
    }
}

