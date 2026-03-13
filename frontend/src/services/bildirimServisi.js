import api from './api'

export const bildirimServisi = {
  async bildirimleriGetir() {
    const yanit = await api.get('/bildirimler')
    return yanit.data
  },

  async okunmamisSayisiGetir() {
    const yanit = await api.get('/bildirimler/sayac')
    return yanit.data.okunmamis
  },

  async tumunuOkunduIsaretle() {
    await api.post('/bildirimler/tumu-okundu')
  },
}
