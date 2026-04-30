package com.rodamientosyfierros.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rodamientosyfierros.R
import com.rodamientosyfierros.models.Producto

class ProductoAdapter(
    private val productos: MutableList<Producto>,
    private val onUpdate: (Producto) -> Unit,
    private val onDelete: (Producto) -> Unit
) : RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder>() {

    inner class ProductoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNombre: TextView = itemView.findViewById(R.id.tv_producto_nombre)
        private val tvFabricante: TextView = itemView.findViewById(R.id.tv_producto_fabricante)
        private val tvValor: TextView = itemView.findViewById(R.id.tv_producto_valor)
        private val btnEditar: Button = itemView.findViewById(R.id.btn_editar)
        private val btnEliminar: Button = itemView.findViewById(R.id.btn_eliminar)

        fun bind(producto: Producto) {
            tvNombre.text = producto.nombre
            tvFabricante.text = "Fabricante: ${producto.fabricante}"
            tvValor.text = "Precio: \$${String.format("%.2f", producto.valor)}"

            btnEditar.setOnClickListener { onUpdate(producto) }
            btnEliminar.setOnClickListener { onDelete(producto) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto, parent, false)
        return ProductoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        holder.bind(productos[position])
    }

    override fun getItemCount(): Int = productos.size

    fun actualizarDatos(nuevosDatos: MutableList<Producto>) {
        productos.clear()
        productos.addAll(nuevosDatos)
        notifyDataSetChanged()
    }
}