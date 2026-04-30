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
        val productos = repository.buscar(query).toMutableList()
        adapter.actualizarDatos(productos)
    }



    private fun mostrarDialogoAgregarProducto() {
        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(48, 16, 48, 16)
        }

        val etNombre = EditText(this).apply { hint = "Nombre del producto (ej: Rodamiento 6205)" }
        val etFabricante = EditText(this).apply { hint = "Fabricante (ej: SKF)" }
        val etValor = EditText(this).apply {
            hint = "Precio"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        }

        layout.addView(etNombre)
        layout.addView(etFabricante)
        layout.addView(etValor)

        AlertDialog.Builder(this)
            .setTitle("Agregar Producto")
            .setView(layout)
            .setPositiveButton("Guardar") { _, _ ->
                val nombre = etNombre.text.toString().trim()
                val fabricante = etFabricante.text.toString().trim()
                val valorStr = etValor.text.toString().trim()

                if (nombre.isEmpty() || valorStr.isEmpty()) {
                    Toast.makeText(this, "Nombre y precio son obligatorios", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                try {
                    val resultado = repository.insertar(
                        Producto(nombre = nombre, fabricante = fabricante, valor = valorStr.toDouble())
                    )
                    if (resultado > 0) {
                        Toast.makeText(this, "Producto agregado", Toast.LENGTH_SHORT).show()
                        cargarProductos()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarDialogoEditarProducto(producto: Producto) {
        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(48, 16, 48, 16)
        }

        val etNombre = EditText(this).apply { setText(producto.nombre) }
        val etFabricante = EditText(this).apply { setText(producto.fabricante) }
        val etValor = EditText(this).apply {
            setText(producto.valor.toString())
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        }

        layout.addView(etNombre)
        layout.addView(etFabricante)
        layout.addView(etValor)

        AlertDialog.Builder(this)
            .setTitle("Editar Producto")
            .setView(layout)
            .setPositiveButton("Guardar") { _, _ ->
                val nombre = etNombre.text.toString().trim()
                val valorStr = etValor.text.toString().trim()

                if (nombre.isEmpty() || valorStr.isEmpty()) {
                    Toast.makeText(this, "Nombre y precio son obligatorios", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                try {
                    val resultado = repository.actualizar(
                        producto.copy(
                            nombre = nombre,
                            fabricante = etFabricante.text.toString().trim(),
                            valor = valorStr.toDouble()
                        )
                    )
                    if (resultado > 0) {
                        Toast.makeText(this, "Producto actualizado", Toast.LENGTH_SHORT).show()
                        cargarProductos()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
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