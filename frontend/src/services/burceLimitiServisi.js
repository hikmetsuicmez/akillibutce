import api from './api'

export const burceLimitiServisi = {
  async limitleriGetir(ay, yil) {
    const yanit = await api.get('/butce-limitleri', { params: { ay, yil } })
    return yanit.data
  },

  async limitKaydet(veri) {
    const yanit = await api.post('/butce-limitleri', veri)
    return yanit.data
  },

  async limitSil(id) {
    await api.delete(`/butce-limitleri/${id}`)
  },
}
