import api from './api'

export const raporServisi = {
  async excelIndir(baslangic, bitis) {
    const params = {}
    if (baslangic) params.baslangic = baslangic
    if (bitis) params.bitis = bitis

    const yanit = await api.get('/raporlar/excel', {
      params,
      responseType: 'blob',
    })

    const url = window.URL.createObjectURL(new Blob([yanit.data]))
    const link = document.createElement('a')
    link.href = url
    const tarih = new Date().toISOString().slice(0, 10)
    link.setAttribute('download', `finansal-rapor-${tarih}.xlsx`)
    document.body.appendChild(link)
    link.click()
    link.remove()
    window.URL.revokeObjectURL(url)
  },
}
