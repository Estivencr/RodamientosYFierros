package com.rodamientosyfierros.repository

import android.content.ContentValues
import com.rodamientosyfierros.data.DatabaseHelper
import com.rodamientosyfierros.models.Cliente

class ClienteRepository(private val dbHelper: DatabaseHelper) {

    /**
     * Obtener todos los clientes
     */
    fun obtenerTodos(): List<Cliente> {
        val db = dbHelper.readableDatabase
        val clientes = mutableListOf<Cliente>()

        val cursor = db.query(
            DatabaseHelper.Companion.TABLE_CLIENTES,
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
                    val idIdx = it.getColumnIndex(DatabaseHelper.Companion.COL_ID_CLIENTE)
                    val nombreIdx = it.getColumnIndex(DatabaseHelper.Companion.COL_NOMBRE)
                    val direccionIdx = it.getColumnIndex(DatabaseHelper.Companion.COL_DIRECCION)
                    val telefonoIdx = it.getColumnIndex(DatabaseHelper.Companion.COL_TELEFONO)

                    val cliente = Cliente(
                        idCliente = it.getInt(idIdx),
                        nombre = it.getString(nombreIdx),
                        direccion = it.getString(direccionIdx) ?: "",
                        telefono = it.getString(telefonoIdx) ?: ""
                    )
                    clientes.add(cliente)
                } while (it.moveToNext())
            }
        }

        return clientes
    }

    /**
     * Obtener un cliente por ID
     */
    fun obtenerPorId(idCliente: Int): Cliente? {
        val db = dbHelper.readableDatabase

        val cursor = db.query(
            DatabaseHelper.Companion.TABLE_CLIENTES,
            null,
            "${DatabaseHelper.Companion.COL_ID_CLIENTE} = ?",
            arrayOf(idCliente.toString()),
            null,
            null,
            null
        )

        var cliente: Cliente? = null
        cursor?.use {
            if (it.moveToFirst()) {
                val nombreIdx = it.getColumnIndex(DatabaseHelper.Companion.COL_NOMBRE)
                val direccionIdx = it.getColumnIndex(DatabaseHelper.Companion.COL_DIRECCION)
                val telefonoIdx = it.getColumnIndex(DatabaseHelper.Companion.COL_TELEFONO)

                cliente = Cliente(
                    idCliente = idCliente,
                    nombre = it.getString(nombreIdx),
                    direccion = it.getString(direccionIdx) ?: "",
                    telefono = it.getString(telefonoIdx) ?: ""
                )
            }
        }

        return cliente
    }

    /**
     * Insertar un nuevo cliente
     * @return ID del cliente creado, o -1 si falla
     */
    fun insertar(cliente: Cliente): Long {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(DatabaseHelper.Companion.COL_NOMBRE, cliente.nombre)
            put(DatabaseHelper.Companion.COL_DIRECCION, cliente.direccion)
            put(DatabaseHelper.Companion.COL_TELEFONO, cliente.telefono)
        }

        return db.insert(DatabaseHelper.Companion.TABLE_CLIENTES, null, values)
    }

    /**
     * Actualizar un cliente existente
     * @return Número de filas actualizadas
     */
    fun actualizar(cliente: Cliente): Int {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(DatabaseHelper.Companion.COL_NOMBRE, cliente.nombre)
            put(DatabaseHelper.Companion.COL_DIRECCION, cliente.direccion)
            put(DatabaseHelper.Companion.COL_TELEFONO, cliente.telefono)
        }

        return db.update(
            DatabaseHelper.Companion.TABLE_CLIENTES,
            values,
            "${DatabaseHelper.Companion.COL_ID_CLIENTE} = ?",
            arrayOf(cliente.idCliente.toString())
        )
    }

    /**
     * Eliminar un cliente por ID
     * @return Número de filas eliminadas
     */
    fun eliminar(idCliente: Int): Int {
        val db = dbHelper.writableDatabase

        return db.delete(
            DatabaseHelper.Companion.TABLE_CLIENTES,
            "${DatabaseHelper.Companion.COL_ID_CLIENTE} = ?",
            arrayOf(idCliente.toString())
        )
    }

    /**
     * Buscar clientes por nombre
     */
    fun buscarPorNombre(nombre: String): List<Cliente> {
        val db = dbHelper.readableDatabase
        val clientes = mutableListOf<Cliente>()

        val cursor = db.query(
            DatabaseHelper.Companion.TABLE_CLIENTES,
            null,
            "${DatabaseHelper.Companion.COL_NOMBRE} LIKE ?",
            arrayOf("%$nombre%"),
            null,
            null,
            null
        )

        cursor?.use {
            if (it.moveToFirst()) {
                do {
                    val idIdx = it.getColumnIndex(DatabaseHelper.Companion.COL_ID_CLIENTE)
                    val nombreIdx = it.getColumnIndex(DatabaseHelper.Companion.COL_NOMBRE)
                    val direccionIdx = it.getColumnIndex(DatabaseHelper.Companion.COL_DIRECCION)
                    val telefonoIdx = it.getColumnIndex(DatabaseHelper.Companion.COL_TELEFONO)

                    val cliente = Cliente(
                        idCliente = it.getInt(idIdx),
                        nombre = it.getString(nombreIdx),
                        direccion = it.getString(direccionIdx) ?: "",
                        telefono = it.getString(telefonoIdx) ?: ""
                    )
                    clientes.add(cliente)
                } while (it.moveToNext())
            }
        }

        return clientes
    }
}