package com.akillibutce.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "kategori")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Kategori {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String isim;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private KategoriTip tip;

    @Column(nullable = false)
    @Builder.Default
    private Boolean zorunluMu = false;

    // NULL ise sistem varsayilan kategorisi, dolu ise premium kullanicinin ozel kategorisi
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kullanici_id")
    private Kullanici kullanici;

    public boolean isSistemKategorisi() {
        return this.kullanici == null;
    }
}
