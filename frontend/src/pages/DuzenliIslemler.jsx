import { useState, useEffect, useCallback } from 'react'
import { Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { duzenliIslemServisi } from '../services/duzenliIslemServisi'
import { kategoriServisi } from '../services/kategoriServisi'
import toast from 'react-hot-toast'

function PaywallEkrani() {
  return (
    <div className="max-w-md mx-auto mt-16 text-center">
      <div className="text-6xl mb-4">🔒</div>
      <h2 className="text-2xl font-bold text-gray-900 dark:text-gray-100 mb-2">
        Premium Özelliği
      </h2>
      <p className="text-gray-500 dark:text-gray-400 mb-6">
        Düzenli İşlemler (Netflix, kira, fatura vb.) yönetimi sadece
        Premium üyelere açıktır. Abonelikler otomatik olarak işlenir.
      </p>
      <Link to="/premium" className="btn-primary inline-block">
        Premium'a Geç
      </Link>
    </div>
  )
}

export default function DuzenliIslemler() {
  const { premiumMu } = useAuth()
  const [islemler, setIslemler] = useState([])
  const [kategoriler, setKategoriler] = useState([])
  const [yukleniyor, setYukleniyor] = useState(true)
  const [formAcik, setFormAcik] = useState(false)
  const [gonderiyor, setGonderiyor] = useState(false)
  const [form, setForm] = useState({
    kategoriId: '',
    tutar: '',
    aciklama: '',
    periyot: 'AYLIK',
    gelecekIslemTarihi: new Date().toISOString().split('T')[0],
  })

  const verileriYukle = useCallback(async () => {
    if (!premiumMu) { setYukleniyor(false); return }
    try {
      const [islemVeri, kategoriVeri] = await Promise.all([
        duzenliIslemServisi.duzenliIslemleriGetir(),
        kategoriServisi.kategorileriGetir(),
      ])
      setIslemler(islemVeri)
      setKategoriler(kategoriVeri)
    } catch {
      toast.error('Veriler yüklenemedi.')
    } finally {
      setYukleniyor(false)
    }
  }, [premiumMu])

  useEffect(() => { verileriYukle() }, [verileriYukle])

  const islemEkle = async (e) => {
    e.preventDefault()
    setGonderiyor(true)
    try {
      await duzenliIslemServisi.duzenliIslemEkle({
        ...form,
        kategoriId: Number(form.kategoriId),
        tutar: parseFloat(form.tutar),
      })
      toast.success('Düzenli işlem eklendi!')
      setFormAcik(false)
      setForm({
        kategoriId: '',
        tutar: '',
        aciklama: '',
        periyot: 'AYLIK',
        gelecekIslemTarihi: new Date().toISOString().split('T')[0],
      })
      verileriYukle()
    } catch (err) {
      toast.error(err.response?.data?.mesaj || 'İşlem eklenemedi.')
    } finally {
      setGonderiyor(false)
    }
  }

  const islemSil = async (id) => {
    if (!window.confirm('Bu düzenli işlemi iptal etmek istiyor musunuz?')) return
    try {
      await duzenliIslemServisi.duzenliIslemSil(id)
      toast.success('Düzenli işlem iptal edildi.')
      verileriYukle()
    } catch {
      toast.error('İşlem iptal edilemedi.')
    }
  }

  if (!premiumMu) return <PaywallEkrani />

  if (yukleniyor) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-600" />
      </div>
    )
  }

  const giderKategorileri = kategoriler.filter((k) => k.tip === 'GIDER')

  return (
    <div className="max-w-3xl mx-auto space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900 dark:text-gray-100">Düzenli İşlemler</h1>
          <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">
            Abonelikler ve tekrar eden ödemeler otomatik işlenir.
          </p>
        </div>
        <button onClick={() => setFormAcik(true)} className="btn-primary flex items-center gap-2">
          <span>+</span> Abonelik Ekle
        </button>
      </div>

      {formAcik && (
        <div className="card">
          <h2 className="font-semibold text-gray-900 dark:text-gray-100 mb-4">Yeni Düzenli İşlem</h2>
          <form onSubmit={islemEkle} className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-medium text-gray-600 dark:text-gray-400 mb-1">Kategori</label>
              <select
                value={form.kategoriId}
                onChange={(e) => setForm((f) => ({ ...f, kategoriId: e.target.value }))}
                className="input-field"
                required
              >
                <option value="">Seçiniz...</option>
                {giderKategorileri.map((k) => (
                  <option key={k.id} value={k.id}>{k.isim}</option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-xs font-medium text-gray-600 dark:text-gray-400 mb-1">Tutar (₺)</label>
              <input
                type="number"
                value={form.tutar}
                onChange={(e) => setForm((f) => ({ ...f, tutar: e.target.value }))}
                className="input-field"
                placeholder="0.00"
                min="0.01"
                step="0.01"
                required
              />
            </div>

            <div>
              <label className="block text-xs font-medium text-gray-600 dark:text-gray-400 mb-1">Açıklama</label>
              <input
                type="text"
                value={form.aciklama}
                onChange={(e) => setForm((f) => ({ ...f, aciklama: e.target.value }))}
                className="input-field"
                placeholder="Netflix, Spotify..."
                required
              />
            </div>

            <div>
              <label className="block text-xs font-medium text-gray-600 dark:text-gray-400 mb-1">Periyot</label>
              <select
                value={form.periyot}
                onChange={(e) => setForm((f) => ({ ...f, periyot: e.target.value }))}
                className="input-field"
              >
                <option value="AYLIK">Aylık</option>
                <option value="YILLIK">Yıllık</option>
              </select>
            </div>

            <div>
              <label className="block text-xs font-medium text-gray-600 dark:text-gray-400 mb-1">İlk İşlem Tarihi</label>
              <input
                type="date"
                value={form.gelecekIslemTarihi}
                onChange={(e) => setForm((f) => ({ ...f, gelecekIslemTarihi: e.target.value }))}
                className="input-field"
                required
              />
            </div>

            <div className="sm:col-span-2 flex gap-3 justify-end">
              <button type="button" onClick={() => setFormAcik(false)} className="btn-secondary">
                İptal
              </button>
              <button type="submit" disabled={gonderiyor} className="btn-primary">
                {gonderiyor ? 'Ekleniyor...' : 'Ekle'}
              </button>
            </div>
          </form>
        </div>
      )}

      {islemler.length === 0 ? (
        <div className="card text-center py-12">
          <div className="text-5xl mb-4">🔄</div>
          <p className="text-gray-500 dark:text-gray-400 font-medium">Henüz düzenli işlem tanımlamadınız.</p>
          <p className="text-sm text-gray-400 dark:text-gray-500 mt-1">
            Netflix, Spotify, kira gibi ödemeleri bir kez girin, sistem otomatik işlesin.
          </p>
        </div>
      ) : (
        <div className="space-y-3">
          {islemler.map((islem) => (
            <div
              key={islem.id}
              className="card flex items-center justify-between p-4"
            >
              <div className="flex items-center gap-4">
                <div className="w-10 h-10 rounded-xl bg-primary-50 dark:bg-primary-900/30 flex items-center justify-center text-lg">
                  🔄
                </div>
                <div>
                  <p className="font-medium text-gray-900 dark:text-gray-100">{islem.aciklama}</p>
                  <p className="text-xs text-gray-400 dark:text-gray-500">
                    {islem.kategoriIsim} · {islem.periyot === 'AYLIK' ? 'Aylık' : 'Yıllık'} · Sonraki:{' '}
                    {new Date(islem.gelecekIslemTarihi).toLocaleDateString('tr-TR')}
                  </p>
                </div>
              </div>
              <div className="flex items-center gap-4">
                <span className="font-semibold text-gray-900 dark:text-gray-100">
                  {islem.tutar.toLocaleString('tr-TR', { minimumFractionDigits: 2 })} ₺
                </span>
                <button
                  onClick={() => islemSil(islem.id)}
                  className="text-sm text-red-400 hover:text-red-600 transition-colors"
                >
                  İptal
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
