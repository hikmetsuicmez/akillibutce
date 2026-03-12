import api from './api'

export const kategoriServisi = {
  async kategorileriGetir() {
    const yanit = await api.get('/kategoriler/liste')
    return yanit.data
  },

  async ozelKategoriEkle(veri) {
    const yanit = await api.post('/kategoriler', veri)
    return yanit.data
  },

  async kategoriSil(id) {
    await api.delete(`/kategoriler/${id}`)
  },
}
