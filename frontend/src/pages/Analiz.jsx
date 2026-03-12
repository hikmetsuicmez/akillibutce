import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { analizServisi } from '../services/analizServisi'
import TrendGrafik from '../components/Dashboard/TrendGrafik'
import TavsiyeKart from '../components/Dashboard/TavsiyeKart'
import toast from 'react-hot-toast'

export default function Analiz() {
  const { premiumMu } = useAuth()
  const [tavsiyeler, setTavsiyeler] = useState([])
  const [trend, setTrend] = useState(null)
  const [yukleniyor, setYukleniyor] = useState(true)

  useEffect(() => {
    if (!premiumMu) { setYukleniyor(false); return }

    const verileriYukle = async () => {
      try {
        const [tavsiyeVeri, trendVeri] = await Promise.all([
          analizServisi.tavsiyeleriGetir(),
          analizServisi.trendleriGetir(),
        ])
        setTavsiyeler(tavsiyeVeri)
        setTrend(trendVeri)
      } catch {
        toast.error('Analiz verileri yüklenemedi.')
      } finally {
        setYukleniyor(false)
      }
    }
    verileriYukle()
  }, [premiumMu])

  if (!premiumMu) {
    return (
      <div className="flex flex-col items-center justify-center min-h-[60vh] text-center px-4">
        <div className="w-20 h-20 bg-yellow-100 rounded-full flex items-center justify-center mb-6">
          <span className="text-4xl">🔒</span>
        </div>
        <h2 className="text-2xl font-bold text-gray-900 mb-2">Premium Özellik</h2>
        <p className="text-gray-500 mb-6 max-w-md">
          Akıllı Tavsiye Motoru ve 6 aylık trend grafikleri Premium üyelere özeldir.
          Finansal alışkanlıklarınızı analiz etmeye başlayın.
        </p>
        <Link to="/premium" className="btn-primary">
          Premium'a Yükselt
        </Link>
      </div>
    )
  }

  if (yukleniyor) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-primary-600" />
      </div>
    )
  }

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-gray-900">Finansal Analiz</h1>

      {/* Akilli Tavsiyeler */}
      <div className="card">
        <h2 className="text-lg font-semibold text-gray-900 mb-4">
          Akıllı Tavsiyeler
          <span className="ml-2 badge-premium">Premium</span>
        </h2>
        {tavsiyeler.length > 0 ? (
          <div className="space-y-3">
            {tavsiyeler.map((tavsiye, i) => (
              <TavsiyeKart key={i} tavsiye={tavsiye} />
            ))}
          </div>
        ) : (
          <div className="text-center py-8 text-gray-400">
            Bu ay için herhangi bir uyarı bulunmuyor. Harcamalarınız kontrol altında!
          </div>
        )}
      </div>

      {/* 6 Aylik Trend */}
      <div className="card">
        <h2 className="text-lg font-semibold text-gray-900 mb-4">
          6 Aylık Gelir / Gider Trendi
          <span className="ml-2 badge-premium">Premium</span>
        </h2>
        {trend ? (
          <TrendGrafik trend={trend} />
        ) : (
          <div className="text-center py-8 text-gray-400">Trend verisi bulunamadı.</div>
        )}
      </div>
    </div>
  )
}
