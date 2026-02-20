package com.example.pedidosapp.data;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.core.content.FileProvider;
import com.example.pedidosapp.model.Pedido;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class ExportUtils {

    // Método para convertir lista de pedidos a CSV y compartir
    public static void exportarPedidosACV(Context context, List<Pedido> pedidos) {
        StringBuilder csvData = new StringBuilder();
        // Cabeceras
        csvData.append("ID,Cliente,Telefono,Direccion,Pago,Fecha,Estado,Latitud,Longitud\n");

        for (Pedido p : pedidos) {
            csvData.append(p.getId()).append(",")
                    .append(p.getNombreCliente()).append(",")
                    .append(p.getTelefono()).append(",")
                    .append(p.getDireccion().replace(",", " ")).append(",") // Evitar romper CSV
                    .append(p.getTipoPago()).append(",")
                    .append(p.getFecha()).append(",")
                    .append(p.getEstado()).append(",")
                    .append(p.getLatitud()).append(",")
                    .append(p.getLongitud()).append("\n");
        }

        try {
            // Guardar en caché temporal de la app
            File file = new File(context.getCacheDir(), "pedidos_logieat.csv");
            FileOutputStream out = new FileOutputStream(file);
            out.write(csvData.toString().getBytes());
            out.close();

            // Compartir archivo
            Uri contentUri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/csv");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Reporte de Pedidos LogiEat");
            intent.putExtra(Intent.EXTRA_STREAM, contentUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(Intent.createChooser(intent, "Exportar pedidos vía..."));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}