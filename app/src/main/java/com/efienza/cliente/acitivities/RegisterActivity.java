package com.efienza.cliente.acitivities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.efienza.cliente.common.JSONParser;
import com.efienza.cliente.common.Utiles;
import com.efienza.cliente.custom.CheckConnection;
import com.efienza.cliente.custom.SetCustomFont;
import com.efienza.cliente.persistance.SesionHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
//import com.google.firebase.iid.FirebaseInstanceId;
//import com.google.firebase.iid.InstanceIdResult;
import com.efienza.cliente.R;
//import com.rapigas.apprapigas2.custom.CheckConnection;
import com.efienza.cliente.broadcastReceiver.Ubicacion;
import com.thebrownarrow.permissionhelper.ActivityManagePermission;
import com.thebrownarrow.permissionhelper.PermissionResult;
import com.thebrownarrow.permissionhelper.PermissionUtils;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.text.Normalizer;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created John Manchego Medina 06/10/2019
 */

public class RegisterActivity extends ActivityManagePermission implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    String permissionAsk[] = {
            PermissionUtils.Manifest_ACCESS_FINE_LOCATION,
            PermissionUtils.Manifest_ACCESS_COARSE_LOCATION};
    RelativeLayout relative_signin;
    TextInputEditText input_email, input_password, input_confirmPassword, input_mobile, input_name;
    AppCompatButton sign_up;
    SwipeRefreshLayout swipeRefreshLayout;
    private static final String TAG = "RegisterActivity";
    private String[][] MatrizSedes;
    double latitude;
    int flag = 0;
    float distance;
    Button _buttonRegistrarme, _buttonIngresar;
    TextView _editTextNombre, _editTextApellido,_editTextNumero,_editEmail, _editConstrasenha,_editConfirmaConstrasenha;
    EditText _editTextDNIRUC;
    String[] ArraySedes,product;
    String nomUsu, apePatUsu, direccion, telefono, email, login, contrasenha, identSede = "",latitudSede = "", longitudSede = "",passwordMD5,response1,sedes = "", codigos = "",editConstrasenha,editConfirmaConstrasenha;
    Double latitud,longitud;
    Address direccionMap;
    NotificationCompat.Builder mBuilder;
    SesionHelper usuarioHelper;
    NotificationManager mgr;
    SharedPreferences prefs;
    Intent intent;
    Spinner _sede,_spinnerTipoDocumento;
    Location location;
    View view;
    Ubicacion ub;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Double currentLatitude;
    private Double currentLongitude;
    private View rootView;
    String token="",identTipoDoc="0",tipoDoc="";
    String[] ArrayTipoDocumento;
    public String[][] MatrizTipoDocumento;
    ProgressDialog progressDialog;
    Geocoder geocoder;
    List<Address> direcciones = null;
    String NumDoc="";
    int flagCheck=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        validarGPS();
        BindView();
        leerSede();
        UDPClient();
        AskPermission();
        applyfonts();
        _spinnerTipoDocumento= (Spinner)findViewById(R.id.spinnerTipoDocumento);
        _editTextDNIRUC = (EditText) findViewById(R.id.editTextDNIRUC);
        //progressDialog = new ProgressDialog(RegisterActivity.this, R.style.AppTheme_Dark_Dialog);
        //progressDialog.setIndeterminate(true);progressDialog.setMessage("Ingresando...");progressDialog.show();
        //consultarTipoDocumento(_spinnerTipoDocumento,_editTextDNIRUC);
        //CheckBox _checkboxBoletaFactura = findViewById( R.id.checkboxBoletaFactura );
        identTipoDoc="-";
        NumDoc= _editTextNumero.getText().toString();
        /*_checkboxBoletaFactura.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if ( isChecked ) {
                    _spinnerTipoDocumento.setVisibility(View.VISIBLE);
                    _editTextDNIRUC.setVisibility(View.VISIBLE);
                    identTipoDoc="";
                    NumDoc= "";
                    tipoDoc="";
                    flagCheck=1;
                }else{
                    _spinnerTipoDocumento.setVisibility(View.GONE);
                    _editTextDNIRUC.setVisibility(View.GONE);
                    identTipoDoc="-";
                    NumDoc= _editTextNumero.getText().toString();
                    tipoDoc="NE";
                    flagCheck=0;
                }
            }
        });*/
        relative_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();

            }
        });
        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckConnection.haveNetworkConnection(getApplicationContext())) {
                    Log.d(TAG, "flagCheck: " + flagCheck);
                    if(flagCheck==0){
                        NumDoc= _editTextNumero.getText().toString();
                        tipoDoc = "NE";
                        identTipoDoc="-";
                        grabarCampos();

                    }else if(flagCheck==1){
                        NumDoc=_editTextDNIRUC.getText().toString();
                        if(identTipoDoc.compareToIgnoreCase("")!=0){
                            if(NumDoc.length() == 8 && identTipoDoc.compareToIgnoreCase("1")==0  || NumDoc.length() == 11 && identTipoDoc.compareToIgnoreCase("6")==0 || NumDoc.length() == 9 && identTipoDoc.compareToIgnoreCase("-")==0){
                                grabarCampos();
                            }else{
                                Toast.makeText(getApplicationContext(), "Ingrese numero de documento correcto", Toast.LENGTH_LONG).show();
                            }
                        }else{
                            Toast.makeText(getApplicationContext(), "Seleccione un tipo de documento", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });
    }
    public void applyfonts() {
        if (getCurrentFocus() != null) {
            SetCustomFont setCustomFont = new SetCustomFont();
            setCustomFont.overrideFonts(getApplicationContext(), getCurrentFocus());
        } else {
            Typeface font1 = Typeface.createFromAsset(getAssets(), "font/AvenirLTStd_Book.otf");
            _editConstrasenha.setTypeface(font1);
            _editConfirmaConstrasenha.setTypeface(font1);
        }
    }
    public void BindView() {
        relative_signin = (RelativeLayout) findViewById(R.id.relative_signin);
        sign_up = (AppCompatButton) findViewById(R.id.buttonRegistrarme);
        _editTextNombre =(TextInputEditText)findViewById(R.id.editTextNombre);
        _editTextApellido = (TextInputEditText) findViewById(R.id.editTextApellido);
        _editTextNumero = (TextInputEditText) findViewById(R.id.editTextNumero);
        _editEmail =(TextInputEditText)findViewById(R.id.editEmail);
        _editConstrasenha = (EditText) findViewById(R.id.editConstrasenha);
        _editConfirmaConstrasenha = (EditText) findViewById(R.id.editConfirmaConstrasenha);
        _sede =(Spinner) findViewById(R.id.spinnerSede);
        prefs = this.getSharedPreferences(Utiles.NOMBRE_PREFERENCIAS, Context.MODE_PRIVATE);
        mgr = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this);
        geocoder = new Geocoder(this);
        usuarioHelper = new SesionHelper(this);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
    String responseEC="";
    protected void grabarCampos() {
        try{
            nomUsu = _editTextNombre.getText().toString();
            apePatUsu = _editTextApellido.getText().toString();
            telefono = _editTextNumero.getText().toString();
            email = _editEmail.getText().toString().toUpperCase();
            editConstrasenha = _editConstrasenha.getText().toString();
            editConfirmaConstrasenha = _editConfirmaConstrasenha.getText().toString();
            obtenerDireccion();
            if(nomUsu.compareTo("") == 0){
                Toast.makeText(getApplicationContext(), "Ingrese nombre", Toast.LENGTH_LONG).show();
            }else if(apePatUsu.compareTo("") == 0){
                Toast.makeText(getApplicationContext(), "Ingrese Apellido", Toast.LENGTH_LONG).show();
            }else if(telefono.compareTo("") == 0){
                Toast.makeText(getApplicationContext(), "Ingrese telefono", Toast.LENGTH_LONG).show();
            }else if(email.compareTo("") == 0){
                Toast.makeText(getApplicationContext(), "Ingrese Email", Toast.LENGTH_LONG).show();
            }else if(telefono.length()!=9){
                Toast.makeText(getApplicationContext(), "Ingrese un numero válido", Toast.LENGTH_LONG).show();
            }else if(editConstrasenha.compareTo("") == 0){
                Toast.makeText(getApplicationContext(), "Ingrese una contraseña válida", Toast.LENGTH_LONG).show();
            }else if(editConfirmaConstrasenha.compareTo("") == 0){
                Toast.makeText(getApplicationContext(), "Valide la contraseña", Toast.LENGTH_LONG).show();
            }else if(editConstrasenha.compareTo(editConfirmaConstrasenha) != 0){
                Toast.makeText(getApplicationContext(), "Las contraseñas no coinciden", Toast.LENGTH_LONG).show();
            }else{
                if(identSede.compareTo("") == 0){
                    Toast.makeText(RegisterActivity.this, "Seleccione una sede y/o localidad.", Toast.LENGTH_LONG).show();
                }else{
                    if (sonEspacios(nomUsu)){
                        Toast.makeText(RegisterActivity.this, "Ingrese el nombre sin espacios en blanco.", Toast.LENGTH_LONG).show();
                        flag=1;
                    }else if(sonEspacios(apePatUsu)){
                        Toast.makeText(RegisterActivity.this, "Ingrese el apellido sin espacios en blanco.", Toast.LENGTH_LONG).show();
                        flag=1;
                    }else if(sonEspacios(email)){
                        Toast.makeText(RegisterActivity.this, "Ingrese el email sin espacios en blanco.", Toast.LENGTH_LONG).show();
                        flag=1;
                    }else{flag=0;}
                    contrasenha = editConstrasenha;
                    Log.d(TAG, "flag: " + flag);
                    Log.d(TAG, "nomUsu: " + nomUsu);
                    Log.d(TAG, "apePatUsu: " + apePatUsu);
                    Log.d(TAG, "direccion: " + direccion);
                    Log.d(TAG, "telefono: " + telefono);
                    Log.d(TAG, "email: " + email);
                    Log.d(TAG, "contrasenha: " + contrasenha);
                    Log.d(TAG, "identSede: " + identSede);
                    Log.d(TAG, "telefono.length(): " + telefono.length());
                    Log.d(TAG, "validarEmailSimple " + validarEmailSimple(email));
                    Log.d(TAG, "telefono.length(): " + telefono.length());
                    Log.d(TAG, "telefono.length(): " + telefono.length());
                    Log.d(TAG, "editConstrasenha " + editConstrasenha);
                    Log.d(TAG, "editConfirmaConstrasenha " + editConfirmaConstrasenha);
                    if(flag==0){
                        nomUsu = nomUsu.replace(" ", "_");
                        apePatUsu = apePatUsu.replace(" ", "_");
                        direccion = direccion.replace(" ", "_");
                        email = email.replace(" ", "");
                        contrasenha = contrasenha.replace(" ", "");
                        telefono = telefono.replace(" ", "");
                        try {
                            try {
                                Log.d(TAG, "Lat SedeXD: " + latitudSede);
                                Log.d(TAG, "Long SedeXD: " + longitudSede);
                                Log.d(TAG, "lat Usu XD: " + String.valueOf(location.getLongitude()));
                                Log.d(TAG, "Long Usu XD: " + String.valueOf(location.getLatitude()));
                                Location LocationSede = new Location("LocationSede");
                                LocationSede.setLongitude(Double.parseDouble(longitudSede));
                                LocationSede.setLatitude(Double.parseDouble(latitudSede));
                                Location locationUsu = new Location("locationUsu");
                                locationUsu.setLatitude(location.getLatitude());
                                locationUsu.setLongitude(location.getLongitude());
                                latitud=location.getLatitude();
                                longitud=location.getLongitude();
                                distance = (LocationSede.distanceTo(locationUsu)) / 1000; //in metres
                                Log.d(TAG, "distance: " + distance);
                            } catch (Exception e) {
                                Log.d(TAG, e.toString());
                            }
                            if (distance > 40) {
                                Toast.makeText(RegisterActivity.this, "Usted no puede crear un usuario que no sea de su ciudad.", Toast.LENGTH_LONG).show();
                            }else if(!validarEmailSimple(email)){
                                Toast.makeText(RegisterActivity.this, "Ingrese un correo válido.", Toast.LENGTH_LONG).show();
                            }else if(identSede.compareTo("") == 0){
                                Toast.makeText(RegisterActivity.this, "Seleccione una sede y/o localidad.", Toast.LENGTH_LONG).show();
                            }else if(nomUsu.compareTo("") == 0){
                                Toast.makeText(RegisterActivity.this, "Ingrese un nombre.", Toast.LENGTH_LONG).show();
                            }else if(apePatUsu.compareTo("")== 0){
                                Toast.makeText(RegisterActivity.this, "Ingrese un apellido.", Toast.LENGTH_LONG).show();
                            }else if(email.compareTo("") ==0){
                                Toast.makeText(RegisterActivity.this, "Ingrese un email.", Toast.LENGTH_LONG).show();
                            }else if(telefono.length()!=9){
                                Toast.makeText(RegisterActivity.this, "Numero Celular inválido.", Toast.LENGTH_LONG).show();
                                flag=1;
                            }else{
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                                alertDialogBuilder.setTitle("TERMINOS Y CONDICIONES DE USO DE LA APLICACIÓN");
                                alertDialogBuilder
                                        .setMessage(Utiles.mensajeTerminosCondiciones)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                if (nomUsu.compareTo("") != 0 && apePatUsu.compareTo("") != 0 && direccion.compareTo("") != 0 && telefono.compareTo("") != 0 && email.compareTo("") != 0 && contrasenha.compareTo("") != 0) {
                                                    if (identSede.compareTo("") != 0) {
                                                        new Thread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                Log.d(TAG, "CONTRASEÑA: " + nomUsu);
                                                                passwordMD5 = new String(Hex.encodeHex(DigestUtils.md5(contrasenha + "C4r0nt3")));
                                                                Log.d(TAG, Utiles.URL_SERVIDOR + "/" + "crearUsuarioClienteFinal" + "/" + nomUsu + "/" + apePatUsu + "/" + direccion + "/" + telefono + "/" + email + "/"+ passwordMD5   + "/" + identSede+ "/" + latitud+ "/" + longitud);
                                                                JSONParser jsonparser2 = new JSONParser();
                                                                int response = jsonparser2.getResponseStatus(Utiles.URL_SERVIDOR + "/" + "crearUsuarioClienteFinal" + "/" + nomUsu + "/" + apePatUsu + "/" + direccion + "/" + telefono + "/" + email + "/" + passwordMD5 + "/" + identSede+ "/" + latitud+ "/" + longitud);
                                                                Log.d(TAG, "Response:" + response);
                                                                try {
                                                                    if (response == Utiles.ERROR) {
                                                                        Log.d(TAG, "Response:" + response);
                                                                        runOnUiThread(new Runnable() {
                                                                            public void run() {
                                                                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegisterActivity.this);
                                                                                alertDialogBuilder.setTitle("");
                                                                                alertDialogBuilder.setMessage("El usuario y/o correo ya existen.")
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
                                                                    } else if (response == Utiles.CREATED) {
                                                                        runOnUiThread(new Runnable() {
                                                                            public void run() {
                                                                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegisterActivity.this);
                                                                                alertDialogBuilder.setTitle("");
                                                                                alertDialogBuilder.setMessage("Se creó el usuario correctamente.")
                                                                                    .setCancelable(false)
                                                                                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                                                                        public void onClick(DialogInterface dialog, int id) {
                                                                                            _editTextNombre.setText("");
                                                                                            _editTextApellido.setText("");
                                                                                            //_editTextDireccion.setText("");
                                                                                            _editTextNumero.setText("");
                                                                                            _editEmail.setText("");
                                                                                            _editConstrasenha.setText("");
                                                                                            _editConfirmaConstrasenha.setText("");
                                                                                            Log.d(TAG, "email: " + email);
                                                                                            Intent intent;
                                                                                            intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                                                                            startActivity(intent);
                                                                                            RegisterActivity.this.finish();
                                                                                            dialog.cancel();
                                                                                            /*if(responseEC.compareToIgnoreCase("true")==0){
                                                                                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegisterActivity.this);
                                                                                                alertDialogBuilder.setTitle("Se envió correo de validación");
                                                                                                alertDialogBuilder.setMessage("Porfavor ingrese a su correo y valide su cuenta.")
                                                                                                        .setCancelable(false)
                                                                                                        .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                                                                                            public void onClick(DialogInterface dialog, int id) {
                                                                                                                _editTextNombre.setText("");
                                                                                                                _editTextApellido.setText("");
                                                                                                                //_editTextDireccion.setText("");
                                                                                                                _editTextNumero.setText("");
                                                                                                                _editEmail.setText("");
                                                                                                                Log.d(TAG, "email: " + email);
                                                                                                                Intent intent;
                                                                                                                intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                                                                                                startActivity(intent);
                                                                                                                RegisterActivity.this.finish();
                                                                                                                dialog.cancel();
                                                                                                            }
                                                                                                        });
                                                                                                AlertDialog alertDialog = alertDialogBuilder.create();
                                                                                                alertDialog.show();

                                                                                            }else{
                                                                                                Log.d(TAG, "NO Envió correo");
                                                                                            }*/
                                                                                            dialog.cancel();
                                                                                        }
                                                                                    });
                                                                                AlertDialog alertDialog = alertDialogBuilder.create();
                                                                                alertDialog.show();
                                                                            }
                                                                        });
                                                                    } else if (response == Utiles.NO) {
                                                                        runOnUiThread(new Runnable() {
                                                                            public void run() {
                                                                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegisterActivity.this);
                                                                                alertDialogBuilder.setTitle("");
                                                                                alertDialogBuilder.setMessage("Error al guardar ")
                                                                                        .setCancelable(false)
                                                                                        .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                                                                            public void onClick(DialogInterface dialog, int id) {
                                                                                                dialog.cancel();
                                                                                                _editTextNombre.setText("");
                                                                                                _editTextApellido.setText("");
                                                                                                //_editTextDireccion.setText("");
                                                                                                _editTextNumero.setText("");
                                                                                                _editEmail.setText("");
                                                                                                _editConstrasenha.setText("");
                                                                                                _editConfirmaConstrasenha.setText("");
                                                                                                Intent intent;
                                                                                                intent = new Intent(RegisterActivity.this, RegisterActivity.class);
                                                                                                startActivity(intent);
                                                                                                RegisterActivity.this.finish();
                                                                                            }
                                                                                        });
                                                                                AlertDialog alertDialog = alertDialogBuilder.create();
                                                                                alertDialog.show();
                                                                            }
                                                                        });
                                                                    }
                                                                } catch (Exception e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        }).start();
                                                    } else {
                                                        Toast.makeText(RegisterActivity.this, "Seleccione una sede.", Toast.LENGTH_LONG).show();
                                                    }
                                                } else {
                                                    Toast.makeText(RegisterActivity.this, "Ingrese todos los datos.", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        })
                                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        });
                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();
                            }
                        } catch (NumberFormatException nf) {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegisterActivity.this);
                                    alertDialogBuilder.setTitle("Error al Conectar al Servidor 2");
                                    alertDialogBuilder.setMessage("Hubo un error al conectar al Servidor, revise que este conectado a internet y vuelva a intentar en unos minutos.")
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
                        }
                    }
                }
            }
        }catch (Exception e){
            Log.d(TAG, "Exception crear Usuario :"  + e.toString());
        }
    }
    private void obtenerDireccion(){
        //DETERMINAR LA DIRECCION
        try {
            direcciones = geocoder.getFromLocation(currentLatitude,currentLongitude,1);
        }catch(Exception e) {
            Toast.makeText(this.getApplicationContext(), "No se pudo encontrar la dirección inténtelo nuevamente", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Error en geocoder:"+e.toString());
        }
        if(direcciones != null && direcciones.size() > 0 ) {
            direccionMap = direcciones.get(0);
            Log.d(TAG, "direcciones XDXD: " + direcciones);
            Log.d(TAG, "Tamaño XDXD: " + direccionMap.getMaxAddressLineIndex());
            Log.d(TAG, "direccionMap XDXD: " + direccionMap.getAddressLine(0));
            direccion=direccionMap.getAddressLine(0);
        }else{
            direccion="";
            runOnUiThread(new Runnable() {
                public void run() {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegisterActivity.this);
                    alertDialogBuilder.setTitle("Error");
                    alertDialogBuilder.setMessage("No se puedo encontrar la dirección")
                            .setCancelable(false)
                            .setNegativeButton("OK",new DialogInterface.OnClickListener() {public void onClick(DialogInterface dialog, int id) {dialog.cancel();}});
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            });
        }
        direccion=quitarAcentos(direccion).toUpperCase();
    }
    public String quitarAcentos(String original){
        String cadenaNormalize = Normalizer.normalize(original, Normalizer.Form.NFD);
        String cadenaSinAcentos = cadenaNormalize.replaceAll("[^\\p{ASCII}]", "");
        return cadenaSinAcentos;
    }
    private void validarGPS() {
        if (Utiles.validarGPS(this) == 1) {
            determinarUbicacion();
        } else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("GPS Desactivado");
            alertDialogBuilder.setMessage("Active su GPS para un correcto funcionamiento.")
                    .setCancelable(false)
                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            finish();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }
    public void determinarUbicacion() {
        try {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
            latitude = location.getLatitude();
        }catch(Exception e){
            Log.d(TAG,"Exception 1 "+ e.toString());
            try {
                ub = new Ubicacion(this);
                location = ub.getLocation(this);
                latitude = location.getLatitude();
                Log.d(TAG,"latitude: "+ latitude);
            }catch (Exception ee){
                Log.d(TAG,"Exception 2 "+ ee.toString());

            }
        }
    }
    public void UDPClient(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        try {
            Utiles.DGSocket = new DatagramSocket(4500);
            Utiles.DGSocket.setReuseAddress(true);
            Utiles.DGSocket.setSoTimeout(8000);
        } catch (SocketException e) {
            Log.i(TAG, "Error Socket: " + e.toString());
        }
        try {
            if (prefs.getString(Utiles.PREFERENCIAS_PERFIL, "Error").compareToIgnoreCase("16") == 1) {
                Log.e(TAG, "ENTRO: UDPClient1");
                new UDPClient1().execute();
            }
        } catch (Exception e) {
            Log.i(TAG, "UDPCLIENT ERRORt: " + e.toString());
        }
    }
    public class UDPClient1 extends AsyncTask<Void, Void, Void> {
        private static final String TAG = "UDPCLIENTE";
        String result;
        @Override
        protected Void doInBackground(Void... params) {
            byte[] mensaje = new byte[4];
            DatagramPacket dPacket = new DatagramPacket(mensaje, mensaje.length);
            while (true) {
                try {
                    Utiles.DGSocket.receive(dPacket);
                    result = new String(dPacket.getData());
                    result = result.substring(0, 2);
                    Log.e(TAG, result);
                    /*if (result.compareToIgnoreCase("69") == 0) {
                        Log.e(TAG, "ENTRO: " + result);
                        mBuilder.setContentTitle("DIGAS")
                                .setContentText("EL RESPARTIDOR ESTA AFUERA")
                                .setSmallIcon(R.drawable.ic_launcher)
                                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                                .setLights(Color.RED, 3000, 3000)
                                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                                .setOngoing(false);
                        mgr.notify(0, mBuilder.build());
                    }*/
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            }
        }
    }
    private void leerSede(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                sincronizarSedes();
                runOnUiThread(new Runnable() {
                    public void run() {
                        mostrarSedes();
                    }
                });
            }
        }).start();
    }
    private void sincronizarSedes(){
        try {
            JSONParser jsonparser1 = new JSONParser();
            response1 = jsonparser1.getResponse(Utiles.URL_SERVIDOR + "/" + "listarSedes");
            Log.d(TAG, "URL: " + Utiles.URL_SERVIDOR + "/" + "listarSedes");
            Log.d(TAG, "response1: " + response1);
            Log.d(TAG, response1);
        } catch (Exception e) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegisterActivity.this);
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
            ArraySedes= new String[jsonArray1.length()+1];
            MatrizSedes=new String[jsonArray1.length()][4];
            ArraySedes[0]="Localidad";
            for (int i = 0; i < jsonArray1.length(); i++) {
                JSONObject jsonObject = jsonArray1.getJSONObject(i);
                codigos=jsonObject.getString("nCodSede");
                sedes=jsonObject.getString("pedido");
                product=sedes.split("\\|");
                MatrizSedes[i][0]=codigos;
                MatrizSedes[i][1]=product[0];
                MatrizSedes[i][2]=product[1];
                MatrizSedes[i][3]=product[2];
                ArraySedes[i+1]= product[0];
                Log.d(TAG, "codigos: " + codigos);
                Log.d(TAG, "sedes: " + product[0]);
                Log.d(TAG, "Lat: " + product[1]);
                Log.d(TAG, "Long: " + product[2]);
            }
        } catch (JSONException e) {
            Log.d(TAG,e.toString());
            runOnUiThread(new Runnable() {
                public void run() {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegisterActivity.this);
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
    private void mostrarSedes(){
        try{
            ArrayAdapter adapterSedes = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, ArraySedes);
            _sede.setAdapter(adapterSedes);
            view = this.getCurrentFocus();
            _sede.setOnTouchListener(spinnerOnTouch);
            _sede.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    //InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    //imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    // your code here
                    Log.d(TAG, "Tipo Position : " + _sede.getItemIdAtPosition(position));
                    Log.d(TAG, "Tipo Item : " + _sede.getSelectedItem());
                    Log.d(TAG, "MatrizSedes.length : " + MatrizSedes.length);
                    try{
                        if(_sede.getItemIdAtPosition(position)==0){
                            identSede = "";
                        }
                        for (int i = 0; i < MatrizSedes.length; i++) {
                            if (MatrizSedes[i][1].compareToIgnoreCase(_sede.getSelectedItem().toString()) == 0) {
                                identSede = "";
                                //if (MatrizSedes[i][1].equals(_sede.getSelectedItem().toString())) {
                                //if (MatrizSedes[i][1].toString().compareTo(_sede.getSelectedItem().toString())==0) {
                                Log.d(TAG, "ID : " + MatrizSedes[i][0]);
                                Log.d(TAG, "Dato : " + MatrizSedes[i][1]);
                                Log.d(TAG, "LAT : " + MatrizSedes[i][2]);
                                Log.d(TAG, "LONG : " + MatrizSedes[i][3]);
                                identSede = MatrizSedes[i][0];
                                latitudSede = MatrizSedes[i][2];
                                longitudSede = MatrizSedes[i][3];
                                if(identSede.compareToIgnoreCase("0")==0){
                                    identSede = "";
                                }
                            } else if (MatrizSedes[i][1].compareToIgnoreCase("Localidad") == 0) {
                                identSede = "";
                                //Log.d(TAG, "ENTRO A SEDE");
                            }
                        }
                        Log.d(TAG, "Dato : " + identSede);
                    }catch (Exception e){
                        Log.d(TAG, "Error al pulsar interno: " +  e.toString());
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }
            });
        }catch (Exception e){
            Log.d(TAG, "Error al pulsar : " +  e.toString());
        }

    }
    public boolean sonEspacios(String cad) {
        for(int i =0; i<cad.length(); i++){
            Log.d(TAG, "char: " + cad.charAt(i));
            if(cad.charAt(i) == ' ')
                return true;
        }
        return false;
    }
    public static boolean validarEmailSimple(String email){
        String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    private View.OnTouchListener spinnerOnTouch = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                Log.d(TAG, "Entro pspspsps");
                view.clearFocus();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            return false;
        }
    };
    @Override
    public void onConnected(@Nullable Bundle bundle) {

        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            if (location == null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

            } else {
                //If everything went fine lets get latitude and longitude
                currentLatitude = location.getLatitude();
                currentLongitude = location.getLongitude();

                //Toast.makeText(getActivity(), currentLatitude + " WORKS " + currentLongitude + "", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {

        }
    }
    @Override
    public void onConnectionSuspended(int i) {
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
        }

    }
    @Override
    public void onLocationChanged(Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();
    }
    public void getCurrentlOcation() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                // The next two lines tell the new client that “this” current class will handle connection stuff
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                //fourth line adds the LocationServices API endpoint from GooglePlayServices
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(30 * 1000);
        mLocationRequest.setFastestInterval(5 * 1000);
    }
    public void tunonGps() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API).addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
            mGoogleApiClient.connect();
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(30 * 1000);
            mLocationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(mLocationRequest);

            // **************************
            builder.setAlwaysShow(true); // this is the key ingredient
            // **************************

            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                    .checkLocationSettings(mGoogleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result
                            .getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            // All location settings are satisfied. The client can
                            // initialize location
                            // requests here.
                            getCurrentlOcation();
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be
                            // fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling
                                // startResolutionForResult(),
                                // and setting the result in onActivityResult().
                                status.startResolutionForResult(RegisterActivity.this, 1000);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have
                            // no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            });
        }

    }
    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                mGoogleApiClient.disconnect();
            }
        }
    }
    @Override
    public void onDestroy() { super.onDestroy(); }
    @Override
    public void onSaveInstanceState(Bundle outState) { super.onSaveInstanceState(outState); }
    @Override
    public void onLowMemory() { super.onLowMemory(); }
    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {

                getCurrentlOcation();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }
    public void AskPermission() {
        askCompactPermissions(permissionAsk, new PermissionResult() {
            @Override
            public void permissionGranted() {
                if (!GPSEnable()) {
                    tunonGps();
                } else {
                    getCurrentlOcation();
                }
            }
            @Override
            public void permissionDenied() {
            }
            @Override
            public void permissionForeverDenied() {
                //  openSettingsApp(getApplicationContext());
            }
        });
    }
    public Boolean GPSEnable() {
        LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return true;
        }
        return false;
    }

}
