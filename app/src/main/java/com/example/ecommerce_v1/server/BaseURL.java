package com.example.ecommerce_v1.server;

public class BaseURL {

    //    public static String baseUrl = "http://192.168.18.7:5050/";
    public static String baseUrl = "http://192.168.18.5:5051/";
    //public static String baseUrl = "http://192.168.43.81:5050/";

    public static String login = baseUrl + "users/signin";
    public static String register = baseUrl + "users/signup";
    public static String updateProfile = baseUrl + "users/updateprofile/";

    public static String checkToko = baseUrl + "toko/cektoko/";
    public static String daftarToko = baseUrl + "toko/daftar";
    public static String getDataToko = baseUrl + "toko/getall";

    public static String checkBarang = baseUrl + "barang/cekbarang/";
    public static String addBarang = baseUrl + "barang/addbarang";
    public static String getDataBarang = baseUrl + "barang/getbarangbyid/";
    public static String deleteBarang = baseUrl + "barang/deletebyid/";
    public static String updateBarang = baseUrl + "barang/update/";

    public static String addPesanan = baseUrl + "transaksi/addkeranjang";
    public static String getDataPesanan = baseUrl + "transaksi/cart/";
    public static String getDataPengiriman = baseUrl + "transaksi/toko/";
    public static String deleteDataPesanan = baseUrl + "transaksi/cart/";
    public static String checkoutDataPesanan = baseUrl + "transaksi/cart/";
    public static String pembayaranPesanan = baseUrl + "transaksi/buktibayar";

    public static String getDataUser = baseUrl + "access/getdataUser";
    public static String completeUser = baseUrl + "access/completeData/";
    public static String updateUser = baseUrl + "access/updateUser/";

}
