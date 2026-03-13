package com.akillibutce.service;

import com.akillibutce.dto.GeriBildirimIstegi;
import com.akillibutce.entity.GeriBildirim;
import com.akillibutce.entity.Kullanici;
import com.akillibutce.repository.GeriBildirimRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GeriBildirimService {

    private final GeriBildirimRepository geriBildirimRepository;

    @Transactional
    public void geriBildirimGonder(GeriBildirimIstegi istek, Kullanici kullanici) {
        GeriBildirim geriBildirim = GeriBildirim.builder()
                .kullanici(kullanici)
                .puan(istek.puan())
                .mesaj(istek.mesaj())
                .build();
        geriBildirimRepository.save(geriBildirim);
    }
}
