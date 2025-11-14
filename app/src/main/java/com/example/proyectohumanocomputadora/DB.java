package com.example.proyectohumanocomputadora;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DB extends SQLiteOpenHelper {

    private static final String DB_NAME = "clinica.db";
    private static final int DB_VERSION = 1;

    public DB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Tabla usuarios
        db.execSQL("CREATE TABLE usuarios (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "usuario TEXT UNIQUE," +
                "password TEXT," +
                "tipo TEXT," +         // paciente o medico
                "nombre TEXT," +
                "apellido TEXT)");

        // Tabla citas
        db.execSQL("CREATE TABLE citas (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "idPaciente INTEGER," +
                "idMedico INTEGER," +
                "fecha TEXT," +
                "hora TEXT," +
                "motivo TEXT," +
                "estado TEXT)");

        // Tabla documentos
        db.execSQL("CREATE TABLE documentos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "idPaciente INTEGER," +
                "nombreArchivo TEXT," +
                "ruta TEXT)");

        // Tabla expedientes
        db.execSQL("CREATE TABLE expedientes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "idPaciente INTEGER," +
                "descripcion TEXT," +
                "fecha TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    // -----------------------------
    //          USUARIOS
    // -----------------------------

    public boolean registrarUsuario(String usuario, String pass, String tipo, String nombre, String apellido) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("usuario", usuario);
        cv.put("password", pass);
        cv.put("tipo", tipo);
        cv.put("nombre", nombre);
        cv.put("apellido", apellido);

        long res = db.insert("usuarios", null, cv);
        return res != -1;
    }

    // LOGIN
    public Cursor login(String usuario, String pass) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM usuarios WHERE usuario=? AND password=?",
                new String[]{usuario, pass});
    }

    // -----------------------------
    //            CITAS
    // -----------------------------

    public boolean agendarCita(int idPaciente, int idMedico, String fecha, String hora, String motivo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("idPaciente", idPaciente);
        cv.put("idMedico", idMedico);
        cv.put("fecha", fecha);
        cv.put("hora", hora);
        cv.put("motivo", motivo);
        cv.put("estado", "pendiente");

        long res = db.insert("citas", null, cv);
        return res != -1;
    }

    public Cursor obtenerCitasPaciente(int idPaciente) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM citas WHERE idPaciente=?",
                new String[]{String.valueOf(idPaciente)});
    }

    public Cursor obtenerCitasMedico(int idMedico) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM citas WHERE idMedico=?",
                new String[]{String.valueOf(idMedico)});
    }

    // Cambiar estado cita (médico)
    public boolean actualizarEstadoCita(int idCita, String nuevoEstado) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("estado", nuevoEstado);

        int res = db.update("citas", cv, "id=?", new String[]{String.valueOf(idCita)});
        return res > 0;
    }

    // -----------------------------
    //          DOCUMENTOS
    // -----------------------------

    public boolean guardarDocumento(int idPaciente, String nombreArchivo, String ruta) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("idPaciente", idPaciente);
        cv.put("nombreArchivo", nombreArchivo);
        cv.put("ruta", ruta);

        return db.insert("documentos", null, cv) != -1;
    }

    public Cursor obtenerDocumentos(int idPaciente) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM documentos WHERE idPaciente=?",
                new String[]{String.valueOf(idPaciente)});
    }

    // -----------------------------
    //        EXPEDIENTES
    // -----------------------------

    public boolean guardarExpediente(int idPaciente, String descripcion, String fecha) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("idPaciente", idPaciente);
        cv.put("descripcion", descripcion);
        cv.put("fecha", fecha);

        return db.insert("expedientes", null, cv) != -1;
    }

    public Cursor obtenerExpedientes(int idPaciente) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM expedientes WHERE idPaciente=?",
                new String[]{String.valueOf(idPaciente)});
    }
}