import { useAuth } from '../context/AuthContext'
import { CheckIcon } from '@heroicons/react/24/solid'
import toast from 'react-hot-toast'

const ucretsizOzellikler = [
  'Gelir ve gider takibi (CRUD)',
  'Temel kategori desteği (12 kategori)',
  'Aylık pasta grafiği',
  'JWT güvenli giriş',
]

const premiumOzellikler = [
  'Tüm Ücretsiz Plan özellikleri',
  'Akıllı Tavsiye Motoru',
  'Özel kategori oluşturma',
  '6 aylık trend grafikleri',
  'Düzenli işlem takibi',
  'Excel dışa aktarma',
  'Birikim hedefleri (sınırsız)',
  'Kategori bütçe limitleri',
  'Öncelikli destek',
]

export default function Premium() {
  const { premiumMu } = useAuth()

  const planSec = (tip) => {
    toast.success(`${tip === 'AYLIK' ? 'Aylık' : 'Yıllık'} plan seçildi. Ödeme entegrasyonu yakında!`)
  }

  return (
    <div className="max-w-4xl mx-auto space-y-8">
      <div className="text-center">
        <h1 className="text-3xl font-bold text-gray-900 dark:text-gray-100">Planları Karşılaştır</h1>
        <p className="text-gray-500 dark:text-gray-400 mt-2">
          İhtiyaçlarınıza uygun planı seçin ve finansal özgürlüğünüze kavuşun.
        </p>
      </div>

      {premiumMu && (
        <div className="bg-green-50 dark:bg-green-900/30 border border-green-200 dark:border-green-700 rounded-xl p-4 text-center text-green-700 dark:text-green-300 font-medium">
          Premium üyesisiniz. Tüm özelliklere erişiminiz aktif.
        </div>
      )}

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {/* Ucretsiz Plan */}
        <div className="card border-2 border-gray-200 dark:border-gray-700">
          <div className="mb-6">
            <span className="badge-free">Ücretsiz</span>
            <div className="mt-4">
              <span className="text-4xl font-bold text-gray-900 dark:text-gray-100">₺0</span>
              <span className="text-gray-500 dark:text-gray-400">/ay</span>
            </div>
            <p className="text-gray-500 dark:text-gray-400 text-sm mt-1">Temel bütçe takibi</p>
          </div>
          <ul className="space-y-3 mb-8">
            {ucretsizOzellikler.map((o, i) => (
              <li key={i} className="flex items-start gap-2 text-sm text-gray-600 dark:text-gray-300">
                <CheckIcon className="w-4 h-4 text-green-500 mt-0.5 shrink-0" />
                {o}
              </li>
            ))}
          </ul>
          <button disabled className="btn-secondary w-full opacity-50 cursor-default">
            Mevcut Plan
          </button>
        </div>

        {/* Aylik Premium */}
        <div className="card border-2 border-primary-500 relative">
          <div className="absolute -top-3 left-1/2 -translate-x-1/2">
            <span className="bg-primary-600 text-white text-xs font-semibold px-3 py-1 rounded-full">
              En Popüler
            </span>
          </div>
          <div className="mb-6">
            <span className="badge-premium">Premium</span>
            <div className="mt-4">
              <span className="text-4xl font-bold text-gray-900 dark:text-gray-100">₺59</span>
              <span className="text-gray-500 dark:text-gray-400">/ay</span>
            </div>
            <p className="text-gray-500 dark:text-gray-400 text-sm mt-1">Aylık abonelik</p>
          </div>
          <ul className="space-y-3 mb-8">
            {premiumOzellikler.map((o, i) => (
              <li key={i} className="flex items-start gap-2 text-sm text-gray-600 dark:text-gray-300">
                <CheckIcon className="w-4 h-4 text-primary-500 mt-0.5 shrink-0" />
                {o}
              </li>
            ))}
          </ul>
          <button
            onClick={() => planSec('AYLIK')}
            disabled={premiumMu}
            className="btn-primary w-full"
          >
            {premiumMu ? 'Aktif Plan' : 'Aylık Başla'}
          </button>
        </div>

        {/* Yillik Premium */}
        <div className="card border-2 border-gray-200 dark:border-gray-700">
          <div className="mb-6">
            <div className="flex items-center gap-2">
              <span className="badge-premium">Premium</span>
              <span className="text-xs font-medium text-green-600 dark:text-green-400 bg-green-100 dark:bg-green-900/30 px-2 py-0.5 rounded-full">
                %30 İndirim
              </span>
            </div>
            <div className="mt-4">
              <span className="text-4xl font-bold text-gray-900 dark:text-gray-100">₺499</span>
              <span className="text-gray-500 dark:text-gray-400">/yıl</span>
            </div>
            <p className="text-gray-500 dark:text-gray-400 text-sm mt-1">≈ ₺41.58/ay</p>
          </div>
          <ul className="space-y-3 mb-8">
            {premiumOzellikler.map((o, i) => (
              <li key={i} className="flex items-start gap-2 text-sm text-gray-600 dark:text-gray-300">
                <CheckIcon className="w-4 h-4 text-primary-500 mt-0.5 shrink-0" />
                {o}
              </li>
            ))}
          </ul>
          <button
            onClick={() => planSec('YILLIK')}
            disabled={premiumMu}
            className="btn-primary w-full"
          >
            {premiumMu ? 'Aktif Plan' : 'Yıllık Başla'}
          </button>
        </div>
      </div>
    </div>
  )
}
