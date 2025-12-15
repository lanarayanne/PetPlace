package com.petplace.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
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
import com.petplace.db.fb.FBDatabase
import com.petplace.db.fb.toFBUser
import com.petplace.model.User


@Composable
fun EditProfilePage(navController: NavController, viewModel: MainViewModel) {
    val user = viewModel.user
    val context = LocalContext.current

    var name by remember { mutableStateOf(user?.name ?: "") }
    var phone by remember { mutableStateOf(user?.phone ?: "") }
    var address by remember { mutableStateOf(user?.address ?: "") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Editar Perfil",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF419D78)
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
                cursorColor = Color(0xFF419D78),
                focusedBorderColor = Color(0xFF419D78),
                focusedLabelColor = Color(0xFF419D78),
                unfocusedBorderColor = Color(0xFF419D78),
                unfocusedLabelColor = Color(0xFF419D78),
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
                cursorColor = Color(0xFF419D78),
                focusedBorderColor = Color(0xFF419D78),
                focusedLabelColor = Color(0xFF419D78),
                unfocusedBorderColor = Color(0xFF419D78),
                unfocusedLabelColor = Color(0xFF419D78),
            )
        )

        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
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
                cursorColor = Color(0xFF419D78),
                focusedBorderColor = Color(0xFF419D78),
                focusedLabelColor = Color(0xFF419D78),
                unfocusedBorderColor = Color(0xFF419D78),
                unfocusedLabelColor = Color(0xFF419D78),
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (user != null) {
                    FBDatabase.updateProfile(
                        name = name,
                        phone = phone,
                        address = address,
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
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF419D78))
        ) {
            Text("Salvar Alterações", color = Color.White)
        }

        TextButton(onClick = { navController.popBackStack() }) {
            Text("Cancelar", color = Color.Gray)
        }
    }
}

