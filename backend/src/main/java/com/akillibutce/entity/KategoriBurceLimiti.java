package com.akillibutce.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "kategori_burce_limiti", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"kullanici_id", "kategori_id", "ay", "yil"})
}, indexes = {
        @Index(columnList = "kullanici_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KategoriBurceLimiti {

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

    @Min(1)
    @Max(12)
    @Column(nullable = false)
    private int ay;

    @Min(2020)
    @Column(nullable = false)
    private int yil;

    @NotNull
    @DecimalMin(value = "0.01")
    @Column(name = "limit_tutar", nullable = false, precision = 15, scale = 2)
    private BigDecimal limitTutar;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime olusturulmaTarihi;
}
