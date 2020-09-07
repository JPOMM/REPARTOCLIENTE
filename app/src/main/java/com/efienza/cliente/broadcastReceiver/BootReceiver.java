package com.efienza.cliente.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.efienza.cliente.common.Utiles;
import com.efienza.cliente.services.LocationService;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context ctx, Intent arg1) {
		SharedPreferences sharedpreferences = ctx.getSharedPreferences(Utiles.NOMBRE_PREFERENCIAS, Context.MODE_PRIVATE);
		Editor editor = sharedpreferences.edit();
		putDouble(editor, Utiles.ANT_LATITUD,0);
		putDouble(editor, Utiles.ANT_LONGITUD,0);
		editor.commit();
		ctx.startService(new Intent(ctx, LocationService.class));

	}
	Editor putDouble(final Editor edit, final String key, final double value) {
		return edit.putLong(key, Double.doubleToRawLongBits(value));
	}
	double getDouble(final SharedPreferences prefs, final String key, final double defaultValue) {
		return Double.longBitsToDouble(prefs.getLong(key, Double.doubleToLongBits(defaultValue)));
	}
}
