package com.kasir.manajemenkasir.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

@Entity
@Table(name = "barang")
public class Barang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idBarang;
    private String namaBarang;
    private String kategori;
    private double hargaModal;
    private double harga;
    private int stok;

    @Column(columnDefinition = "LONGTEXT")
    private String gambar;

    @ManyToOne
    @JoinColumn(name = "toko_id")
    private Toko toko;

    public Barang() {
    }

    public Barang(int idBarang, String namaBarang, String kategori, double hargaModal, double harga, int stok) {
        this.idBarang = idBarang;
        this.namaBarang = namaBarang;
        this.kategori = kategori;
        this.hargaModal = hargaModal;
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

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public double getHargaModal() {
        return hargaModal;
    }

    public void setHargaModal(double hargaModal) {
        this.hargaModal = hargaModal;
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

    public Toko getToko() {
        return toko;
    }

    public void setToko(Toko toko) {
        this.toko = toko;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }
}