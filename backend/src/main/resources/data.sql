-- Varsayilan Kategoriler (kullanici_id NULL = sistem kategorisi)
INSERT INTO kategori (isim, tip, zorunlu_mu, kullanici_id)
SELECT 'Maaş', 'GELIR', false, NULL
WHERE NOT EXISTS (SELECT 1 FROM kategori WHERE isim = 'Maaş' AND kullanici_id IS NULL);

INSERT INTO kategori (isim, tip, zorunlu_mu, kullanici_id)
SELECT 'Ek Gelir', 'GELIR', false, NULL
WHERE NOT EXISTS (SELECT 1 FROM kategori WHERE isim = 'Ek Gelir' AND kullanici_id IS NULL);

INSERT INTO kategori (isim, tip, zorunlu_mu, kullanici_id)
SELECT 'Kira', 'GIDER', true, NULL
WHERE NOT EXISTS (SELECT 1 FROM kategori WHERE isim = 'Kira' AND kullanici_id IS NULL);

INSERT INTO kategori (isim, tip, zorunlu_mu, kullanici_id)
SELECT 'Fatura', 'GIDER', true, NULL
WHERE NOT EXISTS (SELECT 1 FROM kategori WHERE isim = 'Fatura' AND kullanici_id IS NULL);

INSERT INTO kategori (isim, tip, zorunlu_mu, kullanici_id)
SELECT 'Market', 'GIDER', true, NULL
WHERE NOT EXISTS (SELECT 1 FROM kategori WHERE isim = 'Market' AND kullanici_id IS NULL);

INSERT INTO kategori (isim, tip, zorunlu_mu, kullanici_id)
SELECT 'Ulaşım', 'GIDER', true, NULL
WHERE NOT EXISTS (SELECT 1 FROM kategori WHERE isim = 'Ulaşım' AND kullanici_id IS NULL);

INSERT INTO kategori (isim, tip, zorunlu_mu, kullanici_id)
SELECT 'Dışarıda Yemek', 'GIDER', false, NULL
WHERE NOT EXISTS (SELECT 1 FROM kategori WHERE isim = 'Dışarıda Yemek' AND kullanici_id IS NULL);

INSERT INTO kategori (isim, tip, zorunlu_mu, kullanici_id)
SELECT 'Eğlence', 'GIDER', false, NULL
WHERE NOT EXISTS (SELECT 1 FROM kategori WHERE isim = 'Eğlence' AND kullanici_id IS NULL);

INSERT INTO kategori (isim, tip, zorunlu_mu, kullanici_id)
SELECT 'Giyim', 'GIDER', false, NULL
WHERE NOT EXISTS (SELECT 1 FROM kategori WHERE isim = 'Giyim' AND kullanici_id IS NULL);

INSERT INTO kategori (isim, tip, zorunlu_mu, kullanici_id)
SELECT 'Sağlık', 'GIDER', true, NULL
WHERE NOT EXISTS (SELECT 1 FROM kategori WHERE isim = 'Sağlık' AND kullanici_id IS NULL);

INSERT INTO kategori (isim, tip, zorunlu_mu, kullanici_id)
SELECT 'Eğitim', 'GIDER', false, NULL
WHERE NOT EXISTS (SELECT 1 FROM kategori WHERE isim = 'Eğitim' AND kullanici_id IS NULL);

INSERT INTO kategori (isim, tip, zorunlu_mu, kullanici_id)
SELECT 'Diğer', 'GIDER', false, NULL
WHERE NOT EXISTS (SELECT 1 FROM kategori WHERE isim = 'Diğer' AND kullanici_id IS NULL);
