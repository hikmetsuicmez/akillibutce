import { NavLink } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'

const menuOgeleri = [
  { yol: '/', etiket: 'Dashboard', ikon: '📊' },
  { yol: '/islemler', etiket: 'İşlemler', ikon: '💳' },
  { yol: '/hedefler', etiket: 'Hedefler', ikon: '🎯' },
  { yol: '/duzenli-islemler', etiket: 'Abonelikler', ikon: '🔄', premium: true },
  { yol: '/analiz', etiket: 'Analiz', ikon: '📈', premium: true },
  { yol: '/premium', etiket: 'Premium', ikon: '⭐' },
]

export default function Sidebar() {
  const { kullanici, premiumMu } = useAuth()

  return (
    <aside className="w-64 bg-white dark:bg-gray-900 border-r border-gray-100 dark:border-gray-800 flex flex-col">
      <div className="p-6 border-b border-gray-100 dark:border-gray-800">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 bg-primary-600 rounded-xl flex items-center justify-center">
            <span className="text-white font-bold text-lg">₺</span>
          </div>
          <div>
            <p className="font-bold text-gray-900 dark:text-gray-100 leading-none">Akıllı Bütçe</p>
            <p className="text-xs text-gray-400 dark:text-gray-500 mt-0.5">Finans Yönetimi</p>
          </div>
        </div>
      </div>

      <nav className="flex-1 p-4 space-y-1">
        {menuOgeleri.map((oge) => (
          <NavLink
            key={oge.yol}
            to={oge.yol}
            end={oge.yol === '/'}
            className={({ isActive }) =>
              `flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-colors ${
                isActive
                  ? 'bg-primary-50 dark:bg-primary-900/30 text-primary-700 dark:text-primary-400'
                  : 'text-gray-600 dark:text-gray-400 hover:bg-gray-50 dark:hover:bg-gray-800 hover:text-gray-900 dark:hover:text-gray-100'
              }`
            }
          >
            <span className="text-base">{oge.ikon}</span>
            <span>{oge.etiket}</span>
            {oge.premium && !premiumMu && (
              <span className="ml-auto text-xs bg-yellow-100 dark:bg-yellow-900 text-yellow-700 dark:text-yellow-300 px-1.5 py-0.5 rounded">
                PRO
              </span>
            )}
          </NavLink>
        ))}
      </nav>

      <div className="p-4 border-t border-gray-100 dark:border-gray-800">
        <div className="flex items-center gap-3">
          <div className="w-8 h-8 bg-primary-100 dark:bg-primary-900 rounded-full flex items-center justify-center">
            <span className="text-primary-700 dark:text-primary-300 font-semibold text-sm">
              {kullanici?.ad?.[0]?.toUpperCase()}
            </span>
          </div>
          <div className="flex-1 min-w-0">
            <p className="text-sm font-medium text-gray-900 dark:text-gray-100 truncate">
              {kullanici?.ad} {kullanici?.soyad}
            </p>
            <p className="text-xs text-gray-400 dark:text-gray-500 truncate">{kullanici?.eposta}</p>
          </div>
        </div>
      </div>
    </aside>
  )
}
