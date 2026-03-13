package com.akillibutce.repository;

import com.akillibutce.entity.BirikimDurum;
import com.akillibutce.entity.BirikimHedefi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BirikimHedefiRepository extends JpaRepository<BirikimHedefi, Long> {

    List<BirikimHedefi> findByKullaniciIdOrderByOlusturulmaTarihiDesc(Long kullaniciId);

    long countByKullaniciIdAndDurum(Long kullaniciId, BirikimDurum durum);
}
