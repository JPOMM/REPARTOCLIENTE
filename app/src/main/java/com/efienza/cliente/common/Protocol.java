package com.efienza.cliente.common;


import android.location.Location;
import android.util.Log;

import java.util.Calendar;
import java.util.Formatter;
import java.util.Locale;
import java.util.TimeZone;

/**
 *
 * @author John Manchego
 */
public class Protocol {

	/**
	 * Format device id message
	 */
	private static final String TAG = "PROTOCOL";
	public static String createLoginMessage(String id) {
		StringBuilder s = new StringBuilder("$PGID,");
		Formatter f = new Formatter(s, Locale.ENGLISH);
		s.append(id);
		byte checksum = 0;
		for (byte b : s.substring(1).getBytes()) {
			checksum ^= b;
		}
		f.format("*%02x\r\n", (int) checksum);
		f.close();
		return s.toString();
	}

	/**
	 * Format location message
	 */
	public static String createLocationMessage(boolean extended, Location l, double battery, int razonTra , int id_device, float odometro, int idUsuPedido) {
		StringBuilder s = new StringBuilder(extended ? "$TRCCR," : "$GPRMC,");
		Formatter f = new Formatter(s, Locale.ENGLISH);
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);
		calendar.setTimeInMillis(l.getTime());
		calendar.add(Calendar.HOUR_OF_DAY, -5);
		if (extended) {
			f.format("%1$tY-%1$tm-%1$td_%1$tH:%1$tM:%1$tS.%1$tL,A,", calendar);
			f.format("%.6f,%.6f,", l.getLatitude(), l.getLongitude());
			double speed =  l.getSpeed() * 1.943844;
			f.format("%d,%.2f,",  (int)speed , l.getBearing());
			f.format("%.2f,", l.getAltitude());
			f.format("%.0f,", battery);
			f.format("%.2f,", odometro);
		} else {
			f.format("%1$tH%1$tM%1$tS.%1$tL,A,", calendar);
			double lat = l.getLatitude();
			double lon = l.getLongitude();
			f.format("%02d%07.4f,%c,", (int) Math.abs(lat), Math.abs(lat) % 1 * 60, lat < 0 ? 'S' : 'N');
			f.format("%03d%07.4f,%c,", (int) Math.abs(lon), Math.abs(lon) % 1 * 60, lon < 0 ? 'W' : 'E');
			double speed = l.getSpeed() * 1.943844; // speed in knots
			f.format("%.2f,%.2f,", speed, l.getBearing());
			f.format("%1$td%1$tm%1$ty,,", calendar);
			f.format("%.2f,", odometro);
		}
		f.format("%d,", id_device);
		f.format("%d", razonTra);
		f.close();
		return s.toString();
		/*if(razonTra==69){
			StringBuilder s = new StringBuilder(extended ? "$NOTIF," : "$GPRMC,");
			Formatter f = new Formatter(s, Locale.ENGLISH);
			Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);
			calendar.setTimeInMillis(l.getTime());
			//calendar.add(Calendar.HOUR_OF_DAY, -5);
			if (extended) {
				f.format("%1$tY%1$tm%1$td%1$tH%1$tM%1$tS.%1$tL,A,", calendar);
				f.format("%.6f,%.6f,", l.getLatitude(), l.getLongitude());
				f.format("%.2f,%.2f,", l.getSpeed() * 1.943844, l.getBearing());
				f.format("%.2f,", l.getAltitude());
				f.format("%.0f,", battery);
				f.format("%.2f,", odometro);
				f.format("%1$s,", "MOV"+ String.valueOf(idUsuPedido) );

			}
			byte checksum = 0;
			for (byte b : s.substring(1).getBytes()) {
				checksum ^= b;
			}
			//JCVB OBTENER DEVICE ID
			f.format("%d,", id_device);
			f.format("%d", razonTra);
			Log.d(TAG, f.toString());
			f.close();
			return s.toString();
		}
		else{
			StringBuilder s = new StringBuilder(extended ? "$TRCCR," : "$GPRMC,");
			Formatter f = new Formatter(s, Locale.ENGLISH);
			Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC-5"), Locale.ENGLISH);
			calendar.setTimeInMillis(l.getTime());
			//calendar.add(Calendar.HOUR_OF_DAY, -5);
			if (extended) {
				f.format("%1$tY%1$tm%1$td%1$tH%1$tM%1$tS.%1$tL,A,", calendar);
				f.format("%.6f,%.6f,", l.getLatitude(), l.getLongitude());
				f.format("%.2f,%.2f,", l.getSpeed() * 1.943844, l.getBearing());
				f.format("%.2f,", l.getAltitude());
				f.format("%.0f,", battery);
				f.format("%.2f,", odometro);
			} else {
				f.format("%1$tH%1$tM%1$tS.%1$tL,A,", calendar);
				double lat = l.getLatitude();
				double lon = l.getLongitude();
				f.format("%02d%07.4f,%c,", (int) Math.abs(lat), Math.abs(lat) % 1 * 60, lat < 0 ? 'S' : 'N');
				f.format("%03d%07.4f,%c,", (int) Math.abs(lon), Math.abs(lon) % 1 * 60, lon < 0 ? 'W' : 'E');
				double speed = l.getSpeed() * 1.943844; // speed in knots
				f.format("%.2f,%.2f,", speed, l.getBearing());
				f.format("%1$td%1$tm%1$ty,,", calendar);
				f.format("%.2f,", odometro);
			}
			byte checksum = 0;
			for (byte b : s.substring(1).getBytes()) {
				checksum ^= b;
			}
			//JCVB OBTENER DEVICE ID
			f.format("%d,", id_device);
			f.format("%d", razonTra);
			//		f.format("*%02x\r\n", (int) checksum);

			Log.d(TAG, f.toString());

			f.close();
			//ABN5-DNN8-LC8D
			return s.toString();
		}*/
	}
}
