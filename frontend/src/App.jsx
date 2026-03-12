import { Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider, useAuth } from './context/AuthContext'
import Layout from './components/Layout/Layout'
import Giris from './pages/Giris'
import Kayit from './pages/Kayit'
import Dashboard from './pages/Dashboard'
import Islemler from './pages/Islemler'
import Analiz from './pages/Analiz'
import Premium from './pages/Premium'

function KorunanRotalar() {
  const { girisYapildiMi } = useAuth()
  if (!girisYapildiMi) return <Navigate to="/giris" replace />
  return (
    <Layout>
      <Routes>
        <Route path="/" element={<Dashboard />} />
        <Route path="/islemler" element={<Islemler />} />
        <Route path="/analiz" element={<Analiz />} />
        <Route path="/premium" element={<Premium />} />
      </Routes>
    </Layout>
  )
}

function HerkesGirebilir() {
  const { girisYapildiMi } = useAuth()
  if (girisYapildiMi) return <Navigate to="/" replace />
  return null
}

export default function App() {
  return (
    <AuthProvider>
      <Routes>
        <Route path="/giris" element={<><HerkesGirebilir /><Giris /></>} />
        <Route path="/kayit" element={<><HerkesGirebilir /><Kayit /></>} />
        <Route path="/*" element={<KorunanRotalar />} />
      </Routes>
    </AuthProvider>
  )
}
