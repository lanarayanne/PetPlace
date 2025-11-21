package com.petplace

import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.petplace.model.Hosting
import com.petplace.model.PlacePreview
import java.math.BigDecimal



class MainViewModel : ViewModel() {
    private val _hosting = getHostings().toMutableStateList()
    val hosting
        get() = _hosting.toList()

}

fun getHostings() = List(3) { i ->
    PlacePreview(i, "Creche Patinhas", 245, 4,72.0, "Picture", false, 10.0, BigDecimal("145"), "Compartilhado", 1, 2)

}