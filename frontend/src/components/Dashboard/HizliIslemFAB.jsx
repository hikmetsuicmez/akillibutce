import { useState, useEffect } from 'react'
import toast from 'react-hot-toast'
import { kategoriServisi } from '../../services/kategoriServisi'
import { islemServisi } from '../../services/islemServisi'

export default function HizliIslemFAB({ onIslemEklendi }) {
  const [acik, setAcik] = useState(false)
  const [kategoriler, setKategoriler] = useState([])
  const [yukleniyor, setYukleniyor] = useState(false)
  const [form, setForm] = useState({
    kategoriId: '',
    miktar: '',
    aciklama: '',
    islemTarihi: new Date().toISOString().split('T')[0],
  })

  useEffect(() => {
    if (acik && kategoriler.length === 0) {
      kategoriServisi.kategorileriGetir().then(setKategoriler).catch(() => {})
    }
  }, [acik, kategoriler.length])

  const degistir = (e) => setForm((f) => ({ ...f, [e.target.name]: e.target.value }))

  const gonder = async (e) => {
    e.preventDefault()
    if (!form.kategoriId || !form.miktar) {
      toast.error('Kategori ve miktar zorunludur.')
      return
    }
    setYukleniyor(true)
    try {
      await islemServisi.islemEkle({
        ...form,
        kategoriId: Number(form.kategoriId),
        miktar: parseFloat(form.miktar),
      })
      toast.success('İşlem başarıyla eklendi!')
      setAcik(false)
      setForm({
        kategoriId: '',
        miktar: '',
        aciklama: '',
        islemTarihi: new Date().toISOString().split('T')[0],
      })
      onIslemEklendi?.()
    } catch (err) {
      toast.error(err.response?.data?.mesaj || 'İşlem eklenemedi.')
    } finally {
      setYukleniyor(false)
    }
  }

  const gelirKategorileri = kategoriler.filter((k) => k.tip === 'GELIR')
  const giderKategorileri = kategoriler.filter((k) => k.tip === 'GIDER')

  return (
    <>
      {/* Backdrop */}
      {acik && (
        <div
          className="fixed inset-0 bg-black/40 dark:bg-black/60 z-40"
          onClick={() => setAcik(false)}
        />
      )}

      {/* Modal */}
      {acik && (
        <div className="fixed bottom-24 right-6 z-50 w-80 bg-white dark:bg-gray-900 rounded-2xl shadow-2xl border border-gray-100 dark:border-gray-700 p-5 animate-slide-up">
          <div className="flex items-center justify-between mb-4">
            <h3 className="font-semibold text-gray-900 dark:text-gray-100">Hızlı İşlem Ekle</h3>
            <button
              onClick={() => setAcik(false)}
              className="text-gray-400 hover:text-gray-600 dark:hover:text-gray-200 transition-colors"
            >
              <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                <path fillRule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clipRule="evenodd" />
              </svg>
            </button>
          </div>

          <form onSubmit={gonder} className="space-y-3">
            <div>
              <label className="block text-xs font-medium text-gray-600 dark:text-gray-400 mb-1">Kategori</label>
              <select
                name="kategoriId"
                value={form.kategoriId}
                onChange={degistir}
                className="input-field text-sm"
                required
              >
                <option value="">Seçiniz...</option>
                {gelirKategorileri.length > 0 && (
                  <optgroup label="Gelir">
                    {gelirKategorileri.map((k) => (
                      <option key={k.id} value={k.id}>{k.isim}</option>
                    ))}
                  </optgroup>
                )}
                {giderKategorileri.length > 0 && (
                  <optgroup label="Gider">
                    {giderKategorileri.map((k) => (
                      <option key={k.id} value={k.id}>{k.isim}</option>
                    ))}
                  </optgroup>
                )}
              </select>
            </div>

            <div>
              <label className="block text-xs font-medium text-gray-600 dark:text-gray-400 mb-1">Miktar (₺)</label>
              <input
                type="number"
                name="miktar"
                value={form.miktar}
                onChange={degistir}
                className="input-field text-sm"
                placeholder="0.00"
                min="0.01"
                step="0.01"
                required
              />
            </div>

            <div>
              <label className="block text-xs font-medium text-gray-600 dark:text-gray-400 mb-1">Tarih</label>
              <input
                type="date"
                name="islemTarihi"
                value={form.islemTarihi}
                onChange={degistir}
                className="input-field text-sm"
                required
              />
            </div>

            <div>
              <label className="block text-xs font-medium text-gray-600 dark:text-gray-400 mb-1">
                Açıklama <span className="text-gray-400">(opsiyonel)</span>
              </label>
              <input
                type="text"
                name="aciklama"
                value={form.aciklama}
                onChange={degistir}
                className="input-field text-sm"
                placeholder="Örn: Akşam yemeği"
              />
            </div>

            <button type="submit" disabled={yukleniyor} className="btn-primary w-full text-sm">
              {yukleniyor ? 'Ekleniyor...' : 'İşlem Ekle'}
            </button>
          </form>
        </div>
      )}

      {/* FAB Butonu */}
      <button
        onClick={() => setAcik((o) => !o)}
        className={`fixed bottom-6 right-6 z-50 w-14 h-14 rounded-full shadow-lg flex items-center justify-center text-white transition-all duration-200 ${
          acik
            ? 'bg-red-500 hover:bg-red-600 rotate-45'
            : 'bg-primary-600 hover:bg-primary-700 hover:scale-110'
        }`}
        title="Hızlı işlem ekle"
      >
        <svg xmlns="http://www.w3.org/2000/svg" className="h-7 w-7" viewBox="0 0 20 20" fill="currentColor">
          <path fillRule="evenodd" d="M10 3a1 1 0 011 1v5h5a1 1 0 110 2h-5v5a1 1 0 11-2 0v-5H4a1 1 0 110-2h5V4a1 1 0 011-1z" clipRule="evenodd" />
        </svg>
      </button>
    </>
  )
}
