package com.efienza.cliente.persistance;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by John Paul Manchego Medina on 13/07/2016.
 */
public class DBDatos extends SQLiteOpenHelper {
    private static final String TAG = "DBDATOS";
    public final static String NOMBRE_BASE="DIGAS";
    public	static final int DB_VERSION = 1;
    final  static String USUARIO = "CREATE TABLE "+SesionHelper.TABLE_NAME +" (" +
            SesionHelper.ID_USUARIO+" INTEGER PRIMARY KEY, " +
            SesionHelper.NOMBRE_USUARIO+" TEXT NOT NULL, " +
            SesionHelper.CONTASENHA_USUARIO+" TEXT NOT NULL, " +
            SesionHelper.FLAG_SESSION+" TEXT NOT NULL)";

    final  static String UBICACIONES = "CREATE TABLE "+UbicacionHelper.TABLE_NAME +" (" +
            UbicacionHelper.ID_UBICACION+" INTEGER PRIMARY KEY, " +
            UbicacionHelper.MENSAJE+" TEXT NOT NULL)";

    final  static String PEDIDOS = "CREATE TABLE "+PedidoHelper.TABLE_NAME +" (" +
            PedidoHelper.ID_PEDIDO+" INTEGER PRIMARY KEY, " +
            PedidoHelper.LATITUD+" TEXT NOT NULL, "+
            PedidoHelper.LONGITUD+" TEXT NOT NULL, "+
            PedidoHelper.DIRECCION+" TEXT NOT NULL, "+
            PedidoHelper.COD_USUARIO+" TEXT NOT NULL, "+
            PedidoHelper.NUMERO+" TEXT NOT NULL, "+
            PedidoHelper.CONTACTO+" TEXT NOT NULL, "+
            PedidoHelper.ID_CILINDRO+" TEXT NOT NULL, "+
            PedidoHelper.CANTIDAD+" TEXT NOT NULL, "+
            PedidoHelper.COD_CLIENTE+" TEXT NOT NULL, "+
            PedidoHelper.TIPO_PEDIDO+" TEXT NOT NULL, "+
            PedidoHelper.MOTIVO_VISITA+" TEXT NOT NULL, "+
            PedidoHelper.PRECIO+" TEXT NOT NULL, "+
            PedidoHelper.OBSERVACION_REFERENCIA+" TEXT NOT NULL, "+
            PedidoHelper.SEDE+" TEXT NOT NULL)";

    public DBDatos(Context contexto, String nombre, SQLiteDatabase.CursorFactory factory, int version) {
        super(contexto, nombre, factory, version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG,PEDIDOS);
        db.execSQL(PEDIDOS);
        db.execSQL(UBICACIONES);
        db.execSQL(USUARIO);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS "+SesionHelper.TABLE_NAME);
        db.execSQL(USUARIO);
        db.execSQL("DROP TABLE IF EXISTS "+SesionHelper.TABLE_NAME);
        db.execSQL(PEDIDOS);
    }
}