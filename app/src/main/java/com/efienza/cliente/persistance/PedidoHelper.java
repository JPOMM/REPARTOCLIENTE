package com.efienza.cliente.persistance;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.efienza.cliente.model.PedidoVO;

import java.util.ArrayList;
import java.util.List;

public class PedidoHelper {
	private static final String TAG = "PedidoHelper";

	public static final String TABLE_NAME = "PEDIDO";
	public static final String ID_PEDIDO="ID_PEDIDO";
	public static final String LATITUD="LATITUD";
	public static final String LONGITUD="LONGITUD";
	public static final String DIRECCION="DIRECCION";
	public static final String COD_USUARIO="COD_USUARIO";
	public static final String NUMERO="NUMERO";
	public static final String CONTACTO="CONTACTO";
	public static final String ID_CILINDRO="ID_CILINDRO";
	public static final String CANTIDAD="CANTIDAD";
	public static final String COD_CLIENTE="COD_CLIENTE";
	public static final String TIPO_PEDIDO="TIPO_PEDIDO";
	public static final String MOTIVO_VISITA="MOTIVO_VISITA";
	public static final String PRECIO="PRECIO";
	public static final String OBSERVACION_REFERENCIA="OBSERVACION_REFERENCIA";
	public static final String SEDE="SEDE";

	public DBDatos ayudante;
	public SQLiteDatabase db;
	private Context ctx;
	public PedidoHelper(Context ctx) {
		this.ctx=ctx;
		ayudante = new DBDatos(this.ctx, DBDatos.NOMBRE_BASE, null, DBDatos.DB_VERSION);
	}
	
	public List<PedidoVO> obtenerPedido () {
		List<PedidoVO> listaPedidos= new ArrayList<PedidoVO>();
		try {
			db = ayudante.getReadableDatabase();
			Cursor c = db.query(TABLE_NAME, new String[]{ID_PEDIDO,LATITUD,LONGITUD,DIRECCION,COD_USUARIO,NUMERO,CONTACTO,ID_CILINDRO,CANTIDAD,
					COD_CLIENTE,TIPO_PEDIDO,MOTIVO_VISITA,PRECIO,OBSERVACION_REFERENCIA,SEDE}, null, null, null, null, null);
			while (c.moveToNext()) {
				PedidoVO vo = new PedidoVO();
				vo.setID_PEDIDO(c.getString(c.getColumnIndex(ID_PEDIDO)));
				vo.setLATITUD(c.getString(c.getColumnIndex(LATITUD)));
				vo.setLONGITUD(c.getString(c.getColumnIndex(LONGITUD)));
				vo.setDIRECCION(c.getString(c.getColumnIndex(DIRECCION)));
				vo.setCOD_USUARIO(c.getString(c.getColumnIndex(COD_USUARIO)));
				vo.setNUMERO(c.getString(c.getColumnIndex(NUMERO)));
				vo.setCONTACTO(c.getString(c.getColumnIndex(CONTACTO)));
				vo.setID_CILINDRO(c.getString(c.getColumnIndex(ID_CILINDRO)));
				vo.setCANTIDAD(c.getString(c.getColumnIndex(CANTIDAD)));
				vo.setCOD_CLIENTE(c.getString(c.getColumnIndex(COD_CLIENTE)));
				vo.setTIPO_PEDIDO(c.getString(c.getColumnIndex(TIPO_PEDIDO)));
				vo.setMOTIVO_VISITA(c.getString(c.getColumnIndex(MOTIVO_VISITA)));
				vo.setPRECIO(c.getString(c.getColumnIndex(PRECIO)));
				vo.setOBSERVACION_REFERENCIA(c.getString(c.getColumnIndex(OBSERVACION_REFERENCIA)));
				vo.setSEDE(c.getString(c.getColumnIndex(SEDE)));
				Log.d(TAG, LATITUD);
				Log.d(TAG, LONGITUD);
				listaPedidos.add(vo);
			}
			db.close();
		}catch (Exception e){
			Log.d(TAG, e.toString());
			Log.d(TAG, "Error obtenerPedido");
		}
		return listaPedidos;
	}
	public long ingresarPedido(PedidoVO vo) {
		long filas=0;
		try {
			db = ayudante.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(LATITUD, vo.getLATITUD());
			values.put(LONGITUD, vo.getLONGITUD());
			values.put(DIRECCION, vo.getDIRECCION());
			values.put(COD_USUARIO, vo.getCOD_USUARIO());
			values.put(NUMERO, vo.getNUMERO());
			values.put(CONTACTO, vo.getCONTACTO());
			values.put(ID_CILINDRO, vo.getID_CILINDRO());
			values.put(CANTIDAD, vo.getCANTIDAD());
			values.put(COD_CLIENTE, vo.getCOD_CLIENTE());
			values.put(TIPO_PEDIDO, vo.getTIPO_PEDIDO());
			values.put(MOTIVO_VISITA, vo.getMOTIVO_VISITA());
			values.put(PRECIO, vo.getPRECIO());
			values.put(OBSERVACION_REFERENCIA, vo.getOBSERVACION_REFERENCIA());
			values.put(SEDE, vo.getSEDE());
			Log.d(TAG, "values insert: " + values);
			Log.d(TAG, "TABLE_NAME: " + TABLE_NAME);
			filas =db.insert(TABLE_NAME, null, values);
			Log.d(TAG,"filas"+ filas);
			db.close();
			return filas;
		}catch (Exception e){
			Log.d(TAG, e.toString());
			Log.d(TAG, "Error al ingresar pedido");
			return filas;
		}
	}
	public int borrarPedido() {
		db = ayudante.getWritableDatabase();
		int filas = db.delete(TABLE_NAME,"", new String[]{});
		db.close();
		return filas;
	}
	
}
