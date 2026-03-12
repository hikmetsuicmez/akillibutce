import { createContext, useContext, useState, useCallback } from 'react'
import { authServisi } from '../services/authServisi'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [kullanici, setKullanici] = useState(() => {
    const kaydedilen = localStorage.getItem('kullanici')
    return kaydedilen ? JSON.parse(kaydedilen) : null
  })

  const girisYap = useCallback(async (eposta, sifre) => {
    const veri = await authServisi.girisYap({ eposta, sifre })
    localStorage.setItem('token', veri.token)
    localStorage.setItem('kullanici', JSON.stringify(veri))
    setKullanici(veri)
    return veri
  }, [])

  const kayitOl = useCallback(async (formVeri) => {
    const veri = await authServisi.kayitOl(formVeri)
    localStorage.setItem('token', veri.token)
    localStorage.setItem('kullanici', JSON.stringify(veri))
    setKullanici(veri)
    return veri
  }, [])

  const cikisYap = useCallback(() => {
    localStorage.removeItem('token')
    localStorage.removeItem('kullanici')
    setKullanici(null)
  }, [])

  const kullaniciyiGuncelle = useCallback((yeniVeri) => {
    const guncellenen = { ...kullanici, ...yeniVeri }
    localStorage.setItem('kullanici', JSON.stringify(guncellenen))
    setKullanici(guncellenen)
  }, [kullanici])

  return (
    <AuthContext.Provider value={{
      kullanici,
      girisYap,
      kayitOl,
      cikisYap,
      kullaniciyiGuncelle,
      girisYapildiMi: !!kullanici,
      premiumMu: kullanici?.premium || false,
    }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (!context) {
    throw new Error('useAuth, AuthProvider icinde kullanilmalidir')
  }
  return context
}
