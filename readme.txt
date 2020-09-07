Aplicación
Nombre: REPARTOCLIENTE

--------------
KeyStore
Ruta: 
Nombre: REPARTOCLIENTE
Clave: 123456
Alias: REPARTOCLIENTE
Clave: 123456 

Certificate fingerprints:
	MD5: 59:28:93:67:C2:E0:EB:4F:92:2F:2A:D6:02:10:49:44
        SHA1: 7A:75:01:C0:7A:00:39:A5:C3:14:0C:ED:9E:DC:D0:C0:CA:80:13:1A
        SHA256: 9F:9A:B1:2F:E0:5D:7B:F7:3D:03:30:43:3F:C7:F9:BE:A9:2F:9A:42:B3:53:C9:01:AA:B8:0A:3D:BE:10:42:DA
        Nombre del Algoritmo de Firma: SHA256withRSA
Version: 3

--------------
Cuenta para publicar
Email: androiddeveloper@controltotalperu.com
Clave: /solicitar a jefe de sistemas/

keytool -exportcert -list -v -alias REPARTOCLIENTE -keystore G:\ANDROID2\PROYECTOS\REPARTOCLIENTE\REPARTOCLIENTE.jks


/////////////////DATOS ORIGINALES DEL NEGOCIO//////////////////////
/*merchantId = "535704501"; // edtMerchantId.getText().toString();
//userTokenId = useToken ? (isTesting ? "269c8764-2f39-453c-b11f-bbad462ecd71" : "41f4712e-3821-4e70-914d-d92b61b4cba1") : null;
endpoint = "https://apice.vnforapps.com/api.ecommerce/api/v1/ecommerce/token/"+merchantId;
accessKeyId = "AKIAJLT75GDEKBG4R3TA";
secretAccess = "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";
//nextCounterUri = String.format("https://api.vnforapps.com/api.tokenization/api/v2/merchant/%s/nextCounter", merchantId);
nextCounterUri = String.format(URLPRO+"/merchant/%s/nextCounter", merchantId);*/

/////////////////DATOS DE PRUEBA PARA VERIFICAR EL FUNCIONAMIENTO DE VISA/////////////////////
/*merchantId = "515776502"; // edtMerchantId.getText().toString();
//userTokenId = useToken ? (isTesting ? "269c8764-2f39-453c-b11f-bbad462ecd71" : "41f4712e-3821-4e70-914d-d92b61b4cba1") : null;
endpoint = "https://apice.vnforapps.com/api.ecommerce/api/v1/ecommerce/token/"+merchantId;
accessKeyId = "AKIAISPFEE2QMRPG3UUQ";
secretAccess = "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";
nextCounterUri = String.format(URLDEV+"/merchant/%s/nextCounter", merchantId);*/
