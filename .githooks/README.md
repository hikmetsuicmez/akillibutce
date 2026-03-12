# Git Hooks - Akıllı Bütçe Projesi

## Kurulum (tek seferlik)

```bash
bash .githooks/setup-hooks.sh
```

Bu komut Git'in hook dizinini `.githooks` olarak ayarlar.
Tüm ekip üyelerinin klonlama sonrası çalıştırması gerekir.

---

## Hook'lar

### `pre-commit` — Staged Dosya Kalite Kontrolü

`git commit` çalıştırıldığında **otomatik** devreye girer.

| Dosya Tipi | Kontrol |
|---|---|
| `.js .jsx .ts .tsx` | ESLint (hata varsa commit engellenir) |
| `.js .jsx .ts .tsx` | Prettier (format hatası varsa otomatik düzeltir ve re-stage eder) |
| `.java` | Maven compile — syntax hatası varsa commit engellenir |

### `pre-push` — Tam Güvenlik Kapısı

`git push` çalıştırıldığında **otomatik** devreye girer.

| Adım | Komut | Başarısız Olursa |
|---|---|---|
| 1 | `mvn clean test` | Push iptal edilir |
| 2 | `npm run build` | Push iptal edilir |

---

## ⚠️ Kesin Kural

```bash
# YASAK — asla kullanma:
git commit --no-verify
git push --no-verify

# Doğru yaklaşım: hata çıktısını oku → kodu düzelt → tekrar dene
```

---

## Kullanıcı Bazlı Test Runner

```bash
# Linux / macOS / Git Bash
bash test-with-user.sh PREMIUM
bash test-with-user.sh FREE
bash test-with-user.sh ALL

# Windows CMD
test-with-user.bat PREMIUM
test-with-user.bat FREE
test-with-user.bat ALL

# Belirli bir test sınıfı çalıştırmak için
bash test-with-user.sh PREMIUM -Dtest=TavsiyeServicePremiumTest
bash test-with-user.sh FREE    -Dtest=IslemServiceFreeTest
```

### Spring Profilleri

| Parametre | Spring Profil | Test Kullanıcısı | Senaryo |
|---|---|---|---|
| `PREMIUM` | `test-premium` | `premium@test.com` | Tavsiye motoru, trend grafik, özel kategori |
| `FREE` | `test-free` | `free@test.com` | Temel CRUD, aylık özet, tarih filtresi |

Test veritabanı H2 (bellekte) kullanır — gerçek PostgreSQL gerekmez.
