package com.rodamientosyfierros.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rodamientosyfierros.R
import com.rodamientosyfierros.adapters.PedidoAdapter
import com.rodamientosyfierros.data.DatabaseHelper
import com.rodamientosyfierros.models.Factura
import com.rodamientosyfierros.models.Pedido
import com.rodamientosyfierros.repository.ClienteRepository
import com.rodamientosyfierros.repository.FacturaRepository
import com.rodamientosyfierros.repository.PedidoRepository
import com.rodamientosyfierros.repository.ProductoRepository
import java.text.SimpleDateFormat
import java.util.*

class PedidosActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnAgregarPedido: Button
    private lateinit var etBuscar: EditText
    private lateinit var btnVolver: Button
    private lateinit var pedidoRepository: PedidoRepository
    private lateinit var clienteRepository: ClienteRepository
    private lateinit var productoRepository: ProductoRepository
    private lateinit var facturaRepository: FacturaRepository
    private lateinit var adapter: PedidoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pedidos)

        recyclerView = findViewById(R.id.rv_pedidos)
        btnAgregarPedido = findViewById(R.id.btn_agregar_pedido)
        etBuscar = findViewById(R.id.et_buscar_pedido)
        btnVolver = findViewById(R.id.btn_volver)

        val dbHelper = DatabaseHelper(this)
        pedidoRepository = PedidoRepository(dbHelper)
        clienteRepository = ClienteRepository(dbHelper)
        productoRepository = ProductoRepository(dbHelper)
        facturaRepository = FacturaRepository(dbHelper)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = PedidoAdapter(
            mutableListOf(),
            onUpdate = { pedido -> mostrarDialogoEditarPedido(pedido) },
            onDelete = { pedido -> eliminarPedido(pedido) },
            onViewDetails = { pedido -> mostrarDetallesPedido(pedido) },
            onFacturar = { pedido -> facturarPedido(pedido) }
        )
        recyclerView.adapter = adapter

        cargarPedidos()

        btnAgregarPedido.setOnClickListener { mostrarDialogoAgregarPedido() }
        btnVolver.setOnClickListener { finish() }

        etBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun cargarPedidos() {
        val pedidos = pedidoRepository.obtenerTodos().toMutableList()
        adapter.actualizarDatos(pedidos)
    }

    // PASO 1: seleccionar cliente y descripción
    private fun mostrarDialogoAgregarPedido() {
        val clientes = clienteRepository.obtenerTodos()
        if (clientes.isEmpty()) {
            Toast.makeText(this, "Primero registra un cliente", Toast.LENGTH_SHORT).show()
            return
        }

        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(48, 16, 48, 16)
        }

        val tvLabel = TextView(this).apply { text = "Cliente:" }
        val spinnerCliente = Spinner(this)
        val nombresClientes = clientes.map { it.nombre }.toTypedArray()
        spinnerCliente.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, nombresClientes)

        val etDescripcion = EditText(this).apply { hint = "Descripción (opcional)" }

        layout.addView(tvLabel)
        layout.addView(spinnerCliente)
        layout.addView(etDescripcion)

        AlertDialog.Builder(this)
            .setTitle("Nuevo Pedido (1/2) — Cliente")
            .setView(layout)
            .setPositiveButton("Agregar Productos →") { _, _ ->
                val clienteSeleccionado = clientes[spinnerCliente.selectedItemPosition]
                val descripcion = etDescripcion.text.toString().trim().ifEmpty { "Sin descripción" }
                val fecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

                val idPedido = pedidoRepository.insertar(
                    Pedido(idCliente = clienteSeleccionado.idCliente, descripcion = descripcion, fecha = fecha)
                )
                if (idPedido > 0) {
                    mostrarDialogoAgregarProductoAlPedido(idPedido.toInt())
                } else {
                    Toast.makeText(this, "Error al crear el pedido", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // PASO 2: seleccionar productos con cantidad (se puede repetir hasta Finalizar)
    private fun mostrarDialogoAgregarProductoAlPedido(idPedido: Int) {
        val productos = productoRepository.obtenerTodos()
        if (productos.isEmpty()) {
            Toast.makeText(this, "Pedido #$idPedido creado. No hay productos para agregar.", Toast.LENGTH_LONG).show()
            cargarPedidos()
            return
        }

        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(48, 16, 48, 16)
        }

        val tvLabel = TextView(this).apply { text = "Producto:" }
        val spinnerProducto = Spinner(this)
        val nombresProductos = productos.map { "${it.nombre} — \$${String.format("%.2f", it.valor)}" }.toTypedArray()
        spinnerProducto.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, nombresProductos)

        val etCantidad = EditText(this).apply {
            hint = "Cantidad"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
        }

        layout.addView(tvLabel)
        layout.addView(spinnerProducto)
        layout.addView(etCantidad)

        AlertDialog.Builder(this)
            .setTitle("Pedido #$idPedido (2/2) — Agregar Producto")
            .setView(layout)
            .setPositiveButton("+ Agregar otro") { _, _ ->
                guardarProductoEnPedido(idPedido, productos[spinnerProducto.selectedItemPosition].idProducto,
                    etCantidad.text.toString().toIntOrNull() ?: 0)
                mostrarDialogoAgregarProductoAlPedido(idPedido)
            }
            .setNegativeButton("Finalizar") { _, _ ->
                guardarProductoEnPedido(idPedido, productos[spinnerProducto.selectedItemPosition].idProducto,
                    etCantidad.text.toString().toIntOrNull() ?: 0)
                Toast.makeText(this, "Pedido #$idPedido guardado", Toast.LENGTH_SHORT).show()
                cargarPedidos()
            }
            .show()
    }

    private fun guardarProductoEnPedido(idPedido: Int, idProducto: Int, cantidad: Int) {
        if (cantidad > 0) {
            pedidoRepository.agregarProducto(idPedido, idProducto, cantidad)
        }
    }

    private fun mostrarDialogoEditarPedido(pedido: Pedido) {
        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(48, 16, 48, 16)
        }

        val etDescripcion = EditText(this).apply { setText(pedido.descripcion) }
        layout.addView(etDescripcion)

        AlertDialog.Builder(this)
            .setTitle("Editar Pedido #${pedido.idPedido}")
            .setView(layout)
            .setPositiveButton("Guardar") { _, _ ->
                val resultado = pedidoRepository.actualizar(
                    pedido.copy(descripcion = etDescripcion.text.toString())
                )
                if (resultado > 0) {
                    Toast.makeText(this, "Pedido actualizado", Toast.LENGTH_SHORT).show()
                    cargarPedidos()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarDetallesPedido(pedido: Pedido) {
        val detalle = pedidoRepository.obtenerConDetalle(pedido.idPedido, clienteRepository, productoRepository)

        if (detalle != null) {
            val mensaje = buildString {
                append("Cliente: ${detalle.cliente.nombre}\n")
                append("Descripción: ${detalle.pedido.descripcion}\n")
                append("Fecha: ${detalle.pedido.fecha}\n\n")
                if (detalle.productos.isEmpty()) {
                    append("Sin productos agregados.\n")
                } else {
                    append("PRODUCTOS:\n")
                    detalle.productos.forEach { p ->
                        val subtotal = p.cantidad * p.valor
                        append("  ${p.nombre} (${p.fabricante})\n")
                        append("  ${p.cantidad} x \$${String.format("%.2f", p.valor)} = \$${String.format("%.2f", subtotal)}\n")
                    }
                    append("\nTOTAL: \$${String.format("%.2f", detalle.valorTotal)}")
                }
            }

            AlertDialog.Builder(this)
                .setTitle("Pedido #${pedido.idPedido}")
                .setMessage(mensaje)
                .setPositiveButton("Cerrar", null)
                .show()
        }
    }

    private fun facturarPedido(pedido: Pedido) {
        val facturaExistente = facturaRepository.obtenerPorIdPedido(pedido.idPedido)
        if (facturaExistente != null) {
            Toast.makeText(this, "Este pedido ya tiene la factura #${facturaExistente.idFactura}", Toast.LENGTH_LONG).show()
            return
        }

        val detalle = pedidoRepository.obtenerConDetalle(pedido.idPedido, clienteRepository, productoRepository)
        if (detalle == null || detalle.productos.isEmpty()) {
            Toast.makeText(this, "El pedido no tiene productos. Agrega productos antes de facturar.", Toast.LENGTH_LONG).show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Generar Factura")
            .setMessage(
                "Cliente: ${detalle.cliente.nombre}\n" +
                "Productos: ${detalle.productos.size}\n" +
                "Total: \$${String.format("%.2f", detalle.valorTotal)}\n\n" +
                "¿Confirmar generación de factura?"
            )
            .setPositiveButton("Generar") { _, _ ->
                val fecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                val resultado = facturaRepository.insertar(
                    Factura(idPedido = pedido.idPedido, fecha = fecha, valorTotal = detalle.valorTotal)
                )
                if (resultado > 0) {
                    Toast.makeText(this, "Factura #$resultado generada", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error al generar factura", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun eliminarPedido(pedido: Pedido) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar eliminación")
            .setMessage("¿Eliminar pedido #${pedido.idPedido}? Se eliminarán todos sus datos y la factura asociada si existe.")
            .setPositiveButton("Eliminar") { _, _ ->
                val resultado = pedidoRepository.eliminar(pedido.idPedido)
                if (resultado > 0) {
                    Toast.makeText(this, "Pedido eliminado", Toast.LENGTH_SHORT).show()
                    cargarPedidos()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
