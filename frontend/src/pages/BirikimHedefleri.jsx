import { useState, useEffect, useCallback, useRef } from 'react'
import { useAuth } from '../context/AuthContext'
import { hedefServisi } from '../services/hedefServisi'
import toast from 'react-hot-toast'

function KonfettiAnimasyonu({ goster }) {
  const canvasRef = useRef(null)

  useEffect(() => {
    if (!goster) return
    const canvas = canvasRef.current
    if (!canvas) return
    const ctx = canvas.getContext('2d')
    canvas.width = window.innerWidth
    canvas.height = window.innerHeight

    const parcaciklar = Array.from({ length: 150 }, () => ({
      x: Math.random() * canvas.width,
      y: Math.random() * canvas.height - canvas.height,
      r: Math.random() * 8 + 3,
      d: Math.random() * 150 + 10,
      renk: `hsl(${Math.random() * 360}, 90%, 60%)`,
      tilt: Math.random() * 10 - 10,
      tiltAci: 0,
    }))

    let animId
    let frame = 0
    const ciz = () => {
      ctx.clearRect(0, 0, canvas.width, canvas.height)
      frame++
      parcaciklar.forEach((p) => {
        p.tiltAci += 0.1
        p.y += (Math.cos(frame / 10 + p.d) + 3) * 1.5
        p.x += Math.sin(frame / 10) * 0.5
        p.tilt = Math.sin(p.tiltAci) * 12

        ctx.beginPath()
        ctx.lineWidth = p.r / 2
        ctx.strokeStyle = p.renk
        ctx.moveTo(p.x + p.tilt + p.r / 4, p.y)
        ctx.lineTo(p.x + p.tilt, p.y + p.tilt + p.r / 4)
        ctx.stroke()
      })
      if (frame < 180) {
        animId = requestAnimationFrame(ciz)
      } else {
        ctx.clearRect(0, 0, canvas.width, canvas.height)
      }
    }
    ciz()
    return () => cancelAnimationFrame(animId)
  }, [goster])

  if (!goster) return null
  return (
    <canvas
      ref={canvasRef}
      className="fixed inset-0 pointer-events-none z-50"
    />
  )
}

function IlerlemeBar({ yuzde }) {
  const renk =
    yuzde >= 100
      ? 'bg-green-500'
      : yuzde >= 75
      ? 'bg-primary-500'
      : yuzde >= 40
      ? 'bg-yellow-400'
      : 'bg-gray-300 dark:bg-gray-600'

  return (
    <div className="w-full bg-gray-100 dark:bg-gray-700 rounded-full h-3 overflow-hidden">
      <div
        className={`h-3 rounded-full transition-all duration-700 ${renk}`}
        style={{ width: `${Math.min(yuzde, 100)}%` }}
      />
    </div>
  )
}

