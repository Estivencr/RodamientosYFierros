package com.rodamientosyfierros.models

data class PedidoConDetalle(
    val pedido: Pedido,
    val cliente: Cliente,
    val productos: List<ProductoEnPedido>,
    val valorTotal: Double = productos.sumOf { it.cantidad * it.valor }
)