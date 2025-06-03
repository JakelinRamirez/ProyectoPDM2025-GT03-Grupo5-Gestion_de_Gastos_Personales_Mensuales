package com.example.proyectopdm2025_gt03_grupo5_gestion_de_gastos_personales_mensuales;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME    = "bd_gestor_gastos.db";
    private static final int    DB_VERSION = 2;

    public DBHelper(Context ctx) {
        super(ctx, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 1. Usuarios
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS Usuario (" +
                        " id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        " nombre TEXT NOT NULL," +
                        " email TEXT NOT NULL UNIQUE," +
                        " contrasena TEXT NOT NULL," +
                        " nombreRol TEXT" +
                        ");"
        );

        // 2. Categorías de gasto
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS Categoria (" +
                        " id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        " nombre TEXT NOT NULL" +
                        ");"
        );


        // 3. Métodos de pago
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS MetodoPago (" +
                        " id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        " nombre TEXT NOT NULL" +
                        ");"
        );

// Insertar datos predeterminados
        db.execSQL("INSERT INTO MetodoPago (nombre) VALUES ('Efectivo');");
        db.execSQL("INSERT INTO MetodoPago (nombre) VALUES ('Tarjeta de crédito');");
        db.execSQL("INSERT INTO MetodoPago (nombre) VALUES ('Tarjeta de débito');");
        db.execSQL("INSERT INTO MetodoPago (nombre) VALUES ('Transferencia bancaria');");



        // 4. Gastos
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS Gasto (" +
                        " id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        " monto REAL NOT NULL," +
                        " fecha TEXT NOT NULL," +
                        " descripcion TEXT," +
                        " usuario_id INTEGER NOT NULL," +
                        " categoria_id INTEGER," +
                        " metodo_pago_id INTEGER," +
                        " FOREIGN KEY(usuario_id) REFERENCES Usuario(id) " +
                        "   ON DELETE CASCADE ON UPDATE CASCADE," +
                        " FOREIGN KEY(categoria_id) REFERENCES Categoria(id) " +
                        "   ON DELETE SET NULL ON UPDATE CASCADE," +
                        " FOREIGN KEY(metodo_pago_id) REFERENCES MetodoPago(id) " +
                        "   ON DELETE SET NULL ON UPDATE CASCADE" +
                        ");"
        );

        // 5. Exportaciones
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS Exportacion (" +
                        " id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        " usuario_id INTEGER NOT NULL," +
                        " fecha_exportacion TEXT NOT NULL," +
                        " servicio_destino TEXT," +
                        " FOREIGN KEY(usuario_id) REFERENCES Usuario(id) " +
                        "   ON DELETE CASCADE ON UPDATE CASCADE" +
                        ");"
        );

        // 6. Recordatorios
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS Recordatorio (" +
                        " id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        " usuario_id INTEGER NOT NULL," +
                        " gasto_id INTEGER NOT NULL," +
                        " fecha_vencimiento TEXT NOT NULL," +
                        " estado TEXT NOT NULL," +
                        " FOREIGN KEY(usuario_id) REFERENCES Usuario(id) " +
                        "   ON DELETE CASCADE ON UPDATE CASCADE," +
                        " FOREIGN KEY(gasto_id) REFERENCES Gasto(id) " +
                        "   ON DELETE CASCADE ON UPDATE CASCADE" +
                        ");"
        );

        // 7. Objetivos
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS Objetivo (" +
                        " id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        " usuario_id INTEGER NOT NULL," +
                        " monto_limite REAL NOT NULL," +
                        " tipo TEXT NOT NULL," +
                        " fecha_inicio TEXT NOT NULL," +
                        " fecha_fin TEXT," +
                        " FOREIGN KEY(usuario_id) REFERENCES Usuario(id) " +
                        "   ON DELETE CASCADE ON UPDATE CASCADE" +
                        ");"
        );

        // 8. Roles
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS Roles (" +
                        " nombreRol TEXT PRIMARY KEY," +
                        " descripcionRol TEXT" +
                        ");"
        );

        // 9. Opciones de menú
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS OpcionesMenu (" +
                        " id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        " descripcionOpcion TEXT NOT NULL" +
                        ");"
        );

        // 10. RolesOpcionMenu
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS RolesOpcionMenu (" +
                        " id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        " nombreRol TEXT NOT NULL," +
                        " idPermisoOpcion INTEGER NOT NULL," +
                        " FOREIGN KEY(nombreRol) REFERENCES Roles(nombreRol) " +
                        "   ON DELETE CASCADE ON UPDATE CASCADE," +
                        " FOREIGN KEY(idPermisoOpcion) REFERENCES OpcionesMenu(id) " +
                        "   ON DELETE CASCADE ON UPDATE CASCADE" +
                        ");"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        // si cambias versión, borras y recreas
        db.execSQL("DROP TABLE IF EXISTS RolesOpcionMenu");
        db.execSQL("DROP TABLE IF EXISTS OpcionesMenu");
        db.execSQL("DROP TABLE IF EXISTS Roles");
        db.execSQL("DROP TABLE IF EXISTS Objetivo");
        db.execSQL("DROP TABLE IF EXISTS Recordatorio");
        db.execSQL("DROP TABLE IF EXISTS Exportacion");
        db.execSQL("DROP TABLE IF EXISTS Gasto");
        db.execSQL("DROP TABLE IF EXISTS MetodoPago");
        db.execSQL("DROP TABLE IF EXISTS Categoria");
        db.execSQL("DROP TABLE IF EXISTS Usuario");
        onCreate(db);
    }

    // Método para obtener el total de gasto del mes actual
    public double obtenerTotalGastoDelMes() {
        double total = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(monto) FROM Gasto WHERE strftime('%Y-%m', fecha) = strftime('%Y-%m', date('now'))", null);

        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }

        cursor.close();
        db.close();
        return total;
    }

    // Método para obtener el objetivo mensual (más reciente)
    public double obtenerObjetivoMensual() {
        double objetivo = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT monto_limite FROM Objetivo WHERE tipo = 'Mensual' " +
                        "AND strftime('%Y-%m', fecha_inicio) <= strftime('%Y-%m', date('now')) " +
                        "AND (fecha_fin IS NULL OR strftime('%Y-%m', fecha_fin) >= strftime('%Y-%m', date('now'))) " +
                        "ORDER BY fecha_inicio DESC LIMIT 1", null);

        if (cursor.moveToFirst()) {
            objetivo = cursor.getDouble(0);
        }

        cursor.close();
        db.close();
        return objetivo;
    }

}
