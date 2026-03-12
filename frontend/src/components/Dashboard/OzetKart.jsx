const renkler = {
  green: {
    bg: 'bg-green-50',
    text: 'text-green-700',
    label: 'text-green-600',
  },
  red: {
    bg: 'bg-red-50',
    text: 'text-red-700',
    label: 'text-red-600',
  },
  blue: {
    bg: 'bg-blue-50',
    text: 'text-blue-700',
    label: 'text-blue-600',
  },
}

const ikonlar = {
  'arrow-up': '↑',
  'arrow-down': '↓',
  wallet: '₺',
}

export default function OzetKart({ baslik, miktar, renk = 'blue', ikon = 'wallet' }) {
  const r = renkler[renk] || renkler.blue

  const formatla = (sayi) => {
    const n = Number(sayi) || 0
    return new Intl.NumberFormat('tr-TR', {
      style: 'currency',
      currency: 'TRY',
      minimumFractionDigits: 2,
    }).format(n)
  }

  return (
    <div className="card">
      <div className="flex items-center justify-between mb-3">
        <p className="text-sm font-medium text-gray-500">{baslik}</p>
        <div className={`w-9 h-9 ${r.bg} rounded-lg flex items-center justify-center`}>
          <span className={`${r.text} font-bold`}>{ikonlar[ikon]}</span>
        </div>
      </div>
      <p className={`text-2xl font-bold ${r.text}`}>
        {formatla(miktar)}
      </p>
    </div>
  )
}
