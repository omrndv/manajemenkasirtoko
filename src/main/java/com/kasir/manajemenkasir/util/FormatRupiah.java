package com.kasir.manajemenkasir.util;

import java.text.NumberFormat;
import java.util.Locale;

public class FormatRupiah {

    public static String format(double angka) {
        Locale indonesia = new Locale("id", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(indonesia);

        formatRupiah.setMaximumFractionDigits(0);

        return formatRupiah.format(angka);
    }
}