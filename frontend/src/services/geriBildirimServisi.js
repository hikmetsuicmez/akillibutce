import api from './api'

export const geriBildirimServisi = {
  async gonder(veri) {
    await api.post('/geribildirim', veri)
  },
}
