package com.kasir.manajemenkasir.model;

import java.util.ArrayList;
import java.util.List;

public class LaporanPenjualan {
    private String periode;
    private double totalPenjualan;
    private List<Transaksi> listTransaksi;

    public LaporanPenjualan() {
        this.listTransaksi = new ArrayList<>();
    }

    public LaporanPenjualan(String periode) {
        this.periode = periode;
        this.listTransaksi = new ArrayList<>();
    }

    public void tambahTransaksi(Transaksi transaksi) {
        listTransaksi.add(transaksi);
        generateLaporan();
    }

    public double generateLaporan() {
        totalPenjualan = 0;

        for (Transaksi transaksi : listTransaksi) {
            totalPenjualan += transaksi.getTotalBayar();
        }

        return totalPenjualan;
    }

    public List<Transaksi> filterByTanggal(String tanggal) {
        List<Transaksi> hasilFilter = new ArrayList<>();

        for (Transaksi transaksi : listTransaksi) {
            if (transaksi.getTanggal().equals(tanggal)) {
                hasilFilter.add(transaksi);
            }
        }

        return hasilFilter;
    }

    public String getPeriode() {
        return periode;
    }

    public void setPeriode(String periode) {
        this.periode = periode;
    }

    public double getTotalPenjualan() {
        return totalPenjualan;
    }

    public void setTotalPenjualan(double totalPenjualan) {
        this.totalPenjualan = totalPenjualan;
    }

    public List<Transaksi> getListTransaksi() {
        return listTransaksi;
    }

    public void setListTransaksi(List<Transaksi> listTransaksi) {
        this.listTransaksi = listTransaksi;
    }
}