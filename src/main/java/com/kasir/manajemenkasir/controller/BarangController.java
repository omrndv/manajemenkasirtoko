package com.kasir.manajemenkasir.controller;

import com.kasir.manajemenkasir.model.Barang;
import com.kasir.manajemenkasir.model.User;
import com.kasir.manajemenkasir.service.BarangService;
import com.kasir.manajemenkasir.service.AktivitasLogService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class BarangController {
    private final BarangService barangService;
    private final AktivitasLogService aktivitasLogService;

    public BarangController(BarangService barangService, AktivitasLogService aktivitasLogService) {
        this.barangService = barangService;
        this.aktivitasLogService = aktivitasLogService;
    }

    private boolean bukanAdmin(HttpSession session) {
        User user = (User) session.getAttribute("userLogin");
        return user == null || !user.getRole().equalsIgnoreCase("Admin");
    }

    @GetMapping("/barang")
    public String index(Model model, HttpSession session) {
        if (bukanAdmin(session)) {
            return "redirect:/dashboard";
        }

        User user = (User) session.getAttribute("userLogin");

        model.addAttribute("user", user);
        model.addAttribute("daftarBarang", barangService.getAllBarang(user.getToko()));

        return "barang";
    }

    @GetMapping("/barang/tambah")
    public String formTambah(Model model, HttpSession session) {
        if (bukanAdmin(session)) {
            return "redirect:/dashboard";
        }

        User user = (User) session.getAttribute("userLogin");

        java.util.List<String> categories = barangService.getAllBarang(user.getToko()).stream()
                .map(Barang::getKategori)
                .filter(c -> c != null && !c.trim().isEmpty())
                .distinct()
                .collect(java.util.stream.Collectors.toList());

        model.addAttribute("user", user);
        model.addAttribute("barang", new Barang());
        model.addAttribute("categories", categories);

        return "tambah-barang";
    }

    @PostMapping("/barang/tambah")
    public String tambahBarang(Barang barang, HttpSession session) {
        if (bukanAdmin(session)) {
            return "redirect:/dashboard";
        }

        User user = (User) session.getAttribute("userLogin");
        barang.setToko(user.getToko());

        barangService.tambahBarang(barang);
        aktivitasLogService.log(user, "Menambahkan barang baru: " + barang.getNamaBarang() + " (Stok: " + barang.getStok() + ", Harga: " + barang.getHarga() + ")");
        
        return "redirect:/barang";
    }

    @GetMapping("/barang/edit/{id}")
    public String formEdit(@PathVariable int id, Model model, HttpSession session) {
        if (bukanAdmin(session)) {
            return "redirect:/dashboard";
        }

        Barang barang = barangService.getBarangById(id);

        if (barang == null) {
            return "redirect:/barang";
        }

        User user = (User) session.getAttribute("userLogin");

        java.util.List<Barang> list = barangService.getAllBarang(user.getToko());
        int displayId = 1;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getIdBarang() == id) {
                displayId = i + 1;
                break;
            }
        }

        java.util.List<String> categories = barangService.getAllBarang(user.getToko()).stream()
                .map(Barang::getKategori)
                .filter(c -> c != null && !c.trim().isEmpty())
                .distinct()
                .collect(java.util.stream.Collectors.toList());

        model.addAttribute("user", user);
        model.addAttribute("barang", barang);
        model.addAttribute("displayId", displayId);
        model.addAttribute("categories", categories);

        return "edit-barang";
    }

    @PostMapping("/barang/edit/{id}")
    public String updateBarang(@PathVariable int id, Barang barang, HttpSession session) {
        if (bukanAdmin(session)) {
            return "redirect:/dashboard";
        }

        User user = (User) session.getAttribute("userLogin");
        barang.setToko(user.getToko());

        barangService.updateBarang(id, barang);
        aktivitasLogService.log(user, "Mengubah barang ID #" + id + ": " + barang.getNamaBarang() + " (Stok: " + barang.getStok() + ", Harga: " + barang.getHarga() + ")");

        return "redirect:/barang";
    }

    @GetMapping("/barang/hapus/{id}")
    public String hapusBarang(@PathVariable int id, HttpSession session) {
        if (bukanAdmin(session)) {
            return "redirect:/dashboard";
        }

        Barang barang = barangService.getBarangById(id);
        String namaBarang = (barang != null) ? barang.getNamaBarang() : "ID #" + id;

        barangService.hapusBarang(id);
        
        User user = (User) session.getAttribute("userLogin");
        aktivitasLogService.log(user, "Menghapus barang: " + namaBarang);

        return "redirect:/barang";
    }
}