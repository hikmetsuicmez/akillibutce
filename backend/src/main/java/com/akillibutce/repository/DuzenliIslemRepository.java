package com.akillibutce.repository;

import com.akillibutce.entity.DuzenliIslem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DuzenliIslemRepository extends JpaRepository<DuzenliIslem, Long> {

    List<DuzenliIslem> findByKullaniciIdAndAktifTrueOrderByGelecekIslemTarihiAsc(Long kullaniciId);

    List<DuzenliIslem> findByAktifTrueAndGelecekIslemTarihiLessThanEqual(LocalDate tarih);
}
