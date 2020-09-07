package com.efienza.cliente.persistance;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.efienza.cliente.model.UsuarioVO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by John Manchego Medina on 13/07/2016.
 */
public class SesionHelper {
    public static final String TABLE_NAME = "usuario";
    public static final String ID_USUARIO="id_usuario";
    public static final String NOMBRE_USUARIO="nombre_usuario";
    public static final String CONTASENHA_USUARIO="contrasenha_usuario";
    public static final String FLAG_SESSION="flag_session";
    public DBDatos ayudante;
    public SQLiteDatabase db;
    private Context ctx;
    public SesionHelper(Context ctx)
    {
        this.ctx=ctx;
        ayudante = new DBDatos(this.ctx, DBDatos.NOMBRE_BASE, null, DBDatos.DB_VERSION);
    }
    public void inicialFlag(UsuarioVO usuario){
        ContentValues values = new ContentValues();
        values.put(FLAG_SESSION, usuario.getFlag());
        values.put(NOMBRE_USUARIO,usuario.getLogin());
        values.put(CONTASENHA_USUARIO,usuario.getContrasenha());
        SQLiteDatabase db = ayudante.getWritableDatabase();
        db.insert(TABLE_NAME, null, values);
        db.close();
    }
    public void deleteFlag(){
        SQLiteDatabase db = ayudante.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME );
    }
    public List<UsuarioVO> obtenerFlagSession ()
    {
        List<UsuarioVO> listaUsuarios= new ArrayList<UsuarioVO>();
        db = ayudante.getReadableDatabase();
        Cursor c= db.query(TABLE_NAME, new String[] {FLAG_SESSION,NOMBRE_USUARIO,CONTASENHA_USUARIO},null , null, null, null, null);
        while (c.moveToNext()) {
            UsuarioVO vo = new UsuarioVO();
            vo.setFlag(c.getInt(c.getColumnIndex(FLAG_SESSION)));
            vo.setLogin(c.getString(c.getColumnIndex(NOMBRE_USUARIO)));
            vo.setContrasenha(c.getString(c.getColumnIndex(CONTASENHA_USUARIO)));
            //Log.i("LISTADB", String.valueOf(vo.getflag()));
            listaUsuarios.add(vo);
        }
        db.close();
        return listaUsuarios;
    }
}
