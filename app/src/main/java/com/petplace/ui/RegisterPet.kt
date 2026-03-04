package com.petplace.ui

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBack // Import necessário
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
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
import com.petplace.model.Age
import com.petplace.ui.theme.PrimaryGreen
import com.petplace.ui.theme.PrimaryPink

@Composable
fun RegisterPet(navController: NavController, viewModel: MainViewModel) {
    val context = LocalContext.current
    val currentUser = viewModel.user
    val isUserHost = viewModel.isHostMode

    val color = if (isUserHost) PrimaryPink else PrimaryGreen

    var name by remember { mutableStateOf("") }
    var animalType by remember { mutableStateOf("") }
    var age by remember { mutableStateOf(Age.DESCONHECIDO) }
    var weight by remember { mutableStateOf("") }
    var breed by remember { mutableStateOf("") }
    var birthYear by remember { mutableStateOf("") }
    var colorName by remember { mutableStateOf("") }
    var observations by remember { mutableStateOf("") }

    var ageDropdownExpanded by remember { mutableStateOf(false) }

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
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Voltar",
                    tint = color
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

//            Text(
//                text = "Cadastrar Pet",
//                fontSize = 30.sp,
//                fontWeight = FontWeight.Bold,
//                color = color,
//                modifier = Modifier.align(Alignment.Center)
//            )
        }

        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
                .clickable { galleryLauncher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if (selectedImageUri == null) {
                Icon(Icons.Default.AddAPhoto, contentDescription = "Adicionar Foto", tint = Color.White, modifier = Modifier.size(40.dp))
            } else {
                AsyncImage(
                    model = selectedImageUri,
                    contentDescription = "Foto do Pet",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        PetInfoInput(
            value = name,
            onValueChange = { name = it },
            placeholder = "Nome do Pet",
            label = "Nome *",
            isUserHost = isUserHost
        )

        PetInfoInput(
            value = animalType,
            onValueChange = { animalType = it },
            placeholder = "Ex: Cachorro, Gato",
            label = "Tipo de Animal *",
            isUserHost = isUserHost
        )

        PetInfoInput(
            value = breed,
            onValueChange = { breed = it },
            placeholder = "Ex: Labrador, Siamês",
            label = "Raça",
            isUserHost = isUserHost
        )

        Box(modifier = Modifier.fillMaxWidth(0.9f)) {
            OutlinedTextField(
                value = age.faixaEtaria,
                onValueChange = {},
                readOnly = true,
                label = { Text("Faixa Etária *") },
                trailingIcon = {
                    Icon(
                        imageVector = if (ageDropdownExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                        contentDescription = "Selecionar idade"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { ageDropdownExpanded = true },
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = Color.Black,
                    disabledBorderColor = color,
                    disabledLabelColor = color,
                    disabledContainerColor = Color.White
                ),
                shape = CircleShape
            )

            Box(modifier = Modifier
                .matchParentSize()
                .clickable { ageDropdownExpanded = true })

            DropdownMenu(
                expanded = ageDropdownExpanded,
                onDismissRequest = { ageDropdownExpanded = false },
                modifier = Modifier.background(Color.White)
            ) {
                Age.entries.forEach { ageEnum ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = ageEnum.faixaEtaria,
                                fontSize = 16.sp
                            )
                        },
                        onClick = {
                            age = ageEnum
                            ageDropdownExpanded = false
                        },
                        colors = MenuDefaults.itemColors(
                            textColor = Color.Black,
                        )
                    )
                }
            }
        }

        PetInfoInput(
            value = weight,
            onValueChange = {
                if (it.all { char -> char.isDigit() || char == '.' }) weight = it
            },
            placeholder = "Ex: 12.5",
            label = "Peso (kg) *",
            keyboardType = KeyboardType.Decimal,
            isUserHost = isUserHost
        )

        PetInfoInput(
            value = birthYear,
            onValueChange = {
                if (it.length <= 4 && it.all { char -> char.isDigit() }) birthYear = it
            },
            placeholder = "Ex: 2020",
            label = "Ano de Nascimento",
            keyboardType = KeyboardType.Number,
            isUserHost = isUserHost
        )

        PetInfoInput(
            value = colorName,
            onValueChange = { colorName = it },
            placeholder = "Ex: Preto, Malhado",
            label = "Cor",
            isUserHost = isUserHost
        )

        PetInfoInput(
            value = observations,
            onValueChange = { observations = it },
            placeholder = "Observações adicionais",
            label = "Observações",
            isUserHost = isUserHost
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (name.isNotEmpty() && animalType.isNotEmpty() && weight.isNotEmpty()) {
                    val weightDouble = weight.toDoubleOrNull() ?: 0.0
                    val yearInt = birthYear.toIntOrNull()
                    val color = if (colorName.isBlank()) null else colorName

                    viewModel.saveNewPet(
                        name = name,
                        animal = animalType,
                        breed = if (breed.isBlank()) null else breed,
                        age = age,
                        weight = weightDouble,
                        birthYear = yearInt,
                        color = color,
                        observations = if (observations.isBlank()) null else observations,
                        imageUri = selectedImageUri,
                        onSuccess = {
                            Toast.makeText(
                                context,
                                "Pet salvo com sucesso!",
                                Toast.LENGTH_SHORT
                            ).show()
                            navController.popBackStack()
                        },
                        onError = { erroMsg ->
                            Toast.makeText(context, erroMsg, Toast.LENGTH_LONG).show()
                        }
                    )
                } else {
                    Toast.makeText(
                        context,
                        "Preencha os campos obrigatórios (*)",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = color)
        ) {
            Text("Salvar Pet", color = Color.White)
        }

        TextButton(onClick = { navController.popBackStack() }) {
            Text("Cancelar", color = Color.Gray)
        }
    }
}

@Composable
fun PetInfoInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    isUserHost: Boolean
) {

    val color = if (isUserHost) PrimaryPink else PrimaryGreen

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
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
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
}