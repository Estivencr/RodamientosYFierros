package com.rodamientosyfierros.ui

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rodamientosyfierros.R
import com.rodamientosyfierros.adapters.FacturaAdapter
import com.rodamientosyfierros.data.DatabaseHelper
import com.rodamientosyfierros.repository.ClienteRepository
import com.rodamientosyfierros.repository.FacturaRepository
import com.rodamientosyfierros.repository.PedidoRepository
import com.rodamientosyfierros.repository.ProductoRepository

class FacturasActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tvTotalIngresos: TextView
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

        btnVolver.setOnClickListener { finish() }
    }

    private fun cargarFacturas() {
        val facturas = facturaRepository.obtenerTodas().toMutableList()
        adapter.actualizarDatos(facturas)
        val total = facturaRepository.calcularIngresos()
        tvTotalIngresos.text = "\$${String.format("%.2f", total)}"
    }

    private fun mostrarDetallesFactura(factura: com.rodamientosyfierros.models.Factura) {
        val detalle = pedidoRepository.obtenerConDetalle(
            factura.idPedido, clienteRepository, productoRepository
        )

        if (detalle != null) {
            val mensaje = buildString {
                append("Factura #${factura.idFactura} — Pedido #${factura.idPedido}\n\n")
                append("Cliente: ${detalle.cliente.nombre}\n")
                append("Dirección: ${detalle.cliente.direccion}\n")
                append("Teléfono: ${detalle.cliente.telefono}\n\n")
                append("Fecha: ${factura.fecha}\n\n")
                append("PRODUCTOS:\n")
                append("━━━━━━━━━━━━━━━━━━━━━━━\n")
                detalle.productos.forEach { p ->
                    val subtotal = p.cantidad * p.valor
                    append("${p.nombre} (${p.fabricante})\n")
                    append("  ${p.cantidad} x \$${String.format("%.2f", p.valor)} = \$${String.format("%.2f", subtotal)}\n")
                }
                append("━━━━━━━━━━━━━━━━━━━━━━━\n")
                append("TOTAL: \$${String.format("%.2f", factura.valorTotal)}")
            }

            AlertDialog.Builder(this)
                .setTitle("Detalle de Factura")
                .setMessage(mensaje)
                .setPositiveButton("Cerrar", null)
                .show()
        }
    }

    private fun eliminarFactura(factura: com.rodamientosyfierros.models.Factura) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Factura")
            .setMessage("¿Eliminar la factura #${factura.idFactura}? El pedido asociado se mantendrá.")
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
