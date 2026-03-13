package com.akillibutce.service;

import com.akillibutce.dto.OcrYaniti;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

/**
 * Gerçek OCR/AI entegrasyonu için yer tutucu Mock servis.
 * Yüklenen görseli analiz etmiş gibi davranıp rastgele fiş verisi döner.
 */
@Service
public class OcrMockServisi {

    private static final List<String> ORNEK_ACIKLAMALAR = List.of(
            "Market Alisverisi", "Restoran", "Kafe", "Benzin", "Eczane",
            "Giyim Magazasi", "Elektronik Market", "Kitapci", "Firin", "Kirtasiye"
    );

    private static final List<Double> KDV_ORANLARI = List.of(1.0, 8.0, 18.0, 20.0);

    public OcrYaniti fisOku(MultipartFile dosya) {
        // Dosya boyutuna göre deterministic ama "rastgele görünen" değerler üret
        long seed = dosya.getSize() > 0 ? dosya.getSize() : System.currentTimeMillis();
        Random yerelRandom = new Random(seed);

        double hammiktar = 10 + yerelRandom.nextDouble() * 490;
        BigDecimal tutar = BigDecimal.valueOf(hammiktar).setScale(2, RoundingMode.HALF_UP);

        int gunlerOncesi = yerelRandom.nextInt(30);
        LocalDate tarih = LocalDate.now().minusDays(gunlerOncesi);

        double kdvOrani = KDV_ORANLARI.get(yerelRandom.nextInt(KDV_ORANLARI.size()));
        String aciklama = ORNEK_ACIKLAMALAR.get(yerelRandom.nextInt(ORNEK_ACIKLAMALAR.size()));

        return new OcrYaniti(tutar, tarih, kdvOrani, aciklama);
    }
}
