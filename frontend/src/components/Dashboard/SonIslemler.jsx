const formatla = (deger) =>
  new Intl.NumberFormat('tr-TR', { style: 'currency', currency: 'TRY' }).format(Number(deger))

const tarihFormatla = (tarih) =>
  new Date(tarih).toLocaleDateString('tr-TR', { day: 'numeric', month: 'short' })

export default function SonIslemler({ islemler }) {
  if (!islemler.length) {
    return (
      <div className="text-center py-8 text-gray-400 text-sm">
        Henüz işlem eklenmedi.
      </div>
    )
  }

  return (
    <div className="divide-y divide-gray-50">
      {islemler.map((islem) => (
        <div key={islem.id} className="flex items-center justify-between py-3">
          <div className="flex items-center gap-3">
            <div className={`w-8 h-8 rounded-lg flex items-center justify-center text-xs font-bold
              ${islem.kategoriTip === 'GELIR' ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'}`}>
              {islem.kategoriTip === 'GELIR' ? '↑' : '↓'}
            </div>
            <div>
              <p className="text-sm font-medium text-gray-900">{islem.kategoriIsim}</p>
              <p className="text-xs text-gray-400">{tarihFormatla(islem.islemTarihi)}</p>
            </div>
          </div>
          <span className={`text-sm font-semibold
            ${islem.kategoriTip === 'GELIR' ? 'text-green-600' : 'text-red-600'}`}>
            {islem.kategoriTip === 'GELIR' ? '+' : '-'}{formatla(islem.miktar)}
          </span>
        </div>
      ))}
    </div>
  )
}
