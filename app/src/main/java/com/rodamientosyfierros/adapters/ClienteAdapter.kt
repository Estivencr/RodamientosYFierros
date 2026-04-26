package com.rodamientosyfierros.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rodamientosyfierros.R
import com.rodamientosyfierros.models.Cliente

class ClienteAdapter(
    private val clientes: MutableList<Cliente>,
    private val onUpdate: (Cliente) -> Unit,
    private val onDelete: (Cliente) -> Unit
) : RecyclerView.Adapter<ClienteAdapter.ClienteViewHolder>() {

    inner class ClienteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNombre: TextView = itemView.findViewById(R.id.tv_cliente_nombre)
        private val tvDireccion: TextView = itemView.findViewById(R.id.tv_cliente_direccion)
        private val tvTelefono: TextView = itemView.findViewById(R.id.tv_cliente_telefono)
        private val btnEditar: Button = itemView.findViewById(R.id.btn_editar)
        private val btnEliminar: Button = itemView.findViewById(R.id.btn_eliminar)

        fun bind(cliente: Cliente) {
            tvNombre.text = "Nombre: ${cliente.nombre}"
            tvDireccion.text = "Dirección: ${cliente.direccion}"
            tvTelefono.text = "Teléfono: ${cliente.telefono}"

            btnEditar.setOnClickListener {
                onUpdate(cliente)
            }

            btnEliminar.setOnClickListener {
                onDelete(cliente)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClienteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cliente, parent, false)
        return ClienteViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClienteViewHolder, position: Int) {
        holder.bind(clientes[position])
    }

    override fun getItemCount(): Int = clientes.size

    fun actualizarDatos(nuevosDatos: MutableList<Cliente>) {
        clientes.clear()
        clientes.addAll(nuevosDatos)
        notifyDataSetChanged()
    }
}