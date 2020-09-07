package com.efienza.cliente.common;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.efienza.cliente.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by John Manchego Medina on 04/04/2017.
 */
public class LogErrores extends AppCompatActivity {
    private static final String TAG = "LOGERRORES";
    static final int READ_BLOCK_SIZE = 100;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
    }
    static File tarjeta=null;
    static File file=null;
    OutputStreamWriter osw=null;
    public LogErrores(){
        try{
            tarjeta = Environment.getExternalStorageDirectory();
            file = new File(tarjeta.getAbsolutePath(), "LOGERRORES.TXT");
            osw= new OutputStreamWriter(new FileOutputStream(file));
        }catch (Exception e) {
            Log.i(TAG,"CONSTRCUTOR: "+ e.toString());
        }
    }
    public void guardarErrores(String Data) {
        try{
            Log.i(TAG,"Antes de grabar");
            osw.write("\n");
            osw.write(Data);
            Log.i(TAG, Data);
            Log.i(TAG, "Grabo");
            osw.flush();
            Log.i(TAG, "Termino");
            //osw.close();
            //Toast.makeText(this, "Los datos fueron grabados correctamente", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Log.d(TAG, e.toString());
        }
    }
    public void recuperarErrores() {
        try{
            FileInputStream fIn = new FileInputStream(file);
            InputStreamReader archivo = new InputStreamReader(fIn);
            BufferedReader br = new BufferedReader(archivo);
            String linea = br.readLine();
            String todo = "";
            while (linea != null) {
                todo = todo + linea + " ";
                linea = br.readLine();
            }
            Log.i(TAG, "DATOS TXT: " + todo);
            br.close();
            archivo.close();

        }catch (IOException ex){
            Log.d(TAG, ex.toString());
        }
    }
}
