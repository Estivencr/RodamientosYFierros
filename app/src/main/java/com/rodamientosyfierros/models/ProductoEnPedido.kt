package com.rodamientosyfierros.models

data class ProductoEnPedido(
    val idProducto: Int,
    val nombre: String,
    val fabricante: String,
    val valor: Double,
    val cantidad: Int
)