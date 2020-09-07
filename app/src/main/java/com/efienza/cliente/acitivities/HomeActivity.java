package com.efienza.cliente.acitivities;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.efienza.cliente.fragement.IngresarPedidosFragment;
import com.efienza.cliente.services.LocationService;
import com.efienza.cliente.session.SessionManager;
import com.efienza.cliente.R;
//import com.efienza.reparto.Server.Server;
import com.efienza.cliente.common.JSONParser;
import com.efienza.cliente.common.Utiles;
//import CheckConnection;
//import com.efienza.reparto.fragement.AcceptedRequestFragment;
//import com.efienza.reparto.fragement.HomeFragment;
import com.efienza.cliente.custom.CheckConnection;
//import com.efienza.reparto.fragement.ProfileFragment;
//import User;
import com.thebrownarrow.permissionhelper.ActivityManagePermission;

import org.json.JSONArray;
import org.json.JSONObject;
import android.os.AsyncTask;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.List;

/**
 * Created by android on 7/3/17.
 */
public class HomeActivity extends ActivityManagePermission implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "HomeActiviy";
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    public Toolbar toolbar;
    TextView is_online, username;
    SwitchCompat switchCompat;
    LinearLayout linearLayout;
    NavigationView navigationView;
    private ImageView avatar;
    SharedPreferences prefs;
    NotificationManager mgr;
    NotificationCompat.Builder mBuilder;
    ProgressDialog progressDialog;
    Context ctx;
    private String[][] MatrizDatosAlamcen;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        Log.d(TAG,"ENTROP HOME: ");
        BindView();
        Log.d(TAG,"7: ");
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("action")) {
            Log.d(TAG,"8: ");
            String action = intent.getStringExtra("action");
        }
        Menu m = navigationView.getMenu();
        Log.d(TAG,"9: ");
        Log.d(TAG,"m.size(): "+m.size());
        for (int i = 0; i < m.size(); i++) {
            MenuItem mi = m.getItem(i);
            Log.d(TAG,"Menu: "+ m.getItem(i).toString());
            //for aapplying a font to subMenu ...
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu != null && subMenu.size() > 0) {
                for (int j = 0; j < subMenu.size(); j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    Log.d(TAG,"subMenu: "+subMenu.getItem(j).toString());
                    applyFontToMenuItem(subMenuItem);
                }
            }
            applyFontToMenuItem(mi);
        }
        ctx=this;
        prefs = ctx.getSharedPreferences(Utiles.NOMBRE_PREFERENCIAS, Context.MODE_PRIVATE);
        fontToTitleBar( prefs.getString(Utiles.PREFERENCIAS_NOMBRE_APELLIDO, "Error"));
        //progressDialog = new ProgressDialog(ctx, R.style.AppTheme_Dark_Dialog);
        //progressDialog.setIndeterminate(true);progressDialog.setMessage("Cargando...");progressDialog.show();
        //this.startService(new Intent(this, LocationService.class));
        //inicializacionSocket();
        //obtenerDatosAlmacen();
    }
    public void obtenerDatosAlmacen(){
        Log.d(TAG,"obtenerDatosAlmacen");
        new Thread(new Runnable() {
            @Override
            public void run() {
                sincronizarDatosAlmacen();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Log.d(TAG,"Antes de mostrar cilindros");
                        try{
                            // TODO: Implement your own authentication logic here.
                            new Handler().postDelayed(
                                    new Runnable() {
                                        public void run() {
                                            //mostrarcilindros();
                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    progressDialog.dismiss();
                                                }
                                            }).start();
                                        }
                                    }, 1000);
                        }catch (Exception e){
                            Log.d(TAG, "eee.: " + e.toString());
                        }
                    }
                });
            }
        }).start();
    }
    public void sincronizarDatosAlmacen(){
        //Leer Tipo de balon

        try {

            String idAlmacen=prefs.getString(Utiles.PREFERENCIAS_IDALAMCEN, "-1");
            Log.d(TAG, "idAlmacen: "+idAlmacen);
            JSONParser jsonparserDA = new JSONParser();
            String responseDA = jsonparserDA.getResponse(Utiles.SERVIDORPRUEBAS + "/LeerAlmacenPorId?id="+idAlmacen);
            Log.d(TAG, "http://erp.rapigas.pe/Servicio.svc/WEB/" + "LeerAlmacenPorId?id="+idAlmacen);
            Log.d(TAG, "responseTB: "+responseDA);
            Log.d(TAG, "responseTB: "+responseDA.length());
            Log.d(TAG, "responseTB: "+responseDA.substring(0,6));
            if(responseDA.substring(0,6).compareToIgnoreCase("<html>")==0){
                Toast.makeText(this.getApplicationContext(), "Ocurrio un error intentelo nuevamente porfavor", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                this.finish();
            }else{
                if(responseDA!=""){
                    prefs = HomeActivity.this.getSharedPreferences(Utiles.NOMBRE_PREFERENCIAS, Context.MODE_PRIVATE);

                    JSONArray jsonArrayTB = new JSONArray("["+responseDA+"]");
                    Log.d(TAG, "jsonArrayCB.length: "+jsonArrayTB.length());
                    Log.d(TAG, "jsonArrayCB: "+jsonArrayTB);
                    MatrizDatosAlamcen=new String[jsonArrayTB.length()][2];
                    if(jsonArrayTB.length()>0) {
                        for (int i = 0; i < jsonArrayTB.length(); i++) {
                            JSONObject jsonObject = jsonArrayTB.getJSONObject(i);
                            //MatrizDatosAlamcen[i][0] = jsonObject.getString("IDFAMILIA3");
                            //MatrizDatosAlamcen[i][1] = jsonObject.getString("NOMBREFAMILIA3");
                            //MatrizDatosAlamcen[i + 1] = jsonObject.getString("NOMBREFAMILIA3");
                            Log.d(TAG, "IDALMACEN: " + jsonObject.getString("IDALMACEN"));
                            Log.d(TAG, "IDSEDE: " + jsonObject.getString("IDSEDE"));
                            Log.d(TAG, "DATOADICIONAL1: " + jsonObject.getString("DATOADICIONAL1"));
                            Log.d(TAG, "IDALMACENRETORNO1: " + jsonObject.getString("IDALMACENRETORNO1"));
                            Log.d(TAG, "IDALMACENRETORNO2: " + jsonObject.getString("IDALMACENRETORNO2"));
                            Log.d(TAG, "CAJAS: " + jsonObject.getString("CAJAS"));
                            JSONArray jsonArrayTB2 = new JSONArray( jsonObject.getString("CAJAS"));
                            Log.d(TAG, "jsonArrayTB2: " + jsonArrayTB2);
                            JSONObject jsonObject2 = jsonArrayTB2.getJSONObject(0);
                            prefs.edit().putString(Utiles.DATOADICIONAL1,  jsonObject.getString("DATOADICIONAL1")).apply();
                            prefs.edit().putString(Utiles.DATOADICIONAL2,  jsonObject.getString("DATOADICIONAL2")).apply();
                            prefs.edit().putString(Utiles.DIRECCION,  jsonObject.getString("DIRECCION")).apply();
                            prefs.edit().putString(Utiles.SERIE,  jsonObject.getString("SERIE")).apply();
                            prefs.edit().putString(Utiles.IDALMACENRETORNO1,  jsonObject.getString("IDALMACENRETORNO1")).apply();
                            prefs.edit().putString(Utiles.IDALMACENRETORNO2,  jsonObject.getString("IDALMACENRETORNO2")).apply();

                            prefs.edit().putString(Utiles.NOMBREALMACEN,  jsonObject2.getString("NOMBREALMACEN")).apply();
                            prefs.edit().putString(Utiles.IDCAJA,  jsonObject2.getString("IDCAJA")).apply();

                        }
                    }else{
                        Toast.makeText(this.getApplicationContext(), "Ocurrio un error intentelo nuevamente porfavor", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(this, HomeActivity.class);
                        startActivity(intent);
                        this.finish();
                    }
                }else{
                    Toast.makeText(this.getApplicationContext(), "Ocurrio un error intentelo nuevamente porfavor", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(this, HomeActivity.class);
                    startActivity(intent);
                    this.finish();
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "exx.: " + e.toString());
        }

    }
    public void inicializacionSocket(){
        try {
            Log.d(TAG, "10");
            mgr = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            Log.d(TAG, "11");
            mBuilder = new android.support.v4.app.NotificationCompat.Builder(this);
            Log.d(TAG, "12");
            prefs = this.getSharedPreferences(Utiles.NOMBRE_PREFERENCIAS, Context.MODE_PRIVATE);
            Log.d(TAG, "13");
            this.startService(new Intent(this, LocationService.class));
            //cargarDatagramSocket();
            Intent intent1 = getIntent();
            Bundle extras = intent1.getExtras();
            if (extras != null) {
                try {
                    Log.d(TAG, "ENTRO EXTRAS");
                    cargarDatagramSocket();
                    if(extras.getString("id").compareToIgnoreCase("LOGIN")==1){
                        cargarDatagramSocket();
                    }
                }catch (Exception e){
                    Log.i(TAG, "ERROR: " + e.toString());
                }
            }
        }catch (Exception e){
            Log.i(TAG, "UDPERROR: " + e.toString());
            Utiles.objError.guardarErrores(TAG+":2: "+e.toString());
        }
    }
    public void cargarDatagramSocket(){
        try{
            Log.d(TAG,"14");
            //Utiles.DGSocket = new DatagramSocket(4500);
            Utiles.DGSocket = new DatagramSocket(4500);
            Log.d(TAG,"15");
            Utiles.DGSocket.setReuseAddress(true);
            Log.d(TAG,"16");
            Utiles.DGSocket.setSoTimeout(8000);
            Log.d(TAG,"Utiles.DGSocket: "+ Utiles.DGSocket);
            Log.e(TAG, "prefs.getString(Utiles.PREFERENCIAS_PERFIL: "+prefs.getString(Utiles.PREFERENCIAS_PERFIL, "Error"));
            try{
                Log.e(TAG, "prefs.getString(Utiles.PREFERENCIAS_PERFIL: "+prefs.getString(Utiles.PREFERENCIAS_PERFIL, "Error"));
                if(prefs.getString(Utiles.PREFERENCIAS_PERFIL, "Error").compareToIgnoreCase("16")==1){
                    Log.e(TAG, "ENTRO: UDPClient1");
                    new UDPClient1().execute();
                }
            }catch (Exception e){
                Log.i(TAG, "UDPERROR: " + e.toString());
                Utiles.objError.guardarErrores(TAG+":2: "+e.toString());
            }
        }catch(SocketException e){
            Utiles.objError.guardarErrores(TAG+":1: "+e.toString());
            Log.i(TAG,"UDPERROR1: "+ e.toString());
        }catch (Exception e){
            Utiles.objError.guardarErrores(TAG + ":1: " + e.toString());
            Log.i(TAG, "UDPERROR2: "+e.toString());
        }
    }
    public class UDPClient1 extends AsyncTask<Void, Void, Void> {
        private static final String TAG = "UDPCLIENTE";
        String result;
        @Override
        protected Void doInBackground(Void... params) {
            Log.d(TAG,"12: ");
            byte[] mensaje = new byte[4];
            DatagramPacket dPacket= new DatagramPacket(mensaje,mensaje.length);
            Log.d(TAG,"dPacket: "+dPacket);
            while(true){
                try{
                    Utiles.DGSocket.receive(dPacket);
                    result= new String(dPacket.getData());
                    result=result.substring(0,2);
                    Log.e(TAG, result);
                    /*if(result.compareToIgnoreCase("69")==0){
                        Log.e(TAG, "ENTRO: " + result);
                        mBuilder.setContentTitle("RAPIGAS")
                                .setContentText("EL RESPARTIDOR ESTA AFUERA")
                                .setSmallIcon(R.drawable.ic_launcher)
                                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                                .setLights(Color.RED, 3000, 3000)
                                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                                .setOngoing(false);
                        mgr.notify(0, mBuilder.build());
                    }*/
                }catch (Exception e){
                    Utiles.objError.guardarErrores(TAG+":3: "+e.toString());
                    Log.e(TAG, e.toString());
                }
            }
        }
    }
    private void setupDrawer() {
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        //  globatTitle = );
        getSupportActionBar().setTitle(getString(R.string.app_name));
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_name) {
            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        //drawer.shouldDelayChildPressedState();
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) { super.onPostCreate(savedInstanceState);mDrawerToggle.syncState(); }
    @Override
    public void onConfigurationChanged(Configuration newConfig) { super.onConfigurationChanged(newConfig);mDrawerToggle.onConfigurationChanged(newConfig); }
    public void drawer_close() { mDrawerLayout.closeDrawer(GravityCompat.START); }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //AcceptedRequestFragment acceptedRequestFragment = new AcceptedRequestFragment();
        //Bundle bundle;
        switch (item.getItemId()) {
            case R.id.nuevoPedido:
                try {
                    //changeFragment(new HomeFragment(), getString(R.string.home));
                    //changeFragment(new MapaFragment(), "HomeFragment");
                    changeFragment(new IngresarPedidosFragment(), "HomeFragment");
                }catch (Exception e) {
                    Log.v(TAG,"Error case"+ e.toString());
                }
                break;
            case R.id.logout:
                SessionManager.logoutUser(getApplicationContext());
                finish();
                break;
            default:
                break;
        }
        return true;
    }
    public void changeFragment(final Fragment fragment, final String fragmenttag) {
        try {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    drawer_close();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().addToBackStack(null);
                    fragmentTransaction.replace(R.id.frame, fragment, fragmenttag);
                    fragmentTransaction.commit();
                    fragmentTransaction.addToBackStack(null);
                }
            }, 50);
        } catch (Exception e) {
            Log.v(TAG,"Error case"+ e.toString());
        }
    }
    @SuppressLint("ParcelCreator")
    public class CustomTypefaceSpan extends TypefaceSpan {
        private final Typeface newType;
        public CustomTypefaceSpan(String family, Typeface type) {
            super(family);
            newType = type;
        }
        @Override
        public void updateDrawState(TextPaint ds) {
            applyCustomTypeFace(ds, newType);
        }
        @Override
        public void updateMeasureState(TextPaint paint) {
            applyCustomTypeFace(paint, newType);
        }
        private void applyCustomTypeFace(Paint paint, Typeface tf) {
            int oldStyle;
            Typeface old = paint.getTypeface();
            if (old == null) {
                oldStyle = 0;
            } else {
                oldStyle = old.getStyle();
            }
            int fake = oldStyle & ~tf.getStyle();
            if ((fake & Typeface.BOLD) != 0) {
                paint.setFakeBoldText(true);
            }
            if ((fake & Typeface.ITALIC) != 0) {
                paint.setTextSkewX(-0.25f);
            }
            paint.setTypeface(tf);
        }
    }
    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "font/AvenirLTStd_Medium.otf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }
    public void fontToTitleBar(String title) {
        try {
            Typeface font = Typeface.createFromAsset(getAssets(), "font/AvenirLTStd_Book.otf");
            title = "<font color='#000000'>" + title + "</font>";
            SpannableString s = new SpannableString(title);
            s.setSpan(font, 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            Log.d(TAG,"String.valueOf(s): "+Html.fromHtml(String.valueOf(s)));
            Log.d(TAG,"PREFERENCIAS_NOMBRE_APELLIDO: "+ prefs.getString(Utiles.PREFERENCIAS_NOMBRE_APELLIDO, "")) ;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                //toolbar.setTitle(Html.fromHtml(String.valueOf(s), Html.FROM_HTML_MODE_LEGACY));
                toolbar.setTitle(Html.fromHtml(prefs.getString(Utiles.PREFERENCIAS_NOMBRE_APELLIDO, ""), Html.FROM_HTML_MODE_LEGACY));

            } else {
                //toolbar.setTitle((Html.fromHtml(String.valueOf(s))));
                toolbar.setTitle((Html.fromHtml(prefs.getString(Utiles.PREFERENCIAS_NOMBRE_APELLIDO, ""))));
            }
        } catch (Exception e) {
            Log.e("catch", e.toString());
        }
    }
    public Fragment getVisibleFragment() {
        FragmentManager fragmentManager = HomeActivity.this.getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment != null && fragment.isVisible())
                    return fragment;
            }
        }
        return null;
    }
    public void BindView() {
        try {
            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            Log.d(TAG, "1: ");
            toolbar.setTitle(getString(R.string.app_name));
            navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
            Log.d(TAG, "2: ");
            switchCompat = (SwitchCompat) navigationView.getHeaderView(0).findViewById(R.id.online);
            //avatar = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.profile);
            linearLayout = (LinearLayout) navigationView.getHeaderView(0).findViewById(R.id.linear);
            is_online = (TextView) navigationView.getHeaderView(0).findViewById(R.id.is_online);
            username = (TextView) navigationView.getHeaderView(0).findViewById(R.id.txt_name);
            Log.d(TAG, "3: ");
            TextView version = (TextView) navigationView.getHeaderView(0).findViewById(R.id.version);
            try {
                PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
                String ver = pInfo.versionName;
                version.setText("V ".concat(ver));
            } catch (PackageManager.NameNotFoundException e) {
                Log.v(TAG,"Error case"+ e.toString());
            }
            Log.d(TAG, "4: ");
            navigationView.setCheckedItem(R.id.nuevoPedido);
            onNavigationItemSelected(navigationView.getMenu().findItem(R.id.nuevoPedido));
            setupDrawer();
            Log.d(TAG, "5: ");
            try {
                Typeface font = Typeface.createFromAsset(getAssets(), "font/AvenirLTStd_Book.otf");
                username.setTypeface(font);
            } catch (Exception e) {
                Log.v(TAG,"Error case"+ e.toString());
            }
            toolbar.setTitle("");
            if (CheckConnection.haveNetworkConnection(getApplicationContext())) {
                //getUserInfo();
            } else {
                Toast.makeText(HomeActivity.this, getString(R.string.network), Toast.LENGTH_LONG).show();
                String name = SessionManager.getName();
                //String url = SessionManager.getAvatar();
                username.setText(name);
               // Glide.with(getApplicationContext()).load(url).apply(new RequestOptions().error(R.mipmap.ic_account_circle_black_24dp)).into(avatar);
            }
        }
        catch (Exception e) {
            Log.d(TAG, "evv: "+e.toString());
        }
    }
    @Override
    public void onBackPressed() {
        /*if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawer_close();
        } else if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        } else {
            super.onBackPressed();
        }*/
    }

}
