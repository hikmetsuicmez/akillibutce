import { useState, useEffect, useCallback } from 'react'
import { Link } from 'react-router-dom'
import { islemServisi } from '../services/islemServisi'
import { raporServisi } from '../services/raporServisi'
import { useAuth } from '../context/AuthContext'
import OzetKart from '../components/Dashboard/OzetKart'
import PastaGrafik from '../components/Dashboard/PastaGrafik'
import SonIslemler from '../components/Dashboard/SonIslemler'
import HizliIslemFAB from '../components/Dashboard/HizliIslemFAB'
import toast from 'react-hot-toast'

function PremiumPaywallModal({ onKapat }) {
  return (
    <div className="fixed inset-0 bg-black/50 dark:bg-black/70 z-50 flex items-center justify-center p-4">
      <div className="bg-white dark:bg-gray-900 rounded-2xl shadow-2xl p-8 max-w-md w-full text-center">
        <div className="text-5xl mb-4">⭐</div>
        <h2 className="text-xl font-bold text-gray-900 dark:text-gray-100 mb-2">
          Premium Özelliği
        </h2>
        <p className="text-gray-500 dark:text-gray-400 text-sm mb-6">
          Excel raporlama özelliği yalnızca Premium üyelere özeldir. Tüm gelir ve
          gider geçmişinizi detaylı bir Excel dosyasına aktarın.
        </p>
        <div className="flex gap-3 justify-center">
          <button onClick={onKapat} className="btn-secondary">
            Kapat
          </button>
          <Link to="/premium" onClick={onKapat} className="btn-primary">
            Premium'a Geç
          </Link>
        </div>
      </div>
    </div>
  )
}

export default function Dashboard() {
  const { kullanici, premiumMu } = useAuth()
  const [ozet, setOzet] = useState(null)
  const [sonIslemler, setSonIslemler] = useState([])
  const [yukleniyor, setYukleniyor] = useState(true)
  const [paywallAcik, setPaywallAcik] = useState(false)
  const [excelYukleniyor, setExcelYukleniyor] = useState(false)

  const bugun = new Date()
  const yil = bugun.getFullYear()
  const ay = bugun.getMonth() + 1

  const verileriYukle = useCallback(async () => {
    try {
      const [ozetVeri, islemVeri] = await Promise.all([
        islemServisi.aylikOzetGetir(yil, ay),
        islemServisi.islemleriGetir(),
      ])
      setOzet(ozetVeri)
      setSonIslemler(islemVeri.slice(0, 5))
    } catch {
      toast.error('Veriler yüklenirken hata oluştu.')
    } finally {
      setYukleniyor(false)
    }
  }, [yil, ay])

  useEffect(() => { verileriYukle() }, [verileriYukle])

  const excelIndir = async () => {
    if (!premiumMu) {
      setPaywallAcik(true)
      return
    }
    setExcelYukleniyor(true)
    try {
      await raporServisi.excelIndir()
      toast.success('Excel raporu indirildi!')
    } catch {
      toast.error('Rapor indirilemedi.')
    } finally {
      setExcelYukleniyor(false)
    }
  }

  const ayIsmi = bugun.toLocaleString('tr-TR', { month: 'long', year: 'numeric' })

  if (yukleniyor) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-primary-600" />
      </div>
    )
  }

  return (
    <div className="space-y-6">
      {paywallAcik && <PremiumPaywallModal onKapat={() => setPaywallAcik(false)} />}

      {/* Başlık */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900 dark:text-gray-100">
            Merhaba, {kullanici?.ad}!
          </h1>
          <p className="text-gray-500 dark:text-gray-400 mt-0.5">{ayIsmi} finansal durumunuz</p>
        </div>
        <div className="flex items-center gap-3">
          <button
            onClick={excelIndir}
            disabled={excelYukleniyor}
            className="flex items-center gap-2 text-sm border border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-800 text-gray-700 dark:text-gray-300 px-3 py-2 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors disabled:opacity-50"
          >
            <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4 text-green-600" viewBox="0 0 20 20" fill="currentColor">
              <path fillRule="evenodd" d="M3 17a1 1 0 011-1h12a1 1 0 110 2H4a1 1 0 01-1-1zm3.293-7.707a1 1 0 011.414 0L9 10.586V3a1 1 0 112 0v7.586l1.293-1.293a1 1 0 111.414 1.414l-3 3a1 1 0 01-1.414 0l-3-3a1 1 0 010-1.414z" clipRule="evenodd" />
            </svg>
            {excelYukleniyor ? 'İndiriliyor...' : 'Excel İndir'}
            {!premiumMu && <span className="text-xs bg-yellow-100 dark:bg-yellow-900 text-yellow-700 dark:text-yellow-300 px-1 rounded">PRO</span>}
          </button>
          {premiumMu ? (
            <span className="badge-premium">Premium</span>
          ) : (
            <Link to="/premium" className="badge-free hover:bg-yellow-100 dark:hover:bg-yellow-900 hover:text-yellow-800 transition-colors">
              Ücretsiz Plan → Yükselt
            </Link>
          )}
        </div>
      </div>

      {/* Özet kartlar */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <OzetKart baslik="Toplam Gelir" miktar={ozet?.toplamGelir || 0} renk="green" ikon="arrow-up" />
        <OzetKart baslik="Toplam Gider" miktar={ozet?.toplamGider || 0} renk="red" ikon="arrow-down" />
        <OzetKart
          baslik="Net Bakiye"
          miktar={ozet?.netBakiye || 0}
          renk={(ozet?.netBakiye || 0) >= 0 ? 'blue' : 'red'}
          ikon="wallet"
        />
      </div>

      {/* Grafik ve son işlemler */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="card">
          <h2 className="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-4">Harcama Dağılımı</h2>
          {ozet?.kategoriHarcamalari && Object.keys(ozet.kategoriHarcamalari).length > 0 ? (
            <PastaGrafik veri={ozet.kategoriHarcamalari} />
          ) : (
            <div className="text-center py-10 text-gray-400">Bu ay henüz gider eklenmedi.</div>
          )}
        </div>

        <div className="card">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-lg font-semibold text-gray-900 dark:text-gray-100">Son İşlemler</h2>
            <Link to="/islemler" className="text-sm text-primary-600 dark:text-primary-400 hover:underline">
              Tümünü Gör
            </Link>
          </div>
          <SonIslemler islemler={sonIslemler} />
        </div>
      </div>

      {/* Premium tanıtım (sadece free kullanıcılara) */}
      {!premiumMu && (
        <div className="bg-gradient-to-r from-primary-600 to-indigo-600 rounded-xl p-6 text-white">
          <h3 className="text-lg font-bold mb-2">Premium'a Yükseltin</h3>
          <p className="text-primary-100 text-sm mb-4">
            Akıllı tavsiyeler, 6 aylık trend grafikleri, düzenli işlem takibi ve Excel
            raporları ile finansal hedeflerinize daha hızlı ulaşın.
          </p>
          <Link
            to="/premium"
            className="inline-block bg-white text-primary-600 font-semibold px-4 py-2 rounded-lg text-sm hover:bg-primary-50 transition-colors"
          >
            Planları İncele
          </Link>
        </div>
      )}

      {/* FAB */}
      <HizliIslemFAB onIslemEklendi={verileriYukle} />
    </div>
  )
}
