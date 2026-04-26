package com.rodamientosyfierros.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rodamientosyfierros.R
import com.rodamientosyfierros.models.Factura

class FacturaAdapter(
    private val facturas: MutableList<Factura>,
    private val onDelete: (Factura) -> Unit,
    private val onViewDetails: (Factura) -> Unit
) : RecyclerView.Adapter<FacturaAdapter.FacturaViewHolder>() {

    inner class FacturaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvId: TextView = itemView.findViewById(R.id.tv_factura_id)
        private val tvPedido: TextView = itemView.findViewById(R.id.tv_factura_pedido)
        private val tvFecha: TextView = itemView.findViewById(R.id.tv_factura_fecha)
        private val tvValor: TextView = itemView.findViewById(R.id.tv_factura_valor)
        private val btnDetalles: Button = itemView.findViewById(R.id.btn_detalles)
        private val btnEliminar: Button = itemView.findViewById(R.id.btn_eliminar)

        fun bind(factura: Factura) {
            tvId.text = "Factura #${factura.idFactura}"
            tvPedido.text = "Pedido #${factura.idPedido}"
            tvFecha.text = "Fecha: ${factura.fecha}"
            tvValor.text = "Total: \$${String.format("%.2f", factura.valorTotal)}"

            btnDetalles.setOnClickListener { onViewDetails(factura) }
            btnEliminar.setOnClickListener { onDelete(factura) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FacturaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_factura, parent, false)
        return FacturaViewHolder(view)
    }

    override fun onBindViewHolder(holder: FacturaViewHolder, position: Int) {
        holder.bind(facturas[position])
    }

    override fun getItemCount(): Int = facturas.size

    fun actualizarDatos(nuevosDatos: MutableList<Factura>) {
        facturas.clear()
        facturas.addAll(nuevosDatos)
        notifyDataSetChanged()
    }
}