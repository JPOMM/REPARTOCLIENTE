package com.efienza.cliente.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.efienza.cliente.services.LocationService;

public class OnAlarmReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context ctx, Intent intent) {
		Log.d("OnAlarmReceiver", "Recivio el Alarm receiver");
		ctx.startService(new Intent(ctx, LocationService.class));
		Log.d("OnAlarmReceiver", "Inicio el Location Services");
	}
	Editor putDouble(final Editor edit, final String key, final double value) {
		return edit.putLong(key, Double.doubleToRawLongBits(value));
	}
	double getDouble(final SharedPreferences prefs, final String key, final double defaultValue) {
		return Double.longBitsToDouble(prefs.getLong(key, Double.doubleToLongBits(defaultValue)));
	}
}


