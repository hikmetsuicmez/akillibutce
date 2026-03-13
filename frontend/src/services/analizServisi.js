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

  async aylikKarsilastirmaGetir(yil, ay) {
    const oncekiAy = ay === 1 ? 12 : ay - 1
    const oncekiYil = ay === 1 ? yil - 1 : yil
    const [buAy, gecenAy] = await Promise.all([
      api.get('/islemler/ozet', { params: { yil, ay } }),
      api.get('/islemler/ozet', { params: { yil: oncekiYil, ay: oncekiAy } }),
    ])
    return { buAy: buAy.data, gecenAy: gecenAy.data }
  },
}
