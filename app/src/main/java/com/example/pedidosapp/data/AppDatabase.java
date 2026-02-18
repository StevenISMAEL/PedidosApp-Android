package com.example.pedidosapp.data;

import android.content.Context;
import androidx.room.Database;
import com.example.pedidosapp.data.PedidoDao;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.pedidosapp.model.Pedido;

// Definimos las entidades (tablas) y la versión de la BD
@Database(entities = {Pedido.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract PedidoDao pedidoDao();

    // Patrón Singleton para tener una única instancia de la BD en toda la app
    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "pedidos_database")
                            .fallbackToDestructiveMigration() // Si cambias la BD, borra la anterior para no crashear (útil en desarrollo)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}