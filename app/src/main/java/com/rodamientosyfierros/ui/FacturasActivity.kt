package com.rodamientosyfierros.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rodamientosyfierros.R
import com.rodamientosyfierros.adapters.FacturaAdapter
import com.rodamientosyfierros.data.DatabaseHelper
import com.rodamientosyfierros.models.Factura
import com.rodamientosyfierros.repository.ClienteRepository
import com.rodamientosyfierros.repository.FacturaRepository
import com.rodamientosyfierros.repository.PedidoRepository
import com.rodamientosyfierros.repository.ProductoRepository
import java.text.SimpleDateFormat
import java.util.*

class FacturasActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tvTotalIngresos: TextView
    private lateinit var btnGenerarFactura: Button
    private lateinit var btnVolver: Button
    private lateinit var facturaRepository: FacturaRepository
    private lateinit var pedidoRepository: PedidoRepository
    private lateinit var clienteRepository: ClienteRepository
    private lateinit var productoRepository: ProductoRepository
    private lateinit var adapter: FacturaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_facturas)

        recyclerView = findViewById(R.id.rv_facturas)
        tvTotalIngresos = findViewById(R.id.tv_total_ingresos)
        btnGenerarFactura = findViewById(R.id.btn_generar_factura)
        btnVolver = findViewById(R.id.btn_volver)


        val dbHelper = DatabaseHelper(this)
        facturaRepository = FacturaRepository(dbHelper)
        pedidoRepository = PedidoRepository(dbHelper)
        clienteRepository = ClienteRepository(dbHelper)
        productoRepository = ProductoRepository(dbHelper)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = FacturaAdapter(
            mutableListOf(),
            onDelete = { factura -> eliminarFactura(factura) },
            onViewDetails = { factura -> mostrarDetallesFactura(factura) }
        )
        recyclerView.adapter = adapter

        cargarFacturas()

        btnGenerarFactura.setOnClickListener {
            mostrarDialogoGenerarFactura()
        }

        btnVolver.setOnClickListener {
            finish()
        }
    }

    private fun cargarFacturas() {
        val facturas = facturaRepository.obtenerTodas().toMutableList()
        adapter.actualizarDatos(facturas)
        actualizarTotalIngresos()
    }

    private fun actualizarTotalIngresos() {
        val total = facturaRepository.calcularIngresos()
        tvTotalIngresos.text = "\$${String.format("%.2f", total)}"
    }

    private fun mostrarDialogoGenerarFactura() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Generar Factura")

        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
        }

        val etIdPedido = EditText(this).apply {
            hint = "ID del Pedido"
            inputType = android.text.InputType.TYPE_NUMBER_FLAG_SIGNED
        }
        val etValor = EditText(this).apply {
            hint = "Valor Total (opcional - se calcula automáticamente)"
            inputType = android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        }

        layout.addView(etIdPedido)
        layout.addView(etValor)

        builder.setView(layout)
        builder.setPositiveButton("Generar") { _, _ ->
            if (etIdPedido.text.isNotEmpty()) {
                try {
                    val idPedido = etIdPedido.text.toString().toInt()
                    val pedido = pedidoRepository.obtenerPorId(idPedido)

                    if (pedido != null) {
                        // Verificar si ya existe factura para este pedido
                        val facturaExistente = facturaRepository.obtenerPorIdPedido(idPedido)
                        if (facturaExistente != null) {
                            Toast.makeText(this, "Este pedido ya tiene una factura", Toast.LENGTH_SHORT).show()
                            return@setPositiveButton
                        }

                        // Calcular valor total del pedido
                        val detalle = pedidoRepository.obtenerConDetalle(
                            idPedido,
                            clienteRepository,
                            productoRepository
                        )

                        val valorTotal = if (etValor.text.isNotEmpty()) {
                            etValor.text.toString().toDouble()
                        } else {
                            detalle?.valorTotal ?: 0.0
                        }

                        val fechaActual = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                        val factura = Factura(
                            idPedido = idPedido,
                            fecha = fechaActual,
                            valorTotal = valorTotal
                        )

                        val resultado = facturaRepository.insertar(factura)
                        if (resultado > 0) {
                            Toast.makeText(
                                this,
                                "Factura generada (ID: $resultado)",
                                Toast.LENGTH_SHORT
                            ).show()
                            cargarFacturas()
                        } else {
                            Toast.makeText(this, "Error al generar factura", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "Pedido no encontrado", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }

    private fun mostrarDetallesFactura(factura: Factura) {
        val detalle = pedidoRepository.obtenerConDetalle(
            factura.idPedido,
            clienteRepository,
            productoRepository
        )

        if (detalle != null) {
            val mensaje = buildString {
                append("Factura #${factura.idFactura}\n")
                append("Pedido #${factura.idPedido}\n\n")
                append("Cliente: ${detalle.cliente.nombre}\n")
                append("Dirección: ${detalle.cliente.direccion}\n")
                append("Teléfono: ${detalle.cliente.telefono}\n\n")
                append("Fecha: ${factura.fecha}\n\n")
                append("PRODUCTOS:\n")
                append("━━━━━━━━━━━━━━━━━━━━━━━\n")
                detalle.productos.forEach { producto ->
                    val subtotal = producto.cantidad * producto.valor
                    append("${producto.fabricante}\n")
                    append("  ${producto.cantidad} x \$${String.format("%.2f", producto.valor)} = \$${String.format("%.2f", subtotal)}\n")
                }
                append("━━━━━━━━━━━━━━━━━━━━━━━\n")
                append("TOTAL: \$${String.format("%.2f", factura.valorTotal)}")
            }

            AlertDialog.Builder(this)
                .setTitle("Detalles de Factura")
                .setMessage(mensaje)
                .setPositiveButton("Cerrar", null)
                .show()
        }
    }

    private fun eliminarFactura(factura: Factura) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar eliminación")
            .setMessage("¿Deseas eliminar esta factura?")
            .setPositiveButton("Eliminar") { _, _ ->
                val resultado = facturaRepository.eliminar(factura.idFactura)
                if (resultado > 0) {
                    Toast.makeText(this, "Factura eliminada", Toast.LENGTH_SHORT).show()
                    cargarFacturas()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}