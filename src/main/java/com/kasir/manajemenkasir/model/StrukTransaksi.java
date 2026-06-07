package com.kasir.manajemenkasir.model;

public class StrukTransaksi implements Printable {
    private Transaksi transaksi;

    public StrukTransaksi() {
    }

    public StrukTransaksi(Transaksi transaksi) {
        this.transaksi = transaksi;
    }

    @Override
    public void print() {
        System.out.println("========== STRUK TRANSAKSI ==========");
        System.out.println("ID Transaksi : " + transaksi.getIdTransaksi());
        System.out.println("Tanggal      : " + transaksi.getTanggal());
        System.out.println("-------------------------------------");

        for (ItemTransaksi item : transaksi.getDaftarItem()) {
            System.out.println(item.getBarang().getNamaBarang()
                    + " x" + item.getQty()
                    + " = Rp" + item.getSubtotal());
        }

        System.out.println("-------------------------------------");
        System.out.println("Total Bayar  : Rp" + transaksi.getTotalBayar());
        System.out.println("=====================================");
    }

    public Transaksi getTransaksi() {
        return transaksi;
    }

    public void setTransaksi(Transaksi transaksi) {
        this.transaksi = transaksi;
    }
}