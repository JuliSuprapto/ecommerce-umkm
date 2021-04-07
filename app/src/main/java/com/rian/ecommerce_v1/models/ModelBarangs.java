package com.rian.ecommerce_v1.models;

public class ModelBarangs {

    String _id, idPembeli, idUser, namaBarang, deksripsiBarang, hargBarang, stokBarang, fotobarang;

    public ModelBarangs(){

    }

    public ModelBarangs(String _id, String idPembeli, String idUser, String namaBarang, String deksripsiBarang, String hargBarang, String stokBarang, String fotobarang) {
        this._id = _id;
        this.idPembeli = idPembeli;
        this.idUser = idUser;
        this.namaBarang = namaBarang;
        this.deksripsiBarang = deksripsiBarang;
        this.hargBarang = hargBarang;
        this.stokBarang = stokBarang;
        this.fotobarang = fotobarang;
    }

    public String get_id() { return _id; }

    public void set_id(String _id) { this._id = _id; }

    public String getIdPembeli() { return idPembeli; }

    public void setIdPembeli(String idPembeli) { this.idPembeli = idPembeli; }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getNamaBarang() {
        return namaBarang;
    }

    public void setNamaBarang(String namaBarang) {
        this.namaBarang = namaBarang;
    }

    public String getDeksripsiBarang() {
        return deksripsiBarang;
    }

    public void setDeksripsiBarang(String deksripsiBarang) { this.deksripsiBarang = deksripsiBarang; }

    public String getHargBarang() {
        return hargBarang;
    }

    public void setHargBarang(String hargBarang) {
        this.hargBarang = hargBarang;
    }

    public String getStokBarang() {
        return stokBarang;
    }

    public void setStokBarang(String stokBarang) {
        this.stokBarang = stokBarang;
    }

    public String getFotobarang() {
        return fotobarang;
    }

    public void setFotobarang(String fotobarang) {
        this.fotobarang = fotobarang;
    }

}
