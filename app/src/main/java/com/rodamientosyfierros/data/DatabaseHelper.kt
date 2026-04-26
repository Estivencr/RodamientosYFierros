package com.rodamientosyfierros.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(
    context: Context,
    name: String = "ferreteria.db",
    factory: SQLiteDatabase.CursorFactory? = null,
    version: Int = 1
) : SQLiteOpenHelper(context, name, factory, version) {

    companion object {
        // Constantes de tablas y columnas
        const val DB_VERSION = 1

        // Tabla Clientes
        const val TABLE_CLIENTES = "Clientes"
        const val COL_ID_CLIENTE = "IdCliente"
        const val COL_NOMBRE = "Nombre"
        const val COL_DIRECCION = "Direccion"
        const val COL_TELEFONO = "Telefono"

        // Tabla Productos
        const val TABLE_PRODUCTOS = "Productos"
        const val COL_ID_PRODUCTO = "IdProducto"
        const val COL_FABRICANTE = "Fabricante"
        const val COL_VALOR = "Valor"

        // Tabla Pedidos
        const val TABLE_PEDIDOS = "Pedidos"
        const val COL_ID_PEDIDO = "IdPedido"
        const val COL_ID_CLIENTE_FK = "IdCliente"
        const val COL_DESCRIPCION = "Descripcion"
        const val COL_FECHA_PEDIDO = "Fecha"

        // Tabla PedidoProductos
        const val TABLE_PEDIDO_PRODUCTOS = "PedidoProductos"
        const val COL_ID_PEDIDO_PRODUCTO = "Id"
        const val COL_ID_PEDIDO_FK = "IdPedido"
        const val COL_ID_PRODUCTO_FK = "IdProducto"
        const val COL_CANTIDAD = "Cantidad"

        // Tabla Facturas
        const val TABLE_FACTURAS = "Facturas"
        const val COL_ID_FACTURA = "IdFactura"
        const val COL_ID_PEDIDO_FACTURA_FK = "IdPedido"
        const val COL_FECHA_FACTURA = "Fecha"
        const val COL_VALOR_TOTAL = "ValorTotal"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // Crear tabla Clientes
        val createClientesTable = """
            CREATE TABLE $TABLE_CLIENTES (
                $COL_ID_CLIENTE INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_NOMBRE TEXT NOT NULL,
                $COL_DIRECCION TEXT,
                $COL_TELEFONO TEXT
            )
        """.trimIndent()

        // Crear tabla Productos
        val createProductosTable = """
            CREATE TABLE $TABLE_PRODUCTOS (
                $COL_ID_PRODUCTO INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_FABRICANTE TEXT,
                $COL_VALOR REAL
            )
        """.trimIndent()

        // Crear tabla Pedidos
        val createPedidosTable = """
            CREATE TABLE $TABLE_PEDIDOS (
                $COL_ID_PEDIDO INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_ID_CLIENTE_FK INTEGER NOT NULL,
                $COL_DESCRIPCION TEXT,
                $COL_FECHA_PEDIDO TEXT,
                FOREIGN KEY ($COL_ID_CLIENTE_FK) REFERENCES $TABLE_CLIENTES($COL_ID_CLIENTE)
            )
        """.trimIndent()

        // Crear tabla PedidoProductos
        val createPedidoProductosTable = """
            CREATE TABLE $TABLE_PEDIDO_PRODUCTOS (
                $COL_ID_PEDIDO_PRODUCTO INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_ID_PEDIDO_FK INTEGER NOT NULL,
                $COL_ID_PRODUCTO_FK INTEGER NOT NULL,
                $COL_CANTIDAD INTEGER,
                FOREIGN KEY ($COL_ID_PEDIDO_FK) REFERENCES $TABLE_PEDIDOS($COL_ID_PEDIDO),
                FOREIGN KEY ($COL_ID_PRODUCTO_FK) REFERENCES $TABLE_PRODUCTOS($COL_ID_PRODUCTO),
                UNIQUE($COL_ID_PEDIDO_FK, $COL_ID_PRODUCTO_FK)
            )
        """.trimIndent()

        // Crear tabla Facturas
        val createFacturasTable = """
            CREATE TABLE $TABLE_FACTURAS (
                $COL_ID_FACTURA INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_ID_PEDIDO_FACTURA_FK INTEGER NOT NULL UNIQUE,
                $COL_FECHA_FACTURA TEXT,
                $COL_VALOR_TOTAL REAL,
                FOREIGN KEY ($COL_ID_PEDIDO_FACTURA_FK) REFERENCES $TABLE_PEDIDOS($COL_ID_PEDIDO)
            )
        """.trimIndent()

        // Ejecutar las creaciones
        db?.execSQL(createClientesTable)
        db?.execSQL(createProductosTable)
        db?.execSQL(createPedidosTable)
        db?.execSQL(createPedidoProductosTable)
        db?.execSQL(createFacturasTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Eliminar tablas antiguas
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_FACTURAS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_PEDIDO_PRODUCTOS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_PEDIDOS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_PRODUCTOS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_CLIENTES")

        // Recrear tablas
        onCreate(db)
    }
}