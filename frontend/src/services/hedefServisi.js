import api from './api'

export const hedefServisi = {
  async hedefleriGetir() {
    const yanit = await api.get('/hedefler')
    return yanit.data
  },

  async hedefOlustur(veri) {
    const yanit = await api.post('/hedefler', veri)
    return yanit.data
  },

  async paraEkle(id, miktar) {
    const yanit = await api.post(`/hedefler/${id}/para-ekle`, { miktar })
    return yanit.data
  },

  async hedefSil(id) {
    await api.delete(`/hedefler/${id}`)
  },
}
