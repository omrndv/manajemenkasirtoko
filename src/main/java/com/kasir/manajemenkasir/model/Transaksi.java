package com.kasir.manajemenkasir.model;

import java.util.ArrayList;
import java.util.List;

public class Transaksi {
    private int idTransaksi;
    private String tanggal;
    private double totalBayar;
    private List<ItemTransaksi> daftarItem;

    public Transaksi() {
        this.daftarItem = new ArrayList<>();
    }

    public Transaksi(int idTransaksi, String tanggal) {
        this.idTransaksi = idTransaksi;
        this.tanggal = tanggal;
        this.daftarItem = new ArrayList<>();
    }

    public void tambahItem(ItemTransaksi item) {
        daftarItem.add(item);

        if (item.getBarang() != null) {
            item.getBarang().updateStok(item.getQty());
        }

        hitungTotal();
    }

    public double hitungTotal() {
        totalBayar = 0;

        for (ItemTransaksi item : daftarItem) {
            totalBayar += item.getSubtotal();
        }

        return totalBayar;
    }

    public void simpanTransaksi() {
        System.out.println("Transaksi dengan ID " + idTransaksi + " berhasil disimpan.");
    }

    public double hitungKembalian(double uangDibayar) {
        return uangDibayar - totalBayar;
    }

    public int getIdTransaksi() {
        return idTransaksi;
    }

    public void setIdTransaksi(int idTransaksi) {
        this.idTransaksi = idTransaksi;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public double getTotalBayar() {
        return totalBayar;
    }

    public void setTotalBayar(double totalBayar) {
        this.totalBayar = totalBayar;
    }

    public List<ItemTransaksi> getDaftarItem() {
        return daftarItem;
    }

    public void setDaftarItem(List<ItemTransaksi> daftarItem) {
        this.daftarItem = daftarItem;
    }
}