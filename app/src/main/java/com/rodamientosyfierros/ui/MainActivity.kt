package com.rodamientosyfierros.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.rodamientosyfierros.R
import com.rodamientosyfierros.ui.ClientesActivity
import com.rodamientosyfierros.ui.ProductosActivity
import com.rodamientosyfierros.ui.PedidosActivity
import com.rodamientosyfierros.ui.FacturasActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupNavigation()
    }

    private fun setupNavigation() {
        val btnClientes = findViewById<Button>(R.id.btn_clientes)
        val btnProductos = findViewById<Button>(R.id.btn_productos)
        val btnPedidos = findViewById<Button>(R.id.btn_pedidos)
        val btnFacturas = findViewById<Button>(R.id.btn_facturas)

        btnClientes.setOnClickListener {
            startActivity(Intent(this, ClientesActivity::class.java))
        }

        btnProductos.setOnClickListener {
            startActivity(Intent(this, ProductosActivity::class.java))
        }

        btnPedidos.setOnClickListener {
            startActivity(Intent(this, PedidosActivity::class.java))
        }

        btnFacturas.setOnClickListener {
            startActivity(Intent(this, FacturasActivity::class.java))
        }
    }
}