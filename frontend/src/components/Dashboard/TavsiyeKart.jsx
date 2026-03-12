const oncelikRenkleri = {
  YUKSEK: { bg: 'bg-red-50 border-red-200', ikon: '🔴', text: 'text-red-700' },
  ORTA: { bg: 'bg-yellow-50 border-yellow-200', ikon: '🟡', text: 'text-yellow-700' },
  DUSUK: { bg: 'bg-blue-50 border-blue-200', ikon: '🔵', text: 'text-blue-700' },
}

const formatla = (deger) =>
  new Intl.NumberFormat('tr-TR', { style: 'currency', currency: 'TRY' }).format(Number(deger))

export default function TavsiyeKart({ tavsiye }) {
  const stil = oncelikRenkleri[tavsiye.oncelik] || oncelikRenkleri.DUSUK

  return (
    <div className={`border rounded-xl p-4 ${stil.bg}`}>
      <div className="flex items-start gap-3">
        <span className="text-xl mt-0.5">{stil.ikon}</span>
        <div className="flex-1">
          <div className="flex items-center justify-between mb-1">
            <span className={`font-semibold text-sm ${stil.text}`}>
              {tavsiye.kategoriIsim}
            </span>
            <div className="text-right text-xs text-gray-500">
              <span className="font-medium text-gray-700">{formatla(tavsiye.harcamaMiktari)}</span>
              <span> / {formatla(tavsiye.toplamGelir)}</span>
            </div>
          </div>

          {/* Ilerleme cubugu */}
          <div className="w-full bg-white rounded-full h-1.5 mb-2">
            <div
              className={`h-1.5 rounded-full ${
                tavsiye.oncelik === 'YUKSEK' ? 'bg-red-500' :
                tavsiye.oncelik === 'ORTA' ? 'bg-yellow-500' : 'bg-blue-500'
              }`}
              style={{ width: `${Math.min(tavsiye.harcamaYuzdesi, 100)}%` }}
            />
          </div>

          <p className="text-sm text-gray-600">{tavsiye.mesaj}</p>
          <p className="text-xs text-gray-400 mt-1">
            Harcama oranı: %{tavsiye.harcamaYuzdesi.toFixed(1)} (Eşik: %{tavsiye.esikYuzdesi})
          </p>
        </div>
      </div>
    </div>
  )
}
