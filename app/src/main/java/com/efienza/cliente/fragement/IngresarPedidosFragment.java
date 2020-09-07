package com.efienza.cliente.fragement;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.efienza.cliente.Bandas.Bandas;
import com.efienza.cliente.R;
import com.efienza.cliente.acitivities.HomeActivity;
import com.efienza.cliente.acitivities.LoginActivity;
import com.efienza.cliente.broadcastReceiver.Ubicacion;
import com.efienza.cliente.common.GlobalClass;
import com.efienza.cliente.common.Utiles;
import com.efienza.cliente.model.UsuarioVO;
import com.efienza.cliente.persistance.SesionHelper;

import java.util.Calendar;
import java.util.List;

/**
 * Created by John Manchego on 11-06-2018.
 */
public class IngresarPedidosFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "FRAGMENTINGRESOPEDIDOS";
    TextView _textViewNombreUsuario,_textFecha; ListView Lista1 = null; SharedPreferences prefs; ProgressDialog _progresDiag;
    Button _buttonRealizarPedido; View header = null; Intent intent;
    String _origen = "",_destino = "",latitude1, longitude1,codPed, pedido,_CODIGOSPEDIDOS;
    double latitude = 0, longitude = 0; String[] ArrayPedido; Context ctx;
    Location location; Ubicacion ub; int contPedidosant = 0,contPedidos = 0; boolean hilo = true;
    NotificationCompat.Builder mBuilder; NotificationManager mgr;
    private static final String[] INITIAL_PERMS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    private static final int INITIAL_REQUEST = 1337;
    private Button mDisplayDate, mDisplayHora;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private Bandas data[];
    ProgressDialog progressDialog;
    private int horas, minutos;
    //Thread hilo;
    Button direcOrigen;
    EditText editDescripcion;
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try{
            ctx=getActivity();
            mDisplayDate = (Button) getView().findViewById(R.id.iddfecha);
            mDisplayHora = (Button) getView().findViewById(R.id.iddhora);
            direcOrigen = (Button) getView().findViewById(R.id.editOrigen);
            direcOrigen.setOnClickListener(this);
            _buttonRealizarPedido= (Button) getView().findViewById(R.id.buttonRealizarPedido);
            _buttonRealizarPedido.setOnClickListener(this);
            editDescripcion = (EditText)getView().findViewById(R.id.editDescripcion);
            mgr=(NotificationManager)getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
            mBuilder = new NotificationCompat.Builder(getActivity());
            prefs = getActivity().getSharedPreferences(Utiles.NOMBRE_PREFERENCIAS, Context.MODE_PRIVATE);
            ((HomeActivity) getActivity()).fontToTitleBar( prefs.getString(Utiles.PREFERENCIAS_NOMBRE_APELLIDO, "Error"));
            eventosFechaHora();
            //eventosDirecciones();
            /* final TextView direcd =  findViewById(R.id.ddestino);
            direcd.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    globalClass.setTitulo("Dirección de Destino");
                    globalClass.setFdirecciones(2);
                    globalClass.setAddd(2);
                    if (det.getText().length()>0) {
                        globalClass.setDetalle(det.getText().toString());
                    }
                    Intent next = new Intent(activity_pedidos.this, .class);
                    startActivity(next);
                }
            });*/
            //Lista1 = (ListView) getView().findViewById(R.id.ListaClientesCercos);
            //header = (View) getLayoutInflater().inflate(R.layout.list_header_row_listado_despachador, null);
            //Lista1.addHeaderView(header);
            //obtenerFecha();
            //validarGPS();
            //determinarUbicacion();
            //obtenerDatosFragment();
        }catch (Exception e){
            Log.d(TAG, "eew: "+ e.toString() );
        }
    }
    public void eventosDirecciones() {
        try{
            Utiles.globalClass = new GlobalClass();
            if ( Utiles.globalClass.getDdo() == 1){
                //ori = findViewById(R.id.dorigen);
                //ori.setText(globalClass.getDorigen());
            }
            if ( Utiles.globalClass.getDd() == 2){
                //dest = findViewById(R.id.ddestino);
                //dest.setText(globalClass.getDdestino());
            }
            Utiles.globalClass.setTitulo("Dirección de Origen");
            Utiles.globalClass.setFdirecciones(1);
            Utiles.globalClass.setAdo(1);
            if (editDescripcion.getText().length()>0) {
                Utiles.globalClass.setDetalle(editDescripcion.getText().toString());
            }
            Fragment fragment = null;
            Bundle bundle = new Bundle();
            fragment = new DireccionesFrecuentesFragment();
            fragment.setArguments(bundle);
            Log.d(TAG, "Antes de enviar al otro fragment");
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.frame, fragment);
            ft.commit();
        }catch (Exception e){
            Log.d(TAG, "ew:"+e.toString());
        }

    }
    public void eventosFechaHora() {
        mDisplayDate.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                Log.d(TAG, "onDateSet: date: " + year + "/" + month );
                DatePickerDialog dialog = new DatePickerDialog(ctx, android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDateSetListener, year,month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Log.d(TAG, "onDateSet: date: " + year + "/" + month + "/" + dayOfMonth);
                String date = dayOfMonth + "/" + month + "/" + year;
                mDisplayDate.setText(date);
            }
        };
        mDisplayHora.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                final Calendar c = Calendar.getInstance();
                horas = c.get(Calendar.HOUR_OF_DAY);
                minutos = c.get(Calendar.MINUTE);

                TimePickerDialog ponerhora = new TimePickerDialog(ctx, new TimePickerDialog.OnTimeSetListener(){
                    public void  onTimeSet(TimePicker view, int hourDay, int minute){
                        //tvhora.setText(hourDay+":"+minute);
                        mDisplayHora.setText(hourDay+":"+minute);
                    }
                },horas,minutos, false);
                ponerhora.show();
            }
        });
    }
    /*public void obtenerDatosFragment() {
        try{
            _CODIGOSPEDIDOS = getArguments().getString("CODIGOSPEDIDOS");
            Log.d(TAG, "_CODIGOSPEDIDOS: "+_CODIGOSPEDIDOS);
        }catch (Exception e){
            Log.d(TAG,"ERROR AL OBTENER LOS DATOS: "+e.toString());
        }
    }
    public void detenerHilo() { hilo = false; }
    public void determinarUbicacion() {
        try {
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                return;
            }
            location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
            latitude =location.getLatitude();
            longitude=location.getLongitude();
        } catch (Exception e) {
            Log.d(TAG, e.toString());
            try {
                ub = new Ubicacion(getActivity());
                Location localizacion;
                localizacion = ub.getLocation(getActivity());
                latitude = localizacion.getLatitude();
                longitude = localizacion.getLongitude();
            }catch (Exception ee){
                Log.d(TAG, ee.toString());
            }
        }
    }
    private void obtenerFecha() {
        Date d = new Date();
        CharSequence s = DateFormat.format("d MMMM yyyy ", d.getTime());
        _textFecha.setText(s);
    }
    private void validarGPS() {
        if (Utiles.validarGPS(getActivity()) == 1) {
            prefs = getActivity().getSharedPreferences(Utiles.NOMBRE_PREFERENCIAS, Context.MODE_PRIVATE);
            Log.d(TAG, "Datos DESPUES:" + prefs.getString(Utiles.PREFERENCIAS_NOMBRE, "Error") + " " + prefs.getString(Utiles.PREFERENCIAS_APELLIDO_PATERNO, "Error") + " " + Utiles.PREFERENCIAS_APELLIDO_MATERNO);
            String nombreUsuario = prefs.getString(Utiles.PREFERENCIAS_NOMBRE, "Error") + " " + prefs.getString(Utiles.PREFERENCIAS_APELLIDO_PATERNO, "Error");
            if (_textViewNombreUsuario != null)
                _textViewNombreUsuario.setText(nombreUsuario);
            try {
                if (!canAccessLocation()) {
                    ActivityCompat.requestPermissions(getActivity(), INITIAL_PERMS, INITIAL_REQUEST);
                }
            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }
        } else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
            alertDialogBuilder.setTitle("GPS Desactivado");
            alertDialogBuilder.setMessage("Active su GPS para un correcto funcionamiento.")
                    .setCancelable(false)
                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }*/
    private boolean canAccessLocation() {
        return (hasPermission(Manifest.permission.ACCESS_FINE_LOCATION));
    }
    private boolean hasPermission(String perm) {
        return (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(getActivity(), perm));
    }
    public void salirApp(){
        SesionHelper usuarioHelper;
        usuarioHelper=new SesionHelper(getActivity());
        List<UsuarioVO> listaMarca = usuarioHelper.obtenerFlagSession();
        if (listaMarca.size() > 0) {
            if (String.valueOf(listaMarca.get(0).getFlag()).compareTo("1") == 0) {
                intent = new Intent(getActivity(), LoginActivity.class);
                intent.putExtra("DATO", "1");
                intent.putExtra("NOMBRE", listaMarca.get(0).getLogin());
                intent.putExtra("CONTRASENHA", listaMarca.get(0).getContrasenha());
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                getActivity().finish();
            }
        } else {
            intent = new Intent(getActivity(), LoginActivity.class);
            intent.putExtra("DATO", "0");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            getActivity().finish();
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonRealizarPedido:
                //detenerHilo();
                //hilo.stop();
                //salirApp();
                break;
            case R.id.editOrigen:
                eventosDirecciones();
                break;
        }
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ingresar_pedidos, container, false);
    }
}
