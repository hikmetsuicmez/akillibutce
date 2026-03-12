package com.akillibutce.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "kullanici", uniqueConstraints = {
        @UniqueConstraint(columnNames = "eposta")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Kullanici {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String ad;

    @NotBlank
    @Column(nullable = false)
    private String soyad;

    @Email
    @NotBlank
    @Column(nullable = false, unique = true)
    private String eposta;

    @NotBlank
    @Column(nullable = false)
    private String sifre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private KullaniciRol rol = KullaniciRol.ROLE_FREE_USER;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime olusturulmaTarihi;

    @OneToMany(mappedBy = "kullanici", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Islem> islemler = new ArrayList<>();

    @OneToMany(mappedBy = "kullanici", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Kategori> ozelKategoriler = new ArrayList<>();

    @OneToOne(mappedBy = "kullanici", cascade = CascadeType.ALL, orphanRemoval = true)
    private Abonelik abonelik;

    public boolean isPremium() {
        return this.rol == KullaniciRol.ROLE_PREMIUM_USER;
    }
}
