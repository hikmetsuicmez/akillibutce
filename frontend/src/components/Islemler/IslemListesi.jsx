const formatla = (deger) =>
  new Intl.NumberFormat('tr-TR', { style: 'currency', currency: 'TRY' }).format(Number(deger))

const tarihFormatla = (tarih) =>
  new Date(tarih).toLocaleDateString('tr-TR', {
    day: 'numeric', month: 'long', year: 'numeric'
  })

export default function IslemListesi({ islemler, onSil }) {
  if (!islemler.length) {
    return (
      <div className="text-center py-16 text-gray-400">
        <p className="text-4xl mb-3">📂</p>
        <p className="font-medium">Henüz işlem bulunmuyor.</p>
        <p className="text-sm mt-1">Yeni İşlem butonuna tıklayarak başlayın.</p>
      </div>
    )
  }

  return (
    <div className="overflow-x-auto">
      <table className="w-full">
        <thead>
          <tr className="border-b border-gray-100">
            <th className="text-left py-3 px-2 text-xs font-medium text-gray-500 uppercase tracking-wide">
              Kategori
            </th>
            <th className="text-left py-3 px-2 text-xs font-medium text-gray-500 uppercase tracking-wide">
              Açıklama
            </th>
            <th className="text-left py-3 px-2 text-xs font-medium text-gray-500 uppercase tracking-wide">
              Tarih
            </th>
            <th className="text-right py-3 px-2 text-xs font-medium text-gray-500 uppercase tracking-wide">
              Miktar
            </th>
            <th className="py-3 px-2"></th>
          </tr>
        </thead>
        <tbody className="divide-y divide-gray-50">
          {islemler.map((islem) => (
            <tr key={islem.id} className="hover:bg-gray-50 transition-colors">
              <td className="py-3 px-2">
                <div className="flex items-center gap-2">
                  <span className={`w-2 h-2 rounded-full ${
                    islem.kategoriTip === 'GELIR' ? 'bg-green-500' : 'bg-red-500'
                  }`} />
                  <span className="text-sm font-medium text-gray-900">
                    {islem.kategoriIsim}
                  </span>
                </div>
              </td>
              <td className="py-3 px-2 text-sm text-gray-500">
                {islem.aciklama || '-'}
              </td>
              <td className="py-3 px-2 text-sm text-gray-500">
                {tarihFormatla(islem.islemTarihi)}
              </td>
              <td className="py-3 px-2 text-right">
                <span className={`text-sm font-semibold ${
                  islem.kategoriTip === 'GELIR' ? 'text-green-600' : 'text-red-600'
                }`}>
                  {islem.kategoriTip === 'GELIR' ? '+' : '-'}{formatla(islem.miktar)}
                </span>
              </td>
              <td className="py-3 px-2">
                <button
                  onClick={() => onSil(islem.id)}
                  className="text-gray-300 hover:text-red-500 transition-colors text-lg leading-none"
                  title="Sil"
                >
                  ×
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
