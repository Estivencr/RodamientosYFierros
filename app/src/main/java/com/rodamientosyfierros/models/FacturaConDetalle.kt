package com.rodamientosyfierros.models

data class FacturaConDetalle(
    val factura: Factura,
    val pedido: Pedido,
    val cliente: Cliente,
    val productos: List<ProductoEnPedido>
)