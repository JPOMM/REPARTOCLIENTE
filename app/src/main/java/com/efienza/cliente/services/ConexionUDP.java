package com.efienza.cliente.services;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.efienza.cliente.common.Protocol;
import com.efienza.cliente.persistance.UbicacionHelper;
import com.efienza.cliente.common.JSONParser;
import com.efienza.cliente.common.Utiles;

import org.json.JSONArray;
import org.json.JSONException;

public class ConexionUDP implements Runnable {
	private static final String TAG = "ConexionUDP";
	Location loc2;
	int razonTransmision;
	int bateria;
	Context ctx;
	UbicacionHelper ubicacionHelper;
	String messageStr;
	int deviceID;
	float odometro;
	int idUsuPedido;
	public ConexionUDP(Location l, int razonTransmision, int bat, Context ctx, int deviceID, float odometro, int idUsuPedido) {
		Log.d(TAG, "Location: " + l);
		Log.d(TAG, "razonTransmision: " + razonTransmision);
		Log.d(TAG, "bat: " + bat);
		Log.d(TAG, "ctx: " + ctx);
		Log.d(TAG, "deviceID: " + deviceID);
		Log.d(TAG, "odometro: " + odometro);
		Log.d(TAG, "idUsuPedido: " + idUsuPedido);
		loc2 = l;
		this.razonTransmision=razonTransmision;
		bateria= bat;
		this.ctx=ctx;
		ubicacionHelper = new UbicacionHelper(ctx);
		this.deviceID = deviceID;
		this.odometro=odometro;
		this.idUsuPedido=idUsuPedido;
	}

	@Override
	public void run(){
		try{
			Log.d(TAG, "loc2: " + loc2);
			Log.d(TAG, "bateria: " + bateria);
			Log.d(TAG, "razonTransmision: " + razonTransmision);
			Log.d(TAG, "deviceID: " + deviceID);
			messageStr= Protocol.createLocationMessage(true, loc2, bateria, razonTransmision ,deviceID,odometro,idUsuPedido);
			Log.d(TAG, "messageStr: " + messageStr);
			String[] parts = messageStr.split(",");
			String cabecera = parts[0]; // 123
			String fechaComunicacion = parts[1]; // 654321
			String A = parts[2]; // 123
			String latitud = parts[3]; // 654321
			String longitud = parts[4]; // 123
			String velocidad = parts[5]; // 654321
			String bearing = parts[6]; // 123
			String altitud = parts[7]; // 654321

			Log.e(TAG, " cabecera: " + cabecera);
			Log.e(TAG, " fechaComunicacion: " + fechaComunicacion);
			Log.e(TAG, " A: " + A);
			Log.e(TAG, " latitud: " + latitud);
			Log.e(TAG, " longitud: " + longitud);
			Log.e(TAG, " velocidad: " + velocidad);
			Log.e(TAG, " bearing: " + bearing);
			Log.e(TAG, " altitud: " + altitud);
			Log.e(TAG, " bateria: " + bateria);
			Log.e(TAG, " odometro: " + odometro);
			Log.e(TAG, " deviceID: " + deviceID);
			Log.e(TAG, " razonTransmision: " + razonTransmision);
			try {
				Log.d(TAG, Utiles.URL_SERVIDOR + "/" + "insertarTransmisionesReparto/"+cabecera+"/"+fechaComunicacion+"/"+A+"/"+latitud+"/"+longitud+"/"+velocidad
						+"/"+bearing+"/"+altitud+"/"+ bateria+"/" +odometro+"/"+deviceID+"/"+razonTransmision);
				JSONParser jsonparserp = new JSONParser();
				String responsep = jsonparserp.getResponse(Utiles.URL_SERVIDOR + "/" + "insertarTransmisionesReparto/"+cabecera+"/"+fechaComunicacion+"/"+A+"/"+latitud+"/"+longitud+"/"+velocidad
						+"/"+bearing+"/"+altitud+"/"+ bateria+"/" +odometro+"/"+deviceID+"/"+razonTransmision);
				Log.d(TAG, responsep);
				JSONArray jsonArray0 = new JSONArray(responsep);
				//infoDetPed = new String[jsonArray0.length()][4];
				//for (int i = 0; i < jsonArray0.length(); i++) {
				//	JSONObject jsonObject = jsonArray0.getJSONObject(i);
				//	infoDetPed[i][0] = jsonObject.getString("nCodPed");
				//}
			}catch (JSONException e) {
				e.printStackTrace();
				Log.d(TAG, " JSONException: "+ e.toString());
			}catch (Exception e) {
				e.printStackTrace();
				Log.d(TAG, " Exception: "+ e.toString());
			}

		} catch (Exception e) {
			Log.e(TAG,"Error al Conectar Excepption normal", e);
			//Toast.makeText(this.ctx.getApplicationContext(), "No está transmitiendo", Toast.LENGTH_SHORT).show();
			//UbicacionVO vo= new UbicacionVO();
			//vo.setMensaje(messageStr);
			//ubicacionHelper.ingresarUbicacion(vo);
		}
	}

