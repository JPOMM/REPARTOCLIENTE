package com.efienza.cliente.services;

import android.Manifest;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.efienza.cliente.common.Utiles;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class LocationService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    private static final String TAG = "LocationService";
    private boolean currentlyProcessingLocation = false;
    private LocationRequest locationRequest;
    private GoogleApiClient locationClient;
    private int raztra;
    private List<Location> cercas;
    SharedPreferences prefs;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        cercas = new ArrayList<Location>();
        //Centro de Control:
        //-16.386886, -71.552087
        /*Location loc = new Location ("Centro de Control");
        loc.setLatitude(-16.386887);
        loc.setLongitude(-71.552192);
        cercas.add(loc);
        Location locCasa = new Location ("Casa");
        locCasa.setLatitude(-16.420565);
        locCasa.setLongitude(-71.512488);
        cercas.add(locCasa);*/

        if (!currentlyProcessingLocation) {
            startTracking();
        }
        return Service.START_STICKY;
    }

    private void startTracking() {
        Log.d(TAG, "startTracking");
        currentlyProcessingLocation = true;
        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
            locationClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API).build();
            if (!locationClient.isConnected() || !locationClient.isConnecting()) {
                locationClient.connect();
            }
        } else {
            Log.e(TAG, "unable to connect to google play services.");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    Editor putDouble(final Editor edit, final String key, final double value) {
        return edit.putLong(key, Double.doubleToRawLongBits(value));
    }

    double getDouble(final SharedPreferences prefs, final String key, final double defaultValue) {
        return Double.longBitsToDouble(prefs.getLong(key, Double.doubleToLongBits(defaultValue)));
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.e(TAG, "position: " + location.getLatitude() + ", " + location.getLongitude() + " accuracy: " + location.getAccuracy());
        if (location != null) {
            prefs = this.getSharedPreferences(Utiles.NOMBRE_PREFERENCIAS, Context.MODE_PRIVATE);
            Log.e(TAG, "Datos DESPUES:" + prefs.getString(Utiles.PREFERENCIAS_ID_USUARIO, "-1"));
            //String deviceID = prefs.getString(Utiles.PREFERENCIAS_ID_USUARIO, "-1");
            String deviceID = prefs.getString(Utiles.PREFERENCIAS_CODUNI, "-1");
            if (deviceID.compareTo("-1") != 0) {
                Log.e(TAG, " accuracy: " + location.getAccuracy());
                if (location.getAccuracy() < 100.0f) {
                    Log.e(TAG, String.valueOf(validarFechaYHoraLocation()));
                    if (validarFechaYHoraLocation()) {
                        try {
                            stopLocationUpdates();
                            Location anteriorLocation = new Location("AnteriorLocation");
                            anteriorLocation.setLongitude(getDouble(prefs, Utiles.ANT_LONGITUD, 0));
                            anteriorLocation.setLatitude(getDouble(prefs, Utiles.ANT_LATITUD, 0));
                            Location locationB = new Location("point B");
                            locationB.setLatitude(location.getLatitude());
                            locationB.setLatitude(location.getLatitude());
                            locationB.setLongitude(location.getLongitude());
                            float distance = anteriorLocation.distanceTo(locationB); //in metres
                            Log.e(TAG, " Anterior : " + anteriorLocation);
                            Log.e(TAG, " Nueva: " + locationB);
                            Log.e(TAG, " Distancia: " + distance);
                            Log.e(TAG, " Bateria:" + getBatteryLevel());
                            String odoPref = prefs.getString(Utiles.PREFERENCIAS_ODOMETRO, "0");
                            float valtmp = 0;
                            float odometro = Float.parseFloat(odoPref);
                            Log.e(TAG, " Odometro ANT: " + odometro / 1000);
                            if (anteriorLocation.getLongitude() != 0 || anteriorLocation.getLatitude() != 0) {
                                if (distance >= 10) {
                                    if (odometro != 0) {
                                        valtmp = odometro;
                                    }
                                    valtmp = (valtmp + distance);
                                    prefs.edit().putString(Utiles.PREFERENCIAS_ODOMETRO, Float.toString(valtmp)).commit();
                                }
                            }
                            Log.e(TAG, " valtmp: " + valtmp);
                            Log.e(TAG, " Odometro: " + prefs.getString(Utiles.PREFERENCIAS_ODOMETRO, "0"));
                            new Thread(new ConexionUDP(location, Utiles.RAZON_NORMAL_DENTRO, getBatteryLevel(), this.getApplicationContext(), Integer.parseInt(deviceID), Float.parseFloat(prefs.getString(Utiles.PREFERENCIAS_ODOMETRO, "0")) / 1000, 0)).start();

                            Utiles.Despierta(this.getApplicationContext(), Utiles.INTERVALO);
                            Editor editor = prefs.edit();
                            putDouble(editor, Utiles.ANT_LATITUD, location.getLatitude());
                            putDouble(editor, Utiles.ANT_LONGITUD, location.getLongitude());
                            editor.commit();
                        } catch (Exception e) {
                            Utiles.objError.guardarErrores(TAG + ": " + e.toString());
                            Log.e(TAG, " Error Location: " + e.toString());
                        }
                    }
                    this.stopSelf();
                }
            }
        }
    }

    public int getBatteryLevel() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ECLAIR) {
            Intent batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, 1);
            return (int) ((level * 100.0) / scale);
        } else {
            return 0;
        }
    }

    private void stopLocationUpdates() {
        if (locationClient != null && locationClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(locationClient, this);
            locationClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected");
        try {
            locationRequest = LocationRequest.create();
            locationRequest.setInterval(1000); // milliseconds
            locationRequest.setFastestInterval(1000); // the fastest rate in milliseconds at which your app can handle location updates
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
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
            LocationServices.FusedLocationApi.requestLocationUpdates(locationClient, locationRequest, this);
        }catch (Exception e){
            //Utiles.objError.guardarErrores(TAG + ": " + e.toString());
            Log.e(TAG, " Error Location: " + e.toString());
        }
    }
    @Override
    public void onConnectionSuspended(int i) {}
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed");
    }
    private boolean validarFechaYHoraLocation() {
        boolean flagvalido=false;
        Calendar cal= Calendar.getInstance();// Valida si esta de L-V de 7:00-19:00 y los Sabados de 7:00-13:00
        if((cal.get(Calendar.HOUR_OF_DAY)>=7 && cal.get(Calendar.HOUR_OF_DAY)<19&& cal.get(Calendar.DAY_OF_WEEK)>= Calendar.MONDAY &&
                cal.get(Calendar.DAY_OF_WEEK)<= Calendar.FRIDAY)||  (cal.get(Calendar.DAY_OF_WEEK)== Calendar.SATURDAY &&
                cal.get(Calendar.HOUR_OF_DAY)>=7 && cal.get(Calendar.HOUR_OF_DAY)<13)) {
            flagvalido =true;
        }else {
            SharedPreferences sharedpreferences = this.getApplicationContext().getSharedPreferences(Utiles.NOMBRE_PREFERENCIAS, Context.MODE_PRIVATE);
            Editor editor = sharedpreferences.edit();
            putDouble(editor, Utiles.ANT_LATITUD,0);
            putDouble(editor, Utiles.ANT_LONGITUD,0);
            editor.commit();
            NotificationManager mgr=(NotificationManager)this.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            mgr.cancelAll();
            Utiles.DespiertaAlas7(this.getApplicationContext());
        }
        return flagvalido;
    }

}