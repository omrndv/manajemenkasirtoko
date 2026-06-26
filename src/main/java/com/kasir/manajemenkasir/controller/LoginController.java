package com.kasir.manajemenkasir.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kasir.manajemenkasir.model.User;
import com.kasir.manajemenkasir.model.Transaksi;
import com.kasir.manajemenkasir.service.BarangService;
import com.kasir.manajemenkasir.service.LaporanService;
import com.kasir.manajemenkasir.service.TransaksiService;
import com.kasir.manajemenkasir.service.UserService;
import com.kasir.manajemenkasir.service.AktivitasLogService;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

    private final UserService userService;
    private final BarangService barangService;
    private final TransaksiService transaksiService;
    private final LaporanService laporanService;
    private final AktivitasLogService aktivitasLogService;

    public LoginController(
            UserService userService,
            BarangService barangService,
            TransaksiService transaksiService,
            LaporanService laporanService,
            AktivitasLogService aktivitasLogService
    ) {
        this.userService = userService;
        this.barangService = barangService;
        this.transaksiService = transaksiService;
        this.laporanService = laporanService;
        this.aktivitasLogService = aktivitasLogService;
    }

    @GetMapping("/")
    public String landingPage() {
        return "landingpage";
    }

    @GetMapping("/login")
    public String loginPage(jakarta.servlet.http.HttpServletRequest request, Model model) {
        jakarta.servlet.http.Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (jakarta.servlet.http.Cookie cookie : cookies) {
                if ("rememberedUsername".equals(cookie.getName())) {
                    model.addAttribute("rememberedUsername", cookie.getValue());
                    break;
                }
            }
        }
        return "login";
    }

    @GetMapping("/kebijakan-privasi")
    public String kebijakanPrivasiPage() {
        return "kebijakan-privasi";
    }

    @GetMapping("/syarat-ketentuan")
    public String syaratKetentuanPage() {
        return "syarat-ketentuan";
    }

    @GetMapping("/lupa-password")
    public String lupaPasswordPage() {
        return "lupa-password";
    }

    @PostMapping("/lupa-password")
    public String prosesLupaPassword(
            @RequestParam String username,
            @RequestParam String kontak,
            Model model
    ) {
        boolean success = userService.resetPassword(username, kontak, "123456");
        
        if (success) {
            model.addAttribute("success", "Password berhasil di-reset menjadi '123456'. Silakan login dan ubah password Anda.");
            return "login";
        } else {
            model.addAttribute("error", "Username atau kontak toko tidak cocok.");
            return "lupa-password";
        }
    }

    @PostMapping("/login")
    public String prosesLogin(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam(required = false) boolean rememberMe,
            HttpSession session,
            jakarta.servlet.http.HttpServletResponse response,
            Model model
    ) {
        // Intercept superadmin login
        if ("superadmin".equals(username) && "superadmin123".equals(password)) {
            User sa = userService.getOrCreateSuperAdmin("superadmin", "superadmin123");
            session.setAttribute("userLogin", sa);
            
            // Handle cookie for superadmin if checked
            jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("rememberedUsername", username);
            if (rememberMe) {
                cookie.setMaxAge(30 * 24 * 60 * 60);
            } else {
                cookie.setMaxAge(0);
            }
            cookie.setPath("/");
            response.addCookie(cookie);
            
            return "redirect:/superadmin/dashboard";
        }

        User user = userService.login(username, password);

        if (user == null) {
            model.addAttribute("error", "Username atau password salah.");
            return "login";
        }

        if (!user.isAktif()) {
            model.addAttribute("error", "Akun Anda telah dinonaktifkan oleh administrator.");
            return "login";
        }

        if (user.getToko() != null && !user.getToko().isAktif()) {
            model.addAttribute("error", "Toko Anda telah dinonaktifkan oleh superadmin.");
            return "login";
        }

        session.setAttribute("userLogin", user);

        // Handle "Ingat Saya" cookie
        jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("rememberedUsername", username);
        if (rememberMe) {
            cookie.setMaxAge(30 * 24 * 60 * 60); // 30 hari
        } else {
            cookie.setMaxAge(0); // hapus cookie
        }
        cookie.setPath("/");
        response.addCookie(cookie);

        return "redirect:/dashboard";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String prosesRegister(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String namaAdmin,
            @RequestParam String kontak,
            @RequestParam String namaToko,
            Model model
    ) {
        User newUser = userService.registerAdmin(username, password, namaAdmin, kontak, namaToko);
        
        if (newUser == null) {
            model.addAttribute("error", "Username sudah digunakan.");
            return "register";
        }

        aktivitasLogService.log(newUser, "Pendaftaran akun merchant baru untuk toko: " + namaToko);
        model.addAttribute("success", "Pendaftaran berhasil! Silakan login.");
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("userLogin");

        if (user == null) {
            return "redirect:/login";
        }

        // Cek jika superadmin nyasar ke dashboard biasa
        if (user.getRole().equalsIgnoreCase("SuperAdmin")) {
            return "redirect:/superadmin/dashboard";
        }

        model.addAttribute("user", user);
        model.addAttribute("totalBarang", barangService.hitungTotalBarang(user.getToko()));
        model.addAttribute("totalStok", barangService.hitungTotalStok(user.getToko()));
        model.addAttribute("jumlahTransaksi", transaksiService.getAllTransaksi(user.getToko()).size());
        model.addAttribute("totalPenjualan", laporanService.hitungTotalPenjualan(user.getToko()));
        model.addAttribute("barangTerlaris", laporanService.cariBarangTerlaris(user.getToko()));
        
        // Data Tambahan Baru
        model.addAttribute("totalKeuntungan", laporanService.hitungLaba(user.getToko()));
        model.addAttribute("stokMenipisList", barangService.getStokMenipis(user.getToko()));
        model.addAttribute("jumlahStokMenipis", barangService.getStokMenipis(user.getToko()).size());

        java.util.List<Transaksi> semuaTrx = transaksiService.getAllTransaksi(user.getToko());
        
        // Build semuaTransaksiData for dynamic client-side filtering and chart toggles (now including items breakdown for Pie Chart)
        java.util.List<java.util.Map<String, Object>> semuaTransaksiData = new java.util.ArrayList<>();
        for (Transaksi t : semuaTrx) {
            double laba = 0;
            java.util.List<java.util.Map<String, Object>> itemsList = new java.util.ArrayList<>();
            for (com.kasir.manajemenkasir.model.ItemTransaksi item : t.getDaftarItem()) {
                double modal = item.getBarang() != null ? item.getBarang().getHargaModal() : 0;
                double harga = item.getBarang() != null ? item.getBarang().getHarga() : (item.getSubtotal() / item.getQty());
                laba += (harga - modal) * item.getQty();
                
                java.util.Map<String, Object> itemMap = new java.util.HashMap<>();
                itemMap.put("nama", item.getBarang() != null ? item.getBarang().getNamaBarang() : "Produk Dihapus");
                itemMap.put("kategori", item.getBarang() != null && item.getBarang().getKategori() != null && !item.getBarang().getKategori().trim().isEmpty() ? item.getBarang().getKategori().trim() : "Umum");
                itemMap.put("qty", item.getQty());
                itemMap.put("subtotal", item.getSubtotal());
                itemsList.add(itemMap);
            }
            laba -= t.getDiskon();
            
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("tanggal", t.getTanggal());
            map.put("omset", t.getTotalBayar());
            map.put("laba", laba);
            map.put("items", itemsList);
            semuaTransaksiData.add(map);
        }
        model.addAttribute("semuaTransaksiData", semuaTransaksiData);

        // Keep default chart labels/data for backward compatibility or initial load
        java.util.List<String> chartLabels = new java.util.ArrayList<>();
        java.util.List<Double> chartData = new java.util.ArrayList<>();
        java.time.LocalDate today = java.time.LocalDate.now();
        
        java.util.Map<String, Double> penjualanPerHari = new java.util.HashMap<>();
        for (Transaksi t : semuaTrx) {
            penjualanPerHari.put(t.getTanggal(), penjualanPerHari.getOrDefault(t.getTanggal(), 0.0) + t.getTotalBayar());
        }
        
        for (int i = 6; i >= 0; i--) {
            java.time.LocalDate d = today.minusDays(i);
            String dateStr = d.toString();
            String label = d.format(java.time.format.DateTimeFormatter.ofPattern("dd MMM", new java.util.Locale("id")));
            chartLabels.add(label);
            chartData.add(penjualanPerHari.getOrDefault(dateStr, 0.0));
        }
        
        model.addAttribute("chartLabels", chartLabels);
        model.addAttribute("chartData", chartData);

        return "dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
