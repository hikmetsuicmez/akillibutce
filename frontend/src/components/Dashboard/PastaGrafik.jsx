import { PieChart, Pie, Cell, Tooltip, Legend, ResponsiveContainer } from 'recharts'

const RENKLER = [
  '#6366f1', '#22c55e', '#f59e0b', '#ef4444', '#8b5cf6',
  '#06b6d4', '#f97316', '#84cc16', '#ec4899', '#14b8a6',
]

const formatla = (deger) =>
  new Intl.NumberFormat('tr-TR', { style: 'currency', currency: 'TRY' }).format(deger)

export default function PastaGrafik({ veri }) {
  const veriDizisi = Object.entries(veri).map(([isim, miktar]) => ({
    isim,
    miktar: Number(miktar),
  }))

  return (
    <ResponsiveContainer width="100%" height={280}>
      <PieChart>
        <Pie
          data={veriDizisi}
          cx="50%"
          cy="50%"
          innerRadius={60}
          outerRadius={100}
          paddingAngle={3}
          dataKey="miktar"
          nameKey="isim"
        >
          {veriDizisi.map((_, index) => (
            <Cell key={index} fill={RENKLER[index % RENKLER.length]} />
          ))}
        </Pie>
        <Tooltip formatter={(v) => formatla(v)} />
        <Legend
          formatter={(value) => (
            <span className="text-xs text-gray-600">{value}</span>
          )}
        />
      </PieChart>
    </ResponsiveContainer>
  )
}
