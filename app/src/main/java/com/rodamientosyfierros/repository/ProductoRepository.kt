package com.rodamientosyfierros.repository

import android.content.ContentValues
import com.rodamientosyfierros.data.DatabaseHelper
import com.rodamientosyfierros.models.Producto

class ProductoRepository(private val dbHelper: DatabaseHelper) {

    /**
     * Obtener todos los productos
     */
    fun obtenerTodos(): List<Producto> {
        val db = dbHelper.readableDatabase
        val productos = mutableListOf<Producto>()

        val cursor = db.query(
            DatabaseHelper.TABLE_PRODUCTOS,
            null,
            null,
            null,
            null,
            null,
            null
        )

        cursor?.use {
            if (it.moveToFirst()) {
                do {
                    val idIdx = it.getColumnIndex(DatabaseHelper.COL_ID_PRODUCTO)
                    val fabricanteIdx = it.getColumnIndex(DatabaseHelper.COL_FABRICANTE)
                    val valorIdx = it.getColumnIndex(DatabaseHelper.COL_VALOR)

                    val producto = Producto(
                        idProducto = it.getInt(idIdx),
                        fabricante = it.getString(fabricanteIdx) ?: "",
                        valor = it.getDouble(valorIdx)
                    )
                    productos.add(producto)
                } while (it.moveToNext())
            }
        }

        return productos
    }

    /**
     * Obtener un producto por ID
     */
    fun obtenerPorId(idProducto: Int): Producto? {
        val db = dbHelper.readableDatabase

        val cursor = db.query(
            DatabaseHelper.TABLE_PRODUCTOS,
            null,
            "${DatabaseHelper.COL_ID_PRODUCTO} = ?",
            arrayOf(idProducto.toString()),
            null,
            null,
            null
        )

        var producto: Producto? = null
        cursor?.use {
            if (it.moveToFirst()) {
                val fabricanteIdx = it.getColumnIndex(DatabaseHelper.COL_FABRICANTE)
                val valorIdx = it.getColumnIndex(DatabaseHelper.COL_VALOR)

                producto = Producto(
                    idProducto = idProducto,
                    fabricante = it.getString(fabricanteIdx) ?: "",
                    valor = it.getDouble(valorIdx)
                )
            }
        }

        return producto
    }

    /**
     * Insertar un nuevo producto
     */
    fun insertar(producto: Producto): Long {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(DatabaseHelper.COL_FABRICANTE, producto.fabricante)
            put(DatabaseHelper.COL_VALOR, producto.valor)
        }

        return db.insert(DatabaseHelper.TABLE_PRODUCTOS, null, values)
    }

    /**
     * Actualizar un producto existente
     */
    fun actualizar(producto: Producto): Int {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(DatabaseHelper.COL_FABRICANTE, producto.fabricante)
            put(DatabaseHelper.COL_VALOR, producto.valor)
        }

        return db.update(
            DatabaseHelper.TABLE_PRODUCTOS,
            values,
            "${DatabaseHelper.COL_ID_PRODUCTO} = ?",
            arrayOf(producto.idProducto.toString())
        )
    }

    /**
     * Eliminar un producto por ID
     */
    fun eliminar(idProducto: Int): Int {
        val db = dbHelper.writableDatabase

        return db.delete(
            DatabaseHelper.TABLE_PRODUCTOS,
            "${DatabaseHelper.COL_ID_PRODUCTO} = ?",
            arrayOf(idProducto.toString())
        )
    }

    /**
     * Buscar productos por fabricante
     */
    fun buscarPorFabricante(fabricante: String): List<Producto> {
        val db = dbHelper.readableDatabase
        val productos = mutableListOf<Producto>()

        val cursor = db.query(
            DatabaseHelper.TABLE_PRODUCTOS,
            null,
            "${DatabaseHelper.COL_FABRICANTE} LIKE ?",
            arrayOf("%$fabricante%"),
            null,
            null,
            null
        )

        cursor?.use {
            if (it.moveToFirst()) {
                do {
                    val idIdx = it.getColumnIndex(DatabaseHelper.COL_ID_PRODUCTO)
                    val fabricanteIdx = it.getColumnIndex(DatabaseHelper.COL_FABRICANTE)
                    val valorIdx = it.getColumnIndex(DatabaseHelper.COL_VALOR)

                    val producto = Producto(
                        idProducto = it.getInt(idIdx),
                        fabricante = it.getString(fabricanteIdx) ?: "",
                        valor = it.getDouble(valorIdx)
                    )
                    productos.add(producto)
                } while (it.moveToNext())
            }
        }

        return productos
    }

    /**
     * Obtener productos por rango de precio
     */
    fun obtenerPorRangoValor(valorMin: Double, valorMax: Double): List<Producto> {
        val db = dbHelper.readableDatabase
        val productos = mutableListOf<Producto>()

        val cursor = db.query(
            DatabaseHelper.TABLE_PRODUCTOS,
            null,
            "${DatabaseHelper.COL_VALOR} BETWEEN ? AND ?",
            arrayOf(valorMin.toString(), valorMax.toString()),
            null,
            null,
            "${DatabaseHelper.COL_VALOR} ASC"
        )

        cursor?.use {
            if (it.moveToFirst()) {
                do {
                    val idIdx = it.getColumnIndex(DatabaseHelper.COL_ID_PRODUCTO)
                    val fabricanteIdx = it.getColumnIndex(DatabaseHelper.COL_FABRICANTE)
                    val valorIdx = it.getColumnIndex(DatabaseHelper.COL_VALOR)

                    val producto = Producto(
                        idProducto = it.getInt(idIdx),
                        fabricante = it.getString(fabricanteIdx) ?: "",
                        valor = it.getDouble(valorIdx)
                    )
                    productos.add(producto)
                } while (it.moveToNext())
            }
        }

        return productos
    }
}