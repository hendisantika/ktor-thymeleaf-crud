package com.example.models

import java.math.BigDecimal
import java.time.LocalDateTime

data class Product(
    val id: Int = 0,
    val name: String,
    val description: String,
    val price: BigDecimal,
    val quantity: Int,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)