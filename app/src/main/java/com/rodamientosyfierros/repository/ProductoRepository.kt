package com.rodamientosyfierros.repository

import android.content.ContentValues
import com.rodamientosyfierros.data.DatabaseHelper
import com.rodamientosyfierros.models.Producto

class ProductoRepository(private val dbHelper: DatabaseHelper) {

    fun obtenerTodos(): List<Producto> {
        val db = dbHelper.readableDatabase
        val productos = mutableListOf<Producto>()

        val cursor = db.query(
            DatabaseHelper.TABLE_PRODUCTOS,
            null, null, null, null, null,
            "${DatabaseHelper.COL_NOMBRE_PRODUCTO} ASC"
        )

        cursor?.use {
            if (it.moveToFirst()) {
                do {
                    productos.add(cursorToProducto(it))
                } while (it.moveToNext())
            }
        }

        return productos
    }

    fun obtenerPorId(idProducto: Int): Producto? {
        val db = dbHelper.readableDatabase

        val cursor = db.query(
            DatabaseHelper.TABLE_PRODUCTOS,
            null,
            "${DatabaseHelper.COL_ID_PRODUCTO} = ?",
            arrayOf(idProducto.toString()),
            null, null, null
        )

        var producto: Producto? = null
        cursor?.use {
            if (it.moveToFirst()) producto = cursorToProducto(it)
        }
        return producto
    }

    fun insertar(producto: Producto): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COL_NOMBRE_PRODUCTO, producto.nombre)
            put(DatabaseHelper.COL_FABRICANTE, producto.fabricante)
            put(DatabaseHelper.COL_VALOR, producto.valor)
        }
        return db.insert(DatabaseHelper.TABLE_PRODUCTOS, null, values)
    }

    fun actualizar(producto: Producto): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COL_NOMBRE_PRODUCTO, producto.nombre)
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

    fun eliminar(idProducto: Int): Int {
        val db = dbHelper.writableDatabase
        return db.delete(
            DatabaseHelper.TABLE_PRODUCTOS,
            "${DatabaseHelper.COL_ID_PRODUCTO} = ?",
            arrayOf(idProducto.toString())
        )
    }

    // Busca por nombre O fabricante para mayor flexibilidad
    fun buscar(query: String): List<Producto> {
        val db = dbHelper.readableDatabase
        val productos = mutableListOf<Producto>()

        val cursor = db.query(
            DatabaseHelper.TABLE_PRODUCTOS,
            null,
            "${DatabaseHelper.COL_NOMBRE_PRODUCTO} LIKE ? OR ${DatabaseHelper.COL_FABRICANTE} LIKE ?",
            arrayOf("%$query%", "%$query%"),
            null, null,
            "${DatabaseHelper.COL_NOMBRE_PRODUCTO} ASC"
        )

        cursor?.use {
            if (it.moveToFirst()) {
                do {
                    productos.add(cursorToProducto(it))
                } while (it.moveToNext())
            }
        }

        return productos
    }

    fun obtenerPorRangoValor(valorMin: Double, valorMax: Double): List<Producto> {
        val db = dbHelper.readableDatabase
        val productos = mutableListOf<Producto>()

        val cursor = db.query(
            DatabaseHelper.TABLE_PRODUCTOS,
            null,
            "${DatabaseHelper.COL_VALOR} BETWEEN ? AND ?",
            arrayOf(valorMin.toString(), valorMax.toString()),
            null, null,
            "${DatabaseHelper.COL_VALOR} ASC"
        )

        cursor?.use {
            if (it.moveToFirst()) {
                do {
                    productos.add(cursorToProducto(it))
                } while (it.moveToNext())
            }
        }

        return productos
    }

    private fun cursorToProducto(cursor: android.database.Cursor): Producto {
        return Producto(
            idProducto = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_ID_PRODUCTO)),
            nombre = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_NOMBRE_PRODUCTO)) ?: "",
            fabricante = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_FABRICANTE)) ?: "",
            valor = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COL_VALOR))
        )
    }
}
