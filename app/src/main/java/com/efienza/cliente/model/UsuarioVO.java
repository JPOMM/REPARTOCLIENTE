package com.efienza.cliente.model;
/**
 * Created by John Manchego Medina on 13/07/2016.
 */
public class UsuarioVO {
    int CodUsuario;
    int flag;
    String login;
    String Contrasenha;
    String nombre;
    String apePat;
    String apeMat;
    String email;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getContrasenha() {
        return Contrasenha;
    }

    public void setContrasenha(String contrasenha) {
        Contrasenha = contrasenha;
    }

    public int getCodUsuario() {
        return CodUsuario;
    }

    public void setCodUsuario(int codUsuario) {
        CodUsuario = codUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApePat() {
        return apePat;
    }

    public void setApePat(String apePat) {
        this.apePat = apePat;
    }

    public String getApeMat() {
        return apeMat;
    }

    public void setApeMat(String apeMat) {
        this.apeMat = apeMat;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}