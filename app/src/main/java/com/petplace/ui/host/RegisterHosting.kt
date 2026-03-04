package com.petplace.ui.host

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.petplace.MainViewModel
import com.petplace.model.HostingType
import com.petplace.ui.theme.PrimaryPink
import java.math.BigDecimal

@Composable
fun RegisterHosting(navController: NavController, viewModel: MainViewModel) {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(HostingType.COMPARTILHADO) }
    var dailyRate by remember { mutableStateOf("") }
    var vacancies by remember { mutableStateOf("") }
    var size by remember { mutableStateOf("") }
//    var address by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var typeDropdownExpanded by remember { mutableStateOf(false) }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = PrimaryPink
                )
            }

//            Text(
//                text = "Nova Hospedagem",
//                fontSize = 24.sp,
//                fontWeight = FontWeight.Bold,
//                color = PrimaryPink,
//                modifier = Modifier.align(Alignment.Center)
//            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.LightGray)
                .clickable { galleryLauncher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if (selectedImageUri == null) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.AddAPhoto,
                        contentDescription = "Adicionar Foto",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Adicionar foto do local", color = Color.White, fontSize = 14.sp)
                }
            } else {
                AsyncImage(
                    model = selectedImageUri,
                    contentDescription = "Foto da Hospedagem",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))


        HostingInput(
            value = name,
            onValueChange = { name = it },
            placeholder = "Nome",
            label = "Nome *"
        )

        Box(modifier = Modifier.fillMaxWidth(0.9f)) {
            OutlinedTextField(
                value = type.descricao,
                onValueChange = {},
                readOnly = true,
                label = { Text("Tipo de Hospedagem *") },
                trailingIcon = {
                    Icon(
                        imageVector = if (typeDropdownExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                        contentDescription = "Selecionar tipo"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { typeDropdownExpanded = true },
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = Color.Black,
                    disabledBorderColor = PrimaryPink,
                    disabledLabelColor = PrimaryPink,
                    disabledContainerColor = Color.White,
                    disabledTrailingIconColor = PrimaryPink
                ),
                shape = CircleShape
            )

            Box(modifier = Modifier
                .matchParentSize()
                .clickable { typeDropdownExpanded = true })

            DropdownMenu(
                expanded = typeDropdownExpanded,
                onDismissRequest = { typeDropdownExpanded = false },
                modifier = Modifier.background(Color.White)
            ) {
                HostingType.values().forEach { typeEnum ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = typeEnum.descricao,
                                fontSize = 16.sp,
                                color = Color.Black
                            )
                        },
                        onClick = {
                            type = typeEnum
                            typeDropdownExpanded = false
                        }
                    )
                }
            }
        }

        HostingInput(
            value = dailyRate,
            onValueChange = {
                if (it.all { char -> char.isDigit() || char == '.' }) dailyRate = it
            },
            placeholder = "Ex: 50.00",
            label = "Valor da Diária (R$) *",
            keyboardType = KeyboardType.Decimal
        )

        HostingInput(
            value = vacancies,
            onValueChange = {
                if (it.all { char -> char.isDigit() }) vacancies = it
            },
            placeholder = "Ex: 2",
            label = "Número de Vagas *",
            keyboardType = KeyboardType.Number
        )

        HostingInput(
            value = size,
            onValueChange = {
                if (it.all { char -> char.isDigit() }) size = it
            },
            placeholder = "m2",
            label = "Tamanho",
            keyboardType = KeyboardType.Number
        )

        OutlinedTextField(
            value = viewModel.hostingCepState,
            shape = CircleShape,
            modifier = Modifier.fillMaxWidth(fraction = 0.9f),
            textStyle = androidx.compose.ui.text.TextStyle(
                color = Color.Black,
                fontSize = 16.sp
            ),

            onValueChange = {
                viewModel.hostingCepState = it
                if (viewModel.hostingCepError != null) viewModel.hostingCepError = null
                if (it.length >= 8) {
                    viewModel.fetchHostingAddress(context, it)
                }
            },
            isError = viewModel.hostingCepError != null,
            supportingText = {
                if (viewModel.hostingCepError != null) {
                    Text(
                        text = viewModel.hostingCepError!!,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                cursorColor = PrimaryPink,
                focusedBorderColor = PrimaryPink,
                focusedLabelColor = PrimaryPink,
                unfocusedBorderColor = PrimaryPink,
                unfocusedLabelColor = PrimaryPink,

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
                    IconButton(onClick = { viewModel.fetchHostingAddress(context, viewModel.hostingCepState) }) {
                        Icon(Icons.Default.Search, contentDescription = "Buscar")
                    }
                }
            }
        )

        OutlinedTextField(
            value = viewModel.hostingAddressState,
            onValueChange = { viewModel.hostingAddressState = it },
            modifier = Modifier.fillMaxWidth(fraction = 0.9f),
            label = { Text("Endereço") },
            shape = CircleShape,
            textStyle = androidx.compose.ui.text.TextStyle(
                color = Color.Black,
                fontSize = 16.sp
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                cursorColor = PrimaryPink,
                focusedBorderColor = PrimaryPink,
                focusedLabelColor = PrimaryPink,
                unfocusedBorderColor = PrimaryPink,
                unfocusedLabelColor = PrimaryPink,
            ),
        )


        HostingInput(
            value = description,
            onValueChange = { description = it },
            placeholder = "Descreva o local, regras, etc.",
            label = "Descrição",
            singleLine = false
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (name.isNotEmpty() && dailyRate.isNotEmpty() && vacancies.isNotEmpty()) {

                    val rateDouble = dailyRate.toDoubleOrNull() ?: 0.0
                    val sizeDouble = size.toDoubleOrNull() ?: 0.0
                    val vacanciesInt = vacancies.toIntOrNull() ?: 0

                    viewModel.saveNewHosting(
                        name = name,
                        type = type,
                        dailyRate = rateDouble,
                        vacancies = vacanciesInt,
                        size = sizeDouble,
                        cep = viewModel.hostingCepState,
                        address = viewModel.hostingAddressState,
                        lat = viewModel.hostingLatState,
                        lng = viewModel.hostingLngState,
                        context = context,
                        description = description,
                        imageUri = selectedImageUri,
                        onSuccess = {
                            Toast.makeText(context, "Hospedagem criada!", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        },
                        onFailure = { e ->
                            Toast.makeText(context, "Erro: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    )
                } else {
                    Toast.makeText(context, "Preencha os campos obrigatórios (*)", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryPink)
        ) {
            Text("Salvar Hospedagem", color = Color.White)
        }

        TextButton(onClick = { navController.popBackStack() }) {
            Text("Cancelar", color = Color.Gray)
        }
    }
}

@Composable
fun HostingInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(text = placeholder, color = Color.Gray) },
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(fraction = 0.9f),
        shape = CircleShape,
        textStyle = androidx.compose.ui.text.TextStyle(
            color = Color.Black,
            fontSize = 16.sp
        ),
        singleLine = singleLine,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
            cursorColor = PrimaryPink,
            focusedBorderColor = PrimaryPink,
            focusedLabelColor = PrimaryPink,
            unfocusedBorderColor = PrimaryPink,
            unfocusedLabelColor = PrimaryPink,
        )
    )
}