package com.rodamientosyfierros.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rodamientosyfierros.R
import com.rodamientosyfierros.adapters.PedidoAdapter
import com.rodamientosyfierros.data.DatabaseHelper
import com.rodamientosyfierros.models.Pedido
import com.rodamientosyfierros.repository.ClienteRepository
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

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = PedidoAdapter(
            mutableListOf(),
            onUpdate = { pedido -> mostrarDialogoEditarPedido(pedido) },
            onDelete = { pedido -> eliminarPedido(pedido) },
            onViewDetails = { pedido -> mostrarDetallesPedido(pedido) }
        )
        recyclerView.adapter = adapter

        cargarPedidos()

        btnAgregarPedido.setOnClickListener {
            mostrarDialogoAgregarPedido()
        }

        btnVolver.setOnClickListener {
            finish()
        }

        etBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Aquí puedes agregar lógica de búsqueda si lo deseas
                // Por ahora, simplemente mostramos todos los pedidos
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun cargarPedidos() {
        val pedidos = pedidoRepository.obtenerTodos().toMutableList()
        adapter.actualizarDatos(pedidos)
    }

    private fun mostrarDialogoAgregarPedido() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Agregar Pedido")

        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
        }

        val etIdCliente = EditText(this).apply {
            hint = "ID del Cliente"
            inputType = android.text.InputType.TYPE_NUMBER_FLAG_SIGNED
        }
        val etDescripcion = EditText(this).apply {
            hint = "Descripción del Pedido"
        }

        layout.addView(etIdCliente)
        layout.addView(etDescripcion)

        builder.setView(layout)
        builder.setPositiveButton("Guardar") { _, _ ->
            if (etIdCliente.text.isNotEmpty() && etDescripcion.text.isNotEmpty()) {
                try {
                    val idCliente = etIdCliente.text.toString().toInt()
                    val cliente = clienteRepository.obtenerPorId(idCliente)

                    if (cliente != null) {
                        val fechaActual = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                        val pedido = Pedido(
                            idCliente = idCliente,
                            descripcion = etDescripcion.text.toString(),
                            fecha = fechaActual
                        )
                        val resultado = pedidoRepository.insertar(pedido)
                        if (resultado > 0) {
                            Toast.makeText(this, "Pedido agregado (ID: $resultado)", Toast.LENGTH_SHORT).show()
                            cargarPedidos()
                        }
                    } else {
                        Toast.makeText(this, "Cliente no encontrado", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }

    private fun mostrarDialogoEditarPedido(pedido: Pedido) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Editar Pedido")

        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
        }

        val etDescripcion = EditText(this).apply {
            setText(pedido.descripcion)
        }

        layout.addView(etDescripcion)

        builder.setView(layout)
        builder.setPositiveButton("Guardar") { _, _ ->
            val pedidoActualizado = pedido.copy(
                descripcion = etDescripcion.text.toString()
            )
            val resultado = pedidoRepository.actualizar(pedidoActualizado)
            if (resultado > 0) {
                Toast.makeText(this, "Pedido actualizado", Toast.LENGTH_SHORT).show()
                cargarPedidos()
            }
        }
        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }

    private fun mostrarDetallesPedido(pedido: Pedido) {
        val detalle = pedidoRepository.obtenerConDetalle(
            pedido.idPedido,
            clienteRepository,
            productoRepository
        )

        if (detalle != null) {
            val mensaje = buildString {
                append("Cliente: ${detalle.cliente.nombre}\n")
                append("Descripción: ${detalle.pedido.descripcion}\n")
                append("Fecha: ${detalle.pedido.fecha}\n")
                append("Productos:\n")
                detalle.productos.forEach { producto ->
                    append("  - ${producto.fabricante}: ${producto.cantidad} x \$${producto.valor}\n")
                }
                append("Total: \$${String.format("%.2f", detalle.valorTotal)}")
            }

            AlertDialog.Builder(this)
                .setTitle("Detalles del Pedido #${pedido.idPedido}")
                .setMessage(mensaje)
                .setPositiveButton("Cerrar", null)
                .show()
        }
    }

    private fun eliminarPedido(pedido: Pedido) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar eliminación")
            .setMessage("¿Deseas eliminar este pedido? Se eliminarán todos sus datos asociados.")
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