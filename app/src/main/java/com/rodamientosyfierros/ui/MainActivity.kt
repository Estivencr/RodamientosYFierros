package com.rodamientosyfierros.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import com.rodamientosyfierros.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupNavigation()
    }

    private fun setupNavigation() {
        findViewById<MaterialCardView>(R.id.btn_clientes).setOnClickListener {
            startActivity(Intent(this, ClientesActivity::class.java))
        }
        findViewById<MaterialCardView>(R.id.btn_productos).setOnClickListener {
            startActivity(Intent(this, ProductosActivity::class.java))
        }
        findViewById<MaterialCardView>(R.id.btn_pedidos).setOnClickListener {
            startActivity(Intent(this, PedidosActivity::class.java))
        }
        findViewById<MaterialCardView>(R.id.btn_facturas).setOnClickListener {
            startActivity(Intent(this, FacturasActivity::class.java))
        }
    }
}
