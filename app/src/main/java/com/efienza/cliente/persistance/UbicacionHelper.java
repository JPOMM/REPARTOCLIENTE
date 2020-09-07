package com.efienza.cliente.persistance;



import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.efienza.cliente.model.UbicacionVO;

import java.util.ArrayList;
import java.util.List;


public class UbicacionHelper {
	public static final String TABLE_NAME = "ubicacion";
	public static final String ID_UBICACION= "id_ubicacion";
	public static final String MENSAJE= "mensaje";

	private static final String TAG = "UbicacionHelper";
	public DBDatos ayudante;
	public SQLiteDatabase db;
	private Context ctx;
	public UbicacionHelper(Context ctx) {
		this.ctx=ctx;
		ayudante = new DBDatos(this.ctx, DBDatos.NOMBRE_BASE, null, DBDatos.DB_VERSION);
	}
	
	public List<UbicacionVO> obtenerUbicacion () {
		List<UbicacionVO> listaMarca= new ArrayList<UbicacionVO>();
		try {
			db = ayudante.getReadableDatabase();
			Cursor c = db.query(TABLE_NAME, new String[]{ID_UBICACION, MENSAJE}, null, null, null, null, null);
			while (c.moveToNext()) {
				UbicacionVO vo = new UbicacionVO();
				vo.setIdUbicacion(c.getInt(c.getColumnIndex(ID_UBICACION)));
				vo.setMensaje(c.getString(c.getColumnIndex(MENSAJE)));
				listaMarca.add(vo);
			}
			db.close();
		}catch (Exception e){
			Log.d(TAG, "Error obtenerUbicacion");
		}
		return listaMarca;
	}
	public long ingresarUbicacion(UbicacionVO vo) {
		db = ayudante.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(MENSAJE, vo.getMensaje());
		long filas = db.insert(TABLE_NAME, null, values);
		db.close();
		return filas;
	}
	public int borrarUbicacion(int idUbicacion) {
		db = ayudante.getWritableDatabase();
		int filas = db.delete(TABLE_NAME, ID_UBICACION+"=?", new String[]{String.valueOf(idUbicacion)});
		db.close();
		return filas;
	}
	
}
