import api from './api'

export const duzenliIslemServisi = {
  async duzenliIslemleriGetir() {
    const yanit = await api.get('/duzenli-islemler')
    return yanit.data
  },

  async duzenliIslemEkle(veri) {
    const yanit = await api.post('/duzenli-islemler', veri)
    return yanit.data
  },

  async duzenliIslemSil(id) {
    await api.delete(`/duzenli-islemler/${id}`)
  },
}
