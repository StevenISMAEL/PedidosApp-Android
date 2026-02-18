package com.example.pedidosapp.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pedidosapp.R;
import com.example.pedidosapp.model.Pedido;

import java.io.File;
import java.util.List;

public class PedidoAdapter extends RecyclerView.Adapter<PedidoAdapter.PedidoViewHolder> {

    private List<Pedido> listaPedidos;

    public PedidoAdapter(List<Pedido> listaPedidos) {
        this.listaPedidos = listaPedidos;
    }

    public void actualizarLista(List<Pedido> nuevaLista) {
        this.listaPedidos = nuevaLista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PedidoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pedido, parent, false);
        return new PedidoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PedidoViewHolder holder, int position) {
        Pedido pedido = listaPedidos.get(position);

        holder.tvCliente.setText(pedido.getNombreCliente());
        holder.tvFecha.setText(pedido.getFecha());
        holder.tvEstado.setText(pedido.getEstado());

        // Colores
        if ("SINCRONIZADO".equals(pedido.getEstado())) {
            holder.tvEstado.setTextColor(Color.parseColor("#388E3C"));
        } else if ("ERROR".equals(pedido.getEstado())) {
            holder.tvEstado.setTextColor(Color.RED);
        } else {
            holder.tvEstado.setTextColor(Color.parseColor("#F57C00"));
        }

        // Foto miniatura
        if (pedido.getFotoPath() != null && !pedido.getFotoPath().isEmpty()) {
            File imgFile = new File(pedido.getFotoPath());
            if (imgFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                holder.imgFoto.setImageBitmap(myBitmap);
            } else {
                holder.imgFoto.setImageResource(android.R.drawable.ic_menu_camera);
            }
        }

        // --- AQUÍ ESTÁ EL EVENTO CLICK ---
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), DetallePedidoActivity.class);
            // Pasamos todos los datos a la otra pantalla
            intent.putExtra("nombre", pedido.getNombreCliente());
            intent.putExtra("telefono", pedido.getTelefono());
            intent.putExtra("direccion", pedido.getDireccion());
            intent.putExtra("detalle", pedido.getDetallePedido());
            intent.putExtra("pago", pedido.getTipoPago());
            intent.putExtra("foto", pedido.getFotoPath());
            intent.putExtra("lat", pedido.getLatitud());
            intent.putExtra("lon", pedido.getLongitud());
            intent.putExtra("fecha", pedido.getFecha());
            intent.putExtra("estado", pedido.getEstado());
            intent.putExtra("error", pedido.getMensajeError());

            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listaPedidos.size();
    }

    public static class PedidoViewHolder extends RecyclerView.ViewHolder {
        TextView tvCliente, tvFecha, tvEstado;
        ImageView imgFoto;

        public PedidoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCliente = itemView.findViewById(R.id.tvCliente);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvEstado = itemView.findViewById(R.id.tvEstado);
            imgFoto = itemView.findViewById(R.id.imgFoto);
        }
    }
}