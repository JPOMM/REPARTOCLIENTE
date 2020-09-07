package com.efienza.cliente.fragement;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.efienza.cliente.R;
import com.efienza.cliente.acitivities.HomeActivity;
import com.efienza.cliente.broadcastReceiver.Ubicacion;
import com.efienza.cliente.common.JSONParser;
import com.efienza.cliente.common.Utiles;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.List;

/**
 * Created by John Manchego on 11-06-2018.
 */
public class MapaFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "MapaFragment";
    SharedPreferences prefs;
    Context ctx;
    EditText _editTextBuscarDireccion;
    Ubicacion ub;
    String direccion, contact, numero, obsRef, CodUsuario, cantidad="", identCilindro, precio, _Sede;
    Geocoder geocoder;
    Location location;
    double latitude,longitud,latitud,latitudemap;
    LatLng ll;
    Intent intent;
    String referencia="", idPedidoFrecuente = "0", latitudFre, longitudFre,nCodTipBal="0",nCodCap="0",nCodPro="0";
    ImageButton _imgBuscarDireccion, _imgLlamar;
    Button _buttonFrecuente, _buttonSiguiente,_buttonMostrarFragment;
    MapView mMapView;
    private GoogleMap googleMap;
    private String[][] MatrizPedidos;
    String[] ArrayPedidos;
    float distance;
    List<Address> direcciones = null;
    Address direccionMap;
    String telUsu="";
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        _imgBuscarDireccion = (ImageButton) getView().findViewById(R.id.imgBuscarDireccion);
        _imgLlamar = (ImageButton) getView().findViewById(R.id.imgLlamar);
        _editTextBuscarDireccion = getView().findViewById(R.id.editTextBuscarDireccion);
        _buttonFrecuente = (Button) getView().findViewById(R.id.buttonFrecuente);
        _buttonSiguiente = (Button) getView().findViewById(R.id.buttonSiguiente);

        _buttonMostrarFragment = (Button) getView().findViewById(R.id.buttonMostrarFragment);

        _imgBuscarDireccion.setOnClickListener(this);
        _imgLlamar.setOnClickListener(this);
        _buttonSiguiente.setOnClickListener(this);
        _buttonFrecuente.setOnClickListener(this);

        _buttonMostrarFragment.setOnClickListener(this);

        prefs = getActivity().getSharedPreferences(Utiles.NOMBRE_PREFERENCIAS, Context.MODE_PRIVATE);
        CodUsuario = prefs.getString(Utiles.PREFERENCIAS_ID_USUARIO, "Error");
        geocoder = new Geocoder(getActivity());
        _Sede = prefs.getString(Utiles.PREFERENCIAS_SEDE, "Error");
        ctx = getActivity();
        telUsu = prefs.getString(Utiles.PREFERENCIAS_TELEFONO, "Error");
        if(telUsu.compareToIgnoreCase("")==0){
            telUsu="0";
        }
        validarGPS();
        obtenerPedidosFrecuentes();
        ((HomeActivity) getActivity()).fontToTitleBar( prefs.getString(Utiles.PREFERENCIAS_NOMBRE_APELLIDO, "Error"));
    }
    private void obtenerPedidosFrecuentes() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                sincronizarPedidos();
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Log.d(TAG, "Antes de mostrar PEDIDOS");
                        mostrarPedidos();
                    }
                });
            }
        }).start();
    }
    private void sincronizarPedidos() {
        //String CodUsu = prefs.getString(Utiles.PREFERENCIAS_ID_USUARIO, "Error");
        String CodUsu = "0";
        Log.d(TAG, "CodUsu: " + CodUsu);
        Log.d(TAG, "telUsu: " + telUsu);
        if(CodUsu.compareToIgnoreCase("")!=0 && telUsu.compareToIgnoreCase("")!=0){
            Log.d(TAG, Utiles.URL_SERVIDOR + "/obtenerPedidosFrecuentes" + "/" + CodUsu+"/"+telUsu);
            try {
                JSONParser jsonparser2 = new JSONParser();
                String response = jsonparser2.getResponse(Utiles.URL_SERVIDOR + "/obtenerPedidosFrecuentes" + "/" + CodUsu+"/"+telUsu);
                String str = "";
                Log.d(TAG, "response: " + response);
                JSONArray jsonArray = new JSONArray(response);
                Log.d(TAG, "BASEDATOS: " + jsonArray.getJSONObject(0).toString());
                JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                str = jsonObject1.getString("nCodPed");
                Log.d(TAG, "str: " + str);
                String[] datos;
                if (str.compareToIgnoreCase("Error") == 0) {
                    Log.d(TAG, "ERROR: ");
                    ArrayPedidos = new String[1];
                    MatrizPedidos = new String[1][9];
                    MatrizPedidos[0][0] = "0";  //Codigo
                    MatrizPedidos[0][1] = "Nuevo pedido";  //Nuevo pedido
                    MatrizPedidos[0][2] = "0";  //Nuevo pedido
                    MatrizPedidos[0][3] = "0";  //Nuevo peobtenerPedidosFrecuentesdido
                    MatrizPedidos[0][4] = "";  //Referencia
                    MatrizPedidos[0][5] = "";  //Codigo de producto
                    MatrizPedidos[0][6] = "";  //Codigo de capacidad de balon
                    MatrizPedidos[0][7] = "";  //Codigo de tipo de balon
                    MatrizPedidos[0][8] = "";  //Cantidad
                    ArrayPedidos[0] = "Nuevo pedido";
                    Log.d(TAG, "Termino: ");
                } else {
                    Log.d(TAG, "ENTRO ELSE");
                    ArrayPedidos = new String[jsonArray.length() + 1];
                    MatrizPedidos = new String[jsonArray.length() + 1][9];
                    //Arraylugares[0] = "Seleccione";
                    Log.d(TAG, "TAMANHO: " + jsonArray.length());
                    int tamanho = jsonArray.length();
                    for (int i = 0; i < tamanho; i++) {
                        Log.d(TAG, "ENTRO:" + i + " VECES");
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Log.d(TAG, "DATOS:" + jsonObject.getString("pedido"));
                        //product = str.split("\\|");
                        Log.d(TAG, "nCodPed: "+String.valueOf(jsonObject.getInt("nCodPed")));
                        MatrizPedidos[i][0] = String.valueOf(jsonObject.getInt("nCodPed"));  //Codigo del pedido
                        //Matrizlugares[i][1] = String.valueOf(jsonObject.getString("pedido"));  //Datos del pedido
                        //Arraylugares[i] =  String.valueOf(jsonObject.getString("pedido"));
                        datos = String.valueOf(jsonObject.getString("pedido")).split("\\|");
                        //Log.d(TAG, "datos0:" +datos[0]);
                        //Log.d(TAG, "datos1:" +datos[1]);
                        //Log.d(TAG, "datos2:" +datos[2]);
                        //ArrayPedidos[i] = String.valueOf(jsonObject.getInt("nCodPed"))+ "-" + datos[0];;
                        //MatrizPedidos[i][0] = String.valueOf(jsonObject.getInt("nCodPed"))+ "-" + datos[0];//Codigo
                        ArrayPedidos[i] = datos[0];
                        //MatrizPedidos[i][0] =  datos[0];//Codigo
                        MatrizPedidos[i][1] = datos[0]; //DIRECCION
                        MatrizPedidos[i][2] = datos[1];
                        MatrizPedidos[i][3] = datos[2];
                        MatrizPedidos[i][4] = datos[3];//Referencia
                        MatrizPedidos[i][5] = datos[4];//nCodPro
                        MatrizPedidos[i][6] = datos[5];//nCodCap
                        MatrizPedidos[i][7] = datos[6];//nCodTipBal
                        MatrizPedidos[i][8] = datos[7];//Cantidad
                        Log.d(TAG, "nCodTipBal: "+MatrizPedidos[i][6]);
                        Log.d(TAG, "nCodCap: "+MatrizPedidos[i][7]);

                        Log.d(TAG, "CANTIDAD: "+MatrizPedidos[i][8]);
                        Log.d(TAG, "TERMINO FOR");
                    /*Log.d(TAG, "i: " + i);
                    Log.d(TAG, "Arraylugares: "+"["+i+"]:" + Arraylugares[i]);
                    Log.d(TAG, "MatrizCilindro[i][0]: " + Matrizlugares[i][0]);
                    Log.d(TAG, "MatrizCilindro[i][1]: " + Matrizlugares[i][1]);*/
                    }
                    Log.d(TAG, "Problema");
                    MatrizPedidos[jsonArray.length()][0] = "0";  //Codigo
                    MatrizPedidos[jsonArray.length()][1] = "Nuevo pedido";  //Nuevo pedido
                    ArrayPedidos[jsonArray.length()] = "Nuevo pedido";
                    Log.d(TAG, "Termino: ");
                }
            } catch (JSONException e) {
                Log.d(TAG, "e: " + e.toString());
            } catch (Exception e) {
                Log.d(TAG, "e: " + e.toString());
            }
        }else{
            Toast.makeText(getActivity().getApplicationContext(), "Este usuario no tiene telefono actualizado", Toast.LENGTH_LONG).show();
            Log.d(TAG, "ERROR: ");
            ArrayPedidos = new String[1];
            MatrizPedidos = new String[1][8];
            MatrizPedidos[0][0] = "0";  //Codigo
            MatrizPedidos[0][1] = "Nuevo pedido";  //Nuevo pedido
            MatrizPedidos[0][2] = "0";  //Nuevo pedido
            MatrizPedidos[0][3] = "0";  //Nuevo peobtenerPedidosFrecuentesdido
            MatrizPedidos[0][4] = "";  //Referencia
            MatrizPedidos[0][5] = "";  //Codigo de producto
            MatrizPedidos[0][6] = "";  //Codigo de capacidad de balon
            MatrizPedidos[0][7] = "";  //Codigo de tipo de balon
            MatrizPedidos[0][8] = "";  //Cantidad
            ArrayPedidos[0] = "Nuevo pedido";
            Log.d(TAG, "Termino: ");
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mapa, container, false);
        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately
        initMapa();
        return rootView;
    }
    private void buscarDireccion() {
        try {
            String location = _editTextBuscarDireccion.getText().toString();
            Log.d(TAG, "location: " + location);
            List<Address> addressList = null;
            if (location.compareToIgnoreCase("") != 0) {
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                Log.d(TAG, "inputMethodManager: " + inputMethodManager);
                inputMethodManager.hideSoftInputFromWindow(_editTextBuscarDireccion.getWindowToken(), 0);
                if (location == null || !location.equals("")) {
                    Geocoder geocoder = new Geocoder(getActivity());
                    Log.d(TAG, "geocoder: " + geocoder);
                    try {
                        Log.d(TAG, "location: " + location);
                        addressList = geocoder.getFromLocationName(location, 1);
                        Log.d(TAG, "addressList: " + addressList);
                        Address address = addressList.get(0);
                        Log.d(TAG, "address: " + address);
                        Log.d(TAG, "address: " + address.getLatitude());
                        Log.d(TAG, "address: " + address.getLongitude());
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
                    } catch (IOException e) {
                        Log.d(TAG, "ERROR EN GEOCODER: " + e.toString());
                    }
                }
            } else if (location.compareToIgnoreCase("") == 0) {
                Log.d(TAG, "VACIO: ");
                try {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    alertDialogBuilder.setTitle("Error en el ingreso de datos");
                    alertDialogBuilder.setMessage("Ingrese una dirreción para poder buscarla.")
                            .setCancelable(false)
                            .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                } catch (Exception e) {
                    Log.d(TAG, "ERROR: " + e.toString());
                }
            }
        } catch (Exception e) {
            Toast.makeText(getActivity().getApplicationContext(), "Ingrese mayor información para poder buscar", Toast.LENGTH_LONG).show();
            Log.d(TAG, "ERROR EN BUSCAR DIRECCÓN: " + e.toString());
        }
    }
    private void llamar() {
        if (_Sede.compareToIgnoreCase("1") == 0) {
            try {
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:054508759")));
            } catch (Exception e) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:054508759")));
                Log.d(TAG, e.toString());
            }
        } else if (_Sede.compareToIgnoreCase("2") == 0) {
            try {
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:57333333")));
            } catch (Exception e) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:57333333")));
                Log.d(TAG, e.toString());
            }
        } else if (_Sede.compareToIgnoreCase("3") == 0) {
            try {
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:054508759")));
            } catch (Exception e) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:054508759")));
                Log.d(TAG, e.toString());
            }
        } else if (_Sede.compareToIgnoreCase("4") == 0) {
            try {
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:064238383")));
            } catch (Exception e) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:064238383")));
                Log.d(TAG, e.toString());
            }
        } else if (_Sede.compareToIgnoreCase("6") == 0) {
            try {
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:5336666")));
            } catch (Exception e) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:5336666")));
                Log.d(TAG, e.toString());
            }
        } else if (_Sede.compareToIgnoreCase("7") == 0) {
            try {
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:53462020")));
            } catch (Exception e) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:53462020")));
                Log.d(TAG, e.toString());
            }
        }
    }
    private void validarGPS() {
        if (Utiles.validarGPS(getActivity()) == 1) {
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
    }
    public void initMapa() {
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                if (idPedidoFrecuente.compareToIgnoreCase("0") == 0) {
                    try {
                        googleMap = mMap;
                        if (ActivityCompat.checkSelfPermission(getActivity(),
                                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),
                                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            return;
                        }
                        googleMap.setMyLocationEnabled(true);
                        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                        googleMap.getUiSettings().setZoomControlsEnabled(false);
                        googleMap.getUiSettings().setZoomGesturesEnabled(true);
                        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        try {
                            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                            Criteria criteria = new Criteria();
                            if (ActivityCompat.checkSelfPermission(getActivity(),
                                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),
                                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                return;
                            }
                            location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
                            latitude = location.getLatitude();
                        } catch (Exception e) {
                            Log.d(TAG, "Exception 1 " + e.toString());
                            try {
                                ub = new Ubicacion(getActivity());
                                location = ub.getLocation(getActivity());
                                latitude = location.getLatitude();
                                Log.d(TAG, "latitude: " + latitude);
                            } catch (Exception ee) {
                                Log.d(TAG, "Exception 2 " + ee.toString());
                            }
                        }
                        Log.d(TAG, "Longitude 1: " + String.valueOf(location.getLatitude()));
                        Log.d(TAG, "latitude 1: " + String.valueOf(location.getLongitude()));
                        ll = new LatLng(location.getLatitude(), location.getLongitude());
                        Log.d(TAG, "ll: " + String.valueOf(ll));
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ll, 18));
                        Log.d(TAG, "PASO LA CAMARA");
                    } catch (Exception e) {
                        Log.d(TAG, "Error Mapa: " + e.toString());
                    }
                } else {
                    try {
                        Log.d(TAG, "ENTRO ELSEE PEDIDO FRECUENTE" + String.valueOf(location.getLatitude()));
                        Log.d(TAG, "latitudFre XDXD: " + String.valueOf(latitudFre));
                        Log.d(TAG, "latitudFre XDXD: " + String.valueOf(longitudFre));
                        googleMap = mMap;
                        googleMap.setMyLocationEnabled(true);
                        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                        googleMap.getUiSettings().setZoomControlsEnabled(false);
                        googleMap.getUiSettings().setZoomGesturesEnabled(true);
                        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        ll = new LatLng(Double.parseDouble(latitudFre), Double.parseDouble(longitudFre));
                        Log.d(TAG, "ll: " + String.valueOf(ll));
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ll, 18));
                    } catch (Exception e) {
                        Log.d(TAG, "Error Mapa: " + e.toString());
                    }
                }
            }
        });
    }
    @Override
    public void onResume() { super.onResume();mMapView.onResume(); }
    @Override
    public void onPause() { super.onPause();mMapView.onPause(); }
    @Override
    public void onDestroy() { super.onDestroy();mMapView.onDestroy(); }
    @Override
    public void onLowMemory() { super.onLowMemory();mMapView.onLowMemory(); }
    public void obtenerUbicacion() {
        try {
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
                latitudemap = location.getLatitude();
                Log.d(TAG,"latitudemap"+ latitudemap);
                return;
            }
        }catch(Exception e) {
            Log.d(TAG,"Exception 1 "+ e.toString());
            Utiles.objError.guardarErrores(TAG + ":4: " + e.toString());
            try {
                ub = new Ubicacion(getActivity());
                location = ub.getLocation(getActivity());
                latitudemap = location.getLatitude();
                Log.d(TAG,"latitudemap"+ latitudemap);
            }catch (Exception ee){
                Log.d(TAG, "Exception 2 " + ee.toString());
                Utiles.objError.guardarErrores(TAG+":5: "+e.toString());
            }
        }
    }
    private void mostrarPedidos(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this.ctx);
        builder.setTitle("Pedido Frecuente");
        final int checkedItem = 0; // cow
        //for (int i = 0; i < Arraylugares.length; i++) {
        //Log.d(TAG, "Arraylugares: " + Arraylugares[i]);
        //}
        final String[] Seleccionado=new String[1];
        Log.d(TAG, "ArrayPedidos[0]: " + ArrayPedidos[0]);
        Seleccionado[0]= ArrayPedidos[0];
        builder.setSingleChoiceItems(ArrayPedidos, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // user checked an item
                Log.d(TAG, "which: " + which);
                Log.d(TAG, "Arraylugares: " + ArrayPedidos[which]);
                Seleccionado[0]= ArrayPedidos[which];
            }
        });
        Log.d(TAG, "Arraylugares XDXD: " +Seleccionado[0]);
        // add OK and Cancel buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // user clicked OK
                for (int i = 0; i < ArrayPedidos.length; i++) {
                    Log.d(TAG, "ArrayPedidos: " + ArrayPedidos[i]);
                    Log.d(TAG, "MatrizPedidos[i][0]: " + MatrizPedidos[i][0]);
                    Log.d(TAG, "MatrizPedidos[i][1]: " + MatrizPedidos[i][1]);
                    try {
                        Log.d(TAG, "Seleccionado[0]: " + Seleccionado[0]);
                        Log.d(TAG, "MatrizPedidos[i][0]: " + MatrizPedidos[i][0]);
                        Log.d(TAG, "MatrizPedidos[i][1]: " + MatrizPedidos[i][1]);
                        if(Seleccionado[0].compareToIgnoreCase(MatrizPedidos[i][1])==0){
                            Log.d(TAG, "idPedidoFrecuente: " + MatrizPedidos[i][0]);
                            Log.d(TAG, "direccion: " + MatrizPedidos[i][1]);
                            Log.d(TAG, "latitud: " + MatrizPedidos[i][2]);
                            Log.d(TAG, "longitud: " + MatrizPedidos[i][3]);
                            Log.d(TAG, "referencia: " + MatrizPedidos[i][4]);
                            Log.d(TAG, "Codigo producto: " + MatrizPedidos[i][5]);
                            Log.d(TAG, "Codigo Capacidad: " + MatrizPedidos[i][6]);
                            Log.d(TAG, "Codigo tipo de balon: " + MatrizPedidos[i][7]);
                            Log.d(TAG, "Cantidad: " + MatrizPedidos[i][8]);
                            idPedidoFrecuente= MatrizPedidos[i][0];
                            direccion=MatrizPedidos[i][1];
                            latitudFre=MatrizPedidos[i][2];
                            longitudFre=MatrizPedidos[i][3];
                            referencia=MatrizPedidos[i][4];
                            nCodPro=MatrizPedidos[i][5];//Coigo Producto
                            nCodCap=MatrizPedidos[i][6];//Coigo Capacidad
                            nCodTipBal=MatrizPedidos[i][7];//Coigo tipo balon
                            cantidad=MatrizPedidos[i][8];//Coigo tipo balon
                            initMapa();
                        }
                    }catch (Exception e) {
                        Log.d(TAG, "eXDXD: " + e.toString());
                    }
                }
            }
        });
        builder.setNegativeButton("Cancel", null);
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void realizarPedido(){
        try{
            Log.d(TAG, "ENTRO REALIZAR PEDIDO :");
            LatLng center = googleMap.getCameraPosition().target;
            double longitudcamara=center.longitude;
            double latitudcamara=center.latitude;
            Location LocationSede = new Location("LocationSede");
            LocationSede.setLongitude(longitudcamara);
            LocationSede.setLatitude(latitudcamara);
            obtenerUbicacion();
            longitud=location.getLongitude();
            latitud=location.getLatitude();
            Location locationUsu = new Location("locationUsu");
            locationUsu.setLatitude(latitud);
            locationUsu.setLongitude(longitud);
            distance = (LocationSede.distanceTo(locationUsu)); //in metres
            try {
                direcciones = geocoder.getFromLocation(latitudcamara,longitudcamara,1);
            }catch(Exception e) {
                Toast.makeText(getActivity().getApplicationContext(), "No se pudo encontrar la dirección inténtelo nuevamente", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Error en geocoder:"+e.toString());
            }
            Location loc1 = new Location("Sede Petición");
            loc1.setLatitude(latitudcamara);
            loc1.setLongitude(longitudcamara);
            int sede=Utiles.validarSede(loc1);
            if(direcciones != null && direcciones.size() > 0 ){
                direccionMap = direcciones.get(0);
                //Log.d(TAG, "direcciones: " + direcciones.get(0));
                Log.d(TAG, "idPedidoFrecuente XDXD: " + idPedidoFrecuente);
                Log.d(TAG, "Longitude 2 XDXD: " + String.valueOf(longitudcamara));
                Log.d(TAG, "latitude 2 XDXD: " + String.valueOf(latitudcamara));
                Log.d(TAG, "distance XDXD: " + distance);
                Log.d(TAG, "direcciones XDXD: " + direcciones);
                Log.d(TAG, "Tamaño XDXD: " + direccionMap.getMaxAddressLineIndex());
                Log.d(TAG, "direccionMap XDXD: " + direccionMap.getAddressLine(0));
                Log.d(TAG, "sede XDXD :"+sede);
                Log.d(TAG, "nCodTipBal XDXD: " + nCodTipBal);
                Log.d(TAG, "nCodCap XDXD :" + nCodCap);
                Log.d(TAG, "nCodPro XDXD :"+nCodPro);
                Log.d(TAG, "cantidad XDXD :"+cantidad);

                try {
                    Fragment fragment = null;
                    Bundle bundle = new Bundle();
                    bundle.putString("longitud", String.valueOf(longitudcamara));
                    bundle.putString("latitud", String.valueOf(latitudcamara));
                    if (idPedidoFrecuente.compareToIgnoreCase("0") == 0) {
                        bundle.putString("direccion", String.format("%s", direccionMap.getAddressLine(0)));
                    } else {
                        bundle.putString("direccion", direccion);
                    }
                    bundle.putString("sede", String.valueOf(sede));
                    bundle.putString("referencia", referencia);
                    bundle.putString("nCodTipBal", nCodTipBal);
                    bundle.putString("nCodCap", nCodCap);
                    bundle.putString("nCodPro", nCodPro);
                    bundle.putString("cantidad", cantidad);

                    /*fragment = new DatosPedidosFragment();
                    fragment.setArguments(bundle);
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.frame, fragment, "Datos pedidos");
                    ft.addToBackStack(null);
                    Log.d(TAG, "9 :");
                    ft.commit();
                    Log.d(TAG, "10 :");*/
                }catch(Exception e) {
                    Log.d(TAG, "Error NUEVO :"+e.toString());
                    obtenerUbicacion();
                    //realizarPedido();
                }
            }else{
                Log.d(TAG, "ENTRO ELSE :");
                Fragment fragment = null;
                Bundle bundle = new Bundle();
                bundle.putString("longitud", String.valueOf(longitudcamara));
                bundle.putString("latitud", String.valueOf(latitudcamara));
                bundle.putString("direccion", String.format("%s", ""));
                bundle.putString("sede", String.valueOf(sede));
                bundle.putString("referencia",referencia);
                bundle.putString("cantidad", cantidad);
                /*fragment = new DatosPedidosFragment();
                fragment.setArguments(bundle);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.frame, fragment, "Datos pedidos");
                ft.addToBackStack(null);
                ft.commit();*/
            }
        }catch (Exception e){
            Log.d(TAG, "ERROR: "+e.toString());
            obtenerUbicacion();
            //realizarPedido();
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBuscarDireccion:
                buscarDireccion();
                break;
            case R.id.imgLlamar:
                llamar();
                break;
            case R.id.buttonFrecuente:
                obtenerPedidosFrecuentes();
                break;
            case R.id.buttonSiguiente:
                realizarPedido();
                break;



        }
    }
}

