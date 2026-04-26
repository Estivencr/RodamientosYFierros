package com.rodamientosyfierros.repository

import android.content.ContentValues
import com.rodamientosyfierros.data.DatabaseHelper
import com.rodamientosyfierros.models.Pedido
import com.rodamientosyfierros.models.PedidoConDetalle
import com.rodamientosyfierros.models.ProductoEnPedido

class PedidoRepository(private val dbHelper: DatabaseHelper) {

    /**
     * Obtener todos los pedidos
     */
    fun obtenerTodos(): List<Pedido> {
        val db = dbHelper.readableDatabase
        val pedidos = mutableListOf<Pedido>()

        val cursor = db.query(
            DatabaseHelper.TABLE_PEDIDOS,
            null,
            null,
            null,
            null,
            null,
            "${DatabaseHelper.COL_FECHA_PEDIDO} DESC"
        )

        cursor?.use {
            if (it.moveToFirst()) {
                do {
                    val idIdx = it.getColumnIndex(DatabaseHelper.COL_ID_PEDIDO)
                    val idClienteIdx = it.getColumnIndex(DatabaseHelper.COL_ID_CLIENTE_FK)
                    val descripcionIdx = it.getColumnIndex(DatabaseHelper.COL_DESCRIPCION)
                    val fechaIdx = it.getColumnIndex(DatabaseHelper.COL_FECHA_PEDIDO)

                    val pedido = Pedido(
                        idPedido = it.getInt(idIdx),
                        idCliente = it.getInt(idClienteIdx),
                        descripcion = it.getString(descripcionIdx) ?: "",
                        fecha = it.getString(fechaIdx) ?: ""
                    )
                    pedidos.add(pedido)
                } while (it.moveToNext())
            }
        }

        return pedidos
    }

    /**
     * Obtener un pedido por ID
     */
    fun obtenerPorId(idPedido: Int): Pedido? {
        val db = dbHelper.readableDatabase

        val cursor = db.query(
            DatabaseHelper.TABLE_PEDIDOS,
            null,
            "${DatabaseHelper.COL_ID_PEDIDO} = ?",
            arrayOf(idPedido.toString()),
            null,
            null,
            null
        )

        var pedido: Pedido? = null
        cursor?.use {
            if (it.moveToFirst()) {
                val idClienteIdx = it.getColumnIndex(DatabaseHelper.COL_ID_CLIENTE_FK)
                val descripcionIdx = it.getColumnIndex(DatabaseHelper.COL_DESCRIPCION)
                val fechaIdx = it.getColumnIndex(DatabaseHelper.COL_FECHA_PEDIDO)

                pedido = Pedido(
                    idPedido = idPedido,
                    idCliente = it.getInt(idClienteIdx),
                    descripcion = it.getString(descripcionIdx) ?: "",
                    fecha = it.getString(fechaIdx) ?: ""
                )
            }
        }

        return pedido
    }

    /**
     * Insertar un nuevo pedido
     */
    fun insertar(pedido: Pedido): Long {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(DatabaseHelper.COL_ID_CLIENTE_FK, pedido.idCliente)
            put(DatabaseHelper.COL_DESCRIPCION, pedido.descripcion)
            put(DatabaseHelper.COL_FECHA_PEDIDO, pedido.fecha)
        }

