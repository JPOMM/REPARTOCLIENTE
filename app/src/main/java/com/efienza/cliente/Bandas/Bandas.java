package com.efienza.cliente.Bandas;

/**
 * Created by PC-DESARROLLO on 12/01/2016.
 */
public class Bandas {
    public String titleCodPedido;
    public String titleProducto;
    public String titleCantidad;
    public String titleDistancia;
    public String titleLatitud;
    public String titleLongitud;
    public String titleTelefono;
    public String titleFecha;
    public String titleNombreApe;
    public String titleDireccion;
    public String titleUsuPedido;
    public String titleReferencia;

    public String titleCodProm;

    public String titletipoPagoPedido;
    public String titlemontoPagoPedido;
    public String titletipoPagoDelivery;
    public String titlemontoPagoDelivery;

    public Bandas(){
        super();
    }

    public Bandas(String titleCodPedido, String titleProducto, String titleCantidad, String titleDistancia, String titleLatitud, String titleLongitud, String titleTelefono,
                  String titleFecha, String titleNombreApe, String titleDireccion, String titleUsuPedido, String titleReferencia, String titleCodProm,
                  String titletipoPagoPedido,String titlemontoPagoPedido,String titletipoPagoDelivery,String titlemontoPagoDelivery) {
        this.titleCodPedido = titleCodPedido;
        this.titleProducto = titleProducto;
        this.titleCantidad = titleCantidad;
        this.titleDistancia=titleDistancia;
        this.titleLatitud=titleLatitud;
        this.titleLongitud=titleLongitud;
        this.titleTelefono=titleTelefono;
        this.titleFecha=titleFecha;
        this.titleNombreApe=titleNombreApe;
        this.titleDireccion=titleDireccion;
        this.titleUsuPedido=titleUsuPedido;
        this.titleReferencia=titleReferencia;

        this.titleCodProm=titleCodProm;

        this.titletipoPagoPedido=titletipoPagoPedido;
        this.titlemontoPagoPedido=titlemontoPagoPedido;
        this.titletipoPagoDelivery=titletipoPagoDelivery;
        this.titlemontoPagoDelivery=titlemontoPagoDelivery;

    }
}

