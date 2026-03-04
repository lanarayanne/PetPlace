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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import coil.compose.AsyncImage
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.BuildConfig
import com.petplace.db.fb.FBDatabase
import com.petplace.model.Age
import com.petplace.model.Animal
import com.petplace.model.User
import com.petplace.ui.theme.PrimaryGreen
import com.petplace.ui.theme.PrimaryPink


@Composable
fun EditPetProfile(navController: NavController, viewModel: MainViewModel, petId: Int) {
    val pet = viewModel.user?.pets?.find { it.id == petId }
    val context = LocalContext.current

    var name by remember { mutableStateOf(pet?.name ?: "") }
    var breed by remember { mutableStateOf(pet?.breed ?: "") }
    var animal by remember { mutableStateOf(pet?.animal ?: "") }
    var observations by remember { mutableStateOf(pet?.observations ?: "") }
    var weightText by remember { mutableStateOf(pet?.weight?.toString() ?: "") }
    var selectedAge by remember { mutableStateOf(pet?.age ?: Age.DESCONHECIDO) }
    var color by remember { mutableStateOf(pet?.color ?: "") }

    val isUserHost = viewModel.isHostMode
    val pageColor = if (isUserHost) PrimaryPink else PrimaryGreen

    val existingPictureUrl = pet?.picture
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
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Voltar",
            tint = pageColor,
            modifier = Modifier
                .clickable { navController.popBackStack() }
                .align(Alignment.Start)
        )

        Text(
            text = "Editar Pet",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = pageColor
        )

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
                .clickable { galleryLauncher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if (selectedImageUri != null) {
                AsyncImage(
                    model = selectedImageUri,
                    contentDescription = "Nova Foto do Pet",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else if (existingPictureUrl != null) {
                AsyncImage(
                    model = existingPictureUrl,
                    contentDescription = "Foto Atual do Pet",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.AddAPhoto,
                    contentDescription = "Adicionar Foto",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
        Text("Toque para alterar a foto", fontSize = 12.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nome") },
            modifier = Modifier.fillMaxWidth(),
            shape = CircleShape,
            colors = defaultInputColors(pageColor)
        )

        OutlinedTextField(
            value = animal,
            onValueChange = { animal = it },
            label = { Text("Animal") },
            modifier = Modifier.fillMaxWidth(),
            shape = CircleShape,
            colors = defaultInputColors(pageColor)
        )

        DropdownInput(
            label = "Idade",
            options = Age.values().toList(),
            selectedOption = selectedAge,
            onOptionSelected = { selectedAge = it },
            itemLabel = { it.faixaEtaria },
            color = pageColor
        )

        OutlinedTextField(
            value = weightText,
            onValueChange = {
                if (it.all { char -> char.isDigit() || char == '.' }) {
                    weightText = it
                }
            },
            label = { Text("Peso (kg)") },
            modifier = Modifier.fillMaxWidth(),
            shape = CircleShape,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = defaultInputColors(pageColor)
        )

        OutlinedTextField(
            value = breed,
            onValueChange = { breed = it },
            label = { Text("Raça") },
            modifier = Modifier.fillMaxWidth(),
            shape = CircleShape,
            colors = defaultInputColors(pageColor)
        )

        OutlinedTextField(
            value = color,
            onValueChange = { color = it },
            label = { Text("Cor") },
            modifier = Modifier.fillMaxWidth(),
            shape = CircleShape,
            colors = defaultInputColors(pageColor)
        )


        OutlinedTextField(
            value = observations,
            onValueChange = { observations = it },
            label = { Text("Observações") },
            modifier = Modifier.fillMaxWidth(),
            shape = CircleShape,
            colors = defaultInputColors(pageColor)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (pet != null) {
                    if (selectedImageUri != null) {
                        Toast.makeText(context, "Salvando imagem nova e dados...", Toast.LENGTH_SHORT).show()
                    }

                    viewModel.updatePet(
                        petId = pet.id,
                        name = name,
                        animal = animal,
                        age = selectedAge,
                        weight = weightText.toDoubleOrNull() ?: 0.0,
                        breed = breed,
                        color = color,
                        observations = observations,
                        imageUri = selectedImageUri,
                        existingPictureUrl = existingPictureUrl,
                        onSuccess = {
                            Toast.makeText(context, "Pet atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        },
                        onError = { erro ->
                            Toast.makeText(context, erro, Toast.LENGTH_LONG).show()
                        }
                    )
                } else {
                    Toast.makeText(context, "Preencha os campos obrigatórios (*)", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = pageColor)
        ) {
            Text("Salvar Alterações", color = Color.White)
        }

        TextButton(onClick = { navController.popBackStack() }) {
            Text("Cancelar", color = Color.Gray)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> DropdownInput(
    label: String,
    options: List<T>,
    selectedOption: T?,
    onOptionSelected: (T) -> Unit,
    itemLabel: (T) -> String,
    color: Color
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            readOnly = true,
            value = if (selectedOption != null) itemLabel(selectedOption) else "Selecione...",
            onValueChange = {},
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            shape = CircleShape,
            colors = defaultInputColors(color)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color.White)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(itemLabel(option), color = Color.Black) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

@Composable
fun defaultInputColors(color: Color) = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White,
    disabledContainerColor = Color.White,
    focusedTextColor = Color.Black,
    unfocusedTextColor = Color.Black,
    disabledTextColor = Color.Black,
    cursorColor = color,
    focusedBorderColor = color,
    focusedLabelColor = color,
    unfocusedBorderColor = color,
    unfocusedLabelColor = color,
)