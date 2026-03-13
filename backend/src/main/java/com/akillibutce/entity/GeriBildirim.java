package com.akillibutce.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "geri_bildirim", indexes = {
        @Index(columnList = "kullanici_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeriBildirim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kullanici_id", nullable = false)
    private Kullanici kullanici;

    @Min(1)
    @Max(5)
    @Column(nullable = false)
    private int puan;

    @Column(length = 1000)
    private String mesaj;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime olusturulmaTarihi;
}
