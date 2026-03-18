# 💰 Akıllı Bütçe

> Kişisel finans yönetimini kolaylaştıran, yapay zeka destekli akıllı bütçe takip uygulaması.

🌐 **Canlı Demo:** [akillibutce.vercel.app](https://akillibutce.vercel.app)

---

## 📋 İçindekiler

- [Proje Hakkında](#-proje-hakkında)
- [Özellikler](#-özellikler)
- [Teknoloji Yığını](#-teknoloji-yığını)
- [Mimari](#-mimari)
- [Kurulum](#-kurulum)
- [Ortam Değişkenleri](#-ortam-değişkenleri)
- [API Endpoints](#-api-endpoints)
- [Test](#-test)
- [Deployment](#-deployment)

---

## 🚀 Proje Hakkında

**Akıllı Bütçe**, kullanıcıların gelir ve giderlerini kolayca takip etmelerini, birikim hedefleri belirlemelerini, bütçe limitleri ayarlamalarını ve harcama alışkanlıkları hakkında yapay zeka destekli tavsiyeler almalarını sağlayan bir kişisel finans uygulamasıdır.

Spring Boot ile geliştirilmiş RESTful bir backend ve React ile oluşturulmuş modern bir frontend'den oluşan full-stack bir projedir. Ücretsiz ve premium abonelik planlarını destekler.

---

## ✨ Özellikler

### 🔐 Kimlik Doğrulama
- JWT tabanlı güvenli kullanıcı girişi ve kayıt
- Rol tabanlı erişim kontrolü (Free / Premium)

### 💳 İşlem Yönetimi
- Gelir ve gider işlemi ekleme, düzenleme, silme
- Kategori bazlı işlem sınıflandırması
- 📷 **OCR ile fiş yükleme** — makbuz fotoğrafından otomatik veri çıkarma

### 📊 Dashboard & Analiz
- Özet kartlar (toplam bakiye, gelir, gider)
- Pasta grafik ile kategori bazlı harcama dağılımı
- Trend grafikleri ile dönemsel analiz
- Detaylı analiz sayfası

### 🔔 Düzenli İşlemler & Bildirimler
- Tekrarlayan gelir/gider tanımlama (aylık, haftalık vb.)
- Otomatik bildirim sistemi

### 🎯 Birikim Hedefleri
- Hedef oluşturma ve ilerleme takibi
- Hedef tamamlanma durumu (aktif, tamamlandı)

### 📏 Bütçe Limitleri
- Kategori bazlı harcama limiti belirleme
- Limit aşımında uyarı

### 🤖 Yapay Zeka Tavsiyeleri
- Harcama alışkanlıklarına göre kişisel öneriler
- Tasarruf tavsiyeleri

### 📄 Raporlama
- Dönemsel gelir/gider raporları
- Kategori bazlı detaylı raporlar

### 💎 Premium Üyelik
- Free ve Premium plan ayrımı
- Premium'a özel gelişmiş analiz özellikleri

---

## 🛠 Teknoloji Yığını

### Backend
| Teknoloji | Versiyon | Açıklama |
|-----------|----------|----------|
| Java | 17+ | Ana programlama dili |
| Spring Boot | 3.x | Web framework |
| Spring Security | - | JWT kimlik doğrulama |
| Spring Data JPA | - | Veritabanı ORM |
| PostgreSQL | - | Ana veritabanı |
| Docker | - | Konteynerleştirme |
| Maven | - | Bağımlılık yönetimi |

### Frontend
| Teknoloji | Versiyon | Açıklama |
|-----------|----------|----------|
| React | 18+ | UI framework |
| Vite | - | Build tool |
| Tailwind CSS | - | Stil kütüphanesi |
| React Router | - | Sayfa yönlendirme |
| Axios | - | HTTP istemcisi |
| Recharts | - | Grafik kütüphanesi |

### Deployment
| Platform | Kullanım |
|----------|----------|
| Vercel | Frontend |
| Railway | Backend |

---

## 🏗 Mimari

```
akillibutce/
├── backend/                          # Spring Boot uygulaması
│   └── src/main/java/com/akillibutce/
│       ├── controller/               # REST API katmanı
│       │   ├── AuthController        # Kimlik doğrulama
│       │   ├── IslemController       # İşlemler
│       │   ├── KategoriController    # Kategoriler
│       │   ├── AnalizController      # Analiz & trendler
│       │   ├── BirikimHedefiController # Birikim hedefleri
│       │   ├── DuzenliIslemController  # Düzenli işlemler
│       │   ├── BildirimController    # Bildirimler
│       │   ├── KategoriBurceLimitiController # Bütçe limitleri
│       │   ├── RaporController       # Raporlar
│       │   └── GeriBildirimController # Geri bildirim
│       ├── service/                  # İş mantığı katmanı
│       ├── repository/               # Veri erişim katmanı
│       ├── entity/                   # JPA entity'leri
│       ├── dto/                      # Veri transfer objeleri
│       ├── security/                 # JWT güvenlik altyapısı
│       ├── config/                   # Spring konfigürasyonu
│       └── exception/                # Global hata yönetimi
│
└── frontend/                         # React uygulaması
    └── src/
        ├── pages/                    # Sayfa bileşenleri
        ├── components/               # Yeniden kullanılabilir bileşenler
        │   ├── Dashboard/            # Dashboard widget'ları
        │   ├── Islemler/             # İşlem bileşenleri
        │   └── Layout/               # Layout bileşenleri
        ├── services/                 # API servis katmanı
        └── context/                  # React context (Auth, Theme)
```

---

## ⚙️ Kurulum

### Gereksinimler

- Java 17+
- Node.js 18+
- PostgreSQL
- Maven

### Backend

```bash
# Repoyu klonla
git clone https://github.com/hikmetsuicmez/akillibutce.git
cd akillibutce/backend

# Ortam değişkenlerini ayarla
cp ../.env.example .env

# Uygulamayı derle ve çalıştır
mvn clean install
mvn spring-boot:run
```

### Frontend

```bash
cd akillibutce/frontend

# Bağımlılıkları yükle
npm install

# Geliştirme sunucusunu başlat
npm run dev
```

### Git Hook'larını Kur

```bash
cd .githooks
chmod +x setup-hooks.sh
./setup-hooks.sh
```

---

## 🔐 Ortam Değişkenleri

`.env.example` dosyasını kopyalayarak `.env` oluşturun:

```env
# Veritabanı
DB_URL=jdbc:postgresql://localhost:5432/akillibutce
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password

# JWT
JWT_SECRET=your_jwt_secret_key
JWT_EXPIRATION=86400000

# Frontend
VITE_API_URL=http://localhost:8080
```

---

## 📡 API Endpoints

### Auth
| Method | Endpoint | Açıklama |
|--------|----------|----------|
| POST | `/api/auth/kayit` | Kullanıcı kaydı |
| POST | `/api/auth/giris` | Giriş yapma |

### İşlemler
| Method | Endpoint | Açıklama |
|--------|----------|----------|
| GET | `/api/islemler` | İşlem listesi |
| POST | `/api/islemler` | Yeni işlem ekle |
| PUT | `/api/islemler/{id}` | İşlem güncelle |
| DELETE | `/api/islemler/{id}` | İşlem sil |

### Kategoriler
| Method | Endpoint | Açıklama |
|--------|----------|----------|
| GET | `/api/kategoriler` | Kategori listesi |
| POST | `/api/kategoriler` | Kategori ekle |

### Analiz
| Method | Endpoint | Açıklama |
|--------|----------|----------|
| GET | `/api/analiz/ozet` | Finansal özet |
| GET | `/api/analiz/trend` | Harcama trendleri |
| GET | `/api/analiz/tavsiye` | AI tavsiyeleri |

### Birikim Hedefleri
| Method | Endpoint | Açıklama |
|--------|----------|----------|
| GET | `/api/birikim-hedefleri` | Hedef listesi |
| POST | `/api/birikim-hedefleri` | Hedef oluştur |
| PUT | `/api/birikim-hedefleri/{id}/para-ekle` | Hedefe para ekle |

### Raporlar
| Method | Endpoint | Açıklama |
|--------|----------|----------|
| GET | `/api/raporlar` | Dönemsel rapor |

---

## 🧪 Test

### Backend Testleri

Proje, Free ve Premium plan senaryoları için ayrı test konfigürasyonları içerir:

```bash
# Tüm testleri çalıştır
mvn test

# Free plan testleri
mvn test -Dspring.profiles.active=test-free

# Premium plan testleri
mvn test -Dspring.profiles.active=test-premium
```

### Windows

```bat
test-with-user.bat
```

### Linux/macOS

```bash
chmod +x test-with-user.sh
./test-with-user.sh
```

---

## 🚢 Deployment

### Backend (Railway)

Backend, `backend/railway.json` yapılandırması ve `backend/Dockerfile` ile Railway üzerinde deploy edilmektedir.

```bash
# Docker image oluştur
cd backend
docker build -t akillibutce-backend .
```

### Frontend (Vercel)

Frontend, `frontend/vercel.json` yapılandırması ile Vercel üzerinde deploy edilmektedir. `master` branch'e yapılan her push otomatik deployment tetikler.

---

## 👤 Geliştirici

**Hikmet Suiçmez**
- GitHub: [@hikmetsuicmez](https://github.com/hikmetsuicmez)
- Java Developer at Infina

---

## 📄 Lisans

Bu proje açık kaynaklıdır. Kullanım için repo sahibiyle iletişime geçin.
