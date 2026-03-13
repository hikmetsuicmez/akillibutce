import api from './api'

export const islemServisi = {
  async islemEkle(veri) {
    const yanit = await api.post('/islemler', veri)
    return yanit.data
  },

  async islemleriGetir(baslangic, bitis) {
    const params = {}
    if (baslangic) params.baslangic = baslangic
    if (bitis) params.bitis = bitis
    const yanit = await api.get('/islemler', { params })
    return yanit.data
  },

  async islemSil(id) {
    await api.delete(`/islemler/${id}`)
  },

  async aylikOzetGetir(yil, ay) {
    const yanit = await api.get('/islemler/ozet', { params: { yil, ay } })
    return yanit.data
  },

  async fisTara(dosya) {
    const formData = new FormData()
    formData.append('dosya', dosya)
    const yanit = await api.post('/islemler/ocr-tara', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    })
    return yanit.data
  },
}
