package com.rodamientosyfierros.repository

import android.content.ContentValues
import com.rodamientosyfierros.data.DatabaseHelper
import com.rodamientosyfierros.models.Factura

class FacturaRepository(private val dbHelper: DatabaseHelper) {

    /**
     * Obtener todas las facturas
     */
    fun obtenerTodas(): List<Factura> {
        val db = dbHelper.readableDatabase
        val facturas = mutableListOf<Factura>()

        val cursor = db.query(
            DatabaseHelper.TABLE_FACTURAS,
            null,
            null,
            null,
            null,
            null,
            "${DatabaseHelper.COL_FECHA_FACTURA} DESC"
        )

        cursor?.use {
            if (it.moveToFirst()) {
                do {
                    val idFacturaIdx = it.getColumnIndex(DatabaseHelper.COL_ID_FACTURA)
                    val idPedidoIdx = it.getColumnIndex(DatabaseHelper.COL_ID_PEDIDO_FACTURA_FK)
                    val fechaIdx = it.getColumnIndex(DatabaseHelper.COL_FECHA_FACTURA)
                    val valorTotalIdx = it.getColumnIndex(DatabaseHelper.COL_VALOR_TOTAL)

                    val factura = Factura(
                        idFactura = it.getInt(idFacturaIdx),
                        idPedido = it.getInt(idPedidoIdx),
                        fecha = it.getString(fechaIdx) ?: "",
                        valorTotal = it.getDouble(valorTotalIdx)
                    )
                    facturas.add(factura)
                } while (it.moveToNext())
            }
        }

        return facturas
    }

    /**
     * Obtener una factura por ID
     */
    fun obtenerPorId(idFactura: Int): Factura? {
        val db = dbHelper.readableDatabase

        val cursor = db.query(
            DatabaseHelper.TABLE_FACTURAS,
            null,
            "${DatabaseHelper.COL_ID_FACTURA} = ?",
            arrayOf(idFactura.toString()),
            null,
            null,
            null
        )

        var factura: Factura? = null
        cursor?.use {
            if (it.moveToFirst()) {
                val idPedidoIdx = it.getColumnIndex(DatabaseHelper.COL_ID_PEDIDO_FACTURA_FK)
                val fechaIdx = it.getColumnIndex(DatabaseHelper.COL_FECHA_FACTURA)
                val valorTotalIdx = it.getColumnIndex(DatabaseHelper.COL_VALOR_TOTAL)

                factura = Factura(
                    idFactura = idFactura,
                    idPedido = it.getInt(idPedidoIdx),
                    fecha = it.getString(fechaIdx) ?: "",
                    valorTotal = it.getDouble(valorTotalIdx)
                )
            }
        }

        return factura
    }

    /**
     * Obtener factura por ID de pedido
     */
    fun obtenerPorIdPedido(idPedido: Int): Factura? {
        val db = dbHelper.readableDatabase

        val cursor = db.query(
            DatabaseHelper.TABLE_FACTURAS,
            null,
            "${DatabaseHelper.COL_ID_PEDIDO_FACTURA_FK} = ?",
            arrayOf(idPedido.toString()),
            null,
            null,
            null
        )

        var factura: Factura? = null
        cursor?.use {
            if (it.moveToFirst()) {
                val idFacturaIdx = it.getColumnIndex(DatabaseHelper.COL_ID_FACTURA)
                val fechaIdx = it.getColumnIndex(DatabaseHelper.COL_FECHA_FACTURA)
                val valorTotalIdx = it.getColumnIndex(DatabaseHelper.COL_VALOR_TOTAL)

                factura = Factura(
                    idFactura = it.getInt(idFacturaIdx),
                    idPedido = idPedido,
                    fecha = it.getString(fechaIdx) ?: "",
                    valorTotal = it.getDouble(valorTotalIdx)
                )
            }
        }

        return factura
    }

    /**
     * Insertar una nueva factura
     */
    fun insertar(factura: Factura): Long {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(DatabaseHelper.COL_ID_PEDIDO_FACTURA_FK, factura.idPedido)
            put(DatabaseHelper.COL_FECHA_FACTURA, factura.fecha)
            put(DatabaseHelper.COL_VALOR_TOTAL, factura.valorTotal)
        }

        return db.insert(DatabaseHelper.TABLE_FACTURAS, null, values)
    }

    /**
     * Actualizar una factura existente
     */
    fun actualizar(factura: Factura): Int {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(DatabaseHelper.COL_FECHA_FACTURA, factura.fecha)
            put(DatabaseHelper.COL_VALOR_TOTAL, factura.valorTotal)
        }

        return db.update(
            DatabaseHelper.TABLE_FACTURAS,
            values,
            "${DatabaseHelper.COL_ID_FACTURA} = ?",
            arrayOf(factura.idFactura.toString())
        )
    }

    /**
     * Eliminar una factura por ID
     */
    fun eliminar(idFactura: Int): Int {
        val db = dbHelper.writableDatabase

        return db.delete(
            DatabaseHelper.TABLE_FACTURAS,
            "${DatabaseHelper.COL_ID_FACTURA} = ?",
            arrayOf(idFactura.toString())
        )
    }

    /**
     * Eliminar factura por ID de pedido
     */
    fun eliminarPorIdPedido(idPedido: Int): Int {
        val db = dbHelper.writableDatabase

        return db.delete(
            DatabaseHelper.TABLE_FACTURAS,
            "${DatabaseHelper.COL_ID_PEDIDO_FACTURA_FK} = ?",
            arrayOf(idPedido.toString())
        )
    }

    /**
     * Obtener facturas de un rango de fechas
     */
    fun obtenerPorRangoFechas(fechaInicio: String, fechaFin: String): List<Factura> {
        val db = dbHelper.readableDatabase
        val facturas = mutableListOf<Factura>()

        val cursor = db.query(
            DatabaseHelper.TABLE_FACTURAS,
            null,
            "${DatabaseHelper.COL_FECHA_FACTURA} BETWEEN ? AND ?",
            arrayOf(fechaInicio, fechaFin),
            null,
            null,
            "${DatabaseHelper.COL_FECHA_FACTURA} DESC"
        )

        cursor?.use {
            if (it.moveToFirst()) {
                do {
                    val idFacturaIdx = it.getColumnIndex(DatabaseHelper.COL_ID_FACTURA)
                    val idPedidoIdx = it.getColumnIndex(DatabaseHelper.COL_ID_PEDIDO_FACTURA_FK)
                    val fechaIdx = it.getColumnIndex(DatabaseHelper.COL_FECHA_FACTURA)
                    val valorTotalIdx = it.getColumnIndex(DatabaseHelper.COL_VALOR_TOTAL)

                    val factura = Factura(
                        idFactura = it.getInt(idFacturaIdx),
                        idPedido = it.getInt(idPedidoIdx),
                        fecha = it.getString(fechaIdx) ?: "",
                        valorTotal = it.getDouble(valorTotalIdx)
                    )
                    facturas.add(factura)
                } while (it.moveToNext())
            }
        }

        return facturas
    }

    /**
     * Calcular ingresos totales
     */
    fun calcularIngresos(): Double {
        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery(
            "SELECT SUM(${DatabaseHelper.COL_VALOR_TOTAL}) FROM ${DatabaseHelper.TABLE_FACTURAS}",
            null
        )

        var ingresos = 0.0
        cursor?.use {
            if (it.moveToFirst()) {
                ingresos = it.getDouble(0)
            }
        }

        return ingresos
    }
}