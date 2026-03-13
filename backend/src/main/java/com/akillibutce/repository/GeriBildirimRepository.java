package com.akillibutce.repository;

import com.akillibutce.entity.GeriBildirim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeriBildirimRepository extends JpaRepository<GeriBildirim, Long> {
}
