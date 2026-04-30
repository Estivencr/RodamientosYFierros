package com.rodamientosyfierros.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rodamientosyfierros.R
import com.rodamientosyfierros.models.Pedido

class PedidoAdapter(
    private val pedidos: MutableList<Pedido>,
    private val onUpdate: (Pedido) -> Unit,
    private val onDelete: (Pedido) -> Unit,
    private val onViewDetails: (Pedido) -> Unit,
    private val onFacturar: (Pedido) -> Unit
) : RecyclerView.Adapter<PedidoAdapter.PedidoViewHolder>() {

    inner class PedidoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvId: TextView = itemView.findViewById(R.id.tv_pedido_id)
        private val tvCliente: TextView = itemView.findViewById(R.id.tv_pedido_cliente)
        private val tvFecha: TextView = itemView.findViewById(R.id.tv_pedido_fecha)
        private val btnDetalles: Button = itemView.findViewById(R.id.btn_detalles)
        private val btnEditar: Button = itemView.findViewById(R.id.btn_editar)
        private val btnEliminar: Button = itemView.findViewById(R.id.btn_eliminar)
        private val btnFacturar: Button = itemView.findViewById(R.id.btn_facturar)

        fun bind(pedido: Pedido) {
            tvId.text = "Pedido #${pedido.idPedido}"
            tvCliente.text = "Cliente ID: ${pedido.idCliente}"
            tvFecha.text = "Fecha: ${pedido.fecha}"

            btnDetalles.setOnClickListener { onViewDetails(pedido) }
            btnEditar.setOnClickListener { onUpdate(pedido) }
            btnEliminar.setOnClickListener { onDelete(pedido) }
            btnFacturar.setOnClickListener { onFacturar(pedido) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pedido, parent, false)
        return PedidoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PedidoViewHolder, position: Int) {
        holder.bind(pedidos[position])
    }

    override fun getItemCount(): Int = pedidos.size

    fun actualizarDatos(nuevosDatos: MutableList<Pedido>) {
        pedidos.clear()
        pedidos.addAll(nuevosDatos)
        notifyDataSetChanged()
    }
}