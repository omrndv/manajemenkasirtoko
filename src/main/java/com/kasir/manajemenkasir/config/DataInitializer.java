package com.kasir.manajemenkasir.config;

import com.kasir.manajemenkasir.model.Admin;
import com.kasir.manajemenkasir.model.Barang;
import com.kasir.manajemenkasir.model.Kasir;
import com.kasir.manajemenkasir.repository.BarangRepository;
import com.kasir.manajemenkasir.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BarangRepository barangRepository;

    public DataInitializer(UserRepository userRepository, BarangRepository barangRepository) {
        this.userRepository = userRepository;
        this.barangRepository = barangRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Default users and items seeding removed for SaaS Multi-Tenant architecture
    }
}
