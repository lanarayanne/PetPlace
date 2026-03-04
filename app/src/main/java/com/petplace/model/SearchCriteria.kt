package com.petplace.model

import java.math.BigDecimal
import java.util.Date

data class SearchCriteria(
    val location: String = "",
    val startDate: Date? = null,
    val endDate: Date? = null,
    val dailyRates: Int = 0,
    val value: BigDecimal = BigDecimal.ZERO,
    val petsCount: Int = 1
)
