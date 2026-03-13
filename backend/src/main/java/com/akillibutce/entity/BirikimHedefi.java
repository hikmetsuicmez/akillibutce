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
@Table(name = "birikim_hedefi", indexes = {
        @Index(columnList = "kullanici_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BirikimHedefi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kullanici_id", nullable = false)
    private Kullanici kullanici;

    @NotBlank
    @Column(nullable = false, length = 255)
    private String baslik;

    @NotNull
    @DecimalMin(value = "0.01")
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal hedefTutar;

    @Column(nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal mevcutTutar = BigDecimal.ZERO;

    @Column
    private LocalDate sonTarih;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private BirikimDurum durum = BirikimDurum.DEVAM_EDIYOR;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime olusturulmaTarihi;
}
