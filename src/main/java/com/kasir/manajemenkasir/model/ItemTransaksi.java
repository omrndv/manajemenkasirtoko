package com.kasir.manajemenkasir.model;

public class ItemTransaksi {
    private int idItem;
    private Barang barang;
    private int qty;
    private double subtotal;

    public ItemTransaksi() {
    }

    public ItemTransaksi(int idItem, Barang barang, int qty) {
        this.idItem = idItem;
        this.barang = barang;
        this.qty = qty;
        this.subtotal = hitungSubtotal();
    }

    public double hitungSubtotal() {
        if (barang == null) {
            return 0;
        }

        return barang.getHarga() * qty;
    }

    public int getIdItem() {
        return idItem;
    }

    public void setIdItem(int idItem) {
        this.idItem = idItem;
    }

    public Barang getBarang() {
        return barang;
    }

    public void setBarang(Barang barang) {
        this.barang = barang;
        this.subtotal = hitungSubtotal();
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
        this.subtotal = hitungSubtotal();
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }
}