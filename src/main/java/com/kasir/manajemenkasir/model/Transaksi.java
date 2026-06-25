package com.kasir.manajemenkasir.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "transaksi")
public class Transaksi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idTransaksi;
    private String tanggal;
    private double diskon;
    private double pajakPpn;
    private double totalBayar;
    private double uangDibayar;

    @ManyToOne
    @JoinColumn(name = "toko_id")
    private Toko toko;

    @OneToMany(mappedBy = "transaksi", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
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
        item.setTransaksi(this);

        if (item.getBarang() != null) {
            item.getBarang().updateStok(item.getQty());
        }

        hitungTotal();
    }

    public double hitungTotal() {
        double subTotal = 0;

        for (ItemTransaksi item : daftarItem) {
            subTotal += item.getSubtotal();
        }

        if (toko != null && toko.getPersentasePajak() > 0) {
            this.pajakPpn = (subTotal - diskon) * (toko.getPersentasePajak() / 100.0);
        } else {
            this.pajakPpn = 0;
        }

        totalBayar = (subTotal - diskon) + this.pajakPpn;
        
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

    public double getDiskon() {
        return diskon;
    }

    public void setDiskon(double diskon) {
        this.diskon = diskon;
    }

    public double getPajakPpn() {
        return pajakPpn;
    }

    public void setPajakPpn(double pajakPpn) {
        this.pajakPpn = pajakPpn;
    }

    public List<ItemTransaksi> getDaftarItem() {
        return daftarItem;
    }

    public void setDaftarItem(List<ItemTransaksi> daftarItem) {
        this.daftarItem = daftarItem;
    }

    public Toko getToko() {
        return toko;
    }

    public void setToko(Toko toko) {
        this.toko = toko;
    }

    public double getUangDibayar() {
        return uangDibayar;
    }

    public void setUangDibayar(double uangDibayar) {
        this.uangDibayar = uangDibayar;
    }
}