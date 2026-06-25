package com.kasir.manajemenkasir.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("Admin")
public class Admin extends User {
    private String namaAdmin;
    private String kontak;

    public Admin() {
        this.role = "Admin";
    }

    public Admin(int idUser, String username, String password, String namaAdmin, String kontak) {
        super(idUser, username, password, "Admin");
        this.namaAdmin = namaAdmin;
        this.kontak = kontak;
    }

    @Override
    public void tampilkanRole() {
        System.out.println("Role pengguna: Admin");
    }

    public void tambahBarang(Barang barang) {
        System.out.println("Admin menambahkan barang: " + barang.getNamaBarang());
    }

    public void updateBarang(Barang barang) {
        System.out.println("Admin mengubah barang: " + barang.getNamaBarang());
    }

    public void hapusBarang(Barang barang) {
        System.out.println("Admin menghapus barang: " + barang.getNamaBarang());
    }

    public String getNamaAdmin() {
        return namaAdmin;
    }

    public void setNamaAdmin(String namaAdmin) {
        this.namaAdmin = namaAdmin;
    }

    public String getKontak() {
        return kontak;
    }

    public void setKontak(String kontak) {
        this.kontak = kontak;
    }
}