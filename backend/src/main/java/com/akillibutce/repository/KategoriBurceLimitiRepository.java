package com.akillibutce.repository;

import com.akillibutce.entity.KategoriBurceLimiti;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KategoriBurceLimitiRepository extends JpaRepository<KategoriBurceLimiti, Long> {

    List<KategoriBurceLimiti> findByKullaniciIdAndAyAndYil(Long kullaniciId, int ay, int yil);

    Optional<KategoriBurceLimiti> findByKullaniciIdAndKategoriIdAndAyAndYil(
            Long kullaniciId, Long kategoriId, int ay, int yil);
}
