import { useState, useRef } from 'react'
import { islemServisi } from '../../services/islemServisi'
import toast from 'react-hot-toast'

export default function FisYukleModal({ onKapat, onDoldur }) {
  const [surukle, setSurukle] = useState(false)
  const [yukleniyor, setYukleniyor] = useState(false)
  const [onizleme, setOnizleme] = useState(null)
  const inputRef = useRef(null)

  const dosyaIsle = async (dosya) => {
    if (!dosya || !dosya.type.startsWith('image/')) {
      toast.error('Lütfen bir görsel dosyası seçin.')
      return
    }
    setOnizleme(URL.createObjectURL(dosya))
    setYukleniyor(true)
    try {
      const sonuc = await islemServisi.fisTara(dosya)
      onDoldur(sonuc)
      toast.success('Fiş başarıyla okundu! Form dolduruldu.')
      onKapat()
    } catch (err) {
      if (err.response?.status === 403) {
        toast.error('Fiş tarama Premium üyelere özeldir.')
      } else {
        toast.error('Fiş okunamadı, lütfen tekrar deneyin.')
      }
      setOnizleme(null)
    } finally {
      setYukleniyor(false)
    }
  }

  const surukBirak = (e) => {
    e.preventDefault()
    setSurukle(false)
    const dosya = e.dataTransfer.files[0]
    if (dosya) dosyaIsle(dosya)
  }

  return (
    <div className="fixed inset-0 bg-black/50 dark:bg-black/70 z-50 flex items-center justify-center p-4">
      <div className="bg-white dark:bg-gray-900 rounded-2xl shadow-2xl w-full max-w-md p-6">
        <div className="flex items-center justify-between mb-5">
          <div>
            <h2 className="text-lg font-semibold text-gray-900 dark:text-gray-100">Fiş / Fatura Yükle</h2>
            <p className="text-xs text-gray-400 dark:text-gray-500 mt-0.5">AI ile otomatik form doldurma (Premium)</p>
          </div>
          <button
            onClick={onKapat}
            className="text-gray-400 hover:text-gray-600 dark:hover:text-gray-200 transition-colors"
          >
            <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
              <path fillRule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clipRule="evenodd" />
            </svg>
          </button>
        </div>

        {yukleniyor ? (
          <div className="flex flex-col items-center justify-center py-12 gap-4">
            {onizleme && (
              <img src={onizleme} alt="Fiş önizleme" className="w-32 h-32 object-cover rounded-lg opacity-50" />
            )}
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-600" />
            <p className="text-sm text-gray-500 dark:text-gray-400">Fiş okunuyor, lütfen bekleyin...</p>
          </div>
        ) : (
          <div
            onDragOver={(e) => { e.preventDefault(); setSurukle(true) }}
            onDragLeave={() => setSurukle(false)}
            onDrop={surukBirak}
            onClick={() => inputRef.current?.click()}
            className={`border-2 border-dashed rounded-xl p-10 text-center cursor-pointer transition-colors ${
              surukle
                ? 'border-primary-500 bg-primary-50 dark:bg-primary-900/20'
                : 'border-gray-200 dark:border-gray-700 hover:border-primary-300 dark:hover:border-primary-600'
            }`}
          >
            <input
              ref={inputRef}
              type="file"
              accept="image/*"
              className="hidden"
              onChange={(e) => { if (e.target.files[0]) dosyaIsle(e.target.files[0]) }}
            />
            <div className="text-4xl mb-3">📷</div>
            <p className="font-medium text-gray-700 dark:text-gray-300">
              Fiş / fatura görselini buraya sürükleyin
            </p>
            <p className="text-sm text-gray-400 dark:text-gray-500 mt-1">
              veya tıklayarak seçin (JPG, PNG, WEBP)
            </p>
          </div>
        )}

        <p className="text-xs text-gray-400 dark:text-gray-500 text-center mt-4">
          Tutar, tarih ve KDV oranı otomatik algılanır ve form alanlarına doldurulur.
        </p>
      </div>
    </div>
  )
}
