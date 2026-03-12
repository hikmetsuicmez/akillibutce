import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { islemServisi } from '../services/islemServisi'
import { useAuth } from '../context/AuthContext'
import OzetKart from '../components/Dashboard/OzetKart'
import PastaGrafik from '../components/Dashboard/PastaGrafik'
import SonIslemler from '../components/Dashboard/SonIslemler'
import toast from 'react-hot-toast'

export default function Dashboard() {
  const { kullanici, premiumMu } = useAuth()
  const [ozet, setOzet] = useState(null)
  const [sonIslemler, setSonIslemler] = useState([])
  const [yukleniyor, setYukleniyor] = useState(true)

  const bugun = new Date()
  const yil = bugun.getFullYear()
  const ay = bugun.getMonth() + 1

  useEffect(() => {
    const verileriYukle = async () => {
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
    }
    verileriYukle()
  }, [yil, ay])

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
      {/* Hosgeldin basligi */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">
            Merhaba, {kullanici?.ad}!
          </h1>
          <p className="text-gray-500 mt-0.5">{ayIsmi} finansal durumunuz</p>
        </div>
        <div className="flex items-center gap-2">
          {premiumMu ? (
            <span className="badge-premium">Premium</span>
          ) : (
            <Link to="/premium" className="badge-free hover:bg-yellow-100 hover:text-yellow-800 transition-colors">
              Ücretsiz Plan → Yükselt
            </Link>
          )}
        </div>
      </div>

      {/* Ozet kartlar */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <OzetKart
          baslik="Toplam Gelir"
          miktar={ozet?.toplamGelir || 0}
          renk="green"
          ikon="arrow-up"
        />
        <OzetKart
          baslik="Toplam Gider"
          miktar={ozet?.toplamGider || 0}
          renk="red"
          ikon="arrow-down"
        />
        <OzetKart
          baslik="Net Bakiye"
          miktar={ozet?.netBakiye || 0}
          renk={(ozet?.netBakiye || 0) >= 0 ? 'blue' : 'red'}
          ikon="wallet"
        />
      </div>

      {/* Grafik ve son islemler */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="card">
          <h2 className="text-lg font-semibold text-gray-900 mb-4">
            Harcama Dağılımı
          </h2>
          {ozet?.kategoriHarcamalari && Object.keys(ozet.kategoriHarcamalari).length > 0 ? (
            <PastaGrafik veri={ozet.kategoriHarcamalari} />
          ) : (
            <div className="text-center py-10 text-gray-400">
              Bu ay henüz gider eklenmedi.
            </div>
          )}
        </div>

        <div className="card">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-lg font-semibold text-gray-900">Son İşlemler</h2>
            <Link to="/islemler" className="text-sm text-primary-600 hover:underline">
              Tümünü Gör
            </Link>
          </div>
          <SonIslemler islemler={sonIslemler} />
        </div>
      </div>

      {/* Premium tanitim (sadece free kullanicilara) */}
      {!premiumMu && (
        <div className="bg-gradient-to-r from-primary-600 to-indigo-600 rounded-xl p-6 text-white">
          <h3 className="text-lg font-bold mb-2">Premium'a Yükseltin</h3>
          <p className="text-primary-100 text-sm mb-4">
            Akıllı tavsiyeler, 6 aylık trend grafikleri ve özel kategorilerle
            finansal hedeflerinize daha hızlı ulaşın.
          </p>
          <Link
            to="/premium"
            className="inline-block bg-white text-primary-600 font-semibold
                       px-4 py-2 rounded-lg text-sm hover:bg-primary-50 transition-colors"
          >
            Planları İncele
          </Link>
        </div>
      )}
    </div>
  )
}
