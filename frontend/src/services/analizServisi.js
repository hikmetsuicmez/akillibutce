import api from './api'

export const analizServisi = {
  async tavsiyeleriGetir() {
    const yanit = await api.get('/analiz/tavsiyeler')
    return yanit.data
  },

  async trendleriGetir() {
    const yanit = await api.get('/analiz/trendler')
    return yanit.data
  },
}
