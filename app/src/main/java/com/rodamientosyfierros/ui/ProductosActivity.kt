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
import com.rodamientosyfierros.adapters.ProductoAdapter
import com.rodamientosyfierros.data.DatabaseHelper
import com.rodamientosyfierros.models.Producto
import com.rodamientosyfierros.repository.ProductoRepository
class ProductosActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnAgregarProducto: Button
    private lateinit var btnVolver: Button
    private lateinit var etBuscar: EditText
    private lateinit var repository: ProductoRepository
    private lateinit var adapter: ProductoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_productos)

        recyclerView = findViewById(R.id.rv_productos)
        btnAgregarProducto = findViewById(R.id.btn_agregar_producto)
        etBuscar = findViewById(R.id.et_buscar_producto)
        btnVolver = findViewById(R.id.btn_volver)

        val dbHelper = DatabaseHelper(this)
        repository = ProductoRepository(dbHelper)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ProductoAdapter(
            mutableListOf(),
            onUpdate = { producto -> mostrarDialogoEditarProducto(producto) },
            onDelete = { producto -> eliminarProducto(producto) }
        )
        recyclerView.adapter = adapter

        cargarProductos()

        btnAgregarProducto.setOnClickListener {
            mostrarDialogoAgregarProducto()
        }

        btnVolver.setOnClickListener {
            finish()
        }

        etBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    cargarProductos()
                } else {
                    buscarProductos(s.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun cargarProductos() {
        val productos = repository.obtenerTodos().toMutableList()
        adapter.actualizarDatos(productos)
    }

    private fun buscarProductos(query: String) {
        val productos = repository.buscarPorFabricante(query).toMutableList()
        adapter.actualizarDatos(productos)
    }



    private fun mostrarDialogoAgregarProducto() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Agregar Producto")

        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
        }

        val etFabricante = EditText(this).apply {
            hint = "Fabricante"
        }
        val etValor = EditText(this).apply {
            hint = "Valor"
            inputType = android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        }

        layout.addView(etFabricante)
        layout.addView(etValor)

        builder.setView(layout)
        builder.setPositiveButton("Guardar") { _, _ ->
            if (etFabricante.text.isNotEmpty() && etValor.text.isNotEmpty()) {
                try {
                    val producto = Producto(
                        fabricante = etFabricante.text.toString(),
                        valor = etValor.text.toString().toDouble()
                    )
                    val resultado = repository.insertar(producto)
                    if (resultado > 0) {
                        Toast.makeText(this, "Producto agregado", Toast.LENGTH_SHORT).show()
                        cargarProductos()
                    } else {
                        Toast.makeText(this, "Error al agregar", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }

    private fun mostrarDialogoEditarProducto(producto: Producto) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Editar Producto")

        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
        }

        val etFabricante = EditText(this).apply {
            setText(producto.fabricante)
        }
        val etValor = EditText(this).apply {
            setText(producto.valor.toString())
            inputType = android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        }

        layout.addView(etFabricante)
        layout.addView(etValor)

        builder.setView(layout)
        builder.setPositiveButton("Guardar") { _, _ ->
            try {
                val productoActualizado = producto.copy(
                    fabricante = etFabricante.text.toString(),
                    valor = etValor.text.toString().toDouble()
                )
                val resultado = repository.actualizar(productoActualizado)
                if (resultado > 0) {
                    Toast.makeText(this, "Producto actualizado", Toast.LENGTH_SHORT).show()
                    cargarProductos()
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }

    private fun eliminarProducto(producto: Producto) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar eliminación")
            .setMessage("¿Deseas eliminar este producto?")
            .setPositiveButton("Eliminar") { _, _ ->
                val resultado = repository.eliminar(producto.idProducto)
                if (resultado > 0) {
                    Toast.makeText(this, "Producto eliminado", Toast.LENGTH_SHORT).show()
                    cargarProductos()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}