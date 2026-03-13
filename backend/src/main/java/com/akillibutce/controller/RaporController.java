package com.akillibutce.controller;

import com.akillibutce.entity.Kullanici;
import com.akillibutce.service.KullaniciService;
import com.akillibutce.service.RaporService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/raporlar")
@RequiredArgsConstructor
public class RaporController {

    private final RaporService raporService;
    private final KullaniciService kullaniciService;

    @GetMapping("/excel")
    public ResponseEntity<byte[]> excelRaporuIndir(
            @AuthenticationPrincipal UserDetails kullaniciDetaylari,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate baslangic,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate bitis) throws IOException {

        Kullanici kullanici = kullaniciService.epostayaGoreGetir(kullaniciDetaylari.getUsername());

        if (!kullanici.isPremium()) {
            return ResponseEntity.status(403).build();
        }

        LocalDate raporBaslangic = (baslangic != null) ? baslangic : LocalDate.now().withDayOfYear(1);
        LocalDate raporBitis = (bitis != null) ? bitis : LocalDate.now();

        byte[] excelVerisi = raporService.excelRaporuOlustur(kullanici, raporBaslangic, raporBitis);

        String dosyaAdi = "finansal-rapor-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".xlsx";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDisposition(ContentDisposition.attachment().filename(dosyaAdi).build());

        return ResponseEntity.ok().headers(headers).body(excelVerisi);
    }
}
