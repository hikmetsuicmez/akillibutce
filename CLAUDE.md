# Akıllı Bütçe - Claude Code Kılavuzu

## Proje Özeti

Java Spring Boot (backend) + React/Tailwind CSS (frontend) ile geliştirilmiş freemium kişisel finans yönetimi uygulaması.

## Tech Stack

- **Backend:** Java 17, Spring Boot 3.2, Spring Security + JWT, PostgreSQL, Hibernate/JPA
- **Frontend:** React 18, Tailwind CSS, Recharts, Vite
- **Test:** JUnit 5 + AssertJ (backend), Playwright (frontend E2E)
- **Kalite:** ESLint + Prettier (frontend), Maven Checkstyle (backend)

## Kodlama Standartları

- Tüm entity, metod, değişken ve tablo isimleri **Türkçe** olmalıdır.
- Controller → Service → Repository katman ayrımı korunmalıdır.
- SOLID prensiplerine uyulmalıdır.

## Klasör Yapısı

```
akilli-butce/
├── backend/src/main/java/com/akillibutce/
│   ├── config/         # SecurityConfig, vb.
│   ├── controller/     # REST endpoint'leri
│   ├── dto/            # Request/Response nesneleri
│   ├── entity/         # JPA Entity'leri
│   ├── exception/      # GlobalExceptionHandler
│   ├── repository/     # Spring Data JPA
│   ├── security/       # JwtUtil, JwtAuthFilter
│   └── service/        # İş mantığı
├── backend/src/test/   # JUnit 5 testleri (H2 in-memory)
├── frontend/src/
│   ├── components/     # Yeniden kullanılabilir bileşenler
│   ├── context/        # AuthContext (JWT yönetimi)
│   ├── pages/          # Dashboard, İşlemler, Analiz, Premium
│   └── services/       # Axios API servisleri
├── .githooks/          # pre-commit, pre-push hook'ları
├── test-with-user.sh   # Kullanıcı profilli test runner
└── .claude/skills/     # Proje skill'leri (aşağıda açıklandı)
```

## Güvenlik Kuralları

- `.env` dosyası asla commit'e eklenmez (`.gitignore` kapsamında).
- JWT secret, DB şifresi ve API anahtarları yalnızca ortam değişkenlerinden okunur.
- `--no-verify` ile git hook'ları ATLANAMAZ.

## Git Hook Kurulumu (Yeni Ekip Üyeleri)

```bash
bash .githooks/setup-hooks.sh
```

## Test Komutları

```bash
# Belirli kullanıcı profiliyle test
bash test-with-user.sh PREMIUM
bash test-with-user.sh FREE
bash test-with-user.sh ALL

# Frontend kalite kontrolü
cd frontend && npm run lint
cd frontend && npm run format:check
```

---

## Proje Skill'leri

Bu projeye özgü aşağıdaki skill'ler `.claude/skills/` dizininde tanımlıdır:

### `webapp-testing` — Frontend E2E Testi
**Ne zaman kullanılır:** Bir React sayfasını/bileşenini Playwright ile test etmek, UI davranışını doğrulamak, screenshot almak veya browser loglarını incelemek istediğinde.
```bash
# Backend + Frontend birlikte başlatarak test
python .claude/skills/webapp-testing/scripts/with_server.py \
  --server "cd backend && mvn spring-boot:run" --port 8080 \
  --server "cd frontend && npm run dev" --port 5173 \
  -- python senaryom.py
```

### `frontend-design` — UI Bileşen ve Sayfa Tasarımı
**Ne zaman kullanılır:** Yeni bir React bileşeni, Dashboard widget'ı, sayfa düzeni veya mevcut bir bileşenin görsel iyileştirmesi istendiğinde. Tailwind CSS ile üretim kalitesinde, özgün tasarımlar üretir.

### `pdf` — PDF Dışa Aktarma
**Ne zaman kullanılır:** Kullanıcının işlem geçmişini veya aylık özet raporunu PDF olarak indirmesi istendiğinde. Backend'deki `/api/export/pdf` endpoint'i için Python tabanlı PDF üretim mantığı sağlar (iText için referans verir).

### `xlsx` — Excel Dışa Aktarma
**Ne zaman kullanılır:** Kullanıcının işlem verilerini `.xlsx` formatında indirmesi istendiğinde. Backend'deki Apache POI entegrasyonuna ek olarak openpyxl/pandas tabanlı Python dışa aktarma senaryoları için rehber niteliğindedir.
