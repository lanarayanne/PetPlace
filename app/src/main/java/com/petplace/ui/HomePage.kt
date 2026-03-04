package com.petplace.ui

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerFormatter
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerDefaults
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.petplace.MainViewModel
import com.petplace.model.Favorite
import com.petplace.model.PlacePreview
import com.petplace.ui.nav.Route
import com.petplace.ui.theme.PrimaryGreen
import java.math.BigDecimal
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    navController: NavController
) {
    val hostingList = viewModel.searchResults
    val activity = LocalActivity.current as Activity
    val hasSearched = viewModel.hasSearched
    var showSortSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val user = viewModel.user
    val isUserHost = viewModel.isHostMode
//    val userLocation = user?.address ?: ""
//    val userPetsCount = user?.pets?.size ?: 0

    LaunchedEffect(user) {
        if (user != null) {
            val userLocation = user.address ?: ""
            val userPetsCount = user.pets?.size ?: 0

            viewModel.updateSearch(
                viewModel.currentSearch.copy(
                    location = userLocation,
//                    petsCount = if (userPetsCount > 0) userPetsCount else 1
                    petsCount = userPetsCount

                )
            )

            viewModel.performSearch()
            viewModel.onSearchClicked()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        SearchComposable(
            modifier = Modifier.fillMaxWidth(),
            viewModel = viewModel
        )

        if (hasSearched) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "${hostingList.size} Resultado(s)",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.weight(1f))

                        Row(
                            modifier = Modifier
                                .clickable { showSortSheet = true }
                                .padding(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.SwapVert, null, tint = Color.Gray)
                            Spacer(modifier = Modifier.size(2.dp))
                            Text("Ordenar", fontSize = 12.sp, color = Color.Gray)
                        }

//                        Icon(Icons.Default.SwapVert, null, tint = Color.Gray)
//                        Spacer(modifier = Modifier.size(2.dp))
//                        Text("Ordenar", fontSize = 12.sp, color = Color.Gray)

//                        Spacer(modifier = Modifier.size(4.dp))
//                        Icon(Icons.Default.FilterList, null, tint = Color.Gray)
//                        Spacer(modifier = Modifier.size(2.dp))
//                        Text("Filtrar", fontSize = 12.sp, color = Color.Gray)
                    }
                }

                items(hostingList, key = { it.id }) { item ->
                    PreviewItem(
                        preview = item,
                        showFavorite = true,
                        onClick = {
                            navController.navigate(Route.HostingDescription(item.id))
                        },
                        onFavoriteClick = {
                            viewModel.toggleFavorite(item.id)
                        }
                    )
                }
            }
        } else {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = Color.LightGray,
                        modifier = Modifier.size(80.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Faça uma pesquisa para\nver as hospedagens",
                        color = Color.Gray,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }


    if (showSortSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSortSheet = false },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 48.dp)
            ) {

                TextButton(
                    onClick = {
                        viewModel.sortByLowestPrice()
                        showSortSheet = false
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Menor Preço", color = Color.Black, modifier = Modifier.fillMaxWidth().padding(start = 8.dp))
                }

                TextButton(
                    onClick = {
                        viewModel.sortByHighestPrice()
                        showSortSheet = false
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Maior Preço", color = Color.Black, modifier = Modifier.fillMaxWidth().padding(start = 8.dp))
                }
                TextButton(
                    onClick = {
                        viewModel.sortByShortestDistance()
                        showSortSheet = false
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Mais próximo", color = Color.Black, modifier = Modifier.fillMaxWidth().padding(start = 8.dp))
                }
                TextButton(
                    onClick = {
                        viewModel.sortByGreatestDistance()
                        showSortSheet = false
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Mais distante", color = Color.Black, modifier = Modifier.fillMaxWidth().padding(start = 8.dp))
                }
                TextButton(
                    onClick = {
                        viewModel.sortByRatings()
                        showSortSheet = false
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Mais estrelas", color = Color.Black, modifier = Modifier.fillMaxWidth().padding(start = 8.dp))
                }
                TextButton(
                    onClick = {
                        viewModel.sortByEvaluations()
                        showSortSheet = false
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Mais avaliações", color = Color.Black, modifier = Modifier.fillMaxWidth().padding(start = 8.dp))
                }
                TextButton(
                    onClick = {
                        viewModel.sortByGreatestSize()
                        showSortSheet = false
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Maior tamanho", color = Color.Black, modifier = Modifier.fillMaxWidth().padding(start = 8.dp))
                }
            }
        }
    }


}

@Composable
fun PreviewItem(
    preview: PlacePreview,
    showFavorite: Boolean,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                if (preview.picture != null) {
                    AsyncImage(
                        model = preview.picture,
                        contentDescription = "Foto de ${preview.name}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Sem foto",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(110.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = preview.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.Black
                        )
                        if(showFavorite) {
                            Icon(
                                imageVector =
                                    if (preview.isFavorite)
                                        Icons.Filled.Favorite
                                    else
                                        Icons.Outlined.FavoriteBorder,
                                contentDescription = "Favoritar",
                                tint = if (preview.isFavorite) Color.Red else Color.Gray,
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable {
                                        onFavoriteClick()
                                    }
                            )
                        }

                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val yellowColor = Color(0xFFFFC107)
                        repeat(5) { index ->
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = null,
                                tint = if (index < preview.rating) yellowColor else Color.LightGray,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Text(
                        text = "${preview.evaluation} avaliações",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    if(preview.distance != null){
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.LocationOn,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "${preview.distance} m de distância",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "${preview.type} - ${preview.size} m²",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    Text(
                        text = formatCurrency(preview.value),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black
                    )
                    if(preview.dailyCount != null && preview.petCount != null){
                        Text(
                            text = "${preview.dailyCount} diária, ${preview.petCount} pets",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

fun formatCurrency(amount: BigDecimal): String {
    val format = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    return format.format(amount)
}

@Composable
fun SearchComposable(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel

) {
    val user = viewModel.user
    val userLocation = user?.address ?: ""
    val userPetsCount = user?.pets?.size ?: 0

    val activity = LocalActivity.current as Activity

    val search = viewModel.currentSearch
    var showPicker by remember { mutableStateOf(false) }

    val displayDate = remember(search.startDate, search.endDate) {
        if (search.startDate != null && search.endDate != null) {
            val format = SimpleDateFormat("dd/MM", Locale("pt", "BR"))
            "${format.format(search.startDate)} - ${format.format(search.endDate)}"
        } else {
            ""
        }
    }

    //TODO
    LaunchedEffect(Unit) {
        viewModel.updateSearch(
            search.copy(
                location = userLocation,
                petsCount = userPetsCount
            )
        )

        viewModel.performSearch()
        viewModel.onSearchClicked()

    }


    Column(
        modifier = modifier
            .background(PrimaryGreen)
            .padding(horizontal = 16.dp)
            .imePadding(),
        horizontalAlignment = CenterHorizontally

    ) {
        Text(
            text = "PetPlace",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(10.dp))

//        SearchInput(
//            value = location,
//            onValueChange = { location = it },
//            placeholder = "Localização",
//            icon = Icons.Default.LocationOn
//        )

//        Spacer(modifier = Modifier.height(10.dp))

//        SearchInput(
//            value = search.checkin.toString(),
//            onValueChange = { period = it },
//            placeholder = "Selecionar Período",
//            icon = Icons.Default.DateRange
//        )

        DateRangeInput(
            text = displayDate,
            onClick = { showPicker = true }
        )

        if (showPicker) {
            CustomDatePickerDialog(
                onDismiss = { showPicker = false },
                onConfirm = { startMillis, endMillis, days ->
                    if (startMillis != null && endMillis != null) {
                        val startDate = Date(startMillis)
                        val endDate = Date(endMillis)

                        viewModel.updateSearch(
                            search.copy(
                                startDate = startDate,
                                endDate = endDate,
                                dailyRates = days
                            )
                        )
                    }
                    showPicker = false
                }
            )
        }


        Spacer(modifier = Modifier.height(10.dp))

//        SearchInput(
//            value = animals,
//            onValueChange = { animals = it },
//            placeholder = "Selecionar Animais",
//            icon = Icons.Default.Pets
//        )
//
//        Spacer(modifier = Modifier.height(10.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth(fraction = 0.9f)
                .heightIn(min = 50.dp),
            shape = CircleShape,
            border = BorderStroke(1.dp, Color.White),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.White
            ),
            onClick = {
                viewModel.performSearch()
                viewModel.onSearchClicked()

                Toast.makeText(
                    activity,
                    "Buscando...",
                    Toast.LENGTH_LONG
                ).show()

            } ) {
            Text("Pesquisar")
        }
        Spacer(modifier = Modifier.height(10.dp))

    }

}

@Composable
fun SearchInput(
    value: String,
    onValueChange: (String) -> Unit, //TODO: o que é?
    placeholder: String,
    icon: ImageVector? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(text = placeholder, color = Color.Gray) },
        modifier = Modifier
            .fillMaxWidth(fraction = 0.9f)
            .heightIn(min = 40.dp),
        shape = CircleShape,
        textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black),
        singleLine = true,

        leadingIcon = if (icon != null) {
            { Icon(imageVector = icon, contentDescription = null, tint = Color.Gray) }
        } else null,

        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
        )
    )
}

@Composable
fun DateRangeInput(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(fraction = 0.9f)
            .heightIn(min = 40.dp)
            .clip(CircleShape)
            .background(Color.White)
            .clickable { onClick() },
        contentAlignment = Alignment.CenterStart
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = {},
            readOnly = true,
            enabled = false,
            placeholder = { Text("Selecionar Período", color = Color.Gray) },
            leadingIcon = {
                Icon(Icons.Default.DateRange, contentDescription = null, tint = Color.Gray)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = CircleShape,
            textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black),
            singleLine = true,

            colors = OutlinedTextFieldDefaults.colors(
                disabledContainerColor = Color.Transparent,
                disabledBorderColor = Color.Transparent,
                disabledTextColor = Color.Black,
                disabledPlaceholderColor = Color.Gray,
                disabledLeadingIconColor = Color.Gray
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: (Long?, Long?, Int) -> Unit
) {


    val selectableDates = object : SelectableDates {
        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            val todayUtc = calendar.timeInMillis

            return utcTimeMillis >= todayUtc
        }

    }

    val state = rememberDateRangePickerState(
        selectableDates = selectableDates
    )


    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(
                    modifier = Modifier
                        .height(400.dp)
                        .fillMaxWidth()
                ) {
                    DateRangePicker(
                        state = state,
                        title = null,
                        headline = null,
                        showModeToggle = false,
                        colors = DatePickerDefaults.colors(
                            containerColor = Color.White,
                            titleContentColor = Color.Black,
                            headlineContentColor = Color.Black,
                            weekdayContentColor = Color(0xFF2D0C57),
                            subheadContentColor = Color.Black,
                            yearContentColor = Color.Black,
                            currentYearContentColor = PrimaryGreen,
                            selectedYearContentColor = Color.White,
                            selectedYearContainerColor = PrimaryGreen,
                            dayContentColor = Color.Gray,
                            disabledDayContentColor = Color.LightGray,
                            selectedDayContentColor = Color.White,
                            disabledSelectedDayContentColor = Color.Gray,
                            selectedDayContainerColor = PrimaryGreen,
                            dayInSelectionRangeContainerColor = PrimaryGreen.copy(alpha = 0.3f),
                            dayInSelectionRangeContentColor = Color.Black,
                            todayContentColor = PrimaryGreen,
                            todayDateBorderColor = PrimaryGreen
                        ),
                        modifier = Modifier.background(Color.White)
                    )
                }

                val start = state.selectedStartDateMillis
                val end = state.selectedEndDateMillis
                val daysCount = remember(start, end) {
                    if (start != null && end != null) {
                        val diff = end - start
                        TimeUnit.MILLISECONDS.toDays(diff).toInt()
                    } else 0
                }

                if (start != null && end != null) {
                    val format = SimpleDateFormat("dd MMM", Locale("pt", "BR"))
                    val startText = format.format(Date(start))
                    val endText = format.format(Date(end))

                    Text(
                        text = "$startText - $endText • $daysCount diárias",
                        color = Color.DarkGray,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                } else {
                    Text(
                        text = "Selecione as datas de entrada e saída",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                }

                Button(
                    onClick = {
                        onConfirm(state.selectedStartDateMillis, state.selectedEndDateMillis, daysCount)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                    shape = RoundedCornerShape(25.dp),
                    enabled = state.selectedEndDateMillis != null
                ) {
                    Text("Selecionar datas", fontSize = 16.sp, color = Color.White)
                }
            }
        }
    }
}

