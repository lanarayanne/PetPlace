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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.petplace.MainActivity
import com.petplace.MainViewModel
import com.petplace.model.User

@Composable
fun ProfilePage(modifier: Modifier = Modifier, viewModel: MainViewModel) {
    val userData = viewModel.user
    if (userData != null) {
        ProfileData(
            user = userData,
            modifier = modifier
                .fillMaxSize()
                .background(Color.White)
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
    modifier: Modifier = Modifier
) {
    val activity = LocalActivity.current as Activity
    val scrollState = rememberScrollState()

    Column(
        modifier=modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = user.name,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF419D78),
            textAlign = TextAlign.Center,
            fontSize = 30.sp
        )

        Spacer(modifier = Modifier.height(32.dp))
        ProfileInfoRow(label = "Nome", value = user.name)
        ProfileInfoRow(label = "E-mail", value = user.email)
        ProfileInfoRow(label = "Telefone", value = user.phone?: "")
        ProfileInfoRow(label = "Endereço", value = user.address.toString())

        Spacer(modifier = Modifier.height(40.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp))
        {
            ProfileActionButton(text = "Editar Perfil") {
                Toast.makeText(activity, "Editar Perfil", Toast.LENGTH_SHORT).show()
            }
            ProfileActionButton(text = "Alterar Senha") {
                Toast.makeText(activity, "Alterar Senha", Toast.LENGTH_SHORT).show()
            }
            ProfileActionButton(text = "Excluir Conta") {
                Toast.makeText(activity, "Excluir Conta", Toast.LENGTH_SHORT).show()
            }
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
            modifier = Modifier.weight(2f)
        )
    }
}

@Composable
fun ProfileActionButton(text: String, onClick: () -> Unit) {
    Button(
        modifier = Modifier.fillMaxWidth(fraction = 0.9f),
        shape = CircleShape,
        border = BorderStroke(1.dp, Color(0xFF419D78)),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color(0xFF419D78)
        ),
        onClick = onClick
    ) {
        Text(text = text, fontWeight = FontWeight.Bold)
    }
}

