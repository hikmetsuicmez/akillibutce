import { createContext, useContext, useState, useEffect } from 'react'

const ThemeContext = createContext(null)

export function ThemeProvider({ children }) {
  const [karanlikMod, setKaranlikMod] = useState(() => {
    const kaydedilen = localStorage.getItem('karanlik-mod')
    return kaydedilen ? JSON.parse(kaydedilen) : false
  })

  useEffect(() => {
    if (karanlikMod) {
      document.documentElement.classList.add('dark')
    } else {
      document.documentElement.classList.remove('dark')
    }
    localStorage.setItem('karanlik-mod', JSON.stringify(karanlikMod))
  }, [karanlikMod])

  const temaGecis = () => setKaranlikMod((onceki) => !onceki)

  return (
    <ThemeContext.Provider value={{ karanlikMod, temaGecis }}>
      {children}
    </ThemeContext.Provider>
  )
}

export function useTheme() {
  const context = useContext(ThemeContext)
  if (!context) {
    throw new Error('useTheme, ThemeProvider icinde kullanilmalidir')
  }
  return context
}
