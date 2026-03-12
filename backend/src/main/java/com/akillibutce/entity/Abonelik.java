package com.akillibutce.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "abonelik")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Abonelik {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kullanici_id", nullable = false, unique = true)
    private Kullanici kullanici;

    @Column(nullable = false)
    private LocalDate baslangicTarihi;

    @Column(nullable = false)
    private LocalDate bitisTarihi;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private AbonelikDurum durum = AbonelikDurum.AKTIF;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AbonelikTip tip;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime olusturulmaTarihi;

    public boolean isAktif() {
        return this.durum == AbonelikDurum.AKTIF && !LocalDate.now().isAfter(bitisTarihi);
    }
}
