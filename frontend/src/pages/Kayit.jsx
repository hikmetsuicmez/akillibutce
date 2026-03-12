import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import toast from 'react-hot-toast'

export default function Kayit() {
  const [form, setForm] = useState({ ad: '', soyad: '', eposta: '', sifre: '' })
  const [yukleniyor, setYukleniyor] = useState(false)
  const { kayitOl } = useAuth()
  const navigate = useNavigate()

  const degistir = (e) => setForm(f => ({ ...f, [e.target.name]: e.target.value }))

  const gonder = async (e) => {
    e.preventDefault()
    if (form.sifre.length < 6) {
      toast.error('Şifre en az 6 karakter olmalıdır.')
      return
    }
    setYukleniyor(true)
    try {
      await kayitOl(form)
      toast.success('Hesabınız oluşturuldu!')
      navigate('/')
    } catch (err) {
      toast.error(err.response?.data?.mesaj || 'Kayıt başarısız. Lütfen tekrar deneyin.')
    } finally {
      setYukleniyor(false)
    }
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-primary-50 to-indigo-100 flex items-center justify-center p-4">
      <div className="w-full max-w-md">
        <div className="text-center mb-8">
          <div className="w-16 h-16 bg-primary-600 rounded-2xl flex items-center justify-center mx-auto mb-4">
            <span className="text-white text-3xl font-bold">₺</span>
          </div>
          <h1 className="text-2xl font-bold text-gray-900">Akıllı Bütçe</h1>
          <p className="text-gray-500 mt-1">Ücretsiz hesap oluşturun</p>
        </div>

        <div className="card">
          <form onSubmit={gonder} className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Ad</label>
                <input
                  type="text"
                  name="ad"
                  value={form.ad}
                  onChange={degistir}
                  className="input-field"
                  placeholder="Ahmet"
                  required
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Soyad</label>
                <input
                  type="text"
                  name="soyad"
                  value={form.soyad}
                  onChange={degistir}
                  className="input-field"
                  placeholder="Yılmaz"
                  required
                />
              </div>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">E-posta</label>
              <input
                type="email"
                name="eposta"
                value={form.eposta}
                onChange={degistir}
                className="input-field"
                placeholder="ornek@mail.com"
                required
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Şifre</label>
              <input
                type="password"
                name="sifre"
                value={form.sifre}
                onChange={degistir}
                className="input-field"
                placeholder="En az 6 karakter"
                required
              />
            </div>

            <button
              type="submit"
              disabled={yukleniyor}
              className="btn-primary w-full"
            >
              {yukleniyor ? 'Hesap oluşturuluyor...' : 'Kayıt Ol'}
            </button>
          </form>

          <p className="text-center text-sm text-gray-500 mt-4">
            Zaten hesabınız var mı?{' '}
            <Link to="/giris" className="text-primary-600 font-medium hover:underline">
              Giriş Yapın
            </Link>
          </p>
        </div>
      </div>
    </div>
  )
}
