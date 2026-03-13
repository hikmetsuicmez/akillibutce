package com.akillibutce.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "bildirim", indexes = {
        @Index(columnList = "kullanici_id"),
        @Index(columnList = "okundu_mu")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bildirim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kullanici_id", nullable = false)
    private Kullanici kullanici;

    @NotBlank
    @Column(nullable = false, length = 500)
    private String mesaj;

    @Column(name = "okundu_mu", nullable = false)
    @Builder.Default
    private boolean okunduMu = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime olusturulmaTarihi;
}
