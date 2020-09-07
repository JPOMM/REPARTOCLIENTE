package com.efienza.cliente.common;

import android.util.Log;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;
public class JSONParser {


    private  String cookie="";

    public  String getResponse(String url,String cook)
    {
        //Log.d("ALERTA", "URL: " + url);
        //Log.d("ALERTA", "COOK: " + cook);
        String resultado=null;
        DefaultHttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.RFC_2109);
        HttpGet getRequest = new HttpGet(url);
        getRequest.setHeader("Accept", "application/json");
        getRequest.setHeader("Accept-Encoding", "gzip"); //
        getRequest.setHeader("Cookie", "JSESSIONID=" +cook+"=/");
        try {
            //Log.d("ALERTA", "RESPONSE XD: " + getRequest.toString());
            HttpResponse response = (HttpResponse) httpclient.execute(getRequest);
            //Log.d("ALERTA", "RESPONSE XD 222: " + response.toString());
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream instream = entity.getContent();
                Header contentEncoding = response.getFirstHeader("Content-Encoding");
                if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
                    instream = new GZIPInputStream(instream);
                }
                String result = readStream(instream);
                instream.close();
                resultado=result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }
        return resultado;
    }

    public int getResponseStatus(String url){

        Log.d("ALERTA", "URL: " + url);
        int status=0;
        DefaultHttpClient httpclient = new DefaultHttpClient();
        //httpclient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.RFC_2109);
        HttpGet getRequest = new HttpGet(url);
        getRequest.setHeader("Accept", "application/json");
        getRequest.setHeader("Accept-Encoding", "gzip"); //
        //getRequest.setHeader("Cookie", "JSESSIONID=" +cook+"=/");
        try {
            HttpResponse response = (HttpResponse) httpclient.execute(getRequest);
            status= response.getStatusLine().getStatusCode();
        } catch (Exception e) {
            e.printStackTrace();
            return status;
        }
        return status;

    }

    public String getResponse(String url)
    {
        Log.d("ALERTA", "URL: " + url);
        String resultado=null;
        DefaultHttpClient httpclient = new DefaultHttpClient();
        //httpclient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.RFC_2109);
        HttpGet getRequest = new HttpGet(url);
        getRequest.setHeader("Accept", "application/json");
        getRequest.setHeader("Accept-Encoding", "gzip"); //
        //getRequest.setHeader("Cookie", "JSESSIONID=" +cook+"=/");
        try {
            HttpResponse response = (HttpResponse) httpclient.execute(getRequest);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream instream = entity.getContent();
                Header contentEncoding = response.getFirstHeader("Content-Encoding");

                if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
                    instream = new GZIPInputStream(instream);
                }
                String result = readStream(instream);
                instream.close();
                resultado=result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }
        return resultado;
    }

    public  String postResponseExterno(String url,String objson)
    {
        HttpClient hc = new DefaultHttpClient();
        String message;
        String resultado=null;
        HttpPost p = new HttpPost(url);
        try {
            message = objson;
            p.setEntity(new StringEntity(message, "UTF8"));
            p.setHeader("Content-type", "application/json");
            HttpResponse resp = hc.execute(p);
            InputStream instream = resp.getEntity().getContent();
            Header contentEncoding = resp.getFirstHeader("Content-Encoding");
            if (contentEncoding != null
                    && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
                instream = new GZIPInputStream(instream);
            }

            String codigo="";
            codigo=resp.getStatusLine().getReasonPhrase();
            String result = readStream(instream);
            instream.close();
            resultado=result;
            resultado=resultado+"|"+codigo;
            Log.d("BOLETAFACTURAPROMOTOR","resultado: "+resultado);
            Log.d("Status line", "" + resp.getStatusLine().getStatusCode());
        } catch (Exception e) {
            Log.d("BOLETAFACTURAPROMOTOR","Exception  eee: "+e.toString());
        }
        return resultado;
    }

    public  String postResponse(String url,String objson)
    {
        String resultado=null;
        DefaultHttpClient httpclient = new DefaultHttpClient();
        //httpclient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.RFC_2109);
        HttpPost getRequest = new HttpPost(url);
        getRequest.setHeader("Accept", "application/json");
        // Use GZIP encoding
        getRequest.setHeader("Accept-Encoding", "gzip"); //
        //getRequest.setHeader("Cookie", "JSESSIONID="+cook+"=/");
        //getRequest.setHeader("Set-Cookie", "");
        //
        Log.d("BOLETAFACTURAPROMOTOR", "url: "+url);
        Log.d("BOLETAFACTURAPROMOTOR", "objson: "+objson);
        String JSONID =null;
        try {
            StringEntity params = new StringEntity(objson);
            Log.d("BOLETAFACTURAPROMOTOR", "params: "+params.toString());
            HttpResponse response = (HttpResponse) httpclient.execute(getRequest);
            HttpEntity entity = response.getEntity();
            response.setEntity(params);
            if (entity != null) {
                InputStream instream = entity.getContent();
                Header contentEncoding = response.getFirstHeader("Content-Encoding");
                if (contentEncoding != null
                        && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
                    instream = new GZIPInputStream(instream);
                }
                //Header header=
                //cookie=	response.getLastHeader("Set-Cookie").toString();
                String codigo="";
                //codigo= response.getStatusLine().getStatusCode();
                codigo=response.getStatusLine().getReasonPhrase();
                //HeaderElement[] headerlements=header.getElements();
                //JSONID = headerlements[0].getValue();
                //convert content stream to a String
                String result = readStream(instream);
                instream.close();
                resultado=result;
                resultado=resultado+"|"+codigo;
                // Log.i("JSON", result);
            }
        } catch (Exception e) {
            resultado=null;
            e.printStackTrace();
            Log.d("BOLETAFACTURAPROMOTOR", "ERROR JSON ARCHIVO: "+ e.toString());
            return e.toString();
        }

        return resultado;
    }


    public  String getResponsePestillos(String url,String cook)
    //public  String getResponse(String url)
    {

        String resultado=null;
        DefaultHttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.RFC_2109);
        HttpGet getRequest = new HttpGet(url);
        getRequest.setHeader("Accept", "application/json");
        // Use GZIP encoding
        getRequest.setHeader("Accept-Encoding", "gzip"); //
        getRequest.setHeader("Cookie", "JSESSIONID="+cook+"=/");
        //getRequest.setHeader("Set-Cookie", "");
        //

        String JSONID =null;
        try {

            HttpResponse response = (HttpResponse) httpclient.execute(getRequest);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream instream = entity.getContent();
                Header contentEncoding = response
                        .getFirstHeader("Content-Encoding");
                if (contentEncoding != null
                        && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
                    instream = new GZIPInputStream(instream);
                }
                //Header header=
                //cookie=	response.getLastHeader("Set-Cookie").toString();


                //HeaderElement[] headerlements=header.getElements();
                //JSONID = headerlements[0].getValue();
                //convert content stream to a String
                String result = readStream(instream);
                instream.close();
                resultado=result;

                // Log.i("JSON", result);
                //resultado=	response.getLastHeader().toString();
                resultado=response.getStatusLine().toString();
                //resultado=response.getStatusLine().toString()+":"+result;
            }
        } catch (Exception e) {
            resultado=null;
            e.printStackTrace();
            return e.toString();
        }

        return resultado;
    }


    public  String getCookie(String url)
    {

        String resultado="";
        DefaultHttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.RFC_2109);
        HttpPost getRequest = new HttpPost(url);
        getRequest.setHeader("Accept", "application/json");
        getRequest.setHeader("Accept-Encoding", "gzip");
        getRequest.setHeader("Cookie", "");
        //
        HttpResponse response =null;
        String JSONID =null;
        try {

            response = (HttpResponse) httpclient.execute(getRequest);
            Log.i("ALERTA"," 0: "+ response);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream instream = entity.getContent();
                Header contentEncoding = response
                        .getFirstHeader("Content-Encoding");
                if (contentEncoding != null
                        && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
                    instream = new GZIPInputStream(instream);
                }
                //Header header=

                int codigo_login=0;
                codigo_login= response.getStatusLine().getStatusCode();
                Log.i("ALERTA"," 0: "+ codigo_login);
                if(codigo_login==200)
                {
                    cookie=	response.getLastHeader("Set-Cookie").toString();
                    cookie=cookie+":"+String.valueOf(codigo_login);
                }
                else
                {
                    cookie="a:b=c;d=e:"+String.valueOf(codigo_login);
                }
                //HeaderElement[] headerlements=header.getElements();
                //JSONID = headerlements[0].getValue();
                //convert content stream to a String
                String result = readStream(instream);
                instream.close();
                resultado=result;
            }
        } catch (Exception e) {
            resultado=null;
            e.printStackTrace();
            return e.toString();
        }


        return cookie;
    }



    private static String readStream(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();


    }
}
	
	
	
	

