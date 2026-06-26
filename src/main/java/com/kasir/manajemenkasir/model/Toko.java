package com.kasir.manajemenkasir.model;

import jakarta.persistence.*;

@Entity
@Table(name = "toko")
public class Toko {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idToko;
    private String namaToko;
    private String alamat;
    private String kontak;
    private double persentasePajak;
    private boolean aktif = true;

    public Toko() {
    }

    public Toko(String namaToko, String alamat, String kontak) {
        this.namaToko = namaToko;
        this.alamat = alamat;
        this.kontak = kontak;
        this.persentasePajak = 0.0;
    }

    public int getIdToko() {
        return idToko;
    }

    public void setIdToko(int idToko) {
        this.idToko = idToko;
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

    public String getKontak() {
        return kontak;
    }

    public void setKontak(String kontak) {
        this.kontak = kontak;
    }

    public double getPersentasePajak() {
        return persentasePajak;
    }

    public void setPersentasePajak(double persentasePajak) {
        this.persentasePajak = persentasePajak;
    }

    public boolean isAktif() {
        return aktif;
    }

    public void setAktif(boolean aktif) {
        this.aktif = aktif;
    }
}
