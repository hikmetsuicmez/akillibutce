import { useState, useEffect, useRef } from 'react'
import { useAuth } from '../../context/AuthContext'
import { useTheme } from '../../context/ThemeContext'
import { bildirimServisi } from '../../services/bildirimServisi'
import { useNavigate } from 'react-router-dom'
import toast from 'react-hot-toast'

export default function Header() {
  const { kullanici, cikisYap, premiumMu } = useAuth()
  const { karanlikMod, temaGecis } = useTheme()
  const navigate = useNavigate()

  const [bildirimAcik, setBildirimAcik] = useState(false)
  const [bildirimler, setBildirimler] = useState([])
  const [okunmamis, setOkunmamis] = useState(0)
  const dropdownRef = useRef(null)

  useEffect(() => {
    const sayaciGuncelle = async () => {
      try {
        const sayi = await bildirimServisi.okunmamisSayisiGetir()
        setOkunmamis(sayi)
      } catch {}
    }
    sayaciGuncelle()
    const interval = setInterval(sayaciGuncelle, 30000)
    return () => clearInterval(interval)
  }, [])

  useEffect(() => {
    const disariTikla = (e) => {
      if (dropdownRef.current && !dropdownRef.current.contains(e.target)) {
        setBildirimAcik(false)
      }
    }
    document.addEventListener('mousedown', disariTikla)
    return () => document.removeEventListener('mousedown', disariTikla)
  }, [])

  const bildirimAc = async () => {
    if (!bildirimAcik) {
      try {
        const veri = await bildirimServisi.bildirimleriGetir()
        setBildirimler(veri)
        if (veri.some(b => !b.okunduMu)) {
          await bildirimServisi.tumunuOkunduIsaretle()
          setOkunmamis(0)
        }
      } catch {}
    }
    setBildirimAcik(!bildirimAcik)
  }

  const cikisYapIsle = () => {
    cikisYap()
    toast.success('Güvenli çıkış yapıldı.')
    navigate('/giris')
  }

  return (
    <header className="h-16 bg-white dark:bg-gray-900 border-b border-gray-100 dark:border-gray-800 flex items-center justify-between px-6">
      <div className="flex items-center gap-2">
        {premiumMu && (
          <span className="badge-premium">Premium Üye</span>
        )}
      </div>

      <div className="flex items-center gap-3">
        {/* Karanlık Mod Toggle */}
        <button
          onClick={temaGecis}
          className="p-2 rounded-lg text-gray-500 dark:text-gray-400 hover:bg-gray-100 dark:hover:bg-gray-800 transition-colors"
          title={karanlikMod ? 'Aydınlık Moda Geç' : 'Karanlık Moda Geç'}
        >
          {karanlikMod ? (
            <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
              <path fillRule="evenodd" d="M10 2a1 1 0 011 1v1a1 1 0 11-2 0V3a1 1 0 011-1zm4 8a4 4 0 11-8 0 4 4 0 018 0zm-.464 4.95l.707.707a1 1 0 001.414-1.414l-.707-.707a1 1 0 00-1.414 1.414zm2.12-10.607a1 1 0 010 1.414l-.706.707a1 1 0 11-1.414-1.414l.707-.707a1 1 0 011.414 0zM17 11a1 1 0 100-2h-1a1 1 0 100 2h1zm-7 4a1 1 0 011 1v1a1 1 0 11-2 0v-1a1 1 0 011-1zM5.05 6.464A1 1 0 106.465 5.05l-.708-.707a1 1 0 00-1.414 1.414l.707.707zm1.414 8.486l-.707.707a1 1 0 01-1.414-1.414l.707-.707a1 1 0 011.414 1.414zM4 11a1 1 0 100-2H3a1 1 0 000 2h1z" clipRule="evenodd" />
            </svg>
          ) : (
            <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
              <path d="M17.293 13.293A8 8 0 016.707 2.707a8.001 8.001 0 1010.586 10.586z" />
            </svg>
          )}
        </button>

        {/* Bildirim Çanı */}
        <div className="relative" ref={dropdownRef}>
          <button
            onClick={bildirimAc}
            className="relative p-2 rounded-lg text-gray-500 dark:text-gray-400 hover:bg-gray-100 dark:hover:bg-gray-800 transition-colors"
            title="Bildirimler"
          >
            <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
              <path d="M10 2a6 6 0 00-6 6v3.586l-.707.707A1 1 0 004 14h12a1 1 0 00.707-1.707L16 11.586V8a6 6 0 00-6-6zM10 18a3 3 0 01-3-3h6a3 3 0 01-3 3z" />
            </svg>
            {okunmamis > 0 && (
              <span className="absolute top-1 right-1 w-4 h-4 bg-red-500 rounded-full text-[10px] text-white flex items-center justify-center font-bold">
                {okunmamis > 9 ? '9+' : okunmamis}
              </span>
            )}
          </button>

          {bildirimAcik && (
            <div className="absolute right-0 mt-2 w-80 bg-white dark:bg-gray-900 rounded-xl shadow-xl border border-gray-100 dark:border-gray-700 z-50 overflow-hidden">
              <div className="px-4 py-3 border-b border-gray-100 dark:border-gray-800">
                <h3 className="font-semibold text-gray-900 dark:text-gray-100 text-sm">Bildirimler</h3>
              </div>
              <div className="max-h-72 overflow-y-auto">
                {bildirimler.length === 0 ? (
                  <div className="px-4 py-6 text-center text-gray-400 dark:text-gray-500 text-sm">
                    Henüz bildirim yok.
                  </div>
                ) : (
                  bildirimler.slice(0, 10).map((b) => (
                    <div
                      key={b.id}
                      className={`px-4 py-3 border-b border-gray-50 dark:border-gray-800 last:border-0 ${
                        !b.okunduMu ? 'bg-primary-50/50 dark:bg-primary-900/10' : ''
                      }`}
                    >
                      <p className="text-sm text-gray-700 dark:text-gray-300">{b.mesaj}</p>
                      <p className="text-xs text-gray-400 dark:text-gray-500 mt-1">
                        {new Date(b.olusturulmaTarihi).toLocaleString('tr-TR')}
                      </p>
                    </div>
                  ))
                )}
              </div>
            </div>
          )}
        </div>

        <div className="text-right">
          <p className="text-sm font-medium text-gray-900 dark:text-gray-100">
            {kullanici?.ad} {kullanici?.soyad}
          </p>
          <p className="text-xs text-gray-400 dark:text-gray-500">{kullanici?.eposta}</p>
        </div>
        <button
          onClick={cikisYapIsle}
          className="text-sm text-gray-500 dark:text-gray-400 hover:text-red-600 dark:hover:text-red-400 transition-colors font-medium"
        >
          Çıkış
        </button>
      </div>
    </header>
  )
}
