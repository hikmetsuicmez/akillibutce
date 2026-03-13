import { useState, useEffect, useCallback } from 'react'
import { Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { burceLimitiServisi } from '../services/burceLimitiServisi'
import { kategoriServisi } from '../services/kategoriServisi'
import toast from 'react-hot-toast'

function IlerlemeBar({ yuzde }) {
  const renk =
    yuzde >= 100 ? 'bg-red-500' :
    yuzde >= 80 ? 'bg-yellow-400' :
    'bg-primary-500'
  return (
    <div className="w-full bg-gray-100 dark:bg-gray-700 rounded-full h-2 overflow-hidden">
      <div
        className={`h-2 rounded-full transition-all duration-500 ${renk}`}
        style={{ width: `${Math.min(yuzde, 100)}%` }}
      />
    </div>
  )
}

export default function BurceLimitleri() {
  const { premiumMu } = useAuth()
  const [limitler, setLimitler] = useState([])
  const [kategoriler, setKategoriler] = useState([])
  const [yukleniyor, setYukleniyor] = useState(true)
  const [formAcik, setFormAcik] = useState(false)
  const [gonderiyor, setGonderiyor] = useState(false)
  const [form, setForm] = useState({ kategoriId: '', limitTutar: '' })

  const bugun = new Date()
  const ay = bugun.getMonth() + 1
  const yil = bugun.getFullYear()
  const ayIsmi = bugun.toLocaleString('tr-TR', { month: 'long', year: 'numeric' })

  const verileriYukle = useCallback(async () => {
    if (!premiumMu) { setYukleniyor(false); return }
    try {
      const [limitVeri, kategoriVeri] = await Promise.all([
        burceLimitiServisi.limitleriGetir(ay, yil),
        kategoriServisi.kategorileriGetir(),
      ])
      setLimitler(limitVeri)
      setKategoriler(kategoriVeri.filter(k => k.tip === 'GIDER'))
    } catch {
      toast.error('Veriler yüklenemedi.')
    } finally {
      setYukleniyor(false)
    }
  }, [premiumMu, ay, yil])

  useEffect(() => { verileriYukle() }, [verileriYukle])

  const limitKaydet = async (e) => {
    e.preventDefault()
    setGonderiyor(true)
    try {
      await burceLimitiServisi.limitKaydet({
        kategoriId: Number(form.kategoriId),
        limitTutar: parseFloat(form.limitTutar),
        ay,
        yil,
      })
      toast.success('Bütçe limiti kaydedildi!')
      setFormAcik(false)
      setForm({ kategoriId: '', limitTutar: '' })
      verileriYukle()
    } catch (err) {
      toast.error(err.response?.data?.mesaj || 'Limit kaydedilemedi.')
    } finally {
      setGonderiyor(false)
    }
  }

  const limitSil = async (id) => {
    if (!window.confirm('Bu limiti silmek istediğinizden emin misiniz?')) return
    try {
      await burceLimitiServisi.limitSil(id)
      toast.success('Limit silindi.')
      verileriYukle()
    } catch {
      toast.error('Limit silinemedi.')
    }
  }

  if (!premiumMu) {
    return (
      <div className="max-w-md mx-auto mt-16 text-center">
        <div className="text-6xl mb-4">🔒</div>
        <h2 className="text-2xl font-bold text-gray-900 dark:text-gray-100 mb-2">Premium Özelliği</h2>
        <p className="text-gray-500 dark:text-gray-400 mb-6">
          Kategori bazlı bütçe limiti belirleme ve otomatik bildirim sistemi sadece Premium üyelere açıktır.
        </p>
        <Link to="/premium" className="btn-primary inline-block">Premium'a Geç</Link>
      </div>
    )
  }

  if (yukleniyor) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-600" />
      </div>
    )
  }

  return (
    <div className="max-w-3xl mx-auto space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900 dark:text-gray-100">Bütçe Limitleri</h1>
          <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">
            {ayIsmi} için kategori harcama limitleri
          </p>
        </div>
        <button onClick={() => setFormAcik(true)} className="btn-primary flex items-center gap-2">
          <span>+</span> Limit Ekle
        </button>
      </div>

      {formAcik && (
        <div className="card">
          <h2 className="font-semibold text-gray-900 dark:text-gray-100 mb-4">Yeni Limit Belirle</h2>
          <form onSubmit={limitKaydet} className="flex flex-wrap gap-4 items-end">
            <div className="flex-1 min-w-40">
              <label className="block text-xs font-medium text-gray-600 dark:text-gray-400 mb-1">Kategori</label>
              <select
                value={form.kategoriId}
                onChange={(e) => setForm(f => ({ ...f, kategoriId: e.target.value }))}
                className="input-field"
                required
              >
                <option value="">Seçiniz...</option>
                {kategoriler.map(k => (
                  <option key={k.id} value={k.id}>{k.isim}</option>
                ))}
              </select>
            </div>
            <div className="flex-1 min-w-40">
              <label className="block text-xs font-medium text-gray-600 dark:text-gray-400 mb-1">Aylık Limit (₺)</label>
              <input
                type="number"
                value={form.limitTutar}
                onChange={(e) => setForm(f => ({ ...f, limitTutar: e.target.value }))}
                className="input-field"
                placeholder="3000"
                min="0.01"
                step="0.01"
                required
              />
            </div>
            <div className="flex gap-2">
              <button type="button" onClick={() => setFormAcik(false)} className="btn-secondary">İptal</button>
              <button type="submit" disabled={gonderiyor} className="btn-primary">
                {gonderiyor ? 'Kaydediliyor...' : 'Kaydet'}
              </button>
            </div>
          </form>
        </div>
      )}

      {limitler.length === 0 ? (
        <div className="card text-center py-12">
          <div className="text-5xl mb-4">📊</div>
          <p className="text-gray-500 dark:text-gray-400 font-medium">Bu ay için limit belirlenmedi.</p>
          <p className="text-sm text-gray-400 dark:text-gray-500 mt-1">
            Kategori limitlieri belirleyin, %80 ve %100 dolduğunda bildirim alın.
          </p>
        </div>
      ) : (
        <div className="space-y-3">
          {limitler.map((limit) => (
            <div key={limit.id} className="card">
              <div className="flex items-center justify-between mb-2">
                <div className="flex items-center gap-2">
                  <span className="font-medium text-gray-900 dark:text-gray-100">{limit.kategoriIsim}</span>
                  {limit.kullanim_yuzdesi >= 100 && (
                    <span className="text-xs bg-red-100 dark:bg-red-900/30 text-red-600 dark:text-red-400 px-2 py-0.5 rounded-full">Limit Aşıldı!</span>
                  )}
                  {limit.kullanim_yuzdesi >= 80 && limit.kullanim_yuzdesi < 100 && (
                    <span className="text-xs bg-yellow-100 dark:bg-yellow-900/30 text-yellow-600 dark:text-yellow-400 px-2 py-0.5 rounded-full">Dikkat</span>
                  )}
                </div>
                <button onClick={() => limitSil(limit.id)} className="text-sm text-red-400 hover:text-red-600 transition-colors">Sil</button>
              </div>
              <IlerlemeBar yuzde={limit.kullanim_yuzdesi || limit.kullanımYuzdesi || 0} />
              <div className="flex justify-between text-xs text-gray-500 dark:text-gray-400 mt-1.5">
                <span>{Number(limit.mevcutHarcama || 0).toLocaleString('tr-TR', { minimumFractionDigits: 2 })} ₺ harcandı</span>
                <span>Limit: {Number(limit.limitTutar).toLocaleString('tr-TR', { minimumFractionDigits: 2 })} ₺</span>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