	/*@Override
	public void run(){
		try{
			Log.d(TAG, "loc2: " + loc2);
			Log.d(TAG, "bateria: " + bateria);
			Log.d(TAG, "razonTransmision: " + razonTransmision);
			Log.d(TAG, "deviceID: " + deviceID);
			messageStr= Protocol.createLocationMessage(true, loc2, bateria, razonTransmision ,deviceID,odometro,idUsuPedido);
			int server_port = Utiles.PUERTOAGENTE;
			//DatagramSocket s;
			//s = new DatagramSocket();
			//s.setSoTimeout(8000);
			Log.d(TAG, "Utiles.SERVIDORAGENTE: " + Utiles.SERVIDORAGENTE);
			InetAddress local = InetAddress.getByName(Utiles.SERVIDORAGENTE);
			Log.d(TAG, "messageStr: " + messageStr);
			int msg_length=messageStr.length();
			byte[] message = messageStr.getBytes();
			Log.d(TAG, "message: " + message);
			Log.d(TAG, "msg_length: " + msg_length);
			Log.d(TAG, "local: " + local);
			Log.d(TAG, "puerto " + server_port);
			DatagramPacket p = new DatagramPacket(message, msg_length,local,server_port);

			Log.d(TAG, "getAddress " + p.getAddress());
			Log.d(TAG, "p.getAddress " + p.getAddress());
			Log.d(TAG, "p.getLength " + p.getLength());
			Log.d(TAG, "p.getPort " + p.getPort());
			Log.d(TAG, "p.getSocketAddress " + p.getSocketAddress());

			Utiles.DGSocket.send(p);
			//List<UbicacionVO> lista = ubicacionHelper.obtenerUbicacion();
			List<UbicacionVO> lista = ubicacionHelper.obtenerUbicacion();
			if (lista.size()>0) {
				for (int i=0;i<lista.size();i++) {
					messageStr=lista.get(i).getMensaje();
					msg_length=messageStr.length();
					message = messageStr.getBytes();
					p = new DatagramPacket(message, msg_length,local,server_port);
					ubicacionHelper.borrarUbicacion(lista.get(i).getIdUbicacion());
					Utiles.DGSocket.send(p);
					//s.receive(p);
				}
			}
		} catch (SocketException e) {
			Log.e(TAG,"Error al Conectar", e);
			UbicacionVO vo= new UbicacionVO();
			vo.setMensaje(messageStr);
			ubicacionHelper.ingresarUbicacion(vo);
		} catch (IOException e) {
			Log.e(TAG,"Error al Conectar", e);
			UbicacionVO vo= new UbicacionVO();
			vo.setMensaje(messageStr);
			ubicacionHelper.ingresarUbicacion(vo);
		} catch (Exception e) {
			Log.e(TAG,"Error al Conectar Excepption normal", e);
			//Toast.makeText(this.ctx.getApplicationContext(), "No está transmitiendo", Toast.LENGTH_SHORT).show();
			//UbicacionVO vo= new UbicacionVO();
			//vo.setMensaje(messageStr);
			//ubicacionHelper.ingresarUbicacion(vo);
		}
	}*/


}
