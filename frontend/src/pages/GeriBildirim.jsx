import { useState } from 'react'
import { geriBildirimServisi } from '../services/geriBildirimServisi'
import toast from 'react-hot-toast'

function YildizSecici({ deger, onDegis }) {
  const [hover, setHover] = useState(0)

  const etiketler = ['', 'Çok Kötü', 'Kötü', 'Orta', 'İyi', 'Mükemmel']

  return (
    <div className="flex flex-col items-center gap-2">
      <div className="flex gap-2">
        {[1, 2, 3, 4, 5].map((yildiz) => (
          <button
            key={yildiz}
            type="button"
            onClick={() => onDegis(yildiz)}
            onMouseEnter={() => setHover(yildiz)}
            onMouseLeave={() => setHover(0)}
            className="text-4xl transition-transform hover:scale-110"
          >
            <span className={`${(hover || deger) >= yildiz ? 'text-yellow-400' : 'text-gray-200 dark:text-gray-700'}`}>
              ★
            </span>
          </button>
        ))}
      </div>
      {(hover || deger) > 0 && (
        <p className="text-sm font-medium text-gray-600 dark:text-gray-400">
          {etiketler[hover || deger]}
        </p>
      )}
    </div>
  )
}

export default function GeriBildirim() {
  const [puan, setPuan] = useState(0)
  const [mesaj, setMesaj] = useState('')
  const [gonderiyor, setGonderiyor] = useState(false)
  const [gonderildi, setGonderildi] = useState(false)

  const gonder = async (e) => {
    e.preventDefault()
    if (puan === 0) {
      toast.error('Lütfen bir puan verin.')
      return
    }
    setGonderiyor(true)
    try {
      await geriBildirimServisi.gonder({ puan, mesaj: mesaj || null })
      setGonderildi(true)
      toast.success('Geri bildiriminiz iletildi. Teşekkürler!')
    } catch {
      toast.error('Gönderilemedi, lütfen tekrar deneyin.')
    } finally {
      setGonderiyor(false)
    }
  }

  if (gonderildi) {
    return (
      <div className="max-w-md mx-auto mt-20 text-center">
        <div className="text-6xl mb-4">🙏</div>
        <h2 className="text-2xl font-bold text-gray-900 dark:text-gray-100 mb-2">Teşekkürler!</h2>
        <p className="text-gray-500 dark:text-gray-400">
          Geri bildiriminiz başarıyla gönderildi. Uygulamayı daha iyi hale getirmemize yardımcı olduğunuz için teşekkür ederiz.
        </p>
        <button
          onClick={() => { setGonderildi(false); setPuan(0); setMesaj('') }}
          className="btn-secondary mt-6"
        >
          Yeni Geri Bildirim
        </button>
      </div>
    )
  }

  return (
    <div className="max-w-lg mx-auto space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900 dark:text-gray-100">Geri Bildirim</h1>
        <p className="text-gray-500 dark:text-gray-400 mt-1">
          Deneyiminizi paylaşarak uygulamayı geliştirmemize yardımcı olun.
        </p>
      </div>

      <div className="card">
        <form onSubmit={gonder} className="space-y-6">
          <div>
            <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-4 text-center">
              Uygulamayı nasıl değerlendirirsiniz?
            </label>
            <YildizSecici deger={puan} onDegis={setPuan} />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
              Açıklama <span className="text-gray-400">(opsiyonel)</span>
            </label>
            <textarea
              value={mesaj}
              onChange={(e) => setMesaj(e.target.value)}
              className="input-field resize-none"
              rows={4}
              placeholder="Önerilerinizi, beğendiklerinizi veya eksik bulduğunuz özellikleri buraya yazabilirsiniz..."
              maxLength={1000}
            />
            <p className="text-right text-xs text-gray-400 mt-1">{mesaj.length}/1000</p>
          </div>

          <button
            type="submit"
            disabled={gonderiyor || puan === 0}
            className="btn-primary w-full"
          >
            {gonderiyor ? 'Gönderiliyor...' : 'Gönder'}
          </button>
        </form>
      </div>
    </div>
  )
}