export default function BirikimHedefleri() {
  const { premiumMu } = useAuth()
  const [hedefler, setHedefler] = useState([])
  const [yukleniyor, setYukleniyor] = useState(true)
  const [konfetti, setKonfetti] = useState(false)
  const [formAcik, setFormAcik] = useState(false)
  const [paraEkleAcik, setParaEkleAcik] = useState(null)
  const [form, setForm] = useState({ baslik: '', hedefTutar: '', sonTarih: '' })
  const [paraMiktar, setParaMiktar] = useState('')
  const [gonderiyor, setGonderiyor] = useState(false)

  const hedefleriYukle = useCallback(async () => {
    try {
      const veri = await hedefServisi.hedefleriGetir()
      setHedefler(veri)
    } catch {
      toast.error('Hedefler yüklenemedi.')
    } finally {
      setYukleniyor(false)
    }
  }, [])

  useEffect(() => { hedefleriYukle() }, [hedefleriYukle])

  const hedefOlustur = async (e) => {
    e.preventDefault()
    setGonderiyor(true)
    try {
      await hedefServisi.hedefOlustur({
        baslik: form.baslik,
        hedefTutar: parseFloat(form.hedefTutar),
        sonTarih: form.sonTarih || null,
      })
      toast.success('Hedef oluşturuldu!')
      setFormAcik(false)
      setForm({ baslik: '', hedefTutar: '', sonTarih: '' })
      hedefleriYukle()
    } catch (err) {
      toast.error(err.response?.data?.mesaj || err.response?.data?.message || 'Hedef oluşturulamadı.')
    } finally {
      setGonderiyor(false)
    }
  }

  const paraEkle = async (hedefId) => {
    if (!paraMiktar || parseFloat(paraMiktar) <= 0) {
      toast.error('Geçerli bir miktar giriniz.')
      return
    }
    setGonderiyor(true)
    try {
      const guncellenen = await hedefServisi.paraEkle(hedefId, parseFloat(paraMiktar))
      if (guncellenen.durum === 'TAMAMLANDI') {
        setKonfetti(true)
        setTimeout(() => setKonfetti(false), 3500)
        toast.success('🎉 Tebrikler! Hedefinize ulaştınız!')
      } else {
        toast.success('Para eklendi!')
      }
      setParaEkleAcik(null)
      setParaMiktar('')
      hedefleriYukle()
    } catch (err) {
      toast.error(err.response?.data?.mesaj || 'Para eklenemedi.')
    } finally {
      setGonderiyor(false)
    }
  }

  const hedefSil = async (id) => {
    if (!window.confirm('Bu hedefi silmek istediğinizden emin misiniz?')) return
    try {
      await hedefServisi.hedefSil(id)
      toast.success('Hedef silindi.')
      hedefleriYukle()
    } catch {
      toast.error('Hedef silinemedi.')
    }
  }

  if (yukleniyor) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-600" />
      </div>
    )
  }

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      <KonfettiAnimasyonu goster={konfetti} />

      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900 dark:text-gray-100">Birikim Hedefleri</h1>
          <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">
            {premiumMu ? 'Sınırsız hedef oluşturabilirsiniz.' : 'Ücretsiz planda 1 aktif hedef.'}
          </p>
        </div>
        <button onClick={() => setFormAcik(true)} className="btn-primary flex items-center gap-2">
          <span>+</span> Yeni Hedef
        </button>
      </div>

      {/* Yeni Hedef Formu */}
      {formAcik && (
        <div className="card">
          <h2 className="font-semibold text-gray-900 dark:text-gray-100 mb-4">Yeni Hedef Oluştur</h2>
          <form onSubmit={hedefOlustur} className="grid grid-cols-1 sm:grid-cols-3 gap-4">
            <div>
              <label className="block text-xs font-medium text-gray-600 dark:text-gray-400 mb-1">Başlık</label>
              <input
                type="text"
                value={form.baslik}
                onChange={(e) => setForm((f) => ({ ...f, baslik: e.target.value }))}
                className="input-field"
                placeholder="Tatil, Araba..."
                required
              />
            </div>
            <div>
              <label className="block text-xs font-medium text-gray-600 dark:text-gray-400 mb-1">Hedef Tutar (₺)</label>
              <input
                type="number"
                value={form.hedefTutar}
                onChange={(e) => setForm((f) => ({ ...f, hedefTutar: e.target.value }))}
                className="input-field"
                placeholder="10000"
                min="0.01"
                step="0.01"
                required
              />
            </div>
            <div>
              <label className="block text-xs font-medium text-gray-600 dark:text-gray-400 mb-1">
                Son Tarih <span className="text-gray-400">(opsiyonel)</span>
              </label>
              <input
                type="date"
                value={form.sonTarih}
                onChange={(e) => setForm((f) => ({ ...f, sonTarih: e.target.value }))}
                className="input-field"
              />
            </div>
            <div className="sm:col-span-3 flex gap-3 justify-end">
              <button type="button" onClick={() => setFormAcik(false)} className="btn-secondary">
                İptal
              </button>
              <button type="submit" disabled={gonderiyor} className="btn-primary">
                {gonderiyor ? 'Oluşturuluyor...' : 'Oluştur'}
              </button>
            </div>
          </form>
        </div>
      )}

      {/* Hedef Listesi */}
      {hedefler.length === 0 ? (
        <div className="card text-center py-12">
          <div className="text-5xl mb-4">🎯</div>
          <p className="text-gray-500 dark:text-gray-400 font-medium">Henüz hedef oluşturmadınız.</p>
          <p className="text-sm text-gray-400 dark:text-gray-500 mt-1">
            Tatil, araba, acil fon gibi hedefler belirleyin ve birikiminizi takip edin.
          </p>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
          {hedefler.map((hedef) => (
            <div key={hedef.id} className="card relative">
              {hedef.durum === 'TAMAMLANDI' && (
                <div className="absolute top-3 right-3">
                  <span className="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium bg-green-100 dark:bg-green-900 text-green-700 dark:text-green-300">
                    ✓ Tamamlandı
                  </span>
                </div>
              )}

              <div className="mb-3">
                <h3 className="font-semibold text-gray-900 dark:text-gray-100 text-lg">{hedef.baslik}</h3>
                {hedef.sonTarih && (
                  <p className="text-xs text-gray-400 dark:text-gray-500 mt-0.5">
                    Son tarih: {new Date(hedef.sonTarih).toLocaleDateString('tr-TR')}
                  </p>
                )}
              </div>

              <div className="mb-3">
                <div className="flex justify-between text-sm mb-1.5">
                  <span className="text-gray-600 dark:text-gray-400">
                    {hedef.mevcutTutar.toLocaleString('tr-TR', { minimumFractionDigits: 2 })} ₺
                  </span>
                  <span className="font-medium text-gray-900 dark:text-gray-100">
                    {hedef.hedefTutar.toLocaleString('tr-TR', { minimumFractionDigits: 2 })} ₺
                  </span>
                </div>
                <IlerlemeBar yuzde={hedef.ilerlemeYuzdesi} />
                <p className="text-right text-xs text-gray-500 dark:text-gray-400 mt-1">
                  %{hedef.ilerlemeYuzdesi.toFixed(1)}
                </p>
              </div>

              <div className="flex gap-2 mt-4">
                {hedef.durum !== 'TAMAMLANDI' && (
                  <>
                    {paraEkleAcik === hedef.id ? (
                      <div className="flex gap-2 flex-1">
                        <input
                          type="number"
                          value={paraMiktar}
                          onChange={(e) => setParaMiktar(e.target.value)}
                          className="input-field text-sm flex-1"
                          placeholder="Miktar (₺)"
                          min="0.01"
                          step="0.01"
                          autoFocus
                        />
                        <button
                          onClick={() => paraEkle(hedef.id)}
                          disabled={gonderiyor}
                          className="btn-primary text-sm px-3"
                        >
                          Ekle
                        </button>
                        <button
                          onClick={() => { setParaEkleAcik(null); setParaMiktar('') }}
                          className="btn-secondary text-sm px-3"
                        >
                          ✕
                        </button>
                      </div>
                    ) : (
                      <button
                        onClick={() => setParaEkleAcik(hedef.id)}
                        className="btn-primary text-sm flex-1"
                      >
                        + Para Ekle
                      </button>
                    )}
                  </>
                )}
                <button
                  onClick={() => hedefSil(hedef.id)}
                  className="text-sm text-red-500 dark:text-red-400 hover:text-red-700 dark:hover:text-red-300 px-2 transition-colors"
                >
                  Sil
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
