package com.rodamientosyfierros.models

data class PedidoProducto(
    val id: Int = 0,
    val idPedido: Int,
    val idProducto: Int,
    val cantidad: Int
)