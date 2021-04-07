package com.rian.ecommerce_v1.models;

public class ModelUsers {

    String _id, nama, alamat, phone, fotoProfile, email, password, username, role;

    public ModelUsers(){

    }

    public ModelUsers(String _id, String nama, String alamat, String phone, String fotoProfile, String email, String password, String username, String role) {
        this._id = _id;
        this.nama = nama;
        this.alamat = alamat;
        this.phone = phone;
        this.fotoProfile = fotoProfile;
        this.email = email;
        this.password = password;
        this.username = username;
        this.role = role;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFotoProfile() {
        return fotoProfile;
    }

    public void setFotoProfile(String fotoProfile) {
        this.fotoProfile = fotoProfile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
