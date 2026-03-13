import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { analizServisi } from '../services/analizServisi'
import TrendGrafik from '../components/Dashboard/TrendGrafik'
import TavsiyeKart from '../components/Dashboard/TavsiyeKart'
import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Cell
} from 'recharts'
import toast from 'react-hot-toast'

function AyKarsilastirmaKarti({ buAy, gecenAy }) {
  if (!buAy || !gecenAy) return null
  const buAyGider = Number(buAy.toplamGider || 0)
  const gecenAyGider = Number(gecenAy.toplamGider || 0)
  const fark = buAyGider - gecenAyGider
  const yuzde = gecenAyGider > 0
    ? Math.abs(((buAyGider - gecenAyGider) / gecenAyGider) * 100).toFixed(1)
    : null
  const azaldi = fark < 0
  const artti = fark > 0
  return (
    <div className={`card border-l-4 ${azaldi ? "border-l-green-500" : artti ? "border-l-red-500" : "border-l-gray-300"}`}>
      <div className="flex items-center justify-between">
        <div>
          <p className="text-sm text-gray-500 dark:text-gray-400 mb-1">Gecen Aya Gore Harcama</p>
          <div className="flex items-baseline gap-2">
            <span className={`text-2xl font-bold ${azaldi ? "text-green-600 dark:text-green-400" : artti ? "text-red-600 dark:text-red-400" : "text-gray-700 dark:text-gray-300"}`}>
              {azaldi ? "v" : artti ? "^" : "="} {yuzde ? "%" + yuzde : "+-0"}
            </span>
            <span className="text-sm text-gray-500 dark:text-gray-400">
              {azaldi ? "daha az" : artti ? "daha fazla" : "degisim yok"}
            </span>
          </div>
          {yuzde && (
            <p className="text-xs text-gray-400 dark:text-gray-500 mt-1">
              {azaldi ? "Harika! Harcamalarinizi azalttiniz." : "Harcamalariniz artti, dikkat!"}
            </p>
          )}
        </div>
        <div className="text-right">
          <p className="text-xs text-gray-400 dark:text-gray-500">Bu ay</p>
          <p className="text-sm font-semibold text-gray-700 dark:text-gray-300">
            {buAyGider.toLocaleString("tr-TR", { minimumFractionDigits: 2 })} TL
          </p>
          <p className="text-xs text-gray-400 dark:text-gray-500 mt-1">Gecen ay</p>
          <p className="text-sm font-semibold text-gray-500 dark:text-gray-400">
            {gecenAyGider.toLocaleString("tr-TR", { minimumFractionDigits: 2 })} TL
          </p>
        </div>
      </div>
    </div>
  )
}

function KategoriBarGrafik({ kategoriHarcamalari }) {
  if (!kategoriHarcamalari || Object.keys(kategoriHarcamalari).length === 0) {
    return <div className="text-center py-8 text-gray-400">Bu ay gider verisi bulunamadi.</div>
  }
  const renkler = ["#6366f1", "#8b5cf6", "#a78bfa", "#818cf8", "#c4b5fd", "#ddd6fe"]
  const veri = Object.entries(kategoriHarcamalari)
    .map(([isim, tutar]) => ({ isim, tutar: Number(tutar) }))
    .sort((a, b) => b.tutar - a.tutar)
  return (
    <ResponsiveContainer width="100%" height={220}>
      <BarChart data={veri} layout="vertical" margin={{ top: 4, right: 32, left: 8, bottom: 4 }}>
        <CartesianGrid strokeDasharray="3 3" horizontal={false} stroke="#e5e7eb" />
        <XAxis type="number" tick={{ fontSize: 11, fill: "#9ca3af" }} tickFormatter={(v) => (v / 1000).toFixed(1) + "K"} />
        <YAxis type="category" dataKey="isim" tick={{ fontSize: 11, fill: "#6b7280" }} width={90} />
        <Tooltip formatter={(value) => [Number(value).toLocaleString("tr-TR", { minimumFractionDigits: 2 }) + " TL", "Harcama"]} contentStyle={{ fontSize: 12 }} />
        <Bar dataKey="tutar" radius={[0, 4, 4, 0]}>
          {veri.map((_, index) => (
            <Cell key={index} fill={renkler[index % renkler.length]} />
          ))}
        </Bar>
      </BarChart>
    </ResponsiveContainer>
  )
}

export default function Analiz() {
  const { premiumMu } = useAuth()
  const [tavsiyeler, setTavsiyeler] = useState([])
  const [trend, setTrend] = useState(null)
  const [karsilastirma, setKarsilastirma] = useState(null)
  const [buAyOzet, setBuAyOzet] = useState(null)
  const [yukleniyor, setYukleniyor] = useState(true)

  useEffect(() => {
    if (!premiumMu) { setYukleniyor(false); return }
    const verileriYukle = async () => {
      const bugun = new Date()
      const yil = bugun.getFullYear()
      const ay = bugun.getMonth() + 1
      try {
        const [tavsiyeVeri, trendVeri, karsilastirmaVeri] = await Promise.all([
          analizServisi.tavsiyeleriGetir(),
          analizServisi.trendleriGetir(),
          analizServisi.aylikKarsilastirmaGetir(yil, ay),
        ])
        setTavsiyeler(tavsiyeVeri)
        setTrend(trendVeri)
        setKarsilastirma(karsilastirmaVeri)
        setBuAyOzet(karsilastirmaVeri.buAy)
      } catch {
        toast.error("Analiz verileri yuklenemedi.")
      } finally {
        setYukleniyor(false)
      }
    }
    verileriYukle()
  }, [premiumMu])

  if (!premiumMu) {
    return (
      <div className="flex flex-col items-center justify-center min-h-[60vh] text-center px-4">
        <div className="w-20 h-20 bg-yellow-100 dark:bg-yellow-900/30 rounded-full flex items-center justify-center mb-6">
          <span className="text-4xl">&#128274;</span>
        </div>
        <h2 className="text-2xl font-bold text-gray-900 dark:text-gray-100 mb-2">Premium Ozellik</h2>
        <p className="text-gray-500 dark:text-gray-400 mb-6 max-w-md">
          Akilli Tavsiye Motoru, 6 aylik trend grafikleri ve karsilastirmali analiz Premium uyelere ozeldir.
        </p>
        <Link to="/premium" className="btn-primary">Premium a Yukselt</Link>
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
      <h1 className="text-2xl font-bold text-gray-900 dark:text-gray-100">Finansal Analiz</h1>
      {karsilastirma && (
        <AyKarsilastirmaKarti buAy={karsilastirma.buAy} gecenAy={karsilastirma.gecenAy} />
      )}
      <div className="card">
        <h2 className="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-4">
          Kategori Bazli Harcama Dagilimi
          <span className="ml-2 badge-premium">Premium</span>
        </h2>
        <KategoriBarGrafik kategoriHarcamalari={buAyOzet?.kategoriHarcamalari} />
      </div>
      <div className="card">
        <h2 className="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-4">
          Akilli Tavsiyeler
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
            Bu ay icin uyari bulunmuyor. Harcamalariniz kontrol altinda!
          </div>
        )}
      </div>
      <div className="card">
        <h2 className="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-4">
          6 Aylik Gelir / Gider Trendi
          <span className="ml-2 badge-premium">Premium</span>
        </h2>
        {trend ? (
          <TrendGrafik trend={trend} />
        ) : (
          <div className="text-center py-8 text-gray-400">Trend verisi bulunamadi.</div>
        )}
      </div>
    </div>
  )
}
