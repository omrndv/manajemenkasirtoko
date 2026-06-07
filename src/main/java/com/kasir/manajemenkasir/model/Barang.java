package com.kasir.manajemenkasir.model;

public class Barang {
    private int idBarang;
    private String namaBarang;
    private double harga;
    private int stok;

    public Barang() {
    }

    public Barang(int idBarang, String namaBarang, double harga, int stok) {
        this.idBarang = idBarang;
        this.namaBarang = namaBarang;
        this.harga = harga;
        this.stok = stok;
    }

    public void updateStok(int jumlahTerjual) {
        if (jumlahTerjual <= stok) {
            stok -= jumlahTerjual;
        } else {
            System.out.println("Stok tidak mencukupi.");
        }
    }

    public String getInfoBarang() {
        return namaBarang + " - Rp" + harga + " - Stok: " + stok;
    }

    public int getIdBarang() {
        return idBarang;
    }

    public void setIdBarang(int idBarang) {
        this.idBarang = idBarang;
    }

    public String getNamaBarang() {
        return namaBarang;
    }

    public void setNamaBarang(String namaBarang) {
        this.namaBarang = namaBarang;
    }

    public double getHarga() {
        return harga;
    }

    public void setHarga(double harga) {
        this.harga = harga;
    }

    public int getStok() {
        return stok;
    }

    public void setStok(int stok) {
        this.stok = stok;
    }
}