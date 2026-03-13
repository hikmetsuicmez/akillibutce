package com.akillibutce.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "duzenli_islem", indexes = {
        @Index(columnList = "kullanici_id"),
        @Index(columnList = "gelecek_islem_tarihi")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DuzenliIslem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kullanici_id", nullable = false)
    private Kullanici kullanici;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "kategori_id", nullable = false)
    private Kategori kategori;

    @NotNull
    @DecimalMin(value = "0.01")
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal tutar;

    @NotBlank
    @Column(nullable = false, length = 500)
    private String aciklama;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Periyot periyot;

    @NotNull
    @Column(name = "gelecek_islem_tarihi", nullable = false)
    private LocalDate gelecekIslemTarihi;

    @Column(nullable = false)
    @Builder.Default
    private boolean aktif = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime olusturulmaTarihi;
}
