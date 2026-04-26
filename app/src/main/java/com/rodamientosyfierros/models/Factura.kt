package com.rodamientosyfierros.models

data class Factura(
    val idFactura: Int = 0,
    val idPedido: Int,
    val fecha: String,
    val valorTotal: Double
)