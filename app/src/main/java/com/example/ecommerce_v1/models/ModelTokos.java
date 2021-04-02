package com.example.ecommerce_v1.models;

public class ModelTokos {

    String idToko, idUser, namaToko, alamat, logoToko, deskripsi;

    public ModelTokos(){

    }

    public ModelTokos(String idToko, String idUser, String namaToko, String alamat, String logoToko, String deskripsi) {
        this.idToko = idToko;
        this.idUser = idUser;
        this.namaToko = namaToko;
        this.alamat = alamat;
        this.logoToko = logoToko;
        this.deskripsi = deskripsi;
    }

    public String getIdToko() {
        return idToko;
    }

    public void setIdToko(String idToko) {
        this.idToko = idToko;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getNamaToko() {
        return namaToko;
    }

    public void setNamaToko(String namaToko) {
        this.namaToko = namaToko;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getLogoToko() {
        return logoToko;
    }

    public void setLogoToko(String logoToko) {
        this.logoToko = logoToko;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

}
