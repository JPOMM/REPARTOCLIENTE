package com.efienza.cliente.fragement;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.efienza.cliente.R;
import com.efienza.cliente.acitivities.RegisterActivity;
import com.efienza.cliente.common.JSONParser;
import com.efienza.cliente.common.Utiles;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by John Manchego on 11-06-2018.
 */
public class DireccionesFrecuentesFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "FRAGMENTEDIRECCIONES";
    Context ctx;
    ListView lista;
    ArrayList dataModels;
    TextView titulo, adddirecciones;
    String [][] datos = {{"direccion 1", "casa"},{"direccion 2", "casa"},{"direccion 2", "casa"}};
    private static final String[] INITIAL_PERMS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    private static final int INITIAL_REQUEST = 1337;
    String response1;
    String cDireccion,nLat,nLon,cReferencia;
    SharedPreferences prefs;
    ArrayList<String> ArrayDirecciones;
    ListView listView;
    public CustomAdapter adapter;
    private String[][] MatrizDirecciones;
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try{
            ctx=getActivity();
            prefs = getActivity().getSharedPreferences(Utiles.NOMBRE_PREFERENCIAS, Context.MODE_PRIVATE);
            titulo = getView().findViewById(R.id.titulo);
            titulo.setText(Utiles.globalClass.getTitulo());
            adddirecciones = getView().findViewById(R.id.addDireccion);
            adddirecciones.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Utiles.globalClass.getFdirecciones()==1) {
                        //Intent next = new Intent(.this, direcciones.class);
                        //startActivity(next);
                    }
                    if(Utiles.globalClass.getFdirecciones()==2) {
                        //Intent next = new Intent(.this, direcciones_destino.class);
                        //startActivity(next);
                    }
                }
            });
            //listView = (ListView) getView().findViewById(R.id.listViewId);
            dataModels = new ArrayList();
            leerDireccionesFrecuentes();

            lista = (ListView) getView().findViewById(R.id.listViewId);
            /*ArrayAdapter<CharSequence> adaptador = ArrayAdapter.createFromResource( getActivity(), R.array.arrayDias, android.R.layout.simple_list_item_1);
            lista.setAdapter(adaptador);
            lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (Utiles.globalClass.getFdirecciones() == 1) {
                        Utiles.globalClass.setDorigen(parent.getItemAtPosition(position).toString());
                        Utiles.globalClass.setDdo(1);
                    //Toast.makeText(parent.getContext(), "Selecciona: " + parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                    //Intent next = new Intent(.this, activity_pedidos.class);
                    //startActivity(next);
                    }
                    if (Utiles.globalClass.getFdirecciones() == 2) {
                        Utiles.globalClass.setDdestino(parent.getItemAtPosition(position).toString());
                        Utiles.globalClass.setDd(2);
                    //Toast.makeText(parent.getContext(), "Selecciona: " + parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                    //Intent next = new Intent(.this, activity_pedidos.class);
                    //startActivity(next);
                    }
                }
            });*/
        }catch (Exception e){
            Log.d(TAG, "eew: "+ e.toString() );
        }
    }
    private void leerDireccionesFrecuentes(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                sincronizarDireccionesFrecuentes();
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        mostrarDireccionesFrecuentes();
                    }
                });
            }
        }).start();
    }
    public DataModel getItem(int position) {
        return (DataModel) dataModels.get(position);
    }
    private void sincronizarDireccionesFrecuentes(){
        try {
            JSONParser jsonparser1 = new JSONParser();
            response1 = jsonparser1.getResponse(Utiles.URL_SERVIDOR + "/" + "obtenerDireccionesFrecuentesIni"+ "/" +prefs.getString(Utiles.PREFERENCIAS_ID_USUARIO, "Error"));
            Log.d(TAG, "URL: " + Utiles.URL_SERVIDOR + "/" + "obtenerDireccionesFrecuentesIni"+ "/" +prefs.getString(Utiles.PREFERENCIAS_ID_USUARIO, "Error"));
            Log.d(TAG, "response1: " + response1);
            Log.d(TAG, response1);
        } catch (Exception e) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx);
            alertDialogBuilder.setTitle("El servidor no esta activo");
            alertDialogBuilder.setMessage("Hubo un error al conectar al Servidor, revise que este conectado a la red correcta.")
                    .setCancelable(false)
                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
        try {
            JSONArray jsonArray1 = new JSONArray(response1);
            //ArrayDirecciones= new String[jsonArray1.length()+1];
            ArrayDirecciones = new ArrayList<String>();
            MatrizDirecciones=new String[jsonArray1.length()][4];
            //ArrayDirecciones[0]="Localidad";
            for (int i = 0; i < jsonArray1.length(); i++) {
                JSONObject jsonObject = jsonArray1.getJSONObject(i);
                cDireccion=jsonObject.getString("cDireccion");
                nLat=jsonObject.getString("nLat");
                nLon=jsonObject.getString("nLon");
                cReferencia=jsonObject.getString("cReferencia");
                MatrizDirecciones[i][0]=cDireccion;
                MatrizDirecciones[i][1]=nLat;
                MatrizDirecciones[i][2]=nLon;
                MatrizDirecciones[i][3]=cReferencia;
                //ArrayDirecciones[i]= cDireccion;
                ArrayDirecciones.add(cDireccion);
                dataModels.add(new DataModel(cDireccion,Double.parseDouble(nLat),Double.parseDouble( nLon),cReferencia));
                //Log.d(TAG, "ArrayDirecciones: " + ArrayDirecciones[i]);
                Log.d(TAG, "nLat: " + nLat);
                Log.d(TAG, "nLon: " + nLon);
                Log.d(TAG, "cReferencia: " + cReferencia);
            }
        } catch (JSONException e) {
            Log.d(TAG,e.toString());
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx);
                    alertDialogBuilder.setTitle("El servidor no esta activo");
                    alertDialogBuilder.setMessage("Hubo un error al conectar al Servidor, revise que este conectado a la red correcta.")
                            .setCancelable(false)
                            .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            });
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
    }
    private void mostrarDireccionesFrecuentes() {
        try {
            lista = (ListView) getView().findViewById(R.id.listViewId);
            //ArrayAdapter<CharSequence> adaptador = ArrayAdapter.createFromResource( getActivity(), R.array.arrayDias, android.R.layout.simple_list_item_1);
            ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, ArrayDirecciones);
            lista.setAdapter(itemsAdapter);
            lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (Utiles.globalClass.getFdirecciones() == 1) {
                        Utiles.globalClass.setDorigen(parent.getItemAtPosition(position).toString());
                        Utiles.globalClass.setDdo(1);
                        Toast.makeText(parent.getContext(), "Selecciona: " + parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                        //Intent next = new Intent(.this, activity_pedidos.class);
                        //startActivity(next);
                    }
                    if (Utiles.globalClass.getFdirecciones() == 2) {
                        Utiles.globalClass.setDdestino(parent.getItemAtPosition(position).toString());
                        Utiles.globalClass.setDd(2);
                        Toast.makeText(parent.getContext(), "Selecciona: " + parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                        //Intent next = new Intent(.this, activity_pedidos.class);
                        //startActivity(next);
                    }
                }
            });
        } catch (Exception e) {
            Log.d(TAG, "Exeption"+e.toString());
        }
        /*adapter = new CustomAdapter(dataModels,getActivity());
        listView.setAdapter(null);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?>  parent, View view, int position, long id) {
                try {
                    DataModel dataModel= (DataModel) dataModels.get(position);
                    adapter.notifyDataSetChanged();
                    final TextView txtCod = (TextView) view.findViewById(R.id.txtCodPed);
                    //Log.d(TAG, " txtCod"+ txtCod.getText().toString()+ " "+dataModel.checked);
                }catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, " Exception: "+ e.toString());
                }
            }
        });*/
    }
    private boolean canAccessLocation() {
        return (hasPermission(Manifest.permission.ACCESS_FINE_LOCATION));
    }
    private boolean hasPermission(String perm) {
        return (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(getActivity(), perm));
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonRealizarPedido:
                //detenerHilo();
                //hilo.stop();
                //salirApp();
                break;
        }
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_direcciones_frecuentes, container, false);
    }
}
