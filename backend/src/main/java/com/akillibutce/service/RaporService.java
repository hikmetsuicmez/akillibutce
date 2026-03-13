package com.akillibutce.service;

import com.akillibutce.entity.Islem;
import com.akillibutce.entity.Kullanici;
import com.akillibutce.repository.IslemRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RaporService {

    private final IslemRepository islemRepository;
    private static final DateTimeFormatter TARIH_FORMATI = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @Transactional(readOnly = true)
    public byte[] excelRaporuOlustur(Kullanici kullanici, LocalDate baslangic, LocalDate bitis) throws IOException {
        List<Islem> islemler = islemRepository
                .findByKullaniciIdAndIslemTarihiBetweenOrderByIslemTarihiDesc(
                        kullanici.getId(), baslangic, bitis);

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            ozzetSayfasiOlustur(workbook, islemler, kullanici, baslangic, bitis);
            detaySayfasiOlustur(workbook, islemler);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            return baos.toByteArray();
        }
    }

    private void ozzetSayfasiOlustur(XSSFWorkbook workbook, List<Islem> islemler,
                                      Kullanici kullanici, LocalDate baslangic, LocalDate bitis) {
        Sheet sayfa = workbook.createSheet("Finansal Ozet");
        sayfa.setColumnWidth(0, 8000);
        sayfa.setColumnWidth(1, 6000);

        CellStyle baslikStili = baslikStiliOlustur(workbook);
        CellStyle degerStili = degerStiliOlustur(workbook);
        CellStyle paraStili = paraStiliOlustur(workbook);

        int satirNo = 0;

        Row baslikSatiri = sayfa.createRow(satirNo++);
        Cell baslikCell = baslikSatiri.createCell(0);
        baslikCell.setCellValue("FINANSAL OZET RAPORU");
        baslikCell.setCellStyle(baslikStili);
        sayfa.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));
        satirNo++;

        satirEkle(sayfa, satirNo++, "Kullanici", kullanici.getAd() + " " + kullanici.getSoyad(), degerStili, degerStili);
        satirEkle(sayfa, satirNo++, "Rapor Donemi",
                baslangic.format(TARIH_FORMATI) + " - " + bitis.format(TARIH_FORMATI),
                degerStili, degerStili);
        satirNo++;

        BigDecimal toplamGelir = islemler.stream()
                .filter(i -> "GELIR".equals(i.getKategori().getTip().name()))
                .map(Islem::getMiktar)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal toplamGider = islemler.stream()
                .filter(i -> "GIDER".equals(i.getKategori().getTip().name()))
                .map(Islem::getMiktar)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal netDurum = toplamGelir.subtract(toplamGider);

        satirEkleParali(sayfa, satirNo++, "Toplam Gelir", toplamGelir, degerStili, paraStili);
        satirEkleParali(sayfa, satirNo++, "Toplam Gider", toplamGider, degerStili, paraStili);
        satirEkleParali(sayfa, satirNo, "Net Durum", netDurum, degerStili, paraStili);
    }

    private void detaySayfasiOlustur(XSSFWorkbook workbook, List<Islem> islemler) {
        Sheet sayfa = workbook.createSheet("Islem Detaylari");
        sayfa.setColumnWidth(0, 4000);
        sayfa.setColumnWidth(1, 6000);
        sayfa.setColumnWidth(2, 4000);
        sayfa.setColumnWidth(3, 3000);
        sayfa.setColumnWidth(4, 8000);

        CellStyle baslikStili = baslikStiliOlustur(workbook);
        CellStyle normalStili = degerStiliOlustur(workbook);
        CellStyle paraStili = paraStiliOlustur(workbook);

        Row ustBaslik = sayfa.createRow(0);
        String[] sutunlar = {"Tarih", "Kategori", "Tip", "Tutar (TL)", "Aciklama"};
        for (int i = 0; i < sutunlar.length; i++) {
            Cell cell = ustBaslik.createCell(i);
            cell.setCellValue(sutunlar[i]);
            cell.setCellStyle(baslikStili);
        }

        int satirNo = 1;
        for (Islem islem : islemler) {
            Row satir = sayfa.createRow(satirNo++);
            Cell tarihCell = satir.createCell(0);
            tarihCell.setCellValue(islem.getIslemTarihi().format(TARIH_FORMATI));
            tarihCell.setCellStyle(normalStili);

            Cell kategoriCell = satir.createCell(1);
            kategoriCell.setCellValue(islem.getKategori().getIsim());
            kategoriCell.setCellStyle(normalStili);

            Cell tipCell = satir.createCell(2);
            tipCell.setCellValue(islem.getKategori().getTip().name());
            tipCell.setCellStyle(normalStili);

            Cell tutarCell = satir.createCell(3);
            tutarCell.setCellValue(islem.getMiktar().doubleValue());
            tutarCell.setCellStyle(paraStili);

            Cell aciklamaCell = satir.createCell(4);
            aciklamaCell.setCellValue(islem.getAciklama() != null ? islem.getAciklama() : "");
            aciklamaCell.setCellStyle(normalStili);
        }
    }

    private void satirEkle(Sheet sayfa, int satirNo, String etiket, String deger,
                            CellStyle etiketStil, CellStyle degerStil) {
        Row satir = sayfa.createRow(satirNo);
        Cell etiketCell = satir.createCell(0);
        etiketCell.setCellValue(etiket);
        etiketCell.setCellStyle(etiketStil);
        Cell degerCell = satir.createCell(1);
        degerCell.setCellValue(deger);
        degerCell.setCellStyle(degerStil);
    }

    private void satirEkleParali(Sheet sayfa, int satirNo, String etiket, BigDecimal deger,
                                  CellStyle etiketStil, CellStyle paraStil) {
        Row satir = sayfa.createRow(satirNo);
        Cell etiketCell = satir.createCell(0);
        etiketCell.setCellValue(etiket);
        etiketCell.setCellStyle(etiketStil);
        Cell degerCell = satir.createCell(1);
        degerCell.setCellValue(deger.doubleValue());
        degerCell.setCellStyle(paraStil);
    }

    private CellStyle baslikStiliOlustur(Workbook workbook) {
        CellStyle stil = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        stil.setFont(font);
        stil.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        stil.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font beyazFont = workbook.createFont();
        beyazFont.setBold(true);
        beyazFont.setColor(IndexedColors.WHITE.getIndex());
        beyazFont.setFontHeightInPoints((short) 12);
        stil.setFont(beyazFont);
        return stil;
    }

    private CellStyle degerStiliOlustur(Workbook workbook) {
        CellStyle stil = workbook.createCellStyle();
        stil.setBorderBottom(BorderStyle.THIN);
        stil.setBorderTop(BorderStyle.THIN);
        stil.setBorderLeft(BorderStyle.THIN);
        stil.setBorderRight(BorderStyle.THIN);
        return stil;
    }

    private CellStyle paraStiliOlustur(Workbook workbook) {
        CellStyle stil = workbook.createCellStyle();
        DataFormat format = workbook.createDataFormat();
        stil.setDataFormat(format.getFormat("#,##0.00 ₺"));
        stil.setBorderBottom(BorderStyle.THIN);
        stil.setBorderTop(BorderStyle.THIN);
        stil.setBorderLeft(BorderStyle.THIN);
        stil.setBorderRight(BorderStyle.THIN);
        return stil;
    }
}
