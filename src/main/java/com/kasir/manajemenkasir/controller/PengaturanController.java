package com.kasir.manajemenkasir.controller;

import com.kasir.manajemenkasir.model.Toko;
import com.kasir.manajemenkasir.model.User;
import com.kasir.manajemenkasir.repository.TokoRepository;
import com.kasir.manajemenkasir.service.AktivitasLogService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PengaturanController {

    private final TokoRepository tokoRepository;
    private final AktivitasLogService aktivitasLogService;

    public PengaturanController(TokoRepository tokoRepository, AktivitasLogService aktivitasLogService) {
        this.tokoRepository = tokoRepository;
        this.aktivitasLogService = aktivitasLogService;
    }

    private boolean bukanAdmin(HttpSession session) {
        User user = (User) session.getAttribute("userLogin");
        return user == null || !user.getRole().equalsIgnoreCase("Admin");
    }

    @GetMapping("/pengaturan")
    public String index(Model model, HttpSession session) {
        if (bukanAdmin(session)) {
            return "redirect:/dashboard";
        }

        User user = (User) session.getAttribute("userLogin");
        Toko toko = tokoRepository.findById(user.getToko().getIdToko()).orElse(null);

        if (toko == null) {
            return "redirect:/dashboard";
        }

        model.addAttribute("user", user);
        model.addAttribute("toko", toko);

        return "pengaturan";
    }

    @PostMapping("/pengaturan/simpan")
    public String simpanPengaturan(
            @RequestParam String namaToko,
            @RequestParam String kontak,
            @RequestParam String alamat,
            @RequestParam double persentasePajak,
            HttpSession session,
            Model model) {
        
        if (bukanAdmin(session)) {
            return "redirect:/dashboard";
        }

        User user = (User) session.getAttribute("userLogin");
        Toko toko = tokoRepository.findById(user.getToko().getIdToko()).orElse(null);

        if (toko != null) {
            toko.setNamaToko(namaToko);
            toko.setKontak(kontak);
            toko.setAlamat(alamat);
            toko.setPersentasePajak(persentasePajak);
            tokoRepository.save(toko);
            
            // Perbarui objek user di session karena referensi tokonya mungkin berubah
            user.setToko(toko);
            session.setAttribute("userLogin", user);
            
            aktivitasLogService.log(user, "Mengubah pengaturan toko (Nama: " + namaToko + ", Pajak PPN: " + persentasePajak + "%)");
            model.addAttribute("success", "Pengaturan toko berhasil diperbarui.");
        }

        model.addAttribute("user", user);
        model.addAttribute("toko", toko);
        
        return "pengaturan";
    }
}
