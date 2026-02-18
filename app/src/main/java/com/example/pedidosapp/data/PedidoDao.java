package com.example.pedidosapp.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.pedidosapp.model.Pedido;

import java.util.List;

@Dao
public interface PedidoDao {
    @Insert
    long insert(Pedido pedido);

    @Update
    void update(Pedido pedido);

    // FILTRO: Solo pedidos donde usuario_id coincida con el que le pasamos
    @Query("SELECT * FROM pedidos WHERE usuario_id = :userId ORDER BY id DESC")
    List<Pedido> getAllPedidos(String userId);

    // FILTRO: Solo sincronizar lo de ESTE usuario
    @Query("SELECT * FROM pedidos WHERE usuario_id = :userId AND (estado = 'PENDIENTE' OR estado = 'ERROR')")
    List<Pedido> getPedidosPorSincronizar(String userId);
}