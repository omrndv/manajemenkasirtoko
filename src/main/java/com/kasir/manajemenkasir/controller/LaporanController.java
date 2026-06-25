package com.kasir.manajemenkasir.controller;

import com.kasir.manajemenkasir.model.LaporanPenjualan;
import com.kasir.manajemenkasir.model.User;
import com.kasir.manajemenkasir.service.LaporanService;
import com.kasir.manajemenkasir.service.TransaksiService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LaporanController {
    private final LaporanService laporanService;
    private final TransaksiService transaksiService;

    public LaporanController(LaporanService laporanService, TransaksiService transaksiService) {
        this.laporanService = laporanService;
        this.transaksiService = transaksiService;
    }

    @GetMapping("/laporan")
    public String index(Model model, HttpSession session) {
        User user = (User) session.getAttribute("userLogin");

        if (user == null || !user.getRole().equalsIgnoreCase("Admin")) {
            return "redirect:/dashboard";
        }

        LaporanPenjualan laporan = laporanService.generateLaporanSemua(user.getToko());

        model.addAttribute("user", user);
        model.addAttribute("laporan", laporan);
        model.addAttribute("totalPenjualan", laporanService.hitungTotalPenjualan(user.getToko()));
        model.addAttribute("totalLaba", laporanService.hitungLaba(user.getToko()));
        model.addAttribute("jumlahTransaksi", laporanService.hitungJumlahTransaksi(user.getToko()));
        model.addAttribute("barangTerlaris", laporanService.cariBarangTerlaris(user.getToko()));
        model.addAttribute("daftarTransaksi", transaksiService.getAllTransaksi(user.getToko()));
        
        // Advanced reporting metrics
        model.addAttribute("totalDiskon", laporanService.hitungTotalDiskon(user.getToko()));
        model.addAttribute("totalPajak", laporanService.hitungTotalPajak(user.getToko()));
        model.addAttribute("rataRataTransaksi", laporanService.hitungRataRataTransaksi(user.getToko()));
        model.addAttribute("penjualanBarangList", laporanService.getPenjualanBarangList(user.getToko()));
        model.addAttribute("penjualanPerKategori", laporanService.getPenjualanPerKategori(user.getToko()));

        return "laporan";
    }
}