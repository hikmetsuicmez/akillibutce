package com.akillibutce.repository;

import com.akillibutce.entity.Abonelik;
import com.akillibutce.entity.AbonelikDurum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AbonelikRepository extends JpaRepository<Abonelik, Long> {

    Optional<Abonelik> findByKullaniciId(Long kullaniciId);

    Optional<Abonelik> findByKullaniciIdAndDurum(Long kullaniciId, AbonelikDurum durum);
}
