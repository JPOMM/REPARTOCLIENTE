package com.efienza.cliente.fragement;

public class DataModel {

    public String cDireccion;
    public Double nLat;
    public Double nLon;
    public String cReferencia;

    DataModel(String cDireccion, Double nLat,Double nLon,String cReferencia) {
        this.cDireccion=cDireccion;
        this.nLat = nLat;
        this.nLon = nLon;
        this.cReferencia = cReferencia;
    }

    public String getcDireccion() {
        return cDireccion;
    }

    public void setcDireccion(String cDireccion) {
        this.cDireccion = cDireccion;
    }

    public Double getnLat() {
        return nLat;
    }

    public void setnLat(Double nLat) {
        this.nLat = nLat;
    }

    public Double getnLon() {
        return nLon;
    }

    public void setnLon(Double nLon) {
        this.nLon = nLon;
    }

    public String getcReferencia() {
        return cReferencia;
    }

    public void setcReferencia(String cReferencia) {
        this.cReferencia = cReferencia;
    }
}