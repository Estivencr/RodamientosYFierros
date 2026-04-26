package com.rodamientosyfierros.models

data class Pedido(
    val idPedido: Int = 0,
    val idCliente: Int,
    val descripcion: String,
    val fecha: String
)