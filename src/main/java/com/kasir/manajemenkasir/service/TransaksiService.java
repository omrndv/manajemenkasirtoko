package com.kasir.manajemenkasir.service;

import com.kasir.manajemenkasir.model.Barang;
import com.kasir.manajemenkasir.model.ItemTransaksi;
import com.kasir.manajemenkasir.model.Transaksi;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransaksiService {
    private final List<Transaksi> daftarTransaksi = new ArrayList<>();
    private final BarangService barangService;

    public TransaksiService(BarangService barangService) {
        this.barangService = barangService;
    }

    public List<Transaksi> getAllTransaksi() {
        return daftarTransaksi;
    }

    public Transaksi getTransaksiById(int idTransaksi) {
        for (Transaksi transaksi : daftarTransaksi) {
            if (transaksi.getIdTransaksi() == idTransaksi) {
                return transaksi;
            }
        }

        return null;
    }

    public Transaksi buatTransaksi() {
        int idBaru = daftarTransaksi.size() + 1;
        String tanggalHariIni = LocalDate.now().toString();

        return new Transaksi(idBaru, tanggalHariIni);
    }

    public ItemTransaksi buatItemTransaksi(int idItem, int idBarang, int qty) {
        Barang barang = barangService.getBarangById(idBarang);

        if (barang == null) {
            throw new IllegalArgumentException("Barang tidak ditemukan.");
        }

        if (qty <= 0) {
            throw new IllegalArgumentException("Jumlah barang harus lebih dari 0.");
        }

        if (barang.getStok() < qty) {
            throw new IllegalArgumentException("Stok barang tidak mencukupi.");
        }

        return new ItemTransaksi(idItem, barang, qty);
    }

    public void tambahItemKeTransaksi(Transaksi transaksi, int idBarang, int qty) {
        if (transaksi == null) {
            throw new IllegalArgumentException("Transaksi tidak ditemukan.");
        }

        Barang barang = barangService.getBarangById(idBarang);

        if (barang == null) {
            throw new IllegalArgumentException("Barang tidak ditemukan.");
        }

        if (qty <= 0) {
            throw new IllegalArgumentException("Jumlah barang harus lebih dari 0.");
        }

        if (barang.getStok() < qty) {
            throw new IllegalArgumentException("Stok barang tidak mencukupi.");
        }

        for (ItemTransaksi item : transaksi.getDaftarItem()) {
            if (item.getBarang().getIdBarang() == idBarang) {
                item.setQty(item.getQty() + qty);
                barang.updateStok(qty);
                transaksi.hitungTotal();
                return;
            }
        }

        int idItemBaru = transaksi.getDaftarItem().size() + 1;
        ItemTransaksi itemBaru = new ItemTransaksi(idItemBaru, barang, qty);

        transaksi.tambahItem(itemBaru);
    }

    public void tambahQtyItem(Transaksi transaksi, int idItem) {
        if (transaksi == null) {
            throw new IllegalArgumentException("Transaksi tidak ditemukan.");
        }

        for (ItemTransaksi item : transaksi.getDaftarItem()) {
            if (item.getIdItem() == idItem) {
                Barang barang = item.getBarang();

                if (barang.getStok() <= 0) {
                    throw new IllegalArgumentException("Stok barang tidak mencukupi.");
                }

                item.setQty(item.getQty() + 1);
                barang.updateStok(1);
                transaksi.hitungTotal();
                return;
            }
        }

        throw new IllegalArgumentException("Item transaksi tidak ditemukan.");
    }

    public void kurangQtyItem(Transaksi transaksi, int idItem) {
        if (transaksi == null) {
            throw new IllegalArgumentException("Transaksi tidak ditemukan.");
        }

        ItemTransaksi itemDikurangi = null;

        for (ItemTransaksi item : transaksi.getDaftarItem()) {
            if (item.getIdItem() == idItem) {
                itemDikurangi = item;
                break;
            }
        }

        if (itemDikurangi == null) {
            throw new IllegalArgumentException("Item transaksi tidak ditemukan.");
        }

        Barang barang = itemDikurangi.getBarang();
        barang.setStok(barang.getStok() + 1);

        if (itemDikurangi.getQty() <= 1) {
            transaksi.getDaftarItem().remove(itemDikurangi);
        } else {
            itemDikurangi.setQty(itemDikurangi.getQty() - 1);
        }

        transaksi.hitungTotal();
    }

    public void simpanTransaksi(Transaksi transaksi) {
        if (transaksi == null) {
            throw new IllegalArgumentException("Transaksi tidak boleh kosong.");
        }

        if (transaksi.getDaftarItem().isEmpty()) {
            throw new IllegalArgumentException("Transaksi harus memiliki minimal satu item.");
        }

        transaksi.hitungTotal();
        daftarTransaksi.add(transaksi);
        transaksi.simpanTransaksi();
    }

    public double hitungKembalian(Transaksi transaksi, double uangDibayar) {
        if (transaksi == null) {
            throw new IllegalArgumentException("Transaksi tidak ditemukan.");
        }

        if (uangDibayar < transaksi.getTotalBayar()) {
            throw new IllegalArgumentException("Uang pembayaran kurang.");
        }

        return transaksi.hitungKembalian(uangDibayar);
    }

    public void hapusTransaksi(int idTransaksi) {
        daftarTransaksi.removeIf(transaksi -> transaksi.getIdTransaksi() == idTransaksi);
    }

    public void hapusItemDariTransaksi(Transaksi transaksi, int idItem) {
        if (transaksi == null) {
            throw new IllegalArgumentException("Transaksi tidak ditemukan.");
        }

        ItemTransaksi itemDihapus = null;

        for (ItemTransaksi item : transaksi.getDaftarItem()) {
            if (item.getIdItem() == idItem) {
                itemDihapus = item;
                break;
            }
        }

        if (itemDihapus == null) {
            throw new IllegalArgumentException("Item transaksi tidak ditemukan.");
        }

        Barang barang = itemDihapus.getBarang();

        if (barang != null) {
            barang.setStok(barang.getStok() + itemDihapus.getQty());
        }

        transaksi.getDaftarItem().remove(itemDihapus);
        transaksi.hitungTotal();
    }
}