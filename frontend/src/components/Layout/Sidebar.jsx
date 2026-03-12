import { NavLink } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'

const menuOgeleri = [
  { yol: '/', etiket: 'Dashboard', ikon: '📊' },
  { yol: '/islemler', etiket: 'İşlemler', ikon: '💳' },
  { yol: '/analiz', etiket: 'Analiz', ikon: '📈', premium: true },
  { yol: '/premium', etiket: 'Premium', ikon: '⭐' },
]

export default function Sidebar() {
  const { kullanici, premiumMu } = useAuth()

  return (
    <aside className="w-64 bg-white border-r border-gray-100 flex flex-col">
      {/* Logo */}
      <div className="p-6 border-b border-gray-100">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 bg-primary-600 rounded-xl flex items-center justify-center">
            <span className="text-white font-bold text-lg">₺</span>
          </div>
          <div>
            <p className="font-bold text-gray-900 leading-none">Akıllı Bütçe</p>
            <p className="text-xs text-gray-400 mt-0.5">Finans Yönetimi</p>
          </div>
        </div>
      </div>

      {/* Navigasyon */}
      <nav className="flex-1 p-4 space-y-1">
        {menuOgeleri.map((oge) => (
          <NavLink
            key={oge.yol}
            to={oge.yol}
            end={oge.yol === '/'}
            className={({ isActive }) =>
              `flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-colors ${
                isActive
                  ? 'bg-primary-50 text-primary-700'
                  : 'text-gray-600 hover:bg-gray-50 hover:text-gray-900'
              }`
            }
          >
            <span className="text-base">{oge.ikon}</span>
            <span>{oge.etiket}</span>
            {oge.premium && !premiumMu && (
              <span className="ml-auto text-xs bg-yellow-100 text-yellow-700 px-1.5 py-0.5 rounded">
                PRO
              </span>
            )}
          </NavLink>
        ))}
      </nav>

      {/* Kullanici bilgisi */}
      <div className="p-4 border-t border-gray-100">
        <div className="flex items-center gap-3">
          <div className="w-8 h-8 bg-primary-100 rounded-full flex items-center justify-center">
            <span className="text-primary-700 font-semibold text-sm">
              {kullanici?.ad?.[0]?.toUpperCase()}
            </span>
          </div>
          <div className="flex-1 min-w-0">
            <p className="text-sm font-medium text-gray-900 truncate">
              {kullanici?.ad} {kullanici?.soyad}
            </p>
            <p className="text-xs text-gray-400 truncate">{kullanici?.eposta}</p>
          </div>
        </div>
      </div>
    </aside>
  )
}
