package com.example.pedidosapp.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

@Entity(tableName = "pedidos")
public class Pedido {

    @PrimaryKey(autoGenerate = true)
    private int id;

    // --- NUEVO CAMPO OBLIGATORIO ---
    @ColumnInfo(name = "usuario_id")
    private String usuarioId; // Aquí guardaremos el email del usuario logueado

    @ColumnInfo(name = "nombre_cliente")
    private String nombreCliente;

    @ColumnInfo(name = "telefono")
    private String telefono;

    @ColumnInfo(name = "direccion")
    private String direccion;

    @ColumnInfo(name = "detalle_pedido")
    private String detallePedido;

    @ColumnInfo(name = "tipo_pago")
    private String tipoPago;

    @ColumnInfo(name = "foto_path")
    private String fotoPath;

    private double latitud;
    private double longitud;

    private String fecha;
    private String estado;

    @ColumnInfo(name = "mensaje_error")
    private String mensajeError;

    public Pedido() {}

    // Constructor actualizado: Ahora pide usuarioId al principio
    public Pedido(String usuarioId, String nombreCliente, String telefono, String direccion, String detallePedido, String tipoPago, String fotoPath, double latitud, double longitud, String fecha) {
        this.usuarioId = usuarioId;
        this.nombreCliente = nombreCliente;
        this.telefono = telefono;
        this.direccion = direccion;
        this.detallePedido = detallePedido;
        this.tipoPago = tipoPago;
        this.fotoPath = fotoPath;
        this.latitud = latitud;
        this.longitud = longitud;
        this.fecha = fecha;
        this.estado = "PENDIENTE";
        this.mensajeError = "";
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getDetallePedido() { return detallePedido; }
    public void setDetallePedido(String detallePedido) { this.detallePedido = detallePedido; }
    public String getTipoPago() { return tipoPago; }
    public void setTipoPago(String tipoPago) { this.tipoPago = tipoPago; }
    public String getFotoPath() { return fotoPath; }
    public void setFotoPath(String fotoPath) { this.fotoPath = fotoPath; }
    public double getLatitud() { return latitud; }
    public void setLatitud(double latitud) { this.latitud = latitud; }
    public double getLongitud() { return longitud; }
    public void setLongitud(double longitud) { this.longitud = longitud; }
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getMensajeError() { return mensajeError; }
    public void setMensajeError(String mensajeError) { this.mensajeError = mensajeError; }
}