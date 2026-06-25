package com.kasir.manajemenkasir.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("Kasir")
public class Kasir extends User {
    private String namaKasir;
    private String shift;

    public Kasir() {
        this.role = "Kasir";
    }

    public Kasir(int idUser, String username, String password, String namaKasir, String shift) {
        super(idUser, username, password, "Kasir");
        this.namaKasir = namaKasir;
        this.shift = shift;
    }

    @Override
    public void tampilkanRole() {
        System.out.println("Role pengguna: Kasir");
    }

    public Transaksi buatTransaksi(int idTransaksi, String tanggal) {
        return new Transaksi(idTransaksi, tanggal);
    }

    public void cetakStruk(Printable printable) {
        printable.print();
    }

    public String getNamaKasir() {
        return namaKasir;
    }

    public void setNamaKasir(String namaKasir) {
        this.namaKasir = namaKasir;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }
}