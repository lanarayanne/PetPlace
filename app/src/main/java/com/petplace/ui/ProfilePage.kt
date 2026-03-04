package com.petplace.ui

import android.app.Activity
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.petplace.MainActivity
import com.petplace.MainViewModel
import com.petplace.db.fb.FBDatabase
import com.petplace.model.User
import com.petplace.ui.nav.Route
import com.petplace.ui.theme.PrimaryGreen
import com.petplace.ui.theme.PrimaryPink

@Composable
fun ProfilePage(navController: NavController, modifier: Modifier = Modifier, viewModel: MainViewModel, isUserHost: Boolean) {
    val userData = viewModel.user
    if (userData != null) {
        ProfileData(
            user = userData,
            modifier = modifier
                .fillMaxSize()
                .background(Color.White),
            navController = navController,
            isUserHost = isUserHost
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
fun ProfileData(
    user: User,
    isUserHost: Boolean,
    modifier: Modifier = Modifier,
    navController: NavController,
) {
    val activity = LocalActivity.current as Activity
    val scrollState = rememberScrollState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier=modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 40.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = user.name,
            fontWeight = FontWeight.Bold,
            color = if (isUserHost) PrimaryPink else PrimaryGreen,
            textAlign = TextAlign.Center,
            fontSize = 30.sp
        )

        Spacer(modifier = Modifier.height(32.dp))
        ProfileInfoRow(label = "Nome", value = user.name)
        ProfileInfoRow(label = "E-mail", value = user.email)
        ProfileInfoRow(label = "Telefone", value = user.phone?: "")
        ProfileInfoRow(label = "CEP", value = user.cep?: "")
        ProfileInfoRow(label = "Endereço", value = user.address?: "")

        Spacer(modifier = Modifier.height(40.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp))
        {
            ProfileActionButton(
                isUserHost = isUserHost,
                text = "Editar Perfil") {
                navController.navigate(Route.EditProfile)
            }
            ProfileActionButton(
                isUserHost=isUserHost,
                text = "Alterar Senha") {
                Toast.makeText(activity, "Alterar Senha", Toast.LENGTH_SHORT).show()
            }
            ProfileActionButton(
                isUserHost = isUserHost,
                text = "Excluir Conta",
            ) {
                showDeleteDialog = true
            }
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Excluir Conta", color=Color.Black )},
                text = { Text("Tem certeza que deseja excluir sua conta? Essa ação não pode ser desfeita.", color=Color.Black) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            FBDatabase.deleteProfile(
                                onSuccess = {
                                    Toast.makeText(context, "Conta deletada.", Toast.LENGTH_SHORT).show()
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

@Composable
fun ProfileInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "$label:",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun ProfileActionButton(
    text: String,
    isUserHost: Boolean,
    onClick: () -> Unit) {
    Button(
        modifier = Modifier.fillMaxWidth(fraction = 0.9f),
        shape = CircleShape,
        border = BorderStroke(1.dp, if (isUserHost) PrimaryPink else PrimaryGreen),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = if (isUserHost) PrimaryPink else PrimaryGreen
        ),
        onClick = onClick
    ) {
        Text(text = text, fontWeight = FontWeight.Bold)
    }
}

