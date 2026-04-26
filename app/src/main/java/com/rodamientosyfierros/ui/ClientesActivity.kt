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
import com.rodamientosyfierros.adapters.ClienteAdapter
import com.rodamientosyfierros.data.DatabaseHelper
import com.rodamientosyfierros.models.Cliente
import com.rodamientosyfierros.repository.ClienteRepository

class ClientesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnAgregarCliente: Button
    private lateinit var btnVolver: Button
    private lateinit var etBuscar: EditText
    private lateinit var repository: ClienteRepository
    private lateinit var adapter: ClienteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clientes)

        // Inicializar vistas
        recyclerView = findViewById(R.id.rv_clientes)
        btnAgregarCliente = findViewById(R.id.btn_agregar_cliente)
        btnVolver = findViewById(R.id.btn_volver)
        etBuscar = findViewById(R.id.et_buscar_cliente)

        // Inicializar repositorio
        val dbHelper = DatabaseHelper(this)
        repository = ClienteRepository(dbHelper)

        // Configurar RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ClienteAdapter(
            mutableListOf(),
            onUpdate = { cliente -> mostrarDialogoEditarCliente(cliente) },
            onDelete = { cliente -> eliminarCliente(cliente) }
        )
        recyclerView.adapter = adapter

        // Cargar clientes
        cargarClientes()

        // Configurar botón de agregar
        btnAgregarCliente.setOnClickListener {
            mostrarDialogoAgregarCliente()
        }

        // ✅ Configurar botón de regreso
        btnVolver.setOnClickListener {
            finish()
        }

        // Configurar búsqueda
        etBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    cargarClientes()
                } else {
                    buscarClientes(s.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun cargarClientes() {
        val clientes = repository.obtenerTodos().toMutableList()
        adapter.actualizarDatos(clientes)
    }

    private fun buscarClientes(query: String) {
        val clientes = repository.buscarPorNombre(query).toMutableList()
        adapter.actualizarDatos(clientes)
    }

    private fun mostrarDialogoAgregarCliente() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Agregar Cliente")

        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
        }

        val etNombre = EditText(this).apply {
            hint = "Nombre"
        }
        val etDireccion = EditText(this).apply {
            hint = "Dirección"
        }
        val etTelefono = EditText(this).apply {
            hint = "Teléfono"
        }

        layout.addView(etNombre)
        layout.addView(etDireccion)
        layout.addView(etTelefono)

        builder.setView(layout)
        builder.setPositiveButton("Guardar") { _, _ ->
            if (etNombre.text.isNotEmpty()) {
                val cliente = Cliente(
                    nombre = etNombre.text.toString(),
                    direccion = etDireccion.text.toString(),
                    telefono = etTelefono.text.toString()
                )
                val resultado = repository.insertar(cliente)
                if (resultado > 0) {
                    Toast.makeText(this, "Cliente agregado", Toast.LENGTH_SHORT).show()
                    cargarClientes()
                } else {
                    Toast.makeText(this, "Error al agregar cliente", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Por favor ingrese el nombre", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }

    private fun mostrarDialogoEditarCliente(cliente: Cliente) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Editar Cliente")

        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
        }

        val etNombre = EditText(this).apply {
            setText(cliente.nombre)
        }
        val etDireccion = EditText(this).apply {
            setText(cliente.direccion)
        }
        val etTelefono = EditText(this).apply {
            setText(cliente.telefono)
        }

        layout.addView(etNombre)
        layout.addView(etDireccion)
        layout.addView(etTelefono)

        builder.setView(layout)
        builder.setPositiveButton("Guardar") { _, _ ->
            val clienteActualizado = cliente.copy(
                nombre = etNombre.text.toString(),
                direccion = etDireccion.text.toString(),
                telefono = etTelefono.text.toString()
            )
            val resultado = repository.actualizar(clienteActualizado)
            if (resultado > 0) {
                Toast.makeText(this, "Cliente actualizado", Toast.LENGTH_SHORT).show()
                cargarClientes()
            } else {
                Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }

    private fun eliminarCliente(cliente: Cliente) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar eliminación")
            .setMessage("¿Estás seguro de que deseas eliminar a ${cliente.nombre}?")
            .setPositiveButton("Eliminar") { _, _ ->
                val resultado = repository.eliminar(cliente.idCliente)
                if (resultado > 0) {
                    Toast.makeText(this, "Cliente eliminado", Toast.LENGTH_SHORT).show()
                    cargarClientes()
                } else {
                    Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}