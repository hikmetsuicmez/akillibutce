package com.akillibutce.repository;

import com.akillibutce.entity.Bildirim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BildirimRepository extends JpaRepository<Bildirim, Long> {

    List<Bildirim> findByKullaniciIdOrderByOlusturulmaTarihiDesc(Long kullaniciId);

    long countByKullaniciIdAndOkunduMuFalse(Long kullaniciId);
}
