package com.akillibutce.repository;

import com.akillibutce.entity.Kategori;
import com.akillibutce.entity.KategoriTip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KategoriRepository extends JpaRepository<Kategori, Long> {

    // Sistem kategorileri (kullanici_id = NULL)
    List<Kategori> findByKullaniciIsNull();

    // Belirli kullanicinin ozel kategorileri
    List<Kategori> findByKullaniciId(Long kullaniciId);

    // Sistem + kullanicinin kategorileri
    @Query("SELECT k FROM Kategori k WHERE k.kullanici IS NULL OR k.kullanici.id = :kullaniciId")
    List<Kategori> findSistemVeKullaniciKategorileri(@Param("kullaniciId") Long kullaniciId);

    // Belirli tip ve kullaniciya gore kategoriler
    @Query("SELECT k FROM Kategori k WHERE k.tip = :tip AND (k.kullanici IS NULL OR k.kullanici.id = :kullaniciId)")
    List<Kategori> findByTipAndKullanici(@Param("tip") KategoriTip tip, @Param("kullaniciId") Long kullaniciId);
}
