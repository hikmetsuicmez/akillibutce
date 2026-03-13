import { useState, useEffect, useCallback } from 'react'
import { useAuth } from '../context/AuthContext'
import { islemServisi } from '../services/islemServisi'
import { kategoriServisi } from '../services/kategoriServisi'
import IslemFormu from '../components/Islemler/IslemFormu'
import IslemListesi from '../components/Islemler/IslemListesi'
import FisYukleModal from '../components/Islemler/FisYukleModal'
import toast from 'react-hot-toast'

export default function Islemler() {
  const { premiumMu } = useAuth()
  const [islemler, setIslemler] = useState([])
  const [kategoriler, setKategoriler] = useState([])
  const [formAcik, setFormAcik] = useState(false)
  const [fisModalAcik, setFisModalAcik] = useState(false)
  const [yukleniyor, setYukleniyor] = useState(true)
  const [baslangic, setBaslangic] = useState('')
  const [bitis, setBitis] = useState('')
  const [ocrOnDeger, setOcrOnDeger] = useState(null)

  const verileriYukle = useCallback(async () => {
    setYukleniyor(true)
    try {
      const [islemVeri, kategoriVeri] = await Promise.all([
        islemServisi.islemleriGetir(baslangic || undefined, bitis || undefined),
        kategoriServisi.kategorileriGetir(),
      ])
      setIslemler(islemVeri)
      setKategoriler(kategoriVeri)
    } catch {
      toast.error('Veriler yüklenemedi.')
    } finally {
      setYukleniyor(false)
    }
  }, [baslangic, bitis])

  useEffect(() => { verileriYukle() }, [verileriYukle])

  const islemEkle = async (veri) => {
    await islemServisi.islemEkle(veri)
    toast.success('İşlem eklendi.')
    setFormAcik(false)
    setOcrOnDeger(null)
    verileriYukle()
  }

  const islemSil = async (id) => {
    if (!confirm('Bu işlemi silmek istediğinizden emin misiniz?')) return
    try {
      await islemServisi.islemSil(id)
      toast.success('İşlem silindi.')
      setIslemler(prev => prev.filter(i => i.id !== id))
    } catch {
      toast.error('İşlem silinemedi.')
    }
  }

  const ocrdanDoldur = (ocrSonuc) => {
    setOcrOnDeger({
      miktar: ocrSonuc.tutar,
      islemTarihi: ocrSonuc.tarih,
      aciklama: ocrSonuc.aciklama || '',
    })
    setFormAcik(true)
  }

  return (
    <div className="space-y-6">
      {fisModalAcik && (
        <FisYukleModal
          onKapat={() => setFisModalAcik(false)}
          onDoldur={ocrdanDoldur}
        />
      )}

      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-gray-900 dark:text-gray-100">İşlemler</h1>
        <div className="flex items-center gap-2">
          <button
            onClick={() => {
              if (!premiumMu) {
                toast.error('Fiş tarama Premium özelliğidir.')
                return
              }
              setFisModalAcik(true)
            }}
            className="flex items-center gap-2 text-sm border border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-800 text-gray-700 dark:text-gray-300 px-3 py-2 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors"
          >
            <span>📷</span>
            Fiş Yükle
            {!premiumMu && (
              <span className="text-xs bg-yellow-100 dark:bg-yellow-900 text-yellow-700 dark:text-yellow-300 px-1 rounded">PRO</span>
            )}
          </button>
          <button onClick={() => { setOcrOnDeger(null); setFormAcik(true) }} className="btn-primary">
            + Yeni İşlem
          </button>
        </div>
      </div>

      {/* Tarih filtreleri */}
      <div className="card">
        <div className="flex flex-wrap gap-4 items-end">
          <div>
            <label className="block text-xs font-medium text-gray-600 dark:text-gray-400 mb-1">Başlangıç</label>
            <input
              type="date"
              value={baslangic}
              onChange={e => setBaslangic(e.target.value)}
              className="input-field w-auto"
            />
          </div>
          <div>
            <label className="block text-xs font-medium text-gray-600 dark:text-gray-400 mb-1">Bitiş</label>
            <input
              type="date"
              value={bitis}
              onChange={e => setBitis(e.target.value)}
              className="input-field w-auto"
            />
          </div>
          <button
            onClick={() => { setBaslangic(''); setBitis('') }}
            className="btn-secondary text-sm"
          >
            Filtreyi Temizle
          </button>
        </div>
      </div>

      {/* Islem listesi */}
      <div className="card">
        {yukleniyor ? (
          <div className="flex justify-center py-10">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-600" />
          </div>
        ) : (
          <IslemListesi islemler={islemler} onSil={islemSil} />
        )}
      </div>

      {/* Modal form */}
      {formAcik && (
        <div className="fixed inset-0 bg-black/50 dark:bg-black/70 flex items-center justify-center z-50 p-4">
          <div className="bg-white dark:bg-gray-900 rounded-xl w-full max-w-md shadow-xl">
            <div className="flex items-center justify-between p-6 border-b border-gray-100 dark:border-gray-800">
              <h2 className="text-lg font-semibold text-gray-900 dark:text-gray-100">
                {ocrOnDeger ? '📷 Fiş\'ten İşlem Ekle' : 'Yeni İşlem Ekle'}
              </h2>
              <button
                onClick={() => { setFormAcik(false); setOcrOnDeger(null) }}
                className="text-gray-400 hover:text-gray-600 dark:hover:text-gray-200 text-xl"
              >
                ×
              </button>
            </div>
            <div className="p-6">
              <IslemFormu kategoriler={kategoriler} onGonder={islemEkle} onDeger={ocrOnDeger} />
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