        return db.insert(DatabaseHelper.TABLE_PEDIDOS, null, values)
    }

    /**
     * Actualizar un pedido existente
     */
    fun actualizar(pedido: Pedido): Int {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(DatabaseHelper.COL_ID_CLIENTE_FK, pedido.idCliente)
            put(DatabaseHelper.COL_DESCRIPCION, pedido.descripcion)
            put(DatabaseHelper.COL_FECHA_PEDIDO, pedido.fecha)
        }

        return db.update(
            DatabaseHelper.TABLE_PEDIDOS,
            values,
            "${DatabaseHelper.COL_ID_PEDIDO} = ?",
            arrayOf(pedido.idPedido.toString())
        )
    }

    /**
     * Eliminar un pedido por ID (también elimina sus productos asociados)
     */
    fun eliminar(idPedido: Int): Int {
        val db = dbHelper.writableDatabase

        // Primero eliminar los productos del pedido
        db.delete(
            DatabaseHelper.TABLE_PEDIDO_PRODUCTOS,
            "${DatabaseHelper.COL_ID_PEDIDO_FK} = ?",
            arrayOf(idPedido.toString())
        )

        // Luego eliminar la factura asociada si existe
        db.delete(
            DatabaseHelper.TABLE_FACTURAS,
            "${DatabaseHelper.COL_ID_PEDIDO_FACTURA_FK} = ?",
            arrayOf(idPedido.toString())
        )

        // Finalmente eliminar el pedido
        return db.delete(
            DatabaseHelper.TABLE_PEDIDOS,
            "${DatabaseHelper.COL_ID_PEDIDO} = ?",
            arrayOf(idPedido.toString())
        )
    }

    /**
     * Obtener pedidos de un cliente específico
     */
    fun obtenerPorCliente(idCliente: Int): List<Pedido> {
        val db = dbHelper.readableDatabase
        val pedidos = mutableListOf<Pedido>()

        val cursor = db.query(
            DatabaseHelper.TABLE_PEDIDOS,
            null,
            "${DatabaseHelper.COL_ID_CLIENTE_FK} = ?",
            arrayOf(idCliente.toString()),
            null,
            null,
            "${DatabaseHelper.COL_FECHA_PEDIDO} DESC"
        )

        cursor?.use {
            if (it.moveToFirst()) {
                do {
                    val idIdx = it.getColumnIndex(DatabaseHelper.COL_ID_PEDIDO)
                    val descripcionIdx = it.getColumnIndex(DatabaseHelper.COL_DESCRIPCION)
                    val fechaIdx = it.getColumnIndex(DatabaseHelper.COL_FECHA_PEDIDO)

                    val pedido = Pedido(
                        idPedido = it.getInt(idIdx),
                        idCliente = idCliente,
                        descripcion = it.getString(descripcionIdx) ?: "",
                        fecha = it.getString(fechaIdx) ?: ""
                    )
                    pedidos.add(pedido)
                } while (it.moveToNext())
            }
        }

        return pedidos
    }

    /**
     * Obtener pedido con todos sus detalles (cliente, productos)
     */
    fun obtenerConDetalle(idPedido: Int, clienteRepository: ClienteRepository,
                          productoRepository: ProductoRepository): PedidoConDetalle? {
        val pedido = obtenerPorId(idPedido) ?: return null
        val cliente = clienteRepository.obtenerPorId(pedido.idCliente) ?: return null

        // Obtener productos del pedido
        val db = dbHelper.readableDatabase
        val productosEnPedido = mutableListOf<ProductoEnPedido>()

        val cursor = db.rawQuery(
            """
                SELECT pp.${DatabaseHelper.COL_ID_PRODUCTO_FK}, 
                       p.${DatabaseHelper.COL_FABRICANTE},
                       p.${DatabaseHelper.COL_VALOR},
                       pp.${DatabaseHelper.COL_CANTIDAD}
                FROM ${DatabaseHelper.TABLE_PEDIDO_PRODUCTOS} pp
                JOIN ${DatabaseHelper.TABLE_PRODUCTOS} p 
                    ON pp.${DatabaseHelper.COL_ID_PRODUCTO_FK} = p.${DatabaseHelper.COL_ID_PRODUCTO}
                WHERE pp.${DatabaseHelper.COL_ID_PEDIDO_FK} = ?
            """.trimIndent(),
            arrayOf(idPedido.toString())
        )

        cursor?.use {
            if (it.moveToFirst()) {
                do {
                    val idProductoIdx = it.getColumnIndex(DatabaseHelper.COL_ID_PRODUCTO_FK)
                    val fabricanteIdx = it.getColumnIndex(DatabaseHelper.COL_FABRICANTE)
                    val valorIdx = it.getColumnIndex(DatabaseHelper.COL_VALOR)
                    val cantidadIdx = it.getColumnIndex(DatabaseHelper.COL_CANTIDAD)

                    val productoEnPedido = ProductoEnPedido(
                        idProducto = it.getInt(idProductoIdx),
                        nombre = "Producto ${it.getInt(idProductoIdx)}",
                        fabricante = it.getString(fabricanteIdx) ?: "",
                        valor = it.getDouble(valorIdx),
                        cantidad = it.getInt(cantidadIdx)
                    )
                    productosEnPedido.add(productoEnPedido)
                } while (it.moveToNext())
            }
        }

        val valorTotal = productosEnPedido.sumOf { it.cantidad * it.valor }

        return PedidoConDetalle(
            pedido = pedido,
            cliente = cliente,
            productos = productosEnPedido,
            valorTotal = valorTotal
        )
    }
}