import { useAuth } from '../../context/AuthContext'
import { useNavigate } from 'react-router-dom'
import toast from 'react-hot-toast'

export default function Header() {
  const { kullanici, cikisYap, premiumMu } = useAuth()
  const navigate = useNavigate()

  const cikisYapIsle = () => {
    cikisYap()
    toast.success('Güvenli çıkış yapıldı.')
    navigate('/giris')
  }

  return (
    <header className="h-16 bg-white border-b border-gray-100 flex items-center justify-between px-6">
      <div className="flex items-center gap-2">
        {premiumMu && (
          <span className="badge-premium">
            Premium Üye
          </span>
        )}
      </div>

      <div className="flex items-center gap-4">
        <div className="text-right">
          <p className="text-sm font-medium text-gray-900">
            {kullanici?.ad} {kullanici?.soyad}
          </p>
          <p className="text-xs text-gray-400">{kullanici?.eposta}</p>
        </div>
        <button
          onClick={cikisYapIsle}
          className="text-sm text-gray-500 hover:text-red-600 transition-colors font-medium"
        >
          Çıkış
        </button>
      </div>
    </header>
  )
}
