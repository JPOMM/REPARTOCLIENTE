package com.efienza.cliente.Bandas;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.efienza.cliente.R;

public class BandasAdapter extends ArrayAdapter<Bandas> {
    Context context;
    int layoutid;
    Bandas data[];

    public BandasAdapter(Context context, int resource, Bandas[] data) {
        super(context, resource,data);
        this.context=context;
        this.layoutid=resource;
        this.data=data;
    }

    public View getView(int posicion, View converView, ViewGroup padre){
        View row=converView;
        BandasHolder holder=null;
        if(row==null){
            LayoutInflater inflater= ((Activity)context).getLayoutInflater();
            row= inflater.inflate(layoutid,padre,false);
            holder=new BandasHolder();
            holder.textoCodPedido= (TextView) row.findViewById(R.id.textCodigo);
            holder.textoProducto= (TextView) row.findViewById(R.id.textProducto);
            holder.textoCantidad= (TextView) row.findViewById(R.id.textCantidad);
            holder.textoDistancia= (TextView) row.findViewById(R.id.textDistancia);
            holder.textoLatitud= (TextView) row.findViewById(R.id.textLatitud);
            holder.textoLongitud= (TextView) row.findViewById(R.id.textLongitud);
            holder.textoTelefono= (TextView) row.findViewById(R.id.textTelefono);
            holder.textoFecha= (TextView) row.findViewById(R.id.textFecha);
            holder.textoNombreApe= (TextView) row.findViewById(R.id.textNombreApellido);
            holder.textoDireccion= (TextView) row.findViewById(R.id.textDireccion);
            holder.textUsuPedido= (TextView) row.findViewById(R.id.textUsuPedido);
            holder.textReferencia= (TextView)row.findViewById(R.id.textReferencia);

            holder.textCodProm= (TextView)row.findViewById(R.id.textCodProm);


            holder.textTTTipoPagoPedido= (TextView)row.findViewById(R.id.textTTTipoPagoPedido);
            holder.textTTMontoPagoPedido= (TextView)row.findViewById(R.id.textTTMontoPagoPedido);
            holder.textTTTipoPagoDelivery= (TextView)row.findViewById(R.id.textTTTipoPagoDelivery);
            holder.texTTMmontoPagoDelivery= (TextView)row.findViewById(R.id.texTTMmontoPagoDelivery);

            row.setTag(holder);
        }
        else{
            holder = (BandasHolder) row.getTag();
        }
        Bandas bandas=data[posicion];
        holder.textoCodPedido.setText(bandas.titleCodPedido);
        holder.textoProducto.setText(bandas.titleProducto);
        holder.textoCantidad.setText(bandas.titleCantidad);
        holder.textoDistancia.setText(bandas.titleDistancia);
        holder.textoLatitud.setText(bandas.titleLatitud);
        holder.textoLongitud.setText(bandas.titleLongitud);
        holder.textoTelefono.setText(bandas.titleTelefono);
        holder.textoFecha.setText(bandas.titleFecha);
        holder.textoNombreApe.setText(bandas.titleNombreApe);
        holder.textoDireccion.setText(bandas.titleDireccion);
        holder.textUsuPedido.setText(bandas.titleUsuPedido);
        holder.textReferencia.setText(bandas.titleReferencia);

        holder.textCodProm.setText(bandas.titleCodProm);

        holder.textTTTipoPagoPedido.setText(bandas.titletipoPagoPedido);
        holder.textTTMontoPagoPedido.setText(bandas.titlemontoPagoPedido);
        holder.textTTTipoPagoDelivery.setText(bandas.titletipoPagoDelivery);
        holder.texTTMmontoPagoDelivery.setText(bandas.titlemontoPagoDelivery);


        return row;
    }

    static class BandasHolder{
        TextView textoCodPedido;
        TextView textoProducto;
        TextView textoCantidad;
        TextView textoDistancia;
        TextView textoLatitud;
        TextView textoLongitud;
        TextView textoTelefono;
        TextView textoFecha;
        TextView textoNombreApe;
        TextView textoDireccion;
        TextView textUsuPedido;
        TextView textReferencia;

        TextView textCodProm;

        TextView textTTTipoPagoPedido;
        TextView textTTMontoPagoPedido;
        TextView textTTTipoPagoDelivery;
        TextView texTTMmontoPagoDelivery;

    }

}

