package com.efienza.cliente.broadcastReceiver;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.efienza.cliente.common.Utiles;

import java.net.DatagramPacket;

/**
 * Created by PC-DESARROLLO on 25/11/2016.
 */
public class UDPClient extends AsyncTask<String, String, String> {
    private static final String TAG = "UDPCLIENTE";
    String result;
    Context ctx;
    NotificationCompat.Builder mBuilder;

    @Override
    protected String doInBackground(String... params) {
        byte[] mensaje = new byte[4];
        DatagramPacket dPacket= new DatagramPacket(mensaje,mensaje.length);
        while(true){
            try{
                Utiles.DGSocket.receive(dPacket);
                result= new String(dPacket.getData());
                result=result.substring(0,2);
                Log.e(TAG, result);
                if(result.compareToIgnoreCase("69")==0){
                    //Log.e(TAG,"ENTRO: "+ result);

                    /*NotificationManager mgr=(NotificationManager)ctx.getSystemService(Context.NOTIFICATION_SERVICE)	;
                    mBuilder = new android.support.v4.app.NotificationCompat.Builder(ctx);
                    mBuilder.setContentTitle("Uvicar Ventas")
                            .setContentText("EL RESPARTIDOR ESTA AFUERA")
                                    //.setSmallIcon(R.drawable.ic_launcher)
                            .setOngoing(true);
                    mgr.notify(0, mBuilder.build());*/
                    return result;
                }
                return "";
            }catch (Exception e){
                Log.e(TAG, e.toString());
            }
        }
    }
}
