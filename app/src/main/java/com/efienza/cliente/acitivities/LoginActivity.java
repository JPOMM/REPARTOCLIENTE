package com.efienza.cliente.acitivities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.efienza.cliente.model.UsuarioVO;
import com.efienza.cliente.persistance.SesionHelper;
import com.efienza.cliente.session.SessionManager;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.iid.FirebaseInstanceId;
//import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.efienza.cliente.R;
import com.efienza.cliente.broadcastReceiver.Ubicacion;
import com.efienza.cliente.common.JSONParser;
import com.efienza.cliente.common.Utiles;
//import CheckConnection;
import com.efienza.cliente.custom.CheckConnection;
import com.efienza.cliente.custom.SetCustomFont;
import com.efienza.cliente.pojo.User;
import com.thebrownarrow.permissionhelper.ActivityManagePermission;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.Normalizer;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends ActivityManagePermission {
    private static final String TAG = "LoginActiviy";
    //FirebaseUser currentUser;
    RelativeLayout relative_register;
    EditText input_email, input_password;
    AppCompatButton login;
    TextView as, txt_createaccount, forgot_password;
    private String token;
    Context context;
    SwipeRefreshLayout swipeRefreshLayout;
    String passwordMD5="",perfil="",Flag="",direccion="";
    int codigo_login;
    SharedPreferences prefs;
    AlertDialog.Builder dialogo;
    ProgressDialog  progressDialog;
    Geocoder geocoder;
    List<Address> direcciones = null;
    Location location;
    Ubicacion ub;
    double latitude;
    double longitude;
    Address direccionMap;
    CheckBox _sesionActiva;
    SesionHelper _usuarioHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        Log.i(TAG,"ENTRO LOGIN: ");
        bindView();
        applyfonts();
        determinarUbicacion();
        prefs = LoginActivity.this.getSharedPreferences(Utiles.NOMBRE_PREFERENCIAS, Context.MODE_PRIVATE);
        relative_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(LoginActivity.this, R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);progressDialog.setMessage("Ingresando...");progressDialog.show();
                if (validate()) {
                    if (CheckConnection.haveNetworkConnection(getApplicationContext())) {
                        Log.d(TAG,"ENTRO OBTENER CILINDROS");
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        try{
                                            Log.d(TAG,"input_email.getText().toString().trim(): "+ input_email.getText().toString().trim());
                                            Log.d(TAG,"input_password.getText().toString().trim(): "+ input_password.getText().toString().trim());
                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if(input_email.getText().toString().compareTo("")!=0 && input_password.getText().toString().compareTo("")!=0){
                                                        login(input_email.getText().toString().trim(), input_password.getText().toString().trim());
                                                    }else{
                                                        Log.d(TAG,"Ingrese datos: ");
                                                        progressDialog.dismiss();
                                                    }
                                                }
                                            }).start();
                                            progressDialog.dismiss();
                                        }catch (Exception e){
                                            Log.d(TAG,"Error: "+ e.toString());
                                            progressDialog.dismiss();
                                        }
                                    }
                                }, 2000);
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.network), Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                } else {
                    // do nothing
                }
            }
        });
    }
    public void determinarUbicacion() {
        try {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                // public void onRequestPermissionsResult(int requestCode, String[] permissions,int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            Log.d(TAG,"latitude "+ latitude);
            Log.d(TAG,"longitude "+ longitude);
        }catch(Exception e){
            Log.d(TAG,"Exception 1 "+ e.toString());
            try {
                ub = new Ubicacion(this);
                location = ub.getLocation(this);
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                Log.d(TAG,"latitude: "+ latitude);
            }catch (Exception ee){
                Log.d(TAG,"Exception 2 "+ ee.toString());

            }
        }
    }
    public String quitarAcentos(String original){
        String cadenaNormalize = Normalizer.normalize(original, Normalizer.Form.NFD);
        String cadenaSinAcentos = cadenaNormalize.replaceAll("[^\\p{ASCII}]", "");
        return cadenaSinAcentos;
    }
    private void obtenerDireccion(){
        //DETERMINAR LA DIRECCION
        Log.d(TAG,"latitude XD "+ latitude);
        Log.d(TAG,"longitude XD "+ longitude);
        try {
            direcciones = geocoder.getFromLocation(latitude,longitude,1);
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
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
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
    String identTipoDoc ;
    String NumDoc;
    public void ingresarClienteERP(){
        obtenerDireccion();
        // Patrón para validar el email
        Pattern pattern = Pattern
                .compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

        // El email a validar
        String email =  prefs.getString(Utiles.PREFERENCIAS_EMAIL, "Error");
        String nombre =  prefs.getString(Utiles.PREFERENCIAS_NOMBRE_APELLIDO, "Error");
        String tipoDoc =  prefs.getString(Utiles.PREFERENCIAS_TIPDOCERP, "Error");
        identTipoDoc =  prefs.getString(Utiles.PREFERENCIAS_TIPDOCERP, "Error");
        NumDoc =  prefs.getString(Utiles.PREFERENCIAS_NRODOCERP, "Error");
        String Telefono =  prefs.getString(Utiles.PREFERENCIAS_TELEFONO, "Error");
        String identSede =  prefs.getString(Utiles.PREFERENCIAS_SEDE, "Error");
        Matcher mather = pattern.matcher(email);

        Log.i(TAG,"editTextNombre: "+nombre);
        Log.i(TAG,"tipoDoc: "+tipoDoc);
        Log.i(TAG,"identTipoDoc: "+identTipoDoc);
        Log.i(TAG,"NumDoc: "+NumDoc);
        Log.i(TAG,"TAMAÑO editTextDNIRUC: "+NumDoc.length());
        Log.i(TAG,"direccion: "+direccion);
        Log.i(TAG,"_editEmail: "+email);
        Log.i(TAG," telefono: "+ Telefono);
        Log.i(TAG," Tamaño telefono: "+ Telefono.length());

        if(identTipoDoc.compareTo("0")==0 && NumDoc.compareTo("0")==0){
            identTipoDoc="-";
            NumDoc=Telefono;
        }
        if(identSede.compareTo("") == 0){
            Toast.makeText(LoginActivity.this, "Seleccione una sede y/o localidad.", Toast.LENGTH_LONG).show();
        }else{
            if(identTipoDoc.compareToIgnoreCase("")!=0){
                if(NumDoc.length() == 8 && identTipoDoc.compareToIgnoreCase("1")==0  || NumDoc.length() == 11 && identTipoDoc.compareToIgnoreCase("6")==0 || NumDoc.length() == 9 && identTipoDoc.compareToIgnoreCase("-")==0){
                    if(NumDoc.compareTo("") == 0){
                        Toast.makeText(getApplicationContext(), "Ingrese numero de documento", Toast.LENGTH_LONG).show();
                    }else if(nombre.compareTo("") == 0){
                        Toast.makeText(getApplicationContext(), "Ingrese nombre", Toast.LENGTH_LONG).show();
                    }else if(Telefono.compareTo("") == 0){
                        Toast.makeText(getApplicationContext(), "Ingrese telefono", Toast.LENGTH_LONG).show();
                    }else if(email.compareTo("") == 0){
                        Toast.makeText(getApplicationContext(), "Ingrese Email", Toast.LENGTH_LONG).show();
                    }else if(Telefono.length()!=9){
                        Toast.makeText(getApplicationContext(), "Ingrese un numero válido", Toast.LENGTH_LONG).show();
                    } else{
                        if (mather.find() == true) { } else {
                            Toast.makeText(getApplicationContext(), "Ingrese un email válido", Toast.LENGTH_LONG).show();
                        }
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    JSONObject objFactcab = new JSONObject();
                                    objFactcab.put("Errores", null);      //VALIDADO IGUAL
                                    objFactcab.put("EstadoPersistencia", 1); //VALIDADO IGUAL
                                    objFactcab.put("ListaErrores", null); //VALIDADO IGUAL
                                    objFactcab.put("CELULAR",Telefono);
                                    objFactcab.put("CREDITO", true ); //VALIDADO IGUAL
                                    objFactcab.put("DIRECCION",direccion);
                                    objFactcab.put("DIRECCIONES", new String()); //VALIDADO IGUAL
                                    objFactcab.put("EMAIL1", email.toUpperCase());
                                    objFactcab.put("EMAIL2", "");//VALIDADO IGUAL
                                    objFactcab.put("FAX", "");//VALIDADO IGUAL
                                    objFactcab.put("IDCLIENTE", 0);
                                    objFactcab.put("IDTIPOCLIENTE",1 );   //CLIENTE FINAL : CODIGO 1
                                    objFactcab.put("IDTIPODOCUMENTOIDENTIDAD", identTipoDoc );
                                    objFactcab.put("LIMITECREDITO", 500);
                                    objFactcab.put("NOMBRECLIENTE",nombre);
                                    objFactcab.put("NOMBRECOMERCIAL",  "");
                                    objFactcab.put("NOMBRECOMPUESTO", nombre +"["+NumDoc+"]" );
                                    objFactcab.put("NOMBRETIPOCLIENTE",  "CLIENTE FINAL");
                                    objFactcab.put("NOMBRETIPODOCUMENTOIDENTIDAD", tipoDoc);
                                    objFactcab.put("NUMERODOCUMENTOIDENTIDAD", NumDoc);
                                    objFactcab.put("PAGINAWEB","" );
                                    objFactcab.put("PEDIDOS", new String());
                                    objFactcab.put("SALDOINICIAL", 0);
                                    objFactcab.put("TELEFONO1",Telefono );
                                    objFactcab.put("TELEFONO2", ""); //VALIDADO IGUAL
                                    Log.d(TAG,"objFactcabRA "+ objFactcab);

                                    JSONParser jsonparserCC = new JSONParser();
                                    String responseCC = jsonparserCC.postResponseExterno(Utiles.SERVIDORPRUEBAS+"/GrabarCliente",objFactcab.toString());
                                    Log.d(TAG, Utiles.SERVIDORPRUEBAS+"/GrabarCliente");
                                    Log.d(TAG, "responseCC: "+responseCC);
                                    Log.d(TAG, "responseCC: "+responseCC.length());
                                    Log.d(TAG, "responseCC.indexOf(\"Exito\"): "+responseCC.indexOf("Exito"));
                                    Log.d(TAG, "responseCC.substring(): "+responseCC.substring(responseCC.indexOf("Exito")-1,85+13));
                                    String resultado=responseCC.substring(responseCC.indexOf("Exito")-1,85+13);
                                    Log.d(TAG, "resultado.indexOf(\"true\"): "+resultado.contains("true"));
                                    Log.d(TAG, "resultado.indexOf(\"false\"): "+resultado.contains("false"));
                                    if(responseCC.length()==0){
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
                                                alertDialogBuilder.setTitle("Error");
                                                alertDialogBuilder.setMessage("Error al crear el cliente.")
                                                        .setCancelable(false)
                                                        .setNegativeButton("OK",new DialogInterface.OnClickListener() {public void onClick(DialogInterface dialog, int id) {dialog.cancel();}});
                                                AlertDialog alertDialog = alertDialogBuilder.create();
                                                alertDialog.show();
                                            }
                                        });
                                    }else{
                                        //
                                        if(responseCC.substring(0,6).compareToIgnoreCase("<html>")==0){
                                            runOnUiThread(new Runnable() {
                                                public void run() {
                                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
                                                    alertDialogBuilder.setTitle("Error");
                                                    alertDialogBuilder.setMessage("Ocurrio un error intentelo nuevamente porfavor")
                                                            .setCancelable(false)
                                                            .setNegativeButton("OK",new DialogInterface.OnClickListener() {public void onClick(DialogInterface dialog, int id) {dialog.cancel();}});
                                                    AlertDialog alertDialog = alertDialogBuilder.create();
                                                    alertDialog.show();
                                                }
                                            });
                                        }else{
                                            if(resultado.contains("true")==true){
                                                runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
                                                        alertDialogBuilder.setTitle("Exito ");
                                                        alertDialogBuilder.setMessage("Se actualizó el cliente adecuadamente")
                                                                .setCancelable(false)
                                                                .setNegativeButton("OK",new DialogInterface.OnClickListener() {public void onClick(DialogInterface dialog, int id) {dialog.cancel();}});
                                                        AlertDialog alertDialog = alertDialogBuilder.create();
                                                        alertDialog.show();
                                                    }
                                                });
                                            }else{
                                                Log.d(TAG, "CLIENTE YA EXISTE EN LA BASE DE DATOS DE CLIENTES");
                                            }
                                        }
                                    }
                                }catch (Exception e){
                                    Log.d(TAG,"eeee"+ e.toString());
                                }
                            }
                        }).start();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Ingrese datos completos", Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(getApplicationContext(), "Seleccione un tipo de documento", Toast.LENGTH_LONG).show();
            }
        }
    }
    public void bindView() {
        context = LoginActivity.this;
        //as = (TextView) findViewById(R.id.as);
        forgot_password = (TextView) findViewById(R.id.txt_forgotpassword);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        txt_createaccount = (TextView) findViewById(R.id.txt_createaccount);
        input_email = (EditText) findViewById(R.id.input_email);
        input_password = (EditText) findViewById(R.id.input_password);
        relative_register = (RelativeLayout) findViewById(R.id.relative_register);
        login = (AppCompatButton) findViewById(R.id.login);
        dialogo = new AlertDialog.Builder(this);
        geocoder = new Geocoder(this);
        _sesionActiva=findViewById(R.id.checkbox_session);
        _usuarioHelper=new SesionHelper(this);
        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckConnection.haveNetworkConnection(getApplicationContext())) {
                    changepassword_dialog();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.network), Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        verificarRecordarSession();
    }
    public void verificarRecordarSession(){
        List<UsuarioVO> listaMarca = _usuarioHelper.obtenerFlagSession();
        if(listaMarca.size()>0){
            int flag=listaMarca.get(0).getFlag();
            if(flag==1){
                _sesionActiva.setChecked(true);
                input_email.setText(listaMarca.get(0).getLogin());
                input_password.setText(listaMarca.get(0).getContrasenha());
            }else{
                _sesionActiva.setChecked(false);
            }
        }
    }
    public Boolean validate() {
        Boolean value = true;
        if (input_email.getText().toString().equals("") && !android.util.Patterns.EMAIL_ADDRESS.matcher(input_email.getText().toString().trim()).matches()) {
            value = false;
            input_email.setError(getString(R.string.email_invalid));
            progressDialog.dismiss();
        } else {
            input_email.setError(null);
        }
        if (input_password.getText().toString().trim().equalsIgnoreCase("")) {
            value = false;
            input_password.setError(getString(R.string.fiels_is_required));
            progressDialog.dismiss();
        } else {
            input_password.setError(null);
        }
        return value;
    }
    public void applyfonts() {
        if (getCurrentFocus() != null) {
            SetCustomFont setCustomFont = new SetCustomFont();
            setCustomFont.overrideFonts(getApplicationContext(), getCurrentFocus());
        } else {
            Typeface font = Typeface.createFromAsset(getAssets(), "font/AvenirLTStd_Medium.otf");
            Typeface font1 = Typeface.createFromAsset(getAssets(), "font/AvenirLTStd_Book.otf");
            input_email.setTypeface(font1);
            input_password.setTypeface(font1);
            login.setTypeface(font);
            txt_createaccount.setTypeface(font);
            forgot_password.setTypeface(font);
        }
    }
    String user_id="",name="",emailo="",telefono="";
    String _usuario,_contrasenha;
    public void login(String email, String password) {
        RequestParams params = new RequestParams();
        params.put("email", email);
        params.put("password", password);
        params.put("utype", "0");
        params.put("gcm_token", token);
        try {
            JSONParser jsonparser = new JSONParser();
            passwordMD5 = new String(Hex.encodeHex(DigestUtils.md5(password.toUpperCase() + "C4r0nt3")));
            Log.i(TAG, Utiles.URL_LOGIN+ "/"+ email.toUpperCase() + "/"+ passwordMD5+ "/" + Utiles.CODCLIENTE);
            String result = jsonparser.getCookie(Utiles.URL_LOGIN + "/" + email.toUpperCase() + "/" + passwordMD5+ "/"+ Utiles.CODCLIENTE);
            Log.i(TAG,"RESULT: "+ result);
            String[] idsesion = result.split(":");
            String[] idsesion3 = idsesion[1].split("=");
            codigo_login = Integer.parseInt(idsesion[2]);
            Log.i(TAG,"RESULT: "+ codigo_login);
            _usuario=email.toUpperCase();
            _contrasenha=password.toUpperCase();
            if (codigo_login == Utiles.OK) {
                Log.i(TAG, "ENTRO 200");
                Utiles.COOKIE = "" + idsesion3[1];
                try{
                    Log.i(TAG, "GPS: " + String.valueOf(Utiles.validarGPS(this)));
                    if(Utiles.validarGPS(this)==1){
                        Log.i(TAG, "GPS ACTIVO");
                        JSONParser jsonparser2 = new JSONParser();
                        Log.d(TAG, Utiles.URL_SERVIDOR + "/" + "datosUsuario");
                        String response = jsonparser2.getResponse(Utiles.URL_SERVIDOR + "/" + "datosUsuario", Utiles.COOKIE);
                        Log.d(TAG, "RESPONSE: " + response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            perfil=jsonObject.getString("ncodprf");
                            user_id=jsonObject.getString("ncodusu");
                            name=jsonObject.getString("cnomusu")+" "+ jsonObject.getString("capepatusu");
                            emailo=jsonObject.getString("cemailusu");
                            telefono=jsonObject.getString("ctelusu");
                            if(telefono.compareToIgnoreCase("")==1){
                                telefono="0";
                            }
                            prefs = LoginActivity.this.getSharedPreferences(Utiles.NOMBRE_PREFERENCIAS, Context.MODE_PRIVATE);
                            prefs.edit().putInt(Utiles.SHARED_PREFERENCES_INICIADO, Utiles.OK).apply();
                            prefs.edit().putString(Utiles.PREFERENCIAS_ID_USUARIO, user_id).apply();
                            prefs.edit().putString(Utiles.PREFERENCIAS_NOMBRE, jsonObject.getString("cnomusu")).apply();
                            prefs.edit().putString(Utiles.PREFERENCIAS_APELLIDO_PATERNO, jsonObject.getString("capepatusu")).apply();
                            prefs.edit().putString(Utiles.PREFERENCIAS_APELLIDO_MATERNO, jsonObject.getString("capematusu")).apply();
                            prefs.edit().putString(Utiles.PREFERENCIAS_NOMBRE_APELLIDO, jsonObject.getString("cnomusu")+" "+jsonObject.getString("capepatusu")).apply();
                            prefs.edit().putString(Utiles.PREFERENCIAS_DIRECCION, jsonObject.getString("cdirusu")).apply();
                            prefs.edit().putString(Utiles.PREFERENCIAS_TELEFONO, telefono).apply();
                            prefs.edit().putString(Utiles.PREFERENCIAS_EMAIL, emailo).apply();
                            prefs.edit().putString(Utiles.PREFERENCIAS_PERFIL, perfil).apply();
                            prefs.edit().putString(Utiles.PREFERENCIAS_CODUNI, jsonObject.getString("ncoduni")).apply();
                            prefs.edit().putString(Utiles.PREFERENCIAS_SEDE, jsonObject.getString("ncodsede")).apply();
                            prefs.edit().putString(Utiles.PREFERENCIAS_LATSEDE, jsonObject.getString("clatsede")).apply();
                            prefs.edit().putString(Utiles.PREFERENCIAS_LONGSEDE, jsonObject.getString("clonsede")).apply();
                            prefs.edit().putString(Utiles.PREFERENCIAS_LOGIN, jsonObject.getString("cLoginUsu")).apply();
                            //prefs.edit().putString(Utiles.PREFERENCIAS_FLAGPROM, jsonObject.getString("nFlagProm")).apply();
                            //prefs.edit().putString(Utiles.PREFERENCIAS_IDALAMCEN, jsonObject.getString("idAlmacen")).apply();
                            //prefs.edit().putString(Utiles.PREFERENCIAS_CODCLIERP, jsonObject.getString("nCodCliErp")).apply();
                            //prefs.edit().putString(Utiles.PREFERENCIAS_NRODOCERP, jsonObject.getString("cNroDocErp")).apply();
                            //prefs.edit().putString(Utiles.PREFERENCIAS_TIPDOCERP, jsonObject.getString("cTipDoc")).apply();
                            ingresarDatosSession();
                        } catch (JSONException e) {
                            Log.i(TAG, e.toString());
                        } catch (Exception e) {
                            Log.i(TAG, e.toString());
                        }
                        Log.d(TAG, "perfil: " + perfil);
                        if(perfil.compareTo("")!=0){
                            try {
                                if(perfil.compareTo("25")==0) {
                                    this.runOnUiThread(new Runnable() {
                                        public void run() {
                                            try {
                                                //ingresarClienteERP();
                                                JSONObject obj = new JSONObject();
                                                obj.put("user_id", user_id);
                                                obj.put("name", name);
                                                obj.put("email", emailo);
                                                obj.put("mobile", telefono);
                                                obj.put("avatar", "http:\\/\\/icanstudioz.com\\/taxiapp\\/avatar\\/img_1557984683314.jpg");
                                                obj.put("key", passwordMD5);
                                                Gson gson = new Gson();
                                                User user = gson.fromJson(obj.toString(), User.class);
                                                String userString = gson.toJson(user);
                                                SessionManager.setUser(userString);
                                                SessionManager.setIsLogin();
                                                CheckConnection checkConnection = new CheckConnection(LoginActivity.this);
                                                checkConnection.isAnonymouslyLoggedIn();
                                                Toast.makeText(LoginActivity.this, getString(R.string.success_login) + " REPARTO " + name, Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                                intent.putExtra("id", "LOGIN");
                                                startActivity(intent);
                                                LoginActivity.this.finish();
                                                finish();
                                            } catch (Exception e) {
                                                Log.d("ALERTA: ", "error: " + e.toString());
                                            }
                                            Toast.makeText(LoginActivity.this, getString(R.string.success_login) + " REPARTO " + name, Toast.LENGTH_LONG).show();
                                            //startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                            //finish();
                                        }
                                    });
                                }else{
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            //_progresDiag.dismiss();
                                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
                                            alertDialogBuilder.setTitle("Error en el perfil");
                                            alertDialogBuilder.setMessage("Solo promotores pueden ingresar a ésta versión.")
                                                    .setCancelable(false)
                                                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {public void onClick(DialogInterface dialog, int id) {dialog.cancel();}});
                                            AlertDialog alertDialog = alertDialogBuilder.create();
                                            alertDialog.show();
                                        }
                                    });
                                }
                            } catch (Exception e) {
                                Log.i(TAG,"Errorr cliente: "+ e.toString());
                            }
                        } else{
                            Log.i(TAG,"No tiene perfil");
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    //_progresDiag.dismiss();
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
                                    alertDialogBuilder.setTitle("Error en el perfil");
                                    alertDialogBuilder.setMessage("Intente nuevamente.")
                                            .setCancelable(false)
                                            .setNegativeButton("OK", new DialogInterface.OnClickListener() {public void onClick(DialogInterface dialog, int id) {dialog.cancel();}});
                                    AlertDialog alertDialog = alertDialogBuilder.create();
                                    alertDialog.show();
                                }
                            });
                        }
                    }else{
                        Log.i(TAG, "ACTIVA TU GPS");
                        runOnUiThread(new Runnable() {
                            public void run() {
                                //_progresDiag.dismiss();
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
                                alertDialogBuilder.setTitle("GPS Desactivado");
                                alertDialogBuilder.setMessage("Active su GPS en alta precisión para un poder acceder al sistema.")
                                        .setCancelable(false)
                                        .setNegativeButton("OK", new DialogInterface.OnClickListener() {public void onClick(DialogInterface dialog, int id) {dialog.cancel();}});
                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();
                            }
                        });
                    }
                } catch (Exception e){
                    Log.i(TAG, "ACTIVA TU GPS");
                    runOnUiThread(new Runnable() {
                        public void run() {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
                            alertDialogBuilder.setTitle("GPS Desactivado");
                            alertDialogBuilder.setMessage("Active su GPS en alta precisión para un poder acceder al sistema.")
                                    .setCancelable(false)
                                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {public void onClick(DialogInterface dialog, int id) {dialog.cancel();}});
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                        }
                    });
                }
            } else if (codigo_login == Utiles.ERROR) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
                        alertDialogBuilder.setTitle("Usuario Erroneo");
                        alertDialogBuilder.setMessage("Error en el Usuario o Password")
                                .setCancelable(false)
                                .setNegativeButton("OK",new DialogInterface.OnClickListener() {public void onClick(DialogInterface dialog, int id) {dialog.cancel();}});
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    public void run() {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
                        alertDialogBuilder.setTitle("Error al Conectar al Servidor 1");
                        alertDialogBuilder.setMessage("Active su GPS en alta precisión para un poder acceder al sistema. o Hubo un error al conectar al Servidor, revise que este conectado a Internet y vuelva a intentar en unos minutos.")
                                .setCancelable(false)
                                .setNegativeButton("OK",new DialogInterface.OnClickListener() {public void onClick(DialogInterface dialog, int id) {dialog.cancel();}});
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                });
            }
        } catch (NumberFormatException nf) {
            runOnUiThread(new Runnable() {
                public void run() {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
                    alertDialogBuilder.setTitle("Error al Conectar al Servidor 2");
                    alertDialogBuilder.setMessage("Active su GPS en alta precisión para un poder acceder al sistema. o Hubo un error al conectar al Servidor, revise que este conectado a la red correcta.")
                            .setCancelable(false)
                            .setNegativeButton("OK",new DialogInterface.OnClickListener() {public void onClick(DialogInterface dialog, int id) {dialog.cancel();}});
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            });
        } catch (Exception e) {
            runOnUiThread(new Runnable() {
                public void run() {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
                    alertDialogBuilder.setTitle("Error al Conectar al Servidor 3");
                    alertDialogBuilder.setMessage("Active su GPS en alta precisión para un poder acceder al sistema. o Hubo un error al conectar al Servidor, revise que este conectado a Internet y vuelva a intentar en unos minutos.")
                            .setCancelable(false)
                            .setNegativeButton("OK",new DialogInterface.OnClickListener() {public void onClick(DialogInterface dialog, int id) {dialog.cancel();}});
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            });
        }
    }
    public void ingresarDatosSession(){
        if (_sesionActiva.isChecked()) {
            _usuarioHelper.deleteFlag();
            Log.i("ALERTA", "CHECKED");
            //UsuarioVO usuario = new UsuarioVO();
            Utiles.usuario = new UsuarioVO();
            Utiles.usuario.setFlag(1);
            Utiles.usuario.setLogin(_usuario);
            Utiles.usuario.setContrasenha(_contrasenha);
            _usuarioHelper.inicialFlag(Utiles.usuario);
        } else {
            Log.i("ALERTA", "SIN CHECKED");
            _usuarioHelper.deleteFlag();
        }
    }
    public void changepassword_dialog() {
        final Dialog dialog = new Dialog(LoginActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.password_reset);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        final EditText editTextUsuario = (EditText) dialog.findViewById(R.id.editTextUsuario);
        final EditText editTextEmailTelefonoUsuario = (EditText) dialog.findViewById(R.id.editTextEmailTelefonoUsuario);
        TextView title = (TextView) dialog.findViewById(R.id.title);
        TextView message = (TextView) dialog.findViewById(R.id.message);
        AppCompatButton btn_change = (AppCompatButton) dialog.findViewById(R.id.btn_reset);
        AppCompatButton btn_cancel = (AppCompatButton) dialog.findViewById(R.id.btn_cancel);
        Typeface font = Typeface.createFromAsset(getAssets(), "font/AvenirLTStd_Medium.otf");
        Typeface font1 = Typeface.createFromAsset(getAssets(), "font/AvenirLTStd_Book.otf");
        btn_change.setTypeface(font1);
        btn_cancel.setTypeface(font1);
        editTextUsuario.setTypeface(font);
        editTextEmailTelefonoUsuario.setTypeface(font);
        title.setTypeface(font);
        message.setTypeface(font);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        btn_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LoginActivity.this.getCurrentFocus();
                Log.i(TAG,"_editTextUsuario: "+editTextUsuario.getText().toString());
                Log.i(TAG,"_editTextEmailTelefonoUsuario: "+editTextEmailTelefonoUsuario.getText().toString());
                if(editTextUsuario.getText().toString().compareTo("") == 0){
                    Toast.makeText(getApplicationContext(), "Ingrese su usuario", Toast.LENGTH_LONG).show();
                }else{
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONParser jsonparser1 = new JSONParser();
                                String response1 = jsonparser1.getResponse(Utiles.URL_SERVIDOR + "/" + "recuperarContrasenha"+"/"+editTextUsuario.getText().toString()
                                        +"/"+editTextEmailTelefonoUsuario.getText().toString()+"/15");
                                Log.d(TAG, "URL: " + Utiles.URL_SERVIDOR + "/" + "recuperarContrasenha"+"/"+editTextUsuario.getText().toString()
                                        +"/"+editTextEmailTelefonoUsuario.getText().toString()+"/15");
                                Log.d(TAG, "response1: " + response1);
                                JSONArray jsonArray1 = new JSONArray(response1);
                                JSONObject jsonObject = jsonArray1.getJSONObject(0);
                                Flag=jsonObject.getString("flag");
                                Log.d(TAG, jsonObject.getString("flag"));
                                if(Flag.compareToIgnoreCase("1")==0){
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            dialogo.setTitle("Cambio de Contraseña");
                                            dialogo.setMessage("La contraseña a sido cambia a '123456'");
                                            dialogo.setCancelable(false);
                                            dialogo.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialogo1, int id) {
                                                    dialog.cancel();
                                                }
                                            });
                                            dialogo.show();
                                        }
                                    });
                                }else if(Flag.compareToIgnoreCase("Fallo")==0){
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            dialogo.setTitle("Error");
                                            dialogo.setMessage("El usuario ingresado no es correcto o no se encuentra registrado");
                                            dialogo.setCancelable(false);
                                            dialogo.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialogo1, int id) {}
                                            });
                                            dialogo.show();
                                        }
                                    });
                                }
                            }catch (JSONException e) {
                                Log.d(TAG, e.toString());
                            }catch (Exception e) {
                                Log.d(TAG, e.toString());
                            }
                        }
                    }).start();
                }
                if (view != null) {
                    hideKeyboard(LoginActivity.this, view);
                }

            }
        });
        dialog.show();
    }
    public static void hideKeyboard(Context context, View view) {
        // hide virtual keyboard
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
