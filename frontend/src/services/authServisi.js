import api from './api'

export const authServisi = {
  async kayitOl(veri) {
    const yanit = await api.post('/auth/kayit', veri)
    return yanit.data
  },

  async girisYap(veri) {
    const yanit = await api.post('/auth/giris', veri)
    return yanit.data
  },
}
