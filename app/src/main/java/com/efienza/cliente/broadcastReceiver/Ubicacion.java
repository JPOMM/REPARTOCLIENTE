package com.efienza.cliente.broadcastReceiver;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * Created by John Paul Manchego Medina on 21/07/2016.
 */
public class Ubicacion implements LocationListener {

    private Context _ctx;
    LocationManager locationManager;
    String _proveedor;
    private Boolean _networkOn;

    public Ubicacion(Context ctx) {
        this._ctx = ctx;
        locationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        _proveedor = LocationManager.NETWORK_PROVIDER;
        _networkOn = locationManager.isProviderEnabled(_proveedor);
        locationManager.requestLocationUpdates(_proveedor, 1000, 1, this);
        getLocation(ctx);
    }
    public Location getLocation(Context ctx){
        if(_networkOn){
            Location lc=locationManager.getLastKnownLocation(_proveedor);
            if(lc!=null){
                /*StringBuilder builder= new StringBuilder();
                builder.append("Latitud: ").append(lc.getLatitude())
                       .append("Longitud: ").append(lc.getLongitude());
                Toast.makeText(_ctx,builder.toString(),Toast.LENGTH_LONG).show();*/
                return lc;
            }
        }
        return null;
    }
    @Override
    public void onLocationChanged(Location location) {}
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
    @Override
    public void onProviderEnabled(String provider) {
        //ESTA ACTIVADO
    }
    @Override
    public void onProviderDisabled(String provider) {}
}
