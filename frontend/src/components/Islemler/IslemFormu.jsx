import { useState, useEffect } from 'react'
import toast from 'react-hot-toast'

export default function IslemFormu({ kategoriler, onGonder, onDeger }) {
  const [form, setForm] = useState({
    kategoriId: '',
    miktar: '',
    aciklama: '',
    islemTarihi: new Date().toISOString().split('T')[0],
  })
  const [yukleniyor, setYukleniyor] = useState(false)

  useEffect(() => {
    if (onDeger) {
      setForm((f) => ({
        ...f,
        miktar: onDeger.miktar != null ? String(onDeger.miktar) : f.miktar,
        islemTarihi: onDeger.islemTarihi || f.islemTarihi,
        aciklama: onDeger.aciklama || f.aciklama,
      }))
    }
  }, [onDeger])

  const degistir = (e) => setForm(f => ({ ...f, [e.target.name]: e.target.value }))

  const gonder = async (e) => {
    e.preventDefault()
    if (!form.kategoriId || !form.miktar) {
      toast.error('Kategori ve miktar zorunludur.')
      return
    }
    setYukleniyor(true)
    try {
      await onGonder({
        ...form,
        kategoriId: Number(form.kategoriId),
        miktar: parseFloat(form.miktar),
      })
    } catch (err) {
      toast.error(err.response?.data?.mesaj || 'İşlem eklenemedi.')
    } finally {
      setYukleniyor(false)
    }
  }

  const gelirKategorileri = kategoriler.filter(k => k.tip === 'GELIR')
  const giderKategorileri = kategoriler.filter(k => k.tip === 'GIDER')

  return (
    <form onSubmit={gonder} className="space-y-4">
      {onDeger && (
        <div className="bg-primary-50 dark:bg-primary-900/20 border border-primary-200 dark:border-primary-800 rounded-lg p-3 text-sm text-primary-700 dark:text-primary-300">
          Fiş verisi form alanlarına dolduruldu. Kategoriyi seçip onaylayın.
        </div>
      )}

      <div>
        <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Kategori</label>
        <select
          name="kategoriId"
          value={form.kategoriId}
          onChange={degistir}
          className="input-field"
          required
        >
          <option value="">Seçiniz...</option>
          {gelirKategorileri.length > 0 && (
            <optgroup label="Gelir">
              {gelirKategorileri.map(k => (
                <option key={k.id} value={k.id}>{k.isim}</option>
              ))}
            </optgroup>
          )}
          {giderKategorileri.length > 0 && (
            <optgroup label="Gider">
              {giderKategorileri.map(k => (
                <option key={k.id} value={k.id}>{k.isim}</option>
              ))}
            </optgroup>
          )}
        </select>
      </div>

      <div>
        <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Miktar (₺)</label>
        <input
          type="number"
          name="miktar"
          value={form.miktar}
          onChange={degistir}
          className="input-field"
          placeholder="0.00"
          min="0.01"
          step="0.01"
          required
        />
      </div>

      <div>
        <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Tarih</label>
        <input
          type="date"
          name="islemTarihi"
          value={form.islemTarihi}
          onChange={degistir}
          className="input-field"
          required
        />
      </div>

      <div>
        <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
          Açıklama <span className="text-gray-400">(opsiyonel)</span>
        </label>
        <input
          type="text"
          name="aciklama"
          value={form.aciklama}
          onChange={degistir}
          className="input-field"
          placeholder="Örn: Haziran kirası"
        />
      </div>

      <button
        type="submit"
        disabled={yukleniyor}
        className="btn-primary w-full"
      >
        {yukleniyor ? 'Ekleniyor...' : 'İşlem Ekle'}
      </button>
    </form>
  )
}
