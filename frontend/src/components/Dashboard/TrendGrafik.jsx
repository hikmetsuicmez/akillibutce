import {
  LineChart, Line, XAxis, YAxis, CartesianGrid,
  Tooltip, Legend, ResponsiveContainer,
} from 'recharts'

const formatla = (deger) =>
  new Intl.NumberFormat('tr-TR', {
    style: 'currency',
    currency: 'TRY',
    notation: 'compact',
  }).format(deger)

export default function TrendGrafik({ trend }) {
  const veri = trend.aylar.map((ay, i) => ({
    ay,
    Gelir: Number(trend.gelirler[i]),
    Gider: Number(trend.giderler[i]),
    'Net Tasarruf': Number(trend.netTasarruflar[i]),
  }))

  return (
    <ResponsiveContainer width="100%" height={320}>
      <LineChart data={veri} margin={{ top: 5, right: 20, left: 10, bottom: 5 }}>
        <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
        <XAxis dataKey="ay" tick={{ fontSize: 12 }} />
        <YAxis tickFormatter={formatla} tick={{ fontSize: 11 }} />
        <Tooltip formatter={(v) => formatla(v)} />
        <Legend />
        <Line
          type="monotone"
          dataKey="Gelir"
          stroke="#22c55e"
          strokeWidth={2}
          dot={{ r: 4 }}
        />
        <Line
          type="monotone"
          dataKey="Gider"
          stroke="#ef4444"
          strokeWidth={2}
          dot={{ r: 4 }}
        />
        <Line
          type="monotone"
          dataKey="Net Tasarruf"
          stroke="#6366f1"
          strokeWidth={2}
          strokeDasharray="5 5"
          dot={{ r: 4 }}
        />
      </LineChart>
    </ResponsiveContainer>
  )
}
