package com.efienza.cliente.common;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.efienza.cliente.broadcastReceiver.OnAlarmReceiver;
import com.efienza.cliente.broadcastReceiver.UDPClient;
import com.efienza.cliente.model.UsuarioVO;
import com.efienza.cliente.persistance.PedidoHelper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Utiles extends android.support.v4.app.FragmentActivity {

	private static final String TAG = "UTILES";
	public static String COOKIE;
	public static String nombreTXT="textFile.txt";
	public static String CODCLIENTE="1254";
	public static NotificationManager notificationManager=null;
	public static int INTERVALO = 1;
	public static int ESTADO_ACTIVA =1;
	public static int ESTADO_INACTIVA =0;
	public static String NOMBRE_PREFERENCIAS="localizacion";
	public static String ANT_LATITUD="anteriorLatitud";
	public static String ANT_LONGITUD="anteriorLongitud";
	public static String PREFERENCIAS_ID_USUARIO="codigo_usuario";
	public static String PREFERENCIAS_NOMBRE="nombre_usuario";
	public static String PREFERENCIAS_APELLIDO_PATERNO="apellido_paterno_usuario";
	public static String PREFERENCIAS_APELLIDO_MATERNO="apellido_materno_usuario";
	public static String PREFERENCIAS_NOMBRE_APELLIDO="Nombre apellido";
	public static String PREFERENCIAS_DIRECCION="direccion_usuario";
	public static String PREFERENCIAS_TELEFONO="telefono_usuario";
	public static String PREFERENCIAS_PERFIL="perfil_usuario";
	public static String PREFERENCIAS_EMAIL="email_usuario";
	public static String PREFERENCIAS_CODUNI="codUnidad_usuario";
	public static String PREFERENCIAS_ODOMETRO="odometro_usuario";
	public static String PREFERENCIAS_SEDE="sede_usuario";
	public static String PREFERENCIAS_LATSEDE="latsede_usuario";
	public static String PREFERENCIAS_LONGSEDE="longsede_usuario";
	public static String PREFERENCIAS_LOGIN="login_usuario";
	public static String PREFERENCIAS_FLAGPROM="flag_promocion";
	public static String PREFERENCIAS_IDALAMCEN="idAlmacen";
    public static String PREFERENCIAS_CODCLIERP="Codigo del cliente en el ERP";
    public static String PREFERENCIAS_NRODOCERP="Numero de documento en el ERP";
    public static String PREFERENCIAS_TIPDOCERP="Tipo de documento en el ERP";

	public static String DATOADICIONAL1="DATOADICIONAL1";
	public static String DATOADICIONAL2="DATOADICIONAL2";
	public static String DIRECCION="DIRECCION";
	public static String SERIE="SERIE";
	public static String IDALMACENRETORNO1="IDALMACENRETORNO1";
	public static String IDALMACENRETORNO2="IDALMACENRETORNO2";
	public static String IDCAJA="IDCAJA";
	public static String NOMBREALMACEN="NOMBREALMACEN";

	public static DatagramSocket DGSocket;
	public static UDPClient UDPcliente;
	public static PedidoHelper pedidos;
	public static UsuarioVO usuario = new UsuarioVO();
	public static int RAZON_NORMAL_DENTRO=44;
	public static int RAZON_NORMAL_FUERA=45;
	public static int RAZON_ESTOY_AFUERA=69;
	public static String PREFERENCIAS_VE_MAPAS="ve_mapas";
	public static String SHARED_PREFERENCES_INICIADO="iniciado";
	public static int OK=200;
	public static int CREATED=201;
	public static int ERROR=403;
	public static int NO=404;
	public static String SERVIDOR = "http://190.119.213.58";
    //public static String SERVIDOR = "http://192.168.10.103";
	public static String SERVIDORPRUEBAS = "http://erp.rapigas.pe/Servicio.svc/WEB";
	//public static String SERVIDORPRUEBAS = "http://erp.rapigas.pe/Servicio.svc/WEB";
	public static String SERVIDORFORANEO = "http://erp.rapigas.pe/Servicio.svc/WEB";
	//public static String SERVIDORAGENTE = "190.119.213.58";
	public static String SERVIDORAGENTE = "190.119.213.58";
	public static int PUERTO = 2021;
    //public static int PUERTO = 8080;
	public static int PUERTOFORANERO = 8080;
	//public static int PUERTOAGENTE = 241;
	public static int PUERTOAGENTE = 174;
	//public static String URL_LOGIN=SERVIDOR+":"+PUERTO+"/WebServiceNewPedidos/webresources/login";
	//public static String URL_SERVIDOR =SERVIDOR+":"+PUERTO+"/WebServiceNewPedidos/webresources";
	public static String URL_LOGIN=SERVIDOR+":"+PUERTO+"/WebServiceNewPedidos_Pruebas/webresources/login";
	public static String URL_SERVIDOR =SERVIDOR+":"+PUERTO+"/WebServiceNewPedidos_Pruebas/webresources";

	public static GlobalClass globalClass;

	public static LogErrores objError;
	static final int READ_BLOCK_SIZE = 100;
	private static final int REQUEST_EXTERNAL_STORAGE = 1;
	private static String[] PERMISSIONS_STORAGE = {
			Manifest.permission.READ_EXTERNAL_STORAGE,
			Manifest.permission.WRITE_EXTERNAL_STORAGE
	};
	public static void verifyStoragePermissions(Activity activity) {
		int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
		if (permission != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);}
	}
	public static void Despierta(Context context , int tiempo) {
		AlarmManager alarmMgr;
		PendingIntent alarmIntent;
		alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, OnAlarmReceiver.class);
		alarmIntent = PendingIntent.getBroadcast(context, 182, intent, 0);
		alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 60 * 1000 * tiempo, alarmIntent);
	}
	public static void DespiertaAlas7(Context ctx) {
		Calendar cal= Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 7);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		if (cal.getTimeInMillis()< System.currentTimeMillis()) {
			cal.add(Calendar.DAY_OF_MONTH, 1);
		}
		AlarmManager alarmMgr;
		PendingIntent alarmIntent;
		alarmMgr = (AlarmManager)ctx.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(ctx, OnAlarmReceiver.class);
		alarmIntent = PendingIntent.getBroadcast(ctx, 182, intent, 0);
		alarmMgr.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), alarmIntent);
	}
	public static int validarGPS(Context ctx) {
		LocationManager locationManager = (LocationManager) ctx.getSystemService(LOCATION_SERVICE);
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			Log.i("About GPS", "GPS is Enabled in your device");
			return 1;
		} else {
			Log.i("About GPS", "GPS is not Enabled in your device");
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx);
			alertDialogBuilder.setTitle("GPS Desactivado");
			alertDialogBuilder.setMessage("Active su GPS para un correcto funcionamiento.")
					.setCancelable(false)
					.setNegativeButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();}});
			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
			return 0;
		}
	}
	public static int validarSede(Location localizacion){
		List<Location> cercas;
		cercas= new ArrayList<Location>();
		//Camana: 0
		//-16.619113, -72.718658   10km
		Location loc1 = new Location("Sede Camana");
		loc1.setLatitude(-16.619113);
		loc1.setLongitude(-72.718658);
		loc1.setProvider("Sede Camana");
		//Huancayo : 1
		//-12.074443, -75.201867   4km
		Location loc2 = new Location("Sede Huancayo");
		loc2.setLatitude(-12.069003);
		loc2.setLongitude(-75.210963 );
		loc2.setProvider("Sede Huancayo");
		//chilca : 2
		Location loc3 = new Location("Sede Chilca");
		loc3.setLatitude(-12.078766);
		loc3.setLongitude(-75.201882);
		loc3.setProvider("Sede Chilca");
		//Mollendo: 3
		//-17.016865, -72.011091  3km
		Location loc4 = new Location("Sede Mollendo");
		loc4.setLatitude(-17.016865);
		loc4.setLongitude(-72.011091);
		loc4.setProvider("Sede Mollendo");
		//Moquegua: 4
		//-17.195096, -70.937392  4km
		Location loc5 = new Location("Sede Moquegua");
		loc5.setLatitude(-17.195096);
		loc5.setLongitude(-70.937392);
		loc5.setProvider("Sede Moquegua");
		//Arequipa: 5
		//-16.399441, -71.540471  8km
		Location loc6 = new Location("Sede Arequipa");
		loc6.setLatitude(-16.399441);
		loc6.setLongitude(-71.540471);
		loc6.setProvider("Sede Arequipa");
		//Lima: 6
		//-12.092623, -77.011070  30km
		Location loc7 = new Location("Sede Lima");
		loc7.setLatitude(-12.092623);
		loc7.setLongitude(-77.011070);
		loc7.setProvider("Sede Lima");
		cercas.add(loc1);
		cercas.add(loc2);
		cercas.add(loc3);
		cercas.add(loc4);
		cercas.add(loc5);
		cercas.add(loc6);
		cercas.add(loc7);
		int cercano=0;
		int cercanofinal=0;
		int flagCentroSur=0;

		Log.d(TAG, "localizacion.getLatitude(): " + localizacion.getLatitude());
		Log.d(TAG, "localizacion.getLongitude(): " + localizacion.getLongitude());
		if(localizacion.getLatitude() < -11.3 && localizacion.getLatitude() > -18.5  && localizacion.getLongitude()> -77.9 && localizacion.getLongitude()< -68.6){flagCentroSur=1;}else{flagCentroSur=0;}
		Log.d(TAG, "flagCentroSur: " + flagCentroSur);
		if(flagCentroSur==1){
			for(int i =0;i<cercas.size();i++) {
				//Log.d(TAG, "Ubicacion: " + String.valueOf(localizacion.getLatitude()) +","+String.valueOf(localizacion.getLongitude()));
				//Log.d(TAG, "SEDES: "+ String.valueOf(cercas.get(i).getProvider()) +"  LAT,LON: " + String.valueOf(cercas.get(i).getLatitude()) +","+ String.valueOf(cercas.get(i).getLongitude()));
      /*Log.d(TAG, "DISTANCIA CERCO-PUNTO ESCOGIDO: " + cercas.get(i).distanceTo(localizacion));
      Log.d(TAG, "DISTANCIA CERCANO-PUNTO ESCOGIDO: " + cercas.get(cercano).distanceTo(localizacion));
      Log.d(TAG, "Distancia: " + String.valueOf(cercas.get(i).distanceTo(localizacion)));*/
				if(cercas.get(i).distanceTo(localizacion)<cercas.get(cercano).distanceTo(localizacion)) {
					//Log.d(TAG, "cerco: " + String.valueOf(cercas.get(i)));
					cercano = i;
				}
			}
			Log.d(TAG, "cercano XDXD: " + cercano);
			if(cercano==0){
				cercanofinal=2;//Camana
			}if(cercano==1){
				cercanofinal=4;//Huancayo
			}if(cercano==2){
				cercanofinal=3;//chilca
			}if(cercano==3){
				cercanofinal=6;//Mollendo
			}if(cercano==4){
				cercanofinal=7;//Moquegua
			}if(cercano==5){
				cercanofinal=1;//Arequipa
			}if(cercano==6){
				cercanofinal=5;//Lima
			}
			Log.d(TAG, "SEDE XDXD: " + cercanofinal);
			return cercanofinal;
		}else{
			cercanofinal=0;
			return cercanofinal;
		}
	}
	public static int validarCiudad(Location localizacion){
		List<Location> cercas;
		cercas= new ArrayList<Location>();
		//Camana:  0
		//-16.619113, -72.718658   10km
		Location loc1 = new Location("Sede Camana");
		loc1.setLatitude(-16.619113);
		loc1.setLongitude(-72.718658);
		//Huancayo - chilca:  1
		//-12.074443, -75.201867   4km
		Location loc2 = new Location("Sede Huancayo - Chilca");
		loc2.setLatitude(-12.074443);
		loc2.setLongitude(-75.201867 );
		//Mollendo:  2
		//-17.016865, -72.011091  3km
		Location loc3 = new Location("Sede Mollendo");
		loc3.setLatitude(-17.016865);
		loc3.setLongitude(-72.011091);
		//Moquegua:  3
		//-17.195096, -70.937392  4km
		Location loc4 = new Location("Sede Moquegua");
		loc4.setLatitude(-17.195096);
		loc4.setLongitude(-70.937392);
		//Arequipa:   4
		//-16.399441, -71.540471  8km
		Location loc5 = new Location("Sede Arequipa");
		loc5.setLatitude(-16.399441);
		loc5.setLongitude(-71.540471);
		//Lima: 6
		//-12.092623, -77.011070  30km
		Location loc6 = new Location("Sede Lima");
		loc6.setLatitude(-12.092623);
		loc6.setLongitude(-77.011070);
		cercas.add(loc1);
		cercas.add(loc2);
		cercas.add(loc3);
		cercas.add(loc4);
		cercas.add(loc5);
		cercas.add(loc6);
		int cercano=0;
		for(int i =0;i<cercas.size();i++) {
			// Log.d(TAG, "cerco: " + String.valueOf(cercas.get(i)));
			// Log.d(TAG, "Punto: " + String.valueOf(localizacion));
			Log.d(TAG, "Distancia: " + String.valueOf(cercas.get(i).distanceTo(localizacion)));
			if(cercas.get(i).distanceTo(localizacion)<cercas.get(cercano).distanceTo(localizacion)) {
				Log.d(TAG, "cerco: " + String.valueOf(cercas.get(i)));
				cercano = i;
			}
		}
		return cercano;
	}
	public static void guardarErrores(FileOutputStream fos, String Data, Context context) {
		try{
			OutputStreamWriter osw = new OutputStreamWriter(fos);
			// Escribimos el String en el archivo
			osw.write("\n");
			osw.write(Data);
			osw.flush();
			osw.close();
			// Mostramos que se ha guardado
			Toast.makeText(context, "Guardado", Toast.LENGTH_SHORT).show();
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException ex){
			ex.printStackTrace();
		}
	}
	public static void recuperarErrores(FileInputStream fis, Context context) {
		try{

			InputStreamReader isr = new InputStreamReader(fis);
			char[] inputBuffer = new char[READ_BLOCK_SIZE];
			String s = "";
			int charRead;
			while((charRead = isr.read(inputBuffer)) > 0){
				// Convertimos los char a String
				String readString = String.copyValueOf(inputBuffer, 0, charRead);
				s += readString;
				inputBuffer = new char[READ_BLOCK_SIZE];
				Log.i(TAG, "DATOS TXT: "+ s);
			}
			// Mostramos un Toast con el proceso completado
			Toast.makeText(context, "Cargado", Toast.LENGTH_SHORT).show();
			isr.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException ex){
			ex.printStackTrace();
		}
	}
	public static String MensajeVisa="TERMINOS Y CONDICIONES DE USO DE LA APLICACIÓN\n" +
			"\n" +
			"“PEDIDOS RAPIGAS”\n" +
			"\n" +
			"1.- Descripción general de los bienes y/o servicios comercializados.\n" +
			"Balones de gas licuado de petróleo GLP de 10 Kilos y de 45 Kilos\n" +
			"\n" +
			"2.- Políticas de entrega, las cuales deben contener información sobre lo siguiente (si es que aplica):\n" +
			"Plazos de entrega: Dentro de los 60 minutos siguientes de realizado el pedido, contando con un horarios de entrega de 7am. a 8pm.\n" +
			"\n" +
			"3.- Cobertura de la entrega. \n" +
			"Distritos de Cayma, Yanahuara, Umacollo, Primavera, Vallecito, Piedra Santa, Tahuaycani y zonas aledañas.\n" +
			"\n" +
			"4.- Medios de entrega. Descripción del medio y forma por el cual se entregará la mercadería o se brindará el servicio. \n" +
			"El producto se entrega en vehículos motorizados.\n" +
			"\n" +
			"5.- Modo de confirmación de la entrega. \n" +
			"La entrega se confirma electrónicamente a través de un Smartphone.\n" +
			"\n" +
			"6.- Costos relacionados a la entrega. \n" +
			"No aplica\n" +
			"\n" +
			"7.- Políticas relacionadas a la devolución/cancelación de los productos o servicios adquiridos: \n" +
			"Solo se acepta cambio del producto por fuga del gas LP.\n" +
			"\n" +
			"8.- Políticas de cambios. Cuándo aplican, condiciones, plazos, etc. \n" +
			"Por posibles fugas de gas LP y dentro de las 24 horas siguientes a realizada la entrega, de tener más días deberán de comunicarse al servicio de atención al cliente SAC 054-449494.\n" +
			"\n" +
			"9.- Políticas de cancelación. Declaración si se aceptan devoluciones de mercadería o cancelaciones de servicios y bajo qué condiciones y plazos. \n" +
			"Si se aceptan cancelaciones de pedidos, no se acepta devoluciones, solo cambio del cilindro si presenta fuga.\n" +
			"\n" +
			"10.- Políticas de recepción de reclamos. \n" +
			"Se reciben reclamos vía telefónica, vía correo electrónico y a través del libro de reclamaciones.\n" +
			"\n" +
			"11.- Descripción del procedimiento que deben seguir aquellos clientes que quieran presentar un reclamo relacionado al servicio. \n" +
			"Hacer una llamada telefónica al 054-411727, enviar un e-mail a la dirección electrónica arequipa.zona1@digas.com.pe  o  apersonarse al centro de distribución para anotar la queja en el libro de reclamaciones.\n" +
			"\n" +
			"12.- Políticas de privacidad de la información del consumidor. \n" +
			"Los datos del consumidor no podrán ser utilizados para otros fines, sino exclusivamente para anunciar promociones.\n" +
			"\n" +
			"13.- Restricciones \n" +
			"El horario de atención es de 7:00hr. hasta las 19:00 hr., la restricción es fuera del horario referido.\n" +
			"\n" +
			"14.- Restricciones legales. \n" +
			"Ninguna\n" +
			"\n" +
			"15.- Restricciones determinadas por el comercio y aplicables al servicio. \n" +
			"Ninguna\n" +
			"\n" +
			"16.- Datos de contacto del comercio (los mismos incluidos en la sección “Contáctenos”).\n" +
			"Contactar con el Gestor Comercial de la Zona.\n" +
			"\n" +
			"17.- Verificación de Mayoría de Edad. \n" +
			"No aplica por ser producto de necesidad básica.\n" +
			"\n" +
			"18.- Deberán incluir términos de aceptación del tarjetahabiente indicando que es mayor de edad (mayor a 18 años) en el momento de la compra. Esto aplica para aquellos comercios que vendan los siguientes productos: \n" +
			"No aplica\n";

    public static String mensajeTerminosCondiciones="“PEDIDOS RAPIGAS”\\n\" +\n" +
            "            \"1.\\tACEPTACION Y ALCANCE DE LOS TERMINOS Y CONDICIONES\\n\" +\n" +
            "            \"Estos términos y condiciones de uso, en adelante “términos”, son aplicables para el uso de los servicios ofrecidos por  DIGAS a través de la aplicación para teléfonos móviles denominada PEDIDOS RAPIGAS, en adelante “Aplicación”. El usuario manifiesta  y declara expresarte que acepta todos los términos establecidos en el presente documento con la instalación y uso de la aplicación, la misma que se entenderá como plena e indubitable manifestación de la voluntad y aceptación. Si usted no está de acuerdo con los términos de uso de la aplicación y las políticas de privacidad, no instale la aplicación y/o no realice uso de ella.\\n\" +\n" +
            "            \"2.\\tDEFINICIONES\\n\" +\n" +
            "            \"(i)”Usuarios”:\\n\" +\n" +
            "            \"Usuarios conductores y usuarios clientes, es decir todo aquel que utilice la aplicación de PEDIDOS RAPIGAS\\n\" +\n" +
            "            \"(ii) “Usuario Conductor”:\\n\" +\n" +
            "            \"Aquel usuario que concluye exitosamente el proceso de registro de usuario conductor en la aplicación de forma presencial y acepta participación en la comunidad PEDIDOS RAPIGAS y los términos expresados en el presente documento.  Asimismo el usuario conductor es quien a título personal realiza el servicio de transporte de clientes en la modalidad de reparto.\\n\" +\n" +
            "            \"(iii) “Usuario Cliente”:\\n\" +\n" +
            "            \"Aquel usuario que concluye exitosamente el proceso de registro de Usuario cliente en la aplicación de forma electrónica y acepta su participación en la comunidad PEDIDOS RAPIGAS y los términos expresados en el presente documento.\\n\" +\n" +
            "            \"(iv)”Viaje”\\n\" +\n" +
            "            \"Se entenderá por viaje a aquella prestación del servicio de traslado, transporte o reparto realizada por el usuario conductor a un usuario cliente.\\n\" +\n" +
            "            \"3.\\t¿QUÉ ES PEDIDOS RAPIGAS?\\n\" +\n" +
            "            \"PEDIDOS RAPIGAS es una comunidad virtual que brinda diversos servicios con el propósito de optimizar y brindar valor agregado al requerimiento de  prestaciones de reparto de cilindros de GLP a los usuarios de la comunidad.\\n\" +\n" +
            "            \"Entre los servicios que prestan PEDIDOS RAPIGAS se encuentran:\\n\" +\n" +
            "            \"(i)Geo localización:\\n\" +\n" +
            "            \"Consiste en poner en contacto a usuarios cliente con usuarios conductores cercanos de forma automatizada mediante el uso de la aplicación.\\n\" +\n" +
            "            \"(ii)RECAUDACION Y DISPERSION\\n\" +\n" +
            "            \"Consiste e4n re4caudar y dispersar el dinero que los usuarios clientes desean transferir a los usuarios conductores por vía electrónica en pago por los servicios que prestan.\\n\" +\n" +
            "            \"PEDIDOS RAPIGAS no provee el servicio de transporte de clientes en la modalidad de reparto y/u otra modalidad de transporte de forma directa o indirecta, así como tampoco crea una relación laboral con los usuarios conductores ni con los usuarios clientes\\n\" +\n" +
            "            \"Crea una relación laboral con los usuarios conductores ni con los usuarios clientes\\n\" +\n" +
            "            \"4.\\tREGISTRO Y USO DE LA APLICACIÓN\\n\" +\n" +
            "            \"Registro de usuario conductor:\\n\" +\n" +
            "            \"a)\\tCuando se registra, el usuario conductor acepta proveer  información exacta, veraz, completa y actualizada que se requiera para completar el proceso de registro, pudiendo PEDIDOS RAPIGAS realizar controles que crea convenientes para verificar la veracidad de la misma.\\n\" +\n" +
            "            \"b)\\tEl usuario conductor acepta que para registrarse se le podrá exigir diversas evaluaciones las cuales podrán incluir y no se limitan a:\\n\" +\n" +
            "            \"(i)\\tVerificación de antecedentes policiales, antecedentes judiciales, antecedentes penales, verificación domiciliaria, verificación de record de conductor y aptitud psicológica así como  documentación y estado del vehículo a utilizar.\\n\" +\n" +
            "            \"c)\\tPEDIDOS RAPIGAS no se responsabiliza por cualquier daño como resultado de la pérdida o mal uso de la clave por parte de terceros. E4lususario conductor es el único responsable de ello\\n\" +\n" +
            "            \"d)\\tEl registro de usuario conductor, es personal y no se puede transferir por ningún motivo a terceras personas\\n\" +\n" +
            "            \"e)\\tEl usuarios conductor manifiesta y declara su conformidad para que los datos de su registro sean proporcionados a las autoridades administrativas y judiciales ya sea por requerimiento expreso o por razón del inicio de una investigación a cargo de PEDIDOS RAPIGAS\\n\" +\n" +
            "            \"Registro de Usuario Cliente:\\n\" +\n" +
            "            \"a)\\tCuando registra, el usuario cliente acepta proveer información exacta, completa y actualizada que se requiera para completar el proceso de registro pudiendo PEDIDOS RAPIGAS realizar controles que crea convenientes para verificar la veracidad de la misma.\\n\" +\n" +
            "            \"b)\\tSolo las personas que tienen la capacidad legal de ejercicio están autorizadas a utilizar la aplicación. Las personas que no cuenten con esta capacidad entre ellos los menores de edad, deben ser asistidas por sus representantes legales. En este sentido, la declaración realizada por el usuario cliente al momento de ingresar sus datos en la aplicación se entenderá como una declaración jurada.\\n\" +\n" +
            "            \"c)\\tPEDIDOS RAPIGAS, no se responsabiliza por cualquier daño como resultado de la pérdida o mal uso de la clave por parte de terceros. El usuario cliente es el único responsable por ello.\\n\" +\n" +
            "            \"d)\\tEl registro de usuario cliente es personal y no se puede transferir por ningún motivo a terceras personas\\n\" +\n" +
            "            \"e)\\tEl incumplimiento de alguno de los puntos señalados puede devenir en suspensión temporal o definitiva de las cuentas de los usuarios conductores o usuarios clientes en cualquier momento. La solicitud acumulativa de otros usuarios o el incumplimiento de políticas internas podrán DEVENIR EN SIMILARES RESOLUCIONES.\\n\" +\n" +
            "            \"\\n\" +\n" +
            "            \"5.\\tLIMITACIONES DE LA RESPONSABILIDAD\\n\" +\n" +
            "            \"Los usuarios declaran conocer expresamente  que los usuarios conductores debidamente registrados son quienes proveen de forma exclusiva y a título personal los servicios de traslado, transporte o reparto a los usuarios clientes PEDIDOS RAPIGAS no provee servicios de traslado, transporte o reparto.\\n\" +\n" +
            "            \"Al utilizar la aplicación, los usuarios conductores y los usuarios clientes reconocen y aceptan que PEDIDOS RAPIGAS será responsable por cualquier desperfe3cto ocurrido con los s servicios, los cuales son de forma taxativa los de geo localización, recaudación y dispersión.\\n\" +\n" +
            "            \"Asimismo ambos usuarios reconocen y aceptan que dado que la prestación de servicios de traslado, transporte o reparto son provistos de forma exclusiva y a título personal por los usuarios conductores, PEDIDOS RAPIGAS no podrá ser responsable por lo ocurrido durante la prestación de los mismos incluyendo mas no limitando a la culminación de ellos; sin embargo esto no limita cualquier esfuerzo de PEDIDOS RAPIGAS para ayudar a solucionar cualquier inconveniente suscitado entre ambos usuarios aplicando siempre el principio de la buena fe.\\n\" +\n" +
            "            \"Dado que la aplicación se encuentra en un proceso de constante mejora continua y actualización, PEDIDOS RAPIGAS no puede garantizar que la misma esté disponible sin interrupciones y que siempre esté libre de errores y por tanto no se responsabiliza por posibles perjuicios causados a ambos usuarios por este concepto.\\n\" +\n" +
            "            \"Debido a que el servicio de geo localización se presta en tiempo real, PEDIDOS RAPIGAS no garantiza la disponibilidad de usuarios conductores, lo cual dependerá de la ubicación del usuario cliente y la cercanía de un usuario conductor cuando este utiliza aplicación.\\n\" +\n" +
            "            \"6.\\tOBLIGACIONES Y RESPONSABILIDADES DE LOS USUARIOS\\n\" +\n" +
            "            \"Los usuarios son responsables del uso de la información personal intercambiada mutuamente mediante el uso de la aplicación. Dicha información personal debe ser utilizada únicamente para facilitar el encuentro de ambos.\\n\" +\n" +
            "            \"Los usuarios son responsables del consumo de datos o internet de sus dispositivos en los cuales ejecuta/ utiliza la aplicación. En tal sentido, declaran consentir que l aplicación utilizara y/o consumirá recursos de sus plan de datos y/o internet contratado con su operador de telefonía según el uso que hagan de ella\\n\" +\n" +
            "            \"\\n\" +\n" +
            "            \"\\n\" +\n" +
            "            \"OBLIGACIONES Y REONSABILIDADES DEL USUARIO CONDUCTOR\\n\" +\n" +
            "            \"a)\\tEl usuario conductor certifica y declara que está usando la aplicación por su propia voluntad, este reconoce y acepta las responsabilidades y riesgos por usar la aplicación\\n\" +\n" +
            "            \"b)\\tEl usuario conductor reconoce que PEDIDOSRAPIGAS no tiene ninguna participación en la relación contra actual entre el usuario conductor y el usuario cliente y reconoce y acepta los riesgos a título personal por el servicio y traslado, transporte un reparto brindado por el usuario conductor.\\n\" +\n" +
            "            \"c)\\tEl usuario conductor puede libremente aceptar o rechazar viajes de acuerdo al servicio de geo localización que lo ubica cercano a los usuarios clientes que requieren de sus servicios. El usuario conductor reconoce que PEDIDOS RAPIGAS no se responsabiliza por las demoras, cancelaciones y/o errores de comunicación con el usuario cliente, ni cualquier retraso o incumplimiento durante la prestación del servicio de traslado, transporte o reparto\\n\" +\n" +
            "            \"OBLIGACIONES Y RESPONSABILIDADES  DE4L USUARIO CLIENTE\\n\" +\n" +
            "            \"a)\\tEl usuario cliente puede libremente solicitar o cancelar repartos mediante el servicio de geo localización que ubica los usuarios conductores cercanos según el requerimiento de sus  servicios. El usuario cliente reconoce que PEDIDOS RAPIGAS no se responsabiliza por las demoras, cancelaciones y/o errores de comunicación con el usuario conductor, ni cualquier retraso o incumplimiento durante la prestación del servicio de traslado, transporte o reparto.\\n\" +\n" +
            "            \"b)\\tEl usuario cliente Podrá libremente decidir recibir el producto descargado del vehículo del usuario conductor geo localizado mediante la aplicación\\n\" +\n" +
            "            \"c)\\tSegún disponibilidad técnica, el usuario cliente podrá evaluar el servicio de los usuarios conductores, calificando y comentando libremente. Dicha evaluación será de uso exclusivo de PEDIDOS RAPIGAS con la finalidad de brindar mejor servicio\\n\" +\n" +
            "            \"7.\\tPROCEDIMIENTO DE ATENCION Y RECLAMOS\\n\" +\n" +
            "            \"Para la atención de cualquier reclamo en relación a los servicios prestados por PEDIDOS RAPIGAS, es decir únicamente sobre los servicios de geo localización, recaudación y dispersión, los usuarios podrán presentar su reclamo por cualquiera de las vías que la normativa de protección al consumidor ha establecido, las cuales incluyen pero no se limitan a\\n\" +\n" +
            "            \"-\\tPresentación del reclamos vía correo electrónico a xxxxxxxxx;\\n\" +\n" +
            "            \"-\\tPresentación del reclamo en medio físico a nuestras oficinas ubicadas en calle xxxxxxxx\\n\" +\n" +
            "            \"-\\tEl libro de reclamaciones de la empresa se encuentra a disposición de los usuarios en las oficinas antes indicadas\\n\" +\n" +
            "            \"-\\tContactarse con la línea telefónica de atención al usuario XX – XXXX, para presentar un reclamo y/o conocer el estado del mismo\\n\" +\n" +
            "            \"Los reclamos presentados serán atendidos dentro del plazo legal establecido.\\n\" +\n" +
            "            \"En caso los usuarios clientes tengan algún inconveniente con los servicios prestados por los usuarios conductores, podrán reportar dichos inconvenientes al número de atención al usuario antes referido. PEDIDOS RAPIGAS procurara sus mejores esfuerzos para que los usuarios puedan resolver los inconvenientes de manera amistosa y directa\\n\" +\n" +
            "            \"8.\\tPOLITICA DE PRIVACIDAD Y PROTECCION DE DATOS PERSONALES\\n\" +\n" +
            "            \"De conformidad con la ley N° 29733. Ley de protección de datos personales y su norma reglamentaria D.S.003-2013-JUS y demás normas aplicables (en adelante “la ley”), en los términos vigentes de ambas , d4esde el momento que el usuario conductor y el usuario cliente haya declarado la aceptación expresa del tratamiento de sus datos personales mediante la realización de cliqueo/ pinchado (u otro mecanismo utilizado) dispuesto para la autorización mencionada, PE4DIDOS RAPIGAS garantiza la seguridad y confidencialidad en el tratamiento de los datos de carácter personal facilitados por los usuarios de conformidad  con los dispuesto en la ley.\\n\" +\n" +
            "            \"Toda información entregada a PEDIDOS RAPIGAS mediante la aplicación y otra forma de registro que utilicen el usuario conductor y el usuario cliente, será objeto de tratamiento automatizado e incorporada en una o más bases de datos de las que PEDIDOS RAPIGAS será titular y responsable, conforme a los términos previstos por la ley.\\n\" +\n" +
            "            \"El usuario conductor y el usuario cliente otorgan de forma personal cada uno y de forma independiente, autorización expresa e inequívoca a PEDIDOS RAPIGAS para realizar tratamiento y hacer uso de la información personal este proporcione a PEDIDOS RAPIGAS cuando acce4da a la aplicación y/u otra forma de registro que se utilice, participe en promociones comerciales, envié consultas o comunique incidencias  y en general cualquier interacción en la aplicación, además de la información que se derive del uso de los productos y/o servicios que pudiera tener contratados con PEDIDOS RAPIGAS y de cualquier información pública o que pudiera recoger a través de fuentes de acceso público, incluyendo aquellos a los que PEDIDOS RAPIGAS tenga acce4so como consecuencia de su navegación por la aplicación (en adelante “la información”) para las finalidades de envió de comunicaciones comerciales, comercialización de productos y servicios, y del mantenimiento de su relación contractual con PEDIDOS RAPIGAS. La navegación en la aplicación, la participación en promociones comerciales y  cualquier otra interacción implica el consentimiento expreso e inequívoco del usuario conductor y el usuario cliente  para la sesión de sus datos personales a PEDIDOS RAPIGAS. El usuario conductor y el usuario cliente reconocen y aceptan que PEDIDOS RAPIGAS podrá ceder sus datos personales a cualquier tercero, siempre que sea necesaria su participación para cumplir con la prestación de servicios y comercialización de productos y servicios\\n\" +\n" +
            "            \"PEDIDOS RAPIGAS podrá ceder, en su caso, la información a sus empresas  subsidiarias, filiales asociadas afiliadas o miembros del grupo económico al cual pertenece y/o terceros con los que estas mantengan una relación contractual, supuesto en el cual sus datos serán almacenados en los sistemas informáticos de cualquiera de ellos. En todo caso, PEDIDOS RAPIGAS garantiza el mantenimiento de la confidencialidad y el tratamiento seguro de la información en estos casos. El uso de la información por las empresas antes indicadas se circunscribirá a los fines contenidos en el este documento.\\n\" +\n" +
            "            \"La política de privacidad de PEDIDOS RAPIGAS le asegura al usuario el ejercicio de los derechos de la información acceso, actualización, inclusión y revocación del consentimiento, en los términos establecidos en la ley. El cualquier momento el usuario conductor y el usuario cliente tendrán el derecho a solicitar a PEDIDOS RAPIGAS el ejercicio de los derechos que le confiere la ley, así como la revocación de su consentimiento según lo previsto en la ley.\\n\" +\n" +
            "            \"PEDIDOS RAPIGAS garantiza la confidencialidad en el tratamiento de los datos de carácter personal, así como haber adoptado los niveles de seguridad de protección de los datos personales, instalado todos los medios y adoptado todas las medidas técnicas organizativas y legales a su alcance que garanticen la seguridad y eviten la alteración, perdida, tratamiento o acceso no autorizado a los datos personales.\\n\" +\n" +
            "            \"Para cualquier consulta sobre los alcances de la política sobre protección de datos personales o en caso los usuarios deseen ejercitar los derechos de acceso no autorizado a los datos personales\\n\" +\n" +
            "            \"Para cualquier consulta sobre los alcances de la política sobre protección de datos personales o en caso los usuarios deseen ejercitar los derechos de acceso actualización, inclusión, rectificación, supresión o cancelación, oposición u otros contemplados en la ley, sobre sus datos personales, podrán enviar un correo electrónico a (zxxxxxxx).\\n\" +\n" +
            "            \"\\n\" +\n" +
            "            \"9.\\tSANCIONES\\n\" +\n" +
            "            \"PEDIDOS RAPIGAS podrá notificar, suspender o cancelar temporal o permanentemente, la cuenta de cualquier usuario en cualquier momento y tomar acciones legales si, se dan los casos de violación cualquiera de las representaciones, garantías y obligaciones contenidas en estos términos o cualquier política o regla adyacente a la misma, así como también si detecta la realización de las prácticas engañosas o fraudulentas, o si PEDIDOS RAPIGAS concluye que las actividades y actitudes han causado o pueden causar daño a los demás usuarios o al equipo de PEDIDOS RAPIGAS. El usuario no tendrá derecho a ninguna indemnización o compensación por la suspensión temporal o definitiva de su cuenta en la aplicación\\n\" +\n" +
            "            \"\\n\" +\n" +
            "            \"10.\\tLICENCIA\\n\" +\n" +
            "            \"a)\\tPEDIDOS RAPIGAS brinda a ambos usuarios una licencia limitada, personal, no exclusiva, intransferible, no comercial y totalmente revocable para utilizar la aplicación, en conformidad y por acuerdo de los términos contenidos en este documento  PEDIDOS RAPIGAS se reserva todos los derechos sobre la aplicación no expresamente concedidos aquí.\\n\" +\n" +
            "            \"b)\\tPEDIDOS RAPIGAS no se hace responsable de ningún daño sufrido por el usuario que realiza una copia, transferencia, distribución o uso de cualquier contenido de la aplicación protegida, violando los derechos de los demás\\n\" +\n" +
            "            \"\\n\" +\n" +
            "            \"11.\\tCONDICIONES GENERALES\\n\" +\n" +
            "            \"a)\\tLos términos no generan ninguna sociedad, franquicia o relación laboral entre, los usuarios y PEDIDOS RAPIGAS.\\n\" +\n" +
            "            \"b)\\tLos términos pueden ser cambiados por PEDIDOS RAPIGAS en cualquier momento y sin previo aviso. Los cambios serán obligatorios automáticamente en la fecha de publicación de la nueva versión en la página web: xxxxxxxxxx\\n\" +\n" +
            "            \"c)\\tLos presentes términos se regirán e interpretaran de conformidad con la legislación peruana y cualquier disputa y/o controversia entre los usuarios y PEDIDOS RAPIGAS y que no pueda ser resuelta de forma directa y/o a través de los mecanismos de conciliación legales, será sometida a la jurisdicción de los jueces y tribunales de lima cercado.\\n\" +\n" +
            "            \"d)\\tAl registrarse EN LA APLICACIÓN Y ACEPTAR LOS TERMINOS HACIENDO CLIC Y/O TAP EN LA OPCION “Acepto los términos de uso”, el usuario cliente declara automáticamente conocer y se compromete a cumplir con estos términos y todas las demás políticas y normas disponibles en el sistema.\\n\" +\n" +
            "            \"e)\\t Los cambios en los presentes términos podrán ser comunicados mediante la aplicación así como por medio del correo electrónico provisto por ambos usuarios en el momento del registro.\\n\" +\n" +
            "            \"\\n\" +\n" +
            "            \"12.\\tDERECHOS DE PROPIEDAD INTELECTUAL\\n\" +\n" +
            "            \"PEDISO RAPIGAS es titular de la propiedad intelectual de la aplicación, los cuales incluyen de manera enunciativa y no limitada: (i) el código fuente, (ii) el código objeto; (iii) marcas; (iv) nombres comerciales;  (v) diseños; (vi) imágenes; (vii) videos; (viii) fotografías; (ix) lemas o slogans; y (x) finalmente cualquier creación que sea producto de la originalidad y se encuentra protegida por la decisión 486 de la comunidad andina, el decreto legislativito 1075, la decisión 351 de la comunidad andina el decreto legislativo 822 y demás tratados internacionales y normativa aplicable.\\n\" +\n" +
            "            \"Por medio de los presentes términos, la descarga o uso de la aplicación, PEDIDOS RAPIGAS no transfiere ningún derecho de propiedad intelectual a los usuarios.\\n";
}
