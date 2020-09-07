package com.efienza.cliente.common;

import android.app.Application;

public class GlobalClass extends Application {
    private String name;
    private String email;
    private String titulo;
    private String dorigen;
    private String ddestino;
    private String pago;
    private String detalle = "";
    private String referencia_origen;
    private String referencia_destino;
    private int ado = 0;
    private float cprod = 0;
    private String fecha = "";
    private String hora = "";
/////monto langitud latitud origen destino float monto con el que paga
    private Float longitudo;
    private Float latitudo;
    private Float longitudd;

    public void GlobalClass(){

    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public Integer getEstado_seguimiento() {
        return estado_seguimiento;
    }

    public void setEstado_seguimiento(Integer estado_seguimiento) {
        this.estado_seguimiento = estado_seguimiento;
    }

    private Integer estado_seguimiento;

    public Float getCon_cuanto_paga() {
        return con_cuanto_paga;
    }

    public void setCon_cuanto_paga(Float con_cuanto_paga) {
        this.con_cuanto_paga = con_cuanto_paga;
    }

    private Float con_cuanto_paga;

    public Float getLongitudo() {
        return longitudo;
    }

    public void setLongitudo(Float longitudo) {
        this.longitudo = longitudo;
    }

    public Float getLatitudo() {
        return latitudo;
    }

    public void setLatitudo(Float latitudo) {
        this.latitudo = latitudo;
    }

    public Float getLongitudd() {
        return longitudd;
    }

    public void setLongitudd(Float longitudd) {
        this.longitudd = longitudd;
    }

    public Float getLatitudd() {
        return latitudd;
    }

    public void setLatitudd(Float latitudd) {
        this.latitudd = latitudd;
    }

    private Float latitudd;

    public Float getTotal() {
        return total;
    }

    public void setTotal(Float total) {
        this.total = total;
    }

    private Float total;

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    private String usuario;



    public float getCprod() {
        return cprod;
    }

    public void setCprod(float cprod) {
        this.cprod = cprod;
    }

    public float getCprot() {
        return cprot;
    }

    public void setCprot(float cprot) {
        this.cprot = cprot;
    }

    private float cprot = 0;

    public float getAddd() {
        return addd;
    }

    public void setAddd(int addd) {
        this.addd = addd;
    }

    private int addd = 0;

    public int getAdo() {
        return ado;
    }

    public void setAdo(int ado) {
        this.ado = ado;
    }



    public String getReferencia_origen() {
        return referencia_origen;
    }

    public void setReferencia_origen(String referencia_origen) {
        this.referencia_origen = referencia_origen;
    }

    public String getReferencia_destino() {
        return referencia_destino;
    }

    public void setReferencia_destino(String referencia_destino) {
        this.referencia_destino = referencia_destino;
    }

    public int getDdo() {
        return ddo;
    }

    public void setDdo(int ddo) {
        this.ddo = ddo;
    }

    private int ddo=0;

    public int getDd() {
        return dd;
    }

    public void setDd(int dd) {
        this.dd = dd;
    }

    private int dd= 0;

    public int getFdirecciones() {
        return fdirecciones;
    }

    public void setFdirecciones(int fdirecciones) {
        this.fdirecciones = fdirecciones;
    }

    private int fdirecciones;

    public String getPago() {
        return pago;
    }

    public void setPago(String pago) {
        this.pago = pago;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDorigen() {
        return dorigen;
    }

    public void setDorigen(String dorigen) {
        this.dorigen = dorigen;
    }

    public String getDdestino() {
        return ddestino;
    }

    public void setDdestino(String ddestino) {
        this.ddestino = ddestino;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
