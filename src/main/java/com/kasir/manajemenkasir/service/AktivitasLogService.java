package com.kasir.manajemenkasir.service;

import com.kasir.manajemenkasir.model.AktivitasLog;
import com.kasir.manajemenkasir.model.User;
import com.kasir.manajemenkasir.repository.AktivitasLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AktivitasLogService {
    private final AktivitasLogRepository repository;

    public AktivitasLogService(AktivitasLogRepository repository) {
        this.repository = repository;
    }

    public void log(User user, String aksi) {
        if (user == null) return;
        String username = user.getUsername();
        String role = user.getRole();
        String namaToko = (user.getToko() != null) ? user.getToko().getNamaToko() : "Sistem / Superadmin";
        
        AktivitasLog log = new AktivitasLog(username, role, namaToko, aksi);
        repository.save(log);
    }

    public List<AktivitasLog> getAllLogs() {
        return repository.findAllByOrderByIdDesc();
    }
}
