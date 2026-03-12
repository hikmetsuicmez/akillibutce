package com.akillibutce.repository;

import com.akillibutce.entity.Islem;
import com.akillibutce.entity.KategoriTip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface IslemRepository extends JpaRepository<Islem, Long> {

    List<Islem> findByKullaniciIdOrderByIslemTarihiDesc(Long kullaniciId);

    // Tarih araligina gore filtreli islemler
    List<Islem> findByKullaniciIdAndIslemTarihiBetweenOrderByIslemTarihiDesc(
            Long kullaniciId, LocalDate baslangic, LocalDate bitis);

    // Bu ayin islemleri
    @Query("SELECT i FROM Islem i WHERE i.kullanici.id = :kullaniciId " +
           "AND YEAR(i.islemTarihi) = :yil AND MONTH(i.islemTarihi) = :ay " +
           "ORDER BY i.islemTarihi DESC")
    List<Islem> findAylikIslemler(@Param("kullaniciId") Long kullaniciId,
                                   @Param("yil") int yil,
                                   @Param("ay") int ay);

    // Bu ayin toplam geliri
    @Query("SELECT COALESCE(SUM(i.miktar), 0) FROM Islem i " +
           "WHERE i.kullanici.id = :kullaniciId " +
           "AND i.kategori.tip = 'GELIR' " +
           "AND YEAR(i.islemTarihi) = :yil AND MONTH(i.islemTarihi) = :ay")
    BigDecimal findAylikToplamGelir(@Param("kullaniciId") Long kullaniciId,
                                    @Param("yil") int yil,
                                    @Param("ay") int ay);

    // Bu ayin toplam gideri
    @Query("SELECT COALESCE(SUM(i.miktar), 0) FROM Islem i " +
           "WHERE i.kullanici.id = :kullaniciId " +
           "AND i.kategori.tip = 'GIDER' " +
           "AND YEAR(i.islemTarihi) = :yil AND MONTH(i.islemTarihi) = :ay")
    BigDecimal findAylikToplamGider(@Param("kullaniciId") Long kullaniciId,
                                    @Param("yil") int yil,
                                    @Param("ay") int ay);

    // Kategori bazli harcama tutari (tavsiye motoru icin)
    @Query("SELECT COALESCE(SUM(i.miktar), 0) FROM Islem i " +
           "WHERE i.kullanici.id = :kullaniciId " +
           "AND i.kategori.id = :kategoriId " +
           "AND YEAR(i.islemTarihi) = :yil AND MONTH(i.islemTarihi) = :ay")
    BigDecimal findKategoriAylikHarcama(@Param("kullaniciId") Long kullaniciId,
                                         @Param("kategoriId") Long kategoriId,
                                         @Param("yil") int yil,
                                         @Param("ay") int ay);

    // 6 aylik trend verisi
    @Query("SELECT YEAR(i.islemTarihi), MONTH(i.islemTarihi), i.kategori.tip, COALESCE(SUM(i.miktar), 0) " +
           "FROM Islem i " +
           "WHERE i.kullanici.id = :kullaniciId " +
           "AND i.islemTarihi >= :baslangic " +
           "GROUP BY YEAR(i.islemTarihi), MONTH(i.islemTarihi), i.kategori.tip " +
           "ORDER BY YEAR(i.islemTarihi), MONTH(i.islemTarihi)")
    List<Object[]> findAltiAylikTrend(@Param("kullaniciId") Long kullaniciId,
                                       @Param("baslangic") LocalDate baslangic);

    // Kategori bazli aylik harcamalar (pasta grafigi icin)
    @Query("SELECT i.kategori.isim, COALESCE(SUM(i.miktar), 0) FROM Islem i " +
           "WHERE i.kullanici.id = :kullaniciId " +
           "AND i.kategori.tip = 'GIDER' " +
           "AND YEAR(i.islemTarihi) = :yil AND MONTH(i.islemTarihi) = :ay " +
           "GROUP BY i.kategori.isim " +
           "ORDER BY SUM(i.miktar) DESC")
    List<Object[]> findAylikKategoriHarcamalari(@Param("kullaniciId") Long kullaniciId,
                                                 @Param("yil") int yil,
                                                 @Param("ay") int ay);
}
