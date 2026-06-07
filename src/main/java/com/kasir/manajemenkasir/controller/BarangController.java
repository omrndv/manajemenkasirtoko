package com.kasir.manajemenkasir.controller;

import com.kasir.manajemenkasir.model.Barang;
import com.kasir.manajemenkasir.model.User;
import com.kasir.manajemenkasir.service.BarangService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class BarangController {
    private final BarangService barangService;

    public BarangController(BarangService barangService) {
        this.barangService = barangService;
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
        model.addAttribute("daftarBarang", barangService.getAllBarang());

        return "barang";
    }

    @GetMapping("/barang/tambah")
    public String formTambah(Model model, HttpSession session) {
        if (bukanAdmin(session)) {
            return "redirect:/dashboard";
        }

        User user = (User) session.getAttribute("userLogin");

        model.addAttribute("user", user);
        model.addAttribute("barang", new Barang());

        return "tambah-barang";
    }

    @PostMapping("/barang/tambah")
    public String tambahBarang(Barang barang, HttpSession session) {
        if (bukanAdmin(session)) {
            return "redirect:/dashboard";
        }

        barangService.tambahBarang(barang);
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

        model.addAttribute("user", user);
        model.addAttribute("barang", barang);

        return "edit-barang";
    }

    @PostMapping("/barang/edit/{id}")
    public String updateBarang(@PathVariable int id, Barang barang, HttpSession session) {
        if (bukanAdmin(session)) {
            return "redirect:/dashboard";
        }

        barangService.updateBarang(id, barang);
        return "redirect:/barang";
    }

    @GetMapping("/barang/hapus/{id}")
    public String hapusBarang(@PathVariable int id, HttpSession session) {
        if (bukanAdmin(session)) {
            return "redirect:/dashboard";
        }

        barangService.hapusBarang(id);
        return "redirect:/barang";
    }
}