package com.rian.ecommerce_v1.models;

public class ModelKeranjang {

    String _id, idToko, idPembeli, idBarang, namaBarang, deksripsiBarang, hargaBarang, qty, fotobarang, namaToko, totalHarga, grandTotal, status;

    public ModelKeranjang() {

    }

    public ModelKeranjang(String _id, String idToko, String idPembeli, String idBarang, String namaBarang, String deksripsiBarang, String hargaBarang, String qty, String fotobarang, String namaToko, String totalHarga, String grandTotal, String status) {
        this._id = _id;
        this.idToko = idToko;
        this.idPembeli = idPembeli;
        this.idBarang = idBarang;
        this.namaBarang = namaBarang;
        this.deksripsiBarang = deksripsiBarang;
        this.hargaBarang = hargaBarang;
        this.qty = qty;
        this.fotobarang = fotobarang;
        this.namaToko = namaToko;
        this.totalHarga = totalHarga;
        this.grandTotal = grandTotal;
        this.status = status;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getIdToko() { return idToko; }

    public String getIdPembeli() { return idPembeli; }

    public void setIdPembeli(String idPembeli) { this.idPembeli = idPembeli; }

    public void setIdToko(String idToko) { this.idToko = idToko; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public String getIdBarang() {
        return idBarang;
    }

    public void setIdBarang(String idBarang) {
        this.idBarang = idBarang;
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

    public String getHargaBarang() {
        return hargaBarang;
    }

    public void setHargaBarang(String hargaBarang) {
        this.hargaBarang = hargaBarang;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getFotobarang() {
        return fotobarang;
    }

    public void setFotobarang(String fotobarang) {
        this.fotobarang = fotobarang;
    }

    public String getNamaToko() {
        return namaToko;
    }

    public void setNamaToko(String namaToko) {
        this.namaToko = namaToko;
    }

    public String getTotalHarga() {
        return totalHarga;
    }

    public void setTotalHarga(String totalHarga) {
        this.totalHarga = totalHarga;
    }

    public String getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(String grandTotal) {
        this.grandTotal = grandTotal;
    }
}
